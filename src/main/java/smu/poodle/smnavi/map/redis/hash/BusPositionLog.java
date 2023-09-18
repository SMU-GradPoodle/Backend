package smu.poodle.smnavi.map.redis.hash;

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
@RedisHash("bus_position_log")
public class BusPositionLog {
    @Id
    String licensePlate;
    Integer sectionOrder;
    String gpsX;
    String gpsY;

    public static List<BusPositionLog> convertBusPositionList(List<BusPosition> busPositionList) {
        return busPositionList.stream().map(BusPositionLog::convertBusPosition).collect(Collectors.toList());
    }
    public static BusPositionLog convertBusPosition(BusPosition busPosition) {
        return BusPositionLog.builder()
                .licensePlate(busPosition.getLicensePlate())
                .sectionOrder(busPosition.getSectionOrder())
                .gpsX(busPosition.getGpsX())
                .gpsY(busPosition.getGpsY())
                .build();
    }
}
