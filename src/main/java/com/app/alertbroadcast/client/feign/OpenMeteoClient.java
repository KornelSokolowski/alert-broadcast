package com.app.alertbroadcast.client.feign;

import com.app.alertbroadcast.client.model.GenericMetric;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(value = "open-meteo", url = "${external.open-meteo.url}", dismiss404 = true)
public interface OpenMeteoClient {
    @GetMapping(
            path = "/v1/air-quality",
            params = {"latitude", "longitude", "hourly", "start_date", "end_date"}
    )
    GenericMetric getMetrics(
            @RequestParam Double latitude, @RequestParam Double longitude, @RequestParam String hourly,
            @RequestParam(name = "start_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(name = "end_date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate);

}
