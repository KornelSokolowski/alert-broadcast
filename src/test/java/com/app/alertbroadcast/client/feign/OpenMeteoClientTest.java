package com.app.alertbroadcast.client.feign;

import com.app.alertbroadcast.client.AbstractMockedServerIT;
import com.app.alertbroadcast.client.model.GenericMetric;
import com.app.alertbroadcast.client.model.pollen.PollenType;
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
    void getGrassMetrics(SoftAssertions softly) {
        prepareMockedResponseFromFile("/v1/air-quality",
                "sample-response.json");
        LocalDate start = LocalDate.of(2023, 5, 27);
        LocalDate end = start.plusDays(2);
        Double latitude = 52.249996;
        Double longitude = 16.75;
        GenericMetric genericMetric = openMeteoClient.getMetrics(latitude, longitude, PollenType.GRASS.getValue(), start, end);

        softly.assertThat(genericMetric)
                .returns(latitude, GenericMetric::getLatitude)
                .returns(longitude, GenericMetric::getLongitude)
                .returns(start, this::getStartTime)
                .returns(end, this::getEndTime);
    }

    @Test
    void getAlderMetrics(SoftAssertions softly) {
        prepareMockedResponseFromFile("/v1/air-quality",
                "alder-pollen-response.json");
        LocalDate start = LocalDate.of(2023, 5, 27);
        LocalDate end = start.plusDays(2);
        Double latitude = 52.549995;
        Double longitude = 13.450001;
        GenericMetric genericMetric = openMeteoClient.getMetrics(latitude, longitude, PollenType.ALDER.getValue(), start, end);

        softly.assertThat(genericMetric)
                .returns(latitude, GenericMetric::getLatitude)
                .returns(longitude, GenericMetric::getLongitude)
                .returns(start, this::getStartTime)
                .returns(end, this::getEndTime);
    }


    private LocalDate getStartTime(GenericMetric genericMetric) {
        return genericMetric.getHourly().getTime().get(0).toLocalDate();
    }

    private LocalDate getEndTime(GenericMetric genericMetric) {
        return genericMetric.getHourly().getTime().get(genericMetric.getHourly().getTime().size() - 1).toLocalDate();
    }
}