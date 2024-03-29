package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.ItemTable;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ErrorHelper;
import uk.co.jakelee.blacksmith.helper.GooglePlayHelper;
import uk.co.jakelee.blacksmith.helper.ListenerHelper;
import uk.co.jakelee.blacksmith.helper.SoundHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Pending_Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Setting;
import uk.co.jakelee.blacksmith.model.Super_Upgrade;
import uk.co.jakelee.blacksmith.model.Type;

public class InventoryActivity extends Activity implements ItemTable, AdapterView.OnItemSelectedListener {
    private static final Handler handler = new Handler();
    private static DisplayHelper dh;
    private LinearLayout sell1;
    private LinearLayout sell10;
    private LinearLayout sell100;
    private Type selectedType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        dh = DisplayHelper.getInstance(getApplicationContext());
        dh.updateFullscreen(this);
        loadSelectedType();

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

        sell1 = (LinearLayout) findViewById(R.id.sell1);
        sell10 = (LinearLayout) findViewById(R.id.sell10);
        sell100 = (LinearLayout) findViewById(R.id.sell100);

        updateQuantityUI();
        createDropdown();

        if (Setting.getSafeBoolean(Constants.SETTING_HANDLE_MAX)) {
            ((TextView) findViewById(R.id.sell100Text)).setText(R.string.sellMaxText);
        } else {
            ((TextView) findViewById(R.id.sell100Text)).setText(R.string.sell100Text);
        }
    }

    private void loadSelectedType() {
        SharedPreferences prefs = MainActivity.prefs != null ? MainActivity.prefs : getSharedPreferences("uk.co.jakelee.blacksmith", MODE_PRIVATE);
        long savedType = prefs.getLong("inventoryFilter", 0);
        if (savedType > 0) {
            selectedType = Type.findById(Type.class, savedType);
        } else {
            selectedType = null;
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        selectedType = Type.findById(Type.class, pos);
        dh.updateFullscreen(this);
        displayItemsTable();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        displayItemsTable();
    }

    private void createDropdown() {
        Spinner typeSelector = (Spinner) findViewById(R.id.itemTypes);
        typeSelector.getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner, getTypeStrings());
        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        typeSelector.setAdapter(adapter);
        typeSelector.setOnItemSelectedListener(this);
        typeSelector.setSelection(selectedType != null ? adapter.getPosition(selectedType.getName(this)) : 0);
    }

    private List<String> getTypeStrings() {
        List<String> typeStrings = new ArrayList<>();
        List<Type> types = Select.from(Type.class).list();
        typeStrings.add(getString(R.string.exchangePagesAll));
        for (Type type : types) {
            if (type.getId() <= Constants.TYPE_PROCESSED_FOOD) {
                typeStrings.add(type.getName(this));
            }
        }
        return typeStrings;

    }

    protected void onStop() {
        super.onStop();

        getSharedPreferences("uk.co.jakelee.blacksmith", MODE_PRIVATE).edit().putLong("inventoryFilter", selectedType != null ? selectedType.getId() : 0).apply();
        handler.removeCallbacksAndMessages(null);
    }

    public void displayItemsTable() {
        final Activity activity = this;
        final List<Inventory> allInventoryItems;
        if (selectedType != null) {
            allInventoryItems = Inventory.findWithQuery(Inventory.class, "SELECT * FROM inventory INNER JOIN item on inventory.item = item.id WHERE item.id <> 52 AND inventory.quantity > 0 AND item.type = " + selectedType.getId() + " ORDER BY inventory.state, item.name ASC");
        } else {
            allInventoryItems = Inventory.findWithQuery(Inventory.class, "SELECT * FROM inventory INNER JOIN item on inventory.item = item.id WHERE item.id <> 52 AND inventory.quantity > 0 ORDER BY inventory.state, item.name ASC");
        }
        List<TableRow> tableRows = new ArrayList<>();
        final TableLayout inventoryTable = (TableLayout) findViewById(R.id.inventoryTable);

        TableRow headerRow = new TableRow(getApplicationContext());
        headerRow.addView(dh.createTextView("Qty", 22, Color.BLACK));
        headerRow.addView(dh.createTextView("", 22, Color.BLACK));
        headerRow.addView(dh.createTextView("Name", 22, Color.BLACK));
        headerRow.addView(dh.createTextView("Sell", 22, Color.BLACK));
        tableRows.add(headerRow);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.noItems).setVisibility(allInventoryItems.size() > 0 ? View.GONE : View.VISIBLE);
                findViewById(R.id.inventoryTable).setVisibility(allInventoryItems.size() > 0 ? View.VISIBLE : View.INVISIBLE);
            }
        });


        for (final Inventory inventoryItem : allInventoryItems) {
            TableRow itemRow = new TableRow(getApplicationContext());
            final Item item = Item.findById(Item.class, inventoryItem.getItem());

            TextViewPixel count = dh.createTextView(Integer.toString(inventoryItem.getQuantity()), 20, Color.BLACK);

            ImageView image = dh.createItemImage(item.getId(), (int) inventoryItem.getState(), 35, 35, inventoryItem.haveSeen(), true, inventoryItem.isUnsellable());

            final String itemName = item.getPrefix(inventoryItem.getState()) + item.getName(this);
            TextViewPixel name = dh.createTextView(itemName, 20, Color.BLACK);
            name.setSingleLine(false);
            name.setTag(R.id.itemID, inventoryItem.getItem());
            name.setTag(R.id.itemState, inventoryItem.getState());
            name.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            name.setPadding(0, dh.convertDpToPixel(5), 0, 17);
            name.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    ToastHelper.showToast(inventoryTable, ToastHelper.SHORT, item.getDescription(activity), false);
                }
            });
            name.setOnLongClickListener(ListenerHelper.getItemLongClick(this, this));

            itemRow.addView(count);
            itemRow.addView(image);
            itemRow.addView(name);

            TextViewPixel tradeBtn = dh.createTextView("", 20);
            tradeBtn.setClickable(true);
            tradeBtn.setTextColor(getResources().getColorStateList(R.color.text_color));
            tradeBtn.setGravity(Gravity.CENTER);
            tradeBtn.setWidth(dh.convertDpToPixel(40));
            tradeBtn.setBackgroundResource(R.drawable.sell_small);

            if (item.getType() != Constants.TYPE_PAGE && item.getType() != Constants.TYPE_BOOK) {
                tradeBtn.setText(Integer.toString(item.getModifiedValue(inventoryItem.getState())));
                tradeBtn.setTag(R.id.itemID, item.getId());
                tradeBtn.setTag(R.id.itemState, inventoryItem.getState());
                tradeBtn.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        clickSellButton(v);
                    }
                });
            } else if (item.getType() == Constants.TYPE_PAGE && inventoryItem.getQuantity() >= Constants.PAGE_EXCHANGE_QTY) {
                tradeBtn.setText(R.string.exchangePagesText);
                tradeBtn.setTag((int) (long) item.getId());
                tradeBtn.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        clickExchangeButton(v);
                    }
                });
            }
            itemRow.addView(tradeBtn);

            tableRows.add(itemRow);
        }

        final List<TableRow> finalRows = tableRows;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.loadingMessage).setVisibility(View.GONE);
                inventoryTable.removeAllViews();
                for (TableRow row : finalRows) {
                    inventoryTable.addView(row);
                }
            }
        });
    }

    private void clickExchangeButton(View v) {
        int itemId = (int) v.getTag();
        Item item = Item.findById(Item.class, itemId);
        Inventory inventory = Inventory.getInventory(itemId, Constants.STATE_NORMAL);
        if (inventory.isUnsellable()) {
            ToastHelper.showErrorToast(v, Toast.LENGTH_SHORT, getString(ErrorHelper.errors.get(Constants.ERROR_UNSELLABLE)), false);
        } else {
            AlertDialogHelper.confirmPageExchange(this, this, findViewById(R.id.inventoryTable), inventory, item);
        }
    }

    private void clickSellButton(View view) {
        int quantityToSell = 0;
        List<Integer> itemPrices = new ArrayList<>();

        int quantity = getSharedPreferences("uk.co.jakelee.blacksmith", MODE_PRIVATE).getInt("sellQuantity", 1);
        Long itemID = (Long) view.getTag(R.id.itemID);
        long itemState = (Long) view.getTag(R.id.itemState);
        Item itemToSell = Item.findById(Item.class, itemID);
        int itemValue = itemToSell.getModifiedValue(itemState);
        Inventory inventory = Inventory.getInventory(itemID, itemState);

        int canSell = Constants.ERROR_NOT_ENOUGH_ITEMS;
        if (inventory.isUnsellable()) {
            canSell = Constants.ERROR_UNSELLABLE;
        } else if (MainActivity.vh.inventoryBusy) {
            canSell = Constants.ERROR_BUSY;
        } else {
            if (inventory.getQuantity() < quantity || (quantity == 100 && Setting.getSafeBoolean(Constants.SETTING_HANDLE_MAX))) {
                quantity = inventory.getQuantity();
            }

            if (Super_Upgrade.isEnabled(Constants.SU_BONUS_GOLD)) {
                itemValue = itemValue * 2;
            }

            quantityToSell = quantity;
            inventory.setQuantity(inventory.getQuantity() - quantity);
            inventory.save();

            for (int i = 1; i <= quantity; i++) {
                itemPrices.add(itemValue);
            }
        }

        if (quantityToSell > 0) {
            SoundHelper.playSound(this, SoundHelper.sellingSounds);
            ToastHelper.showToast(view, ToastHelper.SHORT, String.format(getString(R.string.sellSuccess), quantity, itemToSell.getName(this), itemValue), false);
            Player_Info.increaseByOne(Player_Info.Statistic.ItemsSold);
            Player_Info.increaseByX(Player_Info.Statistic.CoinsEarned, itemValue * quantityToSell);
            GooglePlayHelper.UpdateEvent(Constants.EVENT_SOLD_ITEM, quantityToSell);

            Pending_Inventory.addScheduledItems(this, itemPrices);
            MainActivity.vh.inventoryBusy = true;
            dimButtons();
        } else {
            ToastHelper.showErrorToast(view, ToastHelper.SHORT, getString(ErrorHelper.errors.get(canSell)), false);
        }
        displayItemsTable();
        dh.updateCoins(Inventory.getCoins());

        int[] location = new int[2];
        view.getLocationOnScreen(location);
    }

    public void sell1Toggle(View view) {
        getSharedPreferences("uk.co.jakelee.blacksmith", MODE_PRIVATE).edit().putInt("sellQuantity", 1).apply();
        updateQuantityUI();
    }

    public void sell10Toggle(View view) {
        getSharedPreferences("uk.co.jakelee.blacksmith", MODE_PRIVATE).edit().putInt("sellQuantity", 10).apply();
        updateQuantityUI();
    }

    public void sell100Toggle(View view) {
        getSharedPreferences("uk.co.jakelee.blacksmith", MODE_PRIVATE).edit().putInt("sellQuantity", 100).apply();
        updateQuantityUI();
    }

    public void calculatingComplete() {
        MainActivity.vh.inventoryBusy = false;
        brightenButtons();
    }

    private void updateQuantityUI() {
        Drawable tick = dh.createDrawable(R.drawable.tick, 25, 25);
        Drawable cross = dh.createDrawable(R.drawable.cross, 25, 25);
        int quantity = getSharedPreferences("uk.co.jakelee.blacksmith", MODE_PRIVATE).getInt("sellQuantity", 1);

        ((ImageView) findViewById(R.id.sell1indicator)).setImageDrawable(quantity == 1 ? tick : cross);
        ((ImageView) findViewById(R.id.sell10indicator)).setImageDrawable(quantity == 10 ? tick : cross);
        ((ImageView) findViewById(R.id.sell100indicator)).setImageDrawable(quantity == 100 ? tick : cross);
    }

    public void callback() {
        displayItemsTable();
    }

    public void brightenButtons() {
        sell1.setAlpha(1);
        sell10.setAlpha(1);
        sell100.setAlpha(1);
    }

    public void dimButtons() {
        sell1.setAlpha(0.3f);
        sell10.setAlpha(0.3f);
        sell100.setAlpha(0.3f);
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Inventory);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
