/*
Исследование пакетных запросов Batch.
В данном примере мы создали записи в
таблицах и тут же их удалили, отследив
процесс на экране.

Не забываем подключить в настройках
драйвер соединения с базой данных (*.jar),
который лежит в папке 'lib' проекта 'JDBCLessonOne',
иначе словим исключение. Не забываем пометить
папку 'resources', как ресурсную.
*/

import connection_util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLQueryApp_12 {

    public static void main(String[] args) throws SQLException {

        int del_flightId = load_data_to_base();

        /*
        Номер рейса для удаления, предварительно
        созданный методом *.load_data_to_base()
        */
        long flightId = del_flightId;
        /* Готовим запросы для удаления данных в формате Statement */
        var deleteFlightSql = "DELETE FROM flight_repository.flight WHERE id = " + flightId;
        var deleteTicketsSql = "DELETE FROM flight_repository.ticket WHERE flight_id = " + flightId;
        /*
        Создаем нулевые соединения и запросы, которые
        должны быть доступны в любом куске кода.
        */
        Connection connection = null;
        Statement statement = null;
        try {
            connection = ConnectionManager.getBaseConnection();
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            /*
            Формируем пакет запросов, сначала удаляем билеты
            рейса, затем рейс, т.к. таблицы связанны.
            */
            statement.addBatch(deleteTicketsSql);
            statement.addBatch(deleteFlightSql);

            var ints = statement.executeBatch();
            for (int res: ints) {
                System.out.println("Результат запроса: " + res);
            }

            /* Подтверждаем пакет запросов */
            connection.commit();
        } catch (Exception e) {
            /* В случае ошибки/исключения откатить все изменения в базе */
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            /* Закрыть все открытые соединения */
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }
    /*
    Предварительно загрузим в базу немного данных, которые
    потом в методе MAIN попробуем удалить.
    */
    private static int load_data_to_base(){
        int auto_gen_key = 0;
        String sql_query_add_flight = """
                        INSERT INTO flight_repository.flight (flight_no, 
                                            departure_date, 
                                            departure_airport_code, 
                                            arrival_date, 
                                            arrival_airport_code, 
                                            aircraft_id,
                                            status)
                        VALUES
                        ('KQ1202', '2021-03-14T14:30', 'MNK', '2020-06-14T18:07', 'LDN', 3, 'DEPARTED');
                        """;
        try (Connection my_connect = ConnectionManager.getBaseConnection();
             Statement my_statement = my_connect.createStatement()){
                 my_statement.executeUpdate(sql_query_add_flight, Statement.RETURN_GENERATED_KEYS);
                 System.out.println("Добавили рейсов: " + my_statement.getUpdateCount());
                 var generatedKeys = my_statement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        var generatedId = generatedKeys.getInt("id");
                        System.out.println("Только что добавили рейс с id:" + generatedId);
                        auto_gen_key = generatedId;
                    }
                    String sql_query_add_ticket =
                            "INSERT INTO flight_repository.ticket " +
                            "(passenger_no, passenger_name, flight_id, seat_no, cost) " +
                            "VALUES ('134533', 'Малкольм Стоун', " + auto_gen_key + ", 'A1', 200)," +
                            "('12434A', 'Санара Куэста', " + auto_gen_key + ", 'B1', 180)," +
                            "('QQ138D', 'Дуглас Линд', " + auto_gen_key + ", 'B2', 175)," +
                            "('QY184E', 'Таймус Роддерик', " + auto_gen_key + ", 'C2', 175), " +
                            "('1OQ2A4', 'Говард Аддингтон', " + auto_gen_key + ", 'D1', 160)," +
                            " ('SS81M3', 'Амир Ахди', " + auto_gen_key + ", 'A2', 198);";

                 my_statement.executeUpdate(sql_query_add_ticket);
                 System.out.println("Добавили билетов: " + my_statement.getUpdateCount());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return auto_gen_key;
    }
}