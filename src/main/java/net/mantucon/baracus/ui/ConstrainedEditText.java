package net.mantucon.baracus.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import net.mantucon.baracus.context.BaracusApplicationContext;
import net.mantucon.baracus.util.Logger;
import net.mantucon.baracus.validation.ConstrainedView;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * EditText which can be fitted with constraints. This element is an
 * implementation of ConstrainedView. To create your own constrained elements
 * simply inherit the control you want to extend with constraints and implement
 * the ConstrainedView interface.
 */
public class ConstrainedEditText extends EditText implements ConstrainedView<String> {

    private String validators;

    public ConstrainedEditText(Context context) {
        super(context);
    }

    public ConstrainedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttribs(attrs);
    }

    public ConstrainedEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseAttribs(attrs);
    }

    private void parseAttribs(AttributeSet attributeSet) {

        Logger log = new Logger(this.getClass());
        log.info("---------------------------------");
        int i = 0;
        boolean cont = true;
        while (i < attributeSet.getAttributeCount() && cont) {
            log.info("$1 -> $2 -> $3", attributeSet.getAttributeName(i), attributeSet.getAttributeValue(i), attributeSet.getPositionDescription());
            if ("validatedBy".equals(attributeSet.getAttributeName(i))) {
                this.validators = attributeSet.getAttributeValue(i);
                log.info("VALIDATORS HAS BEEN SET TO $1", validators);
                cont = false;
            }
            ++i;
        }

        BaracusApplicationContext.verifyValidators(validators);

        log.info("---------------------------------");
    }


    @Override
    public String getCurrentValue() {
        return this.getText() != null ? getText().toString() : null;
    }

    public String getValidators() {
        return validators;
    }
}
