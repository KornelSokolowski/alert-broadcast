package com.app.alertbroadcast.export;

import com.app.alertbroadcast.client.model.weatherforecast.WeatherVariable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class WeatherInfo {

    private WeatherVariable weatherVariable;
    private LocalDateTime date;
    private Double value;
}
