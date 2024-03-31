package smu.poodle.smnavi.navi.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import smu.poodle.smnavi.common.response.BaseResponse;
import smu.poodle.smnavi.navi.dto.ExposedBusStationDto;
import smu.poodle.smnavi.navi.dto.WaypointDto;
import smu.poodle.smnavi.navi.externapi.OdsayTransitRouteApi;
import smu.poodle.smnavi.navi.service.user.PathService;
import smu.poodle.smnavi.navi.service.admin.AdminNaviService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN')")
public class AdminNaviController {
    private final OdsayTransitRouteApi odsayTransitRouteApi;
    private final AdminNaviService adminNaviService;
    private final PathService pathService;

    @PostMapping("/api/map/transit")
    public BaseResponse<Void> savePath(
            @RequestParam String startPlaceName,
            @RequestParam String startX,
            @RequestParam String startY,
            @RequestParam List<Integer> indexes) {

        odsayTransitRouteApi.callApiAndSavePathIfNotExist(startPlaceName, startX, startY, indexes);

        return BaseResponse.ok();
    }

    @PostMapping("/api/route/seen/{id}")
    public BaseResponse<Void> getRouteList(@PathVariable Long id) {
        pathService.updateRouteSeen(id);

        return BaseResponse.created();
    }

    @GetMapping("/api/station/{busNumber}")
    public BaseResponse<List<WaypointDto.BusStationDto>> findBusStations(@PathVariable String busNumber) {
        return BaseResponse.ok(adminNaviService.findBusStations(busNumber));
    }

    @PostMapping("/api/station")
    public BaseResponse<Void> createBusStation(@RequestBody ExposedBusStationDto exposedBusStationDto) {
        adminNaviService.createBusStationInfo(exposedBusStationDto);
        return BaseResponse.created();
    }
}
