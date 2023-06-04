package com.app.alertbroadcast.client.model;

import com.app.alertbroadcast.client.model.pollen.PollenType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class HourlyDeserializer extends StdDeserializer<Hourly> {

    protected HourlyDeserializer() {
        super(Hourly.class);
    }


    @Override
    public Hourly deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        JsonNode node = parser.getCodec().readTree(parser);
        Logger logger = LoggerFactory.getLogger(HourlyDeserializer.class);
        try {
            for (PollenType pollenType : PollenType.values()) {
                JsonNode jsonNode = node.get(pollenType.getValue());
                if (jsonNode != null) {
                    JsonNode nodeTime = node.get("time");
                    ObjectReader reader = mapper.readerFor(new TypeReference<List<LocalDateTime>>() {
                    });
                    List<LocalDateTime> time = reader.readValue(nodeTime);
                    JsonNode pollenNode = node.get(pollenType.getValue());
                    reader = mapper.readerFor(new TypeReference<List<Double>>() {
                    });
                    List<Double> pollen = reader.readValue(pollenNode);
                    return createHourlyInstance(time, pollen);
                }
            }

        } catch (Exception exception) {
            logger.error("deserialization exception", exception);
        }
        return new Hourly();
    }

    private Hourly createHourlyInstance(List<LocalDateTime> time, List<Double> pollen) {
        return new Hourly(time, pollen);
    }
}
