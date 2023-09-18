package smu.poodle.smnavi.map.redis.repository;

import org.springframework.data.repository.CrudRepository;
import smu.poodle.smnavi.map.redis.hash.BusArriveInfo;

import java.util.List;

public interface BusArriveInfoRedisRepository extends CrudRepository<BusArriveInfo, String> {
    List<BusArriveInfo> findAll();
}
