package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ErrorHelper;
import uk.co.jakelee.blacksmith.helper.SoundHelper;
import uk.co.jakelee.blacksmith.model.Criteria;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.State;
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
        displayDemandInfo();
        displayItemsTable();
    }

    public void displayVisitorInfo() {
        TextViewPixel visitorName = (TextViewPixel) findViewById(R.id.visitorName);
        visitorName.setText(visitorType.getName());
    }

    public void displayDemandInfo() {
        Criteria demandCriteria = Criteria.findById(Criteria.class, demand.getCriteriaType());

        TextViewPixel demandTextView = (TextViewPixel) findViewById(R.id.demandInfo);
        demandTextView.setText(demandCriteria.getName() + ": " + Visitor_Demand.getCriteriaName(demand) + " (" + demand.getQuantityProvided() + "/" + demand.getQuantity() + ")");

        ProgressBar demandProgress = (ProgressBar) findViewById(R.id.demandProgress);
        demandProgress.setMax(demand.getQuantity());
        demandProgress.setProgress(demand.getQuantityProvided());
    }

    public void displayItemsTable() {
        TableLayout itemsTable = (TableLayout) findViewById(R.id.itemsTable);
        itemsTable.removeAllViews();

        // Create header row
        TableRow headerRow = new TableRow(getApplicationContext());
        headerRow.addView(dh.createTextView("", 22, Color.BLACK));
        headerRow.addView(dh.createTextView("Item", 22, Color.BLACK));
        headerRow.addView(dh.createTextView("Sell", 22, Color.BLACK));
        headerRow.addView(dh.createTextView("", 22, Color.BLACK));
        itemsTable.addView(headerRow);

        List<Inventory> matchingItems = demand.getMatchingInventory();

        for(Inventory inventory : matchingItems) {
            TableRow itemRow = new TableRow(getApplicationContext());
            Item item = Item.findById(Item.class, inventory.getItem());

            ImageView image = dh.createItemImage(inventory.getItem(), 100, 100, Constants.TRUE);

            String itemName = item.getPrefix(inventory.getState()) + item.getName();
            TextViewPixel name = dh.createTextView(itemName, 20, Color.BLACK);
            name.setSingleLine(false);

            // Create a sell button for that item
            TextViewPixel sell = dh.createTextView(Integer.toString(item.getModifiedValue(inventory.getState())), 18, Color.BLACK);
            sell.setWidth(30);
            sell.setShadowLayer(10, 0, 0, Color.WHITE);
            sell.setGravity(Gravity.CENTER);
            sell.setBackgroundResource(R.drawable.sell_small);
            sell.setTag(R.id.itemID, item.getId());
            sell.setTag(R.id.itemState, inventory.getState());
            sell.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    clickSellButton(v);
                }
            });

            // Work out the multiplier that the player can see
            TextViewPixel bonusText = dh.createTextView("???", 18, Color.BLACK);
            double bonus = visitorType.getDisplayedBonus(inventory);
            if (bonus > Constants.DEFAULT_BONUS) {
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
        Item itemToSell = Item.findById(Item.class, (Long) v.getTag(R.id.itemID));
        State itemState = State.findById(State.class, (int) v.getTag(R.id.itemState));
        Inventory itemInventory = Select.from(Inventory.class).where(
                Condition.prop("item").eq(itemToSell.getId()),
                Condition.prop("state").eq(itemState.getId())).first();

        int quantity = 1;

        // Calculate the item sell value, rounded up
        double bonus = visitorType.getBonus(itemInventory);
        int value = (int) ((itemToSell.getModifiedValue(itemState.getId()) * bonus) + 0.5);

        int tradeResponse = Inventory.tradeItem(itemToSell.getId(), (int) v.getTag(R.id.itemState), quantity, value);
        if (tradeResponse == Constants.SUCCESS) {
            SoundHelper.playSound(this, SoundHelper.sellingSounds);
            Toast.makeText(getApplicationContext(), String.format("Sold %1sx %2s for%3s coin(s)", quantity, itemToSell.getName(), value), Toast.LENGTH_SHORT).show();
            demand.setQuantityProvided(demand.getQuantityProvided() + quantity);
            demand.save();
        } else {
            Toast.makeText(getApplicationContext(), ErrorHelper.errors.get(tradeResponse), Toast.LENGTH_SHORT).show();
        }

        dh.updateCoins(dh.getCoins());
        displayDemandInfo();
        visitorType.updateUnlockedPreferences(itemToSell, (int) v.getTag(R.id.itemState));
        visitorType.updateBestItem(itemToSell, (int) v.getTag(R.id.itemState), value);

        if (demand.isDemandFulfilled()) {
            TableLayout itemsTable = (TableLayout) findViewById(R.id.itemsTable);
            itemsTable.setVisibility(View.GONE);
        } else {
            displayItemsTable();
        }
    }

    public void closeTrade(View view) {
        finish();
    }

}
