/*
ПРИМЕР КАК МОЖНО НЕОБДУМАННЫМ КОДОМ ПОРУШИТЬ БАЗУ
И ПОЗВОЛИТЬ ЗЛОУМЫШЛЕННИКАМ ПОЛУЧИТЬ ПОЛНЫЙ НАД
НЕЙ КОНТРОЛЬ;

!!! Нужно помнить разницу между (возвращаемые данные):

- boolean execute(String sql) - Выполняет запрос SQL,
который может возвращать несколько результатов.

- ResultSet executeQuery(String sql) - Выполняет
запрос SQL, который возвращает один объект ResultSet.

- int executeUpdate(String sql) - Выполняет запрос SQL,
который может быть оператором INSERT, UPDATE или DELETE
или оператором SQL, который ничего не возвращает, например
оператором SQL DDL.

При этом не забываем подключить в настройках
драйвер соединения с базой данных (*.jar),
который лежит в папке 'lib' проекта 'JDBCLessonOne',
иначе словим исключение. Не забываем пометить
папку 'resources', как ресурсную.
*/
import connection_util.ConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLQueryApp_6_bad {

    public static void main(String[] args) throws SQLException {
        /*
        Шаг 3.  Формируем запросы к базе данных:

        - Безобидный и выдающий требуемое:
          String flightId = "2";

        - Хакерский выдающий все id из таблицы:
          String flightId = "2 OR '' = ''";

        - Хакерский выдающий все id из таблицы у убивающий таблицу в базе:
          String flightId = "2 OR 2 = 2; " +
                            "DROP TABLE flight_repository.test_for_delete; " +
                            "COMMIT;";
          Да, в случае последнего запроса мы получим:
            PSQLException: Multiple ResultSets were returned by the query.
          Но, таблица из базы будет удалена, и это легко проверить, т.е.
          при такой конструкции методов и запросов можно сильно
          подставиться.

        */
        // Формируем простой запрос
        String flightId = "2";
        var result = getTicketsByFlightId(flightId);
        System.out.println(result);
    }
    /*
    Шаг 1. Создаем метод, который позволяет получить id из таблицы
           ticket схемы flight_repository по запросу.
    */
    private static List<Long> getTicketsByFlightId(String flightId) throws SQLException {
        /*
        Шаг 2. Создаем строковый SQL запрос к базе и форматируем его.
               Переданный в качестве параметра flightId помещается в
               запрос - стандартное форматирование строки через %s.

               !!! И ловушка захлопнулась!!!
               В качестве flightId принимается строка, а значит в нее
               можно поместить много чего.

               Например, сначала, то, что от нас ждут например 'id = 2',
               а затем через ';' любую команду или запрос, допустим так:
               "2; DROP TABLE flight_repository.test_for_delete;"

        */
        String sql = """
                SELECT id
                FROM flight_repository.ticket
                WHERE flight_id = %s
                """.formatted(flightId);

        // Список для хранения результатов запроса
        List<Long> result = new ArrayList<>();
        // Создаем объекты для соединения с базой и формирования запросов
        try (Connection connection = ConnectionManager.getBaseConnection();
             Statement statement = connection.createStatement())
        {
             // Наш запрос может вернуть несколько записей.
             ResultSet resultSet = statement.executeQuery(sql);
             // Переберем их все.
             while (resultSet.next()) {
             /*
             Добавляем полученные данные из ResultSet в наш List.
             Однако, метод *.getLong() возвращает long и если
             вдруг поле будет NULLable, мы можем поймать исключение.
             Поэтому лучше применять метод описанный ниже
             -> */

             // result.add(resultSet.getLong("id"));

             /* ->

             <T> T getObject(String columnLabel,
             Class<T> type) - Извлекает значение назначенного столбца в текущей
                              строке этого объекта ResultSet и преобразует тип
                              SQL столбца в запрошенный тип данных Java, если
                              преобразование поддерживается.
             */
                result.add(resultSet.getObject("id", Long.class)); // NULL safe
             }
        }
        return result;
    }
}