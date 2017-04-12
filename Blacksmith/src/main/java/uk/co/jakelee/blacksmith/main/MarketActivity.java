package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;
import java.util.Locale;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.helper.TutorialHelper;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Super_Upgrade;
import uk.co.jakelee.blacksmith.model.Trader;
import uk.co.jakelee.blacksmith.model.Trader_Stock;

public class MarketActivity extends Activity {
    public final static String TRADER_TO_LOAD = "uk.co.jakelee.blacksmith.tradertoload";
    private static DisplayHelper dh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);
        dh = DisplayHelper.getInstance(getApplicationContext());
        dh.updateFullscreen(this);

        if (TutorialHelper.currentlyInTutorial && TutorialHelper.currentStage <= Constants.STAGE_14_MARKET) {
            startTutorial();
        }
    }

    private void startTutorial() {
        TutorialHelper th = new TutorialHelper(this, Constants.STAGE_14_MARKET);
        th.addTutorialRectangle(findViewById(R.id.marketList), R.string.tutorialMarket, R.string.tutorialMarketText, false, Gravity.BOTTOM);
        th.addTutorial(findViewById(R.id.close), R.string.tutorialMarketClose, R.string.tutorialMarketCloseText, true);
        th.start();
    }

    @Override
    public void onResume() {
        super.onResume();

        populateTraderList();
        findViewById(R.id.buyAllButton).setVisibility(Super_Upgrade.isEnabled(Constants.SU_BUY_ALL_MARKET) ? View.VISIBLE : View.GONE);
    }

    public void buyAll(View v) {
        AlertDialogHelper.confirmMarketBuyAll(this);
    }

    private void populateTraderList() {
        TableLayout marketLayout = (TableLayout) findViewById(R.id.marketList);
        Trader.checkTraderStatus(this, marketLayout, Constants.LOCATION_MARKET);
        marketLayout.removeAllViews();

        int fixedTraders = Trader.getFixedCount();
        List<Trader> traders = Trader.find(Trader.class, String.format(Locale.ENGLISH,
                "location = %1$d AND (status = %2$d OR fixed = %3$d) ORDER BY fixed DESC, name ASC",
                Constants.LOCATION_MARKET,
                Constants.TRADER_PRESENT,
                Constants.TRUE));

        boolean mixedFixedStatus = fixedTraders > 0;
        boolean haveDisplayedUnlockHeader = false;
        if (mixedFixedStatus) {
            marketLayout.addView(dh.createTextView(getString(R.string.fixed) + String.format(" (%1$d / %2$d)",
                    fixedTraders,
                    Constants.TRADER_LOCK_MAX),
                    30));
        }

        for (Trader trader : traders) {
            if (mixedFixedStatus && !trader.isFixed() && !haveDisplayedUnlockHeader) {
                marketLayout.addView(dh.createTextView("\n" + getString(R.string.temporary), 30));
                haveDisplayedUnlockHeader = true;
            }
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View inflatedView = inflater.inflate(R.layout.custom_trader_preview, null);
            TableRow traderRow = (TableRow) inflatedView.findViewById(R.id.traderRow);

            TextView traderName = (TextView) traderRow.findViewById(R.id.traderName);
            traderName.setText(trader.getName(this));

            TextView traderDescription = (TextView) traderRow.findViewById(R.id.traderDescription);
            traderDescription.setText(trader.getDescription(this));

            LinearLayout traderOfferingsContainer = (LinearLayout) traderRow.findViewById(R.id.traderOfferings);
            populateTraderOfferings(traderOfferingsContainer, trader);

            inflatedView.setTag(trader.getId());
            inflatedView.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - MainActivity.vh.lastTraderClick < 500) {
                        return;
                    } else {
                        MainActivity.vh.lastTraderClick = SystemClock.elapsedRealtime();
                    }
                    Intent intent = new Intent(getApplicationContext(), TraderActivity.class);
                    intent.putExtra(TRADER_TO_LOAD, v.getTag().toString());
                    startActivity(intent);
                }
            });
            marketLayout.addView(inflatedView);

        }
        checkIfOutOfStock(traders.size());

    }

    private void checkIfOutOfStock(int traders) {
        ScrollView emptyContainer = (ScrollView) findViewById(R.id.emptyMarket);
        if (traders > 0) {
            emptyContainer.setVisibility(View.GONE);
        } else {
            emptyContainer.setVisibility(View.VISIBLE);

            TextView restockText = (TextView) findViewById(R.id.restockText);
            int stringID = Player_Info.displayAds() ? R.string.restockMarketTextAdvert : R.string.restockMarketText;
            restockText.setText(String.format(getString(stringID),
                    Trader_Stock.getRestockTimeLeft(),
                    Trader.getRestockAllCost()));
        }
    }

    private void populateTraderOfferings(LinearLayout offeringsContainer, Trader trader) {
        List<Trader_Stock> traderOfferings = Select.from(Trader_Stock.class).where(
                Condition.prop("trader_type").eq(trader.getId()))
                .orderBy("required_purchases ASC").list();

        for (Trader_Stock stock : traderOfferings) {
            boolean isUnlocked = trader.getPurchases() >= stock.getRequiredPurchases();
            ImageView itemImage = dh.createItemImage(stock.getItemID(), stock.getState(), 20, 20, isUnlocked, false);
            offeringsContainer.addView(itemImage);
        }
    }

    public void alertDialogCallback() {
        populateTraderList();
    }

    public void callbackRestock() {
        Trader.restockAll(0);
        ToastHelper.showToast(findViewById(R.id.marketTitle), ToastHelper.LONG, getString(R.string.traderRestockAllCompleteAdvert), true);
        populateTraderList();
    }

    public void clickRestockAll(View view) {
        if (Super_Upgrade.isEnabled(Constants.SU_MARKET_RESTOCK)) {
            callbackRestock();
        } else {
            int restockCost = Trader.getRestockAllCost();
            AlertDialogHelper.confirmTraderRestockAll(getApplicationContext(), this, restockCost);
        }
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Market);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
