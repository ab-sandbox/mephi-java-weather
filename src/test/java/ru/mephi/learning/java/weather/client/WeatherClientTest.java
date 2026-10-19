package ru.mephi.learning.java.weather.client;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mephi.learning.java.weather.exception.WeatherApiException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WeatherClientTest {

    private static final String TEST_API_KEY = "test-api-key";
    private static final String API_KEY_HEADER = "X-Yandex-Weather-Key";
    private static final String FORECAST_PATH = "/forecast";
    private static final String RESPONSE_JSON = "{\"fact\":{\"temp\":14}}";

    private static final int HTTP_OK = 200;
    private static final int HTTP_SERVER_ERROR = 500;

    private volatile String requestMethod;

    private volatile String requestQuery;

    private volatile String requestApiKey;

    private volatile int responseStatus = HTTP_OK;

    private HttpServer server;
    private WeatherClient weatherClient;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 0),
                0
        );

        server.createContext(FORECAST_PATH, this::handleRequest);
        server.start();

        String forecastUrl = "http://127.0.0.1:"
                             + server.getAddress().getPort()
                             + FORECAST_PATH;

        weatherClient = new WeatherClient(TEST_API_KEY, forecastUrl);
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    void shouldSendGetRequestWithParametersAndApiKey()
            throws InterruptedException {

        String response = weatherClient.getForecast(55.75, 37.62, 3);

        assertEquals(RESPONSE_JSON, response);
        assertEquals("GET", requestMethod);
        assertEquals(
                "lat=55.75&lon=37.62&limit=3",
                requestQuery
        );
        assertEquals(TEST_API_KEY, requestApiKey);
    }

    @Test
    void shouldRejectHttpError() {
        responseStatus = HTTP_SERVER_ERROR;

        WeatherApiException exception = assertThrows(
                WeatherApiException.class,
                () -> weatherClient.getForecast(55.75, 37.62, 3)
        );

        assertTrue(
                exception.getMessage().contains(
                        String.valueOf(HTTP_SERVER_ERROR)
                )
        );
    }

    @Test
    void shouldRejectInvalidLatitude() {
        assertThrows(
                IllegalArgumentException.class,
                () -> weatherClient.getForecast(100, 37.62, 3)
        );
    }

    @Test
    void shouldRejectInvalidLongitude() {
        assertThrows(
                IllegalArgumentException.class,
                () -> weatherClient.getForecast(55.75, 200, 3)
        );
    }

    @Test
    void shouldRejectInvalidLimit() {
        assertThrows(
                IllegalArgumentException.class,
                () -> weatherClient.getForecast(55.75, 37.62, 0)
        );
    }

    @Test
    void shouldRejectBlankApiKey() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new WeatherClient("")
        );
    }

    private void handleRequest(HttpExchange exchange) throws IOException {
        requestMethod = exchange.getRequestMethod();
        requestQuery = exchange.getRequestURI().getRawQuery();
        requestApiKey = exchange.getRequestHeaders().getFirst(API_KEY_HEADER);

        byte[] body = RESPONSE_JSON.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(responseStatus, body.length);

        try (var output = exchange.getResponseBody()) {
            output.write(body);
        }
    }
}
