package uk.co.jakelee.blacksmith.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.main.MainActivity;
import uk.co.jakelee.blacksmith.main.MessagesActivity;
import uk.co.jakelee.blacksmith.model.Message;
import uk.co.jakelee.blacksmith.model.Setting;

public class ToastHelper {
    public static int SHORT = Snackbar.LENGTH_SHORT;
    public static int LONG = Snackbar.LENGTH_LONG;

    public static void showToast(Context context, int length, int textID, boolean saveToLog) {
        String string = context.getResources().getString(textID);
        showToast(context, length, string, saveToLog);
    }

    public static void showToast(Context context, int length, String text, boolean saveToLog) {
        showToast(context, length, text, saveToLog, R.color.lightBrown);
    }

    public static void showErrorToast(Context context, int length, int textID, boolean saveToLog) {
        String string = context.getResources().getString(textID);
        showErrorToast(context, length, string, saveToLog);
    }

    public static void showErrorToast(Context context, int length, String text, boolean saveToLog) {
        showToast(context, length, text, saveToLog, R.color.holo_red_dark);
    }

    public static void showTipToast(Context context, int length, String text, boolean saveToLog) {
        showToast(context, length, text, saveToLog, R.color.holo_blue_dark);
    }

    public static void showPositiveToast(Context context, int length, int textID, boolean saveToLog) {
        String string = context.getResources().getString(textID);
        showPositiveToast(context, length, string, saveToLog);
    }

    public static void showPositiveToast(Context context, int length, String text, boolean saveToLog) {
        showToast(context, length, text, saveToLog, R.color.holo_green_dark);
    }

    public static void showToast(final Context context, int length, String text, boolean saveToLog, int color) {
        final Snackbar snackbar = Snackbar.make(MainActivity.questContainer, text, length);

        View snackbarView = snackbar.getView();
        snackbarView.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (Setting.getSafeBoolean(Constants.SETTING_MESSAGE_LOG)) {
                    Intent intent = new Intent(context, MessagesActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else {
                    snackbar.dismiss();
                }
            }
        });
        snackbarView.setPadding(-12, -45, -12, -45);
        snackbarView.setBackgroundResource(color);

        TextView snackbarText = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        snackbarText.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BetterPixels.ttf"));
        snackbarText.setTextSize(18);
        snackbarText.setMinLines(2);
        snackbarText.setMaxLines(10);
        snackbarText.setGravity(Gravity.CENTER_VERTICAL);

        if (saveToLog) {
            Message.add(text);
        }

        snackbar.show();
    }
}
