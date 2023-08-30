package smu.poodle.smnavi.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.poodle.smnavi.user.domain.JwtRefreshToken;

import java.util.Optional;

public interface JwtRefreshTokenRepository extends JpaRepository<JwtRefreshToken, Long> {
    Optional<JwtRefreshToken> findByUserId(Long userId);
    Optional<JwtRefreshToken> findByRefreshToken(String refreshToken);
}
