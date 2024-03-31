package smu.poodle.smnavi.navi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Setter
@Getter
@ToString
public class GpsPointDto {
    private String gpsX;
    private String gpsY;
}
