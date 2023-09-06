package smu.poodle.smnavi.map.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.poodle.smnavi.common.dto.BaseResponse;
import smu.poodle.smnavi.map.redis.domain.BusPosition;
import smu.poodle.smnavi.map.service.BusPositionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BusPositionController {

    private final BusPositionService busPositionService;

    @GetMapping("/api/bus-position")
    public BaseResponse<List<BusPosition>> getBusPosition() {
        return BaseResponse.ok(busPositionService.getBusPositionList());
    }
}
