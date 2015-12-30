package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.DatabaseHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;

public class InventoryActivity extends Activity {
    public static DatabaseHelper dbh;
    public static DisplayHelper dh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        dbh = DatabaseHelper.getInstance(getApplicationContext());
        dh = DisplayHelper.getInstance(getApplicationContext());

        updateInventoryTable();
    }

    public void updateInventoryTable() {
        List<Inventory> allInventoryItems = Inventory.findWithQuery(Inventory.class, "SELECT * FROM inventory WHERE item <> 52");
        TableLayout inventoryTable = (TableLayout) findViewById(R.id.inventoryTable);
        inventoryTable.removeAllViews();

        TableRow headerRow = new TableRow(getApplicationContext());
        headerRow.addView(dh.createTextView("", 18, Color.BLACK));
        headerRow.addView(dh.createTextView("Name", 18, Color.BLACK));
        headerRow.addView(dh.createTextView("Have", 18, Color.BLACK));
        headerRow.addView(dh.createTextView("Sell", 18, Color.BLACK));
        inventoryTable.addView(headerRow);

        for (Inventory inventoryItem : allInventoryItems) {
            TableRow itemRow = new TableRow(getApplicationContext());
            List<Item> items = Item.find(Item.class, "id = " + inventoryItem.getItem());
            Item item = items.get(0);
            ImageView image = dh.createItemImage(item.getId(), 15, 15, 1);

            String itemName = item.getName();
            if (inventoryItem.getState() == 2) {
                itemName = "(unf) " + itemName;
            }

            TextView name = dh.createTextView(itemName, 15, Color.BLACK);
            name.setSingleLine(false);

            ImageView sell = dh.createItemImage(52L, 15, 15, 1);
            sell.setTag(item.getId());
            sell.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    clickSellButton(v);
                }
            });

            itemRow.addView(image);
            itemRow.addView(name);
            itemRow.addView(dh.createTextView(Integer.toString(inventoryItem.getQuantity()), 15, Color.BLACK));
            itemRow.addView(sell);
            inventoryTable.addView(itemRow);
        }
    }

    public void clickSellButton(View view) {
        Item itemToSell = Item.findById(Item.class, (Long) view.getTag());
        if (Inventory.sellItem(itemToSell.getId(), 1, 1, itemToSell.getValue())) {
            Toast.makeText(getApplicationContext(), String.format("Added %1sx %2s to pending selling for %3s coins", 1, itemToSell.getName(), itemToSell.getValue()), Toast.LENGTH_SHORT).show();
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
