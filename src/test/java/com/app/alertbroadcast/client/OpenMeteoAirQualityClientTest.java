package com.app.alertbroadcast.client;

import com.app.alertbroadcast.client.feign.FeignConfiguration;
import com.app.alertbroadcast.client.feign.OpenMeteoAirQualityClient;
import com.app.alertbroadcast.client.model.airquality.GenericMetric;
import com.app.alertbroadcast.client.model.airquality.Hourly;
import com.app.alertbroadcast.client.model.airquality.HourlyUnits;
import com.app.alertbroadcast.client.model.airquality.pollution.PollutionType;
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

    @ParameterizedTest
    @MethodSource("getPollenMetricsArguments")
    void getPollenMetrics(String path, HourlyUnits hourlyUnits, Hourly hourly, String pollutionType, SoftAssertions softly) {
        prepareMockedResponseFromFile(URL_PATH, path);

        GenericMetric genericMetric = openMeteoAirQualityClient.getMetrics(LATITUDE, LONGITUDE, pollutionType, START, END);
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

    private static Stream<Arguments> getPollenMetricsArguments() {
        return Stream.of(
                Arguments.of("pollen-null-values-response.json", createHourlyUnits(), createHourlyWithArraysContainingNulls(), PollutionType.GRASS.getPollutionName()),
                Arguments.of("pollen-null-object-response.json", null, null, PollutionType.GRASS.getPollutionName()),
                Arguments.of("pollen-dummy-value-response.json", createHourlyUnits(), new Hourly(), PollutionType.GRASS.getPollutionName()),
                Arguments.of("pollen-correctly-response.json", createHourlyUnits(), createHourly(), PollutionType.GRASS.getPollutionName()),

                Arguments.of("pollen-null-values-response.json", createHourlyUnits(), createHourlyWithArraysContainingNulls(), PollutionType.ALDER.getPollutionName()),
                Arguments.of("pollen-null-object-response.json", null, null, PollutionType.ALDER.getPollutionName()),
                Arguments.of("pollen-dummy-value-response.json", createHourlyUnits(), new Hourly(), PollutionType.ALDER.getPollutionName()),
                Arguments.of("pollen-correctly-response.json", createHourlyUnits(), createHourly(), PollutionType.ALDER.getPollutionName()),

                Arguments.of("pollen-null-values-response.json", createHourlyUnits(), createHourlyWithArraysContainingNulls(), PollutionType.BIRCH.getPollutionName()),
                Arguments.of("pollen-null-object-response.json", null, null, PollutionType.BIRCH.getPollutionName()),
                Arguments.of("pollen-dummy-value-response.json", createHourlyUnits(), new Hourly(), PollutionType.BIRCH.getPollutionName()),
                Arguments.of("pollen-correctly-response.json", createHourlyUnits(), createHourly(), PollutionType.BIRCH.getPollutionName()),

                Arguments.of("pollen-null-values-response.json", createHourlyUnits(), createHourlyWithArraysContainingNulls(), PollutionType.MUGWORT.getPollutionName()),
                Arguments.of("pollen-null-object-response.json", null, null, PollutionType.MUGWORT.getPollutionName()),
                Arguments.of("pollen-dummy-value-response.json", createHourlyUnits(), new Hourly(), PollutionType.MUGWORT.getPollutionName()),
                Arguments.of("pollen-correctly-response.json", createHourlyUnits(), createHourly(), PollutionType.MUGWORT.getPollutionName()),

                Arguments.of("pollen-null-values-response.json", createHourlyUnits(), createHourlyWithArraysContainingNulls(), PollutionType.OLIVE.getPollutionName()),
                Arguments.of("pollen-null-object-response.json", null, null, PollutionType.OLIVE.getPollutionName()),
                Arguments.of("pollen-dummy-value-response.json", createHourlyUnits(), new Hourly(), PollutionType.OLIVE.getPollutionName()),
                Arguments.of("pollen-correctly-response.json", createHourlyUnits(), createHourly(), PollutionType.OLIVE.getPollutionName()),

                Arguments.of("pollen-null-values-response.json", createHourlyUnits(), createHourlyWithArraysContainingNulls(), PollutionType.RAGWEED.getPollutionName()),
                Arguments.of("pollen-null-object-response.json", null, null, PollutionType.RAGWEED.getPollutionName()),
                Arguments.of("pollen-dummy-value-response.json", createHourlyUnits(), new Hourly(), PollutionType.RAGWEED.getPollutionName()),
                Arguments.of("pollen-correctly-response.json", createHourlyUnits(), createHourly(), PollutionType.RAGWEED.getPollutionName())

        );
    }

    @ParameterizedTest()
    @MethodSource("getSmogMetricsArguments")
    void getSmogMetrics(String path, HourlyUnits hourlyUnits, Hourly hourly, String smog, SoftAssertions softly) {
        prepareMockedResponseFromFile(URL_PATH, path);
        GenericMetric genericMetric = openMeteoAirQualityClient.getMetrics(LATITUDE, LONGITUDE, smog, START, END);
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

    private static Stream<Arguments> getSmogMetricsArguments() {
        return Stream.of(
                Arguments.of("smog-null-values-response.json", createHourlyUnitsSmog(), createHourlyWithArraysContainingNulls(), PollutionType.PM10.getPollutionName()),
                Arguments.of("smog-null-object-response.json", null, null, PollutionType.PM10.getPollutionName()),
                Arguments.of("smog-dummy-value-response.json", createHourlyUnitsSmog(), new Hourly(), PollutionType.PM10.getPollutionName()),
                Arguments.of("smog-correctly-response.json", createHourlyUnitsSmog(), createHourly(), PollutionType.PM10.getPollutionName()),

                Arguments.of("smog-null-values-response.json", createHourlyUnitsSmog(), createHourlyWithArraysContainingNulls(), PollutionType.PM25.getPollutionName()),
                Arguments.of("smog-null-object-response.json", null, null, PollutionType.PM25.getPollutionName()),
                Arguments.of("smog-dummy-value-response.json", createHourlyUnitsSmog(), new Hourly(), PollutionType.PM25.getPollutionName()),
                Arguments.of("smog-correctly-response.json", createHourlyUnitsSmog(), createHourly(), PollutionType.PM25.getPollutionName()),

                Arguments.of("smog-null-values-response.json", createHourlyUnitsSmog(), createHourlyWithArraysContainingNulls(), PollutionType.DUST.getPollutionName()),
                Arguments.of("smog-null-object-response.json", null, null, PollutionType.DUST.getPollutionName()),
                Arguments.of("smog-dummy-value-response.json", createHourlyUnitsSmog(), new Hourly(), PollutionType.DUST.getPollutionName()),
                Arguments.of("smog-correctly-response.json", createHourlyUnitsSmog(), createHourly(), PollutionType.DUST.getPollutionName()),

                Arguments.of("smog-null-values-response.json", createHourlyUnitsSmog(), createHourlyWithArraysContainingNulls(), PollutionType.PM10.getPollutionName()),
                Arguments.of("smog-null-object-response.json", null, null, PollutionType.PM10.getPollutionName()),
                Arguments.of("smog-dummy-value-response.json", createHourlyUnitsSmog(), new Hourly(), PollutionType.PM10.getPollutionName()),
                Arguments.of("smog-correctly-response.json", createHourlyUnitsSmog(), createHourly(), PollutionType.PM10.getPollutionName()),

                Arguments.of("smog-null-values-response.json", createHourlyUnitsSmog(), createHourlyWithArraysContainingNulls(), PollutionType.CO.getPollutionName()),
                Arguments.of("smog-null-object-response.json", null, null, PollutionType.CO.getPollutionName()),
                Arguments.of("smog-dummy-value-response.json", createHourlyUnitsSmog(), new Hourly(), PollutionType.CO.getPollutionName()),
                Arguments.of("smog-correctly-response.json", createHourlyUnitsSmog(), createHourly(), PollutionType.CO.getPollutionName()),

                Arguments.of("smog-null-values-response.json", createHourlyUnitsSmog(), createHourlyWithArraysContainingNulls(), PollutionType.NO2.getPollutionName()),
                Arguments.of("smog-null-object-response.json", null, null, PollutionType.NO2.getPollutionName()),
                Arguments.of("smog-dummy-value-response.json", createHourlyUnitsSmog(), new Hourly(), PollutionType.NO2.getPollutionName()),
                Arguments.of("smog-correctly-response.json", createHourlyUnitsSmog(), createHourly(), PollutionType.NO2.getPollutionName()),

                Arguments.of("smog-null-values-response.json", createHourlyUnitsSmog(), createHourlyWithArraysContainingNulls(), PollutionType.SO2.getPollutionName()),
                Arguments.of("smog-null-object-response.json", null, null, PollutionType.SO2.getPollutionName()),
                Arguments.of("smog-dummy-value-response.json", createHourlyUnitsSmog(), new Hourly(), PollutionType.SO2.getPollutionName()),
                Arguments.of("smog-correctly-response.json", createHourlyUnitsSmog(), createHourly(), PollutionType.SO2.getPollutionName()),

                Arguments.of("smog-null-values-response.json", createHourlyUnitsSmog(), createHourlyWithArraysContainingNulls(), PollutionType.O3.getPollutionName()),
                Arguments.of("smog-null-object-response.json", null, null, PollutionType.O3.getPollutionName()),
                Arguments.of("smog-dummy-value-response.json", createHourlyUnitsSmog(), new Hourly(), PollutionType.O3.getPollutionName()),
                Arguments.of("smog-correctly-response.json", createHourlyUnitsSmog(), createHourly(), PollutionType.O3.getPollutionName()),

                Arguments.of("smog-null-values-response.json", createHourlyUnitsSmog(), createHourlyWithArraysContainingNulls(), PollutionType.NH3.getPollutionName()),
                Arguments.of("smog-null-object-response.json", null, null, PollutionType.NH3.getPollutionName()),
                Arguments.of("smog-dummy-value-response.json", createHourlyUnitsSmog(), new Hourly(), PollutionType.NH3.getPollutionName()),
                Arguments.of("smog-correctly-response.json", createHourlyUnitsSmog(), createHourly(), PollutionType.NH3.getPollutionName()),

                Arguments.of("smog-null-values-response.json", createHourlyUnitsSmog(), createHourlyWithArraysContainingNulls(), PollutionType.DUST.getPollutionName()),
                Arguments.of("smog-null-object-response.json", null, null, PollutionType.DUST.getPollutionName()),
                Arguments.of("smog-dummy-value-response.json", createHourlyUnitsSmog(), new Hourly(), PollutionType.DUST.getPollutionName()),
                Arguments.of("smog-correctly-response.json", createHourlyUnitsSmog(), createHourly(), PollutionType.DUST.getPollutionName())
        );
    }

    @ParameterizedTest()
    @MethodSource("getIndexMetrics")
    void getIndexMetrics(String path, HourlyUnits hourlyUnits, Hourly hourly, String index, SoftAssertions softly) {
        prepareMockedResponseFromFile(URL_PATH, path);
        GenericMetric genericMetric = openMeteoAirQualityClient.getMetrics(LATITUDE, LONGITUDE, index, START, END);
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

    private static Stream<Arguments> getIndexMetrics() {
        return Stream.of(
                Arguments.of("index-correctly-response.json", createHourlyUnitswithEmptyPollenType(), createHourlyForIndexMetrics(), PollutionType.AOD.getPollutionName()),
                Arguments.of("smog-null-values-response.json", createHourlyUnitsSmog(), createHourlyWithArraysContainingNulls(), PollutionType.AOD.getPollutionName()),
                Arguments.of("smog-null-object-response.json", null, null, PollutionType.AOD.getPollutionName()),
                Arguments.of("smog-dummy-value-response.json", createHourlyUnitsSmog(), new Hourly(), PollutionType.AOD.getPollutionName()),

                Arguments.of("index-correctly-response.json", createHourlyUnitswithEmptyPollenType(), createHourlyForIndexMetrics(), PollutionType.UVINDEX.getPollutionName()),
                Arguments.of("smog-null-values-response.json", createHourlyUnitsSmog(), createHourlyWithArraysContainingNulls(), PollutionType.UVINDEX.getPollutionName()),
                Arguments.of("smog-null-object-response.json", null, null, PollutionType.UVINDEX.getPollutionName()),
                Arguments.of("smog-dummy-value-response.json", createHourlyUnitsSmog(), new Hourly(), PollutionType.UVINDEX.getPollutionName()),

                Arguments.of("index-correctly-response.json", createHourlyUnitswithEmptyPollenType(), createHourlyForIndexMetrics(), PollutionType.UVINDEXCLEARSKY.getPollutionName()),
                Arguments.of("smog-null-values-response.json", createHourlyUnitsSmog(), createHourlyWithArraysContainingNulls(), PollutionType.UVINDEXCLEARSKY.getPollutionName()),
                Arguments.of("smog-null-object-response.json", null, null, PollutionType.UVINDEXCLEARSKY.getPollutionName()),
                Arguments.of("smog-dummy-value-response.json", createHourlyUnitsSmog(), new Hourly(), PollutionType.UVINDEXCLEARSKY.getPollutionName())


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

    private static Hourly createHourlyForIndexMetrics() {
        List<LocalDateTime> time = loadLocalDateTimeTestValues("date-test-values.txt");
        List<Double> indexValues = loadDoubleTestValues("areosol-test-values.txt");
        return new Hourly(time, indexValues);
    }

    private static HourlyUnits createHourlyUnits() {
        String time = "iso8601";
        String pollen = "grains/m³";
        return new HourlyUnits(time, pollen);
    }

    private static HourlyUnits createHourlyUnitsSmog() {
        String time = "iso8601";
        String pollen = "μg/m³";
        return new HourlyUnits(time, pollen);
    }

    private static HourlyUnits createHourlyUnitswithEmptyPollenType() {
        String time = "iso8601";
        String pollen = "";
        return new HourlyUnits(time, pollen);
    }

}