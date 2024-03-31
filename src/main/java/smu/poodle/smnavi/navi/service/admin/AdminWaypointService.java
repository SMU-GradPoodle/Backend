package smu.poodle.smnavi.navi.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smu.poodle.smnavi.navi.domain.station.BusStation;
import smu.poodle.smnavi.navi.domain.station.Place;
import smu.poodle.smnavi.navi.domain.station.SubwayStation;
import smu.poodle.smnavi.navi.domain.station.Waypoint;
import smu.poodle.smnavi.navi.dto.AbstractWaypointDto;
import smu.poodle.smnavi.navi.repository.BusStationRepository;
import smu.poodle.smnavi.navi.repository.PlaceRepository;
import smu.poodle.smnavi.navi.repository.SubwayStationRepository;
import smu.poodle.smnavi.navi.repository.WayPointRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminWaypointService {
    private final WayPointRepository wayPointRepository;
    private final BusStationRepository busStationRepository;
    private final SubwayStationRepository subwayStationRepository;
    private final PlaceRepository placeRepository;

    
    public List<Waypoint> saveStationListIfNotExist(List<AbstractWaypointDto> waypointDtoList) {

        List<Waypoint> waypointList = new ArrayList<>();

        for (AbstractWaypointDto waypointDto : waypointDtoList) {
            Waypoint waypoint = waypointDto.toEntity();
            Waypoint persistedWaypoint = saveIfNotExist(waypoint);
            waypointList.add(persistedWaypoint);
        }

        return waypointList;
    }
    
    public Waypoint saveIfNotExist(Waypoint waypoint) {
        Optional<? extends Waypoint> optionalWaypoint = findWaypoint(waypoint);

        if (optionalWaypoint.isPresent()) {
            return optionalWaypoint.get();
        } else {
            return wayPointRepository.save(waypoint);
        }
    }

    public Waypoint getSmuWayPoint() {
       return wayPointRepository.getSmuWayPoint();
    }

    private Optional<? extends Waypoint> findWaypoint(Waypoint waypoint) {
        if (waypoint instanceof BusStation) {
            return findBusStation((BusStation) waypoint);
        } else if (waypoint instanceof SubwayStation) {
            return findSubwayStation((SubwayStation) waypoint);
        } else if (waypoint instanceof Place) {
            return findPlace((Place) waypoint);
        }
        return Optional.empty();
    }

    private Optional<? extends Waypoint> findBusStation(BusStation busStation) {
        return busStationRepository.findFirstByLocalStationId(
                busStation.getLocalStationId());
    }

    private Optional<? extends Waypoint> findSubwayStation(SubwayStation subwayStation) {
        return subwayStationRepository.findFirstByStationId(
                subwayStation.getStationId());
    }

    private Optional<? extends Waypoint> findPlace(Place place) {
        return placeRepository.findFirstByPlaceName(place.getPlaceName());
    }

    public List<Waypoint> findAllById(List<Long> waypointIds) {
        return wayPointRepository.findAllById(waypointIds);
    }
}
