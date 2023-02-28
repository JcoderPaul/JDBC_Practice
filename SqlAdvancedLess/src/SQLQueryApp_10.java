/*
Исследование методов интерфейса DatabaseMetaData:
- *.getCatalogs()
- *.getCatalogs()
- *.getSchemas()
- *.getTables()

Не забываем подключить в настройках
драйвер соединения с базой данных (*.jar),
который лежит в папке 'lib' проекта 'JDBCLessonOne',
иначе словим исключение. Не забываем пометить
папку 'resources', как ресурсную.
*/

import connection_util.ConnectionManager;

import java.sql.*;

public class SQLQueryApp_10 {

    public static void main(String[] args) throws SQLException {
        /* Тестируем работу методов интерфейса DatabaseMetaData */
        checkMetaData();
    }

    private static void checkMetaData() throws SQLException {
        try (Connection connection = ConnectionManager.getBaseConnection()) {
            /*
            Для работы с метаданными нашей базы данных нам не нужен
            интерфейс Statement. Нам нужен интерфейс DatabaseMetaData
            и его методы.

            С помощью интерфейса Connection и реализации его метода
            *.getMetaData() - мы извлекаем объект DatabaseMetaData,
                              содержащий метаданные о базе данных,
                              к которой этот объект Connection
                              представляет соединение.

            У нас это база: flight_repository
            */
            DatabaseMetaData metaData = connection.getMetaData();
            /*
            Метод *.getCatalogs() - извлекает имена
            каталогов, доступные в этой базе данных.
            */
             ResultSet catalogs = metaData.getCatalogs();
             /* Перебираем результирующие строки запроса (как Iterator) */
             while (catalogs.next()) {
                 /* Определили имя каталога(ов) с которым(и) будем работать */
                 String catalog = catalogs.getString(1);
                 System.out.println("*************************** BASE CATALOGS ***************************");
                 System.out.println(catalog);
                    /*
                    Метод *.getSchemas() - извлекает имена схем, доступные в этой базе данных.

                    Из документации можно узнать, что мы можем извлечь из ResultSet:
                    - TABLE_SCHEM String => schema name
                    - TABLE_CATALOG String => catalog name (may be null)
                    */
                    ResultSet schemas = metaData.getSchemas();
                    System.out.println("*************************** SCHEMA ***************************");
                    while (schemas.next()) {
                        /*
                        Метод *.getString(String columnLabel) интерфейса ResultSet
                        возвращает String и извлекает значение указанного столбца
                        в текущей строке текущего объекта ResultSet в виде строки
                        на языке программирования Java.

                        Раннее мы определились, что хотим извлечь TABLE_SCHEM -
                        имена наших схем в базе.
                        */
                        String schema = schemas.getString("TABLE_SCHEM");
                        System.out.println("Name of schema: " + schema);
                        /*
                        Метод *.getTables(String catalog, String schemaPattern,
                                          String tableNamePattern, String[] types)
                        интерфейса DatabaseMetaData возвращает ResultSet и извлекает
                        описание таблиц, доступных в данном каталоге.

                        С данными аргументами (которыми можно "играть") мы хотим
                        получить данные из конкретного 'catalog','schema' используя
                        '%' - мы говорим методу, что все имена. И наконец, последним
                        аргументом мы показываем, что нас интересуют только "TABLE",
                        а не: "TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY",
                              "LOCAL TEMPORARY", "ALIAS", "SYNONYM".
                        */
                        ResultSet tables = metaData.getTables(catalog,
                                                              schema,
                                                "%",
                                                               new String[] {"TABLE"});
                        /* Извлекаем имена таблиц из двух наших схем */
                        if (schema.equals("flight_repository") || schema.equals("train_base")) {
                            System.out.println("***************** TABLE OF '" + schema + "' SCHEMA *****************");
                            while (tables.next()) {
                                System.out.println("Table name: " + tables.getString("TABLE_NAME"));
                            }
                            System.out.println("********************************************************************");
                        }
                    }
             }
        }
    }
}