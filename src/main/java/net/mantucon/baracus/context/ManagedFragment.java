package net.mantucon.baracus.context;

import android.app.Fragment;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 17.05.13
 * <p/>
 * This class is needed in order to juxtaposition the reinstantiaon of Fragment beans
 * by android e.g. when a device rotate happens - unless I have found something
 * more appropriate for it (Hooking into the lifecycle in android handling the fragments
 * must be possible)
 * <p/>
 * Simple inherit this class and register it as usual as a Fragment bean and all accesses
 * to injected beans will work fine :)
 */
public abstract class ManagedFragment extends Fragment {

    // set this View from derived classes if You want to use automatic error handling
    protected View view;

    public ManagedFragment() {
        BaracusApplicationContext.performInjectionsOn(this);
    }

    @Override
    public void onDestroyView() {
        if (view != null) { // if you use the automatic error routing extension, you MUST set the view
            // when inflating the form. Notice, this is normally done in the
            // onCreateView function. If You do not set the view field when inflating
            // a new view, you will create memory leaks, because all bound errorHandlers
            // cannot be marked for removal by the garbage collector because they are
            // held in the application context bound to the containing view instance!
            BaracusApplicationContext.unregisterErrorhandlersForView(this.view);
        }
        super.onDestroyView();
    }

    /**
     * enables the validation of the view onFocusChanged event
     * If you want Your View to
     * be able to receive a validation callback - e.g. in order to manage the
     * visibility of an OK-Button or sth. - Your View must implement
     * the @see ValidatableView interface in order to receive a validation
     * notification.
     * <p/>
     * Notice, You must set the underlying view instance to enable this feature!
     */
    public void enableFocusChangeBasedValidation() {
        BaracusApplicationContext.registerValidationListener(this.view);
    }


    /**
     * enables the validation of the view onFocusChanged event
     * If you want Your View to
     * be able to receive a validation callback - e.g. in order to manage the
     * visibility of an OK-Button or sth. - Your View must implement
     * the @see ValidatableView interface in order to receive a validation
     * notification.
     *
     * @param v - the underlying view, which will be set on the this.view variable
     *
     */
    public void enableFocusChangeBasedValidation(View v) {
        this.view = v;
        enableFocusChangeBasedValidation();
    }

}
