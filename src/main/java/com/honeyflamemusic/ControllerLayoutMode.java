package com.honeyflamemusic;

public enum ControllerLayoutMode {
    MIXER("02"),
    CONTROLS("05"),
    TRANSPORT("03"),
    SENDS("10"),
    CLIPLAUNCHER("16");

    private String layoutValue;

    ControllerLayoutMode(String layoutValue) {
        this.layoutValue = layoutValue;
    }

    public String getLayoutValue() {
        return layoutValue;
    }
}
