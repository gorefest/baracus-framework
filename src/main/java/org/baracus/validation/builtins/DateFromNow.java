package org.baracus.validation.builtins;


import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import org.baracus.R;
import org.baracus.context.BaracusApplicationContext;
import org.baracus.validation.AbstractValidator;
import org.baracus.validation.ConstrainedView;

import java.text.ParseException;
import java.util.Date;

import static org.baracus.util.DateUtil.today;
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
public class DateFromNow extends AbstractValidator<String> {

    @Override
    public boolean validate(ConstrainedView<String> view) {
        String value = view.getCurrentValue();
        if (value != null && value.toString().trim().length() != 0) {
            java.text.DateFormat df = DateFormat.getDateFormat(BaracusApplicationContext.getContext());
            try {
                Date d = df.parse(value);
                return today().getTime() <= d.getTime();
            } catch (ParseException e) {
                return false; //unparseable
            }


        } else {
            return true; // empty string is ok, if you dont want empty strings, then lock the field with a StringNotEmpy constraint
        }
    }


    @Override
    public int getMessageId() {
        return R.string.dateFromNow;
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
