package smu.poodle.smnavi.navi.redisrepository;

import org.springframework.data.repository.CrudRepository;
import smu.poodle.smnavi.navi.redisdomain.BusArriveInfo;

import java.util.List;

public interface BusArriveInfoRedisRepository extends CrudRepository<BusArriveInfo, String> {
    List<BusArriveInfo> findAll();
}
