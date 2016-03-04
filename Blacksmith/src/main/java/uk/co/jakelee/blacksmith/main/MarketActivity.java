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
import android.widget.Toast;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
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

        List<Trader> newTraders = Trader.checkTraderStatus(Constants.LOCATION_MARKET);
        populateTraderList();

        for (Trader trader : newTraders) {
            ToastHelper.showToast(this, Toast.LENGTH_SHORT, "The " + trader.getName() + " trader has arrived.");
        }
    }

    public void populateTraderList() {
        TableLayout marketLayout = (TableLayout) findViewById(R.id.marketList);
        marketLayout.removeAllViews();

        List<Trader> traders = Select.from(Trader.class).where(
                Condition.prop("location").eq(Constants.LOCATION_MARKET),
                Condition.prop("arrival_time").gt(0)).list();

        for (Trader trader : traders) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View inflatedView = inflater.inflate(R.layout.custom_trader_preview, null);
            TableRow traderRow = (TableRow) inflatedView.findViewById(R.id.traderRow);

            TextView traderName = (TextView) traderRow.findViewById(R.id.traderName);
            traderName.setText(trader.getName());

            TextView traderDescription = (TextView) traderRow.findViewById(R.id.traderDescription);
            traderDescription.setText(trader.getDescription());

            LinearLayout traderOfferingsContainer = (LinearLayout) traderRow.findViewById(R.id.traderOfferings);
            populateTraderOfferings(traderOfferingsContainer, trader.getId());

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
    }

    public void populateTraderOfferings(LinearLayout offeringsContainer, long traderType) {
        List<Trader_Stock> traderOfferings = Select.from(Trader_Stock.class).where(
                Condition.prop("trader_type").eq(traderType)).list();

        for (Trader_Stock stock : traderOfferings) {
            ImageView itemImage = dh.createItemImage(stock.getItemID(), 100, 100, stock.getDiscovered());
            offeringsContainer.addView(itemImage);
        }
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.MARKET);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
