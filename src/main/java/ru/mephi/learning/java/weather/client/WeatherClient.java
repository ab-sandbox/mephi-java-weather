package ru.mephi.learning.java.weather.client;

import ru.mephi.learning.java.weather.exception.WeatherApiException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;

public class WeatherClient {

    private static final String API_URL =
            "https://api.weather.yandex.ru/v2/forecast";
    private static final String API_KEY_HEADER = "X-Yandex-Weather-Key";

    private static final int CONNECT_TIMEOUT_SECONDS = 10;
    private static final int REQUEST_TIMEOUT_SECONDS = 20;

    private final HttpClient httpClient;
    private final String apiKey;

    public WeatherClient(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("API key must not be blank");
        }

        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECONDS))
                .build();
    }

    public String getForecast(
            double latitude,
            double longitude,
            int limit
    ) throws InterruptedException {

        validateParameters(latitude, longitude, limit);

        String url = String.format(
                Locale.ROOT,
                "%s?lat=%s&lon=%s&limit=%d",
                API_URL,
                latitude,
                longitude,
                limit
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header(API_KEY_HEADER, apiKey)
                .timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new WeatherApiException(
                        "Weather API returned HTTP " + response.statusCode()
                );
            }

            return response.body();

        } catch (IOException e) {
            throw new WeatherApiException(
                    "Failed to fetch weather forecast", e
            );
        }
    }

    private static void validateParameters(double latitude, double longitude, int limit) {
        if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Invalid latitude");
        }

        if (!Double.isFinite(longitude) || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Invalid longitude");
        }

        if (limit <= 0) {
            throw new IllegalArgumentException("Forecast limit must be positive");
        }
    }
}
