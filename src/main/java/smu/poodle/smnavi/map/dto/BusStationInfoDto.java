package smu.poodle.smnavi.map.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import smu.poodle.smnavi.map.domain.station.BusStationInfo;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BusStationInfoDto {
    String stationName;

    String busName;

    String stationId;

    String x;

    String y;

    public BusStationInfo toEntity() {
        return BusStationInfo.builder()
                .stationName(this.stationName)
                .busName(this.busName)
                .stationId(this.stationId)
                .x(this.x)
                .y(this.y)
                .build();
    }

}
