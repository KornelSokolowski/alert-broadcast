package com.app.alertbroadcast.client.feign;

import com.app.alertbroadcast.client.AbstractMockedServerIT;
import com.app.alertbroadcast.client.model.weatherforecast.Hourly;
import com.app.alertbroadcast.client.model.weatherforecast.HourlyUnits;
import com.app.alertbroadcast.client.model.weatherforecast.WeatherMetric;
import com.app.alertbroadcast.client.model.weatherforecast.WeatherVariable;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
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
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest(classes = {FeignConfiguration.class, OpenMeteoWeatherForecastClient.class})
@ImportAutoConfiguration({FeignAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class})
@ExtendWith(SoftAssertionsExtension.class)
class OpenMeteoWeatherForecastClientTest extends AbstractMockedServerIT {

    private static final Double LATITUDE = 52.52;
    private static final Double LONGITUDE = 13.419998;
    private static final Double generationTimeMs = 0.32901763916015625;
    private static final Long utcOffsetSeconds = 0L;
    private static final String timezone = "GMT";
    private static final String timezoneAbbreviation = "GMT";
    private static final Double elevation = 38.0;
    private static final String URL_PATH = "/v1/forecast";
    private static final LocalDate START = LocalDate.of(2023, 9, 4);
    private static final LocalDate END = START.plusDays(6);

    @Autowired
    private OpenMeteoWeatherForecastClient openMeteoWeatherForecastClient;

    @ParameterizedTest
    @MethodSource("getWeatherMetricsArguments")
    void getWeatherMetrics(String path, HourlyUnits hourlyUnits, Hourly hourly, String weatherVariable, SoftAssertions softly) {
        prepareMockedResponseFromFile(URL_PATH, path);

        WeatherMetric weatherMetric = openMeteoWeatherForecastClient.getMetrics(LATITUDE, LONGITUDE, weatherVariable, START, END);
        softly.assertThat(weatherMetric)
                .returns(LATITUDE,WeatherMetric::getLatitude)
                .returns(LONGITUDE,WeatherMetric::getLongitude)
                .returns(generationTimeMs, WeatherMetric::getGenerationTimeMs)
                .returns(utcOffsetSeconds, WeatherMetric::getUtcOffsetSeconds)
                .returns(timezone,WeatherMetric::getTimezone)
                .returns(timezoneAbbreviation, WeatherMetric::getTimezoneAbbreviation)
                .returns(elevation, WeatherMetric::getElevation);
        softly.assertThat(weatherMetric.getHourlyUnits())
                .usingRecursiveComparison().isEqualTo(hourlyUnits);
        softly.assertThat(weatherMetric.getHourly())
                .usingRecursiveComparison()
                .isEqualTo(hourly);
    }

    private static Stream<Arguments> getWeatherMetricsArguments() {
        return Stream.of(
                Arguments.of("weather-correctly-response.json",createHourlyUnits(),createHourly(),
                        WeatherVariable.TEMPERATURE_2M.getWeatherVariableName()));
    }

    private static HourlyUnits createHourlyUnits(){
        String time = "iso8601";
        String weatherVariable = "°C";
        return new HourlyUnits(time, weatherVariable);
    }
    private static Hourly createHourly(){
        List<LocalDateTime> time = loadLocalDateTimeTestValues("weather-dates-test.txt");
        List<Double> indexValues = loadDoubleTestValues("weather-values-test.txt");
        return new Hourly(time,indexValues);
    }
}