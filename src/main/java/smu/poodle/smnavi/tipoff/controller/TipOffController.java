package smu.poodle.smnavi.tipoff.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import smu.poodle.smnavi.common.response.BaseResponse;
import smu.poodle.smnavi.common.response.PageResult;
import smu.poodle.smnavi.tipoff.dto.LocationDto;
import smu.poodle.smnavi.tipoff.dto.TipOffRequestDto;
import smu.poodle.smnavi.tipoff.dto.TipOffResponseDto;
import smu.poodle.smnavi.tipoff.service.TipOffService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TipOffController {
    private final TipOffService tipOffService;

    /**
     * 제보글 작성
     */
    @PostMapping("/api/info")
    public BaseResponse<TipOffResponseDto.Simple> registerTipOff(@RequestBody @Valid TipOffRequestDto tipOffRequestDto) {

        return BaseResponse.ok(tipOffService.registerTipOff(tipOffRequestDto));
    }

    /**
     * 제보글 목록 조회
     */
    @GetMapping("/api/info") //제보 전체 조회
    public BaseResponse<PageResult<TipOffResponseDto.Detail>> getTipOffList(
            @RequestParam Boolean isMine,
            @PageableDefault(size = 7, sort = "createdAt", direction = Sort.Direction.DESC)  Pageable pageable) {
        return BaseResponse.ok(tipOffService.getTipOffList(isMine, pageable));
    }


    /**
     * 제보글 단건 조회
     */
    @GetMapping("/api/info/{id}")
    public BaseResponse<TipOffResponseDto.Detail> getTipOffById(@PathVariable(value = "id") Long id) {
        TipOffResponseDto.Detail infoDtoId = tipOffService.getTipOffById(id);
        return BaseResponse.ok(infoDtoId);
    }

    /**
     * 제보 글 버튼 조회 API
     */
    @GetMapping("/api/info/button")
    public BaseResponse<List<LocationDto>> getTipOffButton() {

        return BaseResponse.ok(tipOffService.getTipOffButton());
    }

    /**
     * 제보 글 수정
     */
    @PutMapping("/api/info/{id}")
    public BaseResponse<TipOffResponseDto.Simple> updateInfo(@PathVariable(value = "id") Long id, @RequestBody TipOffRequestDto tipOffRequestDto) {
        return BaseResponse.ok(tipOffService.updateInfo(id, tipOffRequestDto));
    }

    /**
     * 제보글 삭제
     */
    @DeleteMapping("/api/info/{id}")
    public BaseResponse<Void> deleteTipOff(@PathVariable(value = "id") Long id, @RequestBody TipOffRequestDto tipOffRequestDto) {
        tipOffService.deleteTipOff(id, tipOffRequestDto);
        return BaseResponse.ok();
    }
}
