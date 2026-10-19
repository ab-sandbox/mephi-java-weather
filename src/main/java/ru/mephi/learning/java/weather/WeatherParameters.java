
package ru.mephi.learning.java.weather;

public record WeatherParameters(
        double latitude,
        double longitude,
        int limit
) {
    private static final double DEFAULT_LATITUDE = 55.75;
    private static final double DEFAULT_LONGITUDE = 37.62;
    private static final int DEFAULT_LIMIT = 3;

    public static WeatherParameters fromArgs(String[] args) {
        if (args.length == 0) {
            return new WeatherParameters(
                    DEFAULT_LATITUDE,
                    DEFAULT_LONGITUDE,
                    DEFAULT_LIMIT
            );
        }

        if (args.length != 3) {
            throw new IllegalArgumentException(
                    "Expected arguments: <latitude> <longitude> <limit>"
            );
        }

        try {
            return new WeatherParameters(
                    Double.parseDouble(args[0]),
                    Double.parseDouble(args[1]),
                    Integer.parseInt(args[2])
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Invalid arguments: coordinates and limit must be numeric",
                    e
            );
        }
    }
}
