package com.app.alertbroadcast.client.feign;

import com.app.alertbroadcast.client.model.geocoding.Results;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "open-meteo-geocoding", url = "${external.open-meteo-geocoding.url}", dismiss404 = true)
public interface OpenMeteoGeocodingClient {

    String PATH = "v1/search";

    @GetMapping(
            path = PATH,
            params = {"name", "count", "language", "format"}
    )
    Results getGeocoding(
            @RequestParam String name, @RequestParam Integer count,
            @RequestParam String language, @RequestParam String format);
}

