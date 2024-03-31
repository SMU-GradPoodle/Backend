package smu.poodle.smnavi.navi.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import smu.poodle.smnavi.navi.redisdomain.BusArriveInfo;
import smu.poodle.smnavi.navi.domain.BusStationInfo;


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
                .stationId(busStationInfo.getStationId())
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
