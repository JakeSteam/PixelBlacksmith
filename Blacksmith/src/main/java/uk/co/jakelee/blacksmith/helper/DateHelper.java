package uk.co.jakelee.blacksmith.helper;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {
    public static String datetime = "yyyy/MM/dd HH:mm:ss";
    public static String date = "yyyy/MM/dd";
    public static String time = "HH:mm:ss";

    public static int MILLISECONDS_IN_SECOND = 1000;
    public static int SECONDS_IN_MINUTE = 60;
    public static int MINUTES_IN_HOUR = 60;

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

    public static int timestampPartSeconds(Long timestamp) {
        double seconds = timestamp / (MILLISECONDS_IN_SECOND);
        return (int) seconds;
    }

    public static int timestampPartMinutes(Long timestamp) {
        double minutes = timestamp / (MILLISECONDS_IN_SECOND * SECONDS_IN_MINUTE);
        return (int) minutes;
    }

    public static int timestampPartHours(Long timestamp) {
        double hours = timestamp / (MILLISECONDS_IN_SECOND * SECONDS_IN_MINUTE * MINUTES_IN_HOUR);
        return (int) hours;
    }

    public static int minutesToMilliseconds(int minutes) {
        return minutes * (MILLISECONDS_IN_SECOND * SECONDS_IN_MINUTE);
    }

    public static int hoursToMilliseconds(int hours) {
        return hours * minutesToMilliseconds(MINUTES_IN_HOUR);
    }
}
