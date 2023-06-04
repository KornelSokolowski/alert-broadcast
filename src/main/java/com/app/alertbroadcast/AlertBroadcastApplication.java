package com.app.alertbroadcast;

import com.app.alertbroadcast.client.feign.OpenMeteoAirQualityClient;
import com.app.alertbroadcast.client.feign.OpenMeteoGeocodingClient;
import com.app.alertbroadcast.client.model.airquality.GenericMetric;
import com.app.alertbroadcast.client.model.airquality.pollen.PollenType;
import com.app.alertbroadcast.client.model.geocoding.Geocoding;
import com.app.alertbroadcast.client.model.geocoding.Language;
import com.app.alertbroadcast.client.model.geocoding.Results;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class AlertBroadcastApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AlertBroadcastApplication.class, args);
//        OpenMeteoAirQualityClient openMeteoAirQualityClient = context.getBean(OpenMeteoAirQualityClient.class);
//        LocalDate start = LocalDate.now();
//        GenericMetric alder = openMeteoAirQualityClient.getMetrics(52.249996, 16.75, PollenType.ALDER.getValue(), start, start);
//        GenericMetric grass = openMeteoAirQualityClient.getMetrics(52.249996, 16.75, PollenType.GRASS.getValue(), start, start);
//        System.out.println(alder);
//        System.out.println();
//        System.out.println(grass);

        OpenMeteoGeocodingClient geocodingClient = context.getBean(OpenMeteoGeocodingClient.class);
        Results geocoding = geocodingClient.getGeocoding("Berlin", 10, Language.EN.name(), "json");
        System.out.println(geocoding);
    }
}
