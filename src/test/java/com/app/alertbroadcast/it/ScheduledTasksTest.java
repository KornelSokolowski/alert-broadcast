package com.app.alertbroadcast.it;

import com.app.alertbroadcast.client.AbstractMockedServerIT;
import com.app.alertbroadcast.client.model.airquality.pollution.PollutionAlertLevel;
import com.app.alertbroadcast.client.model.airquality.pollution.PollutionType;
import com.app.alertbroadcast.export.Alert;
import com.app.alertbroadcast.scheduler.ScheduledTasks;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

//@EmbeddedKafka(
//        partitions = 1,
//        topics = {"pm10_Good","pm10_Fair","pm10_Moderate","pm10_Poor","pm10_Very_poor","pm10_Extremely_poor","ozone_Good",
//                "ozone_Fair","ozone_Moderate","ozone_Poor","ozone_Very_poor","ozone_Extremely_poor",
//                "pm2_5_Good","pm2_5_Fair","pm2_5_Moderate","pm2_5_Poor","pm2_5_Very_poor","pm2_5_Extremely_poor",
//                "sulphur_dioxide_Good","sulphur_dioxide_Fair","sulphur_dioxide_Moderate","sulphur_dioxide_Poor",
//                "sulphur_dioxide_Very_poor","sulphur_dioxide_Extremely_poor","nitrogen_dioxide_Good",
//                "nitrogen_dioxide_Fair","nitrogen_dioxide_Moderate","nitrogen_dioxide_Poor","nitrogen_dioxide_Very_poor",
//                "nitrogen_dioxide_Extremely_poor"})

@SpringBootTest(properties = "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@EmbeddedKafka(
        partitions = 1)

public class ScheduledTasksTest extends AbstractMockedServerIT {

    private static final String URL_PATH = "/v1/air-quality";

    @Autowired
    private ScheduledTasks scheduledTasks;
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Value("${spring.kafka.topics}")
    private String[] addTopics;


    @BeforeEach
    public void setUp() {
        createTopics();
    }

    @Test
    void getAlertLevels() {
        prepareMockedResponseFromFile(URL_PATH, "so2-correctly-levels.json");
        prepareMockedResponseFromFile(URL_PATH, "no2-correctly-levels.json");
        prepareMockedResponseFromFile(URL_PATH, "pm25-correctly-levels.json");
        prepareMockedResponseFromFile(URL_PATH, "pm10-correctly-levels.json");
        prepareMockedResponseResponseFromFileWithParameters(URL_PATH, "o3-correctly-levels.json", PollutionType.O3);
        prepareMockedResponseFromFile(URL_PATH,"o3-correctly-levels.json");
        scheduledTasks.runExport();
        String s = PollutionType.PM10.getPollutionName() + "_" + PollutionAlertLevel.GOOD.getDescription();
        String z = PollutionType.O3.getPollutionName() + "_" + PollutionAlertLevel.EXTREMELY_POOR.getDescription();
        Consumer<String, Alert> consumer = buildTestKafkaConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer,s);
        embeddedKafkaBroker.consumeFromEmbeddedTopics(consumer, z);
        LocalDateTime dateTime = LocalDateTime.now();
        String formattedTimestamp = dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        ConsumerRecord<String, Alert> record = KafkaTestUtils.getSingleRecord(consumer, z);
        ConsumerRecord<String, Alert> record1 = KafkaTestUtils.getSingleRecord(consumer, s);
        Assertions.assertThat(record.value()).isNotNull();
        //   Assertions.assertThat(record.key()).isEqualTo(formattedTimestamp);
        Alert alert = new Alert();
        alert.setPollutionTypes(PollutionType.O3);
        alert.setDate(LocalDateTime.of(2023, 6, 26, 5, 0));
        alert.setValue(452.00);
        alert.setPollutionAlertLevel(PollutionAlertLevel.EXTREMELY_POOR);
        Assertions.assertThat(record.value()).usingRecursiveComparison().isEqualTo(alert);
        Assertions.assertThat(record.topic()).isEqualTo(z);
        Assertions.assertThat(record1.value()).isNotNull();
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

    private void createTopics() {
        embeddedKafkaBroker.addTopics(addTopics);
    }

    @Value("#{'${spring.kafka.topics}'.split(',')}")
    private List<String> topicsList;

}
