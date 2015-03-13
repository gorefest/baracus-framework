package org.baracus.validation.builtins;


import android.view.View;
import android.widget.TextView;
import org.baracus.R;
import org.baracus.validation.AbstractValidator;
import org.baracus.validation.ConstrainedView;

import static org.baracus.util.StringUtil.getString;
import static org.baracus.util.StringUtil.toArray;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 24.09.13
 * Time: 08:47
 * <p/>
 * verifies that the passed String is a number greater than zero
 */
public class NumberMustBeGreaterThanZero extends AbstractValidator<String> {

    @Override
    public boolean validate(ConstrainedView<String> view) {
        String value = view.getCurrentValue();
        if (value != null && value.toString().trim().length() != 0) {
            try {
                Integer i = Integer.valueOf(value);
                return i > 0;
            } catch (NumberFormatException exception) {
                // null activity , try to parse a double out of the string
            }
            try {
                Double d = Double.valueOf(value);
                return d > 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public int getMessageId() {
        return R.string.numberIsSmallerThanZero;
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
