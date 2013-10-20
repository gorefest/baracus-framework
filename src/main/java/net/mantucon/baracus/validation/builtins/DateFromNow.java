package net.mantucon.baracus.validation.builtins;


import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import net.mantucon.baracus.R;
import net.mantucon.baracus.context.BaracusApplicationContext;
import net.mantucon.baracus.validation.AbstractValidator;
import net.mantucon.baracus.validation.ConstrainedView;

import java.text.ParseException;
import java.util.Date;

import static net.mantucon.baracus.util.DateUtil.today;
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
public class DateFromNow extends AbstractValidator<String>{

    @Override
    public boolean validate(ConstrainedView<String> view) {
        String value = view.getCurrentValue();
        if (value != null && value.toString().trim().length() != 0)  {
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
        return toArray(getString((TextView) v));
    }



}
