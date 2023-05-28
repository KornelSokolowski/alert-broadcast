package com.app.alertbroadcast.client.model.pollen.grass;

import com.app.alertbroadcast.client.model.HourlyUnits;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class GrassPollenHourlyUnits extends HourlyUnits {

    @JsonProperty("grass_pollen")
    private String grassPollen;

    public GrassPollenHourlyUnits(String time, String grassPollen) {
        super(time);
        this.grassPollen = grassPollen;
    }
}
