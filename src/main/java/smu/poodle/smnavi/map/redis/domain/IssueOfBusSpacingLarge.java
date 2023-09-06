package smu.poodle.smnavi.map.redis.domain;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "licensePlate")
@FieldDefaults(level = AccessLevel.PRIVATE)
@RedisHash("bus_large_spacing")
public class IssueOfBusSpacingLarge {
    @Id
    String licensePlate;
    int secondsDifferenceFromFrontBus;
}
