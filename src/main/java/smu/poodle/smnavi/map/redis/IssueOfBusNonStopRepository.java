package smu.poodle.smnavi.map.redis;

import org.springframework.data.repository.CrudRepository;
import smu.poodle.smnavi.map.redis.domain.IssueOfBusNonStop;

public interface IssueOfBusNonStopRepository extends CrudRepository<IssueOfBusNonStop, String> {
}
