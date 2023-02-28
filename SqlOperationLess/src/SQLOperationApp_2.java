/*
Пример создания SQL запроса и отправки его в
базу данных:
- Заполним уже созданные таблицы данными 'INSERT';
- Создадим новые таблицы и заполним их данными 'CREATE';
- Сделаем это каскадом в одном *.execute();
- Посмотрим как работает *.executeUpdate();

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

public class SQLOperationApp_2 {

     public static void main(String[] args) {
         /*
         Заполняем таблицу 'train_base.info' в базе 'flight_repository'
         данными. Мы можем отправлять сразу несколько запросов одновременно.

         Создадим еще и таблицу 'train_base.route' и тут же
         добавим данных в созданную таблицу.
         */
         String insert_data = """
                DELETE FROM train_base.info;
                
                INSERT INTO train_base.info (data)
                VALUES
                ('Test_data_1'),
                ('Test_data_2'),
                ('Test_data_3'),
                ('Test_data_4');
                
                                
                CREATE TABLE IF NOT EXISTS train_base.route (
                    id SERIAL PRIMARY KEY ,
                    name_of_route CHAR(128) NOT NULL 
                );
                
                DELETE FROM train_base.route;
                
                INSERT INTO train_base.route (name_of_route)
                VALUES
                ('Калинов мост - Замок Кощея'),
                ('Тридевятое царство - Коряжье болото'),
                ('Лукоморье - Остров Буян'),
                ('ИЗНАКУРНОЖ - ЛОЗМЕГОР');
                """;
         /*
         Создаем таблицу 'train_stop' в схеме 'train_base'
         с автоинкрементируемым id и символьным полем
         'name_of_stop'

         Вторым запросом заполняем вновь созданную таблицу
         */
         String create_table_insert_data = """
                CREATE TABLE IF NOT EXISTS train_base.train_stop 
                (
                    id SERIAL PRIMARY KEY ,
                    name_of_stop CHAR(256) NOT NULL 
                );
                
                DELETE FROM train_base.train_stop;
                
                INSERT INTO train_base.train_stop (name_of_stop)
                VALUES
                ('Калинов мост'),
                ('Пень Водяного'),
                ('Руины замка Кощея'),
                ('Клевые закутки');
                """;
        /*
        В блоке try-with-resources создаем соединение с базой,
        и создаем объект Statement для отправки SQL операторов
        (запросов) в базу данных.
        */
        try (Connection connection = ConnectionManager.getBaseConnection();
                 Statement statement = connection.createStatement()) {
                 /*
                 Отправляем запрос в базу данных.
                 */
                 boolean execute_res_1 = statement.execute(insert_data);
                 // Проверяем результат работы метода
                 System.out.println(execute_res_1);
                 // Уточняем, сколько строк было обновлено
                 System.out.println(statement.getUpdateCount());
                 /*
                 На практике можно использовать метод *.executeUpdate()
                 для получения результата операций
                 */
                 int execute_res_2 = statement.executeUpdate(create_table_insert_data);
                 System.out.println(execute_res_2);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}