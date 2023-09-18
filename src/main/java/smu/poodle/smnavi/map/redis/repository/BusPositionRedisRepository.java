package smu.poodle.smnavi.map.redis.repository;

import org.springframework.data.repository.CrudRepository;
import smu.poodle.smnavi.map.redis.hash.BusPosition;

public interface BusPositionRedisRepository extends CrudRepository<BusPosition, String> {
}
