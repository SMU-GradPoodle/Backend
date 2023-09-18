package smu.poodle.smnavi.map.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransitType {
    SUBWAY(1,"지하철"),
    BUS(2, "버스"),
    WALK(3, "도보");

    private final int number;
    private final String description;

    public static TransitType of(int typeNumber){
        if(typeNumber == 1){
            return TransitType.SUBWAY;
        }
        else if(typeNumber == 2){
            return TransitType.BUS;
        }
        else if(typeNumber == 3){
            return TransitType.WALK;
        }
        return null;
    }
}
