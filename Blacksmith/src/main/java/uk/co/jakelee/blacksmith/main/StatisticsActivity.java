package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.orm.query.Condition;
import com.orm.query.Select;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Upgrade;
import uk.co.jakelee.blacksmith.model.Visitor_Stats;

public class StatisticsActivity extends Activity {
    public static DisplayHelper dh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        dh = DisplayHelper.getInstance(getApplicationContext());

        displayStatistics();
    }

    public void displayStatistics() {
        int currentXP = Player_Info.getXp();
        ((TextViewPixel) findViewById(R.id.currentXP)).setText(String.format("%,d", currentXP));

        int nextLevelXP = Player_Info.convertLevelToXp(Player_Info.getPlayerLevel() + 1);
        int nextLevelIn = nextLevelXP - Player_Info.getXp();
        ((TextViewPixel) findViewById(R.id.nextLevelIn)).setText(String.format("%,d", nextLevelIn));

        int itemsSmelted = Select.from(Player_Info.class).where(Condition.prop("name").eq("ItemsSmelted")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.itemsSmelted)).setText(String.format("%,d", itemsSmelted));

        int itemsCrafted = Select.from(Player_Info.class).where(Condition.prop("name").eq("ItemsCrafted")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.itemsCrafted)).setText(String.format("%,d", itemsCrafted));

        int itemsTraded = Select.from(Player_Info.class).where(Condition.prop("name").eq("ItemsTraded")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.itemsTraded)).setText(String.format("%,d", itemsTraded));

        int itemsBought = Select.from(Player_Info.class).where(Condition.prop("name").eq("ItemsBought")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.itemsBought)).setText(String.format("%,d", itemsBought));

        int itemsSold = Select.from(Player_Info.class).where(Condition.prop("name").eq("ItemsSold")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.itemsSold)).setText(String.format("%,d", itemsSold));

        int itemsEnchanted = Select.from(Player_Info.class).where(Condition.prop("name").eq("ItemsEnchanted")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.itemsEnchanted)).setText(String.format("%,d", itemsEnchanted));

        int visitorsCompleted = Select.from(Player_Info.class).where(Condition.prop("name").eq("VisitorsCompleted")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.visitorsCompleted)).setText(String.format("%,d", visitorsCompleted));

        int coinsEarned = Select.from(Player_Info.class).where(Condition.prop("name").eq("CoinsEarned")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.coinsEarned)).setText(String.format("%,d", coinsEarned));

        long unixRestocked = Select.from(Player_Info.class).where(Condition.prop("name").eq("DateRestocked")).first().getLongValue();
        long unixNextRestock = unixRestocked + DateHelper.hoursToMilliseconds(Upgrade.getValue("Shop Restock Time"));
        long unixRestockDifference = unixNextRestock - System.currentTimeMillis();
        ((TextViewPixel) findViewById(R.id.nextRestock)).setText(DateHelper.getHoursMinsRemaining(unixRestockDifference));

        long unixSpawned = Select.from(Player_Info.class).where(Condition.prop("name").eq("DateVisitorSpawned")).first().getLongValue();
        long unixNextSpawn = unixSpawned + DateHelper.minutesToMilliseconds(Upgrade.getValue("Visitor Spawn Time"));
        long unixVisitorDifference = unixNextSpawn - System.currentTimeMillis();
        if (unixVisitorDifference > 0) {
            ((TextViewPixel) findViewById(R.id.nextVisitorCheck)).setText(DateHelper.getMinsSecsRemaining(unixVisitorDifference));
        } else {
            ((TextViewPixel) findViewById(R.id.nextVisitorCheck)).setText(getString(R.string.visitorsFullRestockTime));
        }

        Long unixRestarted = Select.from(Player_Info.class).where(Condition.prop("name").eq("DateStarted")).first().getLongValue();
        String dateRestarted = DateHelper.displayTime(unixRestarted, DateHelper.date);
        ((TextViewPixel) findViewById(R.id.dateStarted)).setText(dateRestarted);

        int biggestTrade = Select.from(Visitor_Stats.class).orderBy("best_item_value DESC").first().getBestItemValue();
        ((TextViewPixel) findViewById(R.id.biggestTrade)).setText(String.format("%,d", biggestTrade));

        int upgradesBought = Select.from(Player_Info.class).where(Condition.prop("name").eq("UpgradesBought")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.upgradesBought)).setText(String.format("%,d", upgradesBought));

    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Statistics);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
