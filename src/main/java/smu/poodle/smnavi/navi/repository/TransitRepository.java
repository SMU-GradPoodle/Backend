package smu.poodle.smnavi.navi.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import smu.poodle.smnavi.navi.domain.path.DetailPosition;
import smu.poodle.smnavi.navi.domain.path.Edge;
import smu.poodle.smnavi.navi.domain.path.FullPath;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TransitRepository {

    private final EntityManager em;

    public List<DetailPosition> isContainDetailPos(Edge edge) {
        return em.createQuery("select d from DetailPosition d " +
                        "where d.edge = :edge", DetailPosition.class)
                .setParameter("edge", edge)
                .getResultList();

    }

    public FullPath findRouteById(Long routeId) {
        return em.createQuery("select r " +
                        "from FullPath as r " +
                        "join fetch r.startWaypoint " +
                        "where r.id = : id ", FullPath.class)
                .setParameter("id", routeId)
                .getSingleResult();
    }
}
