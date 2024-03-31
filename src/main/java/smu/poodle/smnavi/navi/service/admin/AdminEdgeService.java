package smu.poodle.smnavi.navi.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smu.poodle.smnavi.navi.domain.path.Edge;
import smu.poodle.smnavi.navi.domain.station.Waypoint;
import smu.poodle.smnavi.navi.repository.EdgeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminEdgeService {
    private final EdgeRepository edgeRepository;
    public List<Edge> makeAndSaveEdgeIfNotExist(List<Waypoint> persistedWaypointList) {
        List<Edge> persistedEdgeList = new ArrayList<>();
        List<Edge> edgeList = makeEdgeTypeIsNotWalk(persistedWaypointList);

        for (Edge edge : edgeList) {
            persistedEdgeList.add(saveEdgeIfNotExist(edge));
        }

        return persistedEdgeList;
    }

    public List<Edge> makeEdgeTypeIsNotWalk(List<Waypoint> persistedWaypointList) {
        List<Edge> edgeList = new ArrayList<>();
        Waypoint curSrc = persistedWaypointList.get(0);

        for (int i = 1; i < persistedWaypointList.size(); i++) {
            Waypoint curDstAndNextSrc = persistedWaypointList.get(i);
            edgeList.add(Edge.builder()
                    .src(curSrc)
                    .dst(curDstAndNextSrc)
                    .detailExist(false)
                    .build());

            curSrc = curDstAndNextSrc;
        }
        return edgeList;
    }

    public Edge saveEdgeIfNotExist(Edge edge) {
        Optional<Edge> persistedEdge = edgeRepository.findFirstBySrcIdAndDstId(edge.getSrc().getId(), edge.getDst().getId());
        return persistedEdge.orElseGet(() -> edgeRepository.save(edge));
    }
}
