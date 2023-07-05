package com.app.alertbroadcast.client.model.airquality.pollution;

public enum PollutionAlertLevel {
    GOOD("Good"), FAIR("Fair"),
    MODERATE("Moderate"), POOR("Poor"),
    VERYPOOR("Very poor"), EXTREMLYPOOR("Extremely poor");

    private final String pollutionAlertLevel;


    PollutionAlertLevel(String pollutionAlertLevel) { this.pollutionAlertLevel = pollutionAlertLevel;}

public String getPollutionAlertLevel(){return pollutionAlertLevel;}

}
