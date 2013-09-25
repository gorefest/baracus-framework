package net.mantucon.baracus.errorhandling;

import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 25.09.13
 * Time: 17:47
 *
 * Interface for bridging baracus error handling onto normal android
 * error messaging. This technique mostly relies on the type (@see TextEditErrorHandler)
 *
 */
public interface StandardErrorHandler extends ErrorHandler{

    /**
     * returns true, if the implementation class is able to make standard error
     * handling on the passed view
     *
     * @param v - the view ask for
     * @return true, if the handler is able to handle the passed component
     */
    public boolean canHandleView(View v);

}
