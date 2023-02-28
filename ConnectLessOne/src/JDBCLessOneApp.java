/*
Пример соединения с базой данных, когда все
настройки находятся в одном файле. В данном
случае это PostgreSQL.
*/
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCLessOneApp {
    // Логин для подключения к базе данных
    private static String LOGIN = "postgres";
    // Пароль для подключения к базе данных
    private static String PASS = "testadmin";
    // Адрес для подключения к базе данных, обычно можно взять из настроек соединения IDE и базы.
    private static String BASEURL = "jdbc:postgresql://localhost:5432/flight_repository";

    public static void main(String[] args) {
        /*
        DriverManager.getConnection(DB_URL, DB_USER_NAME, DB_USER_PASSWORD) – устанавливаем
        соединение с СУБД. По переданному адресу, JDBC сама определит тип и местоположение
        нашей СУБД и вернёт Connection, который мы можем использовать для связи с БД.
        */
        try (Connection connection = DriverManager.getConnection(BASEURL,LOGIN,PASS)) {
            /*
            В стандарте SQL описывается четыре уровня изоляции транзакций:
            — Read uncommited (Чтение незафиксированных данных),
            - Read committed (Чтение зафиксированных данных),
            - Repeatable read (Повторяемое чтение),
            - Serializable (Сериализуемость).

            *** 4 свойства транзакций ***

            Основные требования к транзакционной системе:
            - Atomicity (атомарность) — выражается в том, что транзакция должна быть
                                    выполнена в целом или не выполнена вовсе.
            - Consistency (согласованность) — гарантирует, что по мере выполнения транзакций,
                                          данные переходят из одного согласованного
                                          состояния в другое, то есть транзакция не может
                                          разрушить взаимной согласованности данных.
            - Isolation (изолированность) — локализация пользовательских процессов означает,
                                        что конкурирующие за доступ к БД транзакции
                                        физически обрабатываются последовательно,
                                        изолированно друг от друга, но для пользователей
                                        это выглядит, как будто они выполняются параллельно.
            - Durability (долговечность) — устойчивость к ошибкам — если транзакция завершена
                                       успешно, то те изменения в данных, которые были ею
                                       произведены, не могут быть потеряны ни при каких
                                       обстоятельствах.

            В данной ситуации мы смотрим уровень изоляции транзакций нашей базы.
            */
            System.out.println(connection.getTransactionIsolation());
            System.out.println(connection.getCatalog());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}