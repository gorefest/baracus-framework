package net.mantucon.baracus.validation.builtins;


import android.view.View;
import android.widget.TextView;
import net.mantucon.baracus.R;
import net.mantucon.baracus.context.BaracusApplicationContext;
import net.mantucon.baracus.errorhandling.ErrorSeverity;
import net.mantucon.baracus.util.StringUtil;
import net.mantucon.baracus.validation.ConstrainedView;
import net.mantucon.baracus.validation.Validator;

import static net.mantucon.baracus.util.StringUtil.*;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 24.09.13
 * Time: 08:47
 *
 * return true, if a string as a numeric integer. empty or null strings
 * will also return true
 *
 */
public class StringIsNumericInteger implements Validator<String> {

    @Override
    public boolean validate(ConstrainedView<String> view) {
        String value = view.getCurrentValue();
        if (value != null || value.toString().trim().length() != 0)  {
            View v = (View) view;
            String s = value.toString().trim();
            try {
                Integer i = Integer.parseInt(s);
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
        return toArray(getString((TextView) v));
    }
}
