package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseInfo;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.GooglePlayHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.model.Contribution_Goal;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Slot;
import uk.co.jakelee.blacksmith.model.Super_Upgrade;
import uk.co.jakelee.blacksmith.model.Upgrade;

public class PremiumActivity extends Activity implements BillingProcessor.IBillingHandler {
    private static final String SKU_PREMIUM = "premium";
    private static final String SKU_CONTRIBUTE = "contribute";
    private static DisplayHelper dh;
    BillingProcessor bp;
    boolean canBuyIAPs = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);
        dh = DisplayHelper.getInstance(getApplicationContext());
        dh.updateFullscreen(this);

        canBuyIAPs = BillingProcessor.isIabServiceAvailable(this);
        if (canBuyIAPs) {
            bp = new BillingProcessor(this, getPublicKey(), this);
        }

        updatePremiumStatus();
        updateContributeStatus();
    }

    @Override
    public void onBillingInitialized() {
        if (bp.isPurchased(SKU_PREMIUM) && !Player_Info.isPremium()) {
            addPremiumFeatures();
            updatePremiumStatus();

            ToastHelper.showToast(findViewById(R.id.premium), ToastHelper.LONG, getString(R.string.restoredPremium), true);
        }
    }

    @Override
    public void onProductPurchased(String productId, PurchaseInfo details) {
        if (productId.equals(SKU_PREMIUM)) {
            addPremiumFeatures();
            updatePremiumStatus();

            ToastHelper.showToast(findViewById(R.id.premium), ToastHelper.LONG, getString(R.string.boughtPremium), true);
        } else if (productId.equals(SKU_CONTRIBUTE)) {
            bp.consumePurchaseAsync(SKU_CONTRIBUTE, null);
            addContributeFeatures();
            updateContributeStatus();
            ToastHelper.showToast(findViewById(R.id.premium), ToastHelper.LONG, getString(R.string.boughtContribute), true);
        }
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        ToastHelper.showToast(findViewById(R.id.premium), ToastHelper.LONG, getString(R.string.buyingIAPError), true);
    }

    @Override
    public void onPurchaseHistoryRestored() {
    }

    private void updatePremiumStatus() {
        boolean isPremium = Player_Info.isPremium();
        int visibility = (isPremium ? View.GONE : View.VISIBLE);
        findViewById(R.id.buyPremiumButton).setVisibility(visibility);
        findViewById(R.id.premiumFeatures).setVisibility(visibility);
    }

    private void updateContributeStatus() {
        boolean isPremium = Player_Info.isPremium();
        int visibility = (isPremium ? View.VISIBLE : View.GONE);
        TextViewPixel contributeDesc = (TextViewPixel) findViewById(R.id.contributeDescription);
        TextViewPixel contributeButton = (TextViewPixel) findViewById(R.id.contributeButton);

        contributeDesc.setVisibility(visibility);
        contributeButton.setVisibility(visibility);

        Player_Info timesDonated = Select.from(Player_Info.class).where(Condition.prop("name").eq("TimesDonated")).first();

        if (isPremium) {
            createContributeGoals(timesDonated.getIntValue());
        }
    }

    public void createContributeGoals(int timesContributed) {
        List<Contribution_Goal> contributions = Select.from(Contribution_Goal.class).orderBy("req_contributions ASC").list();
        LinearLayout container = (LinearLayout) findViewById(R.id.contributeContainer);
        container.removeAllViews();
        container.addView(dh.createTextView(getString(R.string.contributeHeader), 28));

        for (Contribution_Goal contribution : contributions) {
            boolean unlocked = timesContributed >= contribution.getReqContributions();
            TextView contributeTitle = dh.createTextView(String.format(getString(R.string.contributeTitle),
                    contribution.getName(this),
                    unlocked ? contribution.getReqContributions() : timesContributed,
                    contribution.getReqContributions()), 24);
            contributeTitle.setTextColor(unlocked ? Color.parseColor("#267c18") : Color.BLACK);
            contributeTitle.setPadding(0, 0, 15, 0);

            ProgressBar contributeProgress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            contributeProgress.getProgressDrawable().setColorFilter(0xFF267c18, android.graphics.PorterDuff.Mode.MULTIPLY);
            contributeProgress.setProgress(unlocked ? contribution.getReqContributions() : timesContributed);
            contributeProgress.setMax(contribution.getReqContributions());
            contributeProgress.setPadding(0, 0, 15, 0);

            TextView contributeBody = new TextViewPixel(this);
            contributeBody.setText(Html.fromHtml(unlocked ? contribution.getUnlockedText(this) : contribution.getTeaserText(this) + "<br><br>"));
            contributeBody.setTextSize(20);
            contributeBody.setMovementMethod(LinkMovementMethod.getInstance());
            contributeBody.setTextColor(Color.BLACK);
            contributeBody.setLinkTextColor(Color.BLUE);
            contributeBody.setLinksClickable(true);

            container.addView(contributeTitle);
            container.addView(contributeProgress);
            container.addView(contributeBody);
        }

        TextView contributeSummary = dh.createTextView(String.format(getString(R.string.contributeStatus),
                timesContributed,
                Player_Info.getLastContributed()), 22, Color.DKGRAY);
        container.addView(contributeSummary);
    }

    public void buyPremium(View v) {
        if (canBuyIAPs) {
            bp.purchase(this, SKU_PREMIUM);
        } else {
            ToastHelper.showToast(v, ToastHelper.LONG, getString(R.string.cannotBuyIAP), true);
        }
    }

    public void buyContribute(View v) {
        if (canBuyIAPs) {
            bp.purchase(this, SKU_CONTRIBUTE);
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
        Upgrade goldBonus = Select.from(Upgrade.class).where(Condition.prop("name").eq("Coins Bonus")).first();
        goldBonus.setCurrent(goldBonus.getCurrent() + 20);
        goldBonus.setMaximum(goldBonus.getMaximum() + 50);
        goldBonus.save();

        Upgrade xpBonus = Select.from(Upgrade.class).where(Condition.prop("name").eq("XP Bonus")).first();
        xpBonus.setCurrent(xpBonus.getCurrent() + 20);
        xpBonus.setMaximum(xpBonus.getMaximum() + 50);
        xpBonus.save();

        Slot.executeQuery("UPDATE slot SET premium = 0 WHERE level = 9999");

        MainActivity.needToRedrawSlots = true;
    }

    private void addContributeFeatures() {
        Player_Info timesDonated = Select.from(Player_Info.class).where(Condition.prop("name").eq("TimesDonated")).first();
        Player_Info lastDonated = Select.from(Player_Info.class).where(Condition.prop("name").eq("LastDonated")).first();

        if (timesDonated != null && lastDonated != null) {
            timesDonated.setIntValue(timesDonated.getIntValue() + 1);
            timesDonated.save();

            lastDonated.setTextValue(DateHelper.displayTime(System.currentTimeMillis(), DateHelper.date));
            lastDonated.save();
        }

        int contributeCoins = (Super_Upgrade.isEnabled(Constants.SU_CONTRIBUTIONS) ? 100 : 1) * Constants.CONTRIBUTE_GOLD;
        Inventory.addItem(Constants.ITEM_COINS, Constants.STATE_NORMAL, contributeCoins);

        GooglePlayHelper.UpdateEvent(Constants.EVENT_CONTRIBUTE, 1);
    }

    public void closePopup(View view) {
        finish();
    }
}
