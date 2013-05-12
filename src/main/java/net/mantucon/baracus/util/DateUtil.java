package net.mantucon.baracus.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 17.10.12
 * Time: 06:02
 * To change this template use File | Settings | File Templates.
 */
public class DateUtil {

    protected DateUtil() {
        // Protection Constructor
    }

    public static class DateFormatException extends RuntimeException {
        public DateFormatException(Throwable throwable) {
            super(throwable);
        }
    }

    public static Date toDate(String date) {
        try {
            return new SimpleDateFormat("dd.MM.yyyy").parse(date);
        } catch (ParseException e) {
            throw new DateFormatException(e);
        }
    }

    public static class DateComparator implements Comparator<Date> {
        @Override
        public int compare(Date date, Date date1) {
            if (date1 == null && date == null) {
                return 0;
            }

            if (date1 == null) {
                return 1;
            }

            if (date == null) {
                return -1;
            }

            return java.lang.Long.valueOf(date.getTime()).compareTo(Long.valueOf(date1.getTime()));
        }
    }

    public static String toEuropeanDate(Date date) {
        return date != null ? new SimpleDateFormat("dd.MM.yyyy").format(date) : "";
    }

    public static String toReverseDate(Date date) {
        return date != null ?  new SimpleDateFormat("yyyyMMdd_hhmmss").format(date) : "";
    }

    public static Date addOneYear(Date in) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(in);
        cal.add(Calendar.YEAR, 1);
        return cal.getTime();

    }
}
