package smu.poodle.smnavi.navi.domain.path;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import smu.poodle.smnavi.navi.domain.mapping.FullPathAndSubPath;
import smu.poodle.smnavi.navi.domain.station.Waypoint;

import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FullPath {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Integer totalTime;

    Boolean isSeen;

    @OneToMany(mappedBy = "fullPath")
    List<FullPathAndSubPath> subPaths;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "start_waypoint_id")
    Waypoint startWaypoint;

    public void updateIsSeen() {
        this.isSeen = !this.isSeen;
    }
}
