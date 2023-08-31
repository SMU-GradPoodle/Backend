package smu.poodle.smnavi.map.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import smu.poodle.smnavi.common.domain.BaseTimeEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BusRealTimeLocationInfo extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(unique = true)
    String licensePlate;
    String busName;
    String stationId;
    int stationOrder;

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public void setStationOrder(int stationOrder) {
        this.stationOrder = stationOrder;
    }
}
