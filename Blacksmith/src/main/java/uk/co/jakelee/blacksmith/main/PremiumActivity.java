package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Collections;
import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.IabUtils.IabHelper;
import uk.co.jakelee.blacksmith.helper.IabUtils.IabResult;
import uk.co.jakelee.blacksmith.helper.IabUtils.Inventory;

public class PremiumActivity extends Activity {
    public static String SKU_PREMIUM = "premium";
    public static List<String> SKUs = Collections.singletonList(SKU_PREMIUM);
    public static DisplayHelper dh;
    public static IabHelper ih;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);

        String publicKey = getPublicKey();
        dh = DisplayHelper.getInstance(getApplicationContext());
        ih = new IabHelper(this, publicKey);

        final IabHelper.QueryInventoryFinishedListener mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory)
            {
                if (result.isFailure()) {
                    return;
                } else {
                    String premiumPrice = inventory.getSkuDetails(SKU_PREMIUM).getPrice();
                }
            }
        };

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
        if (ih != null) ih.dispose();
        ih = null;
    }

    private String getPublicKey() {
        return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlONTh/sroNhFQmK14kVk3H0bhmwJfcYpLdtZRSByLyuMlQO+yCY8EgjvLYhwXG+pSc8vZ5ib/y5DCu0aRVAsscYFcM9aAd8O5iTpwbgGqv5UyOMUNE5J3LDu9gSarDHdGOb/CsrfcPcW2WTHbiUCmkDevpFkV0sVInGZ5q0RMtDcOTcCgd5VLIeDa6VFo2Gv69VaHRQsjTu+4MEeGAa3FB47LIFUp/7jsHjB4vYt4bNH5CGTU9BL4pAwJXYif4dMa5r7fnCZQu0s5Kzis4dV919GEQ8BRMYZ+ZcyyCq8DiUjCc9exaszBqUxbCFROfeZ2NBDJPkRGHsgQU75yGolnwIDAQAB";
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Premium);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("IAB", "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!ih.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.i("IAB", "onActivityResult handled by IABUtil.");
        }
    }

    public void closePopup(View view) {
        finish();
    }
}
