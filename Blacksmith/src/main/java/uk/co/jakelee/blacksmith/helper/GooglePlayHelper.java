package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.IOException;
import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.main.MainActivity;
import uk.co.jakelee.blacksmith.model.Achievement;
import uk.co.jakelee.blacksmith.model.Player_Info;

/**
 * Created by Jake on 20/03/2016.
 */
public class GooglePlayHelper {
    public static int RC_SIGN_IN = 9001;
    public static int RC_ACHIEVEMENTS = 9002;
    public static int RC_LEADERBOARDS = 9003;
    public static int RC_SAVED_GAMES = 9004;

    public static final int RESULT_OK = -1;
    private static boolean mResolvingConnectionFailure = false;
    private static boolean mAutoStartSignInFlow = true;
    private static boolean mSignInClicked = false;
    private static String mCurrentSaveName = "blacksmithCloudSave";
    private static byte[] mSaveGameData;
    private static byte[] dummyData = "Dummy data".getBytes();

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

    public static void ActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
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

    public static void UpdateLeaderboards(String leaderboardID, int value) {
        if (!IsConnected()) { return;}

        Games.Leaderboards.submitScore(mGoogleApiClient, leaderboardID, value);
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
                UpdateAchievement(mGoogleApiClient, achievement, currentValue, lastSentValue);
            }

            UpdateStatistic(statistic, currentValue, lastSentValue);
        }
    }

    public static void UpdateAchievement(GoogleApiClient mGoogleApiClient, Achievement achievement, int currentValue, int lastSentValue) {
        boolean hasChanged = (currentValue > lastSentValue);
        boolean isAchieved = (achievement.getMaximumValue() <= lastSentValue);
        if (hasChanged && !isAchieved) {
            int difference = currentValue - lastSentValue;
            if (achievement.getMaximumValue() == 1) {
                Games.Achievements.unlock(mGoogleApiClient, achievement.getRemoteID());
            } else {
                Games.Achievements.increment(mGoogleApiClient, achievement.getRemoteID(), difference);
            }
        }
    }

    public static void UpdateStatistic(Player_Info statistic, int currentValue, int lastSentValue) {
        if (currentValue > lastSentValue) {
            statistic.setLastSentValue(currentValue);
            statistic.save();
        }
    }

    public static void SavedGamesIntent(int requestCode, int resultCode, Intent intent) {
        AsyncTask<Void, Void, Integer> task = new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                Snapshots.OpenSnapshotResult result = Games.Snapshots.open(mGoogleApiClient, mCurrentSaveName, true).await();

                if (result.getStatus().isSuccess()) {
                    Snapshot snapshot = result.getSnapshot();
                    // Read the byte content of the saved game.
                    try {
                        mSaveGameData = snapshot.getSnapshotContents().readFully();
                        SaveToCloud(snapshot);
                    } catch (IOException e) {
                        Log.e("TEST", "Error while reading Snapshot.", e);
                    }
                } else{
                    Log.e("TEST", "Error while loading: " + result.getStatus().getStatusCode());
                }

                return result.getStatus().getStatusCode();
            }
        };

        task.execute();
    }

    private static PendingResult<Snapshots.CommitSnapshotResult> writeSnapshot(Snapshot snapshot) {

        // Set the data payload for the snapshot
        snapshot.getSnapshotContents().writeBytes(dummyData);

        // Create the change operation
        SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder()
                .setDescription("No idea")
                .build();

        // Commit the operation
        return Games.Snapshots.commitAndClose(mGoogleApiClient, snapshot, metadataChange);
    }

    public static void SaveToCloud(Snapshot snapshot) {
        if (!IsConnected()) { return;}

        byte[] data = dummyData;
        String desc = "Test description";

        snapshot.getSnapshotContents().writeBytes(data);

        SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder()
                .setDescription(desc)
                .build();

        // Commit the operation
        Games.Snapshots.commitAndClose(mGoogleApiClient, snapshot, metadataChange);
    }

    public static boolean IsConnected() {
        return GooglePlayHelper.mGoogleApiClient != null && GooglePlayHelper.mGoogleApiClient.isConnected();
    }
}
