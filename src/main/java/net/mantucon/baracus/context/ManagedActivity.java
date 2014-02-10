package net.mantucon.baracus.context;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * User: marcus
 * Date: 23.09.13
 * Time: 08:34
 */
public class ManagedActivity extends Activity{

    protected View underlyingView;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setUnderlyingView();
    }

    @Override
    protected void onDestroy() {
        BaracusApplicationContext.unregisterErrorhandlersForView(underlyingView);
        super.onDestroy();
    }


    /**
     * enables the validation of the view onFocusChanged
     * If you want Your View to
     * be able to receive a validation callback - e.g. in order to manage the
     * visibility of an OK-Button or sth. - Your View must implement
     * the @see ValidatableView interface in order to receive a validation
     * notification.
     *
     * Notice, You must set the underlying view instance to enable this feature!
     *
     */
    public void enableFocusChangeBasedValidation() {
        if (underlyingView == null) {
            setUnderlyingView();
        }
        BaracusApplicationContext.registerValidationListener(underlyingView);
    }

    private void setUnderlyingView() {
        this.underlyingView = getWindow().getDecorView().findViewById(android.R.id.content);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);    //To change body of overridden methods use File | Settings | File Templates.
    }


    /**
     * perform a validation on all validateable fields, map the errors the the view. This is a shortcut
     * method instead of calling all the context functions manually.
     *
     * @return a boolean indicating whether the view is valid (true = valid, false = invalid, has errors)
     */
    public boolean validate() {
        BaracusApplicationContext.resetErrors(underlyingView);
        BaracusApplicationContext.validateView(underlyingView);
        BaracusApplicationContext.applyErrorsOnView(underlyingView);
        return !BaracusApplicationContext.viewHasErrors(underlyingView);
    }



}
