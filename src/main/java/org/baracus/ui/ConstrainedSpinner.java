package org.baracus.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;
import org.baracus.context.BaracusApplicationContext;
import org.baracus.util.Logger;
import org.baracus.validation.ConstrainedView;

/**
 * Constraineable Spinner
 */
public class ConstrainedSpinner extends Spinner implements ConstrainedView<Object> {

    // comma seperated list of validators
    private String validators;

    public ConstrainedSpinner(Context context) {
        super(context);
    }

    public ConstrainedSpinner(Context context, int mode) {
        super(context, mode);
    }

    public ConstrainedSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttribs(attrs);
    }

    public ConstrainedSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseAttribs(attrs);
    }

    public ConstrainedSpinner(Context context, AttributeSet attrs, int defStyle, int mode) {
        super(context, attrs, defStyle, mode);
        parseAttribs(attrs);
    }

    @Override
    public Object getCurrentValue() {
        return getSelectedItem();
    }

    @Override
    public String getValidators() {
        return validators;
    }

    private void parseAttribs(AttributeSet attributeSet) {

        Logger log = new Logger(this.getClass());
        log.info("---------------------------------");
        int i = 0;
        boolean cont = true;
        while (i < attributeSet.getAttributeCount() && cont) {
            log.info("$1 -> $2 -> $3", attributeSet.getAttributeName(i), attributeSet.getAttributeValue(i), attributeSet.getPositionDescription());
            if ("validatedSpinnerBy".equals(attributeSet.getAttributeName(i))) {
                this.validators = attributeSet.getAttributeValue(i);
                log.info("VALIDATORS HAS BEEN SET TO $1", validators);
                cont = false;
            }
            ++i;
        }

        BaracusApplicationContext.verifyValidators(validators);

        log.info("---------------------------------");
    }


}
