/*
БОЛЕЕ РАЗВЕРНУТЫЙ ПРИМЕР ИСПОЛЬЗОВАНИЯ
РrepareStatement с двумя параметрами '?';

Применение методов-запросов без параметров:
- boolean execute();
- ResultSet executeQuery();
- int executeUpdate();

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

public class SQLQueryApp_8 {

    public static void main(String[] args) throws SQLException {
        /*
        Подготавливаем даты для запроса в метод *.getFlightsBetween()

        Переменная my_date_start - начало исследуемого диапазона;

        - Метод LocalDate.of(int year, int month, int dayOfMonth) - принадлежит
        классу LocalDate и возвращает экземпляр LocalDate из года, месяца и дня;
        - Метод *.atStartOfDay() - принадлежит классу LocalDate и возвращает
        LocalDateTime. Он объединяет вызвавшую дату со временем полуночи,
        чтобы создать LocalDateTime в начале вызвавшей даты;
        т.е. тут мы фиксируем стартовую дату.
        */
        LocalDateTime my_date_start = LocalDate.of(2020, 1, 1).atStartOfDay();
        /*
        Переменная my_date_finish - конец исследуемого диапазона;

        Метод LocalDateTime.now() - принадлежит классу LocalDateTime и возвращает
        объект LocalDateTime или текущую дату по системным часам в часовом поясе
        по умолчанию;
        т.е. мы фиксируем финишную дату.
        */
        LocalDateTime my_date_finish = LocalDateTime.now();
        /*
        Передаем подготовленные данные в запрос и смотрим результаты
        */
        var result = getFlightsBetween(my_date_start, my_date_finish);
        System.out.println(result);
    }

    private static List<Long> getFlightsBetween(LocalDateTime start, LocalDateTime end) throws SQLException {
        /*  Подготавливаем SQL запрос в котором уже два знака '?'  */
        String my_sql_query = """
                SELECT id
                FROM flight_repository.flight
                WHERE departure_date BETWEEN ? AND ?
                """;
        List<Long> result = new ArrayList<>();
        /*
        Стартуем соединение с базой данных и создаем объект PrepareStatement
        */
        try (var connection = ConnectionManager.getBaseConnection();
             var preparedStatement = connection.prepareStatement(my_sql_query))
        {
            /* Выведем на экран наш SQL запрос без параметров и явно увидим знаки '?' */
            System.out.println(preparedStatement);
            /*
            Задаем оттиск времени методом *.setTimestamp(int parameterIndex, Timestamp x),
            который устанавливает указанный параметр в заданное значение java.sql.Timestamp.
            Т.е. наш первый знак вопроса мы задаем равным значению 'start' переданным в
            текущий метод *.getFlightsBetween().

            Метод *.valueOf() - принадлежит классу Timestamp и его же возвращает, он
            Получает экземпляр Timestamp из объекта LocalDateTime с тем же годом, месяцем,
            днем месяца, часами, минутами, секундами и значением даты и времени, что и
            предоставленное значение LocalDateTime (см. документацию).
            */
            preparedStatement.setTimestamp(1, Timestamp.valueOf(start));
            /*
            Выведем на экран наш SQL запрос с первым параметром и явно увидим один
            знак '?' стоящий на втором месте
            */
            System.out.println(preparedStatement);
            /* Задаем второй параметр запроса */
            preparedStatement.setTimestamp(2, Timestamp.valueOf(end));
            /* Выводим на экран привычную форму SQL запроса */
            System.out.println(preparedStatement);

            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getLong("id"));
            }
        }
        return result;
    }
}