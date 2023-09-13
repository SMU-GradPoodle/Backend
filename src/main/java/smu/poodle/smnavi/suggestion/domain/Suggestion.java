package smu.poodle.smnavi.suggestion.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import smu.poodle.smnavi.common.domain.BaseTimeEntity;


@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Suggestion extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;
}
