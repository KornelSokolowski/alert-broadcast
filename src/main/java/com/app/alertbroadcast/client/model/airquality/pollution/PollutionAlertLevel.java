package com.app.alertbroadcast.client.model.airquality.pollution;

public enum PollutionAlertLevel {

    GOOD("Good"), FAIR("Fair"),
    MODERATE("Moderate"), POOR("Poor"),
    VERY_POOR("Very_poor"), EXTREMELY_POOR("Extremely_poor");

    private final String description;

    PollutionAlertLevel(String pollutionAlertLevel) {
        this.description = pollutionAlertLevel;
    }

    public String getDescription() {
        return description;
    }

}
