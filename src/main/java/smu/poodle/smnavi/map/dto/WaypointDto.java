package smu.poodle.smnavi.map.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import smu.poodle.smnavi.map.domain.station.BusStation;
import smu.poodle.smnavi.map.domain.station.Place;
import smu.poodle.smnavi.map.domain.station.SubwayStation;

public class WaypointDto {
    @Getter
    @SuperBuilder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SubwayStationDto extends AbstractWaypointDto {
        Integer stationId; //역 아이디
        String stationName;
        @Override
        public SubwayStation toEntity() {
            return SubwayStation.builder()
                    .x(super.getGpsX())
                    .y(super.getGpsY())
                    .stationId(this.stationId)
                    .stationName(this.stationName)
                    .build();
        }
    }

    @Getter
    @SuperBuilder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class BusStationDto extends AbstractWaypointDto {
        String localStationId;
        String stationName;

        @Override
        public BusStation toEntity() {
            return BusStation.builder()
                    .x(super.getGpsX())
                    .y(super.getGpsY())
                    .localStationId(this.localStationId)
                    .stationName(this.stationName)
                    .build();
        }
    }

    @Getter
    @SuperBuilder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class PlaceDto extends AbstractWaypointDto {
        Long id;
        String placeName;
        @Override
        public Place toEntity() {
            return Place.builder()
                    .x(super.getGpsX())
                    .y(super.getGpsY())
                    .placeName(placeName)
                    .build();
        }
    }
}
