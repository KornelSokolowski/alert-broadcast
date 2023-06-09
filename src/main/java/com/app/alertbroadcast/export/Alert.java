package com.app.alertbroadcast.export;

import com.app.alertbroadcast.client.model.airquality.pollution.PollutionAlertLevel;
import com.app.alertbroadcast.client.model.airquality.pollution.PollutionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Alert {

    private PollutionType pollutionTypes;
    private LocalDateTime date;
    private Double value;
    private PollutionAlertLevel pollutionAlertLevel;
}
