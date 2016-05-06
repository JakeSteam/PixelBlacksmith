package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.LinearLayout;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;

public class CreditsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        // Make all the links work
        LinearLayout root = (LinearLayout) findViewById(R.id.creditsContainer);
        for (int i = 0; i < root.getChildCount(); i++) {
            if (root.getChildAt(i) instanceof TextViewPixel) {
                ((TextViewPixel) root.getChildAt(i)).setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Credits);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
