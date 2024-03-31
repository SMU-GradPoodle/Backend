package smu.poodle.smnavi.tipoff.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.poodle.smnavi.common.dto.PageResult;
import smu.poodle.smnavi.common.errorcode.CommonStatusCode;
import smu.poodle.smnavi.common.exception.RestApiException;
import smu.poodle.smnavi.map.enums.TransitType;
import smu.poodle.smnavi.map.domain.station.Waypoint;
import smu.poodle.smnavi.map.repository.BusStationRepository;
import smu.poodle.smnavi.map.repository.SubwayStationRepository;
import smu.poodle.smnavi.tipoff.domain.Location;
import smu.poodle.smnavi.tipoff.domain.TipOff;
import smu.poodle.smnavi.tipoff.dto.LikeInfoDto;
import smu.poodle.smnavi.tipoff.dto.LocationDto;
import smu.poodle.smnavi.tipoff.dto.TipOffRequestDto;
import smu.poodle.smnavi.tipoff.dto.TipOffResponseDto;
import smu.poodle.smnavi.tipoff.exception.TipOffExceptionCode;
import smu.poodle.smnavi.tipoff.repository.TipOffRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static smu.poodle.smnavi.user.util.LoginUserUtil.*;

@Service
@RequiredArgsConstructor
public class TipOffService {
    private final TipOffRepository tipOffRepository;
    private final BusStationRepository busStationRepository;
    private final SubwayStationRepository subwayStationRepository;
    private final ThumbService thumbService;

    @Transactional
    public TipOffResponseDto.Simple registerTipOff(TipOffRequestDto tipOffRequestDto) {
        TipOff tipOff = tipOffRequestDto.ToEntity(getLoginMemberId());

        if (tipOff.getTransitType() == TransitType.BUS) {
            Waypoint waypoint = busStationRepository.findAllByLocalStationId(tipOffRequestDto.getStationId()).get(0);
            tipOff.setWaypoint(waypoint);
        } else if (tipOff.getTransitType() == TransitType.SUBWAY) {
            Waypoint waypoint = subwayStationRepository.findAllByStationId(Integer.parseInt(tipOffRequestDto.getStationId())).get(0);
            tipOff.setWaypoint(waypoint);
        }
        return TipOffResponseDto.Simple.of(tipOffRepository.save(tipOff));
    }

    @Transactional
    public TipOffResponseDto.Simple updateInfo(Long id, TipOffRequestDto tipOffRequestDto) {
        TipOff tipOff = tipOffRepository.findById(id)
                .orElseThrow(() -> new RestApiException(CommonStatusCode.RESOURCE_NOT_FOUND));

        authorizationTipOff(tipOff, tipOffRequestDto.getPassword());

        tipOff.setContent(tipOffRequestDto.getContent());

        return TipOffResponseDto.Simple.of(tipOff);
    }

    @Transactional
    public void deleteTipOff(Long id, TipOffRequestDto tipOffRequestDto) {
        TipOff tipOff = tipOffRepository.findById(id).orElseThrow(() ->
                new RestApiException(CommonStatusCode.FORBIDDEN)
        );

        authorizationTipOff(tipOff, tipOffRequestDto.getPassword());

        tipOffRepository.delete(tipOff);
    }

    public PageResult<TipOffResponseDto.Detail> getTipOffList(Boolean isMine, Pageable pageable) {
        Page<TipOff> tipOffPage = tipOffRepository.findByQuery(isMine, getLoginMemberId(), pageable);

        return PageResult.of(tipOffPage.map((tipOff -> TipOffResponseDto.Detail.of(tipOff,
                thumbService.getLikeInfo(tipOff.getId()), getLoginMemberId()))));
    }

    @Transactional(readOnly = true)
    public TipOffResponseDto.Detail getTipOffById(Long id) {
        TipOff tipOff = tipOffRepository.findById(id).orElseThrow(() ->
                new RestApiException(CommonStatusCode.RESOURCE_NOT_FOUND));
        LikeInfoDto likeInfoDto = thumbService.getLikeInfo(tipOff.getId());
        return TipOffResponseDto.Detail.of(tipOff, likeInfoDto, getLoginMemberId());
    }


    public List<LocationDto> getTipOffButton() {
        List<Location> busTransitType = Location.getByTransitType(TransitType.BUS);
        List<Location> subTransitType = Location.getByTransitType(TransitType.SUBWAY);
        List<LocationDto> locations = new ArrayList<>();
        locations.add(LocationDto.from("버스", busTransitType));
        locations.add(LocationDto.from("지하철", subTransitType));
        return locations;
    }

    private void authorizationTipOff(TipOff tipOff, String password) {
        if (tipOff.getAuthor() == null) {
            if(!Objects.equals(password, tipOff.getPassword())) {
                throw new RestApiException(TipOffExceptionCode.NOT_CORRECT_PASSWORD);
            }
        }
        else{
            if(!Objects.equals(tipOff.getAuthor().getId(), getLoginMemberId())) {
                throw new RestApiException(CommonStatusCode.FORBIDDEN);
            }
        }
    }
}
