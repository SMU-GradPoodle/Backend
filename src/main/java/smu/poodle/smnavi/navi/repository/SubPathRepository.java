package smu.poodle.smnavi.navi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.poodle.smnavi.navi.domain.path.SubPath;
import smu.poodle.smnavi.navi.domain.station.Waypoint;
import smu.poodle.smnavi.navi.enums.TransitType;

import java.util.List;
import java.util.Optional;

public interface SubPathRepository extends JpaRepository<SubPath, Long> {

    Optional<SubPath> findTopBySrcAndDst(Waypoint src, Waypoint dst);
    List<SubPath> findByLineNameAndTransitType(String busNumber, TransitType transitType);
}
