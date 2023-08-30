package smu.poodle.smnavi.user.sevice;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.poodle.smnavi.errorcode.CommonErrorCode;
import smu.poodle.smnavi.exception.RestApiException;
import smu.poodle.smnavi.user.auth.Authority;
import smu.poodle.smnavi.user.domain.JwtRefreshToken;
import smu.poodle.smnavi.user.domain.UserEntity;
import smu.poodle.smnavi.user.dto.LoginRequestDto;
import smu.poodle.smnavi.user.dto.TokenResponseDto;
import smu.poodle.smnavi.user.jwt.TokenProvider;
import smu.poodle.smnavi.user.jwt.TokenType;
import smu.poodle.smnavi.user.repository.JwtRefreshTokenRepository;
import smu.poodle.smnavi.user.repository.UserRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final LoginService loginService;
    private final UserRepository userRepository;
    private final JwtRefreshTokenRepository jwtRefreshTokenRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;


    @Transactional
    public UserEntity signup(LoginRequestDto loginRequestDto) {
        userRepository.findByEmail(loginRequestDto.getEmail()).ifPresent(user -> {
            throw new RestApiException(CommonErrorCode.INVALID_PARAMETER);
        });
        UserEntity user = UserEntity.builder()
                .email(loginRequestDto.getEmail())
                .password(passwordEncoder.encode(loginRequestDto.getPassword()))
                .authority(Authority.ROLE_USER)
                .build();

        return userRepository.save(user);

    }

    @Transactional
    public TokenResponseDto login(HttpServletResponse response, LoginRequestDto loginRequestDto) {
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequestDto.getEmail(), loginRequestDto.getPassword()
        );

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenResponseDto accessToken = tokenProvider.generateTokenResponse(TokenType.ACCESS_TOKEN, authentication);

        TokenResponseDto refreshToken = tokenProvider.generateTokenResponse(TokenType.REFRESH_TOKEN, authentication);
        createOrUpdateRefreshToken(refreshToken);

        setRefreshTokenCookie(response, refreshToken);

        return accessToken;
    }

    private void createOrUpdateRefreshToken(TokenResponseDto refreshTokenDto) {
        Long userId = loginService.getLoginMemberId();

        JwtRefreshToken refreshToken = jwtRefreshTokenRepository.findByUserId(userId)
                .orElse(JwtRefreshToken.builder()
                        .user(UserEntity.builder().id(userId).build())
                        .build());

        refreshToken.updateRefreshToken(refreshTokenDto);
        jwtRefreshTokenRepository.save(refreshToken);
    }

    private void setRefreshTokenCookie(HttpServletResponse response, TokenResponseDto refreshTokenDto) {

        String cookieValue = "Bearer " + refreshTokenDto.getToken();
        cookieValue = URLEncoder.encode(cookieValue, StandardCharsets.UTF_8);

        Cookie cookie = new Cookie(TokenType.REFRESH_TOKEN.getHeader(), cookieValue);
        cookie.setPath("/");
//        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) refreshTokenDto.getExpiresIn());
        response.addCookie(cookie);
    }


    @Transactional
    public void logout(HttpServletRequest request) {
        String refreshToken = tokenProvider.getRefreshToken(request);
        JwtRefreshToken jwtRefreshToken = jwtRefreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() ->
                        new RestApiException(CommonErrorCode.REFRESH_TOKEN_NOT_EXIST));

        jwtRefreshTokenRepository.delete(jwtRefreshToken);
    }

    @Transactional(readOnly = true)
    public TokenResponseDto refreshAccessToken(HttpServletRequest request) {
        String refreshToken = tokenProvider.getRefreshToken(request);
        tokenProvider.validateToken(TokenType.REFRESH_TOKEN, refreshToken);

        UserEntity user = userRepository.findByRefreshToken(refreshToken).orElseThrow(() ->
                new RestApiException(CommonErrorCode.INVALID_TOKEN)
        );

        return tokenProvider.generateTokenResponse(TokenType.ACCESS_TOKEN, user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() ->
                new RestApiException(CommonErrorCode.INVALID_MAIL_OR_PASSWORD));

        return User.builder()
                .username(user.getId().toString())
                .password(user.getPassword())
                .authorities(user.getGrantedAuthority())
                .build();
    }
}
