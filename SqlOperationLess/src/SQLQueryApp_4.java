/*
Пример создания SQL запроса и отправки его в
базу данных:
- Заполним уже созданные таблицы данными 'SELECT';

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

public class SQLQueryApp_4 {

     public static void main(String[] args) throws SQLException{
         /*
         Запрашиваем все данные из таблицы 'ticket' схемы
         'flight_repository', которую создали на уроке
         'ConnectLessOne' пример скриптов лежит в папке 'base'
         */
         String sql_query = """
                 SELECT *
                 FROM flight_repository.ticket               
                 """;
         /*
         В блоке try-with-resources создаем соединение с базой,
         и создаем объект Statement для отправки SQL операторов
         (запросов) в базу данных.
         */
        try (Connection connection = ConnectionManager.getBaseConnection();
             /*
             TYPE_SCROLL_INSENSITIVE - Константа, указывающая тип объекта ResultSet,
                                       который можно прокручивать, но обычно не
                                       чувствителен к изменениям данных, лежащих в
                                       основе ResultSet.

                                       Т.е. мы можем свободно перемещаться по нашему
                                       полученному SET-у и вверх и вниз, а не только
                                       в одну сторону например FETCH_FORWARD.

                                       И можем использовать методы для установки курсора:
                                       *.afterLast(), *.last(), *.beforeFirst(), *.first()
                                       и т.д. Например: executeResult.last()

             CONCUR_UPDATABLE - Константа, указывающая режим параллелизма для объекта
                                ResultSet, который может быть обновлен.

                                Т.е. мы можем используя методы *.update...() производить
                                изменения в таблицах базы данных.
                                Например: executeResult.updateLong("id", 1000);
             */
             Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                              ResultSet.CONCUR_UPDATABLE))
        {
                 /*
                 Отправляем запрос в базу данных. Используем метод
                 *.executeQuery() для получения результата операций.
                 Возвращен будет объект ResultSet (см. документацию
                 папка DOC проекта)
                 */
                    ResultSet executeResult = statement.executeQuery(sql_query);
                    /*
                    boolean next() - Перемещает курсор на одну строку вперед от его
                                     текущей позиции. Только после того, как мы
                                     перевели курсор, мы можем читать данные за
                                     курсором.

                    В данном случае мы в цикле пробегаем по данным полученным
                    из запроса, получаем значения отдельных полей таблицы и
                    выводим их на экран.
                    */
                System.out.println("****** Результат SQL запроса ******");
                    while (executeResult.next()) {
                        System.out.println(executeResult.getLong("id"));
                        System.out.println(executeResult.getString("passenger_no"));
                        System.out.println(executeResult.getBigDecimal("cost"));
                        System.out.println("*************************************");
                    }
        }
    }
}