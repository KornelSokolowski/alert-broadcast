package com.app.alertbroadcast.client.model;

import com.app.alertbroadcast.client.model.pollen.PollenType;
import com.app.alertbroadcast.client.model.pollen.alder.AlderPollenHourlyUnits;
import com.app.alertbroadcast.client.model.pollen.grass.GrassPollenHourlyUnits;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class HourlyUnitsDeserializer extends StdDeserializer<HourlyUnits> {

    protected HourlyUnitsDeserializer() {
        super(HourlyUnits.class);
    }

    @Override
    public HourlyUnits deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);

        for (PollenType pollenType : PollenType.values()) {
            JsonNode jsonNode = node.get(pollenType.getValue());
            if (jsonNode != null) {
                String time = node.get("time").asText();
                String pollen = node.get(pollenType.getValue()).asText();
                return createHourlyUnitsInstance(pollenType, time, pollen);
            }
        }

        return null;
    }

    private HourlyUnits createHourlyUnitsInstance(PollenType pollenType, String time, String pollen) {
        return switch (pollenType) {
            case GRASS -> new GrassPollenHourlyUnits(time, pollen);
            case ALDER -> new AlderPollenHourlyUnits(time, pollen);
        };
    }
}
