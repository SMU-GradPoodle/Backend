package smu.poodle.smnavi.navi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.poodle.smnavi.navi.domain.station.SubwayStation;
import smu.poodle.smnavi.navi.domain.station.Waypoint;

import java.util.List;
import java.util.Optional;

public interface SubwayStationRepository extends JpaRepository<SubwayStation, Long> {

    Optional<SubwayStation> findFirstByStationId(Integer stationId);

    List<Waypoint> findAllByStationId(Integer stationId);
}
