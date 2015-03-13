package org.baracus.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;
import org.baracus.context.BaracusApplicationContext;

/**
 * Created with IntelliJ IDEA.
 * User: marcus
 */
public class Popup {

    public static void showPopup(Context context, String message) {
        AlertDialog.Builder popupBuilder = new AlertDialog.Builder(context);
        TextView myMsg = new TextView(BaracusApplicationContext.getContext());
        myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        myMsg.setText(message);
        popupBuilder.setView(myMsg);
        popupBuilder.show();
    }

}
