package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.model.Character;
import uk.co.jakelee.blacksmith.model.Shop;

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
        shop = Shop.findById(Shop.class, shopId);

        createShopInterface();
    }

    public void createShopInterface() {
        createShopkeeper();
        createItemList();
    }

    public void createShopkeeper() {
        Character shopkeeper = Character.findById(Character.class, shop.getShopkeeper());

        // Creating items
        ImageView shopkeeperPic = new ImageView(getApplicationContext());
        shopkeeperPic.setImageResource(R.drawable.open_shop);
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
        
    }

    public void closeShop(View view) {
        finish();
    }
}
