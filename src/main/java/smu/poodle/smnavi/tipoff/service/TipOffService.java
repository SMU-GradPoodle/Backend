package smu.poodle.smnavi.tipoff.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponseException;
import smu.poodle.smnavi.common.dto.PageResult;
import smu.poodle.smnavi.common.errorcode.CommonErrorCode;
import smu.poodle.smnavi.common.errorcode.DetailErrorCode;
import smu.poodle.smnavi.common.errorcode.ErrorCode;
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
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TipOffService {
    private final LoginService loginService;
    private final TipOffRepository tipOffRepository;
    private final BusStationRepository busStationRepository;
    private final SubwayStationRepository subwayStationRepository;
    private final ThumbService thumbService;

    @Transactional
    @Validated
    public TipOffResponseDto.Simple registerTipOff(@Valid TipOffRequestDto tipOffRequestDto) {
        TipOff tipOff = tipOffRequestDto.ToEntity(loginService.getLoginMemberId());
        if(!tipOffRequestDto.isPasswordRequired()){ //비밀번호를 필요로 하지 않으면
            return null;
        }else{
            tipOff.setPw(tipOffRequestDto.getPw());
        }
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
    public TipOffResponseDto.Detail updateInfo(Long id, TipOffRequestDto tipOffRequestDto) {
        TipOff tipOff = tipOffRepository.findById(id)
                .orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
        if(tipOffRequestDto.isPasswordRequired()){ //익명 사용자라면
            if(Objects.equals(tipOffRequestDto.getPw(), tipOff.getPw())){ //비밀번호가 같으면
                tipOff.setContent(tipOffRequestDto.getContent());
                tipOffRepository.save(tipOff);
            }else{
                throw new RestApiException(DetailErrorCode.NOT_CORRECT_PASSWORD);
            }
        }else{ //로그인한 사용자라면
            tipOff.setContent(tipOffRequestDto.getContent());
            tipOffRepository.save(tipOff);
        }
        return TipOffResponseDto.Detail.of(tipOff, thumbService.getLikeInfo(tipOff.getId()));
    }
    @Transactional
    public void deleteTipOff(Long id, TipOffRequestDto tipOffRequestDto) {
        TipOff tipOff = tipOffRepository.findById(id).orElseThrow(() ->
                new RestApiException(DetailErrorCode.NOT_CERTIFICATED)
        );
        if(tipOffRequestDto.isPasswordRequired()){
            if(Objects.equals(tipOffRequestDto.getPw(), tipOff.getPw())){
                tipOffRepository.delete(tipOff);
            }
        }else{
            throw new RestApiException(DetailErrorCode.NOT_CORRECT_PASSWORD);
        }
    }

    //todo : 제목 검색이 의미가 있는가?
    public PageResult<TipOffResponseDto.Detail> getTipOffList(String keyword, Pageable pageable) {
        Page<TipOff> tipOffPage = tipOffRepository.findByQuery(keyword, pageable);

        return PageResult.of(tipOffPage.map((tipOff -> TipOffResponseDto.Detail.of(tipOff,
                thumbService.getLikeInfo(tipOff.getId())))));
    }

    @Transactional(readOnly = true)
    public TipOffResponseDto.Detail getTipOffById(Long id) {
        TipOff tipOff = tipOffRepository.findById(id).orElseThrow(() ->
                new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));
        LikeInfoDto likeInfoDto = thumbService.getLikeInfo(tipOff.getId());
        return TipOffResponseDto.Detail.of(tipOff, likeInfoDto);
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
