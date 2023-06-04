package com.app.alertbroadcast.service.geocoding;

import com.app.alertbroadcast.client.feign.OpenMeteoGeocodingClient;
import com.app.alertbroadcast.client.model.geocoding.Geocoding;
import com.app.alertbroadcast.client.model.geocoding.Language;
import com.app.alertbroadcast.client.model.geocoding.Results;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeocodingService {

    private static final Integer COUNT = 10;
    private static final Language LANGUAGE = Language.EN;
    private static final String FORMAT = "json";

    private OpenMeteoGeocodingClient openMeteoGeocodingClient;

    public GeocodingService(OpenMeteoGeocodingClient openMeteoGeocodingClient) {
        this.openMeteoGeocodingClient = openMeteoGeocodingClient;
    }

    public List<Geocoding> getGeocodingList(String locationName) {
        Results results = openMeteoGeocodingClient.getGeocoding(locationName, COUNT, LANGUAGE.name(), FORMAT);
        return results.getResults();
    }
    // TODO napisać metodę, która zwraca współrzędne dla danej lokalizacji, wyszukiwanie po id, metoda ma przyjmować id
    // Stworzyć obiekt Coordinates, który będzie przechowywał te współrzędne
}
