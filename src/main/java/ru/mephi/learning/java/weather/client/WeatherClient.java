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
import java.util.Objects;

public class WeatherClient {

    private static final String DEFAULT_FORECAST_URL =
            "https://api.weather.yandex.ru/v2/forecast";

    private static final String API_KEY_HEADER = "X-Yandex-Weather-Key";

    private static final int CONNECT_TIMEOUT_SECONDS = 10;
    private static final int REQUEST_TIMEOUT_SECONDS = 20;

    private final String apiKey;
    private final String forecastUrl;
    private final HttpClient httpClient;

    public WeatherClient(String apiKey) {
        this(apiKey, DEFAULT_FORECAST_URL);
    }

    WeatherClient(String apiKey, String forecastUrl) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("API key must not be blank");
        }

        this.apiKey = apiKey;
        this.forecastUrl = Objects.requireNonNull(forecastUrl);
        this.httpClient = createHttpClient();
    }

    public String getForecast(double latitude, double longitude, int limit)
            throws InterruptedException {

        validateParameters(latitude, longitude, limit);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(buildUri(latitude, longitude, limit))
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

    private URI buildUri(double latitude, double longitude, int limit) {
        String url = String.format(
                Locale.ROOT,
                "%s?lat=%s&lon=%s&limit=%d",
                forecastUrl,
                latitude,
                longitude,
                limit
        );

        return URI.create(url);
    }

    private static HttpClient createHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECONDS))
                .build();
    }

    private static void validateParameters(
            double latitude,
            double longitude,
            int limit) {

        if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Invalid latitude");
        }

        if (!Double.isFinite(longitude) || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Invalid longitude");
        }

        if (limit <= 0) {
            throw new IllegalArgumentException(
                    "Forecast limit must be positive"
            );
        }
    }
}
