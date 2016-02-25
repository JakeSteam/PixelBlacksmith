package uk.co.jakelee.blacksmith.helper;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {
    public static String datetime = "yyyy/MM/dd HH:mm:ss";
    public static String date = "yyyy/MM/dd";
    public static String time = "HH:mm:ss";

    private static int MILLISECONDS = 1000;
    private static int SECONDS = 60;
    private static int MINUTES = 60;

    public static String displayTime(Long timestamp, String timeFormat) {
        Date date = new Date(timestamp);
        Format format = new SimpleDateFormat(timeFormat);
        return format.format(date);
    }

    public static String getHoursMinsRemaining(Long timestamp) {
        int hours = timestampPartHours(timestamp);
        int minutes = timestampPartMinutes(timestamp) - (hours * MINUTES);

        return hours + "hr " + minutes + "min";
    }

    public static String getMinsSecsRemaining(Long timestamp) {
        int hours = timestampPartHours(timestamp);
        int minutes = timestampPartMinutes(timestamp) - (hours * MINUTES);
        int seconds = timestampPartSeconds(timestamp) - (minutes * SECONDS);

        return minutes + "min " + seconds + "s";
    }

    public static int timestampPartSeconds(Long timestamp) {
        double seconds = timestamp / (MILLISECONDS);
        return (int) seconds;
    }

    public static int timestampPartMinutes(Long timestamp) {
        double minutes = timestamp / (MILLISECONDS * SECONDS);
        return (int) minutes;
    }

    public static int timestampPartHours(Long timestamp) {
        double hours = timestamp / (MILLISECONDS * SECONDS * MINUTES);
        return (int) hours;
    }
}
