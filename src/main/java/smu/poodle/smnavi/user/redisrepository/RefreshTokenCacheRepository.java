package smu.poodle.smnavi.user.redisrepository;

import org.springframework.data.repository.CrudRepository;
import smu.poodle.smnavi.user.redisdomain.RefreshTokenCache;

public interface RefreshTokenCacheRepository extends CrudRepository<RefreshTokenCache, String> {
}
