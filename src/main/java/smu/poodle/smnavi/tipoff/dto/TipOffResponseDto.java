package smu.poodle.smnavi.tipoff.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import smu.poodle.smnavi.map.domain.data.TransitType;
import smu.poodle.smnavi.tipoff.domain.Kind;
import smu.poodle.smnavi.tipoff.domain.Location;
import smu.poodle.smnavi.tipoff.domain.TipOff;


public class TipOffResponseDto {

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Detail {
        Long id;
        Kind kind;
        Transportation transportation;
        String title;
        String content;
        String createdAt;


        public static Detail of(TipOff tipOff) {
            return Detail.builder()
                    .id(tipOff.getId())
                    .title(tipOff.getTitle())
                    .content(tipOff.getContent())
                    .transportation(Transportation.of(tipOff.getLocation()))
                    .kind(Kind.of(tipOff.getKind()))
                    .createdAt(tipOff.getCreatedAtToString())
                    .build();
        }
    }

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Kind {
        int number;
        String description;

        public static Kind of(smu.poodle.smnavi.tipoff.domain.Kind kind) {
            return Kind.builder()
                    .number(kind.getKindNumber())
                    .description(kind.getKindDescription())
                    .build();
        }
    }

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Transportation {
        String type;
        String station;

        public static Transportation of(Location location) {
            return Transportation.builder()
                    .type(location.getTransitType().getDescription())
                    .station(location.getStationName())
                    .build();
        }
    }
}


