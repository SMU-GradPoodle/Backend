package smu.poodle.smnavi.tipoff.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import smu.poodle.smnavi.tipoff.domain.Location;
import smu.poodle.smnavi.tipoff.domain.TipOff;


public class TipOffResponseDto {

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Simple {
        Long id;

        public static Simple of(TipOff tipOff) {
            return Simple.builder()
                    .id(tipOff.getId())
                    .build();
        }
    }

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Detail {
        Long id;
        String nickname;
        Kind kind;
        Transportation transportation;
        String content;
        String createdTime;
        LikeInfoDto likeInfo;

        public static Detail of(TipOff tipOff, LikeInfoDto likeInfoDto) {
            return Detail.builder()
                    .id(tipOff.getId())
                    .nickname(tipOff.getAuthor() == null ? "익명이" : tipOff.getAuthor().getNickname())
                    .content(tipOff.getContent())
                    .transportation(Transportation.of(tipOff.getLocation()))
                    .kind(Kind.of(tipOff.getKind()))
                    .createdTime(tipOff.getFormattedCreatedAt())
                    .likeInfo(likeInfoDto)
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


