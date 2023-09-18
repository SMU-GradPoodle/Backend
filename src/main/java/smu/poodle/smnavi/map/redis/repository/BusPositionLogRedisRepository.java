package smu.poodle.smnavi.map.redis.repository;

import org.springframework.data.repository.CrudRepository;
import smu.poodle.smnavi.map.redis.hash.BusPositionLog;

public interface BusPositionLogRedisRepository extends CrudRepository<BusPositionLog, String> {
}
