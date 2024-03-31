package smu.poodle.smnavi.map.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import smu.poodle.smnavi.common.dto.BaseResponse;
import smu.poodle.smnavi.map.callapi.OdsayTransitRouteApi;
import smu.poodle.smnavi.map.service.PathService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PathManageController {
    private final OdsayTransitRouteApi odsayTransitRouteApi;
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

        return BaseResponse.ok();
    }
}
