package com.app.alertbroadcast.client.model.pollen.alder;

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
public class AlderPollenHourlyUnits extends HourlyUnits {

    @JsonProperty("alder_pollen")
    private String alderPollen;

    public AlderPollenHourlyUnits(String time, String alderPollen) {
        super(time);
        this.alderPollen = alderPollen;
    }
}
