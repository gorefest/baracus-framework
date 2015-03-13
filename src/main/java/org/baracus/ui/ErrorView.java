package org.baracus.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import org.baracus.context.BaracusApplicationContext;
import org.baracus.errorhandling.CustomErrorHandler;
import org.baracus.errorhandling.ErrorSeverity;
import org.baracus.util.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 20.09.13
 * Time: 07:54
 * To change this template use File | Settings | File Templates.
 */
public class ErrorView extends TextView implements CustomErrorHandler {

    private int displayMessageFor;
    private boolean highlightTarget;
    private int originalColour;

    public ErrorView(Context context) {
        super(context);
        BaracusApplicationContext.registerCustomErrorHandler(this);
    }

    public ErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttribs(attrs);
        BaracusApplicationContext.registerCustomErrorHandler(this);
    }

    public ErrorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseAttribs(attrs);
        BaracusApplicationContext.registerCustomErrorHandler(this);
    }

    private void parseAttribs(AttributeSet attributeSet) {

        Logger log = new Logger(this.getClass());
        log.info("---------------------------------");
        for (int i = 0; i < attributeSet.getAttributeCount(); ++i) {
            log.info("$1 -> $2 -> $3", attributeSet.getAttributeName(i), attributeSet.getAttributeValue(i), attributeSet.getPositionDescription());
            if ("displayMessageFor".equals(attributeSet.getAttributeName(i))) {
                this.displayMessageFor = attributeSet.getAttributeResourceValue(i, -1);
                log.info("DISPLAY MESSAGE HAS BEEN SET TO $1", displayMessageFor);
            }

            if ("highlightTarget".equals(attributeSet.getAttributeName(i))) {
                this.highlightTarget = "true".equals(attributeSet.getAttributeValue(i));
                log.info("HIGHLIGHT TARGET HAS BEEN SET TO $1", highlightTarget);
            }

        }
        log.info("---------------------------------");
    }

    /**
     * @return the ID of the component, whose errors affect this item
     */
    public int getDisplayMessageFor() {
        return displayMessageFor;
    }

    /**
     * @return true, if the referenced display shall be highlighted in case of an error
     */
    public Boolean getHighlightTarget() {
        return highlightTarget;
    }

    @Override
    public void handleError(View view, int errorMessageId, ErrorSeverity severity, String... params) {
        String errorMessage = BaracusApplicationContext.resolveString(errorMessageId, params);
        ErrorView visualRepresentation = (ErrorView) view.findViewById(this.getId());
        visualRepresentation.setText(errorMessage);

        if (highlightTarget && displayMessageFor != -1) {
            View target = view.findViewById(getIdToDisplayFor());
            originalColour = target.getDrawingCacheBackgroundColor();
            target.setBackgroundColor(Color.argb(100, 255, 100, 100));
        }
    }

    @Override
    public void reset(View view) {
        this.setText("");

        if (highlightTarget && displayMessageFor != -1) {
            View target = view.findViewById(getIdToDisplayFor());
            target.setBackgroundColor(originalColour);
        }
    }

    @Override
    public int getIdToDisplayFor() {
        return displayMessageFor;
    }

}
