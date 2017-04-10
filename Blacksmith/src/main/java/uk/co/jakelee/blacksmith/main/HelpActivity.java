package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.TextHelper;

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
        switch (topic) {
            case Advertising: displayHelpAdvertising(layout); break;
            case Anvil: displayHelpAnvil(layout); break;
            case Coins: displayHelpCoins(layout); break;
            case Credits: displayHelpCredits(layout); break;
            case Furnace: displayHelpFurnace(layout); break;
            case Gem_Table: displayHelpGemTable(layout); break;
            case Help: displayHelpHelp(layout); break;
            case Helper: displayHelpHelper(layout); break;
            case Helper_Food: displayHelpFood(layout); break;
            case Helper_Tools: displayHelpTools(layout); break;
            case Hero: displayHelpHero(layout); break;
            case Hero_Adventures: displayHelpHeroAdventures(layout); break;
            case Hero_Equipment: displayHelpHeroEquipment(layout); break;
            case Hero_Visitors: displayHelpHeroVisitors(layout); break;
            case Inventory: displayHelpInventory(layout); break;
            case Item_Picker: displayHelpItemPicker(layout); break;
            case Market: displayHelpMarket(layout); break;
            case Messages: displayHelpMessages(layout); break;
            case Overview: displayHelpOverview(layout); break;
            case Assistants: displayHelpAssistants(layout); break;
            case Premium: displayHelpPremium(layout); break;
            case Prestige: displayHelpPrestige(layout); break;
            case Quests: displayHelpQuests(layout); break;
            case Settings: displayHelpSettings(layout); break;
            case Statistics: displayHelpStatistics(layout); break;
            case Super_Upgrade: displayHelpSuperUpgrade(layout); break;
            case Table: displayHelpTable(layout); break;
            case Tips_And_Tricks: displayTips(layout); break;
            case Trader: displayHelpTrader(layout); break;
            case Trading: displayHelpTrading(layout); break;
            case Trophy: displayHelpTrophy(layout); break;
            case Upgrade: displayHelpUpgrade(layout); break;
            case Visitor: displayHelpVisitor(layout); break;
        }
    }

    private void displayHelpAssistants(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_assistants), 26));
        layout.addView(dh.createTextView(getString(R.string.help_assistants_text), 22));
    }

    private void displayHelpCoins(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_coins), 26));
        layout.addView(dh.createTextView(getString(R.string.help_coins_text), 22));
    }

    private void displayHelpPrestige(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_prestige), 26));
        layout.addView(dh.createTextView(getString(R.string.help_prestige_text), 22));
    }

    private void displayTips(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_tips), 26));

        List<String> tipArray = TextHelper.getTips(this);
        int i = 1;
        for (String tip : tipArray) {
            layout.addView(dh.createTextView(i + ": " + tip + "\n", 22));
            i++;
        }
    }

    private void displayHelpHelp(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_help), 26));
        layout.addView(dh.createTextView(getString(R.string.help_help_text), 22));
    }

    private void displayHelpAdvertising(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_advertising), 26));
        layout.addView(dh.createTextView(getString(R.string.help_advertising_text), 22));
    }

    private void displayHelpOverview(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_overview), 26));
        layout.addView(dh.createTextView(getString(R.string.help_overview_text), 22));
    }

    private void displayHelpFurnace(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_furnace), 26));
        layout.addView(dh.createTextView(getString(R.string.help_furnace_text), 22));
    }

    private void displayHelpAnvil(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_anvil), 26));
        layout.addView(dh.createTextView(getString(R.string.help_anvil_text), 22));
    }

    private void displayHelpInventory(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_inventory), 26));
        layout.addView(dh.createTextView(getString(R.string.help_inventory_text), 22));
    }

    private void displayHelpFood(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_food), 26));
        layout.addView(dh.createTextView(getString(R.string.help_food_text), 22));
    }

    private void displayHelpCredits(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_credits), 26));
        layout.addView(dh.createTextView(getString(R.string.help_credits_text), 22));
    }

    private void displayHelpGemTable(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_gem), 26));
        layout.addView(dh.createTextView(getString(R.string.help_gem_text), 22));
    }

    private void displayHelpMarket(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_market), 26));
        layout.addView(dh.createTextView(getString(R.string.help_market_text), 22));
    }

    private void displayHelpSettings(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_settings), 26));
        layout.addView(dh.createTextView(getString(R.string.help_settings_text), 22));
        }

    private void displayHelpTrader(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_trader), 26));
        layout.addView(dh.createTextView(getString(R.string.help_trader_text), 22));
    }

    private void displayHelpStatistics(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_statistics), 26));
        layout.addView(dh.createTextView(getString(R.string.help_statistics_text), 22));
    }

    private void displayHelpTable(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_table), 26));
        layout.addView(dh.createTextView(getString(R.string.help_table_text), 22));
    }

    private void displayHelpTrading(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_trade), 26));
        layout.addView(dh.createTextView(getString(R.string.help_trade_text), 22));
    }

    private void displayHelpTrophy(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_trophy), 26));
        layout.addView(dh.createTextView(getString(R.string.help_trophy_text), 22));
    }

    private void displayHelpVisitor(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_visitor), 26));
        layout.addView(dh.createTextView(getString(R.string.help_visitor_text), 22));
    }

    private void displayHelpUpgrade(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_upgrade), 26));
        layout.addView(dh.createTextView(getString(R.string.help_upgrade_text), 22));
    }

    private void displayHelpPremium(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_premium), 26));
        layout.addView(dh.createTextView(getString(R.string.help_premium_text), 22));
    }

    private void displayHelpMessages(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_messages), 26));
        layout.addView(dh.createTextView(getString(R.string.help_messages_text), 22));
    }

    private void displayHelpHelper(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_helper), 26));
        layout.addView(dh.createTextView(getString(R.string.help_helper_text), 22));
    }

    private void displayHelpHero(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_hero), 26));
        layout.addView(dh.createTextView(getString(R.string.help_hero_text), 22));
    }

    private void displayHelpTools(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_tools), 26));
        layout.addView(dh.createTextView(getString(R.string.help_tools_text), 22));
    }

    private void displayHelpQuests(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_quest), 26));
        layout.addView(dh.createTextView(getString(R.string.help_quest_text), 22));
    }

    private void displayHelpItemPicker(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_item_picker), 26));
        layout.addView(dh.createTextView(getString(R.string.help_item_picker_text), 22));
    }

    private void displayHelpHeroAdventures(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_hero_adventures), 26));
        layout.addView(dh.createTextView(getString(R.string.help_hero_adventures_text), 22));
    }

    private void displayHelpHeroEquipment(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_hero_equipment), 26));
        layout.addView(dh.createTextView(getString(R.string.help_hero_equipment_text), 22));
    }

    private void displayHelpHeroVisitors(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_hero_visitors), 26));
        layout.addView(dh.createTextView(getString(R.string.help_hero_visitors_text), 22));
    }

    private void displayHelpSuperUpgrade(LinearLayout layout) {
        layout.addView(dh.createTextView(getString(R.string.help_super_upgrade), 26));
        layout.addView(dh.createTextView(getString(R.string.help_super_upgrade_text), 22));
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

    public enum TOPICS {Tips_And_Tricks, Advertising, Anvil, Assistants, Coins, Credits, Gem_Table, Furnace, Help, Helper, Helper_Tools, Helper_Food, Hero, Hero_Adventures, Hero_Equipment, Hero_Visitors, Inventory, Item_Picker, Market, Messages, Overview, Premium, Prestige, Quests, Settings, Statistics, Super_Upgrade, Table, Trading, Trader, Trophy, Upgrade, Visitor}
}
