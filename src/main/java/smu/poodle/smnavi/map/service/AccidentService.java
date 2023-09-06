package smu.poodle.smnavi.map.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import smu.poodle.smnavi.map.domain.Accident;
import smu.poodle.smnavi.map.domain.station.Waypoint;
import smu.poodle.smnavi.map.dto.AccidentDto;
import smu.poodle.smnavi.map.externapi.busarrinfo.AccidentData;
import smu.poodle.smnavi.map.externapi.busarrinfo.BusArriveInfoApi;
import smu.poodle.smnavi.map.repository.AccidentRepository;
import smu.poodle.smnavi.map.repository.BusStationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccidentService {
    private final AccidentRepository accidentRepository;
    private final BusStationRepository busStationRepository;

    private final BusArriveInfoApi busArriveInfoApi;

    public List<AccidentDto.Info> getAllAccident(){
        //레파지토리에서 모든 사고 정보를 꺼내옴
        //DTO로 변환을 해줨
        //반환
        return accidentRepository.findTopThree().stream().map(AccidentDto.Info::of).toList();
    }
}
