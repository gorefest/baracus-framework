package net.mantucon.baracus.context;

import android.app.Fragment;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 17.05.13
 *
 * This class is needed in order to juxtaposition the reinstantiaon of Fragment beans
 * by android e.g. when a device rotate happens - unless I have found something
 * more appropriate for it (Hooking into the lifecycle in android handling the fragments
 * must be possible)
 *
 * Simple inherit this class and register it as usual as a Fragment bean and all accesses
 * to injected beans will work fine :)
 *
 */
public abstract class ManagedFragment extends Fragment {

    // set this View from derived classes if You want to use automatic error handling
    protected View view;

    public ManagedFragment() {
        BaracusApplicationContext.performInjectionsOn(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (view != null) { // if you use the automatic error routing extension, you MUST set the view
                            // when inflating the form. Notice, this is normally done in the
                            // onCreateView function. If You do not set the view field when inflating
                            // a new view, you will create memory leaks, because all bound errorHandlers
                            // cannot be marked for removal by the garbage collector because they are
                            // held in the application context bound to the containing view instance!
            BaracusApplicationContext.unregisterErrorhandlersForView(this.view);
        }
    }
}
