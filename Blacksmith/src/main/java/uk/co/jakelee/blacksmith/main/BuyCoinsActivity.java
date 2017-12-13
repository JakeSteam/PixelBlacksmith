package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;

import java.text.NumberFormat;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;

public class BuyCoinsActivity extends Activity implements BillingProcessor.IBillingHandler {
    private static final String SKU_COIN_1 = "coin_pack_1";
    private static final String SKU_COIN_2 = "coin_pack_2";
    private static final String SKU_COIN_3 = "coin_pack_3";
    private static final int coinPackAmount1 = 500;
    private static final int coinPackAmount2 = 3000;
    private static final int coinPackAmount3 = 10000;
    BillingProcessor bp;
    boolean canBuyIAPs = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_coins);
        DisplayHelper dh = DisplayHelper.getInstance(getApplicationContext());
        dh.updateFullscreen(this);

        canBuyIAPs = BillingProcessor.isIabServiceAvailable(this);
        if (canBuyIAPs) {
            bp = new BillingProcessor(this, getPublicKey(), this);
        }

        int playerLevel = Player_Info.getPlayerLevel();
        ((TextView) findViewById(R.id.coins1)).setText(String.format(getString(R.string.buyCoinsButton),
                NumberFormat.getIntegerInstance().format(playerLevel * coinPackAmount1),
                getPriceIfPossible(SKU_COIN_1, "$1.99")));
        ((TextView) findViewById(R.id.coins2)).setText(String.format(getString(R.string.buyCoinsButton), NumberFormat.getIntegerInstance().format(playerLevel * coinPackAmount2),
                getPriceIfPossible(SKU_COIN_2, "$4.99")));
        ((TextView) findViewById(R.id.coins3)).setText(String.format(getString(R.string.buyCoinsButton), NumberFormat.getIntegerInstance().format(playerLevel * coinPackAmount3),
                getPriceIfPossible(SKU_COIN_3, "$9.99")));
    }

    @Override
    public void onBillingInitialized() {
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
    public void onProductPurchased(String productId, TransactionDetails details) {
        int coinsPurchased = getCoinsFromSku(productId);
        if (coinsPurchased > 0) {
            bp.consumePurchase(productId);
            Inventory.addItem(Constants.ITEM_COINS, Constants.STATE_NORMAL, coinsPurchased);
            Player_Info.increaseByX(Player_Info.Statistic.CoinsPurchased, coinsPurchased);
            ToastHelper.showToast(findViewById(R.id.buycoins), ToastHelper.LONG, String.format(getString(R.string.buyCoinsSuccess), coinsPurchased), true);
        }
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        ToastHelper.showToast(findViewById(R.id.buycoins), ToastHelper.LONG, getString(R.string.buyingIAPError), true);
    }

    @Override
    public void onPurchaseHistoryRestored() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void buyCoins1(View v) {
        if (canBuyIAPs) {
            bp.purchase(this, SKU_COIN_1);
        } else {
            ToastHelper.showToast(v, ToastHelper.LONG, getString(R.string.cannotBuyIAP), true);
        }
    }

    public void buyCoins2(View v) {
        if (canBuyIAPs) {
            bp.purchase(this, SKU_COIN_2);
        } else {
            ToastHelper.showToast(v, ToastHelper.LONG, getString(R.string.cannotBuyIAP), true);
        }
    }

    public void buyCoins3(View v) {
        if (canBuyIAPs) {
            bp.purchase(this, SKU_COIN_3);
        } else {
            ToastHelper.showToast(v, ToastHelper.LONG, getString(R.string.cannotBuyIAP), true);
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();
        super.onDestroy();
    }

    private String getPublicKey() {
        String[] keyArray = new String[]{
                "MIIBIjANBgkqhki",
                "G9w0BAQEFAAOCAQ",
                "8AMIIBCgKCAQEAlONTh/sroNhFQ",
                "mK14kVk3H0bhmwJfcYpLdtZRSByL",
                "yuMlQO+yCY8EgjvLYhwXG+pSc8vZ5ib/y5DCu0aRVAsscYFcM9aAd8",
                "O5iTpwbgGqv5UyOMUNE5J3LDu9gSarDHdGOb/CsrfcPc",
                "W2WTHbiUCmkDevpFkV0sVInGZ5q0RMtDcOTcCgd5VLIeDa6VFo2Gv69Va",
                "HRQsjTu+4MEeGAa3FB47LIFUp/7jsHjB4vYt4bNH5CGTU",
                "9BL4pAwJXYif4dMa5r7fnCZQu0s5Kzis4dV919GEQ8BR",
                "MYZ+ZcyyCq8DiUjCc9exaszBqUxbCFROfeZ2NBDJPkRGHsgQU75yGolnwIDAQAB"
        };

        StringBuilder builder = new StringBuilder();
        for (String keyPart : keyArray) {
            builder.append(keyPart);
        }

        return builder.toString();
    }

    private String getPriceIfPossible(String iapName, String defaultPrice) {
        try {
            if (bp != null) {
                SkuDetails iapInfo = bp.getPurchaseListingDetails(iapName);
                if (iapInfo != null) {
                    return iapInfo.priceText;
                }
            }
        } catch (Exception e) {}
        return defaultPrice;
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
