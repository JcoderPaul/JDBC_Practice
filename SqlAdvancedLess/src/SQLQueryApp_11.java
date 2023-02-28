/*
Исследование транзакций SQL операторы:
- COMMIT - фиксирует все изменения для текущей транзакции,
           как только COMMIT выполнится, остальным
           пользователям будут доступны внесенные изменения.
- ROLLBACK - используется для отмены работы, выполняемой текущей
             транзакцией или транзакции, которая сомнительна.

В данном примере мы создадим ситуацию при которой попробуем
удалить связные между таблицами данные и с имитируем проблему,
чтобы отменить изменения.

Не забываем подключить в настройках
драйвер соединения с базой данных (*.jar),
который лежит в папке 'lib' проекта 'JDBCLessonOne',
иначе словим исключение. Не забываем пометить
папку 'resources', как ресурсную.
*/

import connection_util.ConnectionManager;

import java.sql.*;

public class SQLQueryApp_11 {

    public static void main(String[] args) throws SQLException {

        int del_flightId = load_data_to_base();

        /*
        Номер рейса для удаления, предварительно
        созданный методом *.load_data_to_base()
        */
        long flightId = del_flightId;
        /* Готовим запросы для удаления данных в формате PreparedStatement */
        var deleteFlightSql = "DELETE FROM flight_repository.flight WHERE id = ?";
        var deleteTicketsSql = "DELETE FROM flight_repository.ticket WHERE flight_id = ?";
        /*
        Создаем нулевые соединения и запросы,
        которые должны быть доступны в любом
        куске кода.
        */
        Connection connection = null;
        PreparedStatement deleteFlightStatement = null;
        PreparedStatement deleteTicketsStatement = null;

        try {
            /* Инициализация */
            connection = ConnectionManager.getBaseConnection();
            deleteFlightStatement = connection.prepareStatement(deleteFlightSql);
            deleteTicketsStatement = connection.prepareStatement(deleteTicketsSql);
            /*
            Отключаем в базе AutoCommit, т.е. теперь
            все подтверждения запросов вручную.
            */
            connection.setAutoCommit(false);
            /* Передаем параметры в наши PreparedStatement запросы */
            deleteFlightStatement.setLong(1, flightId);
            deleteTicketsStatement.setLong(1, flightId);

            /*
            Когда мы создавали таблицы см. папку base в ConnectLessOne.
            Мы таблицу 'flight':
            **********************************************************************
            CREATE TABLE flight
            (
                id BIGSERIAL PRIMARY KEY ,
                flight_no VARCHAR(16) NOT NULL ,
                departure_date TIMESTAMP NOT NULL ,
                departure_airport_code CHAR(3) REFERENCES airport(code) NOT NULL ,
                arrival_date TIMESTAMP NOT NULL ,
                arrival_airport_code CHAR(3) REFERENCES airport(code) NOT NULL ,
                aircraft_id INT REFERENCES aircraft (id) NOT NULL ,
                status VARCHAR(32) NOT NULL
            );
            **********************************************************************
            привязали к таблице 'ticket', через внешний ключ 'flight_id'
            **********************************************************************
            CREATE TABLE ticket
            (
                id BIGSERIAL PRIMARY KEY ,
                passenger_no VARCHAR(32) NOT NULL ,
                passenger_name VARCHAR(128) NOT NULL ,
                flight_id BIGINT REFERENCES flight (id) NOT NULL ,
                seat_no VARCHAR(4) NOT NULL,
                cost NUMERIC(8, 2) NOT NULL
            );
            **********************************************************************
            И теперь мы можем безболезненно удалить данные из таблицы 'ticket',
            а вот данные связные с этой таблицей из таблицы flight мы не можем.

            Не сложно заметить, что внешний ключ не имеет дополнительного
            ограничения, например:
            - ON DELETE CASCADE - означает, что если удаляется запись в
                                  родительской таблице, то соответствующие
                                  записи в дочерней таблице будут удалены
                                  автоматически - каскадное удаление.
            - ON DELETE SET NULL - означает, что если запись в родительской
                                   таблице удаляется, то соответствующие поля
                                   в записи дочерней таблице, имеющие foreign
                                   key станут NULL.
            */
            deleteTicketsStatement.executeUpdate();
            /*
            Предыдущий запрос отправил базе команду на удаление
            билетов с 10 рейса, текущая строка выводит количество
            удаленных строк.
            */
            System.out.println("Удалили билетов: " + deleteTicketsStatement.getUpdateCount());
            /* Имитируем проблему т.е. ниже программа не пойдет*/
            if (true) {
                throw new RuntimeException("Имитируем проблему! После которой, вроде бы удаленные, билеты останутся в базе");
            }
            /*
            Пытаемся удалить рейс с номером 10, но вылетит ошибка,
            т.к. таблицы связны и данная операция без доп. инструкций
            не допустима.
            */
            deleteFlightStatement.executeUpdate();
            /*
            АвтоКоммит отключен и мы делаем подтверждение операций
            руками, однако до сюда программа не дойдет
            */
            connection.commit();

        } catch (Exception e) {
            /*
            Поскольку мы словили исключение, то нужно отменить
            удаление билетов, предварительно проверив соединение
            на NULL
            */
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            /*
            Поскольку у нас нет блока try-with-resources,
            то закрываем все соединения и стайтменты руками.
            */
            if (connection != null) {
                connection.close();
            }
            if (deleteFlightStatement != null) {
                deleteFlightStatement.close();
            }
            if (deleteTicketsStatement != null) {
                deleteTicketsStatement.close();
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