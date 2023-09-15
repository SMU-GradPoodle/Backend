package smu.poodle.smnavi.map.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.poodle.smnavi.map.domain.data.TransitType;
import smu.poodle.smnavi.map.domain.path.FullPath;
import smu.poodle.smnavi.map.domain.station.Place;
import smu.poodle.smnavi.map.domain.station.Waypoint;
import smu.poodle.smnavi.map.dto.AbstractWaypointDto;
import smu.poodle.smnavi.map.dto.PathDto;
import smu.poodle.smnavi.map.dto.RouteDto;
import smu.poodle.smnavi.map.dto.WaypointDto;
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

    public PathDto.Info get7016Route() {

        FullPath fullPath = fullPathRepository.findFullPathById(16L);

        PathDto.Info pathDto = PathDto.Info.fromEntity(fullPath);

        List<PathDto.SubPathDto> subPathList = pathDto.getSubPathList();
        subPathList.removeIf(subPathDto -> subPathDto.getTransitType().equals(TransitType.WALK));

        return pathDto;
    }
}
