package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.model.Shop;

public class MineActivity extends Activity {
    public static DisplayHelper dh;
    private static int mineLocationID = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        dh = DisplayHelper.getInstance(getApplicationContext());

        createShopLists();
    }

    public void createShopLists() {
        List<Shop> discoveredShops = Shop.listAll(Shop.class);//dbh.getAllDiscoveredShops(mineLocationID);

        LinearLayout mineList = (LinearLayout) findViewById(R.id.mineList);
        mineList.removeAllViews();

        for (Shop shop : discoveredShops) {
            TextView shopName = dh.createTextView(shop.getName() + " (" + shop.getDescription() + ")", 16, Color.BLACK);
            mineList.addView(shopName);
        }

    }

    public void closePopup(View view) {
        finish();
    }
}
