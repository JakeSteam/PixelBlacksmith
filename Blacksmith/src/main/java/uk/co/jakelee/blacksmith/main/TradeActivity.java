package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.percent.PercentRelativeLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.ItemTable;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ErrorHelper;
import uk.co.jakelee.blacksmith.helper.GooglePlayHelper;
import uk.co.jakelee.blacksmith.helper.ListenerHelper;
import uk.co.jakelee.blacksmith.helper.ParticleHelper;
import uk.co.jakelee.blacksmith.helper.SoundHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.helper.TutorialHelper;
import uk.co.jakelee.blacksmith.helper.VisitorHelper;
import uk.co.jakelee.blacksmith.model.Criteria;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Setting;
import uk.co.jakelee.blacksmith.model.State;
import uk.co.jakelee.blacksmith.model.Upgrade;
import uk.co.jakelee.blacksmith.model.Visitor;
import uk.co.jakelee.blacksmith.model.Visitor_Demand;
import uk.co.jakelee.blacksmith.model.Visitor_Log;
import uk.co.jakelee.blacksmith.model.Visitor_Type;

public class TradeActivity extends Activity implements ItemTable {
    private static final Handler handler = new Handler();
    private static Visitor_Demand demand;
    private static Visitor_Type visitorType;
    private static DisplayHelper dh;
    private static boolean tradeMax = false;
    private boolean currentlySelling = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);
        dh = DisplayHelper.getInstance(getApplicationContext());
        dh.updateFullscreen(this);

        Intent intent = getIntent();
        int demandId = Integer.parseInt(intent.getStringExtra(DisplayHelper.DEMAND_TO_LOAD));
        if (demandId > 0) {
            demand = Visitor_Demand.findById(Visitor_Demand.class, demandId);
            Visitor visitor = Visitor.findById(Visitor.class, demand.getVisitorID());
            visitorType = Visitor_Type.findById(Visitor_Type.class, visitor.getType());

            new Thread(new Runnable() {
                public void run() {
                    createTradeInterface();
                }
            }).start();
        }

        if (TutorialHelper.currentlyInTutorial && TutorialHelper.currentStage <= Constants.STAGE_3_TRADE) {
            startTutorial();
        }

        final Runnable every2Seconds = new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    public void run() {
                        displayItemsTable();
                    }
                }).start();
                handler.postDelayed(this, DateHelper.MILLISECONDS_IN_SECOND * 2);
            }
        };

        if (Setting.getSafeBoolean(Constants.SETTING_AUTOREFRESH)) {
            handler.post(every2Seconds);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (TutorialHelper.currentlyInTutorial) {
            TutorialHelper.chainTourGuide.next();
        }
    }

    protected void onStop() {
        super.onStop();

        handler.removeCallbacksAndMessages(null);
    }

    private void startTutorial() {
        TutorialHelper th = new TutorialHelper(this, Constants.STAGE_3_TRADE);
        th.addTutorial(findViewById(R.id.itemsTable), R.string.tutorialTradeItems, R.string.tutorialTradeItemsText, true);
        th.addTutorial(findViewById(R.id.finishTrade), R.string.tutorialTradeFinish, R.string.tutorialTradeFinishText, true, Gravity.TOP);
        th.start();
    }

    private void createTradeInterface() {
        displayVisitorInfo();
        displayDemandInfo();
        displayItemsTable();
        updateMax();
    }

    private void displayVisitorInfo() {
        final TextViewPixel visitorName = (TextViewPixel) findViewById(R.id.visitorName);
        final Context context = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                visitorName.setText(visitorType.getName(context));
            }
        });
    }

    private void displayDemandInfo() {
        final Criteria demandCriteria = Criteria.findById(Criteria.class, demand.getCriteriaType());
        final TextViewPixel demandTextView = (TextViewPixel) findViewById(R.id.demandInfo);

        final Activity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                demandTextView.setText(String.format(getString(R.string.demandText),
                        demandCriteria.getName(activity),
                        Visitor_Demand.getCriteriaName(activity, demand),
                        demand.getQuantityProvided(),
                        demand.getQuantity()
                ));
            }
        });

        ProgressBar demandProgress = (ProgressBar) findViewById(R.id.demandProgress);
        demandProgress.setMax(demand.getQuantity());
        demandProgress.setProgress(demand.getQuantityProvided());
    }

    public void displayItemsTable() {
        List<TableRow> tableRows = new ArrayList<>();
        List<Inventory> matchingItems = demand.getMatchingInventory();
        final TableLayout itemsTable = (TableLayout) findViewById(R.id.itemsTable);

        final TextView noItemsMessage = (TextView) findViewById(R.id.noItemsMessage);
        if (matchingItems.size() == 0) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    noItemsMessage.setVisibility(View.VISIBLE);
                }
            });
        } else {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    noItemsMessage.setVisibility(View.GONE);
                }
            });

            // Create header row
            TableRow headerRow = new TableRow(getApplicationContext());
            headerRow.addView(dh.createTextView(getString(R.string.tableQuantity), 22, Color.BLACK));
            headerRow.addView(dh.createTextView(" ", 22, Color.BLACK));
            headerRow.addView(dh.createTextView(getString(R.string.tableItem), 22, Color.BLACK));
            headerRow.addView(dh.createTextView(getString(R.string.tableSell), 22, Color.BLACK));
            headerRow.addView(dh.createTextView(" ", 22, Color.BLACK));
            tableRows.add(headerRow);

            for (final Inventory inventory : matchingItems) {
                boolean hasBeenTried = Visitor_Log.hasBeenTried(visitorType.getVisitorID(), inventory.getItem(), inventory.getState());
                TableRow itemRow = new TableRow(getApplicationContext());
                final Item item = Item.findById(Item.class, inventory.getItem());
                TextViewPixel quantity = dh.createTextView(String.valueOf(inventory.getQuantity()), 20, hasBeenTried ? Color.GRAY : Color.BLACK);
                ImageView image = dh.createItemImage(inventory.getItem(), (int) inventory.getState(), 35, 35, true, true, inventory.isUnsellable());

                final String itemName = item.getPrefix(inventory.getState()) + item.getName(this);
                TextViewPixel name = dh.createTextView(itemName, 20, hasBeenTried ? Color.GRAY : Color.BLACK);
                name.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                name.setPadding(0, dh.convertDpToPixel(5), 0, 17);
                name.setTag(R.id.itemID, inventory.getItem());
                name.setTag(R.id.itemState, inventory.getState());
                name.setSingleLine(false);
                name.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        ToastHelper.showToast(itemsTable, ToastHelper.SHORT, item.getDescription(itemsTable.getContext()), false);
                    }
                });
                name.setOnLongClickListener(ListenerHelper.getItemLongClick(this, this));

                itemRow.addView(quantity);
                itemRow.addView(image);
                itemRow.addView(name);

                // Create a sell button for that item
                if (item.getType() != Constants.TYPE_PAGE && item.getType() != Constants.TYPE_BOOK) {
                    TextViewPixel sell = dh.createTextView(Integer.toString(item.getModifiedValue(inventory.getState())), 18);
                    sell.setClickable(true);
                    sell.setTextColor(getResources().getColorStateList(R.color.text_color));
                    sell.setGravity(Gravity.CENTER);
                    sell.setWidth(dh.convertDpToPixel(40));
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
                    } else if (bonus == Constants.DEFAULT_BONUS && visitorType.isStateDiscovered() && visitorType.isTierDiscovered() && visitorType.isTypeDiscovered()) {
                        bonusText.setText("+0%");
                    }

                    itemRow.addView(sell);
                    itemRow.addView(bonusText);
                }
                tableRows.add(itemRow);
            }
        }

        final List<TableRow> finalRows = tableRows;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                itemsTable.removeAllViews();
                for (TableRow row : finalRows) {
                    itemsTable.addView(row);
                }
            }
        });
    }

    private void clickSellButton(View v) {
        Long itemID = (Long) v.getTag(R.id.itemID);
        Long itemState = (Long) v.getTag(R.id.itemState);

        Inventory inventoryOfItem = Inventory.getInventory(itemID, itemState);
        Item itemObject = Item.findById(Item.class, itemID);
        State itemStateObject = State.findById(State.class, itemState);

        if (inventoryOfItem.getQuantity() > 0 && !currentlySelling) {
            currentlySelling = true;
            new Visitor_Log(visitorType.getVisitorID(), itemID, itemState).save();
            int quantity = 1;
            if (getSharedPreferences("uk.co.jakelee.blacksmith", MODE_PRIVATE).getBoolean("tradeMax", false)) {
                quantity = demand.getQuantity() - demand.getQuantityProvided();
            }
            tradeItem(quantity, itemObject, itemStateObject);
            currentlySelling = false;
        }

        ParticleHelper.getInstance(this).triggerExplosion((PercentRelativeLayout)findViewById(R.id.furnace), v, ParticleHelper.MANY);
    }

    private void tradeItem(int quantity, Item itemToSell, State itemState) {
        Inventory itemInventory = Select.from(Inventory.class).where(
                Condition.prop("item").eq(itemToSell.getId()),
                Condition.prop("state").eq(itemState.getId())).first();

        // Calculate the item sell value, rounded up
        double bonus = visitorType.getBonus(itemInventory);
        double coinMultiplier = VisitorHelper.percentToMultiplier(Upgrade.getValue("Coins Bonus")) + (0.5 * Player_Info.getPrestige());
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
            ToastHelper.showToast(findViewById(R.id.tradeTitle), ToastHelper.SHORT, String.format(getString(R.string.tradedItem),
                    itemsTraded,
                    itemToSell.getName(this),
                    value * itemsTraded), false);
            Player_Info.increaseByX(Player_Info.Statistic.ItemsTraded, itemsTraded);
            Player_Info.increaseByX(Player_Info.Statistic.CoinsEarned, value * itemsTraded);
            GooglePlayHelper.UpdateEvent(Constants.EVENT_TRADE_ITEM, itemsTraded);

            demand.setQuantityProvided(demand.getQuantityProvided() + itemsTraded);
            demand.save();
        } else {
            ToastHelper.showErrorToast(findViewById(R.id.tradeTitle), ToastHelper.SHORT, getString(ErrorHelper.errors.get(tradeResponse)), false);
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
        tradeMax = !getSharedPreferences("uk.co.jakelee.blacksmith", MODE_PRIVATE).getBoolean("tradeMax", false);
        getSharedPreferences("uk.co.jakelee.blacksmith", MODE_PRIVATE).edit().putBoolean("tradeMax", tradeMax).apply();
        updateMax();
    }

    private void updateMax() {
        final Drawable tick = dh.createDrawable(R.drawable.tick, 25, 25);
        final Drawable cross = dh.createDrawable(R.drawable.cross, 25, 25);
        final ImageView maxIndicator = (ImageView) findViewById(R.id.maxIndicator);

        tradeMax = getSharedPreferences("uk.co.jakelee.blacksmith", MODE_PRIVATE).getBoolean("tradeMax", false);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                maxIndicator.setImageDrawable(tradeMax ? tick : cross);
            }
        });
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Trading);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }

}
