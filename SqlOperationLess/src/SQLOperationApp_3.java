/*
Пример создания SQL запроса и отправки его в
базу данных:
- Изменим уже созданные таблицы данными 'UPDATE';
- Получим результат изменений 'RETURNING';

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
import java.sql.SQLException;
import java.sql.Statement;

public class SQLOperationApp_3 {

     public static void main(String[] args) {
         /*
         Обновим наши таблицы
         */
         String update_data = """
                 UPDATE train_base.info
                 SET data = 'Not_test_data_UPDATE'
                 WHERE id = 2;
                                 
                 UPDATE train_base.route
                 SET name_of_route = 'Лысая Гора - Мертвый Родник' 
                 WHERE name_of_route = 'Лукоморье - Остров Буян';                
                """;
         /*
         Обновим таблицу и вернем результат 'RETURNING'
         */
         String update_data_2 = """
                 INSERT INTO train_base.train_stop (name_of_stop)
                 VALUES 
                 ('Калинов Мост');
                 
                 UPDATE train_base.train_stop
                 SET name_of_stop = 'Медная Гора'
                 WHERE name_of_stop = 'Калинов Мост'
                 RETURNING *;
                """;
        /*
        В блоке try-with-resources создаем соединение с базой,
        и создаем объект Statement для отправки SQL операторов
        (запросов) в базу данных.
        */
        try (Connection connection = ConnectionManager.getBaseConnection();
             Statement statement = connection.createStatement()) {
                 /*
                 Отправляем запрос в базу данных. Используем метод
                 *.executeUpdate() для получения результата операций;
                 */
                 int execute_res = statement.executeUpdate(update_data);
                 System.out.println(execute_res);

                 /*
                 Если в запросе мы используем RETURNING, то на выходе
                 получаем строки (поля в которых произошли изменения)
                 */
                 boolean execute_res_2 = statement.execute(update_data_2);
                 System.out.println(execute_res_2);
                 System.out.println(statement.getUpdateCount());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}