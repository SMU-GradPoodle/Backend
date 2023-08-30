package smu.poodle.smnavi.tipoff.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import smu.poodle.smnavi.errorcode.CommonErrorCode;
import smu.poodle.smnavi.errorcode.ErrorCode;
import smu.poodle.smnavi.exception.RestApiException;
import smu.poodle.smnavi.map.domain.data.TransitType;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum Location {
    BUS_GWANGHWAMUN("100000023", TransitType.BUS, "KT광화문지사"), BUS_GYEONGBOKGUNG("100000021", TransitType.BUS, "경복궁역"),
    BUS_CITYHALL("101000033", TransitType.BUS, "시청"),
    SUB_GWANGHWAMUN("533", TransitType.SUBWAY, "광화문 5호선"), SUB_GYEONGBOKGUNG("327", TransitType.SUBWAY, "경복궁 3호선"), SUB_CITYHALL_1("132", TransitType.SUBWAY, "시청역 1호선");
    private final String stationId;
    private final TransitType transitType;
    private final String stationName;

    private static final Map<TransitType, List<Location>> LOCATION_MAP = new HashMap<>();
    private static final Map<String, Location> STATION_ID_MAP = Collections.unmodifiableMap(
            Arrays.stream(values()).collect(Collectors.toMap(Location::getStationId, Function.identity()))
    );

    static { //클래스가 로드될 때 실행
        for (TransitType transitType : TransitType.values()) {
            LOCATION_MAP.put(transitType, new ArrayList<>());
        }
        for (Location location : Location.values()) {
            LOCATION_MAP.get(location.getTransitType()).add(location);
        }
    }

    public static List<Location> getByTransitType(TransitType transitType) {
        return LOCATION_MAP.get(transitType);
    }

    public static Location stationIdToLocation(String stationId) {
        Location location = STATION_ID_MAP.get(stationId);
        if (location == null) {
            throw new RestApiException(CommonErrorCode.INVALID_PARAMETER);
        }
        return location;
    }
}