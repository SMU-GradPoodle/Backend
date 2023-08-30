package smu.poodle.smnavi.tipoff.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import smu.poodle.smnavi.tipoff.domain.Thumb;
import smu.poodle.smnavi.user.domain.UserEntity;

import java.util.Optional;

@Repository
public interface LikeHateRepository extends JpaRepository<Thumb,Integer> { //<엔티티클래스, pk타입>
    @Query("select t from Thumb as t")
    Optional<Thumb> findByUserAndBoard_Id(Long userId, long boardId);

    @Query("select count(t) from Thumb as t")
    int countByBoard_IdAndIdentify(Long board_id, int identify);
}