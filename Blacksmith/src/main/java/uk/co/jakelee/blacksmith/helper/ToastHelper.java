package uk.co.jakelee.blacksmith.helper;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.model.Message;

public class ToastHelper {
    private static Toast internalToast;

    public static void showToast(Context context, int length, int textID, boolean saveToLog) {
        String string = context.getResources().getString(textID);
        showToast(context, length, string, saveToLog);
    }

    public static void showToast(Context context, int length, String text, boolean saveToLog) {
        showToast(context, length, text, saveToLog, R.color.lightBrown);
    }

    public static void showErrorToast(Context context, int length, String text, boolean saveToLog) {
        showToast(context, length, text, saveToLog, R.color.holo_red_dark);
    }

    public static void showPositiveToast(Context context, int length, String text, boolean saveToLog) {
        showToast(context, length, text, saveToLog, R.color.holo_green_dark);
    }

    public static void showToast(Context context, int length, String text, boolean saveToLog, int color) {
        cancelCurrentToast();

        LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
        View inflatedView = inflater.inflate(R.layout.custom_toast, null);
        LinearLayout toastLayout = (LinearLayout) inflatedView.findViewById(R.id.toast);
        toastLayout.setBackgroundResource(color);

        TextView textView = (TextView) toastLayout.findViewById(R.id.text);
        textView.setText(text);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
        toast.setDuration(length);
        toast.setView(toastLayout);

        internalToast = toast;
        internalToast.show();

        if (saveToLog) {
            Message.add(text);
        }
    }

    private static void cancelCurrentToast() {
        if (internalToast != null) {
            internalToast.cancel();
        }
    }
}
