package smu.poodle.smnavi.user.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.poodle.smnavi.user.domain.UserEntity;
import smu.poodle.smnavi.user.dto.LoginRequestDto;
import smu.poodle.smnavi.user.dto.TokenResponseDto;
import smu.poodle.smnavi.user.sevice.AuthService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/api/user/signup")
    public UserEntity signup(@RequestBody LoginRequestDto loginRequestDto){
        return authService.signup(loginRequestDto);
    }


    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto.AccessToken> login(HttpServletResponse response, @RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authService.login(response, loginRequestDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto.AccessToken> refreshAccessToken(HttpServletRequest request) {
        return ResponseEntity.ok(authService.refreshAccessToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok().build();
    }
}
