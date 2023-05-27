package com.app.alertbroadcast.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HourlyUnits {
    private String time;
    @JsonProperty("grass_pollen")
    private String grassPollen;
}
