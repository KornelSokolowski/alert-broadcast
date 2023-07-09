package com.app.alertbroadcast.service;

import com.app.alertbroadcast.client.feign.OpenMeteoAirQualityClient;
import com.app.alertbroadcast.client.model.PollutionProperties;
import com.app.alertbroadcast.client.model.airquality.GenericMetric;
import com.app.alertbroadcast.client.model.airquality.Hourly;
import com.app.alertbroadcast.client.model.airquality.pollution.PollutionAlertLevel;
import com.app.alertbroadcast.client.model.airquality.pollution.PollutionType;
import com.app.alertbroadcast.export.Alert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PollutionService {

    private static final Double LATITUDE = 52.549995;
    private static final Double LONGITUDE = 16.75;
    private static final LocalDate START = LocalDate.now();
    private static final LocalDate END = START.plusDays(3);

    private final OpenMeteoAirQualityClient openMeteoAirQualityClient;
    private final PollutionProperties pollutionProperties;

    public PollutionService(OpenMeteoAirQualityClient openMeteoAirQualityClient, PollutionProperties pollutionProperties) {
        this.openMeteoAirQualityClient = openMeteoAirQualityClient;
        this.pollutionProperties = pollutionProperties;
    }

    public Optional<Alert> getFirstAlert(PollutionType pollutionType, double alertLevel) {
        Hourly hourly = extractHourly(pollutionType);
        List<LocalDateTime> localDateTimes = hourly.getTime();
        List<Double> doubleList = hourly.getPollutionType();
        if (doubleList != null && !doubleList.isEmpty() && !doubleList.contains(null)) {
            for (int j = 0; j < doubleList.size(); j++) {
                if (doubleList.get(j) > alertLevel) {
                    Alert alert = new Alert();
                    alert.setPollutionTypes(pollutionType);
                    alert.setDate(localDateTimes.get(j));
                    alert.setValue(doubleList.get(j));
                    return Optional.of(alert);
                }
            }
        }
        return Optional.empty();
    }

    public List<Alert> getAlertsListByAlertLevel(PollutionType pollutionType, Double alertLevel) {
        Hourly hourly = extractHourly(pollutionType);
        List<LocalDateTime> localDateTimes = hourly.getTime();
        List<Double> doubleList = hourly.getPollutionType();
        List<Alert> alerts = new ArrayList<>();
        if (doubleList != null && !doubleList.isEmpty() && !doubleList.contains(null)) {
            for (int i = 0; i < doubleList.size(); i++) {
                if (doubleList.get(i) > alertLevel) {
                    Alert alert = new Alert();
                    alert.setPollutionTypes(pollutionType);
                    alert.setDate(localDateTimes.get(i));
                    alert.setValue(doubleList.get(i));
                    alerts.add(alert);
                }
            }
        }
        return alerts;
    }

    public List<Alert> getPollutionAlerts(PollutionType pollutionType) {
        Optional<PollutionProperties.Pollution> optionalPollution = pollutionProperties.getPollution().stream()
                .filter(pollution -> pollution.getName().equals(pollutionType.getPollutionName()))
                .findAny();
        List<Alert> alertsLevel = new ArrayList<>();

        optionalPollution.ifPresent(pollution -> {
            Hourly hourly = extractHourly(pollutionType);
            List<LocalDateTime> localDateTimes = hourly.getTime();
            List<Double> doubleList = hourly.getPollutionType();

            int fair = pollution.getFair();
            int moderate = pollution.getModerate();
            int poor = pollution.getPoor();
            int veryPoor = pollution.getVeryPoor();
            int extremelyPoor = pollution.getExtremelyPoor();
            log.info("alert levels ## fair: {}, moderate: {}, poor: {}, very poor: {}, extremely poor: {}", fair, moderate, poor, veryPoor, extremelyPoor);

            if (doubleList != null && !doubleList.isEmpty() && !doubleList.contains(null)) {
                for (int i = 0; i < doubleList.size(); i++) {
                    if (doubleList.get(i) < fair) {
                        Alert alert = new Alert();
                        alert.setPollutionTypes(pollutionType);
                        alert.setDate(localDateTimes.get(i));
                        alert.setValue(doubleList.get(i));
                        alert.setPollutionAlertLevel(PollutionAlertLevel.GOOD);
                        alertsLevel.add(alert);

                    }
                    if (doubleList.get(i) >= fair && doubleList.get(i) < moderate) {
                        Alert alert = new Alert();
                        alert.setPollutionTypes(pollutionType);
                        alert.setDate(localDateTimes.get(i));
                        alert.setValue(doubleList.get(i));
                        alert.setPollutionAlertLevel(PollutionAlertLevel.FAIR);
                        alertsLevel.add(alert);

                    }
                    if (doubleList.get(i) >= moderate && doubleList.get(i) < poor) {
                        Alert alert = new Alert();
                        alert.setPollutionTypes(pollutionType);
                        alert.setDate(localDateTimes.get(i));
                        alert.setValue(doubleList.get(i));
                        alert.setPollutionAlertLevel(PollutionAlertLevel.MODERATE);
                        alertsLevel.add(alert);

                    }
                    if (doubleList.get(i) >= poor && doubleList.get(i) < veryPoor) {
                        Alert alert = new Alert();
                        alert.setPollutionTypes(pollutionType);
                        alert.setDate(localDateTimes.get(i));
                        alert.setValue(doubleList.get(i));
                        alert.setPollutionAlertLevel(PollutionAlertLevel.POOR);
                        alertsLevel.add(alert);
                    }
                    if (doubleList.get(i) >= veryPoor && doubleList.get(i) < extremelyPoor) {
                        Alert alert = new Alert();
                        alert.setPollutionTypes(pollutionType);
                        alert.setDate(localDateTimes.get(i));
                        alert.setValue(doubleList.get(i));
                        alert.setPollutionAlertLevel(PollutionAlertLevel.VERY_POOR);
                        alertsLevel.add(alert);
                    }
                    if (doubleList.get(i) >= extremelyPoor) {
                        Alert alert = new Alert();
                        alert.setPollutionTypes(pollutionType);
                        alert.setDate(localDateTimes.get(i));
                        alert.setValue(doubleList.get(i));
                        alert.setPollutionAlertLevel(PollutionAlertLevel.EXTREMELY_POOR);
                        alertsLevel.add(alert);
                    }
                }
            }
        });
        return alertsLevel;
    }

    private Hourly extractHourly(PollutionType pollutionType) {
        GenericMetric metrics = openMeteoAirQualityClient.getMetrics(LATITUDE, LONGITUDE,
                pollutionType.getPollutionName(), START, END);
        return metrics.getHourly();
    }
}

