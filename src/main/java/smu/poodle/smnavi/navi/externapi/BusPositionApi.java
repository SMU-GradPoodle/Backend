package smu.poodle.smnavi.navi.externapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import smu.poodle.smnavi.navi.enums.MonitoringBus;
import smu.poodle.smnavi.navi.util.XmlApiUtil;
import smu.poodle.smnavi.navi.redisdomain.BusPosition;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BusPositionApi {

    @Value("${PUBLIC_DATA_API_KEY}")
    private String SERVICE_KEY;

    private String getUrl(MonitoringBus monitoringBus) {
        final String URL = "http://ws.bus.go.kr/api/rest/buspos/getBusPosByRouteSt?ServiceKey=%s&busRouteId=%s&startOrd=%d&endOrd=%d";

        return String.format(
                URL,
                SERVICE_KEY,
                monitoringBus.getBusRouteId(),
                monitoringBus.getMonitoringStartStationOrder(),
                monitoringBus.getMonitoringEndStationOrder());
    }

    public List<BusPosition> callApi() {
        Document xmlContent = XmlApiUtil.getRootTag(getUrl(MonitoringBus.BUS_7016));
        Element msgBody = (Element) xmlContent.getElementsByTagName("msgBody").item(0);

        NodeList itemList = msgBody.getElementsByTagName("itemList");

        List<BusPosition> busPositionList = new ArrayList<>();

        for (int i = 0; i < itemList.getLength(); i++) {
            Element element = (Element) itemList.item(i);

            String licensePlate = element.getElementsByTagName("plainNo").item(0).getTextContent();
            String gpsX = element.getElementsByTagName("tmX").item(0).getTextContent();
            String gpsY = element.getElementsByTagName("tmY").item(0).getTextContent();
            int sectionOrder = Integer.parseInt(element.getElementsByTagName("sectOrd").item(0).getTextContent());

            busPositionList.add(BusPosition.builder()
                    .licensePlate(licensePlate)
                    .sectionOrder(sectionOrder)
                    .gpsX(gpsX)
                    .gpsY(gpsY)
                    .hasIssue(false)
                    .build());
        }

        return busPositionList;
    }
}
