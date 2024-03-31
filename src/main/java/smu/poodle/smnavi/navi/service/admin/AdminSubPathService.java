package smu.poodle.smnavi.navi.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smu.poodle.smnavi.navi.domain.mapping.SubPathAndEdge;
import smu.poodle.smnavi.navi.domain.path.Edge;
import smu.poodle.smnavi.navi.domain.path.SubPath;
import smu.poodle.smnavi.navi.enums.TransitType;
import smu.poodle.smnavi.navi.repository.SubPathAndEdgeRepository;
import smu.poodle.smnavi.navi.repository.SubPathRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminSubPathService {

    private final SubPathRepository subPathRepository;

    private final SubPathAndEdgeRepository subPathAndEdgeRepository;

    public SubPath saveWithEdgeMapping(SubPath subPath, List<Edge> edges) {
        //todo: 겹치는 서브 패스가 있는지 확인해야함
        Optional<SubPath> persistedSubPath = subPathRepository.findTopBySrcAndDst(subPath.getSrc(), subPath.getDst());

        if (persistedSubPath.isPresent()) {
            return persistedSubPath.get();
        } else {
            subPathRepository.save(subPath);

            for (Edge edge : edges) {
                subPathAndEdgeRepository.save(
                        SubPathAndEdge.builder()
                                .subPath(subPath)
                                .edge(edge)
                                .build()
                );
            }
            return subPath;
        }
    }

    public List<SubPath> findAllByLineNameAndTransitType(String busNumber, TransitType transitType) {
        return subPathRepository.findByLineNameAndTransitType(busNumber, transitType);
    }
}
