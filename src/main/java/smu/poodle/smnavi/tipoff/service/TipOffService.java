package smu.poodle.smnavi.tipoff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import smu.poodle.smnavi.common.dto.PageResult;
import smu.poodle.smnavi.common.errorcode.CommonErrorCode;
import smu.poodle.smnavi.common.errorcode.DetailErrorCode;
import smu.poodle.smnavi.common.exception.RestApiException;
import smu.poodle.smnavi.map.domain.data.TransitType;
import smu.poodle.smnavi.map.domain.station.Waypoint;
import smu.poodle.smnavi.map.repository.BusStationRepository;
import smu.poodle.smnavi.map.repository.SubwayStationRepository;
import smu.poodle.smnavi.tipoff.domain.Location;
import smu.poodle.smnavi.tipoff.domain.TipOff;
import smu.poodle.smnavi.tipoff.dto.LikeInfoDto;
import smu.poodle.smnavi.tipoff.dto.LocationDto;
import smu.poodle.smnavi.tipoff.dto.TipOffRequestDto;
import smu.poodle.smnavi.tipoff.dto.TipOffResponseDto;
import smu.poodle.smnavi.tipoff.repository.TipOffRepository;
import smu.poodle.smnavi.user.sevice.LoginService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TipOffService {
    private final LoginService loginService;
    private final TipOffRepository tipOffRepository;
    private final BusStationRepository busStationRepository;
    private final SubwayStationRepository subwayStationRepository;
    private final ThumbService thumbService;


    public TipOffResponseDto.Simple registerTipOff(TipOffRequestDto tipOffRequestDto) {

        TipOff tipOff = tipOffRequestDto.ToEntity(loginService.getLoginMemberId());

        if (tipOff.getTransitType() == TransitType.BUS) {
            Waypoint waypoint = busStationRepository.findAllByLocalStationId(tipOffRequestDto.getStationId()).get(0);
            tipOff.setWaypoint(waypoint);
        } else if (tipOff.getTransitType() == TransitType.SUBWAY) {
            Waypoint waypoint = subwayStationRepository.findAllByStationId(Integer.parseInt(tipOffRequestDto.getStationId())).get(0);
            tipOff.setWaypoint(waypoint);
        }

        return TipOffResponseDto.Simple.of(tipOffRepository.save(tipOff));
    }

    public TipOffResponseDto.Detail updateInfo(Long id, TipOffRequestDto tipOffRequestDto) {
        TipOff tipOff = tipOffRepository.findById(id)
                .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        tipOff.setContent(tipOffRequestDto.getContent());
        tipOffRepository.save(tipOff);
        return TipOffResponseDto.Detail.of(tipOff, thumbService.getLikeInfo(tipOff.getId()));
    }


    //todo : 제목 검색이 의미가 있는가?
    public PageResult<TipOffResponseDto.Detail> getTipOffList(String keyword, Pageable pageable) {
        Page<TipOff> tipOffPage = tipOffRepository.findByQuery(keyword, pageable);

        return PageResult.of(tipOffPage.map((tipOff -> TipOffResponseDto.Detail.of(tipOff,
                thumbService.getLikeInfo(tipOff.getId())))));
    }

    public TipOffResponseDto.Detail getTipOffById(Long id) {
        Optional<TipOff> tipOff = tipOffRepository.findById(id);
        if (tipOff.isPresent()) {
            TipOff tipOff1 = tipOff.get();
            LikeInfoDto likeInfoDto = thumbService.getLikeInfo(tipOff1.getId());
            return TipOffResponseDto.Detail.of(tipOff1, likeInfoDto);
        }
        return null;
    }


    public void deleteTipOff(Long id) {
        TipOff tipOff = tipOffRepository.findById(id).orElseThrow(() ->
                new RestApiException(DetailErrorCode.NOT_CERTIFICATED)
        );

        tipOffRepository.delete(tipOff);
    }

    public List<LocationDto> getTipOffButton() {
        List<Location> busTransitType = Location.getByTransitType(TransitType.BUS);
        List<Location> subTransitType = Location.getByTransitType(TransitType.SUBWAY);
        List<LocationDto> locations = new ArrayList<>();
        locations.add(LocationDto.from("버스", busTransitType));
        locations.add(LocationDto.from("지하철", subTransitType));
        return locations;
    }
}
