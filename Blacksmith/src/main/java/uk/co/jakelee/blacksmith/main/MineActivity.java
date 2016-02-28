package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

        TableLayout mineLayout = (TableLayout) findViewById(R.id.mineList);

        for (Shop shop : discoveredShops) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View inflatedView = inflater.inflate(R.layout.custom_shop_preview, null);
            TableRow shopRow = (TableRow) inflatedView.findViewById(R.id.shopRow);

            TextView shopName = (TextView) shopRow.findViewById(R.id.shopName);
            shopName.setText(shop.getName());

            TextView shopDescription = (TextView) shopRow.findViewById(R.id.shopDescription);
            shopDescription.setText(shop.getDescription());

            LinearLayout shopOfferingsContainer = (LinearLayout) shopRow.findViewById(R.id.shopOfferings);
            populateShopOfferings(shopOfferingsContainer, shop.getId());

            ImageView shopButton = (ImageView) shopRow.findViewById(R.id.shopButton);
            shopButton.setTag(shop.getId());
            shopButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ShopActivity.class);
                    intent.putExtra(SHOP_TO_LOAD, v.getTag().toString());
                    startActivity(intent);
                }
            });

            mineLayout.addView(inflatedView);

        }
    }

    public void populateShopOfferings(LinearLayout offeringsContainer, long shopID) {
        List<Shop_Stock> shopOfferings = Select.from(Shop_Stock.class).where(
                Condition.prop("shop_ID").eq(shopID)).list();

        for (Shop_Stock stock : shopOfferings) {
            ImageView itemImage = dh.createItemImage(stock.getItemID(), 100, 100, stock.getDiscovered());
            offeringsContainer.addView(itemImage);
        }
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.MINE);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
