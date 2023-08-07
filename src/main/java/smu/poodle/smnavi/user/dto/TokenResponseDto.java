package smu.poodle.smnavi.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

public class TokenResponseDto {
    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class FullInfo {
        String accessToken;
        Long expiresIn;
        String refreshToken;
        Long refreshTokenExpiresIn;
    }
    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class AccessToken {
        String accessToken;
        Long expiresIn;
        public static AccessToken of(FullInfo fullInfo) {
            return AccessToken.builder()
                    .accessToken(fullInfo.getAccessToken())
                    .expiresIn(fullInfo.getExpiresIn())
                    .build();
        }
    }
}
