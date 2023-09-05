package smu.poodle.smnavi.map.externapi.busarrinfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import smu.poodle.smnavi.map.dto.BusArriveInfoDto;
import smu.poodle.smnavi.map.externapi.util.XmlApiUtil;
import smu.poodle.smnavi.map.service.BusRealTimeLocateService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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
    private final XmlApiUtil xmlApiUtil;
    private final BusRealTimeLocateService busRealTimeLocateService;

    private final String BUS_ROUTE_ID_7016 = "100100447";
    private final int START_OF_CAUTION_STATION_ORDER_7016 = 36; //용산e편한세상?
    private final int END_OF_CAUTION_STATION_ORDER_7016 = 50; //효자동

    private String makeUrl(String busRouteId) {

        final String API_BASE_URL = "http://ws.bus.go.kr/api/rest/arrive/getArrInfoByRouteAll";

        final String SERVICE_KEY = "bFcIfbKjGI8rVFG9xZouBt%2B3s0kITpf0u6Loz8ekrvseXj%2Bye16tUmvGrBgLdK5zbVA3cAanmNPa%2F1o%2B2n2feQ%3D%3D";

        return API_BASE_URL + "?"
                + "serviceKey=" + SERVICE_KEY + "&"
                + "busRouteId=" + busRouteId;
    }

    public List<BusArriveInfoDto> parseDtoFromXml() {
        Document xmlContent = xmlApiUtil.getRootTag(makeUrl(BUS_ROUTE_ID_7016));
        Element msgBody = (Element) xmlContent.getElementsByTagName("msgBody").item(0);

        NodeList itemList = msgBody.getElementsByTagName("itemList");

        List<BusArriveInfoDto> busArriveInfoDtoList = new ArrayList<>();

        for (int i = START_OF_CAUTION_STATION_ORDER_7016; i < END_OF_CAUTION_STATION_ORDER_7016; i++) {
            Node itemNode = itemList.item(i);
            if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                Element itemElement = (Element) itemNode;

                String firstArrivalMessage = itemElement.getElementsByTagName("arrmsg1").item(0).getTextContent();
                String secondArrivalMessage = itemElement.getElementsByTagName("arrmsg2").item(0).getTextContent();

                String firstArrivalLicensePlate = itemElement.getElementsByTagName("plainNo1").item(0).getTextContent();
                String secondArrivalLicensePlate = itemElement.getElementsByTagName("plainNo2").item(0).getTextContent();

                String firstArrivalNextStationId = itemElement.getElementsByTagName("nstnId1").item(0).getTextContent();
                String secondArrivalNextStationId = itemElement.getElementsByTagName("nstnId2").item(0).getTextContent();

                log.info("첫 번째 도착 메시지: {}", firstArrivalMessage);
                log.info("두 번째 도착 메시지: {}", secondArrivalMessage);

                log.info("첫 번째 도착 차량 번호판: {}", firstArrivalLicensePlate);
                log.info("두 번째 도착 차량 번호판: {}", secondArrivalLicensePlate);

                log.info("첫 번째 도착 다음 정류장 ID: {}", firstArrivalNextStationId);
                log.info("두 번째 도착 다음 정류장 ID: {}", secondArrivalNextStationId);

                int stationId = Integer.parseInt(itemElement.getElementsByTagName("stId").item(0).getTextContent());

                boolean isStationNonStop = itemElement.getElementsByTagName("deTourAt").item(0).getTextContent().equals("11");

                busArriveInfoDtoList.add(BusArriveInfoDto.builder()
                        .stationId(stationId)
                        .firstArrivalSeconds(parseTimeString(firstArrivalMessage))
                        .secondArrivalSeconds(parseTimeString(secondArrivalMessage))
                        .firstArrivalLicensePlate(firstArrivalLicensePlate)
                        .secondArrivalLicensePlate(secondArrivalLicensePlate)
                        .firstArrivalNextStationId(firstArrivalNextStationId)
                        .secondArrivalNextStationId(secondArrivalNextStationId)
                        .firstArrivalStationOrder(calculateStationOrder(firstArrivalMessage, i))
                        .secondArrivalStationOrder(calculateStationOrder(secondArrivalMessage, i))
                        .isStationNonStop(isStationNonStop)
                        .stationOrder(i)
                        .build());
            }
        }

        return busArriveInfoDtoList;
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

    public int parseTimeString(String arriveMessage) {
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

    public int calculateStationOrder(String arriveMessage, int stationOrder) {
        if (isArrivingSoon(arriveMessage))
            return stationOrder;

        int diffOrder = 0;
        List<Integer> extractedNumber = extractNumbers(arriveMessage);

        if (extractedNumber.size() == 2) {
            diffOrder = extractedNumber.get(1);
        } else if (extractedNumber.size() == 3) {
            diffOrder = extractedNumber.get(2);
        }

        return stationOrder - diffOrder;
    }

    public List<AccidentData> getTrafficIssue() {
        List<BusArriveInfoDto> busArriveInfoDtoList = parseDtoFromXml();
        busRealTimeLocateService.checkTrafficErrorByBusMovement(busArriveInfoDtoList);

        List<AccidentData> accidentDataList = new ArrayList<>();
        accidentDataList.add(isSpacingTooLarge(busArriveInfoDtoList));
        accidentDataList.add(isSpacingTooNarrow(busArriveInfoDtoList));
        accidentDataList.add(isNonStop(busArriveInfoDtoList));

        return accidentDataList;
    }

    public AccidentData isSpacingTooLarge(List<BusArriveInfoDto> busArriveInfoDtoList) {
        for (BusArriveInfoDto busArriveInfoDto : busArriveInfoDtoList) {
            int intervalSecond = busArriveInfoDto.getSecondArrivalSeconds() - busArriveInfoDto.getFirstArrivalSeconds();
            System.out.println(intervalSecond);
            if (intervalSecond >= 1200) {
                return AccidentData.builder()
                        .stationId(busArriveInfoDto.getStationId())
                        .message("배차간격이 " + intervalSecond / 60 + "분 이상입니다.")
                        .build();
            }
        }
        return null;
    }

    public AccidentData isSpacingTooNarrow(List<BusArriveInfoDto> busArriveInfoDtoList) {
        List<Integer> busLocatedStationOrderList = createBusLocatedStationOrderList(busArriveInfoDtoList);

        Queue<Integer> busQueue = new LinkedList<>();

        for (Integer busLocatedStationOrder : busLocatedStationOrderList) {
            busQueue.offer(busLocatedStationOrder);

            while (!busQueue.isEmpty() && busLocatedStationOrder - busQueue.peek() > 2) {
                busQueue.poll();
            }

            if (busQueue.size() >= 3) {
                for (BusArriveInfoDto busArriveInfoDto : busArriveInfoDtoList) {
                    if (busArriveInfoDto.getStationOrder() == busLocatedStationOrder) {
                        return AccidentData.builder()
                                .stationId(busArriveInfoDto.getStationId())
                                .message("해당 정류장 근처에서 통행 이상이 있습니다.")
                                .build();
                    }
                }
            }
        }
        return null;
    }

    public AccidentData isNonStop(List<BusArriveInfoDto> busArriveInfoDtoList) {
        for (BusArriveInfoDto busArriveInfoDto : busArriveInfoDtoList) {
            if (busArriveInfoDto.isStationNonStop())
                return AccidentData.builder()
                        .stationId(busArriveInfoDto.getStationId())
                        .message("해당 정류장에서 우회중입니다.")
                        .build();
        }
        return null;
    }

    public List<Integer> createBusLocatedStationOrderList(List<BusArriveInfoDto> busArriveInfoDtoList) {
        List<Integer> busLocatedStationOrderList = new ArrayList<>();

        busLocatedStationOrderList.add(busArriveInfoDtoList.get(0).getSecondArrivalStationOrder());
        busLocatedStationOrderList.add(busArriveInfoDtoList.get(0).getFirstArrivalStationOrder());

        for (int i = 1; i < busArriveInfoDtoList.size(); i++) {
            BusArriveInfoDto curBusStation = busArriveInfoDtoList.get(i);

            int firstArrivalStationOrder = curBusStation.getFirstArrivalStationOrder();
            int secondArrivalStationOrder = curBusStation.getSecondArrivalStationOrder();

            if (firstArrivalStationOrder == curBusStation.getStationOrder()) {
                busLocatedStationOrderList.add(firstArrivalStationOrder);
                if (secondArrivalStationOrder == curBusStation.getStationOrder()) {
                    busLocatedStationOrderList.add(secondArrivalStationOrder);
                }
            }
        }

        return busLocatedStationOrderList;
    }
}
