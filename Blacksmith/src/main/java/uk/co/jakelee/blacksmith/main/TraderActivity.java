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
import android.widget.TextView;
import android.widget.Toast;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ErrorHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.model.Character;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Trader;
import uk.co.jakelee.blacksmith.model.Trader_Stock;

public class TraderActivity extends Activity {
    public static DisplayHelper dh;
    public static Trader trader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trader);
        dh = DisplayHelper.getInstance(getApplicationContext());

        Intent intent = getIntent();
        int traderID = Integer.parseInt(intent.getStringExtra(MarketActivity.TRADER_TO_LOAD));

        if (traderID > 0) {
            trader = Trader.findById(Trader.class, traderID);
            createTraderInterface();
        }
    }

    public void createTraderInterface() {
        createTrader();
        createItemList();
    }

    public void createTrader() {
        Character traderCharacter = Character.findById(Character.class, trader.getShopkeeper());

        int drawableID = getApplicationContext().getResources().getIdentifier("character" + traderCharacter.getId(), "drawable", getApplicationContext().getPackageName());
        ImageView traderImage = (ImageView) findViewById(R.id.traderImage);
        traderImage.setImageResource(drawableID);

        TextView traderName = (TextView) findViewById(R.id.traderName);
        traderName.setText(traderCharacter.getName());

        TextView traderGreeting = (TextView) findViewById(R.id.traderGreeting);
        traderGreeting.setText(traderCharacter.getIntro());
    }

    public void createItemList() {
        TableLayout traderItemsInfo = (TableLayout) findViewById(R.id.traderItemsInfo);
        traderItemsInfo.removeAllViews();
        List<Trader_Stock> itemsForSale = Select.from(Trader_Stock.class).where(
                Condition.prop("discovered").eq(Constants.TRUE),
                Condition.prop("trader_type").eq(trader.getId())).list();

        for (Trader_Stock itemForSale : itemsForSale) {
            TableRow itemRow = new TableRow(getApplicationContext());
            Item item = Item.findById(Item.class, itemForSale.getItemID());

            ImageView itemImage = dh.createItemImage(itemForSale.getItemID(), 100, 100, true);
            TextViewPixel itemStock = dh.createTextView(itemForSale.getStock() + " / " + itemForSale.getDefaultStock() + "x " + item.getName(), 16, Color.BLACK);
            TextViewPixel itemBuy = dh.createTextView(Integer.toString(item.getValue()), 18, Color.BLACK);
            itemBuy.setWidth(30);
            itemBuy.setShadowLayer(10, 0, 0, Color.WHITE);
            itemBuy.setGravity(Gravity.CENTER);
            itemBuy.setBackgroundResource(R.drawable.sell_small);
            itemBuy.setTag(item.getId());
            itemBuy.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    clickBuyButton(v);
                }
            });

            itemRow.addView(itemImage);
            itemRow.addView(itemStock);
            itemRow.addView(itemBuy);

            traderItemsInfo.addView(itemRow);
        }
    }

    public void clickBuyButton(View v) {
        int quantity = 1;
        Item itemToBuy = Item.findById(Item.class, (Long) v.getTag());

        int buyResponse = Inventory.buyItem(itemToBuy.getId(), quantity, trader.getId(), itemToBuy.getValue());
        if (buyResponse == Constants.SUCCESS) {
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, String.format("Added %1sx %2s to pending buying for %3s coin(s)", 1, itemToBuy.getName(), itemToBuy.getValue()));
        } else {
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, ErrorHelper.errors.get(buyResponse));
        }
        createItemList();
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TRADER);
        startActivity(intent);
    }

    public void closeTrader(View view) {
        finish();
    }
}
