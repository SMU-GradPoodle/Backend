package smu.poodle.smnavi.navi.domain.path;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import smu.poodle.smnavi.navi.domain.mapping.SubPathAndEdge;
import smu.poodle.smnavi.navi.domain.station.Waypoint;
import smu.poodle.smnavi.navi.enums.BusType;
import smu.poodle.smnavi.navi.enums.TransitType;

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

    Integer sectionTime;

    @Enumerated(EnumType.STRING)
    TransitType transitType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "src_id")
    Waypoint src;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dst_id")
    Waypoint dst;

    String fromName;

    String toName;

    @Enumerated(EnumType.STRING)
    BusType busType;

    String lineName;

    @OneToMany(mappedBy = "subPath")
    List<SubPathAndEdge> edgeInfos;
}
