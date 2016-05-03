package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;

public class HelpActivity extends Activity {
    public static final String INTENT_ID = "uk.co.jakelee.blacksmith.helptoload";
    private static DisplayHelper dh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        dh = DisplayHelper.getInstance(getApplicationContext());

        LinearLayout helpLayout = (LinearLayout) findViewById(R.id.helpLayout);
        helpLayout.removeAllViews();

        TOPICS topicIntent = (TOPICS) getIntent().getSerializableExtra(INTENT_ID);
        if (topicIntent != null) {
            displayHelp(helpLayout, topicIntent);
        } else {
            for (TOPICS topic : TOPICS.values()) {
                TextViewPixel topicView = dh.createTextView(topic.name(), 34);
                int topicPadding = dh.convertDpToPixel(3);
                topicView.setPadding(topicPadding, topicPadding, topicPadding, topicPadding);
                topicView.setTag(topic);
                topicView.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        clickTopic(v);
                    }
                });

                helpLayout.addView(topicView);
            }
        }
    }

    private void clickTopic(View v) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, (TOPICS) v.getTag());
        startActivity(intent);
    }

    private void displayHelp(LinearLayout layout, TOPICS topic) {
        if (topic == TOPICS.Help) {
            displayHelpHelp(layout);
        } else if (topic == TOPICS.Overview) {
            displayHelpOverview(layout);
        } else if (topic == TOPICS.Furnace) {
            displayHelpFurnace(layout);
        } else if (topic == TOPICS.Anvil) {
            displayHelpAnvil(layout);
        } else if (topic == TOPICS.Inventory) {
            displayHelpInventory(layout);
        } else if (topic == TOPICS.Credits) {
            displayHelpCredits(layout);
        } else if (topic == TOPICS.Enchanting) {
            displayHelpEnchanting(layout);
        } else if (topic == TOPICS.Market) {
            displayHelpMarket(layout);
        } else if (topic == TOPICS.Messages) {
            displayHelpMessages(layout);
        } else if (topic == TOPICS.Settings) {
            displayHelpSettings(layout);
        } else if (topic == TOPICS.Trader) {
            displayHelpTrader(layout);
        } else if (topic == TOPICS.Statistics) {
            displayHelpStatistics(layout);
        } else if (topic == TOPICS.Table) {
            displayHelpTable(layout);
        } else if (topic == TOPICS.Trading) {
            displayHelpTrading(layout);
        } else if (topic == TOPICS.Trophy) {
            displayHelpTrophy(layout);
        } else if (topic == TOPICS.Visitor) {
            displayHelpVisitor(layout);
        } else if (topic == TOPICS.Upgrade) {
            displayHelpUpgrade(layout);
        } else if (topic == TOPICS.Premium) {
            displayHelpPremium(layout);
        }
    }

    private void displayHelpHelp(LinearLayout layout) {
        layout.addView(dh.createTextView("Help\n", 26));
        layout.addView(dh.createTextView("You want help for the help!?\n", 22));
        layout.addView(dh.createTextView("Get out of here, there's money to make!", 22));
    }

    private void displayHelpOverview(LinearLayout layout) {
        layout.addView(dh.createTextView("Overview\n", 26));
        layout.addView(dh.createTextView("So, here's the deal: You're a blacksmith. A not very good one, to be honest.\n", 22));
        layout.addView(dh.createTextView("If you want to make a name for yourself, you're going to have to keep visitors happy, and keep an eye on your resources.\n", 22));
        layout.addView(dh.createTextView("Or, I guess you could ignore all the visitors and just work towards making the high end gear. But, y'know, don't do that.\n", 22));
        layout.addView(dh.createTextView("Generally, ores come from the market and other ingredients come from visitors and certain traders.\n", 22));
        layout.addView(dh.createTextView("These ores are smelted in the furnace, crafted at the anvil, finished at the table, and embedded with gems at the gem table, before being sold to a visitor.\n", 22));
        layout.addView(dh.createTextView("Visitors, like the rest of us, have preferences. Once a visitor has been sold one of their favourite types / tiers / states of item, their preference and associated bonus will be saved.\n", 22));
        layout.addView(dh.createTextView("Next time they visit, selling them preferred items will provide a nice healthy bonus tip for yourself.\n", 22));
        layout.addView(dh.createTextView("Get stuck at any stage in the process? Press the help button!", 22));
    }

    private void displayHelpFurnace(LinearLayout layout) {
        layout.addView(dh.createTextView("Furnace\n", 26));
        layout.addView(dh.createTextView("The furnace is the starting point for creating items.\n", 22));
        layout.addView(dh.createTextView("Ore is generally bought from passing traders at the marketplace, but it can also be given as a reward by happy visitors.\n", 22));
        layout.addView(dh.createTextView("Ore you receive will have to be smelted into bars before any items can be created with it. Some bars will require a mixer, generally coal, to facilitate the creation of bars.\n", 22));
        layout.addView(dh.createTextView("Whilst bars can be sold, they'll generally be a lot more valuable if they are first hammered into an unfinished item via the anvil.\n", 22));
        layout.addView(dh.createTextView("Swipe left and right to change items. Pressing 'Smelt 10' will add 10 bars to your smelting queue, if you have the resources.", 22));
    }

    private void displayHelpAnvil(LinearLayout layout) {
        layout.addView(dh.createTextView("Anvil\n", 26));
        layout.addView(dh.createTextView("After the furnace creates bars, the anvil must be used to hammer them into shape.\n", 22));
        layout.addView(dh.createTextView("Most recipes only require bars, with more valuable / higher tier items requiring more bars.\n", 22));
        layout.addView(dh.createTextView("Of course, these unfinished items aren't quite done yet. They will still require an addition of a secondary ingredient, which is done at the crafting table.\n", 22));
        layout.addView(dh.createTextView("Swipe left and right to change items. Use the up and down arrows to change tiers.\n", 22));
        layout.addView(dh.createTextView("Pressing 'Craft 10' add 10 of the item to your crafting queue, if you have the resources.", 22));
    }

    private void displayHelpInventory(LinearLayout layout) {
        layout.addView(dh.createTextView("Inventory\n", 26));
        layout.addView(dh.createTextView("All of your current stock can be viewed here.\n", 22));
        layout.addView(dh.createTextView("Most items can be sold for their basic value here, although this is not recommended.\n", 22));
        layout.addView(dh.createTextView("Instead, try and create items that will sell for a large bonus with visitors.\n", 22));
        layout.addView(dh.createTextView("Scroll up and down to view all items.", 22));
    }

    private void displayHelpCredits(LinearLayout layout) {
        layout.addView(dh.createTextView("Credits\n", 26));
        layout.addView(dh.createTextView("Making a game is hard, but it's also extremely rewarding and educational.\n", 22));
        layout.addView(dh.createTextView("Development would be impossible without building on the work of others via open-source libraries, free resources, and places like StackOverflow.\n", 22));
        layout.addView(dh.createTextView("The credits contain a few of the larger contributions, but the helpfulness of the hundreds of articles / forums / Q&A sites visited during development can't be understated.\n", 22));
        layout.addView(dh.createTextView("Thanks, y'all!", 22));
    }

    private void displayHelpEnchanting(LinearLayout layout) {
        layout.addView(dh.createTextView("Gem Table\n", 26));
        layout.addView(dh.createTextView("Once an item is finished, its value can be greatly increased by putting a valuable gem inside.\n", 22));
        layout.addView(dh.createTextView("Use these wisely, as they are only available in limited quantities, and certain visitors will pay a very hefty bonus for items with their preferred gem in.\n", 22));
        layout.addView(dh.createTextView("As always, swipe left and right to change items, and use the up and down arrows to change tiers.\n", 22));
        layout.addView(dh.createTextView("Once the desired item is selected, tap the gem to be added.", 22));
    }

    private void displayHelpMarket(LinearLayout layout) {
        layout.addView(dh.createTextView("Market\n", 26));
        layout.addView(dh.createTextView("Raw resources (ore, some secondaries) are generally purchased from the market.\n", 22));
        layout.addView(dh.createTextView("Traders come and go, each with different prices and specialities. If you buy a lot of an item, you'll find the trader unable to resupply for a few hours.\n", 22));
        layout.addView(dh.createTextView("Compare prices between traders to ensure you're getting the best deal, but make sure to trade before the next restock.\n", 22));
        layout.addView(dh.createTextView("Only visitors with stock will be displayed, so buying all stock from more common traders is a good way to get rarer traders.\n", 22));
        layout.addView(dh.createTextView("If a lot of trades are conducted with a visitor, more items will unlock for sale.\n", 22));
        layout.addView(dh.createTextView("Scroll up and down to see the full list of traders.\n", 22));
        layout.addView(dh.createTextView("If all traders are out of stock, you'll have to wait for the market to restock, or pay a bribe to get them all to come back immediately.", 22));
    }

    private void displayHelpSettings(LinearLayout layout) {
        layout.addView(dh.createTextView("Settings\n", 26));
        layout.addView(dh.createTextView("Here, game settings such as music / sound and notifications can be enabled and disabled.\n", 22));
        layout.addView(dh.createTextView("Changes take place as soon as the settings interface is closed.", 22));
    }

    private void displayHelpTrader(LinearLayout layout) {
        layout.addView(dh.createTextView("Trader\n", 26));
        layout.addView(dh.createTextView("Traders will drift in and out of the marketplace throughout the day, with some offering steep discounts.\n", 22));
        layout.addView(dh.createTextView("They have a limited amount of stock, restocking happens every few hours (time until next restock is available on the statistics interface, or receive a notification via the settings interface).", 22));
    }

    private void displayHelpStatistics(LinearLayout layout) {
        layout.addView(dh.createTextView("Statistics\n", 26));
        layout.addView(dh.createTextView("Useful statistics such as time until next restock / visitor are available here.\n", 22));
        layout.addView(dh.createTextView("Additionally, progress towards various achievements can be tracked using the statistics displayed, as well as other miscellaneous pieces of information.\n", 22));
        layout.addView(dh.createTextView("Completion percentage is also visible here. Many factors contribute to this percentage:" +
                "- Level\n" +
                "- Upgrades\n" +
                "- Traders unlocked\n" +
                "- Trader stocks unlocked\n" +
                "- Slots unlocked\n" +
                "- Items seen\n" +
                "- Visitor preferences discovered\n" +
                "- Trophies unlocked\n", 22));
        layout.addView(dh.createTextView("Note that every prestige (Level 70+) counts as an additional 100%.", 22));
    }

    private void displayHelpTable(LinearLayout layout) {
        layout.addView(dh.createTextView("Table\n", 26));
        layout.addView(dh.createTextView("The table is an essential part of the item creating process, converting unfinished items into finished items, with the addition of secondary ingredients.\n", 22));
        layout.addView(dh.createTextView("After this, items can be optionally embedded with gems at the gem table.\n", 22));
        layout.addView(dh.createTextView("Swipe left and right to change items. Use the up and down arrows to change tiers.\n", 22));
        layout.addView(dh.createTextView("Pressing 'Craft 10' add 10 of the item to your crafting queue, if you have the resources.", 22));
    }

    private void displayHelpTrading(LinearLayout layout) {
        layout.addView(dh.createTextView("Trade\n", 26));
        layout.addView(dh.createTextView("This screen is where you'll make all of your money!\n", 22));
        layout.addView(dh.createTextView("The discovered bonus is displayed next to the sell price of each item. It's entirely possible the item will sell for more than this, if all of the visitor's preferences have not yet been discovered.\n", 22));
        layout.addView(dh.createTextView("The progress bar will let you see your progress at a glance, and the item criteria and visitor are also visible.\n", 22));
        layout.addView(dh.createTextView("The finish button will close this trade for now.\n", 22));
        layout.addView(dh.createTextView("The 'Max' button will, when checked, let you trade as many of an item as possible at once.", 22));
    }

    private void displayHelpTrophy(LinearLayout layout) {
        layout.addView(dh.createTextView("Trophy\n", 26));
        layout.addView(dh.createTextView("The trophy screen is where notes about all of the seen visitors can be looked at.\n", 22));
        layout.addView(dh.createTextView("Unseen visitors will have no information available about them.\n", 22));
        layout.addView(dh.createTextView("Seen visitors will have a lighter silhouette the more you see them. Additionally, basic information will become available.\n", 22));
        layout.addView(dh.createTextView("Once a visitor has been seen 100 times, they will provide you with a gift, and become fully visible.", 22));
    }

    private void displayHelpVisitor(LinearLayout layout) {
        layout.addView(dh.createTextView("Visitor\n", 26));
        layout.addView(dh.createTextView("This screen provides an overview of the currently selected visitor.\n", 22));
        layout.addView(dh.createTextView("If their preferred item type, tier, and state have been discovered, the associated bonus will be displayed.\n", 22));
        layout.addView(dh.createTextView("Item type: This is the item's category. For example dagger, sword, or hatchet.\n", 22));
        layout.addView(dh.createTextView("Item tier: This is the quality of the item's material. For example bronze, iron, or steel.\n", 22));
        layout.addView(dh.createTextView("Item state: This is the condition of the item. For example unfinished (shattered vial icon) or normal (vial icon). At higher levels, this can be an enchantment such as red (ruby icon) or blue (sapphire icon).\n", 22));
        layout.addView(dh.createTextView("Additionally, the highest value trade with the visitor is displayed.\n", 22));
        layout.addView(dh.createTextView("In the list of demands, black denotes a required trade, whilst grey is optional.\n", 22));
        layout.addView(dh.createTextView("Once all required trades have been completed, the visitor can be completed. If a visitor is fully completed, you'll received double the reward!\n", 22));
        layout.addView(dh.createTextView("Alternatively, they can be shooed away for a fee. This fee is based on your current level, and the number of incomplete demands (required + optional).", 22));
    }

    private void displayHelpUpgrade(LinearLayout layout) {
        layout.addView(dh.createTextView("Upgrade\n", 26));
        layout.addView(dh.createTextView("On this screen various upgrades can be bought, to help improve your shop.\n", 22));
        layout.addView(dh.createTextView("The XP and Gold bonuses will be more effective when dealing with more valuable items, and buying premium will increase the current and maximum values of most upgrades significantly.\n", 22));
        layout.addView(dh.createTextView("Visitor Spawn Time\nUpgrading visitor spawn time will decrease the minutes between additional visitors appearing.\n", 22));
        layout.addView(dh.createTextView("Market Restock Time\nUpgrading market restock time will decrease the hours between new traders entering the market.\n", 22));
        layout.addView(dh.createTextView("Maximum Visitors\nUpgrading maximum visitors will increase the maximum number of visitors in the shop at once.\n", 22));
        layout.addView(dh.createTextView("Maximum Traders\nUpgrading maximum traders will increase how many traders can be in the market at once.\n", 22));
        layout.addView(dh.createTextView("Gold Bonus\nUpgrading gold bonus will increase the % of bonus gold received on every trade / sale.\n", 22));
        layout.addView(dh.createTextView("XP Bonus\nUpgrading XP bonus will increase the % of bonus XP received for every in-game action.\n", 22));
        layout.addView(dh.createTextView("Craft Time\nUpgrading craft time will decrease the amount of time each item takes. This is calculated as item value * craft time.\n", 22));
        layout.addView(dh.createTextView("Legendary Chance\n(Premium Only) Upgrading legendary chance will increase the likelihood of receiving legendary items as a reward from visitors.\n", 22));
        layout.addView(dh.createTextView("Restock All Cost\nUpgrading the restock all cost will reduce the cost of restocking the entire marketplace.", 22));
    }

    private void displayHelpPremium(LinearLayout layout) {
        layout.addView(dh.createTextView("Premium\n", 26));
        layout.addView(dh.createTextView("Buying premium provides a ton of new features, and also helps further development on the game!\n", 22));
        layout.addView(dh.createTextView("Note that premium status is applied to your account forever, and any other devices you install Pixel Blacksmith on will also be made premium.", 22));
    }

    private void displayHelpMessages(LinearLayout layout) {
        layout.addView(dh.createTextView("Messages\n", 26));
        layout.addView(dh.createTextView("The last 100 messages are displayed here. This includes success messages, failure messages, level ups, etc.", 22));
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, TOPICS.Help);
        startActivity(intent);
        this.finish();
    }

    public void closePopup(View view) {
        finish();
    }

    public enum TOPICS {Anvil, Credits, Enchanting, Furnace, Help, Inventory, Market, Messages, Overview, Premium, Settings, Statistics, Table, Trading, Trader, Trophy, Upgrade, Visitor}
}
