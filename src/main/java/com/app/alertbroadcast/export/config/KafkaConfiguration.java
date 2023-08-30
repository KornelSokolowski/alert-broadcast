package com.app.alertbroadcast.export.config;

import com.app.alertbroadcast.export.Alert;
import com.app.alertbroadcast.export.WeatherInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class KafkaConfiguration {

    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaTemplate<String, Alert> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaTemplate<String, WeatherInfo> weatherInfoKafkaTemplate(){return new KafkaTemplate<>(producerFactory1());}

    @Bean
    public ProducerFactory<String, Alert> producerFactory() {
        Map<String, Object> config = new HashMap<>(kafkaProperties.buildProducerProperties());
         log.info("Kafka config: {}", config);
        return new DefaultKafkaProducerFactory<>(config);
    }
    @Bean
    public ProducerFactory<String,WeatherInfo> producerFactory1(){
        Map<String, Object> config = new HashMap<>(kafkaProperties.buildProducerProperties());
        log.info("Kafka config: {}", config);
        return new DefaultKafkaProducerFactory<>(config);
    }
}
