
package ru.mephi.learning.java.weather;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Locale;

public class WeatherApplication {

    private static final String API_KEY_ENV = "YANDEX_WEATHER_KEY";

    private static final double LATITUDE = 55.75;
    private static final double LONGITUDE = 37.62;
    private static final int FORECAST_DAYS = 3;

    public static void main(String[] args) throws InterruptedException {
        String apiKey = System.getenv(API_KEY_ENV);

        if (apiKey == null || apiKey.isBlank()) {
            System.err.println(
                    "Environment variable " + API_KEY_ENV + " is not set"
            );
            return;
        }

        WeatherClient client = new WeatherClient(apiKey);
        WeatherService service = new WeatherService();

        try {
            String json = client.getForecast(
                    LATITUDE,
                    LONGITUDE,
                    FORECAST_DAYS
            );

            System.out.println("Full API response:");
            System.out.println(json);

            JsonNode root = service.parseResponse(json);

            int currentTemperature = service.getCurrentTemperature(root);
            double averageTemperature = service.getAverageTemperature(
                    root,
                    FORECAST_DAYS
            );

            System.out.println();
            System.out.println("Current temperature: "
                               + currentTemperature + " °C");

            System.out.printf(
                    Locale.ROOT,
                    "Average daytime temperature for %d days: %.2f °C%n",
                    FORECAST_DAYS,
                    averageTemperature
            );

        } catch (IOException | IllegalArgumentException e) {
            System.err.println(
                    "Failed to process weather data: " + e.getMessage()
            );
        }
    }
}
