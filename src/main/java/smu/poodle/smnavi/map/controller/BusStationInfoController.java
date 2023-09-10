package smu.poodle.smnavi.map.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.poodle.smnavi.map.response.BaseResponse;
import smu.poodle.smnavi.map.response.BusStationInfoResponse;
import smu.poodle.smnavi.map.service.BusStationInfoService;

@RestController
@RequiredArgsConstructor
public class BusStationInfoController {
    private final BusStationInfoService busStationInfoService;

    @GetMapping("/api/map/busStationInfo")
    public ResponseEntity<BaseResponse> getAllBusStationInfo(){
        return new ResponseEntity<>(
                BusStationInfoResponse.builder()
                        .message("성공")
                        .data(busStationInfoService.getAllBusStationInfo())
                        .build(),
                HttpStatus.OK
        );
    }
}
