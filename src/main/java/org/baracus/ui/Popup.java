package org.baracus.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.TextView;
import org.baracus.R;
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


    /**
     * simplified popup
     *
     * @param message the message to display
     */
    public static void showPopup(String message) {
        showPopup(BaracusApplicationContext.getContext(), message);
    }


    /**
     * Simplifies the creation of generic yes-no-dialogs
     */
    public interface DialogCallback {
        void fire();
    }

    /**
     * launch a yesno-dialog
     *
      * @param message - the question to ask
     * @param yes - the yes action
     * @param no - the no action
     */
    public static void yesNo(Context context, String message,  final DialogCallback yes, final DialogCallback no) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        if (yes != null) {
                            yes.fire();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        if (no != null) {
                            no.fire();
                        }
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message).setPositiveButton(BaracusApplicationContext.resolveString(R.string.yes), dialogClickListener)
            .setNegativeButton(R.string.no, dialogClickListener).show();
    }





}
