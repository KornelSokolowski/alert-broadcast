package com.app.alertbroadcast.export;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@EmbeddedKafka(
        partitions = 1,
        topics = {KafkaAlertProducerTest.TOPIC_NAME})
@SpringBootTest(
        properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
class KafkaAlertProducerTest {

    static final String TOPIC_NAME = "test.topic";
    private static final Alert ALERT = new Alert();

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaAlertProducer kafkaAlertProducer;

    @AfterEach
    void cleanUp() {
        this.embeddedKafkaBroker.destroy();
    }

    @Test
    void testSendDataSynchronously() {
        kafkaAlertProducer.sendDataSynchronously(TOPIC_NAME, "", ALERT);
        Consumer<String, Alert> consumer = buildTestKafkaConsumer();
        embeddedKafkaBroker.consumeFromEmbeddedTopics(consumer, TOPIC_NAME);
        ConsumerRecord<String, Alert> record = KafkaTestUtils.getSingleRecord(consumer, TOPIC_NAME);
        assertThat(record.value())
                .usingRecursiveComparison()
                .isEqualTo(ALERT);

    }

    private <K, V> Consumer<K, V> buildTestKafkaConsumer() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testMetricsEncodedAsSent", "true", this.embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        DefaultKafkaConsumerFactory<K, V> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
        return consumerFactory.createConsumer();
    }
}