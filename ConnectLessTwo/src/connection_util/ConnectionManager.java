package connection_util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionManager {
    // Логин для подключения к базе данных
    private static String LOGIN = "postgres";
    // Пароль для подключения к базе данных
    private static String PASS = "testadmin";
    // Адрес для подключения к базе данных, обычно можно взять из настроек соединения IDE и базы.
    private static String BASEURL = "jdbc:postgresql://localhost:5432/flight_repository";

    static {
        // Загружаем наш PostgreSQL драйвер
        loadDriver();
    }
    /*
    Приватный конструктор данного класса
    */
    private ConnectionManager() {
    }
    /*
    Нам нужно получить соединение с базой данных, т.е. экземпляр
    класса Connection, это можно сделать, как и в прошлый раз через
    перегруженный метод *.getConnection(), класса DriverManager.
    */
    public static Connection getBaseConnection() {
        try {
            /*
            DriverManager.getConnection(DB_URL, DB_USER_NAME, DB_USER_PASSWORD) –
            устанавливаем соединение с СУБД. По переданному адресу, JDBC сама
            определит тип и местоположение нашей СУБД и вернёт Connection, который
            мы можем использовать для связи с БД.
            */
            return DriverManager.getConnection(BASEURL, LOGIN, PASS);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void loadDriver() {
        try {
            /*
            Метод forName() класса java.lang.Class возвращает объект Class,
            связанный с классом или интерфейсом с заданным строковым именем.
            Вызов этого метода эквивалентен:

            Class.forName(className, true, currentLoader)

            , где currentLoader обозначает определяющий загрузчик класса текущего
            класса. Например, следующий фрагмент кода возвращает дескриптор класса
            среды выполнения для класса с именем java.lang.Thread:

            Class t = Class.forName("java.lang.Thread")

            Вызов forName("XYZ") приводит к инициализации класса с именем XYZ.

            Параметры: className — полное имя вызываемого класса.
            Возвращает: объект Class для класса с указанным именем.

            Исключения:
            - LinkageError - если связь не удалась
            - ExceptionInInitializerError - если инициализация, спровоцированная
                                            этим методом, не удалась.
            - ClassNotFoundException - если класс не может быть расположен

            В нашем случае метод Class.forName загружает класс драйвера,
            который мы будем использовать.
            */
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}