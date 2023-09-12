package smu.poodle.smnavi.map.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestBusPositionDto {
    String licensePlate;
    String gpsX;
    String gpsY;
    boolean hasIssue;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String issueMessage;
}
