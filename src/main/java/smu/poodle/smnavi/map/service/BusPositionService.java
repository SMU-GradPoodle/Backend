package smu.poodle.smnavi.map.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.poodle.smnavi.map.domain.station.BusStationInfo;
import smu.poodle.smnavi.map.dto.BusArriveInfoDto;
import smu.poodle.smnavi.map.externapi.redis.BusArriveInfoRedisRepository;
import smu.poodle.smnavi.map.externapi.redis.BusPositionLogRedisRepository;
import smu.poodle.smnavi.map.externapi.redis.BusPositionRedisRepository;
import smu.poodle.smnavi.map.externapi.redis.domain.BusArriveInfo;
import smu.poodle.smnavi.map.externapi.redis.domain.BusPosition;
import smu.poodle.smnavi.map.externapi.redis.domain.BusPositionLog;
import smu.poodle.smnavi.map.repository.BusStationInfoRepository;

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

    public List<BusPosition> getTestBusPosition() {
        List<BusPosition> busPositions = new ArrayList<>();

        // 더미 데이터 생성
        busPositions.add(new BusPosition("서울70사7779", null, "126.963558", "37.53648", false));
        busPositions.add(new BusPosition("서울70사7746", null, "126.968963", "37.540258", false));
        busPositions.add(new BusPosition("서울70사7768", null, "126.971971", "37.541912", false));
        busPositions.add(new BusPosition("서울74사7254", null, "126.972538", "37.543296", true));
        busPositions.add(new BusPosition("서울70사7777", null, "126.976598", "37.575676", false));
        busPositions.add(new BusPosition("서울74사7232", null, "126.961693", "37.59839", false));

        return busPositions;
    }

    public List<BusArriveInfoDto> getBusArriveInfoTest() {

        List<BusArriveInfoDto> data = new ArrayList<>();
        
        data.add(new BusArriveInfoDto("100000008", "경기상고", "5분23초후[3번째 전]", "22분17초후[8번째 전]", "126.9697701102", "37.5871907861", false, false, false));
        data.add(new BusArriveInfoDto("100000016", "통인시장종로구보건소", "2분2초후[1번째 전]", "19분9초후[6번째 전]", "126.9713851083", "37.5805132118", false, false, false));
        data.add(new BusArriveInfoDto("100000017", "효자동", "3분18초후[2번째 전]", "20분12초후[7번째 전]", "126.9706209712", "37.583091043", false, false, false));
        data.add(new BusArriveInfoDto("100000021", "경복궁역", "곧 도착", "17분18초후[5번째 전]", "126.9722828893", "37.5776713863", true, false, true));
        data.add(new BusArriveInfoDto("100000023", "KT광화문지사", "15분5초후[4번째 전]", "15분19초후[5번째 전]", "126.9774187393", "37.572209741", false, true, true));
        data.add(new BusArriveInfoDto("100000045", "자하문터널입구.석파정", "7분36초후[4번째 전]", "25분3초후[9번째 전]", "126.9639775924", "37.5955575712", false, false, false));
        data.add(new BusArriveInfoDto("100000046", "하림각", "8분54초후[5번째 전]", "26분1초후[10번째 전]", "126.962215308", "37.5981476478", false, false, false));
        data.add(new BusArriveInfoDto("100000180", "상명대정문", "2분41초후[1번째 전]", "12분13초후[7번째 전]", "126.9550207178", "37.6016197182", false, false, false));
        data.add(new BusArriveInfoDto("100000188", "상명대입구.세검정교회", "곧 도착", "9분47초후[6번째 전]", "126.9597921957", "37.5994435122", false, false, false));
        data.add(new BusArriveInfoDto("101000007", "서울역버스환승센터.강우규의거터", "4분43초후[1번째 전]", "6분57초후[2번째 전]", "126.972926", "37.555411", false, false, false));
        data.add(new BusArriveInfoDto("101000033", "시청앞", "15분46초후[2번째 전]", "16분후[3번째 전]", "126.976443", "37.562197", true, true, true));
        data.add(new BusArriveInfoDto("101000039", "서울신문사", "11분30초후[3번째 전]", "13분44초후[4번째 전]", "126.9773647051", "37.5683632936", false, false, false));
        data.add(new BusArriveInfoDto("102000009", "숙대입구역", "2분4초후[0번째 전]", "4분38초후[1번째 전]", "126.9725377982", "37.543295633", false, false, false));
        data.add(new BusArriveInfoDto("102000011", "갈월동", "2분49초후[0번째 전]", "5분3초후[1번째 전]", "126.9722373211", "37.5509755993", false, false, false));
        data.add(new BusArriveInfoDto("102000019", "남영역", "2분14초후[0번째 전]", "5분44초후[2번째 전]", "126.972069", "37.541823", false, false, false));
        data.add(new BusArriveInfoDto("102000046", "용산경찰서", "2분56초후[1번째 전]", "8분31초후[6번째 전]", "126.9690615428", "37.5399994643", false, false, false));
        data.add(new BusArriveInfoDto("102000048", "용산e편한세상", "5분33초후[4번째 전]", "23분14초후[10번째 전]", "126.965469", "37.53746", false, false, false));
        data.add(new BusArriveInfoDto("102000049", "남정초등학교", "4분36초후[3번째 전]", "22분18초후[9번째 전]", "126.9636632444", "37.5363187485", false, false, false));
        data.add(new BusArriveInfoDto("102000051", "원효로2가사거리", "3분4초후[2번째 전]", "20분53초후[8번째 전]", "126.9610704217", "37.5349338369", false, false, false));
        data.add(new BusArriveInfoDto("102000297", "용산꿈나무종합타운", "곧 도착", "7분27초후[5번째 전]", "126.9672402984", "37.538486933", false, false, false));

        return data;
    }

}
