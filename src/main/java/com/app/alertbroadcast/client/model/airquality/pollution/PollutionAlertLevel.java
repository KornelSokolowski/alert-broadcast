package com.app.alertbroadcast.client.model.airquality.pollution;

public enum PollutionAlertLevel {

    GOOD("Good"), FAIR("Fair"),
    MODERATE("Moderate"), POOR("Poor"),
    VERY_POOR("Very poor"), EXTREMELY_POOR("Extremely poor");

    private final String pollutionAlertLevel;

    PollutionAlertLevel(String pollutionAlertLevel) {
        this.pollutionAlertLevel = pollutionAlertLevel;
    }

    public String getPollutionAlertLevel() {
        return pollutionAlertLevel;
    }

}
