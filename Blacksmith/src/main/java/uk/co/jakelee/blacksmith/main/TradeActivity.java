package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import uk.co.jakelee.blacksmith.helper.TutorialHelper;
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
    private static Visitor_Demand demand;
    private static Visitor visitor;
    private static Visitor_Type visitorType;
    private static Visitor_Stats visitorStats;
    private static DisplayHelper dh;
    private static SharedPreferences prefs;
    private static boolean tradeMax = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);
        dh = DisplayHelper.getInstance(getApplicationContext());
        prefs = getSharedPreferences("uk.co.jakelee.blacksmith", MODE_PRIVATE);

        Intent intent = getIntent();
        int demandId = Integer.parseInt(intent.getStringExtra(DisplayHelper.DEMAND_TO_LOAD));

        if (demandId > 0) {
            demand = Visitor_Demand.findById(Visitor_Demand.class, demandId);
            visitor = Visitor.findById(Visitor.class, demand.getVisitorID());
            visitorType = Visitor_Type.findById(Visitor_Type.class, visitor.getType());
            visitorStats = Visitor_Stats.findById(Visitor_Stats.class, visitor.getType());
            createTradeInterface();
        }

        if (TutorialHelper.currentlyInTutorial && TutorialHelper.currentStage <= Constants.STAGE_3_TRADE) {
            startTutorial();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (TutorialHelper.currentlyInTutorial) {
            TutorialHelper.chainTourGuide.next();
        }
    }

    private void startTutorial() {
        TutorialHelper th = new TutorialHelper(Constants.STAGE_3_TRADE);
        th.addTutorial(this, findViewById(R.id.itemsTable), R.string.tutorialTradeItems, R.string.tutorialTradeItemsText, true);
        th.addTutorial(this, findViewById(R.id.finishTrade), R.string.tutorialTradeFinish, R.string.tutorialTradeFinishText, true, Gravity.TOP);
        th.start(this);
    }

    private void createTradeInterface() {
        displayVisitorInfo();
        displayDemandInfo();
        displayItemsTable();
        updateMax();
    }

    private void displayVisitorInfo() {
        TextViewPixel visitorName = (TextViewPixel) findViewById(R.id.visitorName);
        visitorName.setText(visitorType.getName());
    }

    private void displayDemandInfo() {
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

    private void displayItemsTable() {
        TableLayout itemsTable = (TableLayout) findViewById(R.id.itemsTable);
        itemsTable.removeAllViews();

        // Create header row
        TableRow headerRow = new TableRow(getApplicationContext());
        headerRow.addView(dh.createTextView(getString(R.string.tableQuantity), 22, Color.BLACK));
        headerRow.addView(dh.createTextView(" ", 22, Color.BLACK));
        headerRow.addView(dh.createTextView(getString(R.string.tableItem), 22, Color.BLACK));
        headerRow.addView(dh.createTextView(getString(R.string.tableSell), 22, Color.BLACK));
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
                bonusText.setTextColor(Color.parseColor("#267c18"));
            }

            itemRow.addView(quantity);
            itemRow.addView(image);
            itemRow.addView(name);
            itemRow.addView(sell);
            itemRow.addView(bonusText);
            itemsTable.addView(itemRow);
        }
    }

    private void clickSellButton(View v) {
        Item itemToSell = Item.findById(Item.class, (Long) v.getTag(R.id.itemID));
        State itemState = State.findById(State.class, (long) v.getTag(R.id.itemState));

        int quantity = 1;
        if (prefs.getBoolean("tradeMax", false)) {
            quantity = demand.getQuantity() - demand.getQuantityProvided();
        }
        tradeItem(quantity, itemToSell, itemState);
    }

    private void tradeItem(int quantity, Item itemToSell, State itemState) {
        Inventory itemInventory = Select.from(Inventory.class).where(
                Condition.prop("item").eq(itemToSell.getId()),
                Condition.prop("state").eq(itemState.getId())).first();

        // Calculate the item sell value, rounded up
        double bonus = visitorType.getBonus(itemInventory);
        double coinMultiplier = VisitorHelper.percentToMultiplier(Upgrade.getValue("Gold Bonus")) * (Player_Info.getPrestige() + 1);
        double modifiedBonus = coinMultiplier * bonus;
        int value = (int) (itemToSell.getModifiedValue(itemState.getId()) * modifiedBonus);

        int itemsTraded = 0;
        int tradeResponse = Constants.ERROR_NOT_ENOUGH_ITEMS;
        boolean successful = true;

        while (successful && itemsTraded < quantity) {
            tradeResponse = Inventory.tradeItem(itemToSell.getId(), itemState.getId(), value);
            if (tradeResponse == Constants.SUCCESS) {
                itemsTraded++;
            } else {
                successful = false;
            }
        }

        if (itemsTraded > 0) {
            SoundHelper.playSound(this, SoundHelper.sellingSounds);
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, String.format(getString(R.string.tradedItem),
                    itemsTraded,
                    itemToSell.getName(),
                    value * itemsTraded), false);
            Player_Info.increaseByX(Player_Info.Statistic.ItemsTraded, itemsTraded);
            Player_Info.increaseByX(Player_Info.Statistic.CoinsEarned, value * itemsTraded);
            demand.setQuantityProvided(demand.getQuantityProvided() + itemsTraded);
            demand.save();
        } else {
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, ErrorHelper.errors.get(tradeResponse), false);
        }

        dh.updateCoins(Inventory.getCoins());
        displayDemandInfo();
        visitorType.updateUnlockedPreferences(itemToSell, itemState.getId());
        visitorType.updateBestItem(itemToSell, itemState.getId(), value);

        if (demand.isDemandFulfilled()) {
            TableLayout itemsTable = (TableLayout) findViewById(R.id.itemsTable);
            itemsTable.setVisibility(View.GONE);
        } else {
            displayItemsTable();
        }
    }

    public void toggleMax(View view) {
        tradeMax = !prefs.getBoolean("tradeMax", false);
        prefs.edit().putBoolean("tradeMax", tradeMax).apply();
        updateMax();
    }

    private void updateMax() {
        Drawable tick = dh.createDrawable(R.drawable.tick, 25, 25);
        Drawable cross = dh.createDrawable(R.drawable.cross, 25, 25);

        ImageView maxIndicator = (ImageView) findViewById(R.id.maxIndicator);
        tradeMax = prefs.getBoolean("tradeMax", false);
        maxIndicator.setImageDrawable(tradeMax ? tick : cross);
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
