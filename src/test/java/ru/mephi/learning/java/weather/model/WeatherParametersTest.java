
package ru.mephi.learning.java.weather.model;

import org.junit.jupiter.api.Test;
import ru.mephi.learning.java.weather.exception.WeatherArgumentsException;

import static org.junit.jupiter.api.Assertions.*;

class WeatherParametersTest {

    @Test
    void shouldUseDefaultParameters() {
        WeatherParameters parameters =
                WeatherParameters.fromArgs(new String[0]);

        assertEquals(55.75, parameters.latitude());
        assertEquals(37.62, parameters.longitude());
        assertEquals(3, parameters.limit());
    }

    @Test
    void shouldParseCommandLineArguments() {
        String[] args = {"59.93", "30.31", "5"};

        WeatherParameters parameters = WeatherParameters.fromArgs(args);

        assertEquals(59.93, parameters.latitude());
        assertEquals(30.31, parameters.longitude());
        assertEquals(5, parameters.limit());
    }

    @Test
    void shouldRejectIncorrectArgumentCount() {
        String[] args = {"55.75", "37.62"};

        assertThrows(
                WeatherArgumentsException.class,
                () -> WeatherParameters.fromArgs(args)
        );
    }

    @Test
    void shouldRejectNonNumericArguments() {
        String[] args = {"Moscow", "37.62", "3"};

        assertThrows(
                WeatherArgumentsException.class,
                () -> WeatherParameters.fromArgs(args)
        );
    }
}
