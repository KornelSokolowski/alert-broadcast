package com.app.alertbroadcast.service.geocoding;

import com.app.alertbroadcast.client.feign.OpenMeteoGeocodingClient;
import com.app.alertbroadcast.client.model.geocoding.Coordinates;
import com.app.alertbroadcast.client.model.geocoding.Geocoding;
import com.app.alertbroadcast.client.model.geocoding.Language;
import com.app.alertbroadcast.client.model.geocoding.Results;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeocodingServiceTest {

    private static final String NAME = "Łódź";
    private static final Integer COUNT = 10;
    private static final String LANGUAGE = Language.EN.name();
    private static final String FORMAT = "json";

    @Mock
    private OpenMeteoGeocodingClient openMeteoGeocodingClient;
    @InjectMocks
    private GeocodingService geocodingService;

    @Test
    void getGeocodingList() {
        Results results = new Results();
        results.setResults(createGeocodingList());

        when(openMeteoGeocodingClient.getGeocoding(NAME, COUNT, LANGUAGE, FORMAT)).thenReturn(results);
        List<Geocoding> geocodingList = geocodingService.getGeocodingList(NAME);
        Assertions.assertThatList(geocodingList).hasSameElementsAs(createGeocodingList());
    }

    @Test
    void getCoordinatesList() {
        Coordinates coordinates = new Coordinates();
        coordinates.setLatitude(49.68333);
        coordinates.setLongitude(22.56667);
        when(openMeteoGeocodingClient.getGeocoding(NAME, COUNT, LANGUAGE, FORMAT)).thenReturn(createResultList());
        Optional<Coordinates> coordinates1 = geocodingService.getCoordinates(766067L, NAME);
        Assertions.assertThat(coordinates1).hasValue(coordinates);
    }

    private Results createResultList() {
        Results results = new Results();
        results.setResults(createGeocodingList());
        return results;
    }

    private List<Geocoding> createGeocodingList() {
        Geocoding geocoding1 = Geocoding.builder()
                .id(766067L)
                .name("Łodzinka Dolna")
                .latitude(49.68333)
                .longitude(22.56667)
                .elevation(417)
                .featureCode("PPL")
                .countryCode("PL")
                .timezone("Europe/Warsaw")
                .countryId(798544L)
                .country("Poland").build();
        Geocoding geocoding2 = Geocoding.builder()
                .id(3093127L)
                .name("Łódź Władysław Reymont Airport")
                .latitude(51.71667)
                .longitude(19.4)
                .elevation(173)
                .featureCode("AIRP")
                .countryCode("PL")
                .timezone("Europe/Warsaw")
                .countryId(798544L)
                .country("Poland").build();

        return List.of(geocoding1, geocoding2);
    }
}