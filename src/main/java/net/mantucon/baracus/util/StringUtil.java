package net.mantucon.baracus.util;

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
}
