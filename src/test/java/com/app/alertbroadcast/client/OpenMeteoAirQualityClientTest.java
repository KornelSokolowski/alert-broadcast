package com.app.alertbroadcast.client;

import com.app.alertbroadcast.client.feign.FeignConfiguration;
import com.app.alertbroadcast.client.feign.OpenMeteoAirQualityClient;
import com.app.alertbroadcast.client.model.airquality.GenericMetric;
import com.app.alertbroadcast.client.model.airquality.Hourly;
import com.app.alertbroadcast.client.model.airquality.HourlyUnits;
import com.app.alertbroadcast.client.model.airquality.pollen.PollenType;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest(classes = {FeignConfiguration.class, OpenMeteoAirQualityClient.class})
@ImportAutoConfiguration({FeignAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class})
@ExtendWith(SoftAssertionsExtension.class)
public class OpenMeteoAirQualityClientTest extends AbstractMockedServerIT {

    private static final Double LATITUDE = 52.549995;
    private static final Double LONGITUDE = 16.75;
    private static final Double generationTimeMs = 0.23496150970458984;
    private static final Long utcOffsetSeconds = 0L;
    private static final String timezone = "GMT";
    private static final String timezoneAbbreviation = "GMT";
    private static final String URL_PATH = "/v1/air-quality";
    private static final LocalDate START = LocalDate.of(2023, 5, 27);
    private static final LocalDate END = START.plusDays(2);

    @Autowired
    private OpenMeteoAirQualityClient openMeteoAirQualityClient;

    // TODO dodac pola z Generic Metric do testów
    @ParameterizedTest
    @MethodSource("getMetricsArguments")
    void getNullMetrics(String path, HourlyUnits hourlyUnits, Hourly hourly, String pollenType, SoftAssertions softly) {
        prepareMockedResponseFromFile(URL_PATH, path);

        GenericMetric genericMetric = openMeteoAirQualityClient.getMetrics(LATITUDE, LONGITUDE, pollenType, START, END);
        softly.assertThat(genericMetric)
                .returns(LATITUDE, GenericMetric::getLatitude)
                .returns(LONGITUDE, GenericMetric::getLongitude)
                .returns(generationTimeMs, GenericMetric::getGenerationTimeMs)
                .returns(utcOffsetSeconds, GenericMetric::getUtcOffsetSeconds)
                .returns(timezone, GenericMetric::getTimezone)
                .returns(timezoneAbbreviation, GenericMetric::getTimezoneAbbreviation);

        softly.assertThat(genericMetric.getHourlyUnits())
                .usingRecursiveComparison()
                .isEqualTo(hourlyUnits);
        softly.assertThat(genericMetric.getHourly())
                .usingRecursiveComparison()
                .isEqualTo(hourly);
    }

    private static Stream<Arguments> getMetricsArguments() {
        return Stream.of(
                Arguments.of("pollen-null-values-response.json", createHourlyUnits(), createHourlyWithArraysContainingNulls(), PollenType.GRASS.getValue()),
                Arguments.of("pollen-null-object-response.json", null, null, PollenType.GRASS.getValue()),
                Arguments.of("pollen-dymmy-value-response.json", createHourlyUnits(), new Hourly(), PollenType.GRASS.getValue()),
                Arguments.of("pollen-correctly-response.json", createHourlyUnits(), createHourly(), PollenType.GRASS.getValue()),

                Arguments.of("pollen-null-values-response.json", createHourlyUnits(), createHourlyWithArraysContainingNulls(), PollenType.ALDER.getValue()),
                Arguments.of("pollen-null-object-response.json", null, null, PollenType.ALDER.getValue()),
                Arguments.of("pollen-dymmy-value-response.json", createHourlyUnits(), new Hourly(), PollenType.ALDER.getValue()),
                Arguments.of("pollen-correctly-response.json", createHourlyUnits(), createHourly(), PollenType.ALDER.getValue())
        );
    }

    private static Hourly createHourlyWithArraysContainingNulls() {
        List<Double> pollen = new ArrayList<>();
        pollen.add(null);
        List<LocalDateTime> time = new ArrayList<>();
        time.add(null);
        return new Hourly(time, pollen);
    }

    private static Hourly createHourly() {
        List<LocalDateTime> time = loadLocalDateTimeTestValues("date-test-values.txt");
        List<Double> pollen = loadDoubleTestValues("pollen-test-values.txt");
        return new Hourly(time, pollen);
    }

    private static HourlyUnits createHourlyUnits() {
        String time = "iso8601";
        String pollen = "grains/m³";
        return new HourlyUnits(time, pollen);
    }

}