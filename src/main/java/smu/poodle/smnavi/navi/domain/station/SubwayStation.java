package smu.poodle.smnavi.navi.domain.station;

import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import smu.poodle.smnavi.navi.dto.AbstractWaypointDto;
import smu.poodle.smnavi.navi.dto.WaypointDto;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubwayStation extends Waypoint {
    Integer stationId; //역 아이디
    String stationName; //역 이름

    @Override
    public String getPointName() {
        return this.stationName;
    }

    @Override
    public AbstractWaypointDto toDto(){
        return WaypointDto.SubwayStationDto.builder()
                .id(super.getId())
                .gpsX(super.getX())
                .gpsY(super.getY())
                .stationId(stationId)
                .stationName(stationName)
                .build();
    }
}
