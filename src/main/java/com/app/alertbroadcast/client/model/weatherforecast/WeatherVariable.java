package com.app.alertbroadcast.client.model.weatherforecast;

public enum WeatherVariable {
    TEMPERATURE_2M("temperature_2m"), RELATIVEHUMIDITY_2M("relativehumidity_2m"),
    DEWPOINT_2M("dewpoint_2m"), APPARENT_TEMPERATURE("apparent_temperature"),
    SURFACE_PRESSURE("surface_pressure"), CLOUDCOVER("cloudcover"),
    WINDSPEED_10M("windspeed_10m"), WINDDIRECTION("winddirection_10m"),
    SHORTWAVE_RADIATION("shortwave_radiation"), SNOWFALL("snowfall"),
    RAIN("rain"), SNOW_DEPTH("snow_depth"), UV_INDEX("uv_index");

    private final String weatherVariable;

    WeatherVariable(String weatherVariable) {
        this.weatherVariable = weatherVariable;
    }

    public String getWeatherVariableName() {
        return weatherVariable;
    }
}
