package smu.poodle.smnavi.tipoff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LikeHateDto {
    private long id;
    private long boardId;
    private int identify;


    public LikeHateDto(){}
    public LikeHateDto(long board, int identify){
        this.boardId = boardId;
        this.identify = identify;
    }

}
