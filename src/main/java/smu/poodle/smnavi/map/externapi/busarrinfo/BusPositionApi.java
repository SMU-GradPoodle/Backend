package smu.poodle.smnavi.map.externapi.busarrinfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import smu.poodle.smnavi.map.externapi.util.XmlApiUtil;
import smu.poodle.smnavi.map.redis.BusPositionLogRedisRepository;
import smu.poodle.smnavi.map.redis.BusPositionRepository;
import smu.poodle.smnavi.map.redis.domain.BusPosition;
import smu.poodle.smnavi.map.redis.domain.BusPositionLog;
import smu.poodle.smnavi.map.service.BusPositionService;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BusPositionApi {

    private final BusPositionRepository busPositionRepository;
    private final BusPositionLogRedisRepository busPositionLogRedisRepository;
    private final BusPositionService busPositionService;

    private String getUrl(MonitoringBus monitoringBus) {
        final String URL = "http://ws.bus.go.kr/api/rest/buspos/getBusPosByRouteSt?ServiceKey=%s&busRouteId=%s&startOrd=%d&endOrd=%d";
        final String SERVICE_KEY = "bFcIfbKjGI8rVFG9xZouBt%2B3s0kITpf0u6Loz8ekrvseXj%2Bye16tUmvGrBgLdK5zbVA3cAanmNPa%2F1o%2B2n2feQ%3D%3D";

        return String.format(
                URL,
                SERVICE_KEY,
                monitoringBus.getBusRouteId(),
                monitoringBus.getMonitoringStartStationOrder(),
                monitoringBus.getMonitoringEndStationOrder());
    }

    @Transactional
    @Scheduled(cron = "0 0/5 9-23 * * *")
    public void cachingBusPosition() {
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
                    .build());
        }

        ZonedDateTime now = ZonedDateTime.now();
        int minutesSinceSevenAM = now.getHour() * 60 + now.getMinute() - 7 * 60;
        if (minutesSinceSevenAM % 8 == 0) {
            busPositionService.catchAccidentInfo();
            log.info(now.format(DateTimeFormatter.ofPattern("MM-dd HH:mm:ss")) + " 버스 교통 이슈 확인");
            busPositionLogRedisRepository.saveAll(BusPositionLog.convertBusPositionList(busPositionList));
        }

        busPositionRepository.deleteAll();
        busPositionRepository.saveAll(busPositionList);
    }
}
