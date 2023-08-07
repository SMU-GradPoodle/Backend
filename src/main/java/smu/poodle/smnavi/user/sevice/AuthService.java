package smu.poodle.smnavi.user.sevice;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.poodle.smnavi.errorcode.CommonErrorCode;
import smu.poodle.smnavi.exception.RestApiException;
import smu.poodle.smnavi.user.auth.Authority;
import smu.poodle.smnavi.user.auth.CustomUserDetail;
import smu.poodle.smnavi.user.domain.JwtRefreshToken;
import smu.poodle.smnavi.user.domain.UserEntity;
import smu.poodle.smnavi.user.dto.LoginRequestDto;
import smu.poodle.smnavi.user.dto.TokenResponseDto;
import smu.poodle.smnavi.user.jwt.TokenProvider;
import smu.poodle.smnavi.user.repository.JwtRefreshTokenRepository;
import smu.poodle.smnavi.user.repository.UserRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
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
    public TokenResponseDto.AccessToken login(HttpServletResponse response, LoginRequestDto loginRequestDto) {
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequestDto.getEmail(), loginRequestDto.getPassword()
        );

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenResponseDto.FullInfo fullTokenInfo = tokenProvider.generateTokenResponse(authentication);

        Long memberId = Long.parseLong(authentication.getName());
        createOrUpdateRefreshToken(memberId, fullTokenInfo);

        setRefreshTokenCookie(response, fullTokenInfo.getRefreshToken());

        return TokenResponseDto.AccessToken.of(fullTokenInfo);
    }

    private void createOrUpdateRefreshToken(Long userId, TokenResponseDto.FullInfo tokenResponseDto) {
        JwtRefreshToken refreshToken = jwtRefreshTokenRepository.findByUserId(userId)
                .orElse(JwtRefreshToken.builder().
                        user(UserEntity.builder().id(userId).build())
                        .build());

        refreshToken.updateRefreshToken(tokenResponseDto);
        jwtRefreshTokenRepository.save(refreshToken);
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {

        refreshToken = "Bearer " + refreshToken;
        refreshToken = URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);

        Cookie cookie = new Cookie("REFRESH_TOKEN", refreshToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setDomain("localhost");
        cookie.setMaxAge(24 * 60 * 60);
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
    public TokenResponseDto.AccessToken refreshAccessToken(HttpServletRequest request) {
        String refreshToken = tokenProvider.getRefreshToken(request);
        tokenProvider.validateAccessToken(refreshToken);

        UserEntity user = userRepository.findByRefreshToken(refreshToken).orElseThrow(() ->
                new RestApiException(CommonErrorCode.INVALID_TOKEN)
        );

        return tokenProvider.generateTokenResponse(user);
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() ->
                new RestApiException(CommonErrorCode.INVALID_MAIL_OR_PASSWORD));

        return new CustomUserDetail(user);
    }
}
