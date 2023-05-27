package com.app.alertbroadcast.client.feign;

import com.app.alertbroadcast.client.AbstractMockedServerIT;
import com.app.alertbroadcast.client.model.GrassPollenMetric;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;

import java.time.LocalDate;

@SpringBootTest(classes = {FeignConfiguration.class, OpenMeteoClient.class})
@ImportAutoConfiguration({FeignAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class})
@ExtendWith(SoftAssertionsExtension.class)
class OpenMeteoClientTest extends AbstractMockedServerIT {

    @Autowired
    private OpenMeteoClient openMeteoClient;

    @Test
    void getMetrics(SoftAssertions softly) {
        prepareMockedResponseFromFile("/v1/air-quality",
                "sample-response.json");
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(2);
        Double latitude = 52.249996;
        Double longitude = 16.75;
        String hourly = "grass_pollen";
        GrassPollenMetric grassPollenMetric = openMeteoClient.getMetrics(latitude, longitude, hourly, start, end);

        softly.assertThat(grassPollenMetric)
                .returns(latitude, GrassPollenMetric::getLatitude)
                .returns(longitude, GrassPollenMetric::getLongitude)
                .returns(start, this::getStartTime)
                .returns(end, this::getEndTime);
    }

    private LocalDate getStartTime(GrassPollenMetric grassPollenMetric) {
        return grassPollenMetric.getHourly().getTime().get(0).toLocalDate();
    }

    private LocalDate getEndTime(GrassPollenMetric grassPollenMetric) {
        return grassPollenMetric.getHourly().getTime().get(grassPollenMetric.getHourly().getTime().size() - 1).toLocalDate();
    }
}