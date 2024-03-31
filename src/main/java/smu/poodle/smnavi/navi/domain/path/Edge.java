package smu.poodle.smnavi.navi.domain.path;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import smu.poodle.smnavi.navi.domain.mapping.SubPathAndEdge;
import smu.poodle.smnavi.navi.domain.station.Waypoint;

import java.util.List;


@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "edges")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Edge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "edge_id")
    Integer id;

    Boolean detailExist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "src_id")
    Waypoint src;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dst_id")
    Waypoint dst;

    @OneToMany(mappedBy = "edge")
    List<SubPathAndEdge> routeInfoList;

    @OneToMany(mappedBy = "edge", cascade = CascadeType.ALL)
    List<DetailPosition> detailPositionList;

    public void setDetailExistTrue() {
        this.detailExist = true;
    }
}
