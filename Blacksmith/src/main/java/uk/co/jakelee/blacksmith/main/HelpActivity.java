package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;

public class HelpActivity extends Activity {
    public static DisplayHelper dh;

    public static String INTENT_ID = "uk.co.jakelee.blacksmith.helptoload";
    public static int HELP = 0;
    public static int FURNACE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        dh = DisplayHelper.getInstance(getApplicationContext());

        int helpToLoad = getIntent().getIntExtra(INTENT_ID, 0);
        displayHelp(helpToLoad);
    }

    public void displayHelp(int helpToLoad) {
        LinearLayout layout = (LinearLayout)findViewById(R.id.helpLayout);
        layout.addView(dh.createTextView("This is just some sample text. It won't be here long. Apologies if you see this!", 18));
        layout.addView(dh.createTextView("This is just some sample text. It won't be here long. Apologies if you see this!", 18));
        layout.addView(dh.createTextView("This is just some sample text. It won't be here long. Apologies if you see this!", 18));
        layout.addView(dh.createTextView("Oh, and the one to load was: " + helpToLoad, 18));
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.HELP);
        startActivity(intent);
        this.finish();
    }

    public void closePopup(View view) {
        finish();
    }
}
