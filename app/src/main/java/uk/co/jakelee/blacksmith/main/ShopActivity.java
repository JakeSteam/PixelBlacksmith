package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
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

        // Creating items
        ImageView shopkeeperPic = dh.createCharacterImage(shopkeeper.getId(), 200, 200);
        shopkeeperPic.setId(R.id.shopkeeperPic);

        TextView shopkeeperName = dh.createTextView(shopkeeper.getName(), 20, Color.BLACK);
        shopkeeperName.setId(R.id.shopkeeperName);

        TextView shopkeeperIntro = dh.createTextView(shopkeeper.getIntro(), 16, Color.BLACK);
        shopkeeperIntro.setId(R.id.shopkeeperIntro);
        shopkeeperIntro.setSingleLine(false);

        // Creating layouts
        RelativeLayout.LayoutParams lpName = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpName.addRule(RelativeLayout.RIGHT_OF, shopkeeperPic.getId());

        RelativeLayout.LayoutParams lpIntro = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpIntro.addRule(RelativeLayout.BELOW, shopkeeperName.getId());
        lpIntro.addRule(RelativeLayout.RIGHT_OF, shopkeeperPic.getId());

        // Add items to layout
        RelativeLayout shopkeeperInfo = (RelativeLayout) findViewById(R.id.shopkeeperInfo);
        shopkeeperInfo.addView(shopkeeperPic);
        shopkeeperInfo.addView(shopkeeperName, lpName);
        shopkeeperInfo.addView(shopkeeperIntro, lpIntro);
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

            ImageView itemImage = dh.createItemImage(itemForSale.getItemID(), 30, 30, Constants.TRUE);
            TextView itemStock = dh.createTextView(itemForSale.getStock() + "x " + item.getName(), 16, Color.BLACK);
            TextView itemBuy = dh.createTextView(Integer.toString(item.getValue()), 18, Color.BLACK);
            itemBuy.setWidth(30);
            itemBuy.setShadowLayer(10, 0, 0, Color.WHITE);
            itemBuy.setGravity(Gravity.CENTER);
            itemBuy.setBackgroundResource(R.drawable.buy);
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

        if (Inventory.buyItem(itemToBuy.getId(), quantity, shop.getId(), itemToBuy.getValue())) {
            Toast.makeText(getApplicationContext(), String.format("Added %1sx %2s to pending buying for %3s coin(s)", 1, itemToBuy.getName(), itemToBuy.getValue()), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), String.format("Couldn't buy %1s", itemToBuy.getName()), Toast.LENGTH_SHORT).show();
        }
        createItemList();
    }

    public void closeShop(View view) {
        finish();
    }
}
