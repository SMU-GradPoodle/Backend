package smu.poodle.smnavi.map.redis.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@RedisHash("issue_of_bus_non_stop")
public class IssueOfBusNonStop {
    @Id
    String busName;

    String nonStopStartStationName;

    String nonStopEndStationName;
}
