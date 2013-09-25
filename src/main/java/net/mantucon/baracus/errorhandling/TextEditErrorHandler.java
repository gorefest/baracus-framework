package net.mantucon.baracus.errorhandling;

import android.view.View;
import android.widget.TextView;
import net.mantucon.baracus.context.BaracusApplicationContext;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 25.09.13
 * Time: 17:48
 *
 * Generic Error handling component to be applied on all TextViews passed for validation (if there
 * is any error to route to the view component)
 *
 *
 */
public class TextEditErrorHandler implements StandardErrorHandler {

    @Override
    public boolean canHandleView(View v) {
        return (v != null && TextView.class.isAssignableFrom(v.getClass())); // Bind this handler to be applied to all
                                                                             // Text views
    }

    @Override
    public void handleError(View view, int errorMessageId, ErrorSeverity severity, String... params) {
        // This function applies the error message to the passed text view
        String message = BaracusApplicationContext.resolveString(errorMessageId, params);
        TextView v  = (TextView) view;
        v.setError(message);
    }

    @Override
    public void reset(View view) {
        // This function removes the error message from the passed view
        TextView v  = (TextView) view;
        v.setError(null);
    }
}
