package com.app.alertbroadcast.service;

import com.app.alertbroadcast.client.model.airquality.pollution.PollutionType;
import com.app.alertbroadcast.export.Alert;
import com.app.alertbroadcast.export.KafkaAlertProducer;
import org.springframework.stereotype.Service;

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
        Arrays.stream(PollutionType.values())
                .forEach(pollutionType -> {
                    List<Alert> pollutionAlerts = pollutionService.getPollutionAlerts(pollutionType);
                    pollutionAlerts.forEach(alert ->
                            kafkaAlertProducer.sendDataSynchronously(pollutionType.getPollutionName() + "_" + alert.getPollutionAlertLevel().getDescription(),
                                    alert.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm")),
                                    alert));
                });
    }
}
