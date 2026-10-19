package ru.mephi.learning.java.weather.service;

import org.junit.jupiter.api.Test;
import ru.mephi.learning.java.weather.exception.WeatherDataException;
import ru.mephi.learning.java.weather.model.WeatherSummary;

import static org.junit.jupiter.api.Assertions.*;

class WeatherServiceTest {

    private final WeatherService service = new WeatherService();

    private static final String VALID_JSON = """
            {
              "fact": {
                "temp": 14
              },
              "forecasts": [
                {
                  "parts": {
                    "day": {
                      "temp_avg": 14
                    }
                  }
                },
                {
                  "parts": {
                    "day": {
                      "temp_avg": 10
                    }
                  }
                },
                {
                  "parts": {
                    "day": {
                      "temp_avg": 8
                    }
                  }
                }
              ]
            }
            """;

    @Test
    void shouldAnalyzeWeatherResponse() {
        WeatherSummary summary = service.analyze(VALID_JSON, 3);

        assertEquals(14, summary.currentTemperature());
        assertEquals(10.67, summary.averageTemperature(), 0.01);
    }

    @Test
    void shouldRespectForecastLimit() {
        WeatherSummary summary = service.analyze(VALID_JSON, 2);

        assertEquals(12.0, summary.averageTemperature(), 0.001);
    }

    @Test
    void shouldRejectMissingCurrentTemperature() {
        String json = """
                {
                  "fact": {},
                  "forecasts": []
                }
                """;

        assertThrows(
                WeatherDataException.class,
                () -> service.analyze(json, 1)
        );
    }

    @Test
    void shouldRejectInsufficientForecastData() {
        assertThrows(
                WeatherDataException.class,
                () -> service.analyze(VALID_JSON, 5)
        );
    }

    @Test
    void shouldRejectInvalidForecastTemperature() {
        String json = VALID_JSON.replace(
                "\"temp_avg\": 14",
                "\"temp_avg\": \"invalid\""
        );

        assertThrows(
                WeatherDataException.class,
                () -> service.analyze(json, 3)
        );
    }

    @Test
    void shouldRejectInvalidJson() {
        assertThrows(
                WeatherDataException.class,
                () -> service.analyze("{invalid}", 3)
        );
    }

    @Test
    void shouldRejectInvalidLimit() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.analyze(VALID_JSON, 0)
        );
    }
}
