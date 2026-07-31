package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Set;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.BillingHelper;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;

public class BuyCoinsActivity extends Activity implements BillingHelper.Listener {
    private static final String SKU_COIN_1 = BillingHelper.SKU_COIN_1;
    private static final String SKU_COIN_2 = BillingHelper.SKU_COIN_2;
    private static final String SKU_COIN_3 = BillingHelper.SKU_COIN_3;
    private static final int coinPackAmount1 = 500;
    private static final int coinPackAmount2 = 3000;
    private static final int coinPackAmount3 = 10000;
    BillingHelper billing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_coins);
        DisplayHelper dh = DisplayHelper.getInstance(getApplicationContext());
        dh.updateFullscreen(this);

        billing = new BillingHelper(this, this, SKU_COIN_1, SKU_COIN_2, SKU_COIN_3);

        updateCoinPackLabels();
    }

    @Override
    public void onBillingReady(Set<String> ownedProducts) {
        // Prices arrive asynchronously, so relabel once Play has told us what they actually are.
        updateCoinPackLabels();
    }

    private void updateCoinPackLabels() {
        int playerLevel = Player_Info.getPlayerLevel();
        setCoinPackLabel(R.id.coins1, playerLevel * coinPackAmount1, SKU_COIN_1, "$1.99");
        setCoinPackLabel(R.id.coins2, playerLevel * coinPackAmount2, SKU_COIN_2, "$4.99");
        setCoinPackLabel(R.id.coins3, playerLevel * coinPackAmount3, SKU_COIN_3, "$9.99");
    }

    private void setCoinPackLabel(int viewId, int coins, String sku, String fallbackPrice) {
        ((TextView) findViewById(viewId)).setText(String.format(getString(R.string.buyCoinsButton),
                NumberFormat.getIntegerInstance().format(coins),
                getPriceIfPossible(sku, fallbackPrice)));
    }

    private int getCoinsFromSku(String sku) {
        int level = Player_Info.getPlayerLevel();
        switch (sku) {
            case SKU_COIN_1:
                return coinPackAmount1 * level;
            case SKU_COIN_2:
                return coinPackAmount2 * level;
            case SKU_COIN_3:
                return coinPackAmount3 * level;
        }
        return 0;
    }

    @Override
    public void onProductPurchased(String productId) {
        int coinsPurchased = getCoinsFromSku(productId);
        if (coinsPurchased > 0) {
            Inventory.addItem(Constants.ITEM_COINS, Constants.STATE_NORMAL, coinsPurchased);
            Player_Info.increaseByX(Player_Info.Statistic.CoinsPurchased, coinsPurchased);
            ToastHelper.showToast(findViewById(R.id.buycoins), ToastHelper.LONG, String.format(getString(R.string.buyCoinsSuccess), coinsPurchased), true);
        }
    }

    @Override
    public void onBillingError() {
        ToastHelper.showToast(findViewById(R.id.buycoins), ToastHelper.LONG, getString(R.string.buyingIAPError), true);
    }

    public void buyCoins1(View v) {
        buyCoinPack(v, SKU_COIN_1);
    }

    public void buyCoins2(View v) {
        buyCoinPack(v, SKU_COIN_2);
    }

    public void buyCoins3(View v) {
        buyCoinPack(v, SKU_COIN_3);
    }

    private void buyCoinPack(View v, String sku) {
        if (billing.isReady()) {
            billing.purchase(sku);
        } else {
            ToastHelper.showToast(v, ToastHelper.LONG, getString(R.string.cannotBuyIAP), true);
        }
    }

    @Override
    public void onDestroy() {
        if (billing != null)
            billing.release();
        super.onDestroy();
    }

    /** The hardcoded fallbacks are US dollars, so they're only right until Play answers. */
    private String getPriceIfPossible(String iapName, String defaultPrice) {
        String price = billing == null ? null : billing.getFormattedPrice(iapName);
        return price == null ? defaultPrice : price;
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Coins);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
