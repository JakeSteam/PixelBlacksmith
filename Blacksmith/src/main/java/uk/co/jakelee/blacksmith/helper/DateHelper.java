package uk.co.jakelee.blacksmith.helper;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {
    public static String datetime = "yyyy/MM/dd HH:mm:ss";
    public static String date = "yyyy/MM/dd";
    public static String time = "HH:mm:ss";

    public static String displayTime(Long timestamp, String timeFormat) {
        Date date = new Date(timestamp);
        Format format = new SimpleDateFormat(timeFormat);
        return format.format(date);
    }
}
