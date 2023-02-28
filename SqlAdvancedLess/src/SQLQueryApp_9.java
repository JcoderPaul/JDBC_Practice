/*
Исследование методов интерфейса Statement:
- *.setFetchSize(int rows) - Дает драйверу JDBC подсказку о количестве строк, которые должны
                             быть извлечены из базы данных, когда требуется больше строк для
                             объектов ResultSet, сгенерированных этим оператором;
- *.setQueryTimeout(int seconds) - Устанавливает количество секунд, в течение которых драйвер
                                   будет ожидать выполнения объекта Statement, до заданного
                                   количества секунд;
- *.setMaxRows(int max) - Устанавливает ограничение на максимальное количество строк, которое
                          может содержать любой объект ResultSet, сгенерированный этим объектом
                          Statement, до заданного числа;

Не забываем подключить в настройках
драйвер соединения с базой данных (*.jar),
который лежит в папке 'lib' проекта 'JDBCLessonOne',
иначе словим исключение. Не забываем пометить
папку 'resources', как ресурсную.
*/

import connection_util.ConnectionManager;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SQLQueryApp_9 {

    public static void main(String[] args) throws SQLException {
        /* Подробный разбор кода приведенного ниже см. в SQLQueryApp_8 */
        LocalDateTime my_date_start = LocalDate.of(2020, 1, 1).atStartOfDay();
        LocalDateTime my_date_finish = LocalDateTime.now();
        List result = getFlightsBetween(my_date_start, my_date_finish);
        System.out.println(result);
    }

    private static List<Long> getFlightsBetween(LocalDateTime start, LocalDateTime end) throws SQLException {
        /* Подготавливаем SQL запрос в котором уже два знака '?' */
        String my_sql_query = """
                SELECT id
                FROM flight_repository.flight
                WHERE departure_date BETWEEN ? AND ?
                """;
        List<Long> result = new ArrayList<>();
        /* Стартуем соединение с базой данных и создаем объект PrepareStatement */
        try (var connection = ConnectionManager.getBaseConnection();
             var preparedStatement = connection.prepareStatement(my_sql_query))
        {
            /*
            Интерфейс PrepareStatement наследует много
            методов от интерфейса Statement, в том числе
            и приведенные (в описании) ниже в коде.
            */
            preparedStatement.setFetchSize(3);
            preparedStatement.setQueryTimeout(10);
            preparedStatement.setMaxRows(3);

            /* Работа приведенного ниже кода более подробно разобрана в SQLQueryApp_8 */
            System.out.println(preparedStatement);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(start));
            System.out.println(preparedStatement);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(end));
            System.out.println(preparedStatement);

            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getLong("id"));
            }
        }
        return result;
    }
}