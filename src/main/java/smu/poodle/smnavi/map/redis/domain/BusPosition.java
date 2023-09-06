package smu.poodle.smnavi.map.redis.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@RedisHash("bus_position")
public class BusPosition {
    @Id
    String licensePlate;

    String gpsX;
    String gpsY;
}
