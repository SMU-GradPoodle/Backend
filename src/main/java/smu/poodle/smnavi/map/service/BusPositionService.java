package smu.poodle.smnavi.map.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.poodle.smnavi.map.domain.station.BusStationInfo;
import smu.poodle.smnavi.map.dto.BusArriveInfoDto;
import smu.poodle.smnavi.map.dto.TestBusPositionDto;
import smu.poodle.smnavi.map.externapi.redis.BusArriveInfoRedisRepository;
import smu.poodle.smnavi.map.externapi.redis.BusPositionLogRedisRepository;
import smu.poodle.smnavi.map.externapi.redis.BusPositionRedisRepository;
import smu.poodle.smnavi.map.externapi.redis.domain.BusArriveInfo;
import smu.poodle.smnavi.map.externapi.redis.domain.BusPosition;
import smu.poodle.smnavi.map.externapi.redis.domain.BusPositionLog;
import smu.poodle.smnavi.map.repository.BusStationInfoRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BusPositionService {
    private final BusPositionRedisRepository busPositionRedisRepository;
    private final BusPositionLogRedisRepository busPositionLogRedisRepository;
    private final BusArriveInfoRedisRepository busArriveInfoRedisRepository;
    private final BusStationInfoRepository busStationInfoRepository;


    public void catchAccidentInfo(List<BusPosition> busPositionList) {
        Iterable<BusPositionLog> busPositionLogIterable = busPositionLogRedisRepository.findAll();

        Map<String, BusPositionLog> busPositionLogMap = new HashMap<>();
        for (BusPositionLog log : busPositionLogIterable) {
            busPositionLogMap.put(log.getLicensePlate(), log);
        }

        for (BusPosition busPosition : busPositionList) {
            BusPositionLog cachedbusPositionLog = busPositionLogMap.getOrDefault(busPosition.getLicensePlate(), null);
            if (cachedbusPositionLog != null &&
                    cachedbusPositionLog.getSectionOrder() < 55 &&
                    cachedbusPositionLog.getSectionOrder().equals(busPosition.getSectionOrder())) {
                log.info("이슈 발견");
                busPosition.setHasIssue(true);
            }
        }

        busPositionLogRedisRepository.deleteAll();
        busPositionLogRedisRepository.saveAll(BusPositionLog.convertBusPositionList(busPositionList));
    }

    @Transactional
    public List<BusPosition> getBusPositionList() {
        return (List<BusPosition>) busPositionRedisRepository.findAll();
    }

    public List<TestBusPositionDto> getTestBusPosition() {
        List<TestBusPositionDto> busPositions = new ArrayList<>();

        // 더미 데이터 생성
        busPositions.add(new TestBusPositionDto("서울70사7744", "126.977167", "37.569005", false, null));
        busPositions.add(new TestBusPositionDto("서울70사7785", "126.972283", "37.577671", true, "KT광화문지사, 버스 지연"));
        busPositions.add(new TestBusPositionDto("서울70사7749", "126.957672", "37.599789", false, null));
        busPositions.add(new TestBusPositionDto("서울70사7797", "126.957371", "37.599717", false, null));
        busPositions.add(new TestBusPositionDto("서울70사7781", "126.956677", "37.600947", false, null));
        busPositions.add(new TestBusPositionDto("서울70사7773", "126.972338", "37.541974", false, null));

        return busPositions;
    }

    public List<BusArriveInfoDto> getBusArriveInfo() {
        //캐싱
        List<BusStationInfo> busStationInfoList = busStationInfoRepository.findByBusName("7016");
        Iterable<BusArriveInfo> busArriveInfoList = busArriveInfoRedisRepository.findAll();

        Map<String, BusStationInfo> busStationInfoMap = busStationInfoList.stream()
                .collect(Collectors.toMap(BusStationInfo::getStationId, busStationInfo -> busStationInfo));

        List<BusArriveInfoDto> busArriveInfoDtoList = new ArrayList<>();
        for (BusArriveInfo busArriveInfo : busArriveInfoList) {
            BusStationInfo busStationInfo = busStationInfoMap.get(busArriveInfo.getStationId());
            busArriveInfoDtoList.add(BusArriveInfoDto.generateDto(busArriveInfo, busStationInfo));
        }

        return busArriveInfoDtoList;
    }
}
