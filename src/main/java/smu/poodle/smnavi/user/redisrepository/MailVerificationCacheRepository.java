package smu.poodle.smnavi.user.redisrepository;

import org.springframework.data.repository.CrudRepository;
import smu.poodle.smnavi.user.redisdomain.MailVerificationCache;

public interface MailVerificationCacheRepository extends CrudRepository<MailVerificationCache, String> {
}
