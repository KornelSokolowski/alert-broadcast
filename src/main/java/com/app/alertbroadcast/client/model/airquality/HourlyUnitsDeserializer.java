package com.app.alertbroadcast.client.model.airquality;

import com.app.alertbroadcast.client.model.airquality.pollution.PollutionType;
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
    public HourlyUnits deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        Logger logger = LoggerFactory.getLogger(HourlyUnitsDeserializer.class);
       try {
           for (PollutionType pollutionType : PollutionType.values()) {
               JsonNode jsonNode = node.get(pollutionType.getPollutionName());
               if (jsonNode != null) {
                   String time = node.get("time").asText();
                   String pollution = node.get(pollutionType.getPollutionName()).asText();
                   return createHourlyUnitsInstance(time, pollution);
               }
           }
       }catch (Exception exception){logger.error("deserialization exception. ",exception);}
        return new HourlyUnits();
    }

    private HourlyUnits createHourlyUnitsInstance(String time, String pollution) {
        return new HourlyUnits(time, pollution);
    }
}
