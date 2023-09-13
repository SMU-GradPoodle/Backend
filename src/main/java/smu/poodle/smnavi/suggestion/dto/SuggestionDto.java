package smu.poodle.smnavi.suggestion.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import smu.poodle.smnavi.suggestion.domain.Suggestion;

@Getter
@AllArgsConstructor
@Builder
public class SuggestionDto {
    private Long id;
    @NotEmpty(message = "내용을 입력해주세요")
    @Size(min=1, max=5000, message = "내용은 1자 이상 5000자 이하로 입력해주세요")
    private String content;

    public Suggestion ToEntity(){
        return new Suggestion(this.id, this.content);
    }
}
