package DAOLessonsOne;

import DAOLessonsOne.flight_repository_dao.TicketDao;
import DAOLessonsOne.flight_repository_entity.Ticket;

import java.math.BigDecimal;
import java.util.List;

public class FindAndUpdateLess {

    public static void main(String[] args) {
        System.out.println("***************** Все билеты *****************");
        List<Ticket> tickets = TicketDao.getInstance().findAll();
        for (Ticket prn: tickets) {
            System.out.println(prn);
        };
        System.out.println("**********************************************");
        Long my_id = 2L;
        double cost_for_edit = 241.34;
        System.out.println("Получаем данные о билете с ID: " + my_id);
        findByIdTest(my_id);
        System.out.println("\nИзменяем поле 'cost' в билете с ID: " + my_id + " на " + cost_for_edit);
        updateTest(my_id, cost_for_edit);
        System.out.println("\nПолучаем данные о билете с ID: " + my_id);
        findByIdTest(my_id);


    }
    /*
    Тестовый метод для проверки работы методов:
    - *.findById()
    - *.update()
    класса TicketDao.
    */
    private static void updateTest(Long id, double cost) {
        /* Получаем объект TicketDao */
        var ticketDao = TicketDao.getInstance();
        /* Получаем запись из базы данных по ID */
        var maybeTicket = ticketDao.findById(id);
        /*
        Если запись найдена тогда изменяем поле 'cost'
        и обновляем запись в базе данных. Метод класса
        Optional *.ifPresent() - позволяет выполнить
        какое-то действие, если объект не пустой.
        */
        maybeTicket.ifPresent(ticket -> {
            ticket.setCost(BigDecimal.valueOf(cost));
            ticketDao.update(ticket);
        });
    }
    private static void findByIdTest(Long id) {
        var ticketDao = TicketDao.getInstance();
        var maybeTicket = ticketDao.findById(id);
        System.out.println(maybeTicket);
    }
}