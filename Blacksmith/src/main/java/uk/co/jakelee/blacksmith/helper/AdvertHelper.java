package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.applovin.adview.AppLovinIncentivizedInterstitial;
import com.applovin.adview.AppLovinInterstitialAd;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdRewardListener;
import com.applovin.sdk.AppLovinAdVideoPlaybackListener;
import com.applovin.sdk.AppLovinSdk;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.tapjoy.TJActionRequest;
import com.tapjoy.TJError;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.Tapjoy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.main.InterstitialActivity;
import uk.co.jakelee.blacksmith.main.MainActivity;
import uk.co.jakelee.blacksmith.main.MarketActivity;
import uk.co.jakelee.blacksmith.main.TraderActivity;
import uk.co.jakelee.blacksmith.main.VisitorActivity;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Super_Upgrade;
import uk.co.jakelee.blacksmith.model.Upgrade;

public class AdvertHelper implements AppLovinAdRewardListener, AppLovinAdDisplayListener, AppLovinAdVideoPlaybackListener, TJPlacementListener {
    public final static String INTENT_ID = "uk.co.jakelee.blacksmith.adverthelper";
    private static AdvertHelper dhInstance = null;
    private final Context context;
    private AppLovinIncentivizedInterstitial advert;
    private MainActivity mainActivity;
    private MarketActivity marketActivity;
    private VisitorActivity visitorActivity;
    private TraderActivity traderActivity;
    private boolean verified;
    private boolean tryingToLoad;
    private advertPurpose currentPurpose;
    public AdvertHelper(Context context) {
        this.context = context;

        Tapjoy.connect(context, "5RRfiBZDQ1igkbGMq000-gECphRBAD7rfoVwE7ZfGkZOWFqxNELMLHp9BVgk", null);

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

    public static String createAdvertReward(Context context) {
        int minimumRewards = Constants.MINIMUM_REWARDS;
        int maximumRewards = Constants.MAXIMUM_REWARDS;
        boolean rewardLegendary = Player_Info.isPremium() && VisitorHelper.getRandomBoolean(100 - Upgrade.getValue("Legendary Chance"));
        boolean rewardPage = VisitorHelper.getRandomBoolean(Super_Upgrade.isEnabled(Constants.SU_PAGE_CHANCE) ? 0 : 65); // 35% chance to get page

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
                    selectedItem.getName(context),
                    legendaryItem.getFullName(context, Constants.STATE_UNFINISHED));
        } else {
            rewardString = String.format(rewardString,
                    numberRewards,
                    selectedItem.getFullName(context, Constants.STATE_NORMAL));
        }

        // Append page earned if possible.
        if (rewardPage) {
            List<Item> pages = Select.from(Item.class).where(Condition.prop("type").eq(Constants.TYPE_PAGE)).list();
            Item rewardedPage = VisitorHelper.pickRandomItemFromList(pages);
            Inventory.addItem(rewardedPage.getId(), Constants.STATE_NORMAL, 1);

            rewardString += (" " + String.format(context.getString(R.string.advertWatchedPageSuffix),
                    rewardedPage.getName(context)));
        }

        return rewardString;
    }

    private static String getRewardString(Context context, boolean rewardLegendary) {
        List<String> strings = new ArrayList<>();
        boolean isPremium = Player_Info.isPremium();
        if (rewardLegendary && isPremium) {
            strings.add(context.getString(R.string.advertWatchedLegendaryPremium1));
            strings.add(context.getString(R.string.advertWatchedLegendaryPremium2));
        } else if (rewardLegendary) {
            strings.add(context.getString(R.string.advertWatchedLegendary1));
            strings.add(context.getString(R.string.advertWatchedLegendary2));
        } else if (isPremium) {
            strings.add(context.getString(R.string.advertWatchedPremium1));
            strings.add(context.getString(R.string.advertWatchedPremium2));
        } else {
            strings.add(context.getString(R.string.advertWatched1));
            strings.add(context.getString(R.string.advertWatched2));
        }
        int position = VisitorHelper.getRandomNumber(0, strings.size() - 1);
        return strings.get(position);
    }

    public void openInterstitial(advertPurpose purpose) {
        Intent intent = new Intent(context, InterstitialActivity.class);
        intent.putExtra(AdvertHelper.INTENT_ID, purpose.toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void showGenericAdvert(Activity activity, final advertPurpose purpose) {
        verified = false;
        tryingToLoad = true;
        currentPurpose = purpose;

        ToastHelper.showTipToast(null, ToastHelper.LONG, activity.getString(R.string.advert_load_start), false);

        if (advert.isAdReadyToDisplay()) {
            advert.show(activity, this, this, this);
        } else {
            if (MainActivity.adPlacement != null) {
               MainActivity.adPlacement.requestContent();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!verified && tryingToLoad) {
                            openInterstitial(purpose);
                        }
                    }
                }, 10000);
            }
        }

    }

    public void showAdvert(MainActivity activity, advertPurpose purpose) {
        mainActivity = activity;
        showGenericAdvert(activity, purpose);
    }

    public void showAdvert(TraderActivity activity, advertPurpose purpose) {
        traderActivity = activity;
        showGenericAdvert(activity, purpose);
    }

    public void showAdvert(MarketActivity activity, advertPurpose purpose) {
        marketActivity = activity;
        showGenericAdvert(activity, purpose);
    }

    public void showAdvert(VisitorActivity activity, advertPurpose purpose) {
        visitorActivity = activity;
        showGenericAdvert(activity, purpose);
    }

    private void tryReward() {
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
            ToastHelper.showErrorToast(null, ToastHelper.LONG, context.getString(R.string.error_ad_unverified), false);
        }
    }

    public void triggerCallback(advertPurpose purpose) {
        switch (purpose) {
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
    }


    @Override
    public void adHidden(AppLovinAd appLovinAd) {
        tryReward();
        advert.preload(null);
    }

    @Override
    public void userRewardVerified(AppLovinAd appLovinAd, Map map) {
        verified = true;
    }

    @Override public void videoPlaybackBegan(AppLovinAd appLovinAd) {}
    @Override public void videoPlaybackEnded(AppLovinAd appLovinAd, double v, boolean b) {}
    @Override public void adDisplayed(AppLovinAd appLovinAd) {
        tryingToLoad = false;
    }
    @Override public void userOverQuota(AppLovinAd appLovinAd, Map map) {}
    @Override public void userRewardRejected(AppLovinAd appLovinAd, Map map) {}
    @Override public void validationRequestFailed(AppLovinAd appLovinAd, int i) {}
    @Override public void userDeclinedToViewAd(AppLovinAd appLovinAd) {}

    public void onContentReady(TJPlacement placement) {
        tryingToLoad = false;
        placement.showContent();
    }
    public void onContentDismiss(TJPlacement placement) {
        verified = true;
        tryReward();
    }
    public void onPurchaseRequest(TJPlacement placement, TJActionRequest tjActionRequest, String string) {} // Called when the SDK has made contact with Tapjoy's servers. It does not necessarily mean that any content is available.
    public void onRewardRequest(TJPlacement placement, TJActionRequest tjActionRequest, String string, int number) {} // Called when the SDK has made contact with Tapjoy's servers. It does not necessarily mean that any content is available.
    public void onRequestSuccess(TJPlacement placement) {} // Called when the SDK has made contact with Tapjoy's servers. It does not necessarily mean that any content is available.
    public void onRequestFailure(TJPlacement placement, TJError error) {} // Called when there was a problem during connecting Tapjoy servers.
    public void onContentShow(TJPlacement placement) {
    } // Called when the content is showed.

    public enum advertPurpose {ConvMarketRestock, ConvTraderRestock, ConvVisitorDismiss, ConvVisitorSpawn, BonusBox}
}
