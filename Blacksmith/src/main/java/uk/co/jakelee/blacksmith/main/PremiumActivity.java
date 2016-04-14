package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.Collections;
import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.IabUtils.IabHelper;
import uk.co.jakelee.blacksmith.helper.IabUtils.IabResult;
import uk.co.jakelee.blacksmith.helper.IabUtils.Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Upgrade;

public class PremiumActivity extends Activity {
    private static final String SKU_PREMIUM = "premium";
    private static final List<String> SKUs = Collections.singletonList(SKU_PREMIUM);
    private static DisplayHelper dh;
    private static IabHelper ih;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);
        dh = DisplayHelper.getInstance(getApplicationContext());

        // Create query listener
        final IabHelper.QueryInventoryFinishedListener mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                if (result.isFailure()) {
                    return;
                } else {
                    String premiumPrice = inventory.getSkuDetails(SKU_PREMIUM).getPrice();
                }
            }
        };

        // Define Iab Helper
        String publicKey = getPublicKey();
        ih = new IabHelper(this, publicKey);
        ih.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d("IAB", "Problem setting up In-app Billing: " + result);
                } else {
                    ih.queryInventoryAsync(true, SKUs, mQueryFinishedListener);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ih != null) {
            ih.dispose();
            ih = null;
        }
    }

    private String getPublicKey() {
        return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlONTh/sroNhFQmK14kVk3H0bhmwJfcYpLdtZRSByLyuMlQO+yCY8EgjvLYhwXG+pSc8vZ5ib/y5DCu0aRVAsscYFcM9aAd8O5iTpwbgGqv5UyOMUNE5J3LDu9gSarDHdGOb/CsrfcPcW2WTHbiUCmkDevpFkV0sVInGZ5q0RMtDcOTcCgd5VLIeDa6VFo2Gv69VaHRQsjTu+4MEeGAa3FB47LIFUp/7jsHjB4vYt4bNH5CGTU9BL4pAwJXYif4dMa5r7fnCZQu0s5Kzis4dV919GEQ8BRMYZ+ZcyyCq8DiUjCc9exaszBqUxbCFROfeZ2NBDJPkRGHsgQU75yGolnwIDAQAB";
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
        goldBonus.setCurrent(goldBonus.getCurrent() + 5);
        goldBonus.setMaximum(goldBonus.getMaximum() + 10);

        Upgrade xpBonus = Select.from(Upgrade.class).where(
                Condition.prop("name").eq("XP Bonus")).first();
        xpBonus.setCurrent(xpBonus.getCurrent() + 5);
        xpBonus.setMaximum(xpBonus.getMaximum() + 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("IAB", "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!ih.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.i("IAB", "onActivityResult handled by IABUtil.");
        }
    }

    public void closePopup(View view) {
        finish();
    }
}
