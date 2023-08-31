package smu.poodle.smnavi.map.odsay;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import smu.poodle.smnavi.map.domain.path.DetailPosition;
import smu.poodle.smnavi.map.domain.path.Edge;
import smu.poodle.smnavi.common.errorcode.ExternApiErrorCode;
import smu.poodle.smnavi.map.dto.PathDto;
import smu.poodle.smnavi.map.externapi.ApiConstantValue;
import smu.poodle.smnavi.map.externapi.ApiKeyValue;
import smu.poodle.smnavi.map.externapi.ApiUtilMethod;
import smu.poodle.smnavi.map.externapi.GpsPoint;
import smu.poodle.smnavi.map.repository.DetailPositionRepository;
import smu.poodle.smnavi.map.repository.TransitRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class RouteDetailPositionApi {
    private final ApiConstantValue apiConstantValue;
    private final TransitRepository transitRepository;
    private final DetailPositionRepository detailPositionRepository;

    public void callApiForSaveDetailPositionList(PathDto.SubPathDto subPathDto, String mapObj, List<Edge> edges) {
        boolean detailExist = true;

        for (Edge edge : edges) {
            detailExist = detailExist & edge.getDetailExist();
        }
        if (detailExist) {
            return;
        }

        String HOST_URL = "https://api.odsay.com/v1/api/loadLane";

        JSONObject jsonObject = ApiUtilMethod.urlBuildWithJson(HOST_URL,
                ExternApiErrorCode.UNSUPPORTED_OR_INVALID_GPS_POINTS,
                new ApiKeyValue("apiKey", apiConstantValue.getOdsayApiKey()),
                new ApiKeyValue("mapObject", "0:0@" + mapObj));

        JSONObject info = jsonObject.getJSONObject("result").getJSONArray("lane").getJSONObject(0);
        int transitType = info.getInt("class");
        JSONArray graphPos = info.getJSONArray("section")
                .getJSONObject(0)
                .getJSONArray("graphPos");

        List<List<DetailPosition>> positionLists;
        if (transitType == 1) {
            positionLists = makeBusDetailPositionList(graphPos, edges);
        } else {
            positionLists = makeSubwayDetailPositionList(graphPos, edges);
        }

        List<PathDto.DetailPositionDto> positionListForSubPath = new ArrayList<>();
        for (List<DetailPosition> positionList : positionLists) {
            detailPositionRepository.saveAll(positionList);
            positionListForSubPath.addAll(positionList.stream().map(PathDto.DetailPositionDto::new).toList());
        }

        subPathDto.setGpsDetail(positionListForSubPath);
    }

    private List<List<DetailPosition>> makeBusDetailPositionList(JSONArray posArray, List<Edge> edges) {
        List<List<DetailPosition>> positionLists = new ArrayList<>();

        int posIdx = 0;

        for (Edge edge : edges) {
            List<DetailPosition> detailPositionList = transitRepository.isContainDetailPos(edge);
            boolean isEmpty = detailPositionList.isEmpty();

            while (posIdx < posArray.length()) {
                JSONObject pos = posArray.getJSONObject(posIdx);
                GpsPoint detailPos = new GpsPoint(pos.getBigDecimal("x").toString(), pos.getBigDecimal("y").toString());

                if (isEmpty) {
                    edge.setDetailExistTrue();
                    detailPositionList.add(DetailPosition.builder()
                            .x(detailPos.getGpsX())
                            .y(detailPos.getGpsY())
                            .edge(edge)
                            .build());
                }
                posIdx++;
                if (edge.getDst().getX().equals(detailPos.getGpsX()) && edge.getDst().getY().equals(detailPos.getGpsY())) {
                    break;
                }
            }
            positionLists.add(detailPositionList);
        }
        return positionLists;
    }

    private List<List<DetailPosition>> makeSubwayDetailPositionList(JSONArray posArray, List<Edge> edges) {
        List<List<DetailPosition>> positionLists = new ArrayList<>();

        int posIdx = 0;

        for (Edge edge : edges) {
            List<DetailPosition> detailPositionList = transitRepository.isContainDetailPos(edge);
            if (detailPositionList.isEmpty()) {
                DetailPosition lastDetailPosition = null;
                while (posIdx < posArray.length()) {
                    JSONObject pos = posArray.getJSONObject(posIdx);
                    GpsPoint detailPos = new GpsPoint(pos.getBigDecimal("x").toString(), pos.getBigDecimal("y").toString());
                    edge.setDetailExistTrue();
                    DetailPosition curDetailPosition = DetailPosition.builder()
                            .x(detailPos.getGpsX())
                            .y(detailPos.getGpsY())
                            .edge(edge)
                            .build();
                    if (lastDetailPosition != null &&
                            curDetailPosition.getX().equals(lastDetailPosition.getX()) &&
                            curDetailPosition.getY().equals(lastDetailPosition.getY())) {
                        break;
                    }
                    detailPositionList.add(curDetailPosition);
                    lastDetailPosition = curDetailPosition;
                    posIdx++;
                }
            } else {
                GpsPoint lastDetailPos = null;
                while (posIdx < posArray.length()) {
                    JSONObject pos = posArray.getJSONObject(posIdx);
                    GpsPoint curDetailPos = new GpsPoint(pos.getBigDecimal("x").toString(), pos.getBigDecimal("y").toString());
                    if (lastDetailPos != null &&
                            curDetailPos.getGpsX().equals(lastDetailPos.getGpsX()) &&
                            curDetailPos.getGpsY().equals(lastDetailPos.getGpsY())) {
                        break;
                    }
                    lastDetailPos = curDetailPos;
                    posIdx++;
                }
            }

            positionLists.add(detailPositionList);
        }
        return positionLists;
    }
}
