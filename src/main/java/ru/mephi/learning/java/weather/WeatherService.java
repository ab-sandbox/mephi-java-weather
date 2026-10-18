
package ru.mephi.learning.java.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class WeatherService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherSummary analyze(String json, int limit) throws IOException {
        validateForecastLimit(limit);

        JsonNode root = objectMapper.readTree(json);

        int currentTemperature = extractCurrentTemperature(root);
        double averageTemperature = calculateAverageTemperature(root, limit);

        return new WeatherSummary(currentTemperature, averageTemperature);
    }

    private static int extractCurrentTemperature(JsonNode root) {
        JsonNode temperature = root.required("fact")
                .required("temp")
                .requireNonNull();

        if (!temperature.isIntegralNumber() || !temperature.canConvertToInt()) {
            throw new IllegalArgumentException("Invalid current temperature");
        }

        return temperature.intValue();
    }

    private static double calculateAverageTemperature(JsonNode root, int limit) {
        JsonNode forecasts = root.required("forecasts");

        if (!forecasts.isArray() || forecasts.size() < limit) {
            throw new IllegalArgumentException(
                    "Not enough forecast data for " + limit + " days"
            );
        }

        double sum = 0;

        for (int i = 0; i < limit; i++) {
            JsonNode temperature = forecasts.get(i)
                    .required("parts")
                    .required("day")
                    .required("temp_avg")
                    .requireNonNull();

            if (!temperature.isNumber()) {
                throw new IllegalArgumentException(
                        "Invalid temperature for forecast day " + (i + 1)
                );
            }

            sum += temperature.doubleValue();
        }

        return sum / limit;
    }

    private static void validateForecastLimit(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException(
                    "Forecast limit must be positive"
            );
        }
    }
}
