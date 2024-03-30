package smu.poodle.smnavi.user.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.poodle.smnavi.common.dto.BaseResponse;
import smu.poodle.smnavi.user.dto.AuthRequestDto;
import smu.poodle.smnavi.user.dto.TokenResponseDto;
import smu.poodle.smnavi.user.sevice.TokenService;
import smu.poodle.smnavi.user.sevice.LoginService;
import smu.poodle.smnavi.user.sevice.SignupService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {
    private final SignupService signupService;
    private final LoginService loginService;
    private final TokenService tokenService;

    @PostMapping("/user/check-duplicate-nickname")
    public BaseResponse<Void> checkDuplicateNickname(@RequestBody @Valid AuthRequestDto.Nickname authRequestDto) {
        signupService.checkDuplicateNickname(authRequestDto);

        return BaseResponse.ok();
    }

    @PostMapping("/user/send-verification-mail")
    public BaseResponse<Void> sendVerificationMail(@RequestBody @Valid AuthRequestDto.Certification authRequestDto) {
        signupService.sendVerificationMail(authRequestDto);

        return BaseResponse.ok();
    }

    @PostMapping("/user/verification-main")
    public BaseResponse<Void> verifyMail(@RequestBody @Valid AuthRequestDto.Certification authRequestDto) {
        signupService.authenticateMail(authRequestDto);

        return BaseResponse.ok();
    }

    @PostMapping("/user/signup")
    public BaseResponse<Void> signup(@RequestBody AuthRequestDto.SignUp authRequestDto){
        signupService.signup(authRequestDto);
        return BaseResponse.ok();
    }

    @PostMapping("/user/login")
    public BaseResponse<TokenResponseDto> login(HttpServletResponse response, @RequestBody AuthRequestDto.Login authRequestDto) {
        return BaseResponse.ok(loginService.login(response, authRequestDto));
    }

    @PostMapping("/user/refresh")
    public BaseResponse<String> refreshAccessToken(HttpServletRequest request) {
        return BaseResponse.ok(tokenService.refreshAccessToken(request));
    }

    @PostMapping("/user/delete-refresh-token")
    public BaseResponse<Void> deleteRefreshToken(HttpServletRequest request) {
        tokenService.deleteRefreshToken(request);
        return BaseResponse.ok();
    }
}
