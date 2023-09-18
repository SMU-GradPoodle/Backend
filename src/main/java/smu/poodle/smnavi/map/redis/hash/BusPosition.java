package smu.poodle.smnavi.map.redis.hash;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@RedisHash("bus_position")
public class BusPosition {
    @Id
    String licensePlate;
    @JsonIgnore
    Integer sectionOrder;
    String gpsX;
    String gpsY;
    @Builder.Default
    Boolean hasIssue = false;
}
