package smu.poodle.smnavi.map.redis.hash;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

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

    public static BusArriveInfo getDefaultInstance() {
        return BusArriveInfo.builder()
                .firstArriveMessage("정보 없음")
                .secondArriveMessage("정보 없음")
                .isNonstop(false)
                .isLargeInterval(false)
                .hasIssue(false)
                .build();
    }
}
