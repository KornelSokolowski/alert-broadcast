package com.app.alertbroadcast.service;

import com.app.alertbroadcast.client.AbstractMockedServerIT;
import com.app.alertbroadcast.client.feign.OpenMeteoAirQualityClient;
import com.app.alertbroadcast.client.model.PollutionProperties;
import com.app.alertbroadcast.client.model.airquality.GenericMetric;
import com.app.alertbroadcast.client.model.airquality.pollution.PollutionAlertLevel;
import com.app.alertbroadcast.client.model.airquality.pollution.PollutionType;
import com.app.alertbroadcast.export.Alert;
import com.app.alertbroadcast.utils.FileLoader;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class PollutionServiceTest extends AbstractMockedServerIT {
    @Mock
    private OpenMeteoAirQualityClient openMeteoAirQualityClient;
    @Mock
    private PollutionProperties pollutionProperties;
    @InjectMocks
    private PollutionService pollutionService;

    @ParameterizedTest()
    @MethodSource("getMetricsForAlerts")
    void getAlertsList(Double expectedAlertValue, Double alertLevel, LocalDateTime timeAlert) {
        Mockito.when(openMeteoAirQualityClient.getMetrics(any(), any(), any(), any(), any()))
                .thenReturn(createGenericMetricForIndexMetricsPM25());
        List<Alert> alerts = pollutionService.getAlertsListByAlertLevel(PollutionType.PM10, alertLevel);
        Assertions.assertThat(alerts.size()).isEqualTo(96);
        Assertions.assertThat(alerts.get(0).getDate()).isEqualTo(timeAlert);
        Assertions.assertThat(alerts.get(0).getValue()).isEqualTo(expectedAlertValue);
    }

    @ParameterizedTest()
    @MethodSource("getMetricsForAlerts")
    void getFirstAlert(Double expectedAlertValue, Double alertLevel, LocalDateTime timeAlert) {
        Mockito.when(openMeteoAirQualityClient.getMetrics(any(), any(), any(), any(), any()))
                .thenReturn(createGenericMetricForIndexMetricsPM25());
        Optional<Alert> firstAlert = pollutionService.getFirstAlert(PollutionType.PM10, alertLevel);
        Double value = firstAlert.get().getValue();
        LocalDateTime date = firstAlert.get().getDate();
        Assertions.assertThat(value).isEqualTo(expectedAlertValue);
        Assertions.assertThat(date).isEqualTo(timeAlert);
    }

    @Test
    void getAlertList() {
        Mockito.when(openMeteoAirQualityClient.getMetrics(any(), any(), any(), any(), any()))
                .thenReturn(createGenericMetricForIndexMetricsPM25());
        List<Alert> alerts = pollutionService.getAlertsListByAlertLevel(PollutionType.PM10, 0.40);
        List<LocalDateTime> expectedTime = loadLocalDateTimeTestValues("alert-correctly-dates.txt");
        List<Double> expectedValues = loadDoubleTestValues("alert-correctly-values.txt");

        List<Double> actualDoubleList = alerts.stream().map(Alert::getValue).toList();
        List<LocalDateTime> timeList = alerts.stream().map(Alert::getDate).toList();
        List<PollutionType> pollutionTypes = alerts.stream().map(Alert::getPollutionTypes).toList();

        Assertions.assertThatList(actualDoubleList).isEqualTo(expectedValues);
        Assertions.assertThatList(timeList).isEqualTo(expectedTime);
        Assertions.assertThatList(pollutionTypes).contains(PollutionType.PM10);
        Assertions.assertThat(alerts.size()).isEqualTo(96);
    }

    private static Stream<Arguments> getMetricsForAlerts() {
        return Stream.of(
                Arguments.of(14.2, 0.40, "2023-06-21T00:00")
        );
    }


    @Test
    void getNoAlerts() {
        Mockito.when(openMeteoAirQualityClient.getMetrics(any(), any(), any(), any(), any()))
                .thenReturn(createGenericMetricForIndexMetricsPM25());
        Optional<Alert> firstAlert = pollutionService.getFirstAlert(PollutionType.PM10, 40.00);
        Assertions.assertThat(firstAlert).isEmpty();
    }

    @Test
    void getPollutionLevelWithNullValues() {
        Mockito.when(openMeteoAirQualityClient.getMetrics(any(), any(), any(), any(), any()))
                .thenReturn(createGenericMetricForIndexMetricsForPM25WithNullValues());
        Mockito.when(pollutionProperties.getPollution()).thenReturn(getPollutions());
        List<Alert> pollutionLevel = pollutionService.getPollutionAlerts(PollutionType.PM25);
        Assertions.assertThatList(pollutionLevel).isEmpty();
    }

    @Test
    void getPollutionLevelWithEmptyArrays() {
        Mockito.when(openMeteoAirQualityClient.getMetrics(any(), any(), any(), any(), any()))
                .thenReturn(createGenericMetricForIndexMetricsForPM25WithEmptyValues());
        Mockito.when(pollutionProperties.getPollution()).thenReturn(getPollutions());
        List<Alert> pollutionLevel = pollutionService.getPollutionAlerts(PollutionType.PM25);
        Assertions.assertThatList(pollutionLevel).isEmpty();
    }

    @Test
    void getPollutionLevelNull() {
        List<Alert> pollutionLevel = pollutionService.getPollutionAlerts(PollutionType.PM25);
        Assertions.assertThat(pollutionLevel).isEmpty();
    }

    private static Stream<Arguments> getCorrectlyPollutionLevelStatusForPollenType() {
        return Stream.of(
                Arguments.of(createGenericMetricForIndexMeMetricsForPM25WithPollutionLevels(), PollutionType.PM25, expectedAlertsForPm25()),
                Arguments.of(createGenericMetricForIndexMetricsForPm10WithPollutionLevels(), PollutionType.PM10, expectedAlertsForPM10()),
                Arguments.of(createGenericMetricForIndexForO3WithPollutionLevels(), PollutionType.O3, expectedAlertsForO3()),
                Arguments.of(createGenericMetricForIndexForNO2WithPollutionLevels(), PollutionType.NO2, expectedAlertsForNO2()),
                Arguments.of(createGenericMetricForIndexForSO2WithPollutionLevels(), PollutionType.SO2, expectedAlertsForSO2()));
    }

    @ParameterizedTest
    @MethodSource("getCorrectlyPollutionLevelStatusForPollenType")
    void getCorrectlyPollutionLevelStatusForPollens(GenericMetric genericMetric, PollutionType pollutionType, List<Alert> expectedAlerts) {
        Mockito.when(openMeteoAirQualityClient.getMetrics(any(), any(), any(), any(), any()))
                .thenReturn(genericMetric);
        Mockito.when(pollutionProperties.getPollution()).thenReturn(getPollutions());
        List<Alert> pollutionAlerts = pollutionService.getPollutionAlerts(pollutionType);
        Assertions.assertThat(pollutionAlerts).usingRecursiveComparison().isEqualTo(expectedAlerts);
        pollutionAlerts
                .forEach(alert -> Assertions.assertThat(alert.getPollutionTypes())
                        .isEqualTo(pollutionType));
    }

    private List<PollutionProperties.Pollution> getPollutions() {
        PollutionProperties.Pollution pollutionPM25 = new PollutionProperties.Pollution();
        pollutionPM25.setName("pm2_5");
        pollutionPM25.setFair(11);
        pollutionPM25.setModerate(21);
        pollutionPM25.setPoor(25);
        pollutionPM25.setVeryPoor(51);
        pollutionPM25.setExtremelyPoor(75);
        PollutionProperties.Pollution pollutionPM10 = new PollutionProperties.Pollution();
        pollutionPM10.setName("pm10");
        pollutionPM10.setFair(21);
        pollutionPM10.setModerate(41);
        pollutionPM10.setPoor(51);
        pollutionPM10.setVeryPoor(101);
        pollutionPM10.setExtremelyPoor(151);
        PollutionProperties.Pollution pollutionO3 = new PollutionProperties.Pollution();
        pollutionO3.setName("ozone");
        pollutionO3.setFair(51);
        pollutionO3.setModerate(101);
        pollutionO3.setPoor(131);
        pollutionO3.setVeryPoor(241);
        pollutionO3.setExtremelyPoor(381);
        PollutionProperties.Pollution pollutionSO2 = new PollutionProperties.Pollution();
        pollutionSO2.setName("sulphur_dioxide");
        pollutionSO2.setFair(101);
        pollutionSO2.setModerate(201);
        pollutionSO2.setPoor(351);
        pollutionSO2.setVeryPoor(501);
        pollutionSO2.setExtremelyPoor(751);
        PollutionProperties.Pollution pollutionNO2 = new PollutionProperties.Pollution();
        pollutionNO2.setName("nitrogen_dioxide");
        pollutionNO2.setFair(41);
        pollutionNO2.setModerate(91);
        pollutionNO2.setPoor(121);
        pollutionNO2.setVeryPoor(231);
        pollutionNO2.setExtremelyPoor(341);
        return List.of(pollutionPM25, pollutionPM10, pollutionO3, pollutionSO2, pollutionNO2);

    }

    private static List<Alert> expectedAlertsForPm25() {
        Alert alertForFairLevelPollution = new Alert();
        alertForFairLevelPollution.setPollutionTypes(PollutionType.PM25);
        alertForFairLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.FAIR);
        alertForFairLevelPollution.setDate(LocalDateTime.of(2023, 6, 21, 00, 00));
        alertForFairLevelPollution.setValue(14.2);
        Alert alertForModerateLevelPollution = new Alert();
        alertForModerateLevelPollution.setPollutionTypes(PollutionType.PM25);
        alertForModerateLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.MODERATE);
        alertForModerateLevelPollution.setDate(LocalDateTime.of(2023, 6, 21, 01, 00));
        alertForModerateLevelPollution.setValue(24.7);
        Alert alertForPoorLevelPollution = new Alert();
        alertForPoorLevelPollution.setPollutionTypes(PollutionType.PM25);
        alertForPoorLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.POOR);
        alertForPoorLevelPollution.setDate(LocalDateTime.of(2023, 6, 21, 02, 00));
        alertForPoorLevelPollution.setValue(34.2);
        Alert alertForVeryPoorLevelPollution = new Alert();
        alertForVeryPoorLevelPollution.setPollutionTypes(PollutionType.PM25);
        alertForVeryPoorLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.VERY_POOR);
        alertForVeryPoorLevelPollution.setDate(LocalDateTime.of(2023, 6, 21, 03, 00));
        alertForVeryPoorLevelPollution.setValue(57.6);
        Alert alertForGoodLevelPollution = new Alert();
        alertForGoodLevelPollution.setPollutionTypes(PollutionType.PM25);
        alertForGoodLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.GOOD);
        alertForGoodLevelPollution.setDate(LocalDateTime.of(2023, 6, 21, 04, 00));
        alertForGoodLevelPollution.setValue(5.6);
        Alert alertForExtremelyPoorLevelPollution = new Alert();
        alertForExtremelyPoorLevelPollution.setPollutionTypes(PollutionType.PM25);
        alertForExtremelyPoorLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.EXTREMELY_POOR);
        alertForExtremelyPoorLevelPollution.setDate(LocalDateTime.of(2023, 6, 21, 23, 00));
        alertForExtremelyPoorLevelPollution.setValue(75.8);
        return List.of(alertForFairLevelPollution, alertForModerateLevelPollution,
                alertForPoorLevelPollution, alertForVeryPoorLevelPollution,
                alertForGoodLevelPollution, alertForExtremelyPoorLevelPollution);
    }

    private static List<Alert> expectedAlertsForPM10() {
        Alert alertForGoodLevelPollution = new Alert();
        alertForGoodLevelPollution.setPollutionTypes(PollutionType.PM10);
        alertForGoodLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.GOOD);
        alertForGoodLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 00, 00));
        alertForGoodLevelPollution.setValue(12.3);
        Alert alertForFairLevelPollution = new Alert();
        alertForFairLevelPollution.setPollutionTypes(PollutionType.PM10);
        alertForFairLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.FAIR);
        alertForFairLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 01, 00));
        alertForFairLevelPollution.setValue(23.1);
        Alert alertForModerateLevelPollution = new Alert();
        alertForModerateLevelPollution.setPollutionTypes(PollutionType.PM10);
        alertForModerateLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.MODERATE);
        alertForModerateLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 02, 00));
        alertForModerateLevelPollution.setValue(44.6);
        Alert alertForPoorLevelPollution = new Alert();
        alertForPoorLevelPollution.setPollutionTypes(PollutionType.PM10);
        alertForPoorLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.POOR);
        alertForPoorLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 03, 00));
        alertForPoorLevelPollution.setValue(56.0);
        Alert alertForVeryPoorLevelPollution = new Alert();
        alertForVeryPoorLevelPollution.setPollutionTypes(PollutionType.PM10);
        alertForVeryPoorLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.VERY_POOR);
        alertForVeryPoorLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 04, 00));
        alertForVeryPoorLevelPollution.setValue(116.8);
        Alert alertForExtremelyPoorLevelPollution = new Alert();
        alertForExtremelyPoorLevelPollution.setPollutionTypes(PollutionType.PM10);
        alertForExtremelyPoorLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.EXTREMELY_POOR);
        alertForExtremelyPoorLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 05, 00));
        alertForExtremelyPoorLevelPollution.setValue(277.8);

        return List.of(alertForGoodLevelPollution, alertForFairLevelPollution, alertForModerateLevelPollution,
                alertForPoorLevelPollution, alertForVeryPoorLevelPollution,
                alertForExtremelyPoorLevelPollution);
    }

    private static List<Alert> expectedAlertsForO3() {
        Alert alertForGoodLevelPollution = new Alert();
        alertForGoodLevelPollution.setPollutionTypes(PollutionType.O3);
        alertForGoodLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.GOOD);
        alertForGoodLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 00, 00));
        alertForGoodLevelPollution.setValue(47.0);
        Alert alertForFairLevelPollution = new Alert();
        alertForFairLevelPollution.setPollutionTypes(PollutionType.O3);
        alertForFairLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.FAIR);
        alertForFairLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 01, 00));
        alertForFairLevelPollution.setValue(58.0);
        Alert alertForModerateLevelPollution = new Alert();
        alertForModerateLevelPollution.setPollutionTypes(PollutionType.O3);
        alertForModerateLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.MODERATE);
        alertForModerateLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 02, 00));
        alertForModerateLevelPollution.setValue(127.0);
        Alert alertForPoorLevelPollution = new Alert();
        alertForPoorLevelPollution.setPollutionTypes(PollutionType.O3);
        alertForPoorLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.POOR);
        alertForPoorLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 03, 00));
        alertForPoorLevelPollution.setValue(143.0);
        Alert alertForVeryPoorLevelPollution = new Alert();
        alertForVeryPoorLevelPollution.setPollutionTypes(PollutionType.O3);
        alertForVeryPoorLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.VERY_POOR);
        alertForVeryPoorLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 04, 00));
        alertForVeryPoorLevelPollution.setValue(247.0);
        Alert alertForExtremelyPoorLevelPollution = new Alert();
        alertForExtremelyPoorLevelPollution.setPollutionTypes(PollutionType.O3);
        alertForExtremelyPoorLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.EXTREMELY_POOR);
        alertForExtremelyPoorLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 05, 00));
        alertForExtremelyPoorLevelPollution.setValue(452.0);
        return List.of(alertForGoodLevelPollution, alertForFairLevelPollution, alertForModerateLevelPollution,
                alertForPoorLevelPollution, alertForVeryPoorLevelPollution,
                alertForExtremelyPoorLevelPollution);
    }

    private static List<Alert> expectedAlertsForSO2() {
        Alert alertForGoodLevelPollution = new Alert();
        alertForGoodLevelPollution.setPollutionTypes(PollutionType.SO2);
        alertForGoodLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.GOOD);
        alertForGoodLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 00, 00));
        alertForGoodLevelPollution.setValue(91.2);
        Alert alertForFairLevelPollution = new Alert();
        alertForFairLevelPollution.setPollutionTypes(PollutionType.SO2);
        alertForFairLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.FAIR);
        alertForFairLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 01, 00));
        alertForFairLevelPollution.setValue(111.4);
        Alert alertForModerateLevelPollution = new Alert();
        alertForModerateLevelPollution.setPollutionTypes(PollutionType.SO2);
        alertForModerateLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.MODERATE);
        alertForModerateLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 02, 00));
        alertForModerateLevelPollution.setValue(201.5);
        Alert alertForPoorLevelPollution = new Alert();
        alertForPoorLevelPollution.setPollutionTypes(PollutionType.SO2);
        alertForPoorLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.POOR);
        alertForPoorLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 03, 00));
        alertForPoorLevelPollution.setValue(352.5);
        Alert alertForVeryPoorLevelPollution = new Alert();
        alertForVeryPoorLevelPollution.setPollutionTypes(PollutionType.SO2);
        alertForVeryPoorLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.VERY_POOR);
        alertForVeryPoorLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 04, 00));
        alertForVeryPoorLevelPollution.setValue(611.5);
        Alert alertForExtremelyPoorLevelPollution = new Alert();
        alertForExtremelyPoorLevelPollution.setPollutionTypes(PollutionType.SO2);
        alertForExtremelyPoorLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.EXTREMELY_POOR);
        alertForExtremelyPoorLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 05, 00));
        alertForExtremelyPoorLevelPollution.setValue(801.8);
        return List.of(alertForGoodLevelPollution, alertForFairLevelPollution, alertForModerateLevelPollution,
                alertForPoorLevelPollution, alertForVeryPoorLevelPollution,
                alertForExtremelyPoorLevelPollution);
    }

    private static List<Alert> expectedAlertsForNO2() {
        Alert alertForGoodLevelPollution = new Alert();
        alertForGoodLevelPollution.setPollutionTypes(PollutionType.NO2);
        alertForGoodLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.GOOD);
        alertForGoodLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 00, 00));
        alertForGoodLevelPollution.setValue(16.0);
        Alert alertForFairLevelPollution = new Alert();
        alertForFairLevelPollution.setPollutionTypes(PollutionType.NO2);
        alertForFairLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.FAIR);
        alertForFairLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 01, 00));
        alertForFairLevelPollution.setValue(45.1);
        Alert alertForModerateLevelPollution = new Alert();
        alertForModerateLevelPollution.setPollutionTypes(PollutionType.NO2);
        alertForModerateLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.MODERATE);
        alertForModerateLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 02, 00));
        alertForModerateLevelPollution.setValue(96.2);
        Alert alertForPoorLevelPollution = new Alert();
        alertForPoorLevelPollution.setPollutionTypes(PollutionType.NO2);
        alertForPoorLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.POOR);
        alertForPoorLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 03, 00));
        alertForPoorLevelPollution.setValue(128.0);
        Alert alertForVeryPoorLevelPollution = new Alert();
        alertForVeryPoorLevelPollution.setPollutionTypes(PollutionType.NO2);
        alertForVeryPoorLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.VERY_POOR);
        alertForVeryPoorLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 04, 00));
        alertForVeryPoorLevelPollution.setValue(246.1);
        Alert alertForExtremelyPoorLevelPollution = new Alert();
        alertForExtremelyPoorLevelPollution.setPollutionTypes(PollutionType.NO2);
        alertForExtremelyPoorLevelPollution.setPollutionAlertLevel(PollutionAlertLevel.EXTREMELY_POOR);
        alertForExtremelyPoorLevelPollution.setDate(LocalDateTime.of(2023, 6, 26, 05, 00));
        alertForExtremelyPoorLevelPollution.setValue(378.2);
        return List.of(alertForGoodLevelPollution, alertForFairLevelPollution, alertForModerateLevelPollution,
                alertForPoorLevelPollution, alertForVeryPoorLevelPollution,
                alertForExtremelyPoorLevelPollution);
    }

    @SneakyThrows
    private static GenericMetric createGenericMetricForIndexMetricsPM25() {
        String load = FileLoader.load("pm25.json");
        return objectMapper.readValue(load, GenericMetric.class);
    }

    @SneakyThrows
    private static GenericMetric createGenericMetricForIndexMetricsForPM25WithEmptyValues() {
        String load = FileLoader.load("pm25-empty.json");
        return objectMapper.readValue(load, GenericMetric.class);
    }

    @SneakyThrows
    private static GenericMetric createGenericMetricForIndexMetricsForPM25WithNullValues() {
        String load = FileLoader.load("pm25-nulls.json");
        return objectMapper.readValue(load, GenericMetric.class);
    }

    @SneakyThrows
    private static GenericMetric createGenericMetricForIndexMeMetricsForPM25WithPollutionLevels() {
        String load = FileLoader.load("pm25-correctly-levels.json");
        return objectMapper.readValue(load, GenericMetric.class);
    }

    @SneakyThrows
    private static GenericMetric createGenericMetricForIndexMetricsPM10() {
        String load = FileLoader.load("pm10.json");
        return objectMapper.readValue(load, GenericMetric.class);
    }

    @SneakyThrows
    private static GenericMetric createGenericMetricForIndexMetricsForPm10WithPollutionLevels() {
        String load = FileLoader.load("pm10-correctly-levels.json");
        return objectMapper.readValue(load, GenericMetric.class);
    }

    @SneakyThrows
    private static GenericMetric createGenericMetricForIndexMetricsO3() {
        String load = FileLoader.load("o3.json");
        return objectMapper.readValue(load, GenericMetric.class);
    }

    @SneakyThrows
    private static GenericMetric createGenericMetricForIndexForO3WithPollutionLevels() {
        String load = FileLoader.load("o3-correctly-levels.json");
        return objectMapper.readValue(load, GenericMetric.class);
    }

    @SneakyThrows
    private static GenericMetric createGenericMetricForIndexMetricsSO2() {
        String load = FileLoader.load("so2.json");
        return objectMapper.readValue(load, GenericMetric.class);
    }

    @SneakyThrows
    private static GenericMetric createGenericMetricForIndexForSO2WithPollutionLevels() {
        String load = FileLoader.load("so2-correctly-levels.json");
        return objectMapper.readValue(load, GenericMetric.class);
    }

    @SneakyThrows
    private static GenericMetric createGenericMetricForIndexMetricsNO2() {
        String load = FileLoader.load("no2.json");
        return objectMapper.readValue(load, GenericMetric.class);
    }

    @SneakyThrows
    private static GenericMetric createGenericMetricForIndexForNO2WithPollutionLevels() {
        String load = FileLoader.load("no2-correctly-levels.json");
        return objectMapper.readValue(load, GenericMetric.class);
    }
}