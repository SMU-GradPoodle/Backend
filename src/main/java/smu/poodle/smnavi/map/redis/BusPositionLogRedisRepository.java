package smu.poodle.smnavi.map.redis;

import org.springframework.data.repository.CrudRepository;
import smu.poodle.smnavi.map.redis.domain.BusPositionLog;

public interface BusPositionLogRedisRepository extends CrudRepository<BusPositionLog, String> {
}
