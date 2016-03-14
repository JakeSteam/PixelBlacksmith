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
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.model.Trader;
import uk.co.jakelee.blacksmith.model.Trader_Stock;

public class MarketActivity extends Activity {
    public static DisplayHelper dh;
    public final static String TRADER_TO_LOAD = "uk.co.jakelee.blacksmith.tradertoload";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);
        dh = DisplayHelper.getInstance(getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();

        Trader.checkTraderStatus(this, Constants.LOCATION_MARKET);
        populateTraderList();
    }

    public void populateTraderList() {
        TableLayout marketLayout = (TableLayout) findViewById(R.id.marketList);
        marketLayout.removeAllViews();

        List<Trader> traders = Select.from(Trader.class).where(
                Condition.prop("location").eq(Constants.LOCATION_MARKET),
                Condition.prop("status").eq(Constants.TRADER_PRESENT)).list();

        for (Trader trader : traders) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View inflatedView = inflater.inflate(R.layout.custom_trader_preview, null);
            TableRow traderRow = (TableRow) inflatedView.findViewById(R.id.traderRow);

            TextView traderName = (TextView) traderRow.findViewById(R.id.traderName);
            traderName.setText(trader.getName());

            TextView traderDescription = (TextView) traderRow.findViewById(R.id.traderDescription);
            traderDescription.setText(trader.getDescription());

            LinearLayout traderOfferingsContainer = (LinearLayout) traderRow.findViewById(R.id.traderOfferings);
            populateTraderOfferings(traderOfferingsContainer, trader);

            ImageView traderButton = (ImageView) traderRow.findViewById(R.id.traderButton);
            traderButton.setTag(trader.getId());
            traderButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), TraderActivity.class);
                    intent.putExtra(TRADER_TO_LOAD, v.getTag().toString());
                    startActivity(intent);
                }
            });

            marketLayout.addView(inflatedView);

        }

        TextViewPixel noTradersMessage = (TextViewPixel) findViewById(R.id.noTradersMessage);
        noTradersMessage.setVisibility(traders.size() > 0 ? View.GONE : View.VISIBLE);
    }

    public void populateTraderOfferings(LinearLayout offeringsContainer, Trader trader) {
        List<Trader_Stock> traderOfferings = Select.from(Trader_Stock.class).where(
                Condition.prop("trader_type").eq(trader.getId())).list();

        for (Trader_Stock stock : traderOfferings) {
            boolean isUnlocked = trader.getPurchases() >= stock.getRequiredPurchases();
            ImageView itemImage = dh.createItemImage(stock.getItemID(), 100, 100, isUnlocked);
            offeringsContainer.addView(itemImage);
        }
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.MARKET);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
