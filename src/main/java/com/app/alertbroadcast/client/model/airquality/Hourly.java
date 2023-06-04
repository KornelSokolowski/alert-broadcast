package com.app.alertbroadcast.client.model.airquality;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonDeserialize(using = HourlyDeserializer.class)
public class Hourly {

    private List<LocalDateTime> time;

    private List<Double> pollen;

}