package smu.poodle.smnavi.map.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import smu.poodle.smnavi.map.domain.path.FullPath;
import smu.poodle.smnavi.map.domain.station.Waypoint;

import java.util.List;


public interface FullPathRepository extends JpaRepository<FullPath, Long> {

    @Query("select f from FullPath as f where f.startWaypoint.id = :startPlaceId")
    List<FullPath> findByStartPlaceId(@Param("startPlaceId") Long startPlaceId);

    FullPath findFullPathById(Long id);
}

