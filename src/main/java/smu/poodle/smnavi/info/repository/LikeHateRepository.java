package smu.poodle.smnavi.info.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smu.poodle.smnavi.info.domain.LikeHateEntity;
import smu.poodle.smnavi.user.domain.UserEntity;

import java.util.Optional;

@Repository
public interface LikeHateRepository extends JpaRepository<LikeHateEntity,Integer> { //<엔티티클래스, pk타입>
    LikeHateEntity save(LikeHateEntity likeHateEntity);
    Optional<LikeHateEntity> findByUserIdAndBoard_Id(Long userId, long boardId);
    int countByBoard_IdAndIdentify(Long board_id, int identify);
}