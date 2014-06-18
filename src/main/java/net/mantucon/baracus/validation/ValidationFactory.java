package net.mantucon.baracus.validation;

import android.view.View;
import android.view.ViewGroup;
import net.mantucon.baracus.annotations.Bean;
import net.mantucon.baracus.context.BaracusApplicationContext;
import net.mantucon.baracus.errorhandling.ErrorHandlingFactory;
import net.mantucon.baracus.errorhandling.ErrorSeverity;
import net.mantucon.baracus.lifecycle.Destroyable;
import net.mantucon.baracus.lifecycle.Initializeable;
import net.mantucon.baracus.util.Logger;
import net.mantucon.baracus.validation.builtins.*;

import java.util.HashMap;
import java.util.Map;

import static net.mantucon.baracus.util.StringUtil.*;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 24.09.13
 * Time: 06:57
 * To change this template use File | Settings | File Templates.
 */
@Bean
public class ValidationFactory implements Initializeable, Destroyable {

    private static final Map<String, Validator<?>> namedValidators = new HashMap<String, Validator<?>>();
    private static final Logger logger = new Logger(ValidationFactory.class);

    @Bean
    ErrorHandlingFactory errorHandlingFactory;

    /**
     * Exception thrown, if an invalid validator name is used within the code
     */
    public static final class InvalidValidatorNameException extends RuntimeException {
        private final String validatorName;

        public InvalidValidatorNameException(String validatorName) {
            super();
            this.validatorName = validatorName;
        }

        @Override
        public String getMessage() {
            return "A VALIDATOR NAMED " + validatorName + " COULD NOT BE FOUND! AVAILABLE VALIDATORS :" + join(namedValidators.keySet());
        }
    }


    /**
     * register a named validator
     *
     * @param name      - name of the validator to be used in XML
     * @param validator - the validator instance
     */
    public synchronized void registerValidator(String name, Validator<?> validator) {
        namedValidators.put(name, validator);
    }

    /**
     * register a named validator, the name will be derived from the class name
     *
     * @param validator - the validator instance
     */
    public synchronized void registerValidator(Validator<?> validator) {
        String name = firstByteToLower(validator.getClass().getSimpleName());
        namedValidators.put(name, validator);
    }

    /**
     * verify that all validators in the list are bound to an existing validator
     *
     * @param validatorList - a comma seperated string containing all validators
     */
    public void verifyValidators(String validatorList) {
        if (validatorList != null && validatorList.trim().length() > 0) {
            String[] validators = validatorList.split(",");
            for (String v : validators) {
                if (!namedValidators.containsKey(v)) {
                    logger.error("A named validator $1 could not be found. Available validators : $2", v, join(namedValidators.keySet()));
                    throw new InvalidValidatorNameException(v);
                }
            }
        }
    }

    /**
     * Verify an entire View. This function will recurse all child views and look for
     * View Components implementing the ConstrainedView interface.
     * <p/>
     * If a component is found, it will be validated by all validators denoted through the
     * validatedBy attribute. If a violation occurs, it is matter
     *
     * @param v - the view to verify
     */
    protected void validateView(View root, View v) {
        if (ConstrainedView.class.isAssignableFrom(v.getClass())) {
            ConstrainedView cv = (ConstrainedView) v;
            String[] validators = splitPurified(cv.getValidators(), ",");
            if (validators != null && validators.length > 0) {
                boolean stop = false;
                int i = 0;
                while (!stop) {
                    String validatorName = validators[i++];
                    Validator validator = namedValidators.get(validatorName);
                    if (validator != null && !validator.validate(cv)) {
                        errorHandlingFactory.addErrorToView(root, v.getId(), validator.getMessageId(), ErrorSeverity.ERROR, validator.viewToMessageParams(v));
                        stop = true;
                    }
                    stop |= (i >= validators.length);
                }
            }
        }

        if (ViewGroup.class.isAssignableFrom(v.getClass())) {
            ViewGroup viewGroup = (ViewGroup) v;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View nextChild = viewGroup.getChildAt(i);
                if (nextChild != null) {
                    validateView(root, nextChild);
                }
            }
        }
    }


    /**
     * Entry function for view validation
     *
     * @param v
     */
    public void validateView(View v) {
        validateView(v, v);
    }

    /**
     * registers the built in validators
     */
    @Override
    public void postConstruct() {
        registerValidator(new StringNotEmpty());
        registerValidator(new StringIsNumericDouble());
        registerValidator(new StringIsNumericInteger());
        registerValidator(new NumberMustBeGreaterThanZero());
        registerValidator(new DateFromNow());
    }

    @Override
    public void onDestroy() {
        namedValidators.clear();
    }


    /**
     * walkt the view tree in order to find components to register
     *
     * @param root
     * @param v
     */
    protected void registerValidationListener(final View root, final View v) {
        if (ConstrainedView.class.isAssignableFrom(v.getClass())) {
            v.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    BaracusApplicationContext.validateView(root);
                    if (ValidatableView.class.isAssignableFrom(root.getClass())) {
                        ValidatableView validatableView = (ValidatableView) root;
                        validatableView.onValidation();
                    }
                }
            });
        }

        if (ViewGroup.class.isAssignableFrom(v.getClass())) {
            ViewGroup viewGroup = (ViewGroup) v;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View nextChild = viewGroup.getChildAt(i);
                if (nextChild != null) {
                    registerValidationListener(root, nextChild);
                }
            }
        }
    }

    /**
     * registers an onFocusChangeListener to all view elements implementing
     * the @see ConstrainedView interface to perform on-the-fly-validation.
     * If you want Your View to be able to receive a validation callback
     * - e.g. in order to manage the visibility of an OK-Button or sth. -
     * Your View must implement the @see ValidatableView interface in
     * order to receive a validation notification callbacks
     *
     * @param root - the view to perform validation on
     */
    public void registerValidationListener(final View root) {
        registerValidationListener(root, root);
    }

}
