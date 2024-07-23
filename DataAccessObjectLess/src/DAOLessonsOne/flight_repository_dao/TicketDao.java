package DAOLessonsOne.flight_repository_dao;

import DAOLessonsOne.connection_util.ConnectionPoolManager;
import DAOLessonsOne.dao_exception.DaoException;
import DAOLessonsOne.flight_repository_dto.TicketFilter;
import DAOLessonsOne.flight_repository_entity.Ticket;

/*
Проект не большой поэтому загрузим
сразу все содержимое пакетов SQL и
UTIL, хотя в целях экономии ресурсов
и производительности лучше так не
делать.
*/
import java.sql.*;
import java.util.*;

import static java.util.stream.Collectors.joining;

/*
Наш класс реализующий методы (функции) CRUD:
Create — создание
Read — чтение
Update — обновление
Delete — удаление
будет реализован как Singleton.
*/
public class TicketDao {
    // Переменная экземпляра объекта
    private static TicketDao INSTANCE;
    // Пустой приватный конструктор
    private TicketDao() {
    }
    /*
    Метод позволяющий инициализировать
    единственный объект класса TicketDao
    */
    public static TicketDao getInstance() {
        if (INSTANCE == null){
            INSTANCE = new TicketDao();
        }
        return INSTANCE;
    }
    /*
    SQL запрос на удаление одной записи из таблицы
    Ticket по ID в формате PrepareStatement
    */
    private static final String DELETE_SQL = """
            DELETE FROM flight_repository.ticket
            WHERE id = ?
            """;
    /*
    SQL запрос на вставку одной записи в таблицу
    Ticket в формате PrepareStatement
    */
    private static final String SAVE_SQL = """
            INSERT INTO flight_repository.ticket (passenger_no, passenger_name, flight_id, seat_no, cost) 
            VALUES (?, ?, ?, ?, ?);
            """;
    /*
    SQL запрос на изменение одной записи в таблице
    Ticket по ID в формате PrepareStatement
    */
    private static final String UPDATE_SQL = """
            UPDATE flight_repository.ticket
            SET passenger_no = ?,
                passenger_name = ?,
                flight_id = ?,
                seat_no = ?,
                cost = ?
            WHERE id = ?
            """;
    /* SQL запрос на получение всех записей из таблицы Ticket */
    private static final String FIND_ALL_SQL = """
            SELECT id,
                passenger_no,
                passenger_name,
                flight_id,
                seat_no,
                cost
            FROM flight_repository.ticket
            """;
    /*
    SQL запрос на получение данных одной записи в
    таблице Ticket по ID в формате PrepareStatement
    */
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;
    /*
    Метод для работы с FIND_ALL_SQL запросом. Перегруженный метод
    *.findAll(TicketFilter filter) приведен в самом низу данного
    класса, он принимает в качестве аргументов ссылку на
    TicketFilter - Data Transfer Object (DTO) — один из шаблонов
    проектирования, используется для передачи данных между
    подсистемами (слоями) приложения. Data Transfer Object, в отличие
    от business object или data access object не должен содержать
    какого-либо поведения.
    */
    public List<Ticket> findAll() {
        /* Устанавливаем связь с базой данных и готовим PrepareStatement объект */
        try (var connection =
                     ConnectionPoolManager.getConnectionFromPool();
             var preparedStatement =
                     connection.prepareStatement(FIND_ALL_SQL)) {
            /* Получаем результат запроса, как коллекцию SET */
            var resultSet = preparedStatement.executeQuery();
            /* Создаем список для хранения билетов полученных по запросу */
            List<Ticket> tickets = new ArrayList<>();
            while (resultSet.next()) {
                /* Загружаем полученный список в коллекцию*/
                tickets.add(buildTicket(resultSet));
            }
            /* Возвращаем результат */
            return tickets;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }
    /*
    Метод для получения данных о билете по ID.
    Результат запроса неоднозначен, поскольку
    может быть запрошен ID которого нет в базе,
    отсюда и возвращаемый объект Optional.
    */
    public Optional<Ticket> findById(Long id) {
        /* Устанавливаем связь, передаем подготовленный FIND_BY_ID_SQL запрос */
        try (var connection =
                     ConnectionPoolManager.getConnectionFromPool();
             var preparedStatement =
                     connection.prepareStatement(FIND_BY_ID_SQL)) {
            /* Устанавливаем интересующий нас параметр в запрос */
            preparedStatement.setLong(1, id);
            /*
            Только после окончательного формирования запроса
            и установки всех параметров передаем запрос в базу
            данных
            */
            var resultSet = preparedStatement.executeQuery();
            /* Создаем пустую ссылку на объект Ticket */
            Ticket ticket = null;
            if (resultSet.next()) {
                /*
                Инициализируем наш объект методом *.buildTicket()
                куда передали результат запроса
                */
                ticket = buildTicket(resultSet);
            }
            /*
            Мы получаем объект, в котором может быть запрашиваемый объект —
            а может быть null. Но с Optional надо как-то работать дальше,
            нам нужна сущность, которую он содержит (или не содержит).

            Cуществует всего три категории Optional:
            - Optional.of - возвращает Optional-объект.
            - Optional.ofNullable - возвращает Optional-объект, а если нет
                                    дженерик-объекта, возвращает пустой
                                    Optional-объект.
            - Optional.empty - возвращает пустой Optional-объект.

            Существует так же два метода, вытекающие из познания, существует
            обёрнутый объект или нет — isPresent() и ifPresent()
            */
            return Optional.ofNullable(ticket);
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }
    /* Метод для обновления сведений о билете */
    public void update(Ticket ticket) {
        /* Создаем соединение с базой и передаем UPDATE_SQL запрос */
        try (var connection =
                     ConnectionPoolManager.getConnectionFromPool();
             var preparedStatement =
                     connection.prepareStatement(UPDATE_SQL)) {
            /* Загружаем параметры '?' в сформированный запрос */
            preparedStatement.setString(1, ticket.getPassengerNo());
            preparedStatement.setString(2, ticket.getPassengerName());
            preparedStatement.setLong(3, ticket.getFlightId());
            preparedStatement.setString(4, ticket.getSeatNo());
            preparedStatement.setBigDecimal(5, ticket.getCost());
            preparedStatement.setLong(6, ticket.getId());
            /* Передаем окончательный запрос в базу данных */
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }
    /*
    Метод для сохранения данных нового билета в базе данных.
    Сам объект-билет передается в качестве параметра из которого
    далее будут извлечены его поля через *.get... методы
    */
    public Ticket save(Ticket ticket) {
        /*
        Связь с базой и PrepareStatement с двумя параметрами:
        - SAVE_SQL запрос;
        - Statement.RETURN_GENERATED_KEYS - флаг на возврат сгенерированного ID;
        */
        try (var connection =
                     ConnectionPoolManager.getConnectionFromPool();
             var preparedStatement =
                     connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            /* Подгружаем в запрос '?' поля, ID формируется автоматически */
            preparedStatement.setString(1, ticket.getPassengerNo());
            preparedStatement.setString(2, ticket.getPassengerName());
            preparedStatement.setLong(3, ticket.getFlightId());
            preparedStatement.setString(4, ticket.getSeatNo());
            preparedStatement.setBigDecimal(5, ticket.getCost());
            /* Отправляем запрос в базу */
            preparedStatement.executeUpdate();
            /* Получаем из базы вновь сгенерированный ID */
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                /* Помещаем его в полученный Ticket объект */
                ticket.setId(generatedKeys.getLong("id"));
            }
            /* Возвращаем полный Ticket объект */
            return ticket;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }
    /* Метод для удаления записи из базы данных по ID */
    public boolean delete(Long id) {
        /* Try-with-resources для объектов Connection и PrepareStatement */
        try (Connection connection =
                     ConnectionPoolManager.getConnectionFromPool();
             var preparedStatement =
                     connection.prepareStatement(DELETE_SQL)) {
            /* Дополняем (загружаем) в наш DELETE_SQL запрос полученный параметр */
            preparedStatement.setLong(1, id);
            /* Возвращаем true - если данные были удалены и false - если нет */
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }
    /*
    Метод извлекающий сведения о билете из объекта
    ResultSet и применяемый в методах *.findById()
    и *.findAll()
    */
    private Ticket buildTicket(ResultSet resultSet) throws SQLException {
        return new Ticket(
                resultSet.getLong("id"),
                resultSet.getString("passenger_no"),
                resultSet.getString("passenger_name"),
                resultSet.getLong("flight_id"),
                resultSet.getString("seat_no"),
                resultSet.getBigDecimal("cost")
        );
    }
    /* Перегруженный метод findAll с фильтрацией по входным параметрам */
    public List<Ticket> findAll(TicketFilter filter) {
        List<Object> parameters = new ArrayList<>(); // Список для хранения параметров заменяющих символ '?' в SQL запросе
        /*
        Объект TicketFilter типа Record может содержать
        четыре параметра: limit, offset,
                          passengerName, seatNo.
        Причем, последние два могут быть NULL, а значит
        могут внести некую турбулентность в SQL запрос,
        если делать его без проверки на NULL.
        */
        List<String> whereSql = new ArrayList<>();
        /*
        Если переданный в TicketFilter параметр NULL, то
        он просто не попадает в будущий SQL запрос или не
        интегрируется в строку как подстрока.

        Если же он не NULL, то в нашем будущем запросе
        должна появится подстрока 'seat_no LIKE ?' и
        естественно знак '?' должен получить свой параметр
        из списка 'parameters' куда он попадает из
        аргументов объекта TicketFilter.
        */
        if (filter.seatNo() != null) {
            whereSql.add("seat_no LIKE ?");
            parameters.add("%" + filter.seatNo() + "%");
        }
        /*
        Та же проверка, что и с параметром seatNo и тот
        же результат в случае передачи NULL значения -
        параметр не попадает в SQL строку запроса ни коим
        образом.

        Если же 'passengerName' не NULL, то в нашем будущем
        SQL запросе должна появится подстрока
        'passenger_name = ?' и естественно знак '?' должен
        получить свой параметр из списка 'parameters' куда
        он попадает из аргументов объекта TicketFilter.
        */
        if (filter.passengerName() != null) {
            whereSql.add("passenger_name = ?");
            parameters.add(filter.passengerName());
        }
        /*
        Параметры для 'LIMIT ?' и 'OFFSET ?' подгружаются из
        геттеров объекта TicketFilter *.limit() и *.offset()
        */
        parameters.add(filter.limit());
        parameters.add(filter.offset());
        /*
        Формируем окончательный вид нашего будущего SQL
        запроса. Наш список 'whereSql' превращаем в Stream
        и преобразуйте его элементы в строку объединив их,
        разделив делиметром (разделителем) AND.

        Наша коллекция 'whereSql' может содержать один,
        два элемента или не содержать ничего. В случае
        если кроме 'Limit' и 'Offset' были переданы еще два
        параметра 'seatNo' и 'passenger_name' то к
        стандартному SQL запросу позволяющему получит полный
        список ticket будет прибавлен или:

        'FIND_ALL_SQL' +
        'WHERE
        seat_no LIKE '% ? %'
        AND
        passenger_name = ' ? '
        LIMIT ? OFFSET ?'

        Если был передан только 'seatNo' без 'passenger_name',
        то запросу прибавится строка или:

        'FIND_ALL_SQL' +
        'WHERE
        seat_no LIKE '% ? %'
        LIMIT ? OFFSET ?'

        Если был передан только 'passenger_name' без 'seatNo',
        то запрос превратится в строку вида:

        'FIND_ALL_SQL' +
        'WHERE
        passenger_name = ' ? '
        LIMIT ? OFFSET ?'

        Ну и наконец, если ни 'passenger_name' ни 'seatNo' не
        были переданы, то запрос примет вид:

        'FIND_ALL_SQL' + LIMIT ? OFFSET ?'

        См. пример реализованный в FindAllWithFilterLess

        Все что описано выше реализуется через Stream и метод
        *.joining() см. раздел DOC по классу Collectors.txt
        */
        String where = whereSql.stream()
                .collect(joining(" AND ", " WHERE ", " LIMIT ? OFFSET ? "));
        /*
        Наш стандартный запрос 'FIND_ALL_SQL' на
        получение всех записей из таблицы ticket:

        SELECT id,
               passenger_no,
               passenger_name,
               flight_id,
               seat_no,
               cost
        FROM flight_repository.ticket

        Он идет в работу если кроме 'Limit' и 'Offset'
        из TicketFilter мы больше ничего не получили.
        */
        String sql = FIND_ALL_SQL + " LIMIT ? OFFSET ? ";

        /*
        Если кроме 'Limit' и 'Offset' из TicketFilter мы
        получили 'seatNo' и/или 'passenger_name', то в
        работу идет конструкция SQL запроса с элементами
        коллекции whereSql или строка '+ where' см. ниже.
        */
        if(filter.seatNo() != null || filter.passengerName() != null) {
            sql = FIND_ALL_SQL + where;
        }
        /* Устанавливаем связь с базой данных и создаем PrepareStatement*/
        try (var connection = ConnectionPoolManager.getConnectionFromPool();
             var preparedStatement = connection.prepareStatement(sql)) {
            /*
            В цикле перебираем все параметры из коллекции 'parameters'.
            Значки '?' будут меняться на содержимое этой коллекции по
            порядку от 1 до n. В нашей текущей реализации n =< 4:
             - 'seatNo'
             - 'passenger_name'
             - 'Limit'
             - 'Offset'
            именно в таком порядке они добавляются в коллекцию и
            естественно будут из нее извлекаться см. полный пример
            в FindAllWithFilterLess.
            */
            for (int i = 0; i < parameters.size(); i++) {
                preparedStatement.setObject(i + 1, parameters.get(i));
            }
            /* Для наглядности выводим на экран получившийся SQl запрос */
            System.out.println(preparedStatement);
            /* Отправляем запрос в базу и получаем результирующую выборку */
            var resultSet = preparedStatement.executeQuery();
            /* Коллекция куда мы поместим результат выборки */
            List<Ticket> tickets = new ArrayList<>();
            /* Заполняем коллекцию */
            while (resultSet.next()) {
                tickets.add(buildTicket(resultSet));
            }
            /* Возвращаем коллекцию */
            return tickets;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }
}
