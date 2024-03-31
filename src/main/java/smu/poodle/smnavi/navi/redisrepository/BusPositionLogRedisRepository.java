package smu.poodle.smnavi.navi.redisrepository;

import org.springframework.data.repository.CrudRepository;
import smu.poodle.smnavi.navi.redisdomain.BusPositionLog;

public interface BusPositionLogRedisRepository extends CrudRepository<BusPositionLog, String> {
}
