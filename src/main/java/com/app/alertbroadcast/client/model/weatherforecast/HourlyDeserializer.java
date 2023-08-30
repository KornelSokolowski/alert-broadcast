package com.app.alertbroadcast.client.model.weatherforecast;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        ObjectReader reader = mapper.readerFor(new TypeReference<List<LocalDateTime>>() {
        });
        JsonNode node = parser.getCodec().readTree(parser);

        Logger logger = LoggerFactory.getLogger(HourlyDeserializer.class);
        try {
            for (WeatherVariable weatherVariable : WeatherVariable.values()) {
                JsonNode jsonNode = node.get(weatherVariable.getWeatherVariableName());
                if (jsonNode != null) {
                    JsonNode nodeTime = node.get("time");
                    List<LocalDateTime> time = reader.readValue(nodeTime);
                    JsonNode weatherNode = node.get(weatherVariable.getWeatherVariableName());
                    reader = mapper.readerFor(new TypeReference<List<Double>>() {
                    });
                    List<Double> weather = reader.readValue(weatherNode);

                    return createHourlyInstance(time, weather);
                }
            }
        } catch (Exception exception) {
            logger.error("deserialization exception", exception);
        }
        return new Hourly();
    }

    private Hourly createHourlyInstance(List<LocalDateTime> time, List<Double> weatherVariable) {
        return new Hourly(time, weatherVariable );
    }
}
