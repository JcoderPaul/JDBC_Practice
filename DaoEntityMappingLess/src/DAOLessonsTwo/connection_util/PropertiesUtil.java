package DAOLessonsTwo.connection_util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtil {
    /*
    Создаем объект класса Properties - унаследован от Hashtable<Object,Object>.
    Его даже можно рассматривать как HashTable, который умеет загружать себя
    из файла.

    Обычно нам нужно выполнить всего две операции – загрузить в объект Properties
    данные из какого-нибудь файла, а затем получить эти свойства с помощью метода
    getProperty(). И естественно мы можем пользоваться объектом Properties как
    HashMap.

    Класс Properties представляет постоянный набор свойств. Свойства можно
    сохранить в поток или загрузить из потока. Каждый ключ и соответствующее ему
    значение в списке свойств являются строкой.
    */
    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private PropertiesUtil() {
    }
    /*
    Метод позволяющий получить свойства ключей
    из файла свойств application.properties
    */
    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    private static void loadProperties() {
        /*
        В блоке try -with-resources получаем (открываем)
        входящий поток данных из файла свойств.

        1. Метод getClassLoader() класса java.lang.Class
        используется для получения classLoader текущего
        объекта. Этот объект может быть классом, массивом,
        интерфейсом и т. д. Метод возвращает classLoader
        этого объекта.

        classLoader - это объект, отвечающий за загрузку
        классов. Класс ClassLoader является абстрактным
        классом. Учитывая двоичное имя класса, загрузчик
        класса должен попытаться найти или сгенерировать
        данные, составляющие определение класса. Типичная
        стратегия состоит в том, чтобы преобразовать имя в
        имя файла, а затем прочитать «файл класса» с этим
        именем из файловой системы.

        2. Метод getResourceAsStream() класса java.lang.Class
        используется для получения ресурса с указанным ресурсом
        текущего класса. Метод возвращает указанный ресурс
        данного класса в виде объекта InputStream.

        Метод принимает параметр resourceName, который является
        ресурсом для получения данных (например, пары KEY-VALUE,
        как у нас).
        */
        try (InputStream inputStream = PropertiesUtil.class
                                                     .getClassLoader()
                                                     .getResourceAsStream("application.properties"))
        {
            /*
            Метод *.load() считывает список свойств
            (пары ключей и элементов) из входного
            потока байтов.
            */
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}