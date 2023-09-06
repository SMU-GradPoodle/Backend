package smu.poodle.smnavi.map.redis;

import org.springframework.data.repository.CrudRepository;
import smu.poodle.smnavi.map.redis.domain.BusPosition;

public interface BusPositionRepository extends CrudRepository<BusPosition, String> {
}
