package com.app.alertbroadcast.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GenericMetric {

    private Double latitude;
    private Double longitude;
    @JsonProperty("generationtime_ms")
    private Double generationTimeMs;
    @JsonProperty("utc_offset_seconds")
    private Long utcOffsetSeconds;
    private String timezone;
    @JsonProperty("timezone_abbreviation")
    private String timezoneAbbreviation;
    @JsonProperty("hourly_units")
    private HourlyUnits hourlyUnits;
    private Hourly hourly;

}
