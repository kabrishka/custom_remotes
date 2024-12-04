package com.trueconf.circleview.view;

public enum Sector {
    RESET(4), // значение для clickedSector, если выбрали сброс настроек
    RIGHT(0),
    BOTTOM(1),
    LEFT(2),
    TOP(3),
    UNKNOWN(-1);

    private final int value;

    Sector(int value) {
        this.value = value;
    }

    public static Sector FromInt(int id) {
        for (Sector type : values()) {
            if (type.ToInt() == id) {
                return type;
            }
        }
        return UNKNOWN;
    }

    public int ToInt() {
        return value;
    }
}
