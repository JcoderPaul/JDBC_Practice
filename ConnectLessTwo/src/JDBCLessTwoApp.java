/*
Пример создания соединения с базой данных через
самописный "утилитный" класс ConnectionManager.

При этом не забываем подключить в настройках
драйвер соединения с базой данных (*.jar),
который лежит в папке lib проекта JDBCLessonOne,
иначе словим исключение.
*/
import connection_util.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;

public class JDBCLessTwoApp {

     public static void main(String[] args) {

        try (Connection connection = ConnectionManager.getBaseConnection()) {
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