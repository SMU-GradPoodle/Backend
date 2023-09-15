package smu.poodle.smnavi.tipoff.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import smu.poodle.smnavi.common.domain.BaseTimeEntity;
import smu.poodle.smnavi.map.domain.data.TransitType;
import smu.poodle.smnavi.map.domain.station.Waypoint;
import smu.poodle.smnavi.user.domain.UserEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
//todo : 테이블 이름 바꾸기
@Table(name = "info_entity")
public class TipOff extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    UserEntity author;

    String content;

    Kind kind;

    @Enumerated(EnumType.STRING)
    Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    Waypoint waypoint;

    @Enumerated(EnumType.STRING)
    TransitType transitType;

    @OneToMany(mappedBy = "tipOff", cascade = CascadeType.REMOVE)
    List<Thumb> thumbList = new ArrayList<>();

}
