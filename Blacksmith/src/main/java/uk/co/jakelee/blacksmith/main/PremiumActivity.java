package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.orm.query.Condition;
import com.orm.query.Select;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Upgrade;

public class PremiumActivity extends Activity implements BillingProcessor.IBillingHandler {
    BillingProcessor bp;
    boolean canBuyIAPs = false;
    private static final String SKU_PREMIUM = "premium";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);

        canBuyIAPs = BillingProcessor.isIabServiceAvailable(this);
        if (canBuyIAPs) {
            bp = new BillingProcessor(this, getPublicKey(), this);
        }
        updatePremiumStatus();
    }

    @Override
    public void onBillingInitialized() {
        if (bp.isPurchased(SKU_PREMIUM) && !Player_Info.isPremium()) {
            addPremiumFeatures();
            updatePremiumStatus();

            ToastHelper.showToast(this, Toast.LENGTH_LONG, R.string.restoredPremium);
            Log.d("Blacksmith", "IAP Engine initialised.");
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        if (productId.equals(SKU_PREMIUM)) {
            addPremiumFeatures();
            updatePremiumStatus();

            ToastHelper.showToast(this, Toast.LENGTH_LONG, R.string.boughtPremium);
            Log.d("Blacksmith", "Purchased premium!");
        }
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        ToastHelper.showToast(this, Toast.LENGTH_LONG, R.string.buyingPremiumError);
        Log.d("Blacksmith", "Error occurred, code: " + errorCode);
    }

    @Override
    public void onPurchaseHistoryRestored() {
        Log.d("IAB", "Purchases restored...");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void updatePremiumStatus() {
        TextViewPixel premiumIndicator = (TextViewPixel) findViewById(R.id.premiumStatusResult);
        TextViewPixel premiumButton = (TextViewPixel) findViewById(R.id.buyPremiumButton);
        boolean isPremium = Player_Info.isPremium();

        String premiumText = getString(isPremium ? R.string.premiumStatusActive : R.string.premiumStatusInactive);
        int premiumColour = getResources().getColor(isPremium ? R.color.holo_green_dark : R.color.holo_red_dark);
        int visibility = (isPremium ? View.GONE : View.VISIBLE);

        premiumIndicator.setText(premiumText);
        premiumIndicator.setTextColor(premiumColour);
        premiumButton.setVisibility(visibility);

    }

    public void buyPremium(View v) {
        if (canBuyIAPs) {
            bp.purchase(this, SKU_PREMIUM);
        } else {
            ToastHelper.showToast(this, Toast.LENGTH_LONG, R.string.cannotBuyPremium);
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
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Premium);
        startActivity(intent);
    }

    private void addPremiumFeatures() {
        // Update database, therefore slots + items
        Player_Info isPremium = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("Premium")).first();
        isPremium.setIntValue(1);
        isPremium.save();

        // Add upgrades, and max upgrades
        Upgrade goldBonus = Select.from(Upgrade.class).where(
                Condition.prop("name").eq("Gold Bonus")).first();
        goldBonus.setCurrent(goldBonus.getCurrent() + 20);
        goldBonus.setMaximum(goldBonus.getMaximum() + 50);

        Upgrade xpBonus = Select.from(Upgrade.class).where(
                Condition.prop("name").eq("XP Bonus")).first();
        xpBonus.setCurrent(xpBonus.getCurrent() + 20);
        xpBonus.setMaximum(xpBonus.getMaximum() + 50);
    }

    public void closePopup(View view) {
        finish();
    }
}
