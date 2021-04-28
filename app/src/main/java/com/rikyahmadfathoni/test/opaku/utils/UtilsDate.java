package com.rikyahmadfathoni.test.opaku.utils;

import android.text.format.DateFormat;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ALL")
public class UtilsDate {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    @Nullable
    public static Date parseDate(String dateText) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = null;
        try {
            date = format.parse(dateText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    @Nullable
    public static Calendar parseCalendar(String dateText) {
        Date date = parseDate(dateText);
        if (date != null) {
            return getCalendar(date);
        }
        return null;
    }

    public static Calendar getCalendar(long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time);
        return cal;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTime(date);
        return cal;
    }

    public static Date getDate(long time) {
        return getCalendar(time).getTime();
    }

    public static String getTimeFormat(long time) {
        return DateFormat.format("HH:mm:ss", time).toString();
    }

    public static String parseTime(long millis) {
        return String.format("%s:%s:%s",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    public static String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        if (hours > 0) {
            buf.append(String.format("%02d", hours))
                    .append(":");
        }

        buf.append(String.format("%d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }

    public static String getStringDate(long time) {
        return DateFormat.format("dd/MM/yyyy HH:mm:ss", getDate(time)).toString();
    }

    public static String getSimpleTanggal(long time) {
        return DateFormat.format("dd/MM/yyyy", getDate(time)).toString();
    }

    public static String getSimpleTanggal(Date date) {
        return DateFormat.format("dd/MM/yyyy", date).toString();
    }

    public static String getDateId(long time) {
        Calendar calendar = getCalendar(time);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH);
        final int year = calendar.get(Calendar.YEAR);

        return String.format("%s %s %s", day, month, year);
    }

    public static String getTanggal(long time) {
        Calendar calendar = getCalendar(time);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH);
        final int year = calendar.get(Calendar.YEAR);

        return String.format("%s %s %s", day, bulanList[month], year);
    }

    public static String getTanggal(Date date) {
        Calendar calendar = getCalendar(date);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH);
        final int year = calendar.get(Calendar.YEAR);

        return String.format("%s %s %s", day, bulanList[month], year);
    }

    public static String getTanggal(String dateText) {
        Date date = parseDate(dateText);
        if (date != null) {
            getTanggal(date);
        }
        return getTanggal(System.currentTimeMillis());
    }

    public static String[] bulanList = new String[]{
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    };

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static long getTimeLeft(long maxTime) {
        final long currentTime = System.currentTimeMillis();
        if (maxTime >= currentTime) {
            final long timeLeft = maxTime - currentTime;
            return timeLeft;
        }
        return -1;
    }

    public static long getHoursLeft(long time, long maxTime) {
        if (maxTime >= time) {
            final long timeLeft = maxTime - time;
            return TimeUnit.MILLISECONDS.toHours(timeLeft);
        }
        return -1;
    }

    public static long getMinutesLeft(long time, long maxTime) {
        if (maxTime >= time) {
            final long timeLeft = maxTime - time;
            return TimeUnit.MILLISECONDS.toMinutes(timeLeft);
        }
        return -1;
    }
}
