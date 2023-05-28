package com.app.alertbroadcast.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonDeserialize(using = HourlyUnitsDeserializer.class)
public abstract class HourlyUnits {

    @JsonProperty("time")
    private String time;

}
