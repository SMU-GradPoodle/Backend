package smu.poodle.smnavi.tipoff.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import smu.poodle.smnavi.tipoff.domain.Location;
import smu.poodle.smnavi.tipoff.domain.TipOff;
import smu.poodle.smnavi.map.domain.data.TransitType;
import smu.poodle.smnavi.tipoff.domain.Kind;
import smu.poodle.smnavi.user.domain.UserEntity;

@Getter
@AllArgsConstructor
@Builder
public class TipOffRequestDto {
    private String transitType;
    private int kind;
    private String stationId;

    @NotEmpty(message = "제목은 필수 항목 입니다.")
    @Size(min = 1, max = 30, message = "제목은 1자 이상 30자 이하로 입력해주세요.")
    private String title;

    @NotEmpty(message = "내용은 필수 항목 입니다.")
    @Size(min = 1, max = 5000, message = "내용은 10자 이상 5000자 이하로 입력해주세요.")
    private String content;

    public TipOff ToEntity(Long loginUserId) {
        return TipOff.builder()
                .author(loginUserId != 0 ? UserEntity.builder().id(loginUserId).build() : null)
                .title(title)
                .content(content)
                .transitType(TransitType.valueOf(transitType))
                .kind(Kind.getKindByNumber(this.kind))
                .location(Location.stationIdToLocation(stationId))
                .increaseCount(0)
                .build();
    }
}


