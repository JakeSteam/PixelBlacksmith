package uk.co.jakelee.blacksmith.controls;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import uk.co.jakelee.blacksmith.helper.DisplayHelper;

public class HorizontalDots extends LinearLayout {

    public HorizontalDots(Context context) {
        super(context);
    }

    public HorizontalDots(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addDots(DisplayHelper dh, int numChildren, int selectedDot) {
        this.removeAllViews();
        for (int i = 0; i < numChildren; i++) {
            if (i == selectedDot) {
                this.addView(dh.createTextView("x", 10, Color.RED));
            } else {
                this.addView(dh.createTextView("o", 10));
            }

            if (i < numChildren - 1) {
                this.addView(dh.createSpace());
            }
        }
    }
}
