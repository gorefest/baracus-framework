package net.mantucon.baracus.validation.builtins;


import android.view.View;
import android.widget.TextView;
import net.mantucon.baracus.R;
import net.mantucon.baracus.validation.AbstractValidator;
import net.mantucon.baracus.validation.ConstrainedView;
import net.mantucon.baracus.validation.Validator;

import static net.mantucon.baracus.util.StringUtil.getDouble;
import static net.mantucon.baracus.util.StringUtil.getString;
import static net.mantucon.baracus.util.StringUtil.toArray;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 24.09.13
 * Time: 08:47
 *
 * verifies that the passed String is a number greater than zero
 *
 */
public class NumberMustBeGreaterThanZero extends AbstractValidator<String>{

    @Override
    public boolean validate(ConstrainedView<String> view) {
        String value = view.getCurrentValue();
        if (value != null && value.toString().trim().length() != 0)  {
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
                throw new IllegalArgumentException("Not the correct type. This validator requires a Text View but got "+v.getClass().getName());
            }
        } else return null;

    }



}
