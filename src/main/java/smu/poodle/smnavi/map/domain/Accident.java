package smu.poodle.smnavi.map.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;
import smu.poodle.smnavi.common.domain.BaseTimeEntity;
import smu.poodle.smnavi.map.domain.station.Waypoint;
import smu.poodle.smnavi.tipoff.domain.Kind;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
//todo : 배포 전에 풀어야함
//@Where(clause = "created_at > date_sub(now(), interval 1 hour)")
@Getter
public class Accident extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Kind kind;

    private String message;

    @ManyToOne
    private Waypoint waypoint;
}
