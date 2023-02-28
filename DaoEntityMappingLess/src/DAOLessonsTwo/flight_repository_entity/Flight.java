package DAOLessonsTwo.flight_repository_entity;

import java.time.LocalDateTime;

public record Flight(Long id,
                     String flightNo,
                     LocalDateTime departureDate,
                     String departureAirportCode,
                     LocalDateTime arrivalDate,
                     String arrivalAirportCode,
                     Integer aircraftId,
                     String status) {
    @Override
    public String toString() {
        return "Flight {" +
                "id = " + id +
                ", flightNo = '" + flightNo + '\'' +
                ", departureDate = " + departureDate +
                ", departureAirportCode = '" + departureAirportCode + '\'' +
                ", arrivalDate = " + arrivalDate +
                ", arrivalAirportCode = '" + arrivalAirportCode + '\'' +
                ", aircraftId = " + aircraftId +
                ", status = '" + status + '\'' +
                '}';
    }
}
