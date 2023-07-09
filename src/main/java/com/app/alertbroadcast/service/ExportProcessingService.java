package com.app.alertbroadcast.service;

import com.app.alertbroadcast.client.model.airquality.pollution.PollutionType;
import com.app.alertbroadcast.export.Alert;
import com.app.alertbroadcast.export.KafkaAlertProducer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class ExportProcessingService {

    private final PollutionService pollutionService;
    private final KafkaAlertProducer kafkaAlertProducer;

    public ExportProcessingService(PollutionService pollutionService, KafkaAlertProducer kafkaAlertProducer) {
        this.pollutionService = pollutionService;
        this.kafkaAlertProducer = kafkaAlertProducer;
    }

    public void startExport() {
        LocalDateTime dateTime = LocalDateTime.now();
        String formattedTimestamp = dateTime.format(DateTimeFormatter.ISO_DATE_TIME);
        Arrays.stream(PollutionType.values())
                .forEach(pollutionType -> {
                    List<Alert> pollutionAlerts = pollutionService.getPollutionAlerts(pollutionType);
                    pollutionAlerts.forEach(alert ->
                            kafkaAlertProducer.sendDataSynchronously(pollutionType.getPollutionName() + "_" + alert.getPollutionAlertLevel().getDescription(), formattedTimestamp, alert));
                });
    }
}
