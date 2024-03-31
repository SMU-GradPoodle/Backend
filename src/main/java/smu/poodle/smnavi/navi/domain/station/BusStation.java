package smu.poodle.smnavi.navi.domain.station;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import smu.poodle.smnavi.navi.dto.AbstractWaypointDto;
import smu.poodle.smnavi.navi.dto.WaypointDto;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BusStation extends Waypoint {
    String localStationId;
    String stationName;

    @Override
    public String getPointName() {
        return this.stationName;
    }

    @Override
    public AbstractWaypointDto toDto() {
        return WaypointDto.BusStationDto.builder()
                .id(super.getId())
                .gpsX(super.getX())
                .gpsY(super.getY())
                .localStationId(localStationId)
                .stationName(stationName)
                .build();
    }
}
