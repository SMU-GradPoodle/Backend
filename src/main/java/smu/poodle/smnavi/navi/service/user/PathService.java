package smu.poodle.smnavi.navi.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.poodle.smnavi.navi.domain.mapping.FullPathAndSubPath;
import smu.poodle.smnavi.navi.domain.mapping.SubPathAndEdge;
import smu.poodle.smnavi.navi.domain.path.FullPath;
import smu.poodle.smnavi.navi.domain.station.Waypoint;
import smu.poodle.smnavi.navi.dto.AbstractWaypointDto;
import smu.poodle.smnavi.navi.dto.PathDto;
import smu.poodle.smnavi.navi.dto.GpsPointDto;
import smu.poodle.smnavi.navi.repository.*;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PathService {

    private final TransitRepository transitRepository;
    private final FullPathRepository fullPathRepository;
    private final PlaceRepository placeRepository;


    public List<PathDto.Info> getPathDetail(Long startPlaceId) {
        List<PathDto.Info> pathInfoList = new ArrayList<>();

        List<FullPath> fullPaths = fullPathRepository.findByStartPlaceId(startPlaceId);

        for (FullPath fullPath : fullPaths) {
            PathDto.Info pathDto = PathDto.Info.fromEntity(fullPath);
            pathInfoList.add(pathDto);
        }

        return pathInfoList;
    }

    public List<AbstractWaypointDto> getRouteList() {
        return placeRepository.findAllStartPlace().stream().map(Waypoint::toDto).toList();
    }

    public void updateRouteSeen(Long id) {
        FullPath fullPath = transitRepository.findRouteById(id);

        fullPath.updateIsSeen();
    }

    public List<GpsPointDto> get7016Route() {

        FullPath fullPath = fullPathRepository.findFullPathById(16L);

        List<GpsPointDto> gpsPointDtoList = new ArrayList<>();

        List<FullPathAndSubPath> subPaths = fullPath.getSubPaths();

        for (FullPathAndSubPath fullPathAndSubPath : subPaths) {
            List<SubPathAndEdge> edgeInfos = fullPathAndSubPath.getSubPath().getEdgeInfos();
            for (SubPathAndEdge edgeInfo : edgeInfos) {
                gpsPointDtoList.addAll(edgeInfo.getEdge().getDetailPositionList().stream().map((detailPosition -> {
                    return new GpsPointDto(detailPosition.getX(), detailPosition.getY());
                })).toList());
            }
        }

        return gpsPointDtoList;
    }
}
