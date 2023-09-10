package smu.poodle.smnavi.map.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smu.poodle.smnavi.map.domain.station.BusStationInfo;
import smu.poodle.smnavi.map.dto.BusStationInfoDto;
import smu.poodle.smnavi.map.repository.BusStationInfoRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BusStationInfoService {
    private final BusStationInfoRepository busStationInfoRepository;

    public List<BusStationInfoDto> getAllBusStationInfo(){
        //레포에서 가져와서 dto로 리턴하고 싶음
        List<BusStationInfo> busStationInfoList = busStationInfoRepository.findAll();
        List<BusStationInfoDto> busStationInfoDtoList = new ArrayList<>();
        //db에서 가져온 list를 dto로 변환하여 번환하고 싶음
        for(int i=0;i<busStationInfoList.size();i++){
            busStationInfoDtoList.add(busStationInfoList.get(i).toDto());
        }
        return busStationInfoDtoList;
    }
}
