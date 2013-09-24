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
        return java.beans.Introspector.decapitalize(instring);
    }

    public static boolean isEmpty(TextView view) {
        if (view != null && view.getText() != null) {
            return view.getText().toString().trim().length() == 0;
        }
        return false;
    }
}
