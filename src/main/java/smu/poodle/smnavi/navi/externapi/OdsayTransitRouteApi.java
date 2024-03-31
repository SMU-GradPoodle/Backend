package smu.poodle.smnavi.navi.externapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import smu.poodle.smnavi.navi.dto.ApiKeyValueDto;
import smu.poodle.smnavi.navi.exception.ExternApiStatusCode;
import smu.poodle.smnavi.navi.util.JsonApiUtil;
import smu.poodle.smnavi.navi.enums.TransitType;
import smu.poodle.smnavi.navi.dto.PathDto;
import smu.poodle.smnavi.navi.dto.AbstractWaypointDto;
import smu.poodle.smnavi.navi.dto.WaypointDto;
import smu.poodle.smnavi.navi.service.admin.AdminNaviService;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OdsayTransitRouteApi {

    private final AdminNaviService adminNaviService;
    private static final String SMU_X = "126.955252";
    private static final String SMU_Y = "37.602638";
    
    @Value("${ODSAY-API-KEY}")
    private String ODSAY_API_KEY;

    public void callApiAndSavePathIfNotExist(
            String startPlaceName, String startX, String startY, List<Integer> indexes) {

        WaypointDto.PlaceDto startPlace = WaypointDto.PlaceDto.builder()
                .placeName(startPlaceName)
                .gpsX(startX)
                .gpsY(startY)
                .build();

        String HOST_URL = "https://api.odsay.com/v1/api/searchPubTransPathT";

        JSONObject transitJson = JsonApiUtil.urlBuildWithJson(HOST_URL,
                ExternApiStatusCode.UNSUPPORTED_OR_INVALID_GPS_POINTS,
                new ApiKeyValueDto("apiKey", ODSAY_API_KEY),
                new ApiKeyValueDto("SX", startX),
                new ApiKeyValueDto("SY", startY),
                new ApiKeyValueDto("EX", SMU_X),
                new ApiKeyValueDto("EY", SMU_Y));

        List<PathDto.Info> transitInfoList = parsePathDto(transitJson, indexes);

        for (PathDto.Info path : transitInfoList) {
            adminNaviService.savePath(startPlace, path);
        }
    }

    private List<PathDto.Info> parsePathDto(JSONObject transitJson, List<Integer> indexes) {
        List<PathDto.Info> transitInfoList = new ArrayList<>();

        log.info(transitJson.toString());
        JSONArray pathList = transitJson.getJSONObject("result").getJSONArray("path");

        for (Integer i : indexes) {
            JSONObject path = pathList.getJSONObject(i);
            JSONObject pathInfo = path.getJSONObject("info");

            int totalTime = pathInfo.getInt("totalTime");

            String mapObj = pathInfo.getString("mapObj");

            List<PathDto.SubPathDto> subPathDtoList = makeSubPathDtoList(path);

            transitInfoList.add(PathDto.Info.builder()
                    .subPathList(subPathDtoList)
                    .time(totalTime)
                    .mapObj(mapObj)
                    .build());
        }
        return transitInfoList;
    }

    private List<PathDto.SubPathDto> makeSubPathDtoList(JSONObject path) {
        List<PathDto.SubPathDto> subPathDtoList = new ArrayList<>();

        JSONArray subPathList = path.getJSONArray("subPath");

        for (int i = 0; i < subPathList.length(); i++) {
            JSONObject subPathJson = subPathList.getJSONObject(i);

            String laneName = null;
            String from = null;
            String to = null;
            List<AbstractWaypointDto> waypointDtoList = new ArrayList<>();

            int trafficType = subPathJson.getInt("trafficType");
            TransitType type = TransitType.of(trafficType);
            int sectionTime = subPathJson.getInt("sectionTime");
            int busTypeInt = 0;

            if (type == TransitType.WALK) {
                if (i == 0) {
                    subPathDtoList.add(PathDto.SubPathDto.builder()
                            .sectionTime(sectionTime)
                            .build());
                    continue;
                }
            } else {
                from = subPathJson.getString("startName");
                to = subPathJson.getString("endName");

                JSONObject lane = subPathJson.getJSONArray("lane").getJSONObject(0);

                //todo: switch-case 문으로 바꾸자
                if (type == TransitType.BUS) {
                    laneName = lane.getString("busNo");
                    busTypeInt = lane.getInt("type");
                } else if (type == TransitType.SUBWAY) {
                    laneName = String.valueOf(lane.getInt("subwayCode"));
                }
                waypointDtoList = makeStationDtoList(subPathJson, type);
            }

            subPathDtoList.add(PathDto.SubPathDto.builder()
                    .transitType(type)
                    .sectionTime(sectionTime)
                    .from(from)
                    .to(to)
                    .busTypeInt(busTypeInt)
                    .lineName(laneName)
                    .stationList(waypointDtoList)
                    .build());

        }
        return subPathDtoList;
    }


    private List<AbstractWaypointDto> makeStationDtoList(JSONObject subPath, TransitType type) {
        List<AbstractWaypointDto> waypointDtoList = new ArrayList<>();

        JSONArray stationList = subPath.getJSONObject("passStopList").getJSONArray("stations");

        for (int i = 0; i < stationList.length(); i++) {
            JSONObject station = stationList.getJSONObject(i);

            String stationName;

            String x = station.getString("x");
            String y = station.getString("y");
            stationName = station.getString("stationName");


            if (type == TransitType.BUS) {
                String stationId = station.getString("localStationID");

                waypointDtoList.add(WaypointDto.BusStationDto.builder()
                        .localStationId(stationId)
                        .stationName(stationName)
                        .gpsX(x)
                        .gpsY(y)
                        .build());

            } else if (type == TransitType.SUBWAY) {
                int stationId = station.getInt("stationID");

                waypointDtoList.add(WaypointDto.SubwayStationDto.builder()
                        .stationId(stationId)
                        .stationName(stationName)
                        .gpsX(x)
                        .gpsY(y)
                        .build());
            }


        }
        return waypointDtoList;
    }
}
