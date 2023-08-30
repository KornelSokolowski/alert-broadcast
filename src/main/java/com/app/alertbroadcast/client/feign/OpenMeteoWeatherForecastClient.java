package com.app.alertbroadcast.client.feign;

import com.app.alertbroadcast.client.model.weatherforecast.WeatherMetric;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(value = "open-meteo-weather-forecast", url = "${external.open-meteo-weather-forecast.url}", dismiss404 = true)
public interface OpenMeteoWeatherForecastClient {
    String PATH = "/v1/forecast";

    @GetMapping(
            path = PATH,
            params = {"latitude", "longitude", "hourly", "start_date", "end_date"}
    )
    WeatherMetric getMetrics(
            @RequestParam Double latitude, @RequestParam Double longitude , @RequestParam(name = "hourly") String weatherVariables,
            @RequestParam(name = "start_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(name = "end_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate);
}
