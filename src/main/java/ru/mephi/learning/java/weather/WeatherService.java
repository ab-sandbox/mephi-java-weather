
package ru.mephi.learning.java.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class WeatherService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode parseResponse(String json) throws IOException {
        return objectMapper.readTree(json);
    }

    public int getCurrentTemperature(JsonNode root) {
        JsonNode temperature = root.required("fact")
                .required("temp")
                .requireNonNull();

        validateCurrentTemperature(temperature);

        return temperature.intValue();
    }

    public double getAverageTemperature(JsonNode root, int limit) {
        validateForecastLimit(limit);

        JsonNode forecasts = root.required("forecasts");
        validateForecasts(forecasts, limit);

        return calculateAverageTemperature(forecasts, limit);
    }

    private static void validateCurrentTemperature(JsonNode temperature) {
        if (!temperature.isIntegralNumber() || !temperature.canConvertToInt()) {
            throw new IllegalArgumentException(
                    "Invalid current temperature"
            );
        }
    }

    private static void validateForecastLimit(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException(
                    "Forecast limit must be positive"
            );
        }
    }

    private static void validateForecasts(JsonNode forecasts, int limit) {
        if (!forecasts.isArray() || forecasts.size() < limit) {
            throw new IllegalArgumentException(
                    "Not enough forecast data for " + limit + " days"
            );
        }
    }

    private static double calculateAverageTemperature(JsonNode forecasts, int limit) {
        double sum = 0;

        for (int i = 0; i < limit; i++) {
            JsonNode temperature = forecasts.get(i)
                    .required("parts")
                    .required("day")
                    .required("temp_avg")
                    .requireNonNull();

            validateForecastTemperature(temperature, i);

            sum += temperature.doubleValue();
        }

        return sum / limit;
    }

    private static void validateForecastTemperature(
            JsonNode temperature, int index) {

        if (!temperature.isNumber()) {
            throw new IllegalArgumentException(
                    "Invalid temperature for forecast day " + (index + 1)
            );
        }
    }
}
