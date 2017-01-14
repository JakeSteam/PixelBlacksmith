package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import java.text.NumberFormat;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;

public class BuyCoinsActivity extends Activity implements BillingProcessor.IBillingHandler {
    private static DisplayHelper dh;
    BillingProcessor bp;
    boolean canBuyIAPs = false;
    private static final String SKU_COIN_1000 = "coin_pack_1";
    private static final String SKU_COIN_5000 = "coin_pack_2";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_coins);
        dh = DisplayHelper.getInstance(getApplicationContext());
        dh.updateFullscreen(this);

        canBuyIAPs = BillingProcessor.isIabServiceAvailable(this);
        if (canBuyIAPs) {
            bp = new BillingProcessor(this, getPublicKey(), this);
        }

        int playerLevel = Player_Info.getPlayerLevel();
        ((TextView)findViewById(R.id.coins1000)).setText(String.format(getString(R.string.buyCoinsButton), NumberFormat.getIntegerInstance().format(playerLevel * 1000)));
        ((TextView)findViewById(R.id.coins10000)).setText(String.format(getString(R.string.buyCoinsButton), NumberFormat.getIntegerInstance().format(playerLevel * 10000)));
    }

    @Override
    public void onBillingInitialized() {
    }

    private int getCoinsFromSku(String sku) {
        int level = Player_Info.getPlayerLevel();
        if (sku.equals(SKU_COIN_1000)) {
            return 1000 * level;
        } else if (sku.equals(SKU_COIN_5000)) {
            return 5000 * level;
        }
        return 0;
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        int coinsPurchased = getCoinsFromSku(productId);
        if (coinsPurchased > 0) {
            bp.consumePurchase(productId);
            Inventory.addItem(Constants.ITEM_COINS, Constants.STATE_NORMAL, coinsPurchased);
            ToastHelper.showToast(findViewById(R.id.buycoins), ToastHelper.LONG, "Yay, coins purchased!", true);
        }
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        ToastHelper.showToast(findViewById(R.id.buycoins), ToastHelper.LONG, getString(R.string.buyingIAPError), true);
    }

    @Override
    public void onPurchaseHistoryRestored() {}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void buy1000(View v) {
        if (canBuyIAPs) {
            bp.purchase(this, SKU_COIN_1000);
        } else {
            ToastHelper.showToast(v, ToastHelper.LONG, getString(R.string.cannotBuyIAP), true);
        }
    }

    public void buy10000(View v) {
        if (canBuyIAPs) {
            bp.purchase(this, SKU_COIN_5000);
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
        String[] keyArray = new String[] {
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

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Coins);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
