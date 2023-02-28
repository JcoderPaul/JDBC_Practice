/*
Исследование работы с Blob и Clob.

В PL/SQL можно объявлять большие объекты четырех типов:
- BFILE - двоичный файл. Переменная этого типа содержит
          локатор файла, указывающий на файл операционной
          системы вне базы данных. Oracle интерпретирует
          содержимое файла как двоичные данные.
- BLOB - большой двоичный объект. Переменная этого типа
         содержит локатор LOB, указывающий на большой
         двоичный объект, хранящийся в базе данных.
- CLOB - большой символьный объект. Переменная этого типа
         содержит локатор LOB, указывающий на хранящийся
         в базе данных большой блок текстовых данных в
         наборе символов базы данных.
- NCLOB - большой символьный объект с поддержкой символов
          национальных языков (NLS). Переменная этого типа
          содержит локатор LOB, указывающий на хранящийся
          в базе данных большой блок текстовых данных с
          национальным набором символов.

Не забываем подключить в настройках
драйвер соединения с базой данных (*.jar),
который лежит в папке 'lib' проекта 'JDBCLessonOne',
иначе словим исключение. Не забываем пометить
папку 'resources', как ресурсную.
*/

import connection_util.ConnectionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLQueryApp_13 {

    public static void main(String[] args) throws IOException, SQLException {
        /*
        В схеме 'train_base' создадим таблицу 'image_trains'
        способную принять blob объект.
        */
        createTableWithBlob();
         /*
            В отличии от Oracle PL/SQL или MySQL в PostgreSQL
            тип blob - представлен bytea, а тип clob - это TEXT
         */
        saveImage(); // Сохраняем файл картинку в базу
        getImage(); // Извлекаем картинку из базы
    }
    private static void getImage() throws SQLException, IOException {
        var sql = """
                SELECT model_pic
                FROM train_base.image_trains
                WHERE id = ?
                """;
        try (var connection = ConnectionManager.getBaseConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, 1);
                var resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    var image_from_byte = resultSet.getBytes("model_pic");
                    Files.write(Path.of("files", "new_train.jpg"),
                                image_from_byte,
                                StandardOpenOption.CREATE);
                }
        }
    }
    private static void saveImage() throws SQLException, IOException {
        var sql = """
                UPDATE train_base.image_trains
                SET model_pic = ?
                WHERE id = 1
                """;
        /* Преобразуем файл картинку в массив байтов */
        byte[] image_in_byte = Files.readAllBytes(Path.of("files", "train.jpg"));
        /* Как всегда, формируем соединение с базой и готовим запрос */
        try (var connection = ConnectionManager.getBaseConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
                /* Помещаем наш массив байт в соответствующее поле таблицы */
                preparedStatement.setBytes(1, image_in_byte);
                /* Отправляем запрос к базе данных */
                preparedStatement.executeUpdate();
        }
    }
    private static void createTableWithBlob() {
        String create_sql = """
                CREATE TABLE IF NOT EXISTS train_base.image_trains
                   (
                     id SERIAL PRIMARY KEY,
                     model_name VARCHAR(128) NOT NULL,
                     model_pic BYTEA,
                     CONSTRAINT constraint_name UNIQUE (model_name)
                   );
                                
                COMMIT;
                
                INSERT INTO train_base.image_trains (model_name)
                VALUES ('Серебряная стрела'),
                       ('Черный горизонт'),
                       ('Красный петух');
                       
                COMMIT;       
                """;
        try(Connection my_create_connection = ConnectionManager.getBaseConnection();
            Statement my_ps = my_create_connection.createStatement()){

            my_ps.execute(create_sql);
        } catch (SQLException e) {
            System.out.println("Существует вероятность, что таблица уже создана или названия не уникальны");;
        }
    }

    /*
      Такой вариант кода можно применить при использовании Oracle PL/SQL или MySQL

      private static void saveImage() throws SQLException, IOException {
            var sql = """
                    UPDATE aircraft
                    SET image = ?
                    WHERE id = 1
                    """;
            try (var connection = ConnectionManager.open();
                 var preparedStatement = connection.prepareStatement(sql)) {
                connection.setAutoCommit(false);
                var blob = connection.createBlob();
                blob.setBytes(1, Files.readAllBytes(Path.of("files", "train.jpg")));

                preparedStatement.setBlob(1, blob);
                preparedStatement.executeUpdate();
                connection.commit();
            }
        }
     */
}