package uk.co.jakelee.blacksmith.helper;

import android.content.Context;

import com.applovin.adview.AppLovinIncentivizedInterstitial;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdRewardListener;
import com.applovin.sdk.AppLovinAdVideoPlaybackListener;
import com.applovin.sdk.AppLovinSdk;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.main.MainActivity;
import uk.co.jakelee.blacksmith.main.MarketActivity;
import uk.co.jakelee.blacksmith.main.TraderActivity;
import uk.co.jakelee.blacksmith.main.VisitorActivity;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Upgrade;

public class AdvertHelper implements AppLovinAdRewardListener, AppLovinAdDisplayListener, AppLovinAdVideoPlaybackListener {
    public enum advertPurpose {ConvMarketRestock, ConvTraderRestock, ConvVisitorDismiss, ConvVisitorSpawn, BonusBox};
    public AppLovinIncentivizedInterstitial advert;
    private final Context context;
    private MainActivity mainActivity;
    private MarketActivity marketActivity;
    private VisitorActivity visitorActivity;
    private TraderActivity traderActivity;
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
            ToastHelper.showErrorToast(null, ToastHelper.LONG, R.string.adFailedToLoad, false);
        }
    }

    public void showAdvert(TraderActivity activity, advertPurpose purpose) {
        verified = false;
        traderActivity = activity;
        currentPurpose = purpose;

        if (advert.isAdReadyToDisplay()) {
            advert.show(activity, this, this, this);
        } else {
            ToastHelper.showErrorToast(activity.findViewById(R.id.trader), ToastHelper.LONG, R.string.adFailedToLoad, false);
        }
    }

    public void showAdvert(MarketActivity activity, advertPurpose purpose) {
        verified = false;
        marketActivity = activity;
        currentPurpose = purpose;

        if (advert.isAdReadyToDisplay()) {
            advert.show(activity, this, this, this);
        } else {
            ToastHelper.showErrorToast(activity.findViewById(R.id.marketTitle), ToastHelper.LONG, R.string.adFailedToLoad, false);
        }
    }

    public void showAdvert(VisitorActivity activity, advertPurpose purpose) {
        verified = false;
        visitorActivity = activity;
        currentPurpose = purpose;

        if (advert.isAdReadyToDisplay()) {
            advert.show(activity, this, this, this);
        } else {
            ToastHelper.showErrorToast(activity.findViewById(R.id.visitor), ToastHelper.LONG, R.string.adFailedToLoad, false);
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
                case ConvTraderRestock:
                    traderActivity.callbackRestock();
                    break;
                case BonusBox:
                    mainActivity.callbackBonus();
                    break;
            }
        } else {
            ToastHelper.showErrorToast(null, ToastHelper.LONG, "Something went wrong, and the ad view couldn't be verified. Sorry!", false);
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
        int minimumRewards = Constants.MINIMUM_REWARDS;
        int maximumRewards = Constants.MAXIMUM_REWARDS;
        boolean rewardLegendary = Player_Info.isPremium() && VisitorHelper.getRandomBoolean(100 - Upgrade.getValue("Legendary Chance"));
        boolean rewardPage = VisitorHelper.getRandomBoolean(65); // 35% chance to get page

        // 75% chance to get a normal (increased) reward, 25% chance to get coin amount.
        Item selectedItem = Item.findById(Item.class, Constants.ITEM_COINS);
        if (VisitorHelper.getRandomBoolean(25)) {
            int typeID = VisitorHelper.pickRandomNumberFromArray(Constants.VISITOR_REWARD_TYPES);
            List<Item> matchingItems = Select.from(Item.class).where(Condition.prop("type").eq(typeID)).list();
            selectedItem = VisitorHelper.pickRandomItemFromList(matchingItems);
        } else {
            minimumRewards = Constants.MINIMUM_COIN_REWARDS;
            maximumRewards = Constants.MAXIMUM_COIN_REWARDS;
        }

        int numberRewards = (Player_Info.isPremium() ? 2 : 1) * VisitorHelper.getRandomNumber(minimumRewards, maximumRewards);
        Inventory.addItem(selectedItem.getId(), Constants.STATE_NORMAL, numberRewards);
        String rewardString = getRewardString(context, rewardLegendary);

        // Create reward string, and legendary if necessary.
        if (rewardLegendary) {
            List<Item> legendaryItems = Select.from(Item.class).where(Condition.prop("tier").eq(Constants.TIER_PREMIUM)).list();
            Item legendaryItem = VisitorHelper.pickRandomItemFromList(legendaryItems);
            Inventory.addItem(legendaryItem.getId(), Constants.STATE_UNFINISHED, 1);
            rewardString = String.format(rewardString,
                    numberRewards,
                    selectedItem.getName(),
                    legendaryItem.getFullName(Constants.STATE_UNFINISHED));
        } else {
            rewardString = String.format(rewardString,
                    numberRewards,
                    selectedItem.getFullName(Constants.STATE_NORMAL));
        }

        // Append page earned if possible.
        if (rewardPage) {
            List<Item> pages = Select.from(Item.class).where(Condition.prop("type").eq(Constants.TYPE_PAGE)).list();
            Item rewardedPage = VisitorHelper.pickRandomItemFromList(pages);
            Inventory.addItem(rewardedPage.getId(), Constants.STATE_NORMAL, 1);

            rewardString += (" " + String.format(context.getString(R.string.advertWatchedPageSuffix),
                rewardedPage.getName()));
        }

        return rewardString;
    }

    private static String getRewardString(Context context, boolean rewardLegendary) {
        List<String> strings = new ArrayList<>();
        boolean isPremium = Player_Info.isPremium();
        if (rewardLegendary && isPremium) {
            strings.add(context.getString(R.string.advertWatchedLegendaryPremium1));
            strings.add(context.getString(R.string.advertWatchedLegendaryPremium2));
        } else if (rewardLegendary && !isPremium) {
            strings.add(context.getString(R.string.advertWatchedLegendary1));
            strings.add(context.getString(R.string.advertWatchedLegendary2));
        }else if (!rewardLegendary && isPremium) {
            strings.add(context.getString(R.string.advertWatchedPremium1));
            strings.add(context.getString(R.string.advertWatchedPremium2));
        }else if (!rewardLegendary && !isPremium) {
            strings.add(context.getString(R.string.advertWatched1));
            strings.add(context.getString(R.string.advertWatched2));
        }
        int position = VisitorHelper.getRandomNumber(0, strings.size() - 1);
        return strings.get(position);
    }
}
