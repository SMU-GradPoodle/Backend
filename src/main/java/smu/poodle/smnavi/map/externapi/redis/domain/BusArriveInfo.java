package smu.poodle.smnavi.map.externapi.redis.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@RedisHash("bus_arrive_info")
public class BusArriveInfo {
    @Id
    String stationId;
    String firstArriveMessage;
    String secondArriveMessage;
    Boolean isNonstop;
    Boolean isLargeInterval;
    Boolean hasIssue;
}
