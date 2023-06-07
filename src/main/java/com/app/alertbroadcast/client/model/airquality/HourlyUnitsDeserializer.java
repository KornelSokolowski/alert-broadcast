package com.app.alertbroadcast.client.model.airquality;

import com.app.alertbroadcast.client.model.airquality.pollen.PollenType;
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
           for (PollenType pollenType : PollenType.values()) {
               JsonNode jsonNode = node.get(pollenType.getValue());
               if (jsonNode != null) {
                   String time = node.get("time").asText();
                   String pollen = node.get(pollenType.getValue()).asText();
                   return createHourlyUnitsInstance(time, pollen);
               }
           }
       }catch (Exception exception){logger.error("deserialization exception. ",exception);}
        return new HourlyUnits();
    }

    private HourlyUnits createHourlyUnitsInstance(String time, String pollen) {
        return new HourlyUnits(time, pollen);
    }
}
