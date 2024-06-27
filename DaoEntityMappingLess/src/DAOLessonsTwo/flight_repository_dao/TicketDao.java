package DAOLessonsTwo.flight_repository_dao;

import DAOLessonsTwo.connection_util.ConnectionPoolManager;
import DAOLessonsTwo.dao_exception.DaoException;
import DAOLessonsTwo.flight_repository_dto.TicketFilter;
import DAOLessonsTwo.flight_repository_entity.Flight;
import DAOLessonsTwo.flight_repository_entity.Ticket;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;
/*
Наш класс реализующий методы (функции) CRUD:
Create — создание
Read — чтение
Update — обновление
Delete — удаление
будет реализован как Singleton.
*/
public class TicketDao implements Dao<Long, Ticket>{
    
    private static TicketDao INSTANCE; // Переменная экземпляра объекта
    
    /* Пустой приватный конструктор */
    private TicketDao() {
    }
    
    /* Метод позволяющий инициализировать единственный объект класса TicketDao */
    public static TicketDao getInstance() {
        if (INSTANCE == null){
            INSTANCE = new TicketDao();
        }
        return INSTANCE;
    }
   
    private final FlightDao flightDao = FlightDao.getInstance(); // Получаем экземпляр класса FlightDao
    
    /* SQL запрос на удаление одной записи из таблицы Ticket по ID в формате PrepareStatement */
    private static final String DELETE_SQL = """
            DELETE FROM flight_repository.ticket
            WHERE id = ?
            """;
    
    /* SQL запрос на вставку одной записи в таблицу Ticket в формате PrepareStatement */
    private static final String SAVE_SQL = """
            INSERT INTO flight_repository.ticket (passenger_no, passenger_name, flight_id, seat_no, cost) 
            VALUES (?, ?, ?, ?, ?);
            """;
    
    /* SQL запрос на изменение одной записи в таблице Ticket по ID в формате PrepareStatement */
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
            SELECT t.id,
                   t.passenger_no,
                   t.passenger_name,
                   t.flight_id,
                   t.seat_no,
                   t.cost,
                   f.flight_no,
                   f.status,
                   f.aircraft_id,
                   f.arrival_airport_code,
                   f.arrival_date,
                   f.departure_airport_code,
                   f.departure_date
            FROM flight_repository.ticket AS t
            JOIN flight_repository.flight AS f
            ON t.flight_id = f.id
            """;
    
    /* SQL запрос на получение данных одной записи в таблице Ticket по ID в формате PrepareStatement */
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE t.id = ?
            """;
    
    /*
    Метод для работы с FIND_ALL_SQL запросом. Перегруженный метод *.findAll(TicketFilter filter) приведен в самом низу данного
    класса, он принимает в качестве аргументов ссылку на TicketFilter - Data Transfer Object (DTO) — один из шаблонов
    проектирования, используется для передачи данных между подсистемами (слоями) приложения. Data Transfer Object, в отличие
    от business object или data access object не должен содержать какого-либо поведения.
    */
    public List<Ticket> findAll() {
        /* Устанавливаем связь с базой данных и готовим PrepareStatement объект */
        try (var connection =
                     ConnectionPoolManager.getConnectionFromPool();
             var preparedStatement =
                     connection.prepareStatement(FIND_ALL_SQL)) {
            
            var resultSet = preparedStatement.executeQuery(); // Получаем результат запроса, как коллекцию SET
            
            List<Ticket> tickets = new ArrayList<>(); // Создаем список для хранения билетов полученных по запросу
            while (resultSet.next()) {
                tickets.add(buildTicket(resultSet)); // Загружаем полученный список в коллекцию
            }
            return tickets; // Возвращаем результат
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }
    
    /* 
    Метод для получения данных о билете по ID. Результат запроса неоднозначен, поскольку     
    может быть запрошен ID которого нет в базе, отсюда и возвращаемый объект Optional.
    */
    public Optional<Ticket> findById(Long id) {
        /* Устанавливаем связь, передаем подготовленный FIND_BY_ID_SQL запрос */
        try (var connection =
                     ConnectionPoolManager.getConnectionFromPool();
             var preparedStatement =
                     connection.prepareStatement(FIND_BY_ID_SQL)) {
            
            preparedStatement.setLong(1, id); // Устанавливаем нужный нам параметр в запрос (в данном случае первый параметр)
            var resultSet = preparedStatement.executeQuery(); //Только после окончательного формирования запроса и установки всех параметров передаем запрос в базу данных
            Ticket ticket = null; // Создаем пустую ссылку на объект Ticket
            if (resultSet.next()) {
                ticket = buildTicket(resultSet); // Инициализируем наш объект методом *.buildTicket() куда передали результат запроса
            }
            
            /*
            Мы получаем объект, в котором может быть запрашиваемый объект — а может быть null. Но с Optional надо как-то работать дальше,
            нам нужна сущность, которую он содержит (или не содержит).

            Cуществует всего три категории Optional:
            - Optional.of - возвращает Optional-объект.
            - Optional.ofNullable - возвращает Optional-объект, а если нет
                                    дженерик-объекта, возвращает пустой
                                    Optional-объект.
            - Optional.empty - возвращает пустой Optional-объект.

            Существует так же два метода, вытекающие из познания, существует обёрнутый объект или нет — isPresent() и ifPresent()
            */
            return Optional.ofNullable(ticket);
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }
    
    /* Метод для обновления сведений о билете */
    public void update(Ticket dataIn) {
        /* Создаем соединение с базой и передаем UPDATE_SQL запрос */
        try (var connection =
                     ConnectionPoolManager.getConnectionFromPool();
             var preparedStatement =
                     connection.prepareStatement(UPDATE_SQL)) {
            /* Загружаем параметры '?' в сформированный запрос */
            preparedStatement.setString(1, dataIn.getPassengerNo());
            preparedStatement.setString(2, dataIn.getPassengerName());
            preparedStatement.setLong(3, dataIn.getFlight().id());
            preparedStatement.setString(4, dataIn.getSeatNo());
            preparedStatement.setBigDecimal(5, dataIn.getCost());
            preparedStatement.setLong(6, dataIn.getId());
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
    public Ticket save(Ticket dataIn) {
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
            preparedStatement.setString(1, dataIn.getPassengerNo());
            preparedStatement.setString(2, dataIn.getPassengerName());
            preparedStatement.setLong(3, dataIn.getFlight().id());
            preparedStatement.setString(4, dataIn.getSeatNo());
            preparedStatement.setBigDecimal(5, dataIn.getCost());
            preparedStatement.executeUpdate(); // Отправляем запрос в базу
            var generatedKeys = preparedStatement.getGeneratedKeys(); // Получаем из базы вновь сгенерированный ID
            if (generatedKeys.next()) {
                 dataIn.setId(generatedKeys.getLong("id")); // Помещаем его в полученный Ticket объект
            }
            return dataIn; // Возвращаем полный Ticket объект
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
    
    /* Метод извлекающий сведения о билете из объекта ResultSet и применяемый в методах *.findById() и *.findAll() */
    private Ticket buildTicket(ResultSet resultSet) throws SQLException {
        var flight = new Flight(
                resultSet.getLong("flight_id"),
                resultSet.getString("flight_no"),
                resultSet.getTimestamp("departure_date").toLocalDateTime(),
                resultSet.getString("departure_airport_code"),
                resultSet.getTimestamp("arrival_date").toLocalDateTime(),
                resultSet.getString("arrival_airport_code"),
                resultSet.getInt("aircraft_id"),
                resultSet.getString("status")
        );
        return new Ticket(
                resultSet.getLong("id"),
                resultSet.getString("passenger_no"),
                resultSet.getString("passenger_name"),
                flightDao.findById(resultSet.getLong("flight_id"),
                        resultSet.getStatement().getConnection()).orElse(null),
                resultSet.getString("seat_no"),
                resultSet.getBigDecimal("cost")
        );
    }
    
    /* Перегруженный метод findAll с фильтрацией по входным параметрам */
    public List<Ticket> findAll(TicketFilter filter) {
        /* Список для хранения параметров заменяющих символ '?' в SQL запросе */
        List<Object> parameters = new ArrayList<>();
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
