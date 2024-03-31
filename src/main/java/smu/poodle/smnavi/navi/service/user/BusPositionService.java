package smu.poodle.smnavi.navi.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.poodle.smnavi.navi.domain.station.BusStationInfo;
import smu.poodle.smnavi.navi.dto.BusArriveInfoDto;
import smu.poodle.smnavi.navi.redisrepository.BusArriveInfoRedisRepository;
import smu.poodle.smnavi.navi.redisrepository.BusPositionLogRedisRepository;
import smu.poodle.smnavi.navi.redisrepository.BusPositionRedisRepository;
import smu.poodle.smnavi.navi.redisdomain.BusArriveInfo;
import smu.poodle.smnavi.navi.redisdomain.BusPosition;
import smu.poodle.smnavi.navi.redisdomain.BusPositionLog;
import smu.poodle.smnavi.navi.repository.BusStationInfoRepository;
import smu.poodle.smnavi.navi.repository.BusStationRepository;
import smu.poodle.smnavi.navi.repository.SubPathRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BusPositionService {
    private final BusPositionRedisRepository busPositionRedisRepository;
    private final BusArriveInfoRedisRepository busArriveInfoRedisRepository;
    private final BusStationInfoRepository busStationInfoRepository;

    public List<BusPosition> getBusPositionList() {
        return (List<BusPosition>) busPositionRedisRepository.findAll();
    }

    public List<BusArriveInfoDto> getBusArriveInfo() {
        List<BusStationInfo> busStationInfoList = busStationInfoRepository.findByBusName("7016");
        List<BusArriveInfo> busArriveInfoList = busArriveInfoRedisRepository.findAll();

        Map<String, BusArriveInfo> busArriveInfoMap = busArriveInfoList.stream()
                .collect(Collectors.toMap(BusArriveInfo::getStationId, busArriveInfo -> busArriveInfo));


        List<BusArriveInfoDto> busArriveInfoDtoList = new ArrayList<>();

        for (BusStationInfo busStationInfo : busStationInfoList) {
            BusArriveInfo busArriveInfo = busArriveInfoMap.getOrDefault(busStationInfo.getStationId(),BusArriveInfo.getDefaultInstance());
            busArriveInfoDtoList.add(BusArriveInfoDto.generateDto(busArriveInfo, busStationInfo));
        }

        return busArriveInfoDtoList;
    }
}
