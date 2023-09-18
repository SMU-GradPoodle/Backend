package smu.poodle.smnavi.common.util;

import lombok.experimental.UtilityClass;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import smu.poodle.smnavi.common.errorcode.CommonErrorCode;
import smu.poodle.smnavi.common.exception.RestApiException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@UtilityClass
public class XmlApiUtil {
    public Document getRootTag(String url) {
        try {
            DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();
            Document doc = dBuilder.parse(url);

            doc.getDocumentElement().normalize();

            return doc;
        }
        catch (ParserConfigurationException | SAXException | IOException e) {
            //todo : 에러코드 반환 형식을 좀 더 이해하기 쉽게 바꾸기
            throw new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND);
        }
    }
}
