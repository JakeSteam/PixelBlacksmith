package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.orm.query.Condition;
import com.orm.query.Select;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.model.Player_Info;
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
        int itemsSmelted = Select.from(Player_Info.class).where(Condition.prop("name").eq("ItemsSmelted")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.itemsSmelted)).setText(Integer.toString(itemsSmelted));

        int itemsCrafted = Select.from(Player_Info.class).where(Condition.prop("name").eq("ItemsCrafted")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.itemsCrafted)).setText(Integer.toString(itemsCrafted));

        int itemsTraded = Select.from(Player_Info.class).where(Condition.prop("name").eq("ItemsTraded")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.itemsTraded)).setText(Integer.toString(itemsTraded));

        int itemsSold = Select.from(Player_Info.class).where(Condition.prop("name").eq("ItemsSold")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.itemsSold)).setText(Integer.toString(itemsSold));

        int visitorsCompleted = Select.from(Player_Info.class).where(Condition.prop("name").eq("VisitorsCompleted")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.visitorsCompleted)).setText(Integer.toString(visitorsCompleted));

        int coinsEarned = Select.from(Player_Info.class).where(Condition.prop("name").eq("CoinsEarned")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.coinsEarned)).setText(Integer.toString(coinsEarned));

        Long unixRestocked = Select.from(Player_Info.class).where(Condition.prop("name").eq("DateRestocked")).first().getLongValue();
        String dateRestocked = DateHelper.displayTime(unixRestocked, DateHelper.time);
        ((TextViewPixel) findViewById(R.id.dateRestocked)).setText(dateRestocked);

        Long unixRestarted = Select.from(Player_Info.class).where(Condition.prop("name").eq("DateStarted")).first().getLongValue();
        String dateRestarted = DateHelper.displayTime(unixRestarted, DateHelper.date);
        ((TextViewPixel) findViewById(R.id.dateStarted)).setText(dateRestarted);

        int biggestTrade = Select.from(Visitor_Stats.class).orderBy("best_item_value DESC").first().getBestItemValue();
        ((TextViewPixel) findViewById(R.id.biggestTrade)).setText(Integer.toString(biggestTrade));

    }

    public void closePopup(View view) {
        finish();
    }
}
