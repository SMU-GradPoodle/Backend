package smu.poodle.smnavi.navi.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import smu.poodle.smnavi.navi.externapi.BusPositionApi;
import smu.poodle.smnavi.navi.redisdomain.BusPosition;
import smu.poodle.smnavi.navi.redisdomain.BusPositionLog;
import smu.poodle.smnavi.navi.redisrepository.BusPositionLogRedisRepository;
import smu.poodle.smnavi.navi.redisrepository.BusPositionRedisRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BusPositionScheduler {
    private final BusPositionApi busPositionApi;

    private final BusPositionLogRedisRepository busPositionLogRedisRepository;
    private final BusPositionRedisRepository busPositionRedisRepository;

    @Scheduled(cron = "0/30 * 6-20 * * *")
    public void scheduleBusPosition() {
        Iterable<BusPositionLog> busPositionLogIterable = busPositionLogRedisRepository.findAll();

        Map<String, BusPositionLog> busPositionLogMap = new HashMap<>();
        for (BusPositionLog log : busPositionLogIterable) {
            busPositionLogMap.put(log.getLicensePlate(), log);
        }

        List<BusPosition> busPositions = busPositionApi.callApi();

        for (BusPosition busPosition : busPositions) {
            BusPositionLog cachedbusPositionLog = busPositionLogMap.getOrDefault(busPosition.getLicensePlate(), null);
            if (cachedbusPositionLog != null &&
                    cachedbusPositionLog.getSectionOrder() < 55 &&
                    cachedbusPositionLog.getSectionOrder().equals(busPosition.getSectionOrder())) {
                busPosition.setHasIssue(true);
            }
        }

        busPositionLogRedisRepository.deleteAll();
        busPositionLogRedisRepository.saveAll(BusPositionLog.convertBusPositionList(busPositions));
    }

    @Scheduled(cron = "* 5 21 * * *")
    public void deleteBusPositionCache() {
        busPositionRedisRepository.deleteAll();
    }
}
