package com.app.alertbroadcast.client.model.pollen.alder;

import com.app.alertbroadcast.client.model.Hourly;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class AlderPollenHourly extends Hourly {

    @JsonProperty("alder_pollen")
    private List<Double> alderPollen;

    public AlderPollenHourly(List<LocalDateTime> time, List<Double> alderPollen) {
        super(time);
        this.alderPollen = alderPollen;
    }
}
