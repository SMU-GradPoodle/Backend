package smu.poodle.smnavi.tipoff.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import smu.poodle.smnavi.tipoff.dto.LikeHateDto;
import smu.poodle.smnavi.tipoff.service.LikeHateService;
import smu.poodle.smnavi.user.auth.CustomUserDetail;


@Controller
@RequiredArgsConstructor
public class LikeController {
    @Autowired
    private LikeHateService likeHateService;
    @PostMapping("/api/info/likehate")
    public ResponseEntity<?>LikeOrHate(@AuthenticationPrincipal CustomUserDetail user, @RequestBody @Valid LikeHateDto likeHateDto, Errors errors){
        if(errors.hasErrors()){
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        int identify = likeHateService.checkLikeOrHate(user.getUser(),likeHateDto.getBoardId(), likeHateDto.getIdentify());
        return ResponseEntity.ok().body(identify);
    }
    @GetMapping("/api/info/likehate")
    public ResponseEntity<?>LikeOrHateEtc(@AuthenticationPrincipal CustomUserDetail user, @RequestBody @Valid LikeHateDto likeHateDto,Errors errors){
        if(errors.hasErrors()){
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(likeHateService.countByBoard_IdAndIdentify(user.getUser(),likeHateDto.getBoardId()));
    }
}
