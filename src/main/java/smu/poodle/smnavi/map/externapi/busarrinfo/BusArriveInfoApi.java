package smu.poodle.smnavi.map.externapi.busarrinfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import smu.poodle.smnavi.map.dto.BusArriveInfoDto;
import smu.poodle.smnavi.map.externapi.util.XmlApiUtil;

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
        Document xmlContent = XmlApiUtil.getRootTag(makeUrl(BUS_ROUTE_ID_7016));
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

                int stationId = Integer.parseInt(itemElement.getElementsByTagName("stId").item(0).getTextContent());

                String stationName = itemElement.getElementsByTagName("stNm").item(0).getTextContent();
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
                        .stationName(stationName)
                        .isStationNonStop(isStationNonStop)
                        .stationOrder(i)
                        .build());
            }
        }

        return busArriveInfoDtoList;
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
}
