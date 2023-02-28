package DAOLessonsTwo.flight_repository_dao;

import DAOLessonsTwo.connection_util.ConnectionPoolManager;
import DAOLessonsTwo.dao_exception.DaoException;
import DAOLessonsTwo.flight_repository_entity.Flight;
import DAOLessonsTwo.flight_repository_entity.Ticket;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FlightDao implements Dao<Long, Flight> {
    /* Объявляем переменную FlightDao, которая будет в единственном экземпляре. */
    private static FlightDao INSTANCE;
    /* Приватный конструктор */
    private FlightDao() {
    }
    /* Метод вызывающий нашу Singleton сущность */
    public static FlightDao getInstance() {
        if(INSTANCE == null){
            INSTANCE = new FlightDao();
        }
        return INSTANCE;
    }
    /*
    SQL запрос на удаление одной записи из таблицы
    Flight по ID в формате PrepareStatement.
    */
    private static final String DELETE_SQL = """
            DELETE FROM flight_repository.flight
            WHERE id = ?
            """;
    /*
    SQL запрос на вставку одной записи в таблицу
    Flight в формате PrepareStatement.
    */
    private static final String SAVE_SQL = """
            INSERT INTO flight_repository.flight (flight_no, departure_date, departure_airport_code, arrival_date, arrival_airport_code, aircraft_id, status) 
            VALUES (?, ?, ?, ?, ?, ?, ?);
            """;
    /*
    SQL запрос на изменение одной записи в таблице
    Ticket по ID в формате PrepareStatement
    */
    private static final String UPDATE_SQL = """
            UPDATE flight_repository.flight
            SET flight_no = ?,
                departure_date = ?,
                departure_airport_code = ?,
                arrival_date = ?,
                arrival_airport_code = ?,
                aircraft_id = ?,
                status = ?
            WHERE id = ?
            """;
    /* SQL запрос на получение всех записей из таблицы Flight */
    private static final String FIND_ALL_SQL = """
            SELECT id,
                   flight_no,
                   departure_date,
                   departure_airport_code,
                   arrival_date,
                   arrival_airport_code,
                   status,
                   aircraft_id
            FROM flight_repository.flight
            """;
    /*
    SQL запрос на получение данных одной записи в
    таблице Flight по ID в формате PrepareStatement
    */
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + """
            WHERE id = ?
            """;

    @Override
    public boolean delete(Long id) {
        try(var connectionPool = ConnectionPoolManager.getConnectionFromPool();
        var prepareStatement = connectionPool.prepareStatement(DELETE_SQL))
        {
            prepareStatement.setLong(1, id);
            return prepareStatement.executeUpdate() > 0;
        } catch (SQLException exp) {
            throw new DaoException(exp);
        }
    }

    @Override
    public Flight save(Flight dataIn) {
        try(var connection =
                    ConnectionPoolManager.getConnectionFromPool();
            var prepareStatement =
                    connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS))
        {
            prepareStatement.setString(1, dataIn.flightNo());
            prepareStatement.setTimestamp(2, Timestamp.valueOf(dataIn.departureDate()));
            prepareStatement.setString(3, dataIn.departureAirportCode());
            prepareStatement.setTimestamp(4, Timestamp.valueOf(dataIn.arrivalDate()));
            prepareStatement.setString(5, dataIn.arrivalAirportCode());
            prepareStatement.setInt(6, dataIn.aircraftId());
            prepareStatement.setString(7, dataIn.status());

            prepareStatement.executeUpdate();
            var generatedAutoId = prepareStatement.getGeneratedKeys();
            Flight back_ticket = null;
            if(generatedAutoId.next())
            {
                long id = generatedAutoId.getLong("id");
                back_ticket = new Flight(id,
                                         dataIn.flightNo(),
                                         dataIn.departureDate(),
                                         dataIn.departureAirportCode(),
                                         dataIn.arrivalDate(),
                                         dataIn.arrivalAirportCode(),
                                         dataIn.aircraftId(),
                                         dataIn.status());
            }
        return back_ticket;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void update(Flight dataIn) {
        try(Connection connection = ConnectionPoolManager.getConnectionFromPool();
        var prepareStatement = connection.prepareStatement(UPDATE_SQL))
        {
            prepareStatement.setString(1,dataIn.flightNo());
            prepareStatement.setTimestamp(2, Timestamp.valueOf(dataIn.departureDate()));
            prepareStatement.setString(3, dataIn.departureAirportCode());
            prepareStatement.setTimestamp(4, Timestamp.valueOf(dataIn.arrivalDate()));
            prepareStatement.setString(5, dataIn.arrivalAirportCode());
            prepareStatement.setInt(6, dataIn.aircraftId());
            prepareStatement.setString(7, dataIn.status());
            prepareStatement.setLong(8, dataIn.id());

            prepareStatement.executeUpdate();
        } catch (SQLException exp) {
            throw new DaoException(exp);
        }
    }

    @Override
    public Optional<Flight> findById(Long id) {
        try (var connection = ConnectionPoolManager.getConnectionFromPool()) {
            return findById(id, connection);
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public Optional<Flight> findById(Long id, Connection connection) {
        try (var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setLong(1, id);

            var resultSet = preparedStatement.executeQuery();
            Flight flight = null;
            if (resultSet.next()) {
                flight = new Flight(
                        resultSet.getLong("id"),
                        resultSet.getString("flight_no"),
                        resultSet.getTimestamp("departure_date").toLocalDateTime(),
                        resultSet.getString("departure_airport_code"),
                        resultSet.getTimestamp("arrival_date").toLocalDateTime(),
                        resultSet.getString("arrival_airport_code"),
                        resultSet.getInt("aircraft_id"),
                        resultSet.getString("status")
                );
            }
            return Optional.ofNullable(flight);
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    @Override
    public List<Flight> findAll() {
        try(Connection connection = ConnectionPoolManager.getConnectionFromPool();
        var prepareStatement = connection.prepareStatement(FIND_ALL_SQL))
        {
            var resOfQuery = prepareStatement.executeQuery();
            List<Flight> findAllFlight = new ArrayList<>();
            while (resOfQuery.next()){
                findAllFlight.add(buildFlight(resOfQuery));
            }
            return findAllFlight;
        } catch (SQLException exp) {
            throw new DaoException(exp);
        }
    }

    private Flight buildFlight(ResultSet resultSet) throws SQLException {
        return new Flight(
                resultSet.getLong("id"),
                resultSet.getString("flight_no"),
                resultSet.getTimestamp("departure_date").toLocalDateTime(),
                resultSet.getString("departure_airport_code"),
                resultSet.getTimestamp("arrival_date").toLocalDateTime(),
                resultSet.getString("arrival_airport_code"),
                resultSet.getInt("aircraft_id"),
                resultSet.getString("status")
        );
    }
}
