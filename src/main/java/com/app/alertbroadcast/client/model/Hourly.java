package com.app.alertbroadcast.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class Hourly {
    private List<LocalDateTime> time;
    @JsonProperty("grass_pollen")
    private List<Double> grassPollen;
}
