package uk.co.jakelee.blacksmith.controls;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewPixel extends AppCompatTextView {
    private static Typeface mTypeface;

    public TextViewPixel(final Context context) {
        this(context, null);
    }

    public TextViewPixel(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextViewPixel(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/BetterPixels.ttf");
        }
        setTypeface(mTypeface);
    }
}
