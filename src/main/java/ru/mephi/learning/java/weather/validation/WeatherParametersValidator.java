package ru.mephi.learning.java.weather.validation;

import ru.mephi.learning.java.weather.exception.WeatherArgumentsException;

public final class WeatherParametersValidator {

    private static final double MIN_LATITUDE = -90;
    private static final double MAX_LATITUDE = 90;
    private static final double MIN_LONGITUDE = -180;
    private static final double MAX_LONGITUDE = 180;

    private WeatherParametersValidator() {
    }

    public static void validate(double latitude, double longitude, int limit) {
        if (!Double.isFinite(latitude)
            || latitude < MIN_LATITUDE
            || latitude > MAX_LATITUDE) {
            throw new IllegalArgumentException("Invalid latitude");
        }

        if (!Double.isFinite(longitude)
            || longitude < MIN_LONGITUDE
            || longitude > MAX_LONGITUDE) {
            throw new IllegalArgumentException("Invalid longitude");
        }

        if (limit <= 0) {
            throw new IllegalArgumentException(
                    "Forecast limit must be positive"
            );
        }
    }
}