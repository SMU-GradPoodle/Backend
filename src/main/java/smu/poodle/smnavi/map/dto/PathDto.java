package smu.poodle.smnavi.map.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import smu.poodle.smnavi.map.enums.TransitType;
import smu.poodle.smnavi.map.domain.mapping.FullPathAndSubPath;
import smu.poodle.smnavi.map.domain.mapping.SubPathAndEdge;
import smu.poodle.smnavi.map.domain.path.DetailPosition;
import smu.poodle.smnavi.map.domain.path.Edge;
import smu.poodle.smnavi.map.domain.path.FullPath;
import smu.poodle.smnavi.map.domain.path.SubPath;

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
                // 요구사항에 의해 walk 시간이 0이면 반환하지 않도록 처리
                // 만약 요구사항이 변경되어도 DB 에는 저장되기 때문에 DTO 변환만 건들면 됨
                if (subPath.getTransitType() == TransitType.WALK && subPath.getSectionTime() == 0) {
                    continue;
                }

                //엣지를 경유 WaypointDto 로 변환하는 작업
                //dto 에 팩토리메서드패턴을 구현하여 edge 의 정보를 waypoint 와 detailPosition 으로 변환하고
                //변환된 정보를 subPathDto 로 담아 생성함
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
