package smu.poodle.smnavi.map.callapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import smu.poodle.smnavi.map.enums.MonitoringBus;
import smu.poodle.smnavi.common.util.XmlApiUtil;
import smu.poodle.smnavi.map.redis.repository.BusArriveInfoRedisRepository;
import smu.poodle.smnavi.map.redis.hash.BusArriveInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * API REF 페이지
 * https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15000314
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class BusArriveInfoApi {

    private final BusArriveInfoRedisRepository busArriveInfoRedisRepository;
    @Value("${PUBLIC_DATA_API_KEY}")
    private String SERVICE_KEY;

    @Scheduled(cron = "0 0/1 6-20 * * *")
    public List<BusArriveInfo> parseDtoFromXml() {
        System.out.println("실행 돼요");
        Document xmlContent = XmlApiUtil.getRootTag(makeUrl(MonitoringBus.BUS_7016.getBusRouteId()));
        Element msgBody = (Element) xmlContent.getElementsByTagName("msgBody").item(0);

        NodeList itemList = msgBody.getElementsByTagName("itemList");

        List<BusArriveInfo> busArriveInfoList = new ArrayList<>();

        for (int i = MonitoringBus.BUS_7016.getMonitoringStartStationOrder(); i < MonitoringBus.BUS_7016.getMonitoringEndStationOrder(); i++) {
            Node itemNode = itemList.item(i);
            if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                Element itemElement = (Element) itemNode;

                String firstArrivalMessage = itemElement.getElementsByTagName("arrmsg1").item(0).getTextContent();
                String secondArrivalMessage = itemElement.getElementsByTagName("arrmsg2").item(0).getTextContent();

                int stationId = Integer.parseInt(itemElement.getElementsByTagName("stId").item(0).getTextContent());

                boolean isStationNonStop = itemElement.getElementsByTagName("deTourAt").item(0).getTextContent().equals("11");

                boolean isLargeInterval = parseSecondsFromString(firstArrivalMessage) >= 15 * 60;
                busArriveInfoList.add(BusArriveInfo.builder()
                        .stationId(String.valueOf(stationId))
                        .firstArriveMessage(firstArrivalMessage)
                        .secondArriveMessage(secondArrivalMessage)
                        .isNonstop(isStationNonStop)
                        .isLargeInterval(isLargeInterval)
                        .hasIssue(isStationNonStop || isLargeInterval)
                        .build());
            }
        }

        busArriveInfoRedisRepository.saveAll(busArriveInfoList);
        return busArriveInfoList;
    }

    private String makeUrl(String busRouteId) {

        final String API_BASE_URL = "http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRouteAll";

        return API_BASE_URL + "?"
                + "serviceKey=" + SERVICE_KEY + "&"
                + "busRouteId=" + busRouteId;
    }

    @Scheduled(cron = "* 5 21 * * *")
    public void deleteBusArriveCache() {
        busArriveInfoRedisRepository.deleteAll();
    }

    public int parseSecondsFromString(String arriveMessage) {
        if (isArrivingSoon(arriveMessage))
            return 60;

        int minutes = 0, seconds = 0;

        List<Integer> extractedNumber = extractNumbers(arriveMessage);

        if (extractedNumber.size() == 2) {
            minutes = extractedNumber.get(0);
        } else if (extractedNumber.size() == 3) {
            minutes = extractedNumber.get(0);
            seconds = extractedNumber.get(1);
        }

        return (minutes * 60) + seconds;
    }

    public boolean isArrivingSoon(String arriveMessage) {
        return arriveMessage.equals("곧 도착");
    }


    public List<Integer> extractNumbers(String arriveMessage) {
        List<Integer> numbers = new ArrayList<>();

        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(arriveMessage);

        while (matcher.find()) {
            String numberStr = matcher.group();
            int number = Integer.parseInt(numberStr);
            numbers.add(number);
        }

        return numbers;
    }
}
