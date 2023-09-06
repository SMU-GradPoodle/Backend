package smu.poodle.smnavi.map.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;


@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BusArriveInfoDto {

    int stationId;
    int stationOrder;
    int firstArrivalSeconds;
    int secondArrivalSeconds;
    String firstArrivalLicensePlate;
    String secondArrivalLicensePlate;
    String firstArrivalNextStationId;
    String secondArrivalNextStationId;
    int firstArrivalStationOrder;
    int secondArrivalStationOrder;
    String stationName;
    boolean isStationNonStop;
}
