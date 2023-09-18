package smu.poodle.smnavi.map.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.poodle.smnavi.map.domain.mapping.FullPathAndSubPath;
import smu.poodle.smnavi.map.domain.mapping.SubPathAndEdge;
import smu.poodle.smnavi.map.domain.path.FullPath;
import smu.poodle.smnavi.map.domain.station.Waypoint;
import smu.poodle.smnavi.map.dto.AbstractWaypointDto;
import smu.poodle.smnavi.map.dto.PathDto;
import smu.poodle.smnavi.map.callapi.GpsPoint;
import smu.poodle.smnavi.map.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PathService {

    private final TransitRepository transitRepository;
    private final FullPathRepository fullPathRepository;
    private final PlaceRepository placeRepository;


    public List<PathDto.Info> getPathByStartPlace(Long startPlaceId) {
        List<PathDto.Info> pathInfoList = new ArrayList<>();

        List<FullPath> fullPaths = fullPathRepository.findByStartPlaceId(startPlaceId);

        for (FullPath fullPath : fullPaths) {
            PathDto.Info pathDto = PathDto.Info.fromEntity(fullPath);
            pathInfoList.add(pathDto);
        }

        return pathInfoList;
    }

    public List<AbstractWaypointDto> getRouteList() {
        //todo: fullpath가 가지고 있는 startstation의 목록을 반환해주는 쿼리를 작성해보기
        return placeRepository.findAllStartPlace().stream().map(Waypoint::toDto).toList();
    }

    public void updateRouteSeen(Long id) {
        FullPath fullPath = transitRepository.findRouteById(id);

        fullPath.updateIsSeen();
    }

    public List<GpsPoint> get7016Route() {

        FullPath fullPath = fullPathRepository.findFullPathById(16L);

        List<GpsPoint> gpsPointList = new ArrayList<>();

        List<FullPathAndSubPath> subPaths = fullPath.getSubPaths();

        for (FullPathAndSubPath fullPathAndSubPath : subPaths) {
            List<SubPathAndEdge> edgeInfos = fullPathAndSubPath.getSubPath().getEdgeInfos();
            for (SubPathAndEdge edgeInfo : edgeInfos) {
                gpsPointList.addAll(edgeInfo.getEdge().getDetailPositionList().stream().map((detailPosition -> {
                    return new GpsPoint(detailPosition.getX(), detailPosition.getY());
                })).toList());
            }
        }

        return gpsPointList;
    }
}
