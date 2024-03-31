package smu.poodle.smnavi.navi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MonitoringBus {
    BUS_7016("100100447", 35, 55);
    private final String busRouteId;
    private final int monitoringStartStationOrder;
    private final int monitoringEndStationOrder;
}
