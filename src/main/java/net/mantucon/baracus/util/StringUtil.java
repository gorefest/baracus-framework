package net.mantucon.baracus.util;

import android.text.format.DateFormat;
import android.widget.TextView;
import net.mantucon.baracus.context.BaracusApplicationContext;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 09.04.13
 * Time: 20:14
 * To change this template use File | Settings | File Templates.
 */
public class StringUtil {
    private StringUtil() {}

    public static String firstByteToLower(String instring) {
        return  instring.substring(0,1).toLowerCase()+instring.substring(1,instring.length());
    }

    /**
     * checks, if the passed TextView contains some text
     * @param view - the TextView
     * @return true, if a non-null, non-blank string is found inside of the text view
     */
    public static boolean isEmpty(TextView view) {
        if (view != null && view.getText() != null) {
            return getString(view).length() == 0;
        }
        return false;
    }

    /**
     * extract the trimmed string value out of the passed TextView
     * @param view - the view to read from
     * @return the trimmed string value from the view
     */
    public static String getString(TextView view) {
        return view.getText().toString().trim();
    }

    /**
     * extract an Integer out of a TextView
     * @param view - the view to read from
     * @return the Integer value or NULL in case of a non-parseable String
     */
    public static Integer getNumber(TextView view) {
        Integer result;
        if (!isEmpty(view)) {
            try {
                result = Integer.parseInt(getString(view));
            } catch (Exception e) {
                result = null;
            }
        } else {
            result = null;
        }
        return result;
    }

    /**
     * extract an Double out of a TextView
     * @param view - the view to read from
     * @return the Double value or NULL in case of a non-parseable String
     */
    public static Double getDouble(TextView view) {
        Double result;
        if (!isEmpty(view)) {
            try {
                result = Double.parseDouble(getString(view));
            } catch (Exception e) {
                result = null;
            }
        } else {
            result = null;
        }
        return result;
    }

    /**
     * make a one-sized array out of the passed string
     * @param input - the string
     * @return an array sized 1 containing the string
     */
    public static String[] toArray(String input) {
        if (input != null && input.length() > 0) {
            String[] result = new String[1];
            result[0] = input;
            return result;
        } else {
            return new String[0];
        }
    }

    /**
     * make a comma seperated string out a passed list/set of strings
     * @param strings - the string collection
     * @return the comma seperated string
     */
    public static String join(Iterable<String> strings) {
        StringBuilder builder = new StringBuilder();
        for (String s : strings){
            builder.append(s).append(", ");
        }
        String result = builder.toString();
        return result.substring(1, result.length()-2);
    }

    /**
     * split a string into an array and trim the strings
     * @param s - the string to split
     * @param delim - the delimiter
     * @return - the array containing all strings trimmed
     */
    public static String[] splitPurified(String s, String delim) {
        String[] result = null;
        if (s != null && s.length() > 0) {
            String[] input = s.split(delim);
            result = new String[input.length];
            int i = 0;
            for (String v : input) {
                result[i] = v != null ? v.trim() : "";
                i++;
            }
        }
        return result;
    }

    /**
     * formats a date to a string using the system's defined dateformat
     * @param date - the date
     * @return a String containing the formatted date
     */
    public static String formatDate(Date date) {
        return DateFormat.getDateFormat(BaracusApplicationContext.getContext()).format(date);
    }

}
