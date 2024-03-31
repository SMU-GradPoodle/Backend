package smu.poodle.smnavi.navi.domain.mapping;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import smu.poodle.smnavi.navi.domain.path.FullPath;
import smu.poodle.smnavi.navi.domain.path.SubPath;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FullPathAndSubPath {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_path_id")
    SubPath subPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "full_path_id")
    FullPath fullPath;
}
