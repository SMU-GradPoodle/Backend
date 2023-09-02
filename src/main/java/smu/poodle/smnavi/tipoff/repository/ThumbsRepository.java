package smu.poodle.smnavi.tipoff.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import smu.poodle.smnavi.tipoff.domain.Thumb;

import java.util.Optional;

@Repository
public interface ThumbsRepository extends JpaRepository<Thumb,Integer> { //<엔티티클래스, pk타입>
    @Query("select t from Thumb as t")
    Optional<Thumb> findByUserAndBoard_Id(Long userId, long boardId);

    @Query("select count(t) from Thumb as t")
    int countByBoard_IdAndIdentify(Long board_id, int identify);

    Optional<Thumb> findByUserIdAndTipOffId(Long userId, Long tipOffId);

    @Query("select count(thumb.id)" +
            "from Thumb as thumb " +
            "join TipOff as tip " +
            "on tip.id = thumb.tipOff.id " +
            "where thumb.tipOff.id = :tipOffId " +
            "and thumb.thumbStatus = 'THUMBS_UP'" +
            "group by tip.id")
    Long getLikeCount(@Param("tipOffId") Long tipOffId);

    @Query("select count(thumb.id)" +
            "from Thumb as thumb " +
            "join TipOff as tip " +
            "on tip.id = thumb.tipOff.id " +
            "where thumb.tipOff.id = :tipOffId " +
            "and thumb.thumbStatus = 'THUMBS_DOWN'" +
            "group by tip.id")
    Long getHateCount(@Param("tipOffId") Long tipOffId);

}