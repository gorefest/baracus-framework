package org.baracus.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.baracus.util.DateUtil.roundToDay;
import static org.baracus.util.DateUtil.subtractDay;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * DayDate: 14.12.13
 * Time: 13:20
 * To change this template use File | Settings | File Templates.
 */
public class
        DayDate extends Date {

    /**
     * Initializes this {@code DayDate} instance to the current time.
     */
    public DayDate() {
        super(roundToDay(new Date()).getTime());
    }

    public DayDate(long millis) {
        super(roundToDay(new Date(millis)).getTime());
    }

    public DayDate(Date d) {
        super(roundToDay(d).getTime());
    }

    @Override
    public long getTime() {
        return super.getTime();
    }

    @Override
    public void setTime(long milliseconds) {
        super.setTime(roundToDay(new Date(milliseconds)).getTime());
    }

    @Override
    public boolean before(Date DayDate) {
        return super.before(roundToDay(DayDate));
    }

    @Override
    public boolean after(Date DayDate) {
        return super.after(roundToDay(DayDate));
    }

    @Override
    public boolean equals(Object object) {
        return super.equals(roundToDay((DayDate) object));
    }

    @Override
    public int compareTo(Date DayDate) {
        return super.compareTo(roundToDay(DayDate));
    }

    public static DayDate today() {
        return new DayDate();
    }

    public static DayDate yesterday() {
        return subtractDay(today());
    }

    public DayDate dayBefore() {
        return subtractDay(this);
    }

    public static DayDate valueOf(Date d) {
        if (d instanceof DayDate) {
            return (DayDate) d;
        } else {
            return new DayDate(d);
        }
    }

    public static DayDate parseEuropeanDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        try {
            return new DayDate(dateFormat.parse(date));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
