package smu.poodle.smnavi.map.externapi.redis;

import org.springframework.data.repository.CrudRepository;
import smu.poodle.smnavi.map.externapi.redis.domain.BusPosition;

public interface BusPositionRedisRepository extends CrudRepository<BusPosition, String> {
}
