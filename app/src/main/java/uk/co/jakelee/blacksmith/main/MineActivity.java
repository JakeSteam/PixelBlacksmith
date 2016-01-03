package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Shop;
import uk.co.jakelee.blacksmith.model.Shop_Stock;

public class MineActivity extends Activity {
    public static DisplayHelper dh;
    private static int mineLocationID = 3;
    public final static String SHOP_TO_LOAD = "uk.co.jakelee.blacksmith.shoptoload";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        dh = DisplayHelper.getInstance(getApplicationContext());

        createShopLists();
    }

    public void createShopLists() {
        List<Shop> discoveredShops = Shop.find(Shop.class, "discovered = 1 AND location = 3 AND level <= ?", Integer.toString(Player_Info.getPlayerLevel()));

        TableLayout mineList = (TableLayout) findViewById(R.id.mineList);
        mineList.setColumnStretchable(0, true);
        mineList.setColumnStretchable(1, false);
        mineList.removeAllViews();

        for (Shop shop : discoveredShops) {
            // Creating elements
            TextView shopName = dh.createTextView(shop.getName(), 20, Color.BLACK);
            shopName.setId(R.id.shopName);

            TextView shopDesc = dh.createTextView(shop.getDescription(), 14, Color.BLACK);
            shopDesc.setId(R.id.shopDesc);
            LinearLayout shopItems = createShopOfferings(shop);

            // Creating open shop button
            ImageView shopBtn = new ImageView(getApplicationContext());
            shopBtn.setTag(shop.getId());
            shopBtn.setBackgroundResource(R.drawable.open_shop);
            shopBtn.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ShopActivity.class);
                    intent.putExtra(SHOP_TO_LOAD, v.getTag().toString());
                    startActivity(intent);
                }
            });

            // Description modifiers
            RelativeLayout.LayoutParams lpDesc = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lpDesc.addRule(RelativeLayout.BELOW, shopName.getId());

            // Stock image modifiers
            RelativeLayout.LayoutParams lpImages = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lpImages.addRule(RelativeLayout.BELOW, shopDesc.getId());

            // Make left cell
            RelativeLayout leftLayout = new RelativeLayout(getApplicationContext());
            leftLayout.addView(shopName);
            leftLayout.addView(shopDesc, lpDesc);
            leftLayout.addView(shopItems, lpImages);

            // Make right cell
            LinearLayout rightLayout = new LinearLayout(getApplicationContext());
            rightLayout.addView(shopBtn);

            // Make shop row
            TableRow shopLayout = new TableRow(getApplicationContext());
            shopLayout.addView(leftLayout);
            shopLayout.addView(rightLayout);
            mineList.addView(shopLayout);
        }

    }

    public LinearLayout createShopOfferings(Shop shop) {
        LinearLayout offeringsLayout = new LinearLayout(getApplicationContext());
        List<Shop_Stock> shopOfferings = Shop_Stock.find(Shop_Stock.class, "shop_ID = ?", Long.toString(shop.getId()));

        for (Shop_Stock stock : shopOfferings) {
            ImageView itemImage = dh.createItemImage(stock.getItemID(), 50, 50, stock.getDiscovered());
            offeringsLayout.addView(itemImage);
        }
        return offeringsLayout;
    }

    public void closePopup(View view) {
        finish();
    }
}
