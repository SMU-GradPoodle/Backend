package smu.poodle.smnavi.tipoff.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import smu.poodle.smnavi.tipoff.domain.TipOff;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TipOffRepository extends JpaRepository<TipOff, Long> {

    @EntityGraph(attributePaths = "author")
    Optional<TipOff> findById(Long id);

    @Query("select t from TipOff as t " +
            "where (:isMine = false or t.author.id = :loginUserId)")
    Page<TipOff> findByQuery(@Param("isMine") Boolean isMine, @Param("loginUserId") Long loginUserId, Pageable pageable);

}
