package ozaii.utils;


import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static String formatTime(long millis) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long hours = TimeUnit.MILLISECONDS.toHours(millis);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
