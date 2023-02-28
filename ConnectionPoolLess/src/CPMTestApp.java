/*
Тестируем наш пул соединений.

При этом не забываем подключить в настройках
драйвер соединения с базой данных (*.jar),
который лежит в папке 'lib' проекта 'JDBCLessonOne',
иначе словим исключение. Не забываем пометить
папку 'resources', как ресурсную.
*/
import connection_util.ConnectionPoolManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CPMTestApp {

     public static void main(String[] args) throws SQLException{
         /*
         Вставляем данные в таблицу 'info' схемы
         'train_base'. Вставляем только данные, т.к.
         id у нас автоинкрементируется. И вот его
         значение мы и хотим получить.
         */
         String sql_insert_query = """
                INSERT INTO train_base.info (data)
                VALUES 
                ('autogenerated_insert_data')
                """;
         /*
         В блоке try-with-resources создаем соединение с базой,
         и создаем объект Statement для отправки SQL операторов
         (запросов) в базу данных.
         */
        try (Connection connection = ConnectionPoolManager.getConnectionFromPool();
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
                                                              ResultSet.CONCUR_UPDATABLE)) {
            /*
            В данном случе применяем для разнообразия не конкретный класс/
            интерфейс int или ResultSet, а var.

            Начиная с версии 10, в Java появилось ключевое слово var. Новая
            фича — local variable type inference (выведение типа локальной
            переменной) — не даёт переменным дополнительных возможностей и
            ограничений на них не накладывает. Просто разработчикам не нужно
            теперь писать лишний код при объявлении переменных, когда их тип
            очевиден из контекста.

            Метод int executeUpdate(String sql, int autoGeneratedKeys) -
            Выполняет запрос SQL и сигнализирует драйверу с заданным флагом о
            том, следует ли сделать автоматически сгенерированные ключи,
            созданные этим объектом оператора, доступными для извлечения.

            RETURN_GENERATED_KEYS - Константа, указывающая, что сгенерированные
                                    ключи должны быть доступны для извлечения.
            */
            var executeResult = statement.executeUpdate(sql_insert_query,
                                                            Statement.RETURN_GENERATED_KEYS);
            /*
            ResultSet getGeneratedKeys() - Извлекает любые автоматически
                                           сгенерированные ключи, созданные
                                           в результате выполнения этого
                                           объекта оператора.
            */
            var generatedKeys = statement.getGeneratedKeys();
            /*
            Если новый ключ сгенерирован, т.е. перед курсором есть запись,
            то извлекаем значение ключа. Поскольку в нашем запросе мы точно
            знаем, что сделали одну вставку и будет сгенерирован лишь один
            ключ, мы не пользуемся циклом для получения всего SET-а, вернее
            наш SET будет содержать только одно значение. Его мы и извлекаем.

            Если бы мы вставляли много записей и было сгенерировано n-ключей,
            мы бы применили цикл для чтения содержимого всего SET-a.
            */
            if (generatedKeys.next()) {
                var generatedId = generatedKeys.getInt("id");
                System.out.println("Только что добавили запись с id:" + generatedId);
                /*
                Мы так же можем использовать порядковый номер колонки в ResultSet,
                у колонки-поля id порядковый номер - 1 у поля data - 2
                */
                var generatedId_by_num = generatedKeys.getInt(1);
                System.out.println("Только что добавили запись в поле 1:" + generatedId_by_num);
            }

        }
    }
}