/*
Создаем метод, который позволяет получить
данные из таблицы 'ticket' по полю 'flight_id'.

ПРИМЕР КАК МОЖНО ИСПРАВИТЬ СИТУАЦИЮ, СОЗДАННУЮ
В КЛАССЕ SQLQueryApp_6_bad, КОГДА НЕОБДУМАННЫМ
КОДОМ МЫ ДОПУСТИЛИ ПРОНИКНОВЕНИЕ ЗЛОУМЫЛЕННИКА
В БАЗУ ДАННЫХ.

В ДАННОМ ПРИМЕРЕ МЫ ИСПОЛЬЗУЕМ КЛАСС и МЕТОДЫ
РrepareStatement КОТОРЫЕ В НЕКОТОРОЙ СТЕПЕНИ
ИСПРАВЛЯЮТ СИТУАЦИЮ;

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

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLQueryApp_7_good {

    public static void main(String[] args) throws SQLException {
        /*
        Шаг 5.  Используем метод *.getTicketsByFlightId()
                для получения данных, метод принимает только
                Long (long). А значит, как в первом случае,
                сделать SQL инъекцию (повредить базе) будет
                сложнее.
        */
        Long flightId = 2L;
        /*      Вместо var можно явно указать List         */
        var result = getTicketsByFlightId(flightId);
        System.out.println(result);
    }
    /*
    Шаг 1. Создаем метод, который позволяет получить id из таблицы
           'ticket' схемы 'flight_repository' по запросу 'flight_id'.
    */
    private static List<Long> getTicketsByFlightId(Long flightId) throws SQLException {
        /*
        Шаг 2. Создаем строковый SQL PrepareStatement запрос к базе
               вместо знака ?, мы должны поместить в запрос данные в
               формате long. Это делается при помощи метода *.setLong().
        */
        String sql_prepare_query = """
                SELECT id
                FROM flight_repository.ticket
                WHERE flight_id = ?
                """;

        // Список для хранения результатов запроса, т.к. билетов может быть много
        List<Long> result = new ArrayList<>();
        /*
        Создаем объект для соединения с базой и объект класса PreparedStatement,
        который кроме выполнения запроса позволяет подготовить запрос и
        отформатировать его должным образом. При этом в отличие от методов класса
        Statement его методы не принимают чистые SQL-выражения.
        */
        try (Connection my_connection = ConnectionManager.getBaseConnection();
             PreparedStatement my_prep_statement = my_connection.prepareStatement(sql_prepare_query))
        {
             /*
             Шаг 3. После того как был подготовлен запрос 'sql_prepare_query' его
                    необходимо должным образом заполнить методом *.set...(), у нас
                    это *.setLong(). В данном случае первый (1 - местоположение
                    в запросе) и единственный знак '?', у нас будет соответствовать
                    значению Long flightId и будет помещен в подготовленный запрос.
             */
             my_prep_statement.setLong(1, flightId);
             /*
             Шаг 4. Наш запрос перед вызовом должен быть подготовлен Шаг 2 и Шаг 3.
                    При этом, в отличие от методов класса Statеment методы *.execute...()
                    класса PrepareStatement не принимают параметров, они уже переданы
                    выше, при создании объекта PrepareStatement (в блоке try-with-resource).
             */
             ResultSet resultSet = my_prep_statement.executeQuery();
             // Переберем их все.
             while (resultSet.next()) {
             /*
             Добавляем полученные данные из ResultSet в наш List.
             Однако, метод *.getLong() возвращает long и если
             вдруг поле будет NULLable (хотя у нас оно NOT NULL),
             мы можем поймать исключение. Поэтому лучше применять
             метод описанный ниже
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