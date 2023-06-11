package com.app.alertbroadcast.client.model.airquality.pollution;

public enum PollutionType {

    GRASS("grass_pollen"), ALDER("alder_pollen"),
    BIRCH("birch_pollen"), MUGWORT("mugwort_pollen"),
    OLIVE("olive_pollen"), RAGWEED("ragweed_pollen"),

    PM10("pm10"), PM25("pm2_5"),

    CO("carbon_monoxide"), NO2("nitrogen_dioxide"),
    SO2("sulphur_dioxide"), O3("ozone"),
    NH3("ammonia"), DUST("dust"),

    AOD("aerosol_optical_depth"),
    UVINDEX("uv_index"), UVINDEXCLEARSKY("uv_index_clear_sky");

    private final String pollutionName;

    PollutionType(String pollutionName) {
        this.pollutionName = pollutionName;
    }

    public String getPollutionName() {
        return pollutionName;
    }
}
