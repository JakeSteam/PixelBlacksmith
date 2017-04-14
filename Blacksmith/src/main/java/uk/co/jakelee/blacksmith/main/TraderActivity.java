package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import uk.co.jakelee.blacksmith.controls.AlertDialogCallback;
import uk.co.jakelee.blacksmith.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ErrorHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.model.Character;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Super_Upgrade;
import uk.co.jakelee.blacksmith.model.Trader;
import uk.co.jakelee.blacksmith.model.Trader_Stock;

public class TraderActivity extends Activity implements AlertDialogCallback {
    private static DisplayHelper dh;
    private static Trader trader;
    private static Character traderCharacter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trader);
        dh = DisplayHelper.getInstance(getApplicationContext());
        dh.updateFullscreen(this);

        Intent intent = getIntent();
        int traderID = Integer.parseInt(intent.getStringExtra(MarketActivity.TRADER_TO_LOAD));

        if (traderID > 0) {
            trader = Trader.findById(Trader.class, traderID);
            traderCharacter = Character.findById(Character.class, trader.getShopkeeper());
            createTraderInterface();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        createItemList();
    }

    private void createTraderInterface() {
        createTrader();
        createItemList();
    }

    private void createTrader() {
        int drawableID = getApplicationContext().getResources().getIdentifier("character" + traderCharacter.getId(), "drawable", getApplicationContext().getPackageName());
        ImageView traderImage = (ImageView) findViewById(R.id.traderImage);
        traderImage.setImageResource(drawableID);

        TextView traderName = (TextView) findViewById(R.id.traderName);
        traderName.setText(traderCharacter.getName(this));

        TextView traderGreeting = (TextView) findViewById(R.id.traderGreeting);
        traderGreeting.setText(traderCharacter.getIntro(this));

        TextView traderTrades = (TextView) findViewById(R.id.traderTrades);
        traderTrades.setText(String.format(getString(R.string.traderTrades), trader.getPurchases()));

        updateLockStatus();
    }

    private void updateLockStatus() {
        ((ImageView) findViewById(R.id.traderLockIndicator)).setImageDrawable(dh.createDrawable(trader.isFixed() ? R.drawable.tick : R.drawable.cross, 25, 25));
    }

    private void createItemList() {
        TableLayout traderItemsInfo = (TableLayout) findViewById(R.id.traderItemsInfo);
        traderItemsInfo.removeAllViews();
        List<Trader_Stock> itemsForSale = Select.from(Trader_Stock.class).where(
                Condition.prop("trader_type").eq(trader.getId()),
                Condition.prop("required_purchases").lt(trader.getPurchases() + 1)).list();

        for (Trader_Stock itemForSale : itemsForSale) {
            traderItemsInfo.addView(createItemRow(itemForSale));
        }
    }

    private View createItemRow(Trader_Stock itemForSale) {
        Item item = Item.findById(Item.class, itemForSale.getItemID());
        boolean outOfStock = itemForSale.getStock() <= 0;

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View inflatedView = inflater.inflate(R.layout.custom_trader_stock, null);
        TableRow itemRow = (TableRow) inflatedView.findViewById(R.id.itemRow);

        ImageView itemImage = (ImageView) itemRow.findViewById(R.id.itemImage);
        int itemImageDrawable = this.getResources().getIdentifier("item" + itemForSale.getItemID(), "drawable", this.getPackageName());
        itemImage.setImageDrawable(dh.createDrawable(itemImageDrawable, 100, 100));

        TextView itemStock = (TextView) itemRow.findViewById(R.id.itemStock);
        itemStock.setText(String.format(getString(R.string.traderStock),
                item.getFullName(this, itemForSale.getState()),
                itemForSale.getStock(),
                itemForSale.getDefaultStock()));

        TextView itemBuy = (TextView) itemRow.findViewById(R.id.itemBuy);

        if (outOfStock) {
            itemStock.setPaintFlags(itemStock.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            itemStock.setTextColor(Color.GRAY);
            itemBuy.setVisibility(View.INVISIBLE);
        } else {
            itemBuy.setTag(itemForSale);
            itemBuy.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    clickBuy(v);
                }
            });
        }

        return inflatedView;
    }

    public void clickRestock(View v) {
        int restockCost = getRestockCost();

        if (restockCost > 0) {
            if (Super_Upgrade.isEnabled(Constants.SU_MARKET_RESTOCK)) {
                callbackRestock();
            } else {
                AlertDialogHelper.confirmTraderRestock(getApplicationContext(), this, trader, restockCost);
            }
        } else if (trader.isFixed()) {
            callbackRestock();
        } else {
            ToastHelper.showToast(findViewById(R.id.trader), ToastHelper.SHORT, getString(R.string.unnecessaryRestock), false);
        }
    }

    public void clickBuyAll(View v) {
        AlertDialogHelper.confirmItemBuyAll(this, trader);
    }

    public void toggleTraderLock(View v) {
        if (!trader.isFixed() && Trader.getFixedCount() > (Constants.TRADER_LOCK_MAX - 1)) {
            ToastHelper.showErrorToast(findViewById(R.id.trader), Toast.LENGTH_SHORT, getString(ErrorHelper.errors.get(Constants.ERROR_MAX_LOCKED_TRADERS)), false);
        } else {
            AlertDialogHelper.confirmTraderLock(this, trader);
        }
    }

    public void toggleTraderLock() {
        if (!trader.isFixed() && Inventory.getCoins() >= Constants.TRADER_LOCK_COST) {
            Inventory coins = Inventory.getInventory(Constants.ITEM_COINS, Constants.STATE_NORMAL);
            coins.setQuantity(coins.getQuantity() - Constants.TRADER_LOCK_COST);
            coins.save();
        }
        trader.setFixed(!trader.isFixed());
        trader.save();
        ToastHelper.showPositiveToast(findViewById(R.id.trader), ToastHelper.SHORT, getString(trader.isFixed() ? R.string.lockSuccess : R.string.unlockSuccess), true);
        updateLockStatus();
    }

    private int getRestockCost() {
        if (trader.isFixed()) {
            return 0;
        }

        int itemsForSale = (int) Select.from(Trader_Stock.class).where(
                Condition.prop("trader_type").eq(trader.getId()),
                Condition.prop("required_purchases").lt(trader.getPurchases() + 1),
                Condition.prop("stock").eq(0)).count();

        int costPerStock = Player_Info.getPlayerLevel() * Constants.RESTOCK_COST_MULTIPLIER;
        return costPerStock * itemsForSale;
    }

    private void clickBuy(View v) {
        Trader_Stock itemStock = (Trader_Stock) v.getTag();
        AlertDialogHelper.confirmItemBuy(getApplicationContext(), this, itemStock, trader.isFixed());
    }

    public void callbackRestock() {
        trader.restock(0);
        ToastHelper.showToast(findViewById(R.id.trader), ToastHelper.LONG, getString(R.string.traderRestockCompleteAdvert), true);
        createTraderInterface();
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Trader);
        startActivity(intent);
    }

    public void alertDialogCallback() {
        createTraderInterface();
    }

    public void closePopup(View view) {
        finish();
    }
}
