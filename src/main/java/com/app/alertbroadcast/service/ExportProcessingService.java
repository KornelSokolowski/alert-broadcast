package com.app.alertbroadcast.service;

import com.app.alertbroadcast.client.model.airquality.pollution.PollutionType;
import com.app.alertbroadcast.client.model.weatherforecast.WeatherVariable;
import com.app.alertbroadcast.export.Alert;
import com.app.alertbroadcast.export.KafkaAlertProducer;
import com.app.alertbroadcast.export.KafkaWeatherProducer;
import com.app.alertbroadcast.export.WeatherInfo;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class ExportProcessingService {

    private final PollutionService pollutionService;
    private final KafkaAlertProducer kafkaAlertProducer;
    private final WeatherInfoService weatherInfoService;
    private final KafkaWeatherProducer kafkaWeatherProducer;

    public ExportProcessingService(PollutionService pollutionService, KafkaAlertProducer kafkaAlertProducer, WeatherInfoService weatherInfoService, KafkaWeatherProducer kafkaWeatherProducer) {
        this.pollutionService = pollutionService;
        this.kafkaAlertProducer = kafkaAlertProducer;
        this.weatherInfoService = weatherInfoService;
        this.kafkaWeatherProducer = kafkaWeatherProducer;
    }


    public void startExport() {
        Arrays.stream(PollutionType.values())
                .forEach(pollutionType -> {
                    List<Alert> pollutionAlerts = pollutionService.getPollutionAlerts(pollutionType);
                    pollutionAlerts.forEach(alert ->
                            kafkaAlertProducer.sendDataSynchronously(pollutionType.getPollutionName() + "_" + alert.getPollutionAlertLevel().getDescription(),
                                    alert.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")),
                                    alert));
                });
        Arrays.stream(WeatherVariable.values())
                .forEach(weatherVariable -> {
                    List<WeatherInfo> weatherInfo = weatherInfoService.getWeatherByWeatherVariable(weatherVariable);
                    weatherInfo.forEach(weatherInfo1 -> kafkaWeatherProducer.sendWeatherDataSynchronously(weatherVariable.getWeatherVariableName(),
                            weatherInfo1.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")),
                            weatherInfo1));
                });
    }
}
