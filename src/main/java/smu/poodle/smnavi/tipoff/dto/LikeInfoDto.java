package smu.poodle.smnavi.tipoff.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import smu.poodle.smnavi.tipoff.domain.Thumb;
import smu.poodle.smnavi.tipoff.domain.ThumbStatus;

import java.util.Optional;

@Getter
@Builder
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LikeInfoDto {
//    @JsonIgnore
//    long tipOffId;
    long likeCount;
    Boolean islLiked;
    long hateCount;
    Boolean isHated;

    public LikeInfoDto(Long likeCount, Long hateCount, Optional<Thumb> userThumb) {
        this.likeCount = likeCount == null ? 0 : likeCount;
        this.hateCount = hateCount == null ? 0 : hateCount;
        this.islLiked = userThumb.isPresent() && userThumb.get().getThumbStatus().equals(ThumbStatus.THUMBS_UP);
        this.isHated = userThumb.isPresent() && userThumb.get().getThumbStatus().equals(ThumbStatus.THUMBS_DOWN);
    }
}
