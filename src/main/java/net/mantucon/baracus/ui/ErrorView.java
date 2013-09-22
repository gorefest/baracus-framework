package net.mantucon.baracus.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import net.mantucon.baracus.R;
import net.mantucon.baracus.context.BaracusApplicationContext;
import net.mantucon.baracus.errorhandling.ErrorHandler;
import net.mantucon.baracus.errorhandling.ErrorSeverity;
import net.mantucon.baracus.util.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 * Date: 20.09.13
 * Time: 07:54
 * To change this template use File | Settings | File Templates.
 */
public class ErrorView extends TextView implements ErrorHandler {

    private int displayMessageFor;
    private boolean highlightTarget;
    private int originalColour;

    public ErrorView(Context context) {
        super(context);
        BaracusApplicationContext.registerErrorHandler(this);
    }

    public ErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttribs(attrs);
        BaracusApplicationContext.registerErrorHandler(this);
    }

    public ErrorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        parseAttribs(attrs);
        BaracusApplicationContext.registerErrorHandler(this);
    }

    private void parseAttribs(AttributeSet attributeSet) {

        Logger log = new Logger(this.getClass());
        log.info("---------------------------------");
        for (int i = 0; i < attributeSet.getAttributeCount(); ++i) {
            log.info("$1 -> $2 -> $3", attributeSet.getAttributeName(i), attributeSet.getAttributeValue(i), attributeSet.getPositionDescription());
            if ("displayMessageFor".equals(attributeSet.getAttributeName(i))) {
                displayMessageFor = attributeSet.getAttributeResourceValue(i,-1);
                log.info("DISPLAY MESSAGE HAS BEEN SET TO $1", displayMessageFor);
            }

        }
        log.info("---------------------------------");

//        this.displayMessageFor = attributeSet.getAttributeResourceValue("app","displayMessageFor",-1);
//        this.displayMessageFor = attributeSet.getAttributeIntValue(R.styleable.ErrorView_displayMessageFor,-1);



        this.highlightTarget = attributeSet.getAttributeBooleanValue(R.styleable.ErrorView_highlightTarget, false);
    }

    public int getDisplayMessageFor() {
        return displayMessageFor;
    }

    public void setDisplayMessageFor(int displayMessageFor) {
        this.displayMessageFor = displayMessageFor;
    }

    public Boolean getHighlightTarget() {
        return highlightTarget;
    }

    public void setHighlightTarget(Boolean highlightTarget) {
        this.highlightTarget = highlightTarget;
    }

    @Override
    public void handleError(View view, int errorMessageId, ErrorSeverity severity, String... params) {
        String errorMessage = BaracusApplicationContext.resolveString(errorMessageId, params);
        ErrorView visualRepresentation  = (ErrorView ) view.findViewById(this.getId());
        visualRepresentation.setText(errorMessage);

        if (highlightTarget && displayMessageFor != -1) {
            View target = view.findViewById(getIdToDisplayFor());
            originalColour = target.getDrawingCacheBackgroundColor();
            target.setBackgroundColor(Color.argb(100, 50, 50, 50));
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
