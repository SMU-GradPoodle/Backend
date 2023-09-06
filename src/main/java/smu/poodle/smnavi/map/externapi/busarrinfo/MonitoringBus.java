package smu.poodle.smnavi.map.externapi.busarrinfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MonitoringBus {
    BUS_7016("100100447", 36, 57);
    private final String busRouteId;
    private final int monitoringStartStationOrder;
    private final int monitoringEndStationOrder;
}
