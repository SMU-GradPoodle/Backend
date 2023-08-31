package smu.poodle.smnavi.map.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import smu.poodle.smnavi.map.domain.Accident;

public class AccidentDto {

    @Getter
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Info {
        Long id;
        String kind;
        AbstractWaypointDto station;
        String createdAt;

        public static AccidentDto.Info of(Accident accident) {
            return Info.builder()
                    .id(accident.getId())
                    .kind(accident.getMessage())
                    .station(accident.getWaypoint().toDto())
                    .createdAt(accident.getCreatedTimeToString())
                    .build();
        }
    }
}
