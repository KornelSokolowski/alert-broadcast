package com.app.alertbroadcast.client.model.airquality.pollen;

public enum PollenType {

    GRASS("grass_pollen"), ALDER("alder_pollen"),
    BIRCH("birch_pollen"), MUGWORT("mugwort_pollen"),
    OLIVE("olive_pollen"), RAGWEED("ragweed_pollen");

    private final String value;

    PollenType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
