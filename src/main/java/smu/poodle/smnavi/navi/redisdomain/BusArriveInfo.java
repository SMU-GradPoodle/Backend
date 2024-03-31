package smu.poodle.smnavi.navi.redisdomain;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@RedisHash("bus_arrive_info")
public class BusArriveInfo {
    private static BusArriveInfo DEFAULT_INSTANCE;

    @Id
    String stationId;
    String firstArriveMessage;
    String secondArriveMessage;
    Boolean isNonstop;
    Boolean isLargeInterval;
    Boolean hasIssue;

    static {
        DEFAULT_INSTANCE = BusArriveInfo.builder()
                .firstArriveMessage("정보 없음")
                .secondArriveMessage("정보 없음")
                .isNonstop(false)
                .isLargeInterval(false)
                .hasIssue(false)
                .build();
    }

    public static BusArriveInfo getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }
}
