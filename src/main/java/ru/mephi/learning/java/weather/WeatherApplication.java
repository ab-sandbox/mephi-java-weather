
package ru.mephi.learning.java.weather;

import java.io.IOException;

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

        try {
            String json = client.getForecast(
                    LATITUDE,
                    LONGITUDE,
                    FORECAST_DAYS
            );

            System.out.println(json);

        } catch (IOException e) {
            System.err.println(
                    "Failed to get weather forecast: " + e.getMessage()
            );
        }
    }
}
