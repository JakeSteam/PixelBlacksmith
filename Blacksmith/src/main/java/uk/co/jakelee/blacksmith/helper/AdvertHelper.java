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

import uk.co.jakelee.blacksmith.main.MainActivity;

public class AdvertHelper implements AppLovinAdRewardListener, AppLovinAdDisplayListener, AppLovinAdVideoPlaybackListener {
    public enum advertPurpose {ConvMarketRestock, ConvVisitorDismiss, ConvVisitorSpawn, ConvSkipTime, BonusBox};
    public AppLovinIncentivizedInterstitial advert;
    private Context context;
    private MainActivity mainActivity;
    private boolean verified;
    private advertPurpose currentPurpose;

    public AdvertHelper(Context context) {
        this.context = context;

        AppLovinSdk.initializeSdk(context);
        advert = AppLovinIncentivizedInterstitial.create(context);
        advert.preload(null);
    }

    public void showAdvert(MainActivity activity, advertPurpose purpose) {
        verified = false;
        mainActivity = activity;
        currentPurpose = purpose;
        advert.show(activity, this, this, this);
    }

    @Override
    public void adHidden(AppLovinAd appLovinAd) {
        if (verified) {
            switch (currentPurpose) {
                case ConvMarketRestock:
                    break;
                case ConvVisitorDismiss:
                    break;
                case ConvVisitorSpawn:
                    mainActivity.callbackSpawn();
                    break;
                case ConvSkipTime:
                    break;
                case BonusBox:
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
