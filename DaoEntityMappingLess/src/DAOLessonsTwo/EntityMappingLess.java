package DAOLessonsTwo;

import DAOLessonsTwo.flight_repository_dao.FlightDao;
import DAOLessonsTwo.flight_repository_dao.TicketDao;
import DAOLessonsTwo.flight_repository_entity.Flight;

import java.time.LocalDateTime;

public class EntityMappingLess {
    public static void main(String[] args) {
        System.out.println("************************* Тест *.testFindByTicketId() *************************");
        long find_id = 55L;
        testFindByTicketId(find_id);

        System.out.println("************************* Тест *.testSaveFlight() *************************");
        testSaveFlight();

        System.out.println("************************* Тест *.testDeleteFlight() *************************");
        testDeleteFlight(50L);

        System.out.println("************************* Тест *.testFlightFindAll() *************************");
        testFindAllFlight();

        System.out.println("************************* Тест *.testUpdateFlightData() *************************");
        testUpdateFlightData();
    }
    /* На момент тестирования ID билетов лежали в диапазоне от 1L до 55L */
    private static void testFindByTicketId(Long id) {
        var ticket = TicketDao.getInstance().findById(id);
        System.out.println(ticket);
    }

    private static void testSaveFlight() {
        /* Получаем объект FlightDao */
        var flightDao = FlightDao.getInstance();
        /*
        Создаем новый рейс и загружаем его данными.
        Поскольку класс Flight у нас Record, цельный
        и не изменяемый на входе, то его мы загружаем
        полностью в плоть до ID, хотя по факту в базу
        его не передаем, т.к. он создается автоматически.
        */
        var load_flight = new Flight(
                1L,
                "KX4946",
                LocalDateTime.of(2020, 6, 14, 14, 30, 10),
                "LDN",
                LocalDateTime.of(2020, 7, 14, 18, 10, 15),
                "MSK",
                1,
                "ARRIVED");
        /* Получаем результат работы метода *.save() класса FlightDao */
        var savedFlight = flightDao.save(load_flight);
        /* Результат на экран */
        System.out.println(savedFlight);
    }
    private static void testDeleteFlight(Long id){
        var canDelete = FlightDao.getInstance().delete(id);
        /*
        true - если такой билет в базе есть и его можно удалить
        false - если такого билета в базе нет и его нельзя удалить
        */
        System.out.println(canDelete);
    }
    private static void testFindAllFlight(){
        for (Flight prn: FlightDao.getInstance().findAll())
        {
            System.out.println(prn.toString());
        }
    }
    private static void testUpdateFlightData(){
        long test_for_update_id = 9L;
        System.out.println(FlightDao.getInstance().findById(test_for_update_id));
        /*
        Старые данные рейса:
        Flight {id = 9,
                flightNo = 'QS8712',
                departureDate = 2020-12-18T03:35,
                departureAirportCode = 'MNK',
                arrivalDate = 2020-12-18T06:46,
                arrivalAirportCode = 'LDN',
                aircraftId = 2,
                status = 'ARRIVED'}
        */
        Flight updateFlightData =
                new Flight(test_for_update_id,
                      "112233LL",
                        LocalDateTime.of(2021, 9, 15, 19, 30, 10),
            "MSK",
                        LocalDateTime.of(2021, 9, 16, 21, 10, 15),
               "MNK",
                      1,
                        "GONE OFF RADAR");
        FlightDao.getInstance().update(updateFlightData);
        /* Проводим повторный поиск данных */
        System.out.println(FlightDao.getInstance().findById(test_for_update_id));
    }
}
