package com.app.alertbroadcast.client.model.geocoding;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@ToString
@EqualsAndHashCode
public class Geocoding {
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Integer elevation;
    @JsonProperty("feature_code")
    private String featureCode;
    @JsonProperty("country_code")
    private String countryCode;
    private String timezone;
    private Long population;
    @JsonProperty("country_id")
    private Long countryId;
    private String country;
}
