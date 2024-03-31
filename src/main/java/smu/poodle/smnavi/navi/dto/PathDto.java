package smu.poodle.smnavi.navi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import smu.poodle.smnavi.navi.enums.TransitType;
import smu.poodle.smnavi.navi.domain.mapping.FullPathAndSubPath;
import smu.poodle.smnavi.navi.domain.mapping.SubPathAndEdge;
import smu.poodle.smnavi.navi.domain.path.DetailPosition;
import smu.poodle.smnavi.navi.domain.path.Edge;
import smu.poodle.smnavi.navi.domain.path.FullPath;
import smu.poodle.smnavi.navi.domain.path.SubPath;

import java.util.ArrayList;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class PathDto {

    @Getter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Info {
        int time;
        int subPathCnt;
        List<SubPathDto> subPathList;

        List<String> accidents;

        @JsonIgnore
        String mapObj;

        public static Info fromEntity(FullPath fullPath) {
            List<SubPath> subPaths = fullPath.getSubPaths().stream()
                    .map(FullPathAndSubPath::getSubPath).toList();

            List<PathDto.SubPathDto> subPathDtos = new ArrayList<>();
            List<String> accidents = new ArrayList<>();

            for (SubPath subPath : subPaths) {
                if (subPath.getTransitType() == TransitType.WALK && subPath.getSectionTime() == 0) {
                    continue;
                }

                List<Edge> edges = subPath.getEdgeInfos().stream().map(SubPathAndEdge::getEdge).toList();

                subPathDtos.add(PathDto.SubPathDto.makeSubPathDtoWithEdges(subPath, edges));
            }

            return Info.builder()
                    .subPathList(subPathDtos)
                    .time(fullPath.getTotalTime())
                    .subPathCnt(subPaths.size())
                    .accidents(accidents)
                    .build();
        }
    }

    @Getter
    @Setter
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SubPathDto {
        TransitType transitType;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String busType;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        Integer busTypeInt;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        String lineName;
        String from;
        String to;
        Integer sectionTime;
        List<AbstractWaypointDto> stationList;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        List<DetailPositionDto> gpsDetail;

        public static SubPathDto makeSubPathDtoWithEdges(SubPath subPath, List<Edge> edges) {
            SubPathDto subPathDto = SubPathDto.builder()
                    .transitType(subPath.getTransitType())
                    .sectionTime(subPath.getSectionTime())
                    .stationList(AbstractWaypointDto.edgesToWaypointDtos(edges))
                    .from(subPath.getToName())
                    .to(subPath.getToName())
                    .gpsDetail(DetailPositionDto.edgesToDetailPositionDtos(edges))
                    .lineName(subPath.getLineName())
                    .from(subPath.getFromName())
                    .to(subPath.getToName())
                    .build();

            if (subPath.getTransitType() == TransitType.BUS) {
                subPathDto.setBusType(subPath.getBusType().getTypeName());
            }

            return subPathDto;
        }

    }

    @Data
    @AllArgsConstructor
    public static class DetailPositionDto {
        private String gpsX;
        private String gpsY;

        public DetailPositionDto(DetailPosition detailPosition) {
            this.gpsX = detailPosition.getX();
            this.gpsY = detailPosition.getY();
        }

        public static List<DetailPositionDto> edgesToDetailPositionDtos(List<Edge> edges) {
            List<DetailPositionDto> detailPositionDtos = new ArrayList<>();

            for (Edge edge : edges) {
                detailPositionDtos.addAll(edge.getDetailPositionList()
                        .stream().map(PathDto.DetailPositionDto::new).toList());
            }

            return detailPositionDtos;
        }
    }
}
