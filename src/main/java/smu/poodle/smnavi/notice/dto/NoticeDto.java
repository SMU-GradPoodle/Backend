package smu.poodle.smnavi.notice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import smu.poodle.smnavi.notice.domain.NoticeEntity;

import java.time.LocalDateTime;

//게시글 등록
//제목, 내용
@Getter
@AllArgsConstructor
@Builder
public class NoticeDto {
    private Long id;
    private LocalDateTime regDate;
    private LocalDateTime updateDate;
    private int increaseCount; //조회수

    @NotEmpty(message = "제목은 필수 항목 입니다.")
    @Size(min = 1, max = 30, message = "제목은 1자 이상 30자 이하로 입력해주세요.")
    private String title;

    @NotEmpty(message = "내용은 필수 항목 입니다.")
    @Size(min = 1, max = 5000, message = "내용은 1자 이상 5000자 이하로 입력해주세요.")
    private String content;

    public NoticeDto(){
    }
    public NoticeDto(Long id, String title, String content){
        this.id = id;
        this.title = title;
        this.content = content;
    }
    public NoticeDto(Long id){
        this.id = id;
    }
    public NoticeDto(Long id, LocalDateTime regDate, LocalDateTime updateDate, String title, String content) {
        this.id = id;
        this.regDate = regDate;
        this.updateDate = updateDate;
        this.title = title;
        this.content = content;
    }
    public NoticeDto(Long id, String title, String content, LocalDateTime regDate, LocalDateTime updateDate, int increaseCount){
        this.id = id;
        this.regDate = regDate;
        this.updateDate = updateDate;
        this.title = title;
        this.content = content;
        this.increaseCount = increaseCount;
    }
    public NoticeEntity ToEntity(){
        return new NoticeEntity(this.id, this.title, this.content,this.regDate, this.updateDate,0);
    }

}
