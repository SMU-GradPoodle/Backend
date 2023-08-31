package smu.poodle.smnavi.tipoff.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smu.poodle.smnavi.common.dto.BaseResponse;
import smu.poodle.smnavi.common.dto.PageResult;
import smu.poodle.smnavi.tipoff.domain.TipOff;
import smu.poodle.smnavi.tipoff.dto.LocationDto;
import smu.poodle.smnavi.tipoff.dto.TipOffRequestDto;
import smu.poodle.smnavi.tipoff.dto.TipOffResponseDto;
import smu.poodle.smnavi.tipoff.service.TipOffService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class TipOffController {
    private final TipOffService tipOffService;

    @PostMapping("/api/info") //글 작성
    public ResponseEntity<Object> addInfo(@RequestBody @Valid TipOffRequestDto tipOffRequestDto) {
        tipOffService.addInfo(tipOffRequestDto);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/info/{id}") //제보 id별로 조회
    public ResponseEntity<?> getInfoById(@PathVariable(value = "id") Long id) {
        Optional<TipOffRequestDto> infoDtoId = tipOffService.getInfoById(id);
        if (infoDtoId.isPresent()) {
            tipOffService.increaseViews(id);
            return ResponseEntity.ok().body(infoDtoId.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/info/button")
    public ResponseEntity<?> buttons() {
        List<LocationDto> locationDto1 = tipOffService.getBusLocationList();
        return ResponseEntity.ok().body(locationDto1);
    }

    @PutMapping("/api/info/{id}") //수정
    public ResponseEntity<?> updateInfo(@PathVariable(value = "id") Long id, @RequestBody TipOffRequestDto tipOffRequestDto) {
        Optional<TipOff> updateInfo = tipOffService.updateInfo(id, tipOffRequestDto);
        if (updateInfo.isPresent()) {
            return ResponseEntity.ok().body(updateInfo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/info") //제보 전체 조회
    public BaseResponse<PageResult<TipOffResponseDto.Detail>> getTipOffList(@RequestParam(required = false) String keyword,
                                                                     Pageable pageable) {

        return BaseResponse.ok(tipOffService.getTipOffList(keyword, pageable));
    }

    @DeleteMapping("/api/info/{id}") //제보 글 삭제
    public ResponseEntity<?> deleteInfoId(@PathVariable(value = "id") Long id) {
        Long deleteinfo = tipOffService.deleteInfoId(id);
        if (deleteinfo == null) {
            return ResponseEntity.ok().body(deleteinfo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
