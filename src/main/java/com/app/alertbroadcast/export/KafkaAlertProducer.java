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

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendDataSynchronously(String topic, String message) {
        try {
            SendResult<String, String> result = kafkaTemplate.send(topic, message).get();
            log.info("Sending data to kafka succeeded {}", result.toString());
        } catch (Exception e) {
            log.error("A exception occurred during sending kafka message ", e);
        }
    }
}
