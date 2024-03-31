package smu.poodle.smnavi.navi.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.poodle.smnavi.navi.domain.station.BusStation;
import smu.poodle.smnavi.navi.domain.BusStationInfo;
import smu.poodle.smnavi.navi.dto.ExposedBusStationDto;
import smu.poodle.smnavi.navi.enums.BusType;
import smu.poodle.smnavi.navi.enums.TransitType;
import smu.poodle.smnavi.navi.domain.path.Edge;
import smu.poodle.smnavi.navi.domain.path.FullPath;
import smu.poodle.smnavi.navi.domain.path.SubPath;
import smu.poodle.smnavi.navi.domain.station.Waypoint;
import smu.poodle.smnavi.navi.dto.PathDto;
import smu.poodle.smnavi.navi.dto.WaypointDto;
import smu.poodle.smnavi.navi.externapi.OdsayTransitRouteApi;
import smu.poodle.smnavi.navi.externapi.PathFindOdsayExternApi;
import smu.poodle.smnavi.navi.repository.BusStationInfoRepository;
import smu.poodle.smnavi.navi.repository.TransitRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminNaviService {
    private final OdsayTransitRouteApi odsayTransitRouteApi;
    private final PathFindOdsayExternApi pathFindOdsayExternApi;

    private final AdminWaypointService adminWaypointService;
    private final AdminEdgeService adminEdgeService;
    private final AdminSubPathService adminSubPathService;
    private final AdminFullPathService adminFullPathService;
    private final TransitRepository transitRepository;

    private final BusStationInfoRepository busStationInfoRepository;

    public void savePath(String startPlaceName, String startX, String startY, List<Integer> indexes) {

        List<PathDto.Info> pathDtos = odsayTransitRouteApi.callApi(startX, startY, indexes);

        WaypointDto.PlaceDto startPlace = WaypointDto.PlaceDto.builder()
                .placeName(startPlaceName)
                .gpsX(startX)
                .gpsY(startY)
                .build();

        for (PathDto.Info pathDto : pathDtos) {
            Waypoint startPoint = adminWaypointService.saveIfNotExist(startPlace.toEntity());

            List<SubPath> subPaths = new ArrayList<>(pathDto.getSubPathList().size());

            List<String> mapObjArr = Arrays.stream(pathDto.getMapObj().split("@")).collect(Collectors.toList());

            for (int i = 0; i < pathDto.getSubPathList().size(); i++) {
                subPaths.add(SubPath.builder().build());
            }

            int firstSectionTime = pathDto.getSubPathList().get(0).getSectionTime();
            subPaths.set(0, createFirstWalkSubPath(startPlace, firstSectionTime, pathDto.getSubPathList().get(1)));

            for (int i = 1; i < pathDto.getSubPathList().size(); i++) {
                PathDto.SubPathDto subPathDto = pathDto.getSubPathList().get(i);

                if (subPathDto.getTransitType() == TransitType.WALK)
                    continue;

                List<Waypoint> persistedWaypointList = adminWaypointService.saveStationListIfNotExist(subPathDto.getStationList());
                Waypoint subPathSrc = persistedWaypointList.get(0);
                Waypoint subPathDst = persistedWaypointList.get(persistedWaypointList.size() - 1);


                List<Edge> persistedEdgeList = adminEdgeService.makeAndSaveEdgeIfNotExist(persistedWaypointList);

                //엣지의 디테일 루트 만들기
                pathFindOdsayExternApi.callApiForSaveDetailPositionList(subPathDto,
                        mapObjArr.remove(0),
                        persistedEdgeList);

                SubPath subPath = SubPath.builder()
                        .sectionTime(subPathDto.getSectionTime())
                        .transitType(subPathDto.getTransitType())
                        .fromName(subPathDto.getFrom())
                        .toName(subPathDto.getTo())
                        .src(subPathSrc)
                        .dst(subPathDst)
                        .busType(BusType.fromTypeNumber(subPathDto.getBusTypeInt()))
                        .lineName(subPathDto.getLineName())
                        .build();

                SubPath persistedSubPath = adminSubPathService.saveWithEdgeMapping(subPath, persistedEdgeList);

                subPaths.set(i, persistedSubPath);
            }

            for (int i = 1; i < pathDto.getSubPathList().size(); i++) {
                PathDto.SubPathDto subPathDto = pathDto.getSubPathList().get(i);

                if (subPathDto.getTransitType() == TransitType.WALK) {
                    List<Edge> edges = new ArrayList<>();
                    Waypoint src, dst;
                    src = subPaths.get(i - 1).getDst();

                    if (i == pathDto.getSubPathList().size() - 1) {
                        dst = adminWaypointService.getSmuWayPoint();
                    } else {
                        dst = subPaths.get(i + 1).getSrc();
                    }

                    Edge edge = Edge.builder()
                            .src(src)
                            .dst(dst)
                            .detailExist(false)
                            .build();

                    adminEdgeService.saveEdgeIfNotExist(edge);

                    edges.add(edge);

                    SubPath subPath = SubPath.builder()
                            .src(src)
                            .dst(dst)
                            .fromName(edge.getSrc().getPointName())
                            .toName(edge.getDst().getPointName())
                            .sectionTime(subPathDto.getSectionTime())
                            .transitType(TransitType.WALK)
                            .build();

                    SubPath persistedSubPath = adminSubPathService.saveWithEdgeMapping(subPath, edges);

                    subPaths.set(i, persistedSubPath);
                }
            }

            FullPath fullPath = FullPath.builder()
                    .isSeen(true)
                    .startWaypoint(startPoint)
                    .totalTime(pathDto.getTime())
                    .build();

            adminFullPathService.saveFullPathMappingSubPath(fullPath, subPaths);
        }
    }

    private SubPath createFirstWalkSubPath(WaypointDto.PlaceDto startPlace, int firstSectionTime, PathDto.SubPathDto subPathDto) {
        List<Edge> edges = new ArrayList<>();
        Waypoint src, dst;
        src = adminWaypointService.saveIfNotExist(startPlace.toEntity());
        dst = adminWaypointService.saveIfNotExist(subPathDto.getStationList().get(0).toEntity());

        Edge edge = Edge.builder()
                .src(src)
                .dst(dst)
                .detailExist(false)
                .build();

        adminEdgeService.saveEdgeIfNotExist(edge);

        edges.add(edge);

        SubPath subPath = SubPath.builder()
                .src(src)
                .dst(dst)
                .fromName(edge.getSrc().getPointName())
                .toName(edge.getDst().getPointName())
                .sectionTime(firstSectionTime)
                .transitType(TransitType.WALK)
                .build();

        return adminSubPathService.saveWithEdgeMapping(subPath, edges);
    }

    @Transactional(readOnly = true)
    public List<WaypointDto.BusStationDto> findBusStations(String busNumber) {
        List<SubPath> subPaths = adminSubPathService.findAllByLineNameAndTransitType(busNumber, TransitType.BUS);
        Set<Waypoint> busStations = subPaths.stream()
                .map(SubPath::getEdgeInfos)
                .flatMap(Collection::stream)
                .map(subPathAndEdge -> {
                    Edge edge = subPathAndEdge.getEdge();
                    return edge.getSrc();
                })
                .collect(Collectors.toSet());

        subPaths.forEach(subPath -> busStations.add(subPath.getDst()));

        return busStations.stream().map(busStation -> (WaypointDto.BusStationDto) busStation.toDto()).collect(Collectors.toList());
    }

    public void updateRouteSeen(Long id) {
        FullPath fullPath = transitRepository.findRouteById(id);

        fullPath.updateIsSeen();
    }

    @Transactional
    public void createBusStationInfo(ExposedBusStationDto exposedBusStationDto) {
        adminWaypointService.findAllById(exposedBusStationDto.getBusStationIds())
                .forEach(waypoint -> {
                    BusStation busStation = (BusStation) waypoint;
                    busStationInfoRepository.save(BusStationInfo.builder()
                            .busName(exposedBusStationDto.getBusName())
                            .stationName(busStation.getStationName())
                            .stationId(busStation.getLocalStationId())
                            .x(busStation.getX())
                            .y(busStation.getY())
                            .build());
                });
    }
}
