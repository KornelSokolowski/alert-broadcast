package com.app.alertbroadcast.export;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaAlertProducer {

    private final KafkaTemplate<String, Alert> kafkaTemplate;

    public void sendDataSynchronously(String topic, String key, Alert message) {
        try {
            SendResult<String, Alert> result = kafkaTemplate.send(topic, key, message).get();
            log.info("Sending data to kafka succeeded {}", result.toString());
        } catch (Exception e) {
            log.error("A exception occurred during sending kafka message ", e);
        }
    }
}
