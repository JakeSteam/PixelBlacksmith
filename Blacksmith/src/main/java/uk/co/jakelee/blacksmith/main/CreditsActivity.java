package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;

public class CreditsActivity extends Activity {
    public static DisplayHelper dh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);
        dh = DisplayHelper.getInstance(getApplicationContext());
    }


    public void closePopup(View view) {
        finish();
    }
}