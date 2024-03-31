package smu.poodle.smnavi.tipoff.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.poodle.smnavi.common.dto.BaseResponse;
import smu.poodle.smnavi.tipoff.domain.ThumbStatus;
import smu.poodle.smnavi.tipoff.dto.LikeInfoDto;
import smu.poodle.smnavi.tipoff.service.ThumbService;


@RestController
@RequiredArgsConstructor
public class ThumbController {

    private final ThumbService thumbService;

    @PostMapping("/api/info/{id}/like")
    public BaseResponse<LikeInfoDto> doLike(@PathVariable("id") Long tipOffId) {
        return BaseResponse.ok(thumbService.doLikeOrHate(tipOffId, ThumbStatus.THUMBS_UP));
    }

    @PostMapping("/api/info/{id}/hate")
    public BaseResponse<LikeInfoDto> doHate(@PathVariable("id") Long tipOffId) {
        return BaseResponse.ok(thumbService.doLikeOrHate(tipOffId, ThumbStatus.THUMBS_DOWN));
    }
}
