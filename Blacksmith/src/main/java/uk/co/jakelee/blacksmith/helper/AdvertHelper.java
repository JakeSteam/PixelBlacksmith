package uk.co.jakelee.blacksmith.helper;

import android.content.Context;
import android.widget.Toast;

import com.applovin.adview.AppLovinIncentivizedInterstitial;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdRewardListener;
import com.applovin.sdk.AppLovinAdVideoPlaybackListener;
import com.applovin.sdk.AppLovinSdk;

import java.util.Map;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.main.MainActivity;
import uk.co.jakelee.blacksmith.main.MarketActivity;
import uk.co.jakelee.blacksmith.main.VisitorActivity;

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
}
