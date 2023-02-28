package DAOLessonsOne;

import DAOLessonsOne.flight_repository_dao.TicketDao;
import DAOLessonsOne.flight_repository_entity.Ticket;

import java.math.BigDecimal;

public class SaveAndDeleteLess {

    public static void main(String[] args) {
        saveTest();

        deleteTest(134L);
    }
    /*
    Блок простых статических методов для проверки
    работоспособности класса TicketDao и его методов
    */
    private static void deleteTest(Long id) {
        /*
        Создаем объект TicketDao, поскольку он реализован
        при помощи паттерна Одиночка - Singleton, то
        его единственный экземпляр можно получить методом
        *.getInstance()
        */
        var ticketDao = TicketDao.getInstance();
        /*
        Передаем полученный ID в метод для удаления записи
        в базе данных
        */
        var deleteResult = ticketDao.delete(id);
        System.out.println(deleteResult);
    }

    private static void saveTest() {
        /* Получаем объект TicketDao */
        var ticketDao = TicketDao.getInstance();
        /* Создаем новый билет и загружаем его данными */
        var load_ticket = new Ticket();
        load_ticket.setPassengerNo("1234567");
        load_ticket.setPassengerName("Test");
        load_ticket.setFlightId(3L);
        load_ticket.setSeatNo("B3");
        load_ticket.setCost(BigDecimal.TEN);
        /*
        В данных загруженных строками выше не хватает ID,
        т.к. он генерируется базой данных. После того как
        мы загрузили (сохранили) эти сведения (поля) в
        записи базы данных, мы получили ID и объект
        savedTicket стал полным в отличие от load_ticket
        */
        var savedTicket = ticketDao.save(load_ticket);
        /* Результат на экран */
        System.out.println(savedTicket);
    }

}