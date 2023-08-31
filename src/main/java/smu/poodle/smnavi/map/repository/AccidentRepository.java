package smu.poodle.smnavi.map.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import smu.poodle.smnavi.map.domain.Accident;

import java.util.List;

public interface AccidentRepository extends JpaRepository<Accident, Long> {
    @Query("select a from Accident as a " +
            "order by a.createdAt desc " +
            "limit 3")
    List<Accident> findTopThree();
}
