package smu.poodle.smnavi.navi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.poodle.smnavi.navi.domain.path.Edge;

import java.util.Optional;

public interface EdgeRepository extends JpaRepository<Edge, Long> {
    Optional<Edge> findFirstBySrcIdAndDstId(Long srcId, Long dstId);
}
