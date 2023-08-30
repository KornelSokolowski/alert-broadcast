package com.app.alertbroadcast.client.model.weatherforecast;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HourlyUnitsDeserializer extends StdDeserializer<HourlyUnits> {

    protected HourlyUnitsDeserializer() {
        super(HourlyUnits.class);
    }

    @Override
    public HourlyUnits deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        Logger logger = LoggerFactory.getLogger(HourlyUnitsDeserializer.class);
        try {
            for (WeatherVariable weatherVariable : WeatherVariable.values()){
                JsonNode jsonNode = node.get(weatherVariable.getWeatherVariableName());
                if(jsonNode != null) {
                    String time = node.get("time").asText();
                    String weather = node.get(weatherVariable.getWeatherVariableName()).asText();
                    return createHourlyUnitsInstance(time,weather);
                }
            }
        }catch (Exception exception){
            logger.error("deserialization exception. ",exception);
        }
        return new HourlyUnits();
    }
    private HourlyUnits createHourlyUnitsInstance(String time, String weather){
        return new HourlyUnits(time,weather);
    }
}
