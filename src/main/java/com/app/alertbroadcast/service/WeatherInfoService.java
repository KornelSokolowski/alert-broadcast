package com.app.alertbroadcast.service;

import com.app.alertbroadcast.client.feign.OpenMeteoWeatherForecastClient;
import com.app.alertbroadcast.client.model.weatherforecast.Hourly;
import com.app.alertbroadcast.client.model.weatherforecast.WeatherMetric;
import com.app.alertbroadcast.client.model.weatherforecast.WeatherVariable;
import com.app.alertbroadcast.export.WeatherInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class WeatherInfoService {

    private static final Double LATITUDE = 52.549995;
    private static final Double LONGITUDE = 16.75;
    private static final LocalDate START = LocalDate.now();
    private static final LocalDate END = START.plusDays(3);

    private final OpenMeteoWeatherForecastClient openMeteoWeatherForecastClient;

    public WeatherInfoService(OpenMeteoWeatherForecastClient openMeteoWeatherForecastClient) {
        this.openMeteoWeatherForecastClient = openMeteoWeatherForecastClient;
    }

    public List<WeatherInfo> getWeatherByWeatherVariable(WeatherVariable weatherVariable){
        Hourly hourly = extractHourly(weatherVariable);
        List<LocalDateTime> localDateTimes = hourly.getTime();
        List<Double> doubleList = hourly.getWeatherVariable();
        List<WeatherInfo> weatherInfos = new ArrayList<>();
        if(doubleList != null && !doubleList.isEmpty() && !doubleList.contains(null)){
            for (int i = 0; i<doubleList.size(); i++){
                WeatherInfo weatherInfo = new WeatherInfo();
                weatherInfo.setWeatherVariable(weatherVariable);
                weatherInfo.setDate(localDateTimes.get(i));
                weatherInfo.setValue(doubleList.get(i));
                weatherInfos.add(i,weatherInfo);
            }
        }
        return weatherInfos;
    }
    private Hourly extractHourly(WeatherVariable weatherVariable){
        WeatherMetric weatherMetric = openMeteoWeatherForecastClient.getMetrics(LATITUDE,LONGITUDE,weatherVariable.getWeatherVariableName(),
                START,END);
        return weatherMetric.getHourly();
    }
}
