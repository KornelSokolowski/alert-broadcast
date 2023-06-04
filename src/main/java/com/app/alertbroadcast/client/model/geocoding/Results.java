package com.app.alertbroadcast.client.model.geocoding;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class Results {

    private List<Geocoding> results;
}
