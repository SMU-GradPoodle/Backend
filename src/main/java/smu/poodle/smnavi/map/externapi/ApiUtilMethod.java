package smu.poodle.smnavi.map.externapi;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import smu.poodle.smnavi.common.errorcode.CommonErrorCode;
import smu.poodle.smnavi.common.errorcode.ErrorCode;
import smu.poodle.smnavi.common.exception.RestApiException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiUtilMethod {
    public static JSONObject urlBuildWithJson(final String hostUrl, ErrorCode errorCode, ApiKeyValue ...params){
        StringBuilder sb = new StringBuilder();
        sb.append(hostUrl + "?");

        int i;
        for (i = 0; i < params.length - 1; i++) {
            sb.append(params[i].getKey() + "=" + params[i].getValue() + "&");
        }

        sb.append(params[i].getKey() + "=" + params[i].getValue());

        try {
            URL url = new URL(sb.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                throw new RestApiException(errorCode);
            }
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                jsonBuilder.append(line);
            }
            //중간에서 예외터져서 여기서 못닫으면??
            rd.close();
            conn.disconnect();
            return new JSONObject(jsonBuilder.toString());
        }
        catch (Exception e){
            throw new RestApiException(errorCode);
        }
    }

    public static String getTagValue(String tag, Element eElement) {

        //결과를 저장할 result 변수 선언
        String result = "";

        NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();

        result = nlList.item(0).getTextContent();

        return result;
    }

    // tag값의 정보를 가져오는 함수
    public static String getTagValue(String tag, String childTag, Element eElement) {

        //결과를 저장할 result 변수 선언
        String result = "";

        NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();

        for(int i = 0; i < eElement.getElementsByTagName(childTag).getLength(); i++) {

            //result += nlList.item(i).getFirstChild().getTextContent() + " ";
            result += nlList.item(i).getChildNodes().item(0).getTextContent() + " ";
        }

        return result;
    }

    public static double calculateDistance(GpsPoint gpsPoint1, GpsPoint gpsPoint2) {
        double lat1 = Double.parseDouble(gpsPoint1.getGpsX());
        double lon1 = Double.parseDouble(gpsPoint1.getGpsY());
        double lat2 = Double.parseDouble(gpsPoint2.getGpsX());
        double lon2 = Double.parseDouble(gpsPoint2.getGpsY());
        int earthRadius = 6371000; // 지구 반지름 (미터)

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lat1) *
                        Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));

        return earthRadius * c;
    }

    public static Document callXmlApi(String url){
        try {
            DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();
            Document xmlContent = dBuilder.parse(url);

            xmlContent.getDocumentElement().normalize();

            return xmlContent;
        }
        catch (Exception e){
            throw new RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR);
        }

    }
}
