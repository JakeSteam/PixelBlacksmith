package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Visitor;
import uk.co.jakelee.blacksmith.model.Visitor_Demand;
import uk.co.jakelee.blacksmith.model.Visitor_Stats;
import uk.co.jakelee.blacksmith.model.Visitor_Type;

public class TradeActivity extends Activity {
    public static Visitor_Demand demand;
    public static Visitor visitor;
    public static Visitor_Type visitorType;
    public static Visitor_Stats visitorStats;
    public static DisplayHelper dh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);
        dh = DisplayHelper.getInstance(getApplicationContext());

        Intent intent = getIntent();
        int demandId = Integer.parseInt(intent.getStringExtra(DisplayHelper.DEMAND_TO_LOAD));

        if (demandId > 0) {
            demand = Visitor_Demand.findById(Visitor_Demand.class, demandId);
            visitor = Visitor.findById(Visitor.class, demand.getVisitorID());
            visitorType = Visitor_Type.findById(Visitor_Type.class, visitor.getType());
            visitorStats = Visitor_Stats.findById(Visitor_Stats.class, visitor.getType());
            createTradeInterface();
        }
    }

    public void createTradeInterface() {
        displayVisitorInfo();
        displayItemsTable();
    }

    public void displayVisitorInfo() {
        int drawableId = getApplicationContext().getResources().getIdentifier("visitor" + visitorType.getVisitorID(), "drawable", getApplicationContext().getPackageName());
        ImageView visitorPicture = (ImageView) findViewById(R.id.visitorPicture);
        visitorPicture.setImageResource(drawableId);

        TextView visitorName = (TextView) findViewById(R.id.visitorName);
        visitorName.setText(visitorType.getName());

        TextView visitorDesc = (TextView) findViewById(R.id.visitorDesc);
        visitorDesc.setText(visitorType.getDesc());

        TextView visitorVisits = (TextView) findViewById(R.id.visitorVisits);
        visitorVisits.setText("Visits: " + Integer.toString(visitorStats.getVisits()));
    }

    public void displayItemsTable() {
        TableLayout itemsTable = (TableLayout) findViewById(R.id.itemsTable);

        // Create header row
        TableRow headerRow = new TableRow(getApplicationContext());
        headerRow.addView(dh.createTextView("", 18, Color.BLACK));
        headerRow.addView(dh.createTextView("Item", 18, Color.BLACK));
        headerRow.addView(dh.createTextView("Sell", 18, Color.BLACK));
        headerRow.addView(dh.createTextView("", 18, Color.BLACK));
        itemsTable.addView(headerRow);

        List<Inventory> matchingItems = demand.getMatchingInventory();

        for(Inventory inventory : matchingItems) {
            TableRow itemRow = new TableRow(getApplicationContext());
            Item item = Item.findById(Item.class, inventory.getItem());

            ImageView image = dh.createItemImage(inventory.getItem(), 100, 100, 1);

            String itemName = item.getName();
            if (inventory.getState() == 2) {
                itemName = "(unf) " + itemName;
            }
            TextView name = dh.createTextView(itemName, 15, Color.BLACK);
            name.setSingleLine(false);

            // Create a sell button for that item
            TextView sell = dh.createTextView(Integer.toString(item.getValue()), 18, Color.BLACK);
            sell.setWidth(30);
            sell.setShadowLayer(10, 0, 0, Color.WHITE);
            sell.setGravity(Gravity.CENTER);
            sell.setBackgroundResource(R.drawable.sell);
            sell.setTag(item.getId());
            sell.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    clickSellButton(v);
                }
            });

            // Work out the multiplier that the player can see
            TextView bonusText = dh.createTextView("???", 15, Color.BLACK);
            double bonus = visitorType.getDisplayedBonus(inventory);
            if (bonus > 1) {
                bonusText.setText(Double.toString(bonus) + "x");
                bonusText.setTextColor(Color.GREEN);
            }

            itemRow.addView(image);
            itemRow.addView(name);
            itemRow.addView(sell);
            itemRow.addView(bonusText);
            itemsTable.addView(itemRow);
        }
    }

    public void clickSellButton(View v) {

    }

    public void closeTrade(View view) {
        finish();
    }

}
