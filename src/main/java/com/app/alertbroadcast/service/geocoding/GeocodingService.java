package com.app.alertbroadcast.service.geocoding;

import com.app.alertbroadcast.client.feign.OpenMeteoGeocodingClient;
import com.app.alertbroadcast.client.model.geocoding.Coordinates;
import com.app.alertbroadcast.client.model.geocoding.Geocoding;
import com.app.alertbroadcast.client.model.geocoding.Language;
import com.app.alertbroadcast.client.model.geocoding.Results;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<Coordinates> getCoordinates(Long id, String locationName) {
        List<Geocoding> geocodingList = getGeocodingList(locationName);
        Optional<Geocoding> optionalGeocoding = geocodingList
                .stream()
                .filter(geocoding -> geocoding.getId().equals(id))
                .findFirst();
        return optionalGeocoding
                .map(this::getCoordinatesFromGeocoding);
    }

    private Coordinates getCoordinatesFromGeocoding(@NotNull Geocoding geocoding) {
        Double latitude = geocoding.getLatitude();
        Double longitude = geocoding.getLongitude();
        Coordinates coordinates = new Coordinates();
        coordinates.setLatitude(latitude);
        coordinates.setLongitude(longitude);
        return coordinates;
    }
}
