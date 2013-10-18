package net.mantucon.baracus.validation;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 02.10.13
 * Time: 05:30
 *
 * Interface indicating, that a view has got a validation callback. implement this
 * in Your view in order to receive validation callbacks (e.g. to manage validation-
 * dependent visibility like enabling an OK-Button etc).
 *
 */
public interface ValidatableView {

    public void onValidation();

}
