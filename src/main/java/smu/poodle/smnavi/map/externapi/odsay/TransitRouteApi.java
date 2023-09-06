package smu.poodle.smnavi.map.externapi.odsay;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import smu.poodle.smnavi.common.errorcode.ExternApiErrorCode;
import smu.poodle.smnavi.map.domain.data.TransitType;
import smu.poodle.smnavi.map.dto.BusStationDto;
import smu.poodle.smnavi.map.dto.PathDto;
import smu.poodle.smnavi.map.dto.AbstractWaypointDto;
import smu.poodle.smnavi.map.dto.WaypointDto;
import smu.poodle.smnavi.map.externapi.ApiConstantValue;
import smu.poodle.smnavi.map.externapi.ApiKeyValue;
import smu.poodle.smnavi.map.externapi.ApiUtilMethod;
import smu.poodle.smnavi.map.service.manage.PathManageService;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TransitRouteApi {

    private final PathManageService pathManageService;
    private final ApiConstantValue apiConstantValue;


    public List<PathDto.Info> callApiAndSavePathIfNotExist(
            String startPlaceName, String startX, String startY, List<Integer> indexes) {

        WaypointDto.PlaceDto startPlace = WaypointDto.PlaceDto.builder()
                .placeName(startPlaceName)
                .gpsX(startX)
                .gpsY(startY)
                .build();

        String HOST_URL = "https://api.odsay.com/v1/api/searchPubTransPathT";

        JSONObject transitJson = ApiUtilMethod.urlBuildWithJson(HOST_URL,
                ExternApiErrorCode.UNSUPPORTED_OR_INVALID_GPS_POINTS,
                new ApiKeyValue("apiKey", apiConstantValue.getOdsayApiKey()),
                new ApiKeyValue("SX", startX),
                new ApiKeyValue("SY", startY),
                new ApiKeyValue("EX", apiConstantValue.getSMU_X()),
                new ApiKeyValue("EY", apiConstantValue.getSMU_Y()));

        List<PathDto.Info> transitInfoList = parsePathDto(transitJson, indexes);

        for (PathDto.Info path : transitInfoList) {
            pathManageService.savePath(startPlace, path);
        }

        return transitInfoList;
    }

    private List<PathDto.Info> parsePathDto(JSONObject transitJson, List<Integer> indexes) {
        List<PathDto.Info> transitInfoList = new ArrayList<>();

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

                waypointDtoList.add(BusStationDto.builder()
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
