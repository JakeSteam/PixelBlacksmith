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
import uk.co.jakelee.blacksmith.model.Shop;
import uk.co.jakelee.blacksmith.model.Shop_Stock;

public class ShopActivity extends Activity {
    public static DisplayHelper dh;
    public static Shop shop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        dh = DisplayHelper.getInstance(getApplicationContext());

        Intent intent = getIntent();
        int shopId = Integer.parseInt(intent.getStringExtra(MineActivity.SHOP_TO_LOAD));

        if (shopId > 0) {
            shop = Shop.findById(Shop.class, shopId);
            createShopInterface();
        }
    }

    public void createShopInterface() {
        createShopkeeper();
        createItemList();
    }

    public void createShopkeeper() {
        Character shopkeeper = Character.findById(Character.class, shop.getShopkeeper());

        int drawableID = getApplicationContext().getResources().getIdentifier("character" + shopkeeper.getId(), "drawable", getApplicationContext().getPackageName());
        ImageView shopkeeperImage = (ImageView) findViewById(R.id.shopkeeperImage);
        shopkeeperImage.setImageResource(drawableID);

        TextView shopkeeperName = (TextView) findViewById(R.id.shopkeeperName);
        shopkeeperName.setText(shopkeeper.getName());

        TextView shopkeeperGreeting = (TextView) findViewById(R.id.shopkeeperGreeting);
        shopkeeperGreeting.setText(shopkeeper.getIntro());
    }

    public void createItemList() {
        TableLayout shopItemsInfo = (TableLayout) findViewById(R.id.shopItemsInfo);
        shopItemsInfo.removeAllViews();
        List<Shop_Stock> itemsForSale = Select.from(Shop_Stock.class).where(
                Condition.prop("discovered").eq(Constants.TRUE),
                Condition.prop("shop_id").eq(shop.getId())).list();

        for (Shop_Stock itemForSale : itemsForSale) {
            TableRow itemRow = new TableRow(getApplicationContext());
            Item item = Item.findById(Item.class, itemForSale.getItemID());

            ImageView itemImage = dh.createItemImage(itemForSale.getItemID(), 100, 100, Constants.TRUE);
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

            shopItemsInfo.addView(itemRow);
        }
    }

    public void clickBuyButton(View v) {
        int quantity = 1;
        Item itemToBuy = Item.findById(Item.class, (Long) v.getTag());

        int buyResponse = Inventory.buyItem(itemToBuy.getId(), quantity, shop.getId(), itemToBuy.getValue());
        if (buyResponse == Constants.SUCCESS) {
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, String.format("Added %1sx %2s to pending buying for %3s coin(s)", 1, itemToBuy.getName(), itemToBuy.getValue()));
        } else {
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, ErrorHelper.errors.get(buyResponse));
        }
        createItemList();
    }

    public void closeShop(View view) {
        finish();
    }
}
