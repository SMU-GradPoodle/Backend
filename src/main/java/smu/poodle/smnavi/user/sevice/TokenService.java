package smu.poodle.smnavi.user.sevice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smu.poodle.smnavi.common.errorcode.CommonErrorCode;
import smu.poodle.smnavi.common.exception.RestApiException;
import smu.poodle.smnavi.user.jwt.TokenProvider;
import smu.poodle.smnavi.user.redisdomain.RefreshTokenCache;
import smu.poodle.smnavi.user.redisrepository.RefreshTokenCacheRepository;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final RefreshTokenCacheRepository refreshTokenCacheRepository;
    private final TokenProvider tokenProvider;

    public String refreshAccessToken(HttpServletRequest request) {
        String refreshToken = tokenProvider.getRefreshToken(request);

        refreshTokenCacheRepository.findById(refreshToken).orElseThrow(() ->
                new RestApiException(CommonErrorCode.INVALID_TOKEN)
        );

        return tokenProvider.createAccessTokenByRefreshToken(refreshToken);
    }

    public void deleteRefreshToken(HttpServletRequest request) {
        String refreshToken = tokenProvider.getRefreshToken(request);
        RefreshTokenCache refreshTokenCache = refreshTokenCacheRepository.findById(refreshToken)
                .orElseThrow(() -> new RestApiException(CommonErrorCode.REFRESH_TOKEN_NOT_EXIST));

        refreshTokenCacheRepository.delete(refreshTokenCache);
    }
}
