package DAOLessonsOne;

import DAOLessonsOne.flight_repository_dao.TicketDao;
import DAOLessonsOne.flight_repository_dto.TicketFilter;

public class FindAllWithFilterLess {
    public static void main(String[] args) {
        var ticketFilter = new TicketFilter(3, 0, "Евгений Кудрявцев", "A1");
        var tickets = TicketDao.getInstance().findAll(ticketFilter);
        System.out.println(tickets);

        System.out.println("**********************************************************************");
        var ticketFilter_without_passName = new TicketFilter(3, 0, null, "A1");
        var tickets_2 = TicketDao.getInstance().findAll(ticketFilter_without_passName);
        System.out.println(tickets);

        System.out.println("**********************************************************************");
        var ticketFilter_without_seatNo = new TicketFilter(3, 0, "Евгений Кудрявцев", null);
        var tickets_3 = TicketDao.getInstance().findAll(ticketFilter_without_seatNo);
        System.out.println(tickets);

        System.out.println("**********************************************************************");
        var ticketFilter_without_twoparams = new TicketFilter(3, 0, null, null);
        var tickets_4 = TicketDao.getInstance().findAll(ticketFilter_without_twoparams);
        System.out.println(tickets);
    }
}
