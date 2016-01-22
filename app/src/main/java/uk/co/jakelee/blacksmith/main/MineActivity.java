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

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Shop;
import uk.co.jakelee.blacksmith.model.Shop_Stock;

public class MineActivity extends Activity {
    public static DisplayHelper dh;
    public final static String SHOP_TO_LOAD = "uk.co.jakelee.blacksmith.shoptoload";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        dh = DisplayHelper.getInstance(getApplicationContext());

        createShopLists();
    }

    public void createShopLists() {
        int playerLevel = Player_Info.getPlayerLevel();
        List<Shop> discoveredShops = Select.from(Shop.class).where(
                Condition.prop("discovered").eq(Constants.TRUE),
                Condition.prop("location").eq(Constants.LOCATION_SELLING),
                Condition.prop("level").lt(playerLevel + 1)).list();

        TableLayout mineList = (TableLayout) findViewById(R.id.mineList);
        mineList.setColumnStretchable(0, true);
        mineList.setColumnStretchable(1, false);
        mineList.removeAllViews();

        for (Shop shop : discoveredShops) {
            // Creating elements
            TextView shopName = dh.createTextView(getApplicationContext(), shop.getName(), 20, Color.BLACK);
            shopName.setId(R.id.shopName);

            TextView shopDesc = dh.createTextView(getApplicationContext(), shop.getDescription(), 14, Color.BLACK);
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
        List<Shop_Stock> shopOfferings = Select.from(Shop_Stock.class).where(
                Condition.prop("shop_ID").eq(shop.getId())).list();

        for (Shop_Stock stock : shopOfferings) {
            ImageView itemImage = dh.createItemImage(stock.getItemID(), 100, 100, stock.getDiscovered());
            offeringsLayout.addView(itemImage);
        }
        return offeringsLayout;
    }

    public void closePopup(View view) {
        finish();
    }
}
