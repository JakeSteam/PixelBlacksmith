package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ErrorHelper;
import uk.co.jakelee.blacksmith.helper.SoundHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Pending_Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;

public class InventoryActivity extends Activity {
    private static final Handler handler = new Handler();
    private static DisplayHelper dh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        dh = DisplayHelper.getInstance(getApplicationContext());

        final Runnable every2Seconds = new Runnable() {
            @Override
            public void run() {
                updateInventoryTable();
                handler.postDelayed(this, DateHelper.MILLISECONDS_IN_SECOND * 2);
            }
        };
        handler.post(every2Seconds);

        updateQuantityUI();
    }

    private void updateInventoryTable() {
        List<Inventory> allInventoryItems = Inventory.findWithQuery(Inventory.class, "SELECT * FROM inventory INNER JOIN item on inventory.item = item.id WHERE item.id <> 52 AND inventory.quantity > 0 ORDER BY item.name ASC");
        TableLayout inventoryTable = (TableLayout) findViewById(R.id.inventoryTable);
        inventoryTable.removeAllViews();

        TableRow headerRow = new TableRow(getApplicationContext());
        headerRow.addView(dh.createTextView("Qty", 22, Color.BLACK));
        headerRow.addView(dh.createTextView("", 22, Color.BLACK));
        headerRow.addView(dh.createTextView("Name", 22, Color.BLACK));
        headerRow.addView(dh.createTextView("Sell", 22, Color.BLACK));
        inventoryTable.addView(headerRow);

        for (Inventory inventoryItem : allInventoryItems) {
            TableRow itemRow = new TableRow(getApplicationContext());
            Item item = Item.findById(Item.class, inventoryItem.getItem());

            TextViewPixel count = dh.createTextView(Integer.toString(inventoryItem.getQuantity()), 20, Color.BLACK);

            ImageView image = dh.createItemImage(item.getId(), 35, 35, inventoryItem.haveSeen(), true);

            String itemName = item.getPrefix(inventoryItem.getState()) + item.getName();
            TextViewPixel name = dh.createTextView(itemName, 20, Color.BLACK);
            name.setSingleLine(false);
            name.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            name.setPadding(0, dh.convertDpToPixel(5), 0, 17);

            TextViewPixel sell = dh.createTextView(Integer.toString(item.getModifiedValue(inventoryItem.getState())), 20);
            sell.setClickable(true);
            sell.setTextColor(getResources().getColorStateList(R.color.text_color));
            sell.setGravity(Gravity.CENTER);
            sell.setBackgroundResource(R.drawable.sell_small);

            sell.setTag(R.id.itemID, item.getId());
            sell.setTag(R.id.itemState, inventoryItem.getState());
            sell.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    clickSellButton(v);
                }
            });

            itemRow.addView(count);
            itemRow.addView(image);
            itemRow.addView(name);
            itemRow.addView(sell);
            inventoryTable.addView(itemRow);
        }
    }

    private void clickSellButton(View view) {
        int quantityToSell = 0;
        List<Integer> itemPrices = new ArrayList<>();

        int quantity = MainActivity.prefs.getInt("sellQuantity", 1);
        Long itemID = (Long) view.getTag(R.id.itemID);
        long itemState = (Long) view.getTag(R.id.itemState);
        Item itemToSell = Item.findById(Item.class, itemID);
        int itemValue = itemToSell.getModifiedValue(itemState);
        Inventory inventory = Inventory.getInventory(itemID, itemState);

        int canSell = Constants.ERROR_NOT_ENOUGH_ITEMS;
        if (MainActivity.vh.inventoryBusy) {
            canSell = Constants.ERROR_BUSY;
        } else if (inventory.getQuantity() >= quantity) {
            quantityToSell = quantity;
            inventory.setQuantity(inventory.getQuantity() - quantity);
            inventory.save();

            for (int i = 1; i <= quantity; i++) {
                itemPrices.add(itemValue);
            }
        }

        if (quantityToSell > 0) {
            SoundHelper.playSound(this, SoundHelper.sellingSounds);
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, String.format(getString(R.string.sellSuccess), quantity, itemToSell.getName(), itemValue), false);
            Player_Info.increaseByOne(Player_Info.Statistic.ItemsSold);
            Player_Info.increaseByX(Player_Info.Statistic.CoinsEarned, itemValue * quantityToSell);

            Pending_Inventory.addScheduledItems(this, itemPrices);
            MainActivity.vh.inventoryBusy = true;
        } else {
            ToastHelper.showErrorToast(getApplicationContext(), Toast.LENGTH_SHORT, ErrorHelper.errors.get(canSell), false);
        }
        updateInventoryTable();
        dh.updateCoins(Inventory.getCoins());
    }

    public void sell1Toggle(View view) {
        MainActivity.prefs.edit().putInt("sellQuantity", 1).apply();
        updateQuantityUI();
    }

    public void sell10Toggle(View view) {
        MainActivity.prefs.edit().putInt("sellQuantity", 10).apply();
        updateQuantityUI();
    }

    public void sell100Toggle(View view) {
        MainActivity.prefs.edit().putInt("sellQuantity", 100).apply();
        updateQuantityUI();
    }

    public void calculatingComplete() {
        MainActivity.vh.inventoryBusy = false;
    }

    private void updateQuantityUI() {
        Drawable tick = dh.createDrawable(R.drawable.tick, 25, 25);
        Drawable cross = dh.createDrawable(R.drawable.cross, 25, 25);
        int quantity = MainActivity.prefs.getInt("sellQuantity", 1);

        ((ImageView) findViewById(R.id.sell1indicator)).setImageDrawable(quantity == 1 ? tick : cross);
        ((ImageView) findViewById(R.id.sell10indicator)).setImageDrawable(quantity == 10 ? tick : cross);
        ((ImageView) findViewById(R.id.sell100indicator)).setImageDrawable(quantity == 100 ? tick : cross);
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
