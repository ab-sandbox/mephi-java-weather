package ru.mephi.learning.java.weather.exception;

public class WeatherArgumentsException extends RuntimeException {

    public WeatherArgumentsException(String message) {
        super(message);
    }

    public WeatherArgumentsException(String message, Throwable cause) {
        super(message, cause);
    }
}
