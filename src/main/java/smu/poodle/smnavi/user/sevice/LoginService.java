package smu.poodle.smnavi.user.sevice;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import smu.poodle.smnavi.common.errorcode.CommonStatusCode;
import smu.poodle.smnavi.common.exception.RestApiException;
import smu.poodle.smnavi.user.domain.UserEntity;
import smu.poodle.smnavi.user.dto.AuthRequestDto;
import smu.poodle.smnavi.user.dto.TokenResponseDto;
import smu.poodle.smnavi.user.jwt.TokenProvider;
import smu.poodle.smnavi.user.redisdomain.RefreshTokenCache;
import smu.poodle.smnavi.user.redisrepository.RefreshTokenCacheRepository;
import smu.poodle.smnavi.user.repository.UserRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static smu.poodle.smnavi.user.exception.AuthExceptionCode.INVALID_MAIL_OR_PASSWORD;
import static smu.poodle.smnavi.user.jwt.TokenType.ACCESS_TOKEN;
import static smu.poodle.smnavi.user.jwt.TokenType.REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final TokenProvider tokenProvider;

    private final UserRepository userRepository;
    private final RefreshTokenCacheRepository refreshTokenCacheRepository;

    private final PasswordEncoder passwordEncoder;
    private final Environment env;

    public TokenResponseDto login(HttpServletResponse response, AuthRequestDto.Login authRequestDto) {
        UserEntity user = userRepository.findByEmail(authRequestDto.getEmail()).orElseThrow(() ->
                new RestApiException(INVALID_MAIL_OR_PASSWORD));

        if (!passwordEncoder.matches(authRequestDto.getPassword(), user.getPassword())) {
            throw new RestApiException(INVALID_MAIL_OR_PASSWORD);
        }

        String accessToken = tokenProvider.createToken(ACCESS_TOKEN, user.getId(), user.getAuthority().name());
        Authentication authentication = tokenProvider.createAuthenticationByAccessToken(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String refreshToken = tokenProvider.createToken(REFRESH_TOKEN, user.getId(), user.getAuthority().name());
        saveRefreshToken(refreshToken);
        setRefreshTokenCookie(response, refreshToken);

        return TokenResponseDto.builder()
                .tokenType(ACCESS_TOKEN)
                .token(accessToken)
                .build();
    }

    private void saveRefreshToken(String refreshToken) {
        refreshTokenCacheRepository.save(RefreshTokenCache.builder()
                .refreshToken(refreshToken)
                .expiration(tokenProvider.getExpirationSeconds(REFRESH_TOKEN, refreshToken))
                .build());
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        String cookieValue = "Bearer " + refreshToken;
        cookieValue = URLEncoder.encode(cookieValue, StandardCharsets.UTF_8);
        Cookie cookie = new Cookie(REFRESH_TOKEN.getHeader(), cookieValue);
        int expirationSeconds = (int) tokenProvider.getExpirationSeconds(REFRESH_TOKEN, refreshToken);
        cookie.setMaxAge(expirationSeconds);

        if (isProd()) {
            setRefreshTokenCookieWhenProd(response, cookie);
        }

        if (!isProd()) {
            setRefreshTokenCookieWhenLocal(response, cookie);
        }
    }

    private boolean isProd() {
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }

    private void setRefreshTokenCookieWhenProd(HttpServletResponse response, Cookie cookie) {
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setDomain("smnavi.me");
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    private void setRefreshTokenCookieWhenLocal(HttpServletResponse response, Cookie cookie) {
        cookie.setPath("/");
        cookie.setDomain("localhost");
        response.addCookie(cookie);
    }
}

