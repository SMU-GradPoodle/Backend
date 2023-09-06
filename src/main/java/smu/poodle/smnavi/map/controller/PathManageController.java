package smu.poodle.smnavi.map.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import smu.poodle.smnavi.map.dto.PathDto;
import smu.poodle.smnavi.map.externapi.odsay.TransitRouteApi;
import smu.poodle.smnavi.map.response.BaseResponse;
import smu.poodle.smnavi.map.response.TransitResponse;
import smu.poodle.smnavi.map.service.PathService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PathManageController {
    private final TransitRouteApi transitRouteApi;

    private final PathService pathService;

    //odsay api 호출해서 api 만드는 요청
    @PostMapping("/api/map/transit")
    public ResponseEntity<BaseResponse> savePath(
            @RequestParam String startPlaceName,
            @RequestParam String startX,
            @RequestParam String startY,
            @RequestParam List<Integer> indexes){

        List<PathDto.Info> transitRoute =
                transitRouteApi.callApiAndSavePathIfNotExist(startPlaceName, startX, startY, indexes);

        TransitResponse transitResponse = TransitResponse.builder()
                .message("정상적으로 경로를 불러왔습니다.")
                .pathInfoList(transitRoute)
                .build();

        return new ResponseEntity<>(transitResponse, HttpStatus.OK);
    }

    @PostMapping("/api/route/seen/{id}")
    public ResponseEntity<HttpStatus> getRouteList(@PathVariable Long id){
        pathService.updateRouteSeen(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
