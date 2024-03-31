package smu.poodle.smnavi.navi.domain.path;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "detail_positions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DetailPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_position_id")
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    Edge edge;

    String x;
    String y;
}
