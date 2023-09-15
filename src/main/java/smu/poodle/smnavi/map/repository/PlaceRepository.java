package smu.poodle.smnavi.map.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import smu.poodle.smnavi.map.domain.station.Place;
import smu.poodle.smnavi.map.domain.station.Waypoint;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<? extends Waypoint> findFirstByPlaceName(String placeName);

    @Query("select distinct p from Place as p " +
            "right join FullPath as f on p = f.startWaypoint " +
            "where f.isSeen = true " +
            "order by p.placeName")
    List<Waypoint> findAllStartPlace();
}
