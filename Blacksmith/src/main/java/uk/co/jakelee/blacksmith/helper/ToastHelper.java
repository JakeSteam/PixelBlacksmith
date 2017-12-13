package uk.co.jakelee.blacksmith.helper;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
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
    public static final int SHORT = Setting.getSafeBoolean(Constants.SETTING_LONG_TOAST) ? 11000 : Snackbar.LENGTH_SHORT;
    public static final int LONG = Setting.getSafeBoolean(Constants.SETTING_LONG_TOAST) ? 35000 : Snackbar.LENGTH_LONG;
    public static final int EXTRALONG = Setting.getSafeBoolean(Constants.SETTING_LONG_TOAST) ? 65000 : 15000;

    public static void showToast(View view, int length, String text, boolean saveToLog) {
        showToast(view, length, text, saveToLog, R.color.lightBrown);
    }

    public static void showErrorToast(View view, int length, String text, boolean saveToLog) {
        showToast(view, length, text, saveToLog, R.color.holo_red_dark);
    }

    public static void showTipToast(View view, int length, String text, boolean saveToLog) {
        showToast(view, length, text, saveToLog, R.color.holo_blue_dark);
    }

    public static void showPositiveToast(View view, int length, String text, boolean saveToLog) {
        showToast(view, length, text, saveToLog, R.color.holo_green_dark);
    }

    public static void showToast(View targetView, int length, String text, boolean saveToLog, int color) {
        if (targetView == null) {
            targetView = MainActivity.questContainer;
        }

        if (targetView == null) {
            return;
        }

        final Snackbar snackbar = Snackbar.make(targetView, text, length);
        final Context context = targetView.getContext();

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
        snackbarView.setPadding(
                convertDpToPixel(context, -5), // Left
                convertDpToPixel(context, -18), // Top
                convertDpToPixel(context, -5), // Right
                convertDpToPixel(context, -18)); // Bottom
        snackbarView.setBackgroundResource(color);

        TextView snackbarText = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        snackbarText.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BetterPixels.ttf"));
        snackbarText.setTextSize(20);
        snackbarText.setMinLines(2);
        snackbarText.setMaxLines(10);
        snackbarText.setGravity(Gravity.CENTER_VERTICAL);

        if (saveToLog) {
            Message.add(text);
        }

        snackbar.show();
    }

    public static int convertDpToPixel(Context context, float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }
}
