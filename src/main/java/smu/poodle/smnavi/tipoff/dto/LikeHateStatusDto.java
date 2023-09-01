package smu.poodle.smnavi.tipoff.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeHateStatusDto {
    private int likeCount;
    private int hateCount;
    private int identify;

    public LikeHateStatusDto(int likeCount, int hateCount, int identify){
        this.likeCount = likeCount;
        this.hateCount = hateCount;
        this.identify = identify;
    }
}
