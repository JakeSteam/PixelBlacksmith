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
    public static int ANVIL = 3;
    public static int INVENTORY = 4;
    public static int CREDITS = 5;
    public static int ENCHANTING = 6;
    public static int MINE = 7;
    public static int SETTINGS = 8;
    public static int SHOP = 9;
    public static int STATISTICS = 10;
    public static int TABLE = 11;
    public static int TRADE = 12;
    public static int TROPHY = 13;
    public static int VISITOR = 14;

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
        } else if (helpToLoad == ANVIL) {
            displayHelpAnvil(layout);
        } else if (helpToLoad == INVENTORY) {
            displayHelpInventory(layout);
        } else if (helpToLoad == CREDITS) {
            displayHelpCredits(layout);
        } else if (helpToLoad == ENCHANTING) {
            displayHelpEnchanting(layout);
        } else if (helpToLoad == MINE) {
            displayHelpMine(layout);
        } else if (helpToLoad == SETTINGS) {
            displayHelpSettings(layout);
        } else if (helpToLoad == SHOP) {
            displayHelpShop(layout);
        } else if (helpToLoad == STATISTICS) {
            displayHelpStatistics(layout);
        } else if (helpToLoad == TABLE) {
            displayHelpTable(layout);
        } else if (helpToLoad == TRADE) {
            displayHelpTrade(layout);
        } else if (helpToLoad == TROPHY) {
            displayHelpTrophy(layout);
        } else if (helpToLoad == VISITOR) {
            displayHelpVisitor(layout);
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

    public void displayHelpAnvil(LinearLayout layout) {
        layout.addView(dh.createTextView("And here is a bit of help for the anvil. Not at all meta.", 18));
    }

    public void displayHelpInventory(LinearLayout layout) {
        layout.addView(dh.createTextView("And here is a bit of help for the inventory. Not at all meta.", 18));
    }

    public void displayHelpCredits(LinearLayout layout) {
        layout.addView(dh.createTextView("And here is a bit of help for the credits. Not at all meta.", 18));
    }

    public void displayHelpEnchanting(LinearLayout layout) {
        layout.addView(dh.createTextView("And here is a bit of help for the enchanting. Not at all meta.", 18));
    }

    public void displayHelpMine(LinearLayout layout) {
        layout.addView(dh.createTextView("And here is a bit of help for the mine. Not at all meta.", 18));
    }

    public void displayHelpSettings(LinearLayout layout) {
        layout.addView(dh.createTextView("And here is a bit of help for the settings. Not at all meta.", 18));
    }

    public void displayHelpShop(LinearLayout layout) {
        layout.addView(dh.createTextView("And here is a bit of help for the shop. Not at all meta.", 18));
    }

    public void displayHelpStatistics(LinearLayout layout) {
        layout.addView(dh.createTextView("And here is a bit of help for the statistics. Not at all meta.", 18));
    }

    public void displayHelpTable(LinearLayout layout) {
        layout.addView(dh.createTextView("And here is a bit of help for the table. Not at all meta.", 18));
    }

    public void displayHelpTrade(LinearLayout layout) {
        layout.addView(dh.createTextView("And here is a bit of help for the trade. Not at all meta.", 18));
    }

    public void displayHelpTrophy(LinearLayout layout) {
        layout.addView(dh.createTextView("And here is a bit of help for the trophy. Not at all meta.", 18));
    }

    public void displayHelpVisitor(LinearLayout layout) {
        layout.addView(dh.createTextView("And here is a bit of help for the visitor. Not at all meta.", 18));
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
