package smu.poodle.smnavi.navi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.poodle.smnavi.navi.domain.station.BusStation;
import smu.poodle.smnavi.navi.domain.station.Waypoint;

import java.util.List;
import java.util.Optional;

public interface BusStationRepository extends JpaRepository<BusStation, Long> {
    Optional<BusStation> findFirstByLocalStationId(String localStationId);

    //todo : optional로 변경
    List<Waypoint> findAllByLocalStationId(String localStationId);

}
