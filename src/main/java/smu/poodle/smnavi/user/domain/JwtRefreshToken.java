package smu.poodle.smnavi.user.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import smu.poodle.smnavi.user.dto.TokenResponseDto;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "jwt_refresh_tokens")
public class JwtRefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    UserEntity user;
    String refreshToken;
    long expiredTime;

    public void updateRefreshToken(TokenResponseDto tokenResponseDto) {
        refreshToken = tokenResponseDto.getToken();
        expiredTime = tokenResponseDto.getExpiresIn();
    }
}
