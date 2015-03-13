package org.baracus.validation;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 02.10.13
 * Time: 05:30
 * <p/>
 * Interface indicating, that a view has got a validation callback. implement this
 * in your view in order to receive validation callbacks (e.g. to manage validation-
 * dependent visibility like enabling an OK-Button etc).
 */
public interface ValidatableView {

    /**
     * Method callback for validateable views. Let your activity or fragment implement
     * and register it for validation like usual using the ApplicationContext.
     * If Baracus detects the implementation of this interface, the callback will be
     * fired on each validation.
     */
    public void onValidation();

}
