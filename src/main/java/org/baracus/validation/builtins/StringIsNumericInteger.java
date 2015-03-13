package org.baracus.validation.builtins;


import android.view.View;
import android.widget.TextView;
import org.baracus.R;
import org.baracus.validation.ConstrainedView;
import org.baracus.validation.Validator;

import static org.baracus.util.StringUtil.getString;
import static org.baracus.util.StringUtil.toArray;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 24.09.13
 * Time: 08:47
 * <p/>
 * return true, if a string as a numeric integer. empty or null strings
 * will also return true
 */
public class StringIsNumericInteger implements Validator<String> {

    @Override
    public boolean validate(ConstrainedView<String> view) {
        String value = view.getCurrentValue();
        if (value != null && value.toString().trim().length() != 0) {
            String s = value.toString().trim();
            try {
                Integer.parseInt(s);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getMessageId() {
        return R.string.notAIntegerField;
    }

    public String[] viewToMessageParams(View v) {
        if (v != null) {
            if (TextView.class.isAssignableFrom(v.getClass())) {
                return toArray(getString((TextView) v));
            } else {
                throw new IllegalArgumentException("Not the correct type. This validator requires a Text View but got " + v.getClass().getName());
            }
        } else return null;
    }
}
