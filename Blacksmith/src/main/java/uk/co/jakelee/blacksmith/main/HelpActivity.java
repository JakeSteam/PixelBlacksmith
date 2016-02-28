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
    public static int MAIN = 1;
    public static int FURNACE = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        dh = DisplayHelper.getInstance(getApplicationContext());

        int helpToLoad = getIntent().getIntExtra(INTENT_ID, 0);
        displayHelp(helpToLoad);
    }

    public void displayHelp(int helpToLoad) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.helpLayout);

        if (helpToLoad == HELP) {
            displayHelpHelp(layout);
        } else if (helpToLoad == MAIN) {
            displayHelpMain(layout);
        } else if (helpToLoad == FURNACE) {
            displayHelpFurnace(layout);
        }
    }

    public void displayHelpHelp(LinearLayout layout) {
        layout.addView(dh.createTextView("Here is some help for the help. Meta, eh?", 18));
    }

    public void displayHelpMain(LinearLayout layout) {
        layout.addView(dh.createTextView("Overview of the game, as well as info on the help system.", 18));
    }

    public void displayHelpFurnace(LinearLayout layout) {
        layout.addView(dh.createTextView("And here is a bit of help for the furnace. Not at all meta.", 18));
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
