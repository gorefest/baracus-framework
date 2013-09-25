package net.mantucon.baracus.validation.builtins;


import android.view.View;
import net.mantucon.baracus.R;
import net.mantucon.baracus.context.BaracusApplicationContext;
import net.mantucon.baracus.errorhandling.ErrorSeverity;
import net.mantucon.baracus.validation.AbstractValidator;
import net.mantucon.baracus.validation.ConstrainedView;
import net.mantucon.baracus.validation.Validator;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 24.09.13
 * Time: 08:47
 * To change this template use File | Settings | File Templates.
 */
public class StringNotEmpty extends AbstractValidator<String> {

    @Override
    public boolean validate(ConstrainedView<String> view) {
        String value = view.getCurrentValue();
        if (value == null || value.toString().trim().length() == 0)  {
            return false;
        }
        return true;

    }

    @Override
    public int getMessageId() {
        return R.string.notNullField;
    }


}
