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
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;

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
        headerRow.addView(dh.createTextView("", 18, Color.BLACK));
        headerRow.addView(dh.createTextView("Name", 18, Color.BLACK));
        headerRow.addView(dh.createTextView("Qty", 18, Color.BLACK));
        headerRow.addView(dh.createTextView("Sell", 18, Color.BLACK));
        inventoryTable.addView(headerRow);

        for (Inventory inventoryItem : allInventoryItems) {
            TableRow itemRow = new TableRow(getApplicationContext());
            Item item = Item.findById(Item.class, inventoryItem.getItem());
            ImageView image = dh.createItemImage(item.getId(), 100, 100, Constants.TRUE);

            String itemName = item.getName();
            if (inventoryItem.getState() == Constants.STATE_NORMAL) {
                itemName = "(unf) " + itemName;
            }

            TextViewPixel name = dh.createTextView(itemName, 15, Color.BLACK);
            name.setSingleLine(false);
            name.setWidth(275);

            TextViewPixel count = dh.createTextView(Integer.toString(inventoryItem.getQuantity()), 15, Color.BLACK);

            TextViewPixel sell = dh.createTextView(Integer.toString(item.getValue()), 18, Color.BLACK);
            sell.setWidth(30);
            sell.setShadowLayer(10, 0, 0, Color.WHITE);
            sell.setGravity(Gravity.CENTER);
            sell.setBackgroundResource(R.drawable.sell);
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
            itemRow.addView(sell, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            inventoryTable.addView(itemRow);
        }
    }

    public void clickSellButton(View view) {
        int quantity = 1;
        Long itemID = (Long)view.getTag(R.id.itemID);
        int itemState = (int)view.getTag(R.id.itemState);
        Item itemToSell = Item.findById(Item.class, itemID);

        if (Inventory.sellItem(itemID, itemState, quantity, itemToSell.getValue())) {
            Toast.makeText(getApplicationContext(), String.format("Added %1sx %2s to pending selling for %3s coin(s)", quantity, itemToSell.getName(), itemToSell.getValue()), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), String.format("Couldn't sell %1s", itemToSell.getName()), Toast.LENGTH_SHORT).show();
        }
        updateInventoryTable();
        dh.updateCoins(dh.getCoins());
    }


    public void closeInventory(View view) {
        finish();
    }
}
