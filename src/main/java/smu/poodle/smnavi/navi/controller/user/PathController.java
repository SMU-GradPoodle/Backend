package smu.poodle.smnavi.navi.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.poodle.smnavi.common.response.BaseResponse;
import smu.poodle.smnavi.navi.dto.AbstractWaypointDto;
import smu.poodle.smnavi.navi.dto.PathDto;
import smu.poodle.smnavi.navi.dto.GpsPointDto;
import smu.poodle.smnavi.navi.service.user.PathService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PathController {

    private final PathService pathService;

    /**
     * startStationId 를 통해 경로를 불러오는 api
     */
    @GetMapping("/path/{startPlaceId}")
    public BaseResponse<List<PathDto.Info>> getRoute(@PathVariable Long startPlaceId) {

        List<PathDto.Info> transitRoute = pathService.getPathDetail(startPlaceId);


        return BaseResponse.ok(transitRoute);
    }

    /**
     * 정해진 시작 지점의 아이디와 정류장이름을 모두 반환하는 API
     */
    @GetMapping("/waypoints")
    public ResponseEntity<List<AbstractWaypointDto>> getExposedWayPointList() {
        return new ResponseEntity<>(pathService.getRouteList(), HttpStatus.OK);
    }

    @GetMapping("/bus-info/path/7016")
    public BaseResponse<List<GpsPointDto>> get7016Route() {
        return BaseResponse.ok(pathService.get7016Route());
    }
}


