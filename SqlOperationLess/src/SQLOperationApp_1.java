/*
Пример создания SQL запроса и отправки его в
базу данных.

При этом не забываем подключить в настройках
драйвер соединения с базой данных (*.jar),
который лежит в папке 'lib' проекта 'JDBCLessonOne',
иначе словим исключение. Не забываем пометить
папку 'resources', как ресурсную.
*/
import connection_util.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLOperationApp_1 {

     public static void main(String[] args) {
         // Создаем схему 'train_base' в базе 'flight_repository'
         String create_schema = """
                CREATE SCHEMA IF NOT EXISTS train_base;
                """;
         // Создаем таблицу 'info' в схеме 'train_base'
         String create_table = """
                CREATE TABLE IF NOT EXISTS train_base.info (
                    id SERIAL PRIMARY KEY ,
                    data TEXT NOT NULL 
                );
                """;
        /*
        В блоке try-with-resources создаем соединение с базой,
        и создаем объект Statement для отправки SQL операторов
        (запросов) в базу данных.
        */
        try (Connection connection = ConnectionManager.getBaseConnection();
             Statement statement = connection.createStatement()) {
             /*
             Отправляем запрос в базу данных. Метод *.execute()
             возвращает состояние true/false в случае успешного
             или нет проведения операции.
             */
             boolean execute_res_1 = statement.execute(create_schema);
             System.out.println(execute_res_1);

             boolean execute_res_2 = statement.execute(create_table);
             System.out.println(execute_res_2);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}