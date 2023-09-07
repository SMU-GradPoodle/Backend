package smu.poodle.smnavi.map.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.poodle.smnavi.map.domain.Accident;
import smu.poodle.smnavi.map.domain.station.Waypoint;
import smu.poodle.smnavi.map.dto.BusArriveInfoDto;
import smu.poodle.smnavi.map.dto.BusPositionDto;
import smu.poodle.smnavi.map.externapi.busarrinfo.AccidentData;
import smu.poodle.smnavi.map.externapi.busarrinfo.BusArriveInfoApi;
import smu.poodle.smnavi.map.redis.BusPositionRepository;
import smu.poodle.smnavi.map.redis.domain.BusPosition;
import smu.poodle.smnavi.map.redis.domain.IssueOfBusNonStop;
import smu.poodle.smnavi.map.redis.IssueOfBusNonStopRepository;
import smu.poodle.smnavi.map.redis.domain.IssueOfBusSpacingLarge;
import smu.poodle.smnavi.map.repository.AccidentRepository;
import smu.poodle.smnavi.map.repository.BusRealTimeLocateLogRepository;
import smu.poodle.smnavi.map.repository.BusRealTimeLocateRepository;
import smu.poodle.smnavi.map.repository.BusStationRepository;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BusPositionService {

    private final BusArriveInfoApi busArriveInfoApi;
    private final IssueOfBusNonStopRepository issueOfBusNonStopRepository;
    private final BusRealTimeLocateRepository busRealTimeLocateRepository;
    private final BusRealTimeLocateLogRepository busRealTimeLocateLogRepository;
    private final AccidentRepository accidentRepository;
    private final BusStationRepository busStationRepository;
    private final BusPositionRepository busPositionRepository;

    @Scheduled(cron = "0 0/10 7-17 * * *")
    @Transactional
    public void catchAccidentInfo() {
        List<AccidentData> trafficIssue = guessTrafficIssue();

        for (AccidentData accidentData : trafficIssue) {
            if (accidentData != null) {
                List<Waypoint> busStation = busStationRepository.findAllByLocalStationId(String.valueOf(accidentData.getStationId()));
                if (!busStation.isEmpty()) {
                    accidentRepository.save(Accident.builder()
                            .waypoint(busStation.get(0))
                            .message(accidentData.message)
                            .build());
                }
            }
        }
    }

    public List<AccidentData> guessTrafficIssue() {
        List<BusArriveInfoDto> busArriveInfoDtoList = busArriveInfoApi.parseDtoFromXml();

        checkTrafficErrorByBusMovement(busArriveInfoDtoList);

        List<AccidentData> accidentDataList = new ArrayList<>();
        accidentDataList.add(isSpacingTooLarge(busArriveInfoDtoList));
        accidentDataList.add(isSpacingTooNarrow(busArriveInfoDtoList));
        updateNonStop(busArriveInfoDtoList);

        return accidentDataList;
    }

    public AccidentData isSpacingTooNarrow(List<BusArriveInfoDto> busArriveInfoDtoList) {
        List<Integer> busLocatedStationOrderList = createBusLocatedStationOrderList(busArriveInfoDtoList);

        Queue<Integer> busQueue = new LinkedList<>();

        for (Integer busLocatedStationOrder : busLocatedStationOrderList) {
            busQueue.offer(busLocatedStationOrder);

            while (!busQueue.isEmpty() && busLocatedStationOrder - busQueue.peek() > 2) {
                busQueue.poll();
            }

            if (busQueue.size() >= 3) {
                for (BusArriveInfoDto busArriveInfoDto : busArriveInfoDtoList) {
                    if (busArriveInfoDto.getStationOrder() == busLocatedStationOrder) {
                        return AccidentData.builder()
                                .stationId(busArriveInfoDto.getStationId())
                                .message("해당 정류장 근처에서 통행 이상이 있습니다.")
                                .build();
                    }
                }
            }
        }
        return null;
    }

    @Transactional
    public void updateNonStop(List<BusArriveInfoDto> busArriveInfoDtoList) {
        boolean nonStop = false;
        String nonStopStartStationName = "", nonStopEndStationName = "";
        for (BusArriveInfoDto busArriveInfoDto : busArriveInfoDtoList) {
            if (busArriveInfoDto.isStationNonStop() && !nonStop) {
                nonStop = true;
                nonStopStartStationName = busArriveInfoDto.getStationName();
            } else if (!busArriveInfoDto.isStationNonStop() && nonStop) {
                nonStopEndStationName = busArriveInfoDto.getStationName();
                break;
            }
        }
        issueOfBusNonStopRepository.deleteAll();

        if (nonStop) {
            issueOfBusNonStopRepository.save(IssueOfBusNonStop.builder()
                    .busName("7016")
                    .nonStopStartStationName(nonStopStartStationName)
                    .nonStopEndStationName(nonStopEndStationName)
                    .build());
        }
    }

    public AccidentData isSpacingTooLarge(List<BusArriveInfoDto> busArriveInfoDtoList) {
        List<IssueOfBusSpacingLarge> issueOfBusSpacingLargeList = new ArrayList<>();
        for (BusArriveInfoDto busArriveInfoDto : busArriveInfoDtoList) {
            int intervalSecond = busArriveInfoDto.getSecondArrivalSeconds() - busArriveInfoDto.getFirstArrivalSeconds();

        }
        return null;
    }

    public List<Integer> createBusLocatedStationOrderList(List<BusArriveInfoDto> busArriveInfoDtoList) {
        List<Integer> busLocatedStationOrderList = new ArrayList<>();

        busLocatedStationOrderList.add(busArriveInfoDtoList.get(0).getSecondArrivalStationOrder());
        busLocatedStationOrderList.add(busArriveInfoDtoList.get(0).getFirstArrivalStationOrder());

        for (int i = 1; i < busArriveInfoDtoList.size(); i++) {
            BusArriveInfoDto curBusStation = busArriveInfoDtoList.get(i);

            int firstArrivalStationOrder = curBusStation.getFirstArrivalStationOrder();
            int secondArrivalStationOrder = curBusStation.getSecondArrivalStationOrder();

            if (firstArrivalStationOrder == curBusStation.getStationOrder()) {
                busLocatedStationOrderList.add(firstArrivalStationOrder);
                if (secondArrivalStationOrder == curBusStation.getStationOrder()) {
                    busLocatedStationOrderList.add(secondArrivalStationOrder);
                }
            }
        }

        return busLocatedStationOrderList;
    }


    @Transactional
    public void checkTrafficErrorByBusMovement(List<BusArriveInfoDto> busArriveInfoDtoList) {
        Map<String, BusPositionDto> busRealTimeLocationDtoMap = parseMapFromDto(busArriveInfoDtoList);

        for (String licensePlate : busRealTimeLocationDtoMap.keySet()) {
            BusPositionDto busPositionDto = busRealTimeLocationDtoMap.get(licensePlate);
            busRealTimeLocateLogRepository.save(busPositionDto.toLogEntity("7016"));
            busRealTimeLocateRepository.findByLicensePlate(licensePlate).ifPresentOrElse(
                    busRealTimeLocateInfo -> {
                        if (busRealTimeLocateInfo.getStationOrder() == busPositionDto.getStationOrder()) {
                            List<Waypoint> busStation = busStationRepository.findAllByLocalStationId(busPositionDto.getStationId());

                            if (!busStation.isEmpty()) {
                                accidentRepository.save(busPositionDto.toAccidentEntity(busStation.get(0)));
                            }
                        } else {
                            busRealTimeLocateInfo.setStationId(busPositionDto.getStationId());
                            busRealTimeLocateInfo.setStationOrder(busPositionDto.getStationOrder());
                        }
                    },
                    () -> busRealTimeLocateRepository.save(busPositionDto.toInfoEntity("7016"))
            );
        }

        busRealTimeLocateRepository.deleteAllOutOfBoundBusInfo(busRealTimeLocationDtoMap.keySet());

    }

    private Map<String, BusPositionDto> parseMapFromDto(List<BusArriveInfoDto> busArriveInfoDtoList) {
        Map<String, BusPositionDto> busRealTimeLocationDtoMap = new HashMap<>();
        for (BusArriveInfoDto busArriveInfoDto : busArriveInfoDtoList) {

            String firstArrivalLicensePlate = busArriveInfoDto.getFirstArrivalLicensePlate();
            String secondArrivalLicensePlate = busArriveInfoDto.getSecondArrivalLicensePlate();


            busRealTimeLocationDtoMap.putIfAbsent(
                    firstArrivalLicensePlate,
                    BusPositionDto.builder()
                            .licensePlate(firstArrivalLicensePlate)
                            .stationOrder(busArriveInfoDto.getFirstArrivalStationOrder())
                            .stationId(busArriveInfoDto.getFirstArrivalNextStationId())
                            .build());

            busRealTimeLocationDtoMap.putIfAbsent(
                    secondArrivalLicensePlate,
                    BusPositionDto.builder()
                            .licensePlate(secondArrivalLicensePlate)
                            .stationOrder(busArriveInfoDto.getSecondArrivalStationOrder())
                            .stationId(busArriveInfoDto.getSecondArrivalNextStationId())
                            .build());
        }

        return busRealTimeLocationDtoMap;
    }

    @Transactional
    public List<BusPosition> getBusPositionList() {
        return (List<BusPosition>) busPositionRepository.findAll();
    }
}
