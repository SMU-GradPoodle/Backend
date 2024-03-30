package smu.poodle.smnavi.user.redisdomain;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Setter
@Builder
@RedisHash("mail_authentication")
public class RefreshTokenCache {
    @Id
    String refreshToken;

    @TimeToLive
    Long expiration;
}
