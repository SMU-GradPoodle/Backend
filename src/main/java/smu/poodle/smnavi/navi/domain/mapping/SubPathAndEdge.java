package smu.poodle.smnavi.navi.domain.mapping;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.poodle.smnavi.navi.domain.path.Edge;
import smu.poodle.smnavi.navi.domain.path.SubPath;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sub_path_edge")
public class SubPathAndEdge {
    @Id
    @GeneratedValue
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    Edge edge;

    @ManyToOne(fetch = FetchType.LAZY)
    SubPath subPath;
}
