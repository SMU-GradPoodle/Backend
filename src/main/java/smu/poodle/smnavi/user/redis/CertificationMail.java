package smu.poodle.smnavi.user.redis;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Setter
@Builder
@RedisHash("certification_mail")
public class CertificationMail {
    @Id
    String email;
    String certificationKey;
    Boolean isCertificate;
    @TimeToLive
    Long expiration;
}
