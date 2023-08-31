package smu.poodle.smnavi.user.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.poodle.smnavi.common.dto.BaseResponse;
import smu.poodle.smnavi.user.domain.UserEntity;
import smu.poodle.smnavi.user.dto.AuthRequestDto;
import smu.poodle.smnavi.user.dto.TokenResponseDto;
import smu.poodle.smnavi.user.sevice.AuthService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/user/chek-duplicate-nickname")
    public BaseResponse<Void> checkDuplicateNickname(@RequestBody @Valid AuthRequestDto.Nickname authRequestDto) {
        authService.checkDuplicateNickname(authRequestDto);

        return BaseResponse.ok();
    }


    @PostMapping("/user/send-certification-mail")
    public BaseResponse<Void> sendCertificationMail(@RequestBody @Valid AuthRequestDto.Certification authRequestDto) {
        authService.sendCertificationMail(authRequestDto);

        return BaseResponse.ok();
    }

    @PostMapping("/user/certification")
    public BaseResponse<Void> certificateMail(@RequestBody @Valid AuthRequestDto.Certification authRequestDto) {
        authService.certificateMail(authRequestDto);

        return BaseResponse.ok();
    }


    @PostMapping("/user/signup")
    public UserEntity signup(@RequestBody AuthRequestDto.SignUp authRequestDto){
        return authService.signup(authRequestDto);
    }


    @PostMapping("/user/login")
    public BaseResponse<TokenResponseDto> login(HttpServletResponse response, @RequestBody AuthRequestDto.Login authRequestDto) {
        return BaseResponse.ok(authService.login(response, authRequestDto));
    }

    @PostMapping("/user/refresh")
    public ResponseEntity<TokenResponseDto> refreshAccessToken(HttpServletRequest request) {
        return ResponseEntity.ok(authService.refreshAccessToken(request));
    }

    @PostMapping("/user/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok().build();
    }
}
