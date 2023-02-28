package DAOLessonsOne.flight_repository_entity;
/*
Data Access Object (DAO).

При проектировании информационной системы выявляются
некоторые слои, которые отвечают за взаимодействие
различных модулей системы. Соединение с базой данных
является одной из важнейшей составляющей приложения.
Всегда выделяется часть кода, модуль, отвечающающий
за передачу запросов в БД и обработку полученных от
неё ответов.

В общем случае, определение Data Access Object описывает
его как прослойку между БД и системой. DAO абстрагирует
сущности системы и делает их отображение на БД, определяет
общие методы использования соединения, его получение,
закрытие и (или) возвращение в Connection Pool.

Вершиной иерархии DAO является абстрактный класс или
интерфейс с описанием общих методов, которые будут
использоваться при взаимодействии с базой данных.
Как правило, это методы поиска, удаление по ключу,
обновление и т.д. -> CRUD - Copy Read Update Delete

Создадим сущность 'Билет' - 'Ticket', наша таблица
ticket в базе flight_repository содержит 6 - ть полей
вот их мы сделаем параметрами нашего объекта (сущности).
*/
import java.math.BigDecimal;

public class Ticket {
    // Bigint в SQL это аналог Long в Java
    private Long id;
    // Varchar(32) в SQL это аналог String в Java
    private String passengerNo;
    // Varchar(128) в SQL это аналог String в Java
    private String passengerName;
    // Bigint в SQL это аналог Long в Java
    private Long flightId;
    // Varchar(128) в SQL это аналог String в Java
    private String seatNo;
    /*
    Numeric (8,2) в SQL это аналог BigDecimal или Double
    (если нас не интересует прецизионная точность) в Java
    */
    private BigDecimal cost;
    /* Полный конструктор */
    public Ticket(Long id, String passengerNo, String passengerName,
                  Long flightId, String seatNo, BigDecimal cost) {
        this.id = id;
        this.passengerNo = passengerNo;
        this.passengerName = passengerName;
        this.flightId = flightId;
        this.seatNo = seatNo;
        this.cost = cost;
    }

    /* Конструктор без ID */
    public Ticket(String passengerNo, String passengerName,
                  Long flightId, String seatNo, BigDecimal cost) {
        this.passengerNo = passengerNo;
        this.passengerName = passengerName;
        this.flightId = flightId;
        this.seatNo = seatNo;
        this.cost = cost;
    }

    public Ticket() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassengerNo() {
        return passengerNo;
    }

    public void setPassengerNo(String passengerNo) {
        this.passengerNo = passengerNo;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public String getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(String seatNo) {
        this.seatNo = seatNo;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", passengerNo='" + passengerNo + '\'' +
                ", passengerName='" + passengerName + '\'' +
                ", flightId=" + flightId +
                ", seatNo='" + seatNo + '\'' +
                ", cost=" + cost +
                '}';
    }
}
