package uk.co.jakelee.blacksmith.helper;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.model.Message;

public class ToastHelper {
    private static Toast internalToast;

    public static void showToast(Context context, int length, int textID) {
        String string = context.getResources().getString(textID);
        showToast(context, length, string);
    }

    public static void showToast(Context context, int length, String text) {
        cancelCurrentToast();

        LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
        View inflatedView = inflater.inflate(R.layout.custom_toast, null);
        View toastLayout = inflatedView.findViewById(R.id.toast);

        TextView textView = (TextView) toastLayout.findViewById(R.id.text);
        textView.setText(text);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
        toast.setDuration(length);
        toast.setView(toastLayout);

        internalToast = toast;
        internalToast.show();

        Message.add(text);
    }

    private static void cancelCurrentToast() {
        if (internalToast != null) {
            internalToast.cancel();
        }
    }
}
