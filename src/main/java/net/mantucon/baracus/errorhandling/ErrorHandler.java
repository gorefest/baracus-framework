package net.mantucon.baracus.errorhandling;

import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 20.09.13
 * Time: 08:35
 * <p/>
 * Error Handler interface. An error handler must be able to process an error and to reset
 * <p/>
 * If the error handler is bound to a view-component, it can influence the visual representation
 * or style. Also, it must be capable to reset the error - both on itself and (in case of) the bound
 * view component.
 *
 * @see net.mantucon.baracus.ui.ErrorView
 */
public interface ErrorHandler {

    /**
     * process an error
     *
     * @param view           - the view, where the error shall be applied
     * @param errorMessageId - the message Id, which shall be used to display
     * @param severity       - the severity of an error
     * @param params         - varargs substituted for $1...$n in the message text
     */
    public void handleError(View view, int errorMessageId, ErrorSeverity severity, String... params);

    /**
     * take the error view off the passed view.
     *
     * @param view - the view, where any bound components shall be reset using findById
     */
    public void reset(View view);


}
