package smu.poodle.smnavi.navi.redisrepository;

import org.springframework.data.repository.CrudRepository;
import smu.poodle.smnavi.navi.redisdomain.BusPosition;

public interface BusPositionRedisRepository extends CrudRepository<BusPosition, String> {
}
