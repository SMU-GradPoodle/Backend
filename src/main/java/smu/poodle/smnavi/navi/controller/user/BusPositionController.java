package smu.poodle.smnavi.navi.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.poodle.smnavi.common.response.BaseResponse;
import smu.poodle.smnavi.navi.dto.BusArriveInfoDto;
import smu.poodle.smnavi.navi.redisdomain.BusPosition;
import smu.poodle.smnavi.navi.service.user.BusPositionService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BusPositionController {

    private final BusPositionService busPositionService;

    @GetMapping("/bus-position")
    public BaseResponse<List<BusPosition>> getBusPosition() {
        return BaseResponse.ok(busPositionService.getBusPositionList());
    }

    @GetMapping("/bus-station-info")
    public BaseResponse<List<BusArriveInfoDto>> getBusArriveInfo() {
        return BaseResponse.ok(busPositionService.getBusArriveInfo());
    }
}
