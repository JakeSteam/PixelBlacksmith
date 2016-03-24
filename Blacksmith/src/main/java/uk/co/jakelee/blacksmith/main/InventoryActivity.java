package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
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
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.helper.VisitorHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Upgrade;

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
                Condition.prop("item").notEq(Constants.ITEM_COINS))
                .orderBy("state ASC").list();
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

            ImageView image = dh.createItemImage(item.getId(), 100, 100, inventoryItem.haveSeen());

            String itemName = item.getPrefix(inventoryItem.getState()) + item.getName();
            TextViewPixel name = dh.createTextView(itemName, 20, Color.BLACK);
            name.setSingleLine(false);
            name.setPadding(0, 12, 0, 0);

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

            itemRow.addView(count);
            itemRow.addView(image);
            itemRow.addView(name);
            itemRow.addView(sell);
            inventoryTable.addView(itemRow);
        }
    }

    public void clickSellButton(View view) {
        int quantity = 1;
        Long itemID = (Long)view.getTag(R.id.itemID);
        Long itemState = (Long)view.getTag(R.id.itemState);
        Item itemToSell = Item.findById(Item.class, itemID);
        int itemValue = itemToSell.getModifiedValue(itemState);

        double coinMultiplier = VisitorHelper.percentToMultiplier(Upgrade.getValue("Gold Bonus"));
        double modifiedPrice = coinMultiplier * itemValue;

        int sellResponse = Inventory.sellItem(itemID, itemState, quantity, (int) modifiedPrice);
        if (sellResponse == Constants.SUCCESS) {
            SoundHelper.playSound(this, SoundHelper.sellingSounds);
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, String.format("Added %1sx %2s to pending selling for %3s coin(s)", quantity, itemToSell.getName(), (int) modifiedPrice));
            Player_Info.increaseByOne(Player_Info.Statistic.ItemsSold);
            Player_Info.increaseByX(Player_Info.Statistic.CoinsEarned, itemValue);
        } else {
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, ErrorHelper.errors.get(sellResponse));
        }
        updateInventoryTable();
        dh.updateCoins(Inventory.getCoins());
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
