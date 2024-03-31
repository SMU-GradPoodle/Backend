package smu.poodle.smnavi.navi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.poodle.smnavi.navi.domain.mapping.SubPathAndEdge;
public interface SubPathAndEdgeRepository extends JpaRepository<SubPathAndEdge, Long> {
}
