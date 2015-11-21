package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.List;

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
        dbh = new DatabaseHelper(getApplicationContext());
        dh = new DisplayHelper(getApplicationContext());

        updateInventoryTable();
    }

    public void updateInventoryTable() {
        List<Inventory> allInventoryItems = dbh.getAllInventoryItems();
        TableLayout inventoryTable = (TableLayout) findViewById(R.id.inventoryTable);
        inventoryTable.removeAllViews();

        TableRow headerRow = new TableRow(getApplicationContext());
        headerRow.addView(dh.CreateTextView("", 18, Color.BLACK));
        headerRow.addView(dh.CreateTextView("Name", 18, Color.BLACK));
        headerRow.addView(dh.CreateTextView("Have", 18, Color.BLACK));
        headerRow.addView(dh.CreateTextView("Sell", 18, Color.BLACK));
        inventoryTable.addView(headerRow);

        for (Inventory inventoryItem : allInventoryItems) {
            TableRow itemRow = new TableRow(getApplicationContext());
            Item item = dbh.getItemById(inventoryItem.getItem());

            itemRow.addView(dh.CreateItemImage(item.getId(), 10, 10, "T"));
            itemRow.addView(dh.CreateTextView(item.getName(), 15, Color.BLACK));
            itemRow.addView(dh.CreateTextView(Integer.toString(inventoryItem.getQuantity()), 15, Color.BLACK));
            itemRow.addView(dh.CreateTextView(Integer.toString(item.getId()), 15, Color.BLACK));
            inventoryTable.addView(itemRow);
        }
    }


    public void CloseInventory(View view) {
        finish();
    }
}
