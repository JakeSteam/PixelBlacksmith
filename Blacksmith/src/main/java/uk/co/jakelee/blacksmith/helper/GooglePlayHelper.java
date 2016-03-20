package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.example.games.basegameutils.BaseGameUtils;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.main.MainActivity;

/**
 * Created by Jake on 20/03/2016.
 */
public class GooglePlayHelper {
    private static int RC_SIGN_IN = 9001;
    public static final int RESULT_OK = -1;
    private static boolean mResolvingConnectionFailure = false;
    private static boolean mAutoStartSignInFlow = true;
    private static boolean mSignInClicked = false;

    public static GoogleApiClient mGoogleApiClient;

    public static void ConnectionFailed(MainActivity activity, ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) { return; }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            if (!BaseGameUtils.resolveConnectionFailure(activity,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, "Couldn't log in. Please try again later.")) {
                mResolvingConnectionFailure = false;
            }
        }
    }

    public static void ActivityResult(Activity activity, int requestCode, int resultCode) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                BaseGameUtils.showActivityResultError(activity,
                        requestCode, resultCode, R.string.unknown_error);
            }
        }
    }
}
