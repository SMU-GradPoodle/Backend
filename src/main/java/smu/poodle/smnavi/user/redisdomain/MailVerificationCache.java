package smu.poodle.smnavi.user.redisdomain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("mail_verification")
public class MailVerificationCache {
    @Id
    String email;
    String verificationKey;
    Boolean isVerify;
    @TimeToLive
    Long expiration;
}
