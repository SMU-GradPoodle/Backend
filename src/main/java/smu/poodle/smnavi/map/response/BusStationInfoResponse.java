package smu.poodle.smnavi.map.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import smu.poodle.smnavi.map.dto.BusStationInfoDto;

import java.util.List;

@Getter
@SuperBuilder
public class BusStationInfoResponse extends BaseResponse{
    private final List<BusStationInfoDto> data;
}
