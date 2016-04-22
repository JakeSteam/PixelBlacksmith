package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.AlertDialogCallback;
import uk.co.jakelee.blacksmith.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.model.Character;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Trader;
import uk.co.jakelee.blacksmith.model.Trader_Stock;

public class TraderActivity extends Activity implements AlertDialogCallback {
    private static DisplayHelper dh;
    private static Trader trader;

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
        Character traderCharacter = Character.findById(Character.class, trader.getShopkeeper());

        int drawableID = getApplicationContext().getResources().getIdentifier("character" + traderCharacter.getId(), "drawable", getApplicationContext().getPackageName());
        ImageView traderImage = (ImageView) findViewById(R.id.traderImage);
        traderImage.setImageResource(drawableID);

        TextView traderName = (TextView) findViewById(R.id.traderName);
        traderName.setText(traderCharacter.getName());

        TextView traderGreeting = (TextView) findViewById(R.id.traderGreeting);
        traderGreeting.setText(traderCharacter.getIntro());
    }

    private void createItemList() {
        TableLayout traderItemsInfo = (TableLayout) findViewById(R.id.traderItemsInfo);
        traderItemsInfo.removeAllViews();
        List<Trader_Stock> itemsForSale = Select.from(Trader_Stock.class).where(
                Condition.prop("trader_type").eq(trader.getId()),
                Condition.prop("stock").gt(0),
                Condition.prop("required_purchases").lt(trader.getPurchases() + 1)).list();

        for (Trader_Stock itemForSale : itemsForSale) {
            Item item = Item.findById(Item.class, itemForSale.getItemID());

            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View inflatedView = inflater.inflate(R.layout.custom_trader_stock, null);
            TableRow itemRow = (TableRow) inflatedView.findViewById(R.id.itemRow);

            ImageView itemImage = (ImageView) itemRow.findViewById(R.id.itemImage);
            int itemImageDrawable = this.getResources().getIdentifier("item" + itemForSale.getItemID(), "drawable", this.getPackageName());
            itemImage.setImageDrawable(dh.createDrawable(itemImageDrawable, 100, 100));

            TextView itemName = (TextView) itemRow.findViewById(R.id.itemName);
            itemName.setText(item.getName());

            TextView itemStock = (TextView) itemRow.findViewById(R.id.itemStock);
            itemStock.setText(String.format(getString(R.string.genericProgress),
                    itemForSale.getStock(),
                    itemForSale.getDefaultStock()));

            TextView itemBuy = (TextView) itemRow.findViewById(R.id.itemBuy);
            itemBuy.setTag(itemForSale);
            itemBuy.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    clickBuy(v);
                }
            });

            traderItemsInfo.addView(inflatedView);
        }
    }

    private void clickBuy(View v) {
        Trader_Stock itemStock = (Trader_Stock) v.getTag();
        AlertDialogHelper.confirmItemBuy(getApplicationContext(), this, itemStock);
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Trader);
        startActivity(intent);
    }

    public void alertDialogCallback() {
        createItemList();
    }

    public void closeTrader(View view) {
        finish();
    }
}
