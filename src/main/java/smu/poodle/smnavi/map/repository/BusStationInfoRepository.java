package smu.poodle.smnavi.map.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smu.poodle.smnavi.map.domain.station.BusStationInfo;

import java.util.List;

@Repository
public interface BusStationInfoRepository extends JpaRepository<BusStationInfo, Long> {
    List<BusStationInfo> findByBusName(String busName);
}