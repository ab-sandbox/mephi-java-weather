
package ru.mephi.learning.java.weather;

import java.io.IOException;
import java.util.Locale;

public class WeatherApplication {

    private static final String API_KEY_ENV = "YANDEX_WEATHER_KEY";

    public static void main(String[] args) throws InterruptedException {
        String apiKey = System.getenv(API_KEY_ENV);

        if (apiKey == null || apiKey.isBlank()) {
            System.err.println(
                    "Environment variable " + API_KEY_ENV + " is not set"
            );
            return;
        }

        try {
            WeatherParameters parameters = WeatherParameters.fromArgs(args);

            WeatherClient client = new WeatherClient(apiKey);
            WeatherService service = new WeatherService();

            String json = client.getForecast(
                    parameters.latitude(),
                    parameters.longitude(),
                    parameters.limit()
            );

            WeatherSummary summary = service.analyze(
                    json,
                    parameters.limit()
            );

            printResults(json, summary, parameters.limit());

        } catch (IOException | IllegalArgumentException e) {
            System.err.println(
                    "Failed to process weather data: " + e.getMessage()
            );
        }
    }

    private static void printResults(
            String json,
            WeatherSummary summary,
            int days
    ) {
        System.out.println("Full API response:");
        System.out.println(json);

        System.out.println();
        System.out.println(
                "Current temperature: "
                + summary.currentTemperature() + " °C"
        );

        System.out.printf(
                Locale.ROOT,
                "Average daytime temperature for %d days: %.2f °C%n",
                days,
                summary.averageTemperature()
        );
    }
}
