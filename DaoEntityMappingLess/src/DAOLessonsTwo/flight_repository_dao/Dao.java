package DAOLessonsTwo.flight_repository_dao;

import java.util.List;
import java.util.Optional;

public interface Dao<K, E> {

    boolean delete(K id);

    E save(E dataIn);

    void update(E dataIn);

    Optional<E> findById(K id);

    List<E> findAll();
}
