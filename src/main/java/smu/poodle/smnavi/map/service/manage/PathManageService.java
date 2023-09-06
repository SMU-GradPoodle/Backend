package smu.poodle.smnavi.map.service.manage;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smu.poodle.smnavi.map.domain.data.BusType;
import smu.poodle.smnavi.map.domain.data.TransitType;
import smu.poodle.smnavi.map.domain.path.Edge;
import smu.poodle.smnavi.map.domain.path.FullPath;
import smu.poodle.smnavi.map.domain.path.SubPath;
import smu.poodle.smnavi.map.domain.station.Waypoint;
import smu.poodle.smnavi.map.dto.PathDto;
import smu.poodle.smnavi.map.dto.WaypointDto;
import smu.poodle.smnavi.map.externapi.odsay.RouteDetailPositionApi;
import smu.poodle.smnavi.map.service.EdgeService;
import smu.poodle.smnavi.map.service.FullPathService;
import smu.poodle.smnavi.map.service.SubPathService;
import smu.poodle.smnavi.map.service.WaypointService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PathManageService {
    private final WaypointService waypointService;
    private final EdgeService edgeService;
    private final SubPathService subPathService;
    private final FullPathService fullPathService;
    private final RouteDetailPositionApi routeDetailPositionApi;

    @Transactional
    public void savePath(WaypointDto.PlaceDto startPlace, PathDto.Info pathDto) {
        Waypoint startPoint = waypointService.getAndSaveIfNotExist(startPlace.toEntity());

        List<SubPath> subPaths = new ArrayList<>(pathDto.getSubPathList().size());

        List<String> mapObjArr = Arrays.stream(pathDto.getMapObj().split("@")).collect(Collectors.toList());

        //todo : 진짜 거지같은 코드를 짜놨네..
        for (int i = 0; i < pathDto.getSubPathList().size(); i++) {
            subPaths.add(SubPath.builder().build());
        }

        int firstSectionTime = pathDto.getSubPathList().get(0).getSectionTime();
        subPaths.set(0, createFirstWalkSubPath(startPlace, firstSectionTime, pathDto.getSubPathList().get(1)));

        for (int i = 1; i < pathDto.getSubPathList().size(); i++) {
            PathDto.SubPathDto subPathDto = pathDto.getSubPathList().get(i);

            if (subPathDto.getTransitType() == TransitType.WALK)
                continue;

            List<Waypoint> persistedWaypointList = waypointService.saveStationListIfNotExist(subPathDto.getStationList());
            Waypoint subPathSrc = persistedWaypointList.get(0);
            Waypoint subPathDst = persistedWaypointList.get(persistedWaypointList.size() - 1);


            List<Edge> persistedEdgeList = edgeService.makeAndSaveEdgeIfNotExist(persistedWaypointList);

            //엣지의 디테일 루트 만들기
            routeDetailPositionApi.callApiForSaveDetailPositionList(subPathDto,
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

            SubPath persistedSubPath = subPathService.saveWithEdgeMapping(subPath, persistedEdgeList);

            subPaths.set(i, persistedSubPath);
        }

        for (int i = 1; i < pathDto.getSubPathList().size(); i++) {
            PathDto.SubPathDto subPathDto = pathDto.getSubPathList().get(i);

            if (subPathDto.getTransitType() == TransitType.WALK) {
                List<Edge> edges = new ArrayList<>();
                // 0번째는 WALK 일 수 없도록 처리하였음
                Waypoint src, dst;
                src = subPaths.get(i - 1).getDst();

                //마지막 서브패스는 무조건 걷는 것이라고 가정
                if (i == pathDto.getSubPathList().size() - 1) {
                    //todo: 상명대 위치 픽스하기
                    dst = waypointService.getSmuWayPoint();
                } else {
                    dst = subPaths.get(i + 1).getSrc();
                }

                Edge edge = Edge.builder()
                        .src(src)
                        .dst(dst)
                        .detailExist(false)
                        .build();

                edgeService.saveEdgeIfNotExist(edge);

                edges.add(edge);

                SubPath subPath = SubPath.builder()
                        .src(src)
                        .dst(dst)
                        .fromName(edge.getSrc().getPointName())
                        .toName(edge.getDst().getPointName())
                        .sectionTime(subPathDto.getSectionTime())
                        .transitType(TransitType.WALK)
                        .build();

                SubPath persistedSubPath = subPathService.saveWithEdgeMapping(subPath, edges);

                subPaths.set(i, persistedSubPath);
            }
        }

        FullPath fullPath = FullPath.builder()
                .isSeen(true)
                .startWaypoint(startPoint)
                .totalTime(pathDto.getTime())
                .build();

        FullPath persistedFullPath = fullPathService.saveFullPathMappingSubPath(fullPath, subPaths);
    }

    private SubPath createFirstWalkSubPath(WaypointDto.PlaceDto startPlace, int firstSectionTime, PathDto.SubPathDto subPathDto) {
        List<Edge> edges = new ArrayList<>();
        // 0번째는 WALK 일 수 없도록 처리하였음
        Waypoint src, dst;
        src = waypointService.getAndSaveIfNotExist(startPlace.toEntity());
        dst = waypointService.getAndSaveIfNotExist(subPathDto.getStationList().get(0).toEntity());

        Edge edge = Edge.builder()
                .src(src)
                .dst(dst)
                .detailExist(false)
                .build();

        edgeService.saveEdgeIfNotExist(edge);

        edges.add(edge);

        SubPath subPath = SubPath.builder()
                .src(src)
                .dst(dst)
                .fromName(edge.getSrc().getPointName())
                .toName(edge.getDst().getPointName())
                .sectionTime(firstSectionTime)
                .transitType(TransitType.WALK)
                .build();

        return subPathService.saveWithEdgeMapping(subPath, edges);
    }
}
