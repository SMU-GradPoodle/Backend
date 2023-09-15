package smu.poodle.smnavi.map.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import smu.poodle.smnavi.map.domain.station.BusStationInfo;
import smu.poodle.smnavi.map.externapi.redis.domain.BusArriveInfo;


@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BusArriveInfoDto {
    String stationId;
    String stationName;
    String firstArriveMessage;
    String secondArriveMessage;
    String gpsX;
    String gpsY;
    Boolean isNonstop;
    Boolean isLargeInterval;
    Boolean hasIssue;

    public static BusArriveInfoDto generateDto(BusArriveInfo busArriveInfo, BusStationInfo busStationInfo) {
        return BusArriveInfoDto.builder()
                .stationId(busArriveInfo.getStationId())
                .stationName(busStationInfo.getStationName())
                .firstArriveMessage(busArriveInfo.getFirstArriveMessage())
                .secondArriveMessage(busArriveInfo.getSecondArriveMessage())
                .gpsX(busStationInfo.getX())
                .gpsY(busStationInfo.getY())
                .isNonstop(busArriveInfo.getIsNonstop())
                .isLargeInterval(busArriveInfo.getIsLargeInterval())
                .hasIssue(busArriveInfo.getHasIssue())
                .build();
    }
}
