package smu.poodle.smnavi.map.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.poodle.smnavi.common.dto.BaseResponse;
import smu.poodle.smnavi.map.dto.BusArriveInfoDto;
import smu.poodle.smnavi.map.dto.TestBusPositionDto;
import smu.poodle.smnavi.map.externapi.redis.domain.BusPosition;
import smu.poodle.smnavi.map.service.BusPositionService;

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

    @GetMapping("/test/bus-position")
    public BaseResponse<List<TestBusPositionDto>> getTestBusPosition() {
        return BaseResponse.ok(busPositionService.getTestBusPosition());
    }

    @GetMapping("/bus-station-info")
    public BaseResponse<List<BusArriveInfoDto>> getBusArriveInfo() {
        return BaseResponse.ok(busPositionService.getBusArriveInfo());
    }

}
