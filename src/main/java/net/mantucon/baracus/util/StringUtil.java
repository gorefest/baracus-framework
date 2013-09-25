package net.mantucon.baracus.util;

import android.widget.TextView;

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

    public static String[] toArray(String input) {
        String[] result = new String[1];
        result[1] = input;
        return result;
    }

    public static String join(Iterable<String> strings) {
        StringBuilder builder = new StringBuilder();
        for (String s : strings){
            builder.append(s).append(", ");
        }
        String result = builder.toString();
        return result.substring(1, result.length()-2);
    }

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

}
