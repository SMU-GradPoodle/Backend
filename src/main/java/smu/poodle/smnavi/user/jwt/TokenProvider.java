package smu.poodle.smnavi.user.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import smu.poodle.smnavi.common.errorcode.CommonErrorCode;
import smu.poodle.smnavi.common.exception.RestApiException;
import smu.poodle.smnavi.user.domain.UserEntity;

import java.security.Key;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static smu.poodle.smnavi.user.jwt.TokenType.*;


@Component
public class TokenProvider {
    private static final String TOKEN_TYPE = "Bearer";
    private static final String AUTHORITY_KEY = "auth";
    @Value("${JWT_ACCESS_TOKEN_SECRET_KEY}")
    private String accessTokenSecretKey;

    @Value("${JWT_REFRESH_TOKEN_SECRET_KEY}")
    private String refreshTokenSecretKey;
    private Key accessTokenKey;
    private Key refershTokenKey;

    @PostConstruct
    public void init() {
        accessTokenKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessTokenSecretKey));
        refershTokenKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshTokenSecretKey));
    }

    public String createAccessTokenByRefreshToken(String refreshToken) {
        Claims claims = parseTokenClaims(REFRESH_TOKEN, refreshToken);
        String userId = claims.getSubject();
        String authority = claims.get(AUTHORITY_KEY).toString();
        return createToken(REFRESH_TOKEN, Long.parseLong(userId), authority);
    }

    public String createToken(TokenType tokenType, Long userId, String authority) {
        long nowMillisecond = new Date().getTime();

        return Jwts.builder()
                .setIssuer("poodle")
                .setSubject(userId.toString())
                .setExpiration(new Date(nowMillisecond + tokenType.getValidMillisecond()))
                .claim(AUTHORITY_KEY, authority)
                .signWith(getKey(tokenType), SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication createAuthenticationByAccessToken(String accessToken) {
        Claims claims = parseClaims(accessToken, accessTokenKey);

        if (ObjectUtils.isEmpty(claims.get(AUTHORITY_KEY))) {
            throw new RestApiException(CommonErrorCode.INVALID_TOKEN);
        }

        Collection<? extends GrantedAuthority> authority = Collections.singleton(new SimpleGrantedAuthority(claims.get(AUTHORITY_KEY).toString()));
        return new UsernamePasswordAuthenticationToken(claims.getSubject(), "", authority);
    }

    public void validateToken(TokenType tokenType, String token) {
        parseTokenClaims(tokenType, token);
    }

    public long getExpirationSeconds(TokenType tokenType, String token) {
        Claims claims = parseTokenClaims(tokenType, token);
        long expirationTime = claims.getExpiration().getTime();
        return (expirationTime - System.currentTimeMillis()) / 1000;
    }

    private Claims parseTokenClaims(TokenType tokenType, String token) {
        return parseClaims(token, getKey(tokenType));
    }

    private Claims parseClaims(String token, Key key) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new RestApiException(CommonErrorCode.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new RestApiException(CommonErrorCode.INVALID_TOKEN);
        }
    }

    public String getAccessToken(HttpServletRequest request) {
        String token = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION)).orElseThrow(() ->
                new RestApiException(CommonErrorCode.LOGIN_REQUIRED)
        );

        if (!StringUtils.hasText(token) || !StringUtils.startsWithIgnoreCase(token, TOKEN_TYPE)) {
            throw new RestApiException(CommonErrorCode.INVALID_TOKEN);
        }

        return token.substring(7);
    }

    public String getRefreshToken(HttpServletRequest request) {
        String token = getCookieByName(request, REFRESH_TOKEN.getHeader()).orElseThrow(() ->
                new RestApiException(CommonErrorCode.REFRESH_TOKEN_NOT_EXIST)
        );

        if (!StringUtils.hasText(token) || !StringUtils.startsWithIgnoreCase(token, TOKEN_TYPE)) {
            throw new RestApiException(CommonErrorCode.INVALID_TOKEN);
        }

        return token.substring(7);
    }

    private Optional<String> getCookieByName(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    private Key getKey(TokenType tokenType) {
        if (tokenType == ACCESS_TOKEN) {
            return accessTokenKey;
        } else if (tokenType == REFRESH_TOKEN) {
            return refershTokenKey;
        }
        return null;
    }
}
