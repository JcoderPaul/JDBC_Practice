package DAOLessonsOne.flight_repository_dto;
/*
Специальный класс Record см. описание в DOC
*/
public record TicketFilter(int limit,
                           int offset,
                           String passengerName,
                           String seatNo) {
}
