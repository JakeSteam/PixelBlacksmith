package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.AdvertHelper;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;

public class InterstitialActivity extends Activity {
    private static DisplayHelper dh;
    private AdvertHelper.advertPurpose purpose;
    private boolean timerEnded = false;
    private boolean calledCallback = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);
        dh = DisplayHelper.getInstance(getApplicationContext());
        dh.updateFullscreen(this);

        Intent intent = getIntent();
        purpose = AdvertHelper.advertPurpose.valueOf(intent.getStringExtra(AdvertHelper.INTENT_ID));
        timerEnded = false;

        setupTimer();

        LinearLayout root = (LinearLayout) findViewById(R.id.interstitialLayout);
        for (int i = 0; i < root.getChildCount(); i++) {
            if (root.getChildAt(i) instanceof TextViewPixel) {
                ((TextViewPixel) root.getChildAt(i)).setMovementMethod(LinkMovementMethod.getInstance());
            }
        }

        String[] tipArray = getResources().getStringArray(R.array.tipsArray);
        for (int i = 1; i <= 5; i++) {
            root.addView(dh.createTextView("Tip " + i + ": " + tipArray[new Random().nextInt(tipArray.length)] + "\n", 22));
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (timerEnded && !calledCallback) {
            AdvertHelper.getInstance(this).triggerCallback(purpose);
            calledCallback = true;
        }
    }

    public void setupTimer() {
        final TextView countdownTimer = (TextView)findViewById(R.id.countdownTimer);
        new CountDownTimer(Constants.ADVERT_TIMEOUT, DateHelper.MILLISECONDS_IN_SECOND) {
            public void onTick(long millisUntilFinished) {
                int timeLeft = (int) (millisUntilFinished / 1000) + 1;
                countdownTimer.setText(String.format(getString(R.string.interstitialTimeLeft), timeLeft));
            }

            public void onFinish() {
                timerEnded = true;
                countdownTimer.setText(String.format(getString(R.string.interstitialTimeLeft), 0));
                countdownTimer.setTextColor(Color.parseColor("#267c18"));
                countdownTimer.setPaintFlags(countdownTimer.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

        }.start();
    }

    public void closePopup(View view) {
        finish();
    }
}
