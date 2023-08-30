package com.app.alertbroadcast.client.model.weatherforecast;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WeatherMetric {

    private Double latitude;
    private Double longitude;
    @JsonProperty("generationtime_ms")
    private Double generationTimeMs;
    @JsonProperty("utc_offset_seconds")
    private Long utcOffsetSeconds;
    private String timezone;
    @JsonProperty("timezone_abbreviation")
    private String timezoneAbbreviation;
    @JsonProperty("elevation")
    private Double elevation;
    @JsonProperty("hourly_units")
    private HourlyUnits hourlyUnits;
    private Hourly hourly;

}
