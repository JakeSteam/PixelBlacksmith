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
    public static int MARKET = 7;
    public static int SETTINGS = 8;
    public static int TRADER = 9;
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
        } else if (helpToLoad == MARKET) {
            displayHelpMarket(layout);
        } else if (helpToLoad == SETTINGS) {
            displayHelpSettings(layout);
        } else if (helpToLoad == TRADER) {
            displayHelpTrader(layout);
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
        layout.addView(dh.createTextView("Help\n", 26));
        layout.addView(dh.createTextView("You want help for the help!?\n", 22));
        layout.addView(dh.createTextView("Get out of here, there's money to make!\n", 22));
    }

    public void displayHelpMain(LinearLayout layout) {
        layout.addView(dh.createTextView("Overview\n", 26));
        layout.addView(dh.createTextView("So, here's the deal: You're a blacksmith. A not very good one, to be honest.\n", 22));
        layout.addView(dh.createTextView("If you want to make a name for yourself, you're going to have to keep visitors happy, and keep an eye on your resources.\n", 22));
        layout.addView(dh.createTextView("Or, I guess you could ignore all the visitors and just work towards making the high end gear. But, y'know, don't do that.\n", 22));
        layout.addView(dh.createTextView("Generally, ores come from the market and other ingredients come from visitors and certain traders.\n", 22));
        layout.addView(dh.createTextView("These ores are smelted in the furnace, crafted at the anvil, finished at the table, and enchanted at the enchanting table, before being sold to a visitor.\n", 22));
        layout.addView(dh.createTextView("Visitors, like the rest of us, have preferences. Once a visitor has been sold one of their favourite types / tiers / states of item, their preference and associated bonus will be saved.\n", 22));
        layout.addView(dh.createTextView("Next time they visit, selling them preferred items will provide a nice healthy bonus tip for yourself.\n", 22));
        layout.addView(dh.createTextView("Get stuck at any stage in the process? Press the help button!\n", 22));
    }

    public void displayHelpFurnace(LinearLayout layout) {
        layout.addView(dh.createTextView("Furnace\n", 26));
        layout.addView(dh.createTextView("The furnace is the starting point for creating items.\n", 22));
        layout.addView(dh.createTextView("Ore is generally bought from passing traders at the marketplace, but it can also be given as a reward by happy visitors.\n", 22));
        layout.addView(dh.createTextView("Ore you receive will have to be smelted into bars before any items can be created with it. Some bars will require a mixer, generally coal, to facilitate the creation of bars.\n", 22));
        layout.addView(dh.createTextView("Whilst bars can be sold, they'll generally be a lot more valuable if they are first hammered into an unfinished item via the anvil.\n", 22));
        layout.addView(dh.createTextView("Swipe left and right to change items. Pressing 'Smelt Max' will create as many bars as possible with your current free slots and resources.\n", 22));
    }

    public void displayHelpAnvil(LinearLayout layout) {
        layout.addView(dh.createTextView("Anvil\n", 26));
        layout.addView(dh.createTextView("After the furnace creates bars, the anvil must be used to hammer them into shape.\n", 22));
        layout.addView(dh.createTextView("Most recipes only require bars, with more valuable / higher tier items requiring more bars.\n", 22));
        layout.addView(dh.createTextView("Of course, these unfinished items aren't quite done yet. They will still require an addition of a secondary ingredient, which is done at the crafting table.\n", 22));
        layout.addView(dh.createTextView("Swipe left and right to change items. Use the up and down arrows to change tiers.\n", 22));
        layout.addView(dh.createTextView("Pressing 'Craft Max' will create as many of the selected item as possible with your current free slots and resources.\n", 22));
    }

    public void displayHelpInventory(LinearLayout layout) {
        layout.addView(dh.createTextView("Inventory\n", 26));
        layout.addView(dh.createTextView("All of your current stock can be viewed here.\n", 22));
        layout.addView(dh.createTextView("Most items can be sold for their basic value here, although this is not recommended.\n", 22));
        layout.addView(dh.createTextView("Instead, try and create items that will sell for a large bonus with visitors.\n", 22));
        layout.addView(dh.createTextView("Scroll up and down to view all items.\n", 22));
    }

    public void displayHelpCredits(LinearLayout layout) {
        layout.addView(dh.createTextView("Credits\n", 26));
        layout.addView(dh.createTextView("Making a game is hard, but it's also extremely rewarding and educational.\n", 22));
        layout.addView(dh.createTextView("Development would be impossible without building on the work of others via open-source libraries, free resources, and places like StackOverflow.\n", 22));
        layout.addView(dh.createTextView("The credits contain a few of the larger contributions, but the helpfulness of the hundreds of articles / forums / Q&A sites visited during development can't be understated.\n", 22));
        layout.addView(dh.createTextView("Thanks, y'all!\n", 22));
    }

    public void displayHelpEnchanting(LinearLayout layout) {
        layout.addView(dh.createTextView("Enchanting\n", 26));
        layout.addView(dh.createTextView("Once an item is finished, its value can be greatly increased by putting a valuable gem inside.\n", 22));
        layout.addView(dh.createTextView("Use these wisely, as they are only available in limited quantities, and certain visitors will pay a very hefty bonus for items with their preferred gem in.\n", 22));
        layout.addView(dh.createTextView("As always, swipe left and right to change items, and use the up and down arrows to change tiers.\n", 22));
        layout.addView(dh.createTextView("Once the desired item is selected, tap the gem to be added.\n", 22));
    }

    public void displayHelpMarket(LinearLayout layout) {
        layout.addView(dh.createTextView("Market\n", 26));
        layout.addView(dh.createTextView("Raw resources (ore, some secondaries) are generally purchased from the market.\n", 22));
        layout.addView(dh.createTextView("Traders come and go, each with different prices and specialities. If you buy a lot of an item, you'll find the trader unable to resupply for a few hours.\n", 22));
        layout.addView(dh.createTextView("Compare prices between traders to ensure you're getting the best deal, but make sure to trade before they leave for the day.\n", 22));
        layout.addView(dh.createTextView("Scroll up and down to see the full list of traders.\n", 22));
    }

    public void displayHelpSettings(LinearLayout layout) {
        layout.addView(dh.createTextView("Settings\n", 26));
        layout.addView(dh.createTextView("Here, game settings such as music / sound and notifications can be enabled and disabled.\n", 22));
        layout.addView(dh.createTextView("Changes take place as soon as the settings interface is closed.\n", 22));
    }

    public void displayHelpTrader(LinearLayout layout) {
        layout.addView(dh.createTextView("Trader\n", 26));
        layout.addView(dh.createTextView("Traders will drift in and out of the marketplace throughout the day, with some offering steep discounts.\n", 22));
        layout.addView(dh.createTextView("They have a limited amount of stock, restocking happens every few hours (time until next restock is available on the statistics interface, or receive a notification via the settings interface).\n", 22));
    }

    public void displayHelpStatistics(LinearLayout layout) {
        layout.addView(dh.createTextView("Statistics\n", 26));
        layout.addView(dh.createTextView("Useful statistics such as time until next restock / visitor are available here.\n", 22));
        layout.addView(dh.createTextView("Additionally, progress towards various achievements can be tracked using the miscellaneous statistics displayed.\n", 22));
    }

    public void displayHelpTable(LinearLayout layout) {
        layout.addView(dh.createTextView("Table\n", 26));
        layout.addView(dh.createTextView("The table is an essential part of the item creating process, converting unfinished items into finished items, with the addition of secondary ingredients.\n", 22));
        layout.addView(dh.createTextView("After this, items can be optionally enchanted with gems at the enchanting table.\n", 22));
        layout.addView(dh.createTextView("Swipe left and right to change items. Use the up and down arrows to change tiers.\n", 22));
        layout.addView(dh.createTextView("Pressing 'Craft Max' will create as many of the selected item as possible with your current free slots and resources.\n", 22));
    }

    public void displayHelpTrade(LinearLayout layout) {
        layout.addView(dh.createTextView("Trade\n", 26));
        layout.addView(dh.createTextView("This screen is where you'll make all of your money!\n", 22));
        layout.addView(dh.createTextView("The discovered bonus is displayed next to the sell price of each item. It's entirely possible the item will sell for more than this, if all of the visitor's preferences have not yet been discovered.\n", 22));
        layout.addView(dh.createTextView("The progress bar will let you see your progress at a glance, and the item criteria and visitor are also visible.\n", 22));
        layout.addView(dh.createTextView("The finish button will close this criteria trade for now.\n", 22));
    }

    public void displayHelpTrophy(LinearLayout layout) {
        layout.addView(dh.createTextView("Trophy\n", 26));
        layout.addView(dh.createTextView("The trophy screen is where notes about all of the seen visitors can be looked at.\n", 22));
        layout.addView(dh.createTextView("Unseen visitors will have no information available about them.\n", 22));
        layout.addView(dh.createTextView("Seen visitors will have a lighter silhouette the more you see them. Additionally, basic information will be available.\n", 22));
        layout.addView(dh.createTextView("Once a visitor has been seen 100 times, they will provide you with a gift, and become fully visible.\n", 22));
    }

    public void displayHelpVisitor(LinearLayout layout) {
        layout.addView(dh.createTextView("Visitor\n", 26));
        layout.addView(dh.createTextView("This screen provides an overview of the currently selected visitor.\n", 22));
        layout.addView(dh.createTextView("If their preferred item type, tier, and state have been discovered, the associated bonus will be displayed.\n", 22));
        layout.addView(dh.createTextView("Additionally, the highest value trade with the visitor is displayed.\n", 22));
        layout.addView(dh.createTextView("In the list of demands, black denotes a required trade, whilst grey is optional.\n", 22));
        layout.addView(dh.createTextView("Once all required trades have been completed, the visitor can be completed.\n", 22));
        layout.addView(dh.createTextView("Alternatively, they can be shooed away for a fee. This fee is based on your current level, and the number of uncompleted demands (required + optional).\n", 22));
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