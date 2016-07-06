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
        dh.updateFullscreen(this);

        LinearLayout helpLayout = (LinearLayout) findViewById(R.id.helpLayout);
        helpLayout.removeAllViews();

        TOPICS topicIntent = (TOPICS) getIntent().getSerializableExtra(INTENT_ID);
        if (topicIntent != null) {
            displayHelp(helpLayout, topicIntent);
        } else {
            for (TOPICS topic : TOPICS.values()) {
                TextViewPixel topicView = dh.createTextView(topic.name().replace("_"," "), 34);
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
        } else if (topic == TOPICS.Tips_And_Tricks) {
            displayTips(layout);
        } else if (topic == TOPICS.Overview) {
            displayHelpOverview(layout);
        } else if (topic == TOPICS.Furnace) {
            displayHelpFurnace(layout);
        } else if (topic == TOPICS.Advertising) {
            displayHelpAdvertising(layout);
        } else if (topic == TOPICS.Anvil) {
            displayHelpAnvil(layout);
        } else if (topic == TOPICS.Inventory) {
            displayHelpInventory(layout);
        } else if (topic == TOPICS.Credits) {
            displayHelpCredits(layout);
        } else if (topic == TOPICS.Gem_Table) {
            displayHelpGemTable(layout);
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
        }else if (topic == TOPICS.Super_Upgrade) {
            displayHelpSuperUpgrade(layout);
        } else if (topic == TOPICS.Premium) {
            displayHelpPremium(layout);
        } else if (topic == TOPICS.Helper) {
            displayHelpHelper(layout);
        } else if (topic == TOPICS.Helper_Tools) {
            displayHelpTools(layout);
        } else if (topic == TOPICS.Helper_Food) {
            displayHelpFood(layout);
        } else if (topic == TOPICS.Quests) {
            displayHelpQuests(layout);
        } else if (topic == TOPICS.Item_Picker) {
            displayHelpItemPicker(layout);
        } else if (topic == TOPICS.Prestige) {
            displayHelpPrestige(layout);
        } else if (topic == TOPICS.Hero) {
            displayHelpHero(layout);
        } else if (topic == TOPICS.Hero_Adventures) {
            displayHelpHeroAdventures(layout);
        } else if (topic == TOPICS.Hero_Equipment) {
            displayHelpHeroEquipment(layout);
        } else if (topic == TOPICS.Hero_Visitors) {
            displayHelpHeroVisitors(layout);
        }
    }

    private void displayHelpPrestige(LinearLayout layout) {
        layout.addView(dh.createTextView("Prestige\n", 26));
        layout.addView(dh.createTextView("Prestiging resets many aspects of your game, and can only be performed at level 70 by premium players.\n", 22));
        layout.addView(dh.createTextView("Benefits:", 24));
        layout.addView(dh.createTextView("+50% coins bonus.\n-25% XP bonus.\n+100% to completion percentage.\nAchievement on first prestige.\n", 22));
        layout.addView(dh.createTextView("Reset:", 24));
        layout.addView(dh.createTextView("All items (except pages and books).\nAll upgrades (except premium benefits).\nAll slots.\nXP / Level.\nCurrent visitors.\nExtra trader stock unlocked.\nAll workers.\n", 22));
        layout.addView(dh.createTextView("Kept:", 24));
        layout.addView(dh.createTextView("Discovered visitor preferences.\nVisitor trophy progress.\nHighest level statistic.\nItems smelted / crafted / traded / bought / sold & gems embedded statistics.\nCoins earned statistic.\nBiggest trade statistic.\nHelper's trips statistic.\nQuests completed statistic.", 22));
    }

    private void displayTips(LinearLayout layout) {
        layout.addView(dh.createTextView("Tips And Tricks\n", 26));

        String[] tipArray = getResources().getStringArray(R.array.tipsArray);
        int i = 1;
        for (String tip : tipArray) {
            layout.addView(dh.createTextView(i + ": " + tip + "\n", 22));
            i++;
        }
    }

    private void displayHelpHelp(LinearLayout layout) {
        layout.addView(dh.createTextView("Help\n", 26));
        layout.addView(dh.createTextView("You want help for the help!?\n", 22));
        layout.addView(dh.createTextView("Get out of here, there's money to make!", 22));
    }

    private void displayHelpAdvertising(LinearLayout layout) {
        layout.addView(dh.createTextView("Advertising\n", 26));
        layout.addView(dh.createTextView("There are 2 types of advert in Pixel Blacksmith.\n", 22));
        layout.addView(dh.createTextView("The first is a 'convenience' one, e.g. skipping a timer, getting a free restock. The other type is the 'bonus' advert, accessible via the bonus chest.\n", 22));
        layout.addView(dh.createTextView("This chest can only be opened every few hours (premium members have a shorter timer).\n", 22));
        layout.addView(dh.createTextView("Premium members can also disable adverts. If this option is selected, all mention of adverts will be removed.", 22));
    }

    private void displayHelpOverview(LinearLayout layout) {
        layout.addView(dh.createTextView("Overview\n", 26));
        layout.addView(dh.createTextView("So, here's the deal: You're a blacksmith. A not very good one, to be honest.\n", 22));
        layout.addView(dh.createTextView("If you want to make a name for yourself, you're going to have to keep visitors happy, and keep an eye on your resources.\n", 22));
        layout.addView(dh.createTextView("Or, I guess you could ignore all the visitors and just work towards making the high end gear. But, y'know, don't do that.\n", 22));
        layout.addView(dh.createTextView("Generally, ores come from the market and other ingredients come from visitors and certain traders. You can also hire workers to gather resources for you.\n", 22));
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
        layout.addView(dh.createTextView("Whilst bars can be sold, they'll generally be a lot more valuable if they are first hammered into an unfinished item via the anvil, and finally finished at the crafting table.\n", 22));
        layout.addView(dh.createTextView("Food can also be created here. This can be sold to visitors, or given to workers to increase resources returned / increase the chance of pages.\n", 22));
        layout.addView(dh.createTextView("Swipe left and right to change items. Pressing 'Smelt 10' will add 10 bars to your smelting queue, if you have the resources.", 22));
    }

    private void displayHelpAnvil(LinearLayout layout) {
        layout.addView(dh.createTextView("Anvil\n", 26));
        layout.addView(dh.createTextView("After the furnace creates bars, the anvil must be used to hammer them into shape.\n", 22));
        layout.addView(dh.createTextView("Most recipes only require bars, with more valuable items requiring more bars.\n", 22));
        layout.addView(dh.createTextView("Of course, these unfinished items aren't quite done yet. They will still require combining with a secondary ingredient, which is done at the crafting table.\n", 22));
        layout.addView(dh.createTextView("Rings can also be crafted via the separate 'Rings' tab.\n", 22));
        layout.addView(dh.createTextView("Swipe left and right to change items. Use the up and down arrows to change tiers.\n", 22));
        layout.addView(dh.createTextView("Pressing '10' will add 10 of the item to your crafting queue, if you have the resources.", 22));
    }

    private void displayHelpInventory(LinearLayout layout) {
        layout.addView(dh.createTextView("Inventory\n", 26));
        layout.addView(dh.createTextView("All of your current stock can be viewed here.\n", 22));
        layout.addView(dh.createTextView("Most items can be sold for their basic value here, although this is not recommended. Note that any prestige / coins bonus will not apply here.\n", 22));
        layout.addView(dh.createTextView("Instead, try and create items that will sell for a large bonus with visitors.\n", 22));
        layout.addView(dh.createTextView("If a page has \"Exc\" next to it, it can be exchanged for a random page. This can be useful when trying to get the last few pages for a book.\n", 22));
        layout.addView(dh.createTextView("Scroll up and down to view all items.", 22));
    }

    private void displayHelpFood(LinearLayout layout) {
        layout.addView(dh.createTextView("Food Selection\n", 26));
        layout.addView(dh.createTextView("Helpers can be given food here.\n", 22));
        layout.addView(dh.createTextView("Each worker has a favourite food, and if it has been used in the past it will be highlighted here with green text and double the usual bonus.\n", 22));
        layout.addView(dh.createTextView("Trying different foods with helpers to find their favourite is a good way to maximise bonus resources.", 22));
    }

    private void displayHelpCredits(LinearLayout layout) {
        layout.addView(dh.createTextView("Credits\n", 26));
        layout.addView(dh.createTextView("Making a game is hard, but it's also extremely rewarding and educational.\n", 22));
        layout.addView(dh.createTextView("Development would be impossible without building on the work of others via open-source libraries, free resources, and places like StackOverflow.\n", 22));
        layout.addView(dh.createTextView("The credits contain a few of the larger contributions, but the helpfulness of the hundreds of articles / forums / Q&A sites visited during development can't be understated.\n", 22));
        layout.addView(dh.createTextView("Thanks, y'all!", 22));
    }

    private void displayHelpGemTable(LinearLayout layout) {
        layout.addView(dh.createTextView("Gem Table\n", 26));
        layout.addView(dh.createTextView("Once an item is finished, its value can be greatly increased by putting a valuable gem inside.\n", 22));
        layout.addView(dh.createTextView("Use these wisely, as they are only available in limited quantities, and certain visitors will pay a very hefty bonus for items with their preferred gem in.\n", 22));
        layout.addView(dh.createTextView("Gems can also be crushed into powders via the 'Powder' tab.\n", 22));
        layout.addView(dh.createTextView("As always, swipe left and right to change items, and use the up and down arrows to change tiers.\n", 22));
        layout.addView(dh.createTextView("Once the desired item is selected, tap the gem to be added.", 22));
    }

    private void displayHelpMarket(LinearLayout layout) {
        layout.addView(dh.createTextView("Market\n", 26));
        layout.addView(dh.createTextView("Raw resources (ore, some secondaries) are generally purchased from the market. Traders come and go, each with different specialities.\n", 22));
        layout.addView(dh.createTextView("Only traders with stock will be displayed, so buying all stock from more common traders is a good way to get rarer traders.\n", 22));
        layout.addView(dh.createTextView("If a lot of purchases are made from a trader, more items will unlock for sale.\n", 22));
        layout.addView(dh.createTextView("Scroll up and down to see the full list of traders.\n", 22));
        layout.addView(dh.createTextView("If all traders are out of stock, you'll have to wait for the market to restock, or pay a bribe / watch an advert to get them all to come back immediately.", 22));
    }

    private void displayHelpSettings(LinearLayout layout) {
        layout.addView(dh.createTextView("Settings\n", 26));
        layout.addView(dh.createTextView("Game settings can be changed here, changes take place as soon as the settings interface is closed.\n", 22));
        layout.addView(dh.createTextView("Sound Options:", 24));
        layout.addView(dh.createTextView("Here all game sounds, game music, and notification sounds can be enabled / disabled.\n", 22));
        layout.addView(dh.createTextView("Notification Options:", 24));
        layout.addView(dh.createTextView("Market restock, visitor spawn, worker return, and bonus chest refill notifications can be enabled / disabled.\n", 22));
        layout.addView(dh.createTextView("Gameplay Options:", 24));
        layout.addView(dh.createTextView("'Full Screen' enables a more immersive full screen mode.\n", 22));
        layout.addView(dh.createTextView("The 'Quick Select' functionality allows tapping the item image in a crafting interface to change item. Disabling 'Quick Select' will make swiping to change item easier.\n", 22));
        layout.addView(dh.createTextView("The 'Quick Log Access' option makes tapping a message open the message log instead of closing the current message.\n", 22));
        layout.addView(dh.createTextView("Performance Options:", 24));
        layout.addView(dh.createTextView("The 'Auto Refresh' option will enable auto refreshing on inventory and trade screens. This feature can cause performance issues, so is disabled by default.\n", 22));
        layout.addView(dh.createTextView("The 'Full Screen Check' option will enable check fullscreen status. If disabled, fullscreen mode will be less reliable, but performance will be improved.\n", 22));
        layout.addView(dh.createTextView("The 'Update Slots' option will enable updating item slots. Disabling it is not recommended, but can improve performance slightly.\n", 22));
        layout.addView(dh.createTextView("Premium Options:", 24));
        layout.addView(dh.createTextView("Premium players can disable all mention of adverts and prestige their account. Prestiging resets all items (except pages & books), XP, coins, upgrades, and trader progress, but keeps statistics and premium bonuses.\n", 22));
        layout.addView(dh.createTextView("In return for being set back to level 1, you'll receive +50% to coin earnings, and -25% to XP gains.\n", 22));
        layout.addView(dh.createTextView("Extras:", 24));
        layout.addView(dh.createTextView("The message log will show the last 100 important game messages.\n", 22));
        layout.addView(dh.createTextView("The tutorial button will replay the game tutorial, whilst the credits button will tell you a bit more about the people and technologies that contributed towards the game.\n", 22));
        layout.addView(dh.createTextView("The rate app button will link you to the game's Play Store listing to rate it, whilst the social media button provides links to Pixel Blacksmith online.\n", 22));
        layout.addView(dh.createTextView("If you've received a support code from customer support, it can also be entered here.\n", 22));
        layout.addView(dh.createTextView("Google Play:", 24));
        layout.addView(dh.createTextView("Google Play Games services can also be accessed here, including cloud saves, achievements, leaderboards, and quests.\n", 22));
        layout.addView(dh.createTextView("Settings Code:", 24));
        layout.addView(dh.createTextView("The settings code is an indicator of your current settings. Changing any setting will generate a new unique code. It can be used to help diagnose problems, or to quickly check two devices have the same settings.", 22));
        }

    private void displayHelpTrader(LinearLayout layout) {
        layout.addView(dh.createTextView("Trader\n", 26));
        layout.addView(dh.createTextView("Various traders will appear in the market, each offering different items. Making many purchases from a trader will usually unlock more items for sale.\n", 22));
        layout.addView(dh.createTextView("Many traders require a minimum level before they will appear, especially those selling higher level / rarer equipment.\n", 22));
        layout.addView(dh.createTextView("They each have a limited amount of stock and restocking happens every 24 hours. Time until next restock is available on the statistics interface, and a notification is also sent.\n", 22));
        layout.addView(dh.createTextView("A trader can also be restocked for a cost. The buy all button will also buy every item the trader is currently selling.", 22));
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
                "- Trophies unlocked\n" +
                "- Helpers purchased\n", 22));
        layout.addView(dh.createTextView("Note that every prestige (Level 70+) counts as an additional 100%.\n", 22));
        layout.addView(dh.createTextView("Total coin percentage is calculated as: Coins Upgrade + (0.5 * Prestige Level)), then converted from a multiplier into a percentage.", 22));
        layout.addView(dh.createTextView("Total XP percentage is calculated as: XP Upgrade * (0.75 ^ Prestige Level)), then converted from a multiplier into a percentage.", 22));
    }

    private void displayHelpTable(LinearLayout layout) {
        layout.addView(dh.createTextView("Table\n", 26));
        layout.addView(dh.createTextView("The table is an essential part of the item creating process, converting unfinished items into finished items, with the addition of secondary ingredients.\n", 22));
        layout.addView(dh.createTextView("After this, items can be optionally embedded with gems at the gem table.\n", 22));
        layout.addView(dh.createTextView("Additionally, rare pages can be crafted into books here. Crafting a book will require 10 pages, and many additional ingredients. Pages are obtained from a variety of sources:\n" +
                "- 3 pages are rewarded when a visitor trophy is unlocked.\n" +
                "- 35% chance to receive one when opening the bonus chest.\n" +
                "- 0-90% chance to receive one from a worker, depending on food used (see worker help for more info).\n", 22));
        layout.addView(dh.createTextView("Swipe left and right to change items. Use the up and down arrows to change tiers.\n", 22));
    }

    private void displayHelpTrading(LinearLayout layout) {
        layout.addView(dh.createTextView("Trade\n", 26));
        layout.addView(dh.createTextView("This screen is where you'll make all of your money!\n", 22));
        layout.addView(dh.createTextView("The discovered bonus is displayed next to the sell price of each item. It's entirely possible the item will sell for more than this, if all of the visitor's preferences have not yet been discovered.\n", 22));
        layout.addView(dh.createTextView("Undiscovered preferences will be displayed as \"???\".\n", 22));
        layout.addView(dh.createTextView("The progress bar will let you see your progress at a glance, and the item criteria and visitor are also visible.\n", 22));
        layout.addView(dh.createTextView("The finish button will close this trade for now.\n", 22));
        layout.addView(dh.createTextView("The 'Max' button will, when checked, let you trade as many of an item as possible at once.", 22));
    }

    private void displayHelpTrophy(LinearLayout layout) {
        layout.addView(dh.createTextView("Trophy\n", 26));
        layout.addView(dh.createTextView("The trophy screen is where notes about all of the seen visitors can be looked at.\n", 22));
        layout.addView(dh.createTextView("Unseen visitors will have no information available about them.\n", 22));
        layout.addView(dh.createTextView("Seen visitors will have a lighter silhouette the more you see them. Additionally, basic information will become available.\n", 22));
        layout.addView(dh.createTextView("Once a visitor has been seen 100 times, they will provide you with a gift, and become fully visible.\n", 22));
        layout.addView(dh.createTextView("The higher the 'Appear chance', the more likely a visitor is to arrive.", 22));
    }

    private void displayHelpVisitor(LinearLayout layout) {
        layout.addView(dh.createTextView("Visitor\n", 26));
        layout.addView(dh.createTextView("This screen provides an overview of the currently selected visitor.\n", 22));
        layout.addView(dh.createTextView("Once a visitor's preferred item type, tier, or state have been discovered through trading, this will be displayed underneath the visitor's picture. Also shown is how much extra a visitor will pay for these preferences. Combine all three to achieve the biggest profits!\n", 22));
        layout.addView(dh.createTextView("Item type: This is the item's category. For example dagger, sword, or hatchet.\n", 22));
        layout.addView(dh.createTextView("Item tier: This is the quality of the item's material. For example bronze, iron, or steel.\n", 22));
        layout.addView(dh.createTextView("Item state: This is the condition of the item. For example unfinished (shattered vial icon) or normal (vial icon). At higher levels, this can be an embedded gem such as red (ruby icon) or blue (sapphire icon).\n", 22));
        layout.addView(dh.createTextView("Additionally, the highest value item previously traded with the visitor is displayed.\n", 22));
        layout.addView(dh.createTextView("In the list of demands, black denotes a required trade, whilst grey is optional.\n", 22));
        layout.addView(dh.createTextView("Once all required trades have been completed, the visitor can be completed and you will receive a reward. If all of the visitor's optional demands have also been completed, you'll receive double the reward!\n", 22));
        layout.addView(dh.createTextView("Alternatively, they can be shooed away for a fee. This fee is based on your current level, how long the visitor has been waiting, and the number of incomplete demands (required + optional).", 22));
    }

    private void displayHelpUpgrade(LinearLayout layout) {
        layout.addView(dh.createTextView("Upgrade\n", 26));
        layout.addView(dh.createTextView("On this screen various upgrades can be bought, to help improve your shop.\n", 22));
        layout.addView(dh.createTextView("Craft Time\nUpgrading craft time will decrease the amount of time each item takes. This is calculated as item value * craft time.\n", 22));
        layout.addView(dh.createTextView("Coins Bonus\nUpgrading coins bonus will increase the % of bonus coins received on every trade / sale.\n", 22));
        layout.addView(dh.createTextView("Legendary Chance\n(Premium Only) Upgrading legendary chance will increase the likelihood of receiving legendary items as a reward from visitors.\n", 22));
        layout.addView(dh.createTextView("Market Restock Time\nUpgrading market restock time will decrease the hours between new traders entering the market.\n", 22));
        layout.addView(dh.createTextView("Maximum Traders\nUpgrading maximum traders will increase how many traders can be in the market at once.\n", 22));
        layout.addView(dh.createTextView("Maximum Visitor Rewards\nUpgrading maximum visitor rewards will increase the maximum number of items a visitor can give you.\n", 22));
        layout.addView(dh.createTextView("Maximum Visitors\nUpgrading maximum visitors will increase the maximum number of visitors in the shop at once.\n", 22));
        layout.addView(dh.createTextView("Minimum Visitor Rewards\nUpgrading minimum visitor rewards will increase the minimum number of items a visitor can give you.\n", 22));
        layout.addView(dh.createTextView("Restock All Cost\nUpgrading the restock all cost will reduce the cost of restocking the entire marketplace.\n", 22));
        layout.addView(dh.createTextView("Visitor Spawn Time\nUpgrading visitor spawn time will decrease the minutes between additional visitors appearing.\n", 22));
        layout.addView(dh.createTextView("Worker Time\nUpgrading worker time will decrease the time taken for a helper or hero to complete a trip.\n", 22));
        layout.addView(dh.createTextView("XP Bonus\nUpgrading XP bonus will increase the % of bonus XP received for every in-game action.\n", 22));
    }

    private void displayHelpPremium(LinearLayout layout) {
        layout.addView(dh.createTextView("Premium\n", 26));
        layout.addView(dh.createTextView("Buying premium provides a ton of new features and helps further development on the game!\n", 22));
        layout.addView(dh.createTextView("Once premium, you'll also be able to prestige, essentially starting a new game with +50% coin earnings and -25% XP gain.\n", 22));
        layout.addView(dh.createTextView("There's also extremely valuable legendary items, a reduced cooldown on the bonus chest, and many more features!\n", 22));
        layout.addView(dh.createTextView("Among the benefits is a tax paid by all out of stock traders when an automatic restock happens. Think of it as a reward for being a loyal customer!\n", 22));
        layout.addView(dh.createTextView("Note that premium status is applied to your account forever, and any other devices you install Pixel Blacksmith on will also be made premium.\n", 22));
        layout.addView(dh.createTextView("Contributions\n", 26));
        layout.addView(dh.createTextView("Contributing is a way to continue helping the games's development. Soon, contributing repeatedly will provide extra benefits such as suggesting new items and visitors!\n", 22));
    }

    private void displayHelpMessages(LinearLayout layout) {
        layout.addView(dh.createTextView("Messages\n", 26));
        layout.addView(dh.createTextView("The last 100 important messages are displayed here.", 22));
        layout.addView(dh.createTextView("Common messages, such as crafting beginning will not be displayed, but rarer ones like levelling up will. This way, if a message is missed, it can be reviewed here.", 22));
        layout.addView(dh.createTextView("By default, tapping a message will close it. The 'Quick Log Access' setting will change this to open the messages interface instead.", 22));
    }

    private void displayHelpHelper(LinearLayout layout) {
        layout.addView(dh.createTextView("Helper\n", 26));
        layout.addView(dh.createTextView("Helpers help gather resources. Every helper has a minimum level, and a hire cost based on that level.\n", 22));
        layout.addView(dh.createTextView("Once a helper has been hired, they will gather resources for you over time.\n", 22));
        layout.addView(dh.createTextView("The resources gained depends on the tool used. Higher tier tools will gather more / better resources.\n", 22));
        layout.addView(dh.createTextView("Providing a helper with food will provide bonus resources and provide a chance of finding a page.\n",22));
        layout.addView(dh.createTextView("Each helper has a favourite food, which will provide twice the normal bonus if provided. Food is consumed on each trip.\n", 22));
        layout.addView(dh.createTextView("If 'Auto-feed' is enabled, helpers (and heroes) will automatically restock themselves if the last used food is available.\n", 22));
        layout.addView(dh.createTextView("Tapping the 'Send Out Helpers' button will send out all available helpers / heroes.\n", 22));
        layout.addView(dh.createTextView("Tapping a purchased helper will tell you their name, total trips, and current food item.\n", 22));
        layout.addView(dh.createTextView("Tapping the food item will let you pick another. Discovered favourite food(s) are highlighted in green, and provide double food bonuses + page chance.\n", 22));
        layout.addView(dh.createTextView("Tapping the tool will let you to choose another, based on items you currently own.\n", 22));
        layout.addView(dh.createTextView("Tapping the resource indicator will provide information on the resources currently being gathered.\n", 22));
        layout.addView(dh.createTextView("The large button below the helper sends them out to gather resources. Additionally, tapping the button whilst the helper is busy will display the exact time until they return.", 22));
    }

    private void displayHelpHero(LinearLayout layout) {
        layout.addView(dh.createTextView("Hero\n", 26));
        layout.addView(dh.createTextView("Heroes are hireable workers who can be given equipment, and sent on adventures.\n", 22));
        layout.addView(dh.createTextView("The higher a hero's strength, the more likely they are to succeed.\n", 22));
        layout.addView(dh.createTextView("If hero strength = adventure difficulty, there is a 50% chance of success. If hero strength >= 2x adventure difficulty, success is guaranteed.\n", 22));
        layout.addView(dh.createTextView("If a hero fails an adventure, they will lose a random 1-5 pieces of equipment. As such, be careful not to send heroes on unwinnable adventures.\n", 22));
    }

    private void displayHelpTools(LinearLayout layout) {
        layout.addView(dh.createTextView("Tools\n", 26));
        layout.addView(dh.createTextView("Select a tool for a helper to use by first selecting a category, then an item.\n", 22));
        layout.addView(dh.createTextView("Only currently owned items will be displayed. Note that higher tier tools will provide better / more resources.\n", 22));
        layout.addView(dh.createTextView("Tools do not degrade, and can be reclaimed by providing the helper with a replacement tool.", 22));
    }

    private void displayHelpQuests(LinearLayout layout) {
        layout.addView(dh.createTextView("Quests\n", 26));
        layout.addView(dh.createTextView("Quests are an excellent way to gather additional rewards whilst playing.\n", 22));
        layout.addView(dh.createTextView("Once a quest is accepted, the relevant icon and current progress will appear in the indicator. If multiple are selected, the next to end will be displayed.\n", 22));
        layout.addView(dh.createTextView("A list of accepted / upcoming / completed / failed quests is available, as well as a schedule of all events.\n", 22));
        layout.addView(dh.createTextView("All quests reward XP, 0-2 pages, and 2-24 items / 50-2100 coins.\n", 22));
        layout.addView(dh.createTextView("XP is calculated by multiplier * current level. The values for the multiplier are: Easy 9, Medium 15, Hard 35, Elite 75.\n", 22));
        layout.addView(dh.createTextView("Page chance is 25% for Easy, 50% for Medium, 100% for Hard, and 200% for Elite (2 pages!).\n", 22));
        layout.addView(dh.createTextView("The number of items / coins rewarded is halved for Easy, unchanged for Medium, doubled for Hard, and tripled for Elite.\n", 22));
        layout.addView(dh.createTextView("Accepted quests (except contribution quests) will give a reminder notification (via Google Play Games) 24 hours before the end.", 22));
    }

    private void displayHelpItemPicker(LinearLayout layout) {
        layout.addView(dh.createTextView("Quick Select\n", 26));
        layout.addView(dh.createTextView("When the \"Quick Select\" setting is enabled, tapping an item will display a list of all items in the current tier.\n", 22));
        layout.addView(dh.createTextView("Tapping any item image / name will load it into the crafting interface, and this can often be faster than swiping to the area.\n", 22));
        layout.addView(dh.createTextView("When this option is enabled, swiping to change item can be trickier, so some players may wish to disable it via the settings menu for easier swiping.", 22));
        layout.addView(dh.createTextView("Additionally, the \"Show All Items\" toggle will switch between only displaying creatable items, and displaying all items.", 22));
    }

    private void displayHelpHeroAdventures(LinearLayout layout) {
        layout.addView(dh.createTextView("Adventures\n", 26));
        layout.addView(dh.createTextView("Adventures can be selected for your hero to go on.\n", 22));
        layout.addView(dh.createTextView("They are organised into categories and subcategories. Quests with a higher difficulty generally offer better rewards, but your hero is more likely to fail!\n", 22));
    }

    private void displayHelpHeroEquipment(LinearLayout layout) {
        layout.addView(dh.createTextView("Equipment\n", 26));
        layout.addView(dh.createTextView("Any item of the correct type can be given to a hero.\n", 22));
        layout.addView(dh.createTextView("However, taking notice of their preferences will ensure the most efficient tools are selected..\n", 22));
    }

    private void displayHelpHeroVisitors(LinearLayout layout) {
        layout.addView(dh.createTextView("Visitors\n", 26));
        layout.addView(dh.createTextView("A list of all visitors is here, along with progress towards unlocking them.\n", 22));
        layout.addView(dh.createTextView("Once the requirements have been met, they can be selected as a hero.\n", 22));
        layout.addView(dh.createTextView("Selecting heroes with high preference bonuses is a good way to increase total strength.\n", 22));
    }

    private void displayHelpSuperUpgrade(LinearLayout layout) {
        layout.addView(dh.createTextView("Super Upgrades\n", 26));
        layout.addView(dh.createTextView("Super upgrades are much more powerful upgrades that can only be used by players that have completed a collection.\n", 22));
        layout.addView(dh.createTextView("Collections can be created from books in the 'Books' tab of the crafting table.\n", 22));
        layout.addView(dh.createTextView("Prestiging will unlock more Super Upgrades, whilst completing collections will increase the maximum that can be enabled at once.\n", 22));
        layout.addView(dh.createTextView("'100x Contribution Reward' increases the 1337 coins usually received by contributing to a staggering 13,370 coins.\n", 22));
        layout.addView(dh.createTextView("'Free Market Restock' makes restocking the market free, perfect for emptying traders out!\n", 22));
        layout.addView(dh.createTextView("'2x Crafted Items' doubles the number of items received from every crafting process. Note that pages + books are excluded from this.\n", 22));
        layout.addView(dh.createTextView("'2x Worker Resources' doubles the resources received from Helper tasks and Hero adventures.\n", 22));
        layout.addView(dh.createTextView("'Guaranteed Pages' guarantees that workers with food, watching adverts, and quests will all reward pages.\n", 22));
        layout.addView(dh.createTextView("'-50% Bonus Chest Time' halves the time taken for the bonus chest to refill.\n", 22));
        layout.addView(dh.createTextView("'1 Demand Per Visitor' forces each visitor to only have 1 demand.\n", 22));
        layout.addView(dh.createTextView("'-50% Market Buy Cost' will halve the price of all items on the market.\n", 22));
        layout.addView(dh.createTextView("'-50% Worker Time' will have the time taken for a helper or hero to complete a task.\n", 22));
        layout.addView(dh.createTextView("'2x Trade Price' will double the money received from every trade.\n", 22));
        layout.addView(dh.createTextView("'2x Trader Items Purchased' will double all items purchased from a trader.\n", 22));
        layout.addView(dh.createTextView("'2x All XP' will double all XP (after other bonuses have been applied).\n", 22));
        layout.addView(dh.createTextView("'2x Coin Earnings' will double all coins earned (after other bonuses have been applied).\n", 22));
        layout.addView(dh.createTextView("'All Quests Medium / Hard / Elite' increase the minimum quest reward to the specified level.\n", 22));
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

    public enum TOPICS {Tips_And_Tricks, Advertising, Anvil, Credits, Gem_Table, Furnace, Help, Helper, Helper_Tools, Helper_Food, Hero, Hero_Adventures, Hero_Equipment, Hero_Visitors, Inventory, Item_Picker, Market, Messages, Overview, Premium, Prestige, Quests, Settings, Statistics, Super_Upgrade, Table, Trading, Trader, Trophy, Upgrade, Visitor}
}
