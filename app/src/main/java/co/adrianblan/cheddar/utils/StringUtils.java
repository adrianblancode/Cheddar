package co.adrianblan.cheddar.utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class StringUtils {

    // Converts the difference between two dates into a pretty date
    // There's probably a joke in there somewhere
    public static String getPrettyDate(Long time) {

        Date past = new Date(time * 1000);
        Date now = new Date();

        if (TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) > 0) {
            return TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime()) + "d";
        } else if (TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) > 0) {
            return TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime()) + "h";
        }
        if (TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) > 0) {
            return TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime()) + "m";
        } else {
            return TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime()) + "s";
        }
    }
}
