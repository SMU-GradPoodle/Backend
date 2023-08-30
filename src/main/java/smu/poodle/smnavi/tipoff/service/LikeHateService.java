package smu.poodle.smnavi.tipoff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smu.poodle.smnavi.tipoff.domain.Thumb;
import smu.poodle.smnavi.tipoff.dto.LikeHateDto;
import smu.poodle.smnavi.tipoff.repository.LikeHateRepository;
import smu.poodle.smnavi.user.sevice.LoginService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeHateService {
    private final LikeHateRepository likeHateRepository;
    private final LoginService loginService;

    public int checkLikeOrHate(LikeHateDto likeHateDto) {
        Long userId = loginService.getLoginMemberId();
        Optional<Thumb> likeOrHate = likeHateRepository.findByUserAndBoard_Id(userId, likeHateDto.getBoardId());
        //좋아요->좋아요 : 좋아요 취소
        //좋아요->싫어요 : 좋아요 취소 싫어요 증가
        //null->좋아요 : 좋아요 증가
        //null->싫어요 : 싫어요 증가
        //싫어요->좋아요 : 좋아요 증가 싫어요 감소
        //싫어요->싫어요 : 싫어요 취소
        if (likeOrHate.isPresent()) { //누른 전적이 있음. 수정 삭제
            likeHateRepository.delete(likeOrHate.get());
        } else { //수정
            likeOrHate.get().setIdentify(likeHateDto.getIdentify());
            likeHateRepository.save(likeOrHate.get());
            return likeOrHate.get().getIdentify();
        }
        return -1; //-1은 그냥 없는거야! 0은 싫어요, 1은 좋아요야.
    }

    public Object countByBoard_IdAndIdentify(Long boardId) {
        Long userId = loginService.getLoginMemberId();

        Map<String, Object> result = new HashMap<>();
        int like = likeHateRepository.countByBoard_IdAndIdentify(boardId, 1); //좋아요 개수
        int hate = likeHateRepository.countByBoard_IdAndIdentify(boardId, 0); //싫어요개수
        Optional<Thumb> likeOrHate = likeHateRepository.findByUserAndBoard_Id(userId, boardId); //눌렀는지 아닌지 여부
        result.put("like", like);
        result.put("hate", hate);

        if (likeOrHate.isPresent()) {
            result.put("emotion", likeOrHate.get().getIdentify());
        } else {
            result.put("emotion", -1); //아무런 공감도 표시하지 않음
        }
        return result;
    }
}
