package com.app.alertbroadcast.export;

import com.app.alertbroadcast.export.WeatherInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaWeatherProducer {

    private final KafkaTemplate<String, WeatherInfo> weatherInfoKafkaTemplate;

    public void sendWeatherDataSynchronously(String topic, String key, WeatherInfo weatherInfo) {
        try {
            SendResult<String, WeatherInfo> result = weatherInfoKafkaTemplate.send(topic, key, weatherInfo).get();
            log.info("Sending Weather Data to kafka succeeded {}", result.toString());
        } catch (Exception e) {
            log.error("A exception occurred during sending kafka message ", e);
        }
    }
}
