package smu.poodle.smnavi.map.externapi;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
//todo: 이렇게 사용하지 말자..
@Getter
@Component
public class ApiConstantValue {
    @Value("${SMU-X}")
    private String SMU_X;
    @Value("${SMU-Y}")
    private String SMU_Y;
    @Value("${SEOUL-DATA-API-KEY}")
    private String seoulDataApiKey;
    @Value("${ODSAY-API-KEY}")
    private String odsayApiKey;
    @Value("${GPS-CONVERT-API-KEY}")
    private String gpsConvertApiKey;
}
