package uk.co.jakelee.blacksmith.helper;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {
    public static final String date = "yyyy/MM/dd";
    public static final String time = "HH:mm";

    public static final int MILLISECONDS_IN_SECOND = 1000;
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int MINUTES_IN_HOUR = 60;

    public static String displayTime(Long timestamp, String timeFormat) {
        Date date = new Date(timestamp);
        Format format = new SimpleDateFormat(timeFormat);
        return format.format(date);
    }

    public static String getHoursMinsRemaining(Long timestamp) {
        int hours = timestampPartHours(timestamp);
        int minutes = timestampPartMinutes(timestamp) - (hours * MINUTES_IN_HOUR);

        return hours + "hr " + minutes + "min";
    }

    public static String getMinsSecsRemaining(Long timestamp) {
        int hours = timestampPartHours(timestamp);
        int minutes = timestampPartMinutes(timestamp) - (hours * MINUTES_IN_HOUR);
        int seconds = timestampPartSeconds(timestamp) - (minutes * SECONDS_IN_MINUTE);

        return minutes + "min " + seconds + "s";
    }

    private static int timestampPartSeconds(Long timestamp) {
        double seconds = timestamp / (MILLISECONDS_IN_SECOND);
        return (int) seconds;
    }

    private static int timestampPartMinutes(Long timestamp) {
        double minutes = timestamp / (MILLISECONDS_IN_SECOND * SECONDS_IN_MINUTE);
        return (int) minutes;
    }

    private static int timestampPartHours(Long timestamp) {
        double hours = timestamp / (MILLISECONDS_IN_SECOND * SECONDS_IN_MINUTE * MINUTES_IN_HOUR);
        return (int) hours;
    }

    public static int minutesToMilliseconds(int minutes) {
        return minutes * (MILLISECONDS_IN_SECOND * SECONDS_IN_MINUTE);
    }

    public static int hoursToMilliseconds(int hours) {
        return hours * minutesToMilliseconds(MINUTES_IN_HOUR);
    }

    public static int getSecondsRoundUp(long milliseconds) {
        double secondsLeft = (double) milliseconds / MILLISECONDS_IN_SECOND;
        return (int) Math.ceil(secondsLeft);
    }
}
