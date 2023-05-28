package com.app.alertbroadcast;

import com.app.alertbroadcast.client.feign.OpenMeteoClient;
import com.app.alertbroadcast.client.model.GenericMetric;
import com.app.alertbroadcast.client.model.pollen.PollenType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.LocalDate;

@SpringBootApplication
public class AlertBroadcastApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AlertBroadcastApplication.class, args);
        OpenMeteoClient openMeteoClient = context.getBean(OpenMeteoClient.class);
        LocalDate start = LocalDate.now();
        GenericMetric alder = openMeteoClient.getMetrics(52.249996, 16.75, PollenType.ALDER.getValue(), start, start);
        GenericMetric grass = openMeteoClient.getMetrics(52.249996, 16.75, PollenType.GRASS.getValue(), start, start);
        System.out.println(alder);
        System.out.println();
        System.out.println(grass);
    }

}
