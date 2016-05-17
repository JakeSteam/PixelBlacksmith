package uk.co.jakelee.blacksmith.helper;

import android.content.Context;
import android.widget.Toast;

import com.applovin.adview.AppLovinIncentivizedInterstitial;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdRewardListener;
import com.applovin.sdk.AppLovinAdVideoPlaybackListener;
import com.applovin.sdk.AppLovinSdk;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;
import java.util.Map;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.main.MainActivity;
import uk.co.jakelee.blacksmith.main.MarketActivity;
import uk.co.jakelee.blacksmith.main.VisitorActivity;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Upgrade;

public class AdvertHelper implements AppLovinAdRewardListener, AppLovinAdDisplayListener, AppLovinAdVideoPlaybackListener {
    public enum advertPurpose {ConvMarketRestock, ConvVisitorDismiss, ConvVisitorSpawn, BonusBox};
    public AppLovinIncentivizedInterstitial advert;
    private final Context context;
    private MainActivity mainActivity;
    private MarketActivity marketActivity;
    private VisitorActivity visitorActivity;
    private boolean verified;
    private advertPurpose currentPurpose;
    private static AdvertHelper dhInstance = null;

    public AdvertHelper(Context context) {
        this.context = context;

        AppLovinSdk.initializeSdk(context);
        advert = AppLovinIncentivizedInterstitial.create(context);
        advert.preload(null);
    }

    public static AdvertHelper getInstance(Context ctx) {
        if (dhInstance == null) {
            dhInstance = new AdvertHelper(ctx.getApplicationContext());
        }
        return dhInstance;
    }

    public void showAdvert(MainActivity activity, advertPurpose purpose) {
        verified = false;
        mainActivity = activity;
        currentPurpose = purpose;

        if (advert.isAdReadyToDisplay()) {
            advert.show(activity, this, this, this);
        } else {
            ToastHelper.showErrorToast(activity, Toast.LENGTH_LONG, R.string.adFailedToLoad, false);
        }
    }

    public void showAdvert(MarketActivity activity, advertPurpose purpose) {
        verified = false;
        marketActivity = activity;
        currentPurpose = purpose;

        if (advert.isAdReadyToDisplay()) {
            advert.show(activity, this, this, this);
        } else {
            ToastHelper.showErrorToast(activity, Toast.LENGTH_LONG, R.string.adFailedToLoad, false);
        }
    }

    public void showAdvert(VisitorActivity activity, advertPurpose purpose) {
        verified = false;
        visitorActivity = activity;
        currentPurpose = purpose;

        if (advert.isAdReadyToDisplay()) {
            advert.show(activity, this, this, this);
        } else {
            ToastHelper.showErrorToast(activity, Toast.LENGTH_LONG, R.string.adFailedToLoad, false);
        }
    }

    @Override
    public void adHidden(AppLovinAd appLovinAd) {
        if (verified) {
            switch (currentPurpose) {
                case ConvMarketRestock:
                    marketActivity.callbackRestock();
                    break;
                case ConvVisitorDismiss:
                    visitorActivity.callbackDismiss();
                    break;
                case ConvVisitorSpawn:
                    mainActivity.callbackSpawn();
                    break;
                case BonusBox:
                    mainActivity.callbackBonus();
                    break;
            }
        } else {
            ToastHelper.showErrorToast(context, Toast.LENGTH_LONG, "Something went wrong, and the ad view couldn't be verified. Sorry!", false);
        }
        // Begin loading next advert.
        advert.preload(null);
    }

    @Override
    public void userRewardVerified(AppLovinAd appLovinAd, Map map) {
        verified = true;
    }

    @Override public void videoPlaybackBegan(AppLovinAd appLovinAd) {}
    @Override public void videoPlaybackEnded(AppLovinAd appLovinAd, double v, boolean b) {}
    @Override public void adDisplayed(AppLovinAd appLovinAd) {}
    @Override public void userOverQuota(AppLovinAd appLovinAd, Map map) {}
    @Override public void userRewardRejected(AppLovinAd appLovinAd, Map map) {}
    @Override public void validationRequestFailed(AppLovinAd appLovinAd, int i) {}
    @Override public void userDeclinedToViewAd(AppLovinAd appLovinAd) {}

    public static String createAdvertReward(Context context) {
        int minimumRewards = 4;
        int maximumRewards = 8;
        boolean rewardLegendary = Player_Info.isPremium() && VisitorHelper.getRandomBoolean(100 - Upgrade.getValue("Legendary Chance"));

        // 75% chance to get a normal (increased) reward, 25% chance to get coin amount.
        Item selectedItem = Item.findById(Item.class, Constants.ITEM_COINS);
        if (VisitorHelper.getRandomBoolean(75)) {
            int typeID = VisitorHelper.pickRandomNumberFromArray(Constants.VISITOR_REWARD_TYPES);
            List<Item> matchingItems = Select.from(Item.class).where(Condition.prop("type").eq(typeID)).list();
            selectedItem = VisitorHelper.pickRandomItemFromList(matchingItems);
        } else {
            minimumRewards = 100;
            maximumRewards = 1100;
        }

        int numberRewards = (Player_Info.isPremium() ? 2 : 1) * VisitorHelper.getRandomNumber(minimumRewards, maximumRewards);
        Inventory.addItem(selectedItem.getId(), Constants.STATE_NORMAL, numberRewards);
        String rewardString = VisitorHelper.getRewardString(context, rewardLegendary, Player_Info.isPremium());

        if (rewardLegendary) {
            List<Item> premiumItems = Select.from(Item.class).where(Condition.prop("tier").eq(Constants.TIER_PREMIUM)).list();
            Item premiumItem = VisitorHelper.pickRandomItemFromList(premiumItems);
            Inventory.addItem(premiumItem.getId(), Constants.STATE_UNFINISHED, 1);
            return String.format(rewardString,
                    numberRewards,
                    selectedItem.getName(),
                    premiumItem.getFullName(Constants.STATE_UNFINISHED));
        } else {
            return String.format(rewardString,
                    numberRewards,
                    selectedItem.getFullName(Constants.STATE_NORMAL));
        }
    }
}