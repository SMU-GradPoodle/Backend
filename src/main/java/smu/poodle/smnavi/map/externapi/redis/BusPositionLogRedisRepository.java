package smu.poodle.smnavi.map.externapi.redis;

import org.springframework.data.repository.CrudRepository;
import smu.poodle.smnavi.map.externapi.redis.domain.BusPositionLog;

public interface BusPositionLogRedisRepository extends CrudRepository<BusPositionLog, String> {
}
