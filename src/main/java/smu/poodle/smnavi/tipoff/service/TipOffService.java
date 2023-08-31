package smu.poodle.smnavi.tipoff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import smu.poodle.smnavi.common.dto.PageResult;
import smu.poodle.smnavi.common.errorcode.CommonErrorCode;
import smu.poodle.smnavi.common.errorcode.DetailErrorCode;
import smu.poodle.smnavi.common.exception.RestApiException;
import smu.poodle.smnavi.map.domain.Accident;
import smu.poodle.smnavi.map.domain.data.TransitType;
import smu.poodle.smnavi.map.domain.station.Waypoint;
import smu.poodle.smnavi.map.repository.AccidentRepository;
import smu.poodle.smnavi.map.repository.BusStationRepository;
import smu.poodle.smnavi.map.repository.SubwayStationRepository;
import smu.poodle.smnavi.tipoff.domain.Location;
import smu.poodle.smnavi.tipoff.domain.TipOff;
import smu.poodle.smnavi.tipoff.dto.LocationDto;
import smu.poodle.smnavi.tipoff.dto.TipOffRequestDto;
import smu.poodle.smnavi.tipoff.dto.TipOffResponseDto;
import smu.poodle.smnavi.tipoff.repository.TipOffRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TipOffService {
    private final TipOffRepository tipOffRepository;
    private final BusStationRepository busStationRepository;
    private final SubwayStationRepository subwayStationRepository;
    private final AccidentRepository accidentRepository;


    public void addInfo(TipOffRequestDto tipOffRequestDto) {

        TipOff tipOff = tipOffRequestDto.ToEntity();

        if (tipOff.getTransitType() == TransitType.BUS) {
            Waypoint waypoint = busStationRepository.findAllByLocalStationId(tipOffRequestDto.getStationId()).get(0);
            tipOff.setWaypoint(waypoint);
        }
        else if (tipOff.getTransitType() == TransitType.SUBWAY) {
            Waypoint waypoint = subwayStationRepository.findAllByStationId(Integer.parseInt(tipOffRequestDto.getStationId())).get(0);
            tipOff.setWaypoint(waypoint);
        }

        tipOffRepository.save(tipOff);
    }

    //todo : accident 가 아니라 다른걸로 대체 필요
    private void createAccident(TipOff tipOff){
        Accident accident = Accident.builder()
                .kind(tipOff.getKind())
                .waypoint(tipOff.getWaypoint())
                .build();

        accidentRepository.save(accident);
    }

    //todo : 제목 검색이 의미가 있는가?
    public PageResult<TipOffResponseDto.Detail> getTipOffList(String keyword, Pageable pageable) {
        Page<TipOff> tipOffPage = tipOffRepository.findByQuery(keyword, pageable);

        return PageResult.of(tipOffPage.map(TipOffResponseDto.Detail::of));
    }

    public Optional<TipOff> updateInfo(Long id, TipOffRequestDto tipOffRequestDto) {
        LocalDateTime updateTime = LocalDateTime.now().minusMinutes(1);
        int infoCount = 0;
        if (infoCount > 0) {
            throw new RestApiException(DetailErrorCode.NOT_MODIFY_ERROR);
        }
        TipOff tipOff = tipOffRepository.findById(id)
                .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
        tipOff.setTitle(tipOffRequestDto.getTitle());
        tipOff.setContent(tipOffRequestDto.getContent());
        tipOffRepository.save(tipOff);
        return Optional.of(tipOff);
    }

    public void increaseViews(Long id) {
        TipOff tipOff = tipOffRepository.findById(id)
                .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
        tipOff.increaseViews();
        tipOffRepository.save(tipOff);
    }

    public Optional<TipOffRequestDto> getInfoById(Long id) {
        Optional<TipOff> infoEntity = tipOffRepository.findById(id);
        Optional<TipOffRequestDto> infoDto = Optional.ofNullable(TipOffRequestDto.builder()
                .title(infoEntity.get().getTitle())
                .content(infoEntity.get().getContent())
                .build());
        return infoDto;
    }

    public Long deleteInfoId(Long id) {
        Optional<TipOff> infoEntity = tipOffRepository.findById(id);
        if (!infoEntity.isPresent()) {
            throw new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND);
        } else {
            tipOffRepository.delete(infoEntity.get());
            return infoEntity.get().getId();
        }
    }

    public List<LocationDto> getBusLocationList() {
        List<Location> busTransitType = Location.getByTransitType(TransitType.BUS);
        List<Location> subTransitType = Location.getByTransitType(TransitType.SUBWAY);
        List<LocationDto> locations = new ArrayList<>();
        locations.add(LocationDto.from("버스", busTransitType));
        locations.add(LocationDto.from("지하철", subTransitType));
        return locations;
    }
}
