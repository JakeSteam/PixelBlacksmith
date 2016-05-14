package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.applovin.adview.AppLovinIncentivizedInterstitial;
import com.applovin.sdk.AppLovinAd;
import com.applovin.sdk.AppLovinAdDisplayListener;
import com.applovin.sdk.AppLovinAdRewardListener;
import com.applovin.sdk.AppLovinSdk;

import java.util.Map;

public class AdvertHelper implements AppLovinAdRewardListener, AppLovinAdDisplayListener {
    private Context context;
    public AppLovinIncentivizedInterstitial myIncent;

    public AdvertHelper(Context context) {
        this.context = context;

        AppLovinSdk.initializeSdk(context);
        myIncent = AppLovinIncentivizedInterstitial.create(context);
        myIncent.preload(null);
    }

    public void showAdvert(Activity activity) {
        myIncent.show(activity, this, null, this);
    }

    @Override
    public void adHidden(AppLovinAd appLovinAd) {
        myIncent.preload(null);
    }

    @Override
    public void adDisplayed(AppLovinAd appLovinAd) {}

    @Override
    public void userRewardVerified(AppLovinAd appLovinAd, Map map) {
        ToastHelper.showPositiveToast(context, Toast.LENGTH_LONG, "Woo, you watched an ad!", false);
    }

    @Override
    public void userOverQuota(AppLovinAd appLovinAd, Map map) {
        ToastHelper.showErrorToast(context, Toast.LENGTH_LONG, "Oh no, over quota!", false);
    }

    @Override
    public void userRewardRejected(AppLovinAd appLovinAd, Map map) {}

    @Override
    public void validationRequestFailed(AppLovinAd appLovinAd, int i) {}

    @Override
    public void userDeclinedToViewAd(AppLovinAd appLovinAd) {}
}
