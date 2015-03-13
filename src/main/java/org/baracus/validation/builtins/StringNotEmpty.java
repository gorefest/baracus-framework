package org.baracus.validation.builtins;


import org.baracus.R;
import org.baracus.validation.AbstractValidator;
import org.baracus.validation.ConstrainedView;

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
        if (value == null || value.toString().trim().length() == 0) {
            return false;
        }
        return true;

    }

    @Override
    public int getMessageId() {
        return R.string.notNullField;
    }


}
