package smu.poodle.smnavi.navi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import smu.poodle.smnavi.navi.domain.mapping.FullPathAndSubPath;
import smu.poodle.smnavi.navi.domain.path.FullPath;
import smu.poodle.smnavi.navi.domain.path.SubPath;

import java.util.List;

public interface FullPathAndSubPathRepository extends JpaRepository<FullPathAndSubPath, Long> {

    @Query("select f from FullPath f " +
            "join FullPathAndSubPath as fullAndSubMapping " +
            "on f = fullAndSubMapping.fullPath " +
            "where fullAndSubMapping.subPath in :subPathList " +
            "group by f.id " +
            "having count(fullAndSubMapping) = :subPathSize ")
    List<FullPath> findAllBySubPath(@Param("subPathList") List<SubPath> subPathList
            , @Param("subPathSize") int subPathSize);
}
