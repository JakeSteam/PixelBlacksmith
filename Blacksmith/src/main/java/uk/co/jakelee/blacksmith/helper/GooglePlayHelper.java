package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.main.MainActivity;
import uk.co.jakelee.blacksmith.model.Achievement;
import uk.co.jakelee.blacksmith.model.Player_Info;

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

    public static void UpdateAchievements() {
        if (!IsConnected()) { return;}

        List<Player_Info> statistics = Select.from(Player_Info.class).where(
                Condition.prop("last_sent_value").notEq(Constants.STATISTIC_NOT_TRACKED)).list();

        for (Player_Info statistic : statistics) {
            int currentValue = statistic.getIntValue();
            int lastSentValue = statistic.getLastSentValue();
            List<Achievement> achievements = Select.from(Achievement.class).where(
                    Condition.prop("player_info_id").eq(statistic.getId())).orderBy("maximum_value ASC").list();

            for (Achievement achievement : achievements) {
                boolean hasChanged = (currentValue > lastSentValue);
                boolean isAchieved = (achievement.getMaximumValue() <= lastSentValue);
                if (hasChanged && !isAchieved) {
                    int difference = currentValue - lastSentValue;
                    Games.Achievements.increment(mGoogleApiClient, achievement.getRemoteID(), difference);
                }
            }

            if (currentValue > lastSentValue) {
                statistic.setLastSentValue(currentValue);
                statistic.save();
            }
        }
    }

    public static boolean IsConnected() {
        return GooglePlayHelper.mGoogleApiClient != null && GooglePlayHelper.mGoogleApiClient.isConnected();
    }
}
