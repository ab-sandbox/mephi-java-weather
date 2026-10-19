package ru.mephi.learning.java.weather;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mephi.learning.java.weather.exception.WeatherApiException;
import ru.mephi.learning.java.weather.exception.WeatherArgumentsException;
import ru.mephi.learning.java.weather.exception.WeatherDataException;

import java.util.Locale;

public class WeatherApplication {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(WeatherApplication.class);

    private static final String API_KEY_ENV = "YANDEX_WEATHER_KEY";

    public static void main(String[] args) throws InterruptedException {
        String apiKey = System.getenv(API_KEY_ENV);

        if (apiKey == null || apiKey.isBlank()) {
            LOGGER.error("Environment variable {} is not set", API_KEY_ENV);
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

        } catch (WeatherArgumentsException
                 | WeatherApiException
                 | WeatherDataException
                 | IllegalArgumentException e) {

            LOGGER.error("Failed to process weather forecast", e);
        }
    }

    private static void printResults(
            String json,
            WeatherSummary summary,
            int days) {

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
