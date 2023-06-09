package com.app.alertbroadcast.client.model.airquality;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonDeserialize(using = HourlyUnitsDeserializer.class)
public class HourlyUnits {

    @JsonProperty("time")
    private String time;

    private String pollutionType;

}
