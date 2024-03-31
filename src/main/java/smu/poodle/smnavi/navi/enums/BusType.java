package smu.poodle.smnavi.navi.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum BusType {
    NORMAL("일반", 1),
    VILLAGE("마을버스", 3),
    BLUE("간선", 11),
    GREEN("지선", 12),
    ;

    private final String typeName;
    private final Integer typeNumber;

    private static final Map<Integer, BusType> INTEGER_BUS_TYPE_MAP = new HashMap<>();
    private static final Map<Integer, String> TYPE_NAME_MAP = new HashMap<>();

    static {
        for (BusType busType : values()) {
            INTEGER_BUS_TYPE_MAP.put(busType.getTypeNumber(), busType);
            TYPE_NAME_MAP.put(busType.getTypeNumber(), busType.getTypeName());
        }
    }

    public static BusType fromTypeNumber(Integer typeNumber) {
        try{
            return INTEGER_BUS_TYPE_MAP.get(typeNumber);
        }
        catch (Exception e){
            return null;
        }
    }

    public static String getTypeName(Integer typeNumber) {
        return TYPE_NAME_MAP.get(typeNumber);
    }


    BusType(String typeName, Integer typeNumber) {
        this.typeName = typeName;
        this.typeNumber = typeNumber;
    }
}
