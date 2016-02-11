package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;

public class InventoryActivity extends Activity {
    public static DisplayHelper dh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        dh = DisplayHelper.getInstance(getApplicationContext());

        updateInventoryTable();
    }

    public void updateInventoryTable() {
        List<Inventory> allInventoryItems = Select.from(Inventory.class).where(
                Condition.prop("quantity").gt(0),
                Condition.prop("item").notEq(Constants.ITEM_COINS)).list();
        TableLayout inventoryTable = (TableLayout) findViewById(R.id.inventoryTable);
        inventoryTable.removeAllViews();

        TableRow headerRow = new TableRow(getApplicationContext());
        headerRow.addView(dh.createTextView("", 22, Color.BLACK));
        headerRow.addView(dh.createTextView("Name", 22, Color.BLACK));
        headerRow.addView(dh.createTextView("Qty ", 22, Color.BLACK));
        headerRow.addView(dh.createTextView("Sell", 22, Color.BLACK));
        inventoryTable.addView(headerRow);

        for (Inventory inventoryItem : allInventoryItems) {
            TableRow itemRow = new TableRow(getApplicationContext());
            Item item = Item.findById(Item.class, inventoryItem.getItem());
            ImageView image = dh.createItemImage(item.getId(), 100, 100, Constants.TRUE);

            String itemName = item.getPrefix(inventoryItem.getState()) + item.getName();
            TextViewPixel name = dh.createTextView(itemName, 20, Color.BLACK);
            name.setSingleLine(false);
            name.setPadding(0, 12, 0, 0);

            TextViewPixel count = dh.createTextView(Integer.toString(inventoryItem.getQuantity()), 20, Color.BLACK);

            TextViewPixel sell = dh.createTextView(Integer.toString(item.getModifiedValue(inventoryItem.getState())), 20, Color.BLACK);
            sell.setGravity(Gravity.CENTER);
            sell.setBackgroundResource(R.drawable.sell_small);

            sell.setTag(R.id.itemID, item.getId());
            sell.setTag(R.id.itemState, inventoryItem.getState());
            sell.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    clickSellButton(v);
                }
            });

            itemRow.addView(image);
            itemRow.addView(name);
            itemRow.addView(count);
            itemRow.addView(sell);
            inventoryTable.addView(itemRow);
        }
    }

    public void clickSellButton(View view) {
        int quantity = 1;
        Long itemID = (Long)view.getTag(R.id.itemID);
        int itemState = (int)view.getTag(R.id.itemState);
        Item itemToSell = Item.findById(Item.class, itemID);
        int itemValue = itemToSell.getModifiedValue(itemState);

        int sellResponse = Inventory.sellItem(itemID, itemState, quantity, itemValue);
        if (sellResponse == Constants.SUCCESS) {
            SoundHelper.playSound(this, SoundHelper.sellingSounds);
            Toast.makeText(getApplicationContext(), String.format("Added %1sx %2s to pending selling for %3s coin(s)", quantity, itemToSell.getName(), itemValue), Toast.LENGTH_SHORT).show();
            Player_Info.increaseByOne(Player_Info.Statistic.ItemsSold);
            Player_Info.increaseByX(Player_Info.Statistic.CoinsEarned, itemValue);
        } else {
            Toast.makeText(getApplicationContext(), ErrorHelper.errors.get(sellResponse), Toast.LENGTH_SHORT).show();
        }
        updateInventoryTable();
        dh.updateCoins(dh.getCoins());
    }


    public void closePopup(View view) {
        finish();
    }
}
