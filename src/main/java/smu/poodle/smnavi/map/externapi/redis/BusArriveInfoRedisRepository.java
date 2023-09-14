package smu.poodle.smnavi.map.externapi.redis;

import org.springframework.data.repository.CrudRepository;
import smu.poodle.smnavi.map.externapi.redis.domain.BusArriveInfo;

public interface BusArriveInfoRedisRepository extends CrudRepository<BusArriveInfo, String> {
}
