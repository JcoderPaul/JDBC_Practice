package DAOLessonsTwo.connection_util;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class ConnectionPoolManager {
    /* Извлекаем данные из application.properties */
    private static final String PASSWORD_KEY = "db.password";
    private static final String USERNAME_KEY = "db.username";
    private static final String URL_KEY = "db.url";
    private static final String POOL_SIZE_KEY = "db.pool.size";
    /* Задаем размер пула по-умолчанию */
    private static final Integer DEFAULT_POOL_SIZE = 10;
    /*
    Потокобезопасная блокирующая очередь для
    хранения наших прокси - соединений
    */
    private static BlockingQueue<Connection> pool;
    /* Список наших реальных соединений */
    private static List<Connection> sourceConnections;

    static {
        loadDriver();
        initConnectionPool();
    }
    /* Приватный конструктор данного класса */
    private ConnectionPoolManager() {
    }

    private static void initConnectionPool() {
        /* Извлекаем значение размера будущего пула соединений из файла*/
        String poolSize = PropertiesUtil.get(POOL_SIZE_KEY);
        /*
        Существует вероятность, что в ходе извлечения данных из файла
        свойств, либо в момент преобразования данных в int может произойти
        ошибка (вот какая интересно?). Для того чтобы этого избежать
        воспользуемся тернарным оператором и как вариант подставим дефолтный
        размер пула.
        */
        int size = poolSize == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSize);
        /* Создаем очередь для хранения наших прокси - соединений */
        pool = new ArrayBlockingQueue<>(size);
        /*
        Создаем список для хранения наших реальных соединений.
        К нему мы обратимся в момент закрытия всех реальных
        соединений.
        */
        sourceConnections = new ArrayList<>(size);
        /*
        В цикле создаем реальное и прокси - соединение с базой.
        Помещаем и то и другое в свои коллекции данных.
        */
        for (int i = 0; i < size; i++) {
            Connection connection = openConnection();
            /*
            В общем случае прокси-объект - это объект, который служит
            посредником для доступа к другому объекту, каким-то образом
            меняя свойства или поведение этого объекта.

            Так что со стороны клиента (т.е. объекта-пользователя)
            поведение выглядит несколько не так, как было бы при
            непосредственном доступе.

            Используется также в случаях, когда прямой доступ к
            используемому объекту по какой-то причине невозможен.
            ('прокси - объект' - паттерн проектирования)

            Самое простое — использовать java.lang.reflect.Proxy,
            который является частью JDK. Этот класс может создать
            прокси-класс или напрямую его инстанс.

            Пользоваться прокси, встроенным в Java, очень просто.
            Все что нужно — реализовать java.lang.InvocationHandler,
            чтобы прокси-объект мог его вызывать.

            Интерфейс InvocationHandler крайне прост и содержит только
            один метод: invoke(). При его вызове, аргументы содержат
            проксируемый оригинальный объект, вызванный метод
            (как отражение объекта Method) и массив объектов исходных
            аргументов.
            */
            Connection proxyConnection = (Connection) Proxy.newProxyInstance(
                    ConnectionPoolManager.class.getClassLoader(),
                    new Class[]{Connection.class},
                    (proxy, method, args) -> method.getName().equals("close")
                                    ? pool.add((Connection) proxy)
                                    : method.invoke(connection, args));
            pool.add(proxyConnection);
            sourceConnections.add(connection);
        }
    }
    /* Методы для получения прокси - соединения из пула */
    public static Connection getConnectionFromPool() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    /*
    Приватный метод для создания соединения с базой данных
    сделанный для защиты от случайного создания соединения
    сверх размера пула.
    */
    private static Connection openConnection() {
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.get(URL_KEY),
                    PropertiesUtil.get(USERNAME_KEY),
                    PropertiesUtil.get(PASSWORD_KEY)
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /* Загружаем драйвер PostgreSQL */
    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    /* Метод закрывает реальные соединения с базой данных всего пула */
    public static void closePool() {
        try {
            for (Connection sourceConnection : sourceConnections) {
                sourceConnection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}