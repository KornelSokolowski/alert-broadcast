package com.app.alertbroadcast.client.model.pollen.grass;

import com.app.alertbroadcast.client.model.Hourly;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class GrassPollenHourly extends Hourly {
    @JsonProperty("grass_pollen")
    private List<Double> grassPollen;

    public GrassPollenHourly(List<LocalDateTime> time, List<Double> grassPollen) {
        super(time);
        this.grassPollen = grassPollen;
    }
}
