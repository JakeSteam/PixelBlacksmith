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
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.helper.VisitorHelper;
import uk.co.jakelee.blacksmith.model.Criteria;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.State;
import uk.co.jakelee.blacksmith.model.Upgrade;
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
        demandTextView.setText(String.format(getString(R.string.demandText),
                demandCriteria.getName(),
                Visitor_Demand.getCriteriaName(demand),
                demand.getQuantityProvided(),
                demand.getQuantity()
        ));

        ProgressBar demandProgress = (ProgressBar) findViewById(R.id.demandProgress);
        demandProgress.setMax(demand.getQuantity());
        demandProgress.setProgress(demand.getQuantityProvided());
    }

    public void displayItemsTable() {
        TableLayout itemsTable = (TableLayout) findViewById(R.id.itemsTable);
        itemsTable.removeAllViews();

        // Create header row
        TableRow headerRow = new TableRow(getApplicationContext());
        headerRow.addView(dh.createTextView(getString(R.string.tableQuantity), 22, Color.BLACK));
        headerRow.addView(dh.createTextView(" ", 22, Color.BLACK));
        headerRow.addView(dh.createTextView(getString(R.string.tableItem), 22, Color.BLACK));
        headerRow.addView(dh.createTextView(getString(R.string.tableSold), 22, Color.BLACK));
        headerRow.addView(dh.createTextView(" ", 22, Color.BLACK));
        itemsTable.addView(headerRow);

        List<Inventory> matchingItems = demand.getMatchingInventory();

        for (Inventory inventory : matchingItems) {
            TableRow itemRow = new TableRow(getApplicationContext());

            Item item = Item.findById(Item.class, inventory.getItem());

            TextViewPixel quantity = dh.createTextView(String.valueOf(inventory.getQuantity()), 20);

            ImageView image = dh.createItemImage(inventory.getItem(), 30, 30, inventory.haveSeen());

            String itemName = item.getPrefix(inventory.getState()) + item.getName();
            TextViewPixel name = dh.createTextView(itemName, 20, Color.BLACK);
            name.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            name.setPadding(0, 0, 0, 17);
            name.setSingleLine(false);

            // Create a sell button for that item
            TextViewPixel sell = dh.createTextView(Integer.toString(item.getModifiedValue(inventory.getState())), 18, Color.BLACK);
            sell.setWidth(dh.convertDpToPixel(40));
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
            TextViewPixel bonusText = dh.createTextView(getString(R.string.unknownText), 18, Color.BLACK);
            double bonus = visitorType.getDisplayedBonus(inventory);
            if (bonus > Constants.DEFAULT_BONUS) {
                bonusText.setText(VisitorHelper.multiplierToPercent(bonus));
                bonusText.setTextColor(Color.GREEN);
            }

            itemRow.addView(quantity);
            itemRow.addView(image);
            itemRow.addView(name);
            itemRow.addView(sell);
            itemRow.addView(bonusText);
            itemsTable.addView(itemRow);
        }
    }

    public void clickSellButton(View v) {
        Item itemToSell = Item.findById(Item.class, (Long) v.getTag(R.id.itemID));
        State itemState = State.findById(State.class, (long) v.getTag(R.id.itemState));
        Inventory itemInventory = Select.from(Inventory.class).where(
                Condition.prop("item").eq(itemToSell.getId()),
                Condition.prop("state").eq(itemState.getId())).first();

        int quantity = 1;

        // Calculate the item sell value, rounded up
        double bonus = visitorType.getBonus(itemInventory);
        double coinMultiplier = VisitorHelper.percentToMultiplier(Upgrade.getValue("Gold Bonus")) * Player_Info.getPrestige();
        double modifiedBonus = coinMultiplier * bonus;

        int value = (int) (itemToSell.getModifiedValue(itemState.getId()) * modifiedBonus);

        int tradeResponse = Inventory.tradeItem(itemToSell.getId(), (long) v.getTag(R.id.itemState), quantity, value);
        if (tradeResponse == Constants.SUCCESS) {
            SoundHelper.playSound(this, SoundHelper.sellingSounds);
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, String.format(getString(R.string.soldItem), quantity, itemToSell.getName(), value));
            Player_Info.increaseByOne(Player_Info.Statistic.ItemsTraded);
            Player_Info.increaseByX(Player_Info.Statistic.CoinsEarned, value);
            demand.setQuantityProvided(demand.getQuantityProvided() + quantity);
            demand.save();
        } else {
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, ErrorHelper.errors.get(tradeResponse));
        }

        dh.updateCoins(Inventory.getCoins());
        displayDemandInfo();
        visitorType.updateUnlockedPreferences(itemToSell, (long) v.getTag(R.id.itemState));
        visitorType.updateBestItem(itemToSell, (long) v.getTag(R.id.itemState), value);

        if (demand.isDemandFulfilled()) {
            TableLayout itemsTable = (TableLayout) findViewById(R.id.itemsTable);
            itemsTable.setVisibility(View.GONE);
        } else {
            displayItemsTable();
        }
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Trading);
        startActivity(intent);
    }

    public void closeTrade(View view) {
        finish();
    }

}
