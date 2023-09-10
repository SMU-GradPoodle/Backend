package smu.poodle.smnavi.map.domain.station;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import smu.poodle.smnavi.map.dto.BusStationInfoDto;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BusStationInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String stationName;

    @Column
    private String busName;

    @Column
    private String stationId;

    @Column
    private String x;

    @Column
    private String y;

    public BusStationInfoDto toDto(){
        return BusStationInfoDto.builder()
                .stationName(this.stationName)
                .busName(this.busName)
                .stationId(this.stationId)
                .x(this.x)
                .y(this.y)
                .build();
    }
}