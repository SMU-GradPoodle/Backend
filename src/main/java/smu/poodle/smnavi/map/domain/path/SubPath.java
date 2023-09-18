package smu.poodle.smnavi.map.domain.path;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import smu.poodle.smnavi.map.domain.mapping.SubPathAndEdge;
import smu.poodle.smnavi.map.domain.station.Waypoint;
import smu.poodle.smnavi.map.enums.BusType;
import smu.poodle.smnavi.map.enums.TransitType;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubPath {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Integer sectionTime; //환승경로별 섹션 소요 시간

    @Enumerated(EnumType.STRING)
    TransitType transitType; //교통 타입 (버스, 지하철, 도보)

    //todo: src, dst 매핑이 필요한가?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "src_id")
    Waypoint src; //서브경로 출발 지점

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dst_id")
    Waypoint dst; //서브경로 도착 지점

    String fromName;

    String toName;

    @Enumerated(EnumType.STRING)
    BusType busType;

    String lineName; //버스 번호 혹은 지하철 호선 이름

    @OneToMany(mappedBy = "subPath")
    List<SubPathAndEdge> edgeInfos;
}
