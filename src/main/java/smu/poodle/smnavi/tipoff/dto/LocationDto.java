package smu.poodle.smnavi.tipoff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import smu.poodle.smnavi.tipoff.domain.Location;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class LocationDto {
    private String transitType;
    private List<LocationInfoDto> locationInfos;

    @AllArgsConstructor
    @Builder
    @Getter
    public static class LocationInfoDto {
        private String stationId;
        private String stationName;
        public static LocationInfoDto of(Location location) {
            return LocationInfoDto.builder()
                    .stationId(location.getStationId())
                    .stationName(location.getStationName())
                    .build();
        }
    }
    public static LocationDto from(String transitType, List<Location> locations) {
        return LocationDto.builder()
                .transitType(transitType)
                .locationInfos(locations.stream().map(LocationInfoDto::of).toList())
                .build();
    }
}
