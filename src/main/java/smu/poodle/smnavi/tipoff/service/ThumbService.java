package smu.poodle.smnavi.tipoff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.poodle.smnavi.common.exception.RestApiException;
import smu.poodle.smnavi.tipoff.domain.Thumb;
import smu.poodle.smnavi.tipoff.domain.ThumbStatus;
import smu.poodle.smnavi.tipoff.domain.TipOff;
import smu.poodle.smnavi.tipoff.dto.LikeInfoDto;
import smu.poodle.smnavi.tipoff.repository.ThumbsRepository;
import smu.poodle.smnavi.user.domain.UserEntity;

import java.util.Optional;

import static smu.poodle.smnavi.user.exception.AuthExceptionCode.AUTHORIZATION_REQUIRED;
import static smu.poodle.smnavi.user.util.LoginUserUtil.*;

@Service
@RequiredArgsConstructor
public class ThumbService {
    private final ThumbsRepository thumbsRepository;

    @Transactional
    public LikeInfoDto doLikeOrHate(Long tipOffId, ThumbStatus thumbStatus) {
        Long userId = getLoginMemberId();

        if(userId == 0) {
            throw new RestApiException(AUTHORIZATION_REQUIRED);
        }

        thumbsRepository.findByUserIdAndTipOffId(userId, tipOffId)
                .ifPresentOrElse((thumb -> {
                            if (thumb.getThumbStatus().equals(thumbStatus)) {
                                thumbsRepository.delete(thumb);
                            } else {
                                thumb.setThumbStatus(thumbStatus);
                            }
                        }),

                        () -> thumbsRepository.save(Thumb.builder()
                                .user(UserEntity.builder().id(userId).build())
                                .tipOff(TipOff.builder().id(tipOffId).build())
                                .thumbStatus(thumbStatus)
                                .build()));

        return getLikeInfo(tipOffId);
    }

    @Transactional
    public LikeInfoDto getLikeInfo(Long tipOffId) {
        Long userId = getLoginMemberId();

        Long likeCount = thumbsRepository.getLikeCount(tipOffId);
        Long hateCount = thumbsRepository.getHateCount(tipOffId);

        Optional<Thumb> optionalThumb = thumbsRepository.findByUserIdAndTipOffId(userId, tipOffId);

        return new LikeInfoDto(likeCount, hateCount, optionalThumb);
    }
}
