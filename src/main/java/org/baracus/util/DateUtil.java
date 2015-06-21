package org.baracus.util;

import android.text.format.DateFormat;
import org.baracus.context.BaracusApplicationContext;

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

    private static volatile GregorianCalendar gregorianCalendar = new GregorianCalendar();

    protected DateUtil() {
        // Protection Constructor
    }

    /**
     * Simple Exception to encapsulate date exceptions
     */
    public static class DateFormatException extends RuntimeException {
        public DateFormatException(Throwable throwable) {
            super(throwable);
        }
    }

    /**
     * reverts a date string back into a date usign the system's date format
     *
     * @param date - the date string
     * @return the parsed date
     */
    public static Date toDate(String date) {
        try {
            return DateFormat.getDateFormat(BaracusApplicationContext.getContext()).parse(date);
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

    public static Date fromEuropeanDate(String date) {
        try {
            return date != null ? new SimpleDateFormat("dd.MM.yyyy").parse(date) : null;
        } catch (ParseException e) {
            throw new DateFormatException(e);
        }
    }

    public static String toReverseDate(Date date) {
        return date != null ? new SimpleDateFormat("yyyyMMdd_hhmmss").format(date) : "";
    }


    /**
     * @return today's date
     */
    public static Date today() {
        return fromEuropeanDate(toEuropeanDate(new Date()));
    }

    public static DayDate addDay(Date d) {
        gregorianCalendar.setTime(d);
        gregorianCalendar.add(Calendar.DAY_OF_MONTH, 1);
        return new DayDate(gregorianCalendar.getTime());

    }

    public static DayDate subtractDay(Date d) {
        gregorianCalendar.setTime(d);
        gregorianCalendar.add(Calendar.DAY_OF_MONTH, -1);
        return new DayDate(gregorianCalendar.getTime());

    }

    /*
    public static DayDate subtractSecond(Date d) {
        gregorianCalendar.setTime(d);
        gregorianCalendar.add(Calendar.SECOND,-1);
        return gregorianCalendar.getTime();

    } */

    public static DayDate addWeek(Date d) {
        gregorianCalendar.setTime(d);
        gregorianCalendar.add(Calendar.DAY_OF_MONTH, 7);
        return new DayDate(gregorianCalendar.getTime());
    }

    public static DayDate addMonth(Date d) {
        gregorianCalendar.setTime(d);
        gregorianCalendar.add(Calendar.MONTH, 1);
        return new DayDate(gregorianCalendar.getTime());
    }

    public static DayDate addMonths(Date d, int months) {
        gregorianCalendar.setTime(d);
        gregorianCalendar.add(Calendar.MONTH, months);
        return new DayDate(gregorianCalendar.getTime());
    }

    public static DayDate addTwoMonths(Date d) {
        gregorianCalendar.setTime(d);
        gregorianCalendar.add(Calendar.MONTH, 2);
        return new DayDate(gregorianCalendar.getTime());
    }

    public static DayDate addQuarter(Date d) {
        gregorianCalendar.setTime(d);
        gregorianCalendar.add(Calendar.MONTH, 3);
        return new DayDate(gregorianCalendar.getTime());
    }

    public static DayDate addHalfYear(Date d) {
        gregorianCalendar.setTime(d);
        gregorianCalendar.add(Calendar.MONTH, 6);
        return new DayDate(gregorianCalendar.getTime());
    }

    public static DayDate addYear(Date d) {
        gregorianCalendar.setTime(d);
        gregorianCalendar.add(Calendar.MONTH, 12);
        return new DayDate(gregorianCalendar.getTime());
    }

    public static Date roundToDay(Date d) {
        return fromEuropeanDate(toEuropeanDate(d));
    }


    public static DayDate addOneYear(Date today) {
        return new DayDate(org.baracus.util.DateUtil.addOneYear(today));
    }

    public static DayDate endOfTime() {
        return new DayDate(fromEuropeanDate("31.12.9999"));
    }

    public static DayDate beginOfTime() {
        return new DayDate(fromEuropeanDate("01.01.01"));
    }
}
