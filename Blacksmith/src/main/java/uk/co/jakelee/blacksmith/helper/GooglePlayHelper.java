package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.quest.Quest;
import com.google.android.gms.games.quest.QuestBuffer;
import com.google.android.gms.games.quest.Quests;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.google.gson.Gson;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.model.Achievement;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Setting;
import uk.co.jakelee.blacksmith.model.Trader;
import uk.co.jakelee.blacksmith.model.Upgrade;
import uk.co.jakelee.blacksmith.model.Visitor;
import uk.co.jakelee.blacksmith.model.Visitor_Demand;
import uk.co.jakelee.blacksmith.model.Visitor_Stats;
import uk.co.jakelee.blacksmith.model.Visitor_Type;
import uk.co.jakelee.blacksmith.model.Worker;

public class GooglePlayHelper implements com.google.android.gms.common.api.ResultCallback {
    public static final int RC_ACHIEVEMENTS = 9002;
    public static final int RC_LEADERBOARDS = 9003;
    public static final int RC_SAVED_GAMES = 9004;
    public static final int RC_QUESTS = 9005;
    private static final int RESULT_OK = -1;
    private static final int RC_SIGN_IN = 9001;
    private static final String SAVE_DELIMITER = "UNIQUEDELIMITINGSTRING";
    private static final String mCurrentSaveName = "blacksmithCloudSave";
    public static GoogleApiClient mGoogleApiClient;
    private static boolean mResolvingConnectionFailure = false;
    private static byte[] cloudSaveData;
    private static Context callingContext;
    private static Activity callingActivity;

    public static void ConnectionFailed(Activity activity, ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            return;
        }

        mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(activity,
                mGoogleApiClient, connectionResult,
                RC_SIGN_IN, activity.getString(R.string.connectionFailure));
    }

    public static void ActivityResult(Activity activity, int requestCode, int resultCode) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                Setting signIn = Setting.findById(Setting.class, Constants.SETTING_SIGN_IN);
                signIn.setBoolValue(false);
                signIn.save();
            }
        }
    }

    public static String CompleteQuest(Quest quest) {
        Games.Quests.claim(mGoogleApiClient, quest.getQuestId(),
                quest.getCurrentMilestone().getMilestoneId());
        Context context = mGoogleApiClient.getContext();

        String questName = quest.getName();
        String questDifficulty = new String(quest.getCurrentMilestone().getCompletionRewardData(), Charset.forName("UTF-8"));
        String questReward = QuestHelper.getQuestReward(context, questDifficulty);

        Player_Info.increaseByOne(Player_Info.Statistic.QuestsCompleted);
        return String.format(context.getString(R.string.questComplete),
                questName,
                questDifficulty,
                questReward);
    }

    public void onResult(com.google.android.gms.common.api.Result result) {
        Quests.LoadQuestsResult r = (Quests.LoadQuestsResult)result;
        QuestBuffer qb = r.getQuests();

        int current = 0;
        int max = 1;
        String event = "";
        if (qb.getCount() > 0) {
            Quest q = qb.get(0);
            current = (int) q.getCurrentMilestone().getCurrentProgress();
            max = (int) q.getCurrentMilestone().getTargetProgress();
            event = q.getCurrentMilestone().getEventId();
        }

        DisplayHelper.updateQuest(current, max, event);
        qb.close();
    }

    public void UpdateQuest() {
        PendingResult<Quests.LoadQuestsResult> quests = Games.Quests.load(mGoogleApiClient, new int[] {Quest.STATE_ACCEPTED}, Quests.SORT_ORDER_ENDING_SOON_FIRST, false);
        quests.setResultCallback(this);
    }

    public static void UpdateEvent(String eventId, int quantity) {
        Games.Events.increment(mGoogleApiClient, eventId, quantity);
    }

    public static void UpdateLeaderboards(String leaderboardID, int value) {
        if (!IsConnected()) {
            return;
        }

        Games.Leaderboards.submitScore(mGoogleApiClient, leaderboardID, value);
    }

    public static void UpdateAchievements() {
        if (!IsConnected()) {
            return;
        }

        List<Player_Info> statistics = Select.from(Player_Info.class).where(
                Condition.prop("last_sent_value").notEq(Constants.STATISTIC_NOT_TRACKED)).list();

        for (Player_Info statistic : statistics) {
            int currentValue = statistic.getIntValue();
            int lastSentValue = statistic.getLastSentValue();
            List<Achievement> achievements = Select.from(Achievement.class).where(
                    Condition.prop("player_info_id").eq(statistic.getId())).orderBy("maximum_value ASC").list();

            for (Achievement achievement : achievements) {
                UpdateAchievement(achievement, currentValue, lastSentValue);
            }

            UpdateStatistic(statistic, currentValue, lastSentValue);
        }
    }

    private static void UpdateAchievement(Achievement achievement, int currentValue, int lastSentValue) {
        boolean hasChanged = (currentValue > lastSentValue);
        boolean isAchieved = (achievement.getMaximumValue() <= lastSentValue);
        if (hasChanged && !isAchieved && mGoogleApiClient.isConnected()) {
            int difference = currentValue - lastSentValue;
            if (achievement.getMaximumValue() == 1 || achievement.getPlayerInfoID() == 17) {
                Games.Achievements.unlock(mGoogleApiClient, achievement.getRemoteID());
            } else {
                Games.Achievements.increment(mGoogleApiClient, achievement.getRemoteID(), difference);
            }
        }
    }

    private static void UpdateStatistic(Player_Info statistic, int currentValue, int lastSentValue) {
        if (currentValue > lastSentValue && mGoogleApiClient.isConnected()) {
            statistic.setLastSentValue(currentValue);
            statistic.save();
        }
    }

    public static void SavedGamesIntent(final Context context, final Activity activity, final Intent intent) {
        if (intent == null || !mGoogleApiClient.isConnected()) {
            return;
        }
        callingContext = context;
        callingActivity = activity;

        AsyncTask<Void, Void, Integer> task = new AsyncTask<Void, Void, Integer>() {
            String currentTask = "synchronising";

            @Override
            protected Integer doInBackground(Void... params) {
                Snapshots.OpenSnapshotResult result = Games.Snapshots.open(mGoogleApiClient, mCurrentSaveName, true).await();

                if (result.getStatus().isSuccess()) {
                    Snapshot snapshot = result.getSnapshot();
                    try {
                        if (intent.hasExtra(Snapshots.EXTRA_SNAPSHOT_METADATA)) {
                            cloudSaveData = snapshot.getSnapshotContents().readFully();
                            loadFromCloud(true);
                            currentTask = "loading";
                        } else if (intent.hasExtra(Snapshots.EXTRA_SNAPSHOT_NEW)) {
                            saveToCloud(context, snapshot);
                            currentTask = "saving";
                        }
                    } catch (IOException e) {
                        ToastHelper.showErrorToast(context, Toast.LENGTH_SHORT, String.format(context.getString(R.string.cloudLocalFailure), currentTask, e.toString()), true);
                    }
                } else {
                    ToastHelper.showErrorToast(context, Toast.LENGTH_SHORT, String.format(context.getString(R.string.cloudRemoteFailure), currentTask, result.getStatus().getStatusCode()), true);
                }

                return result.getStatus().getStatusCode();
            }
        };

        task.execute();
    }

    private static void loadFromCloud(final boolean checkIsImprovement) {
        if (!IsConnected()) {
            return;
        }

        callingActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!checkIsImprovement) {
                    ToastHelper.showToast(callingActivity, Toast.LENGTH_LONG, R.string.cloudLoadBeginning, false);
                }
            }
        });

        Pair<Integer, Integer> cloudData = getPrestigeAndXPFromSave(cloudSaveData);

        if (!checkIsImprovement || cloudSaveIsBetter(cloudData)) {
            applyBackup(new String(cloudSaveData));
        } else {
            AlertDialogHelper.confirmWorseCloudLoad(callingContext, callingActivity,
                    Player_Info.getPrestige(),
                    Player_Info.getXp(),
                    cloudData.first,
                    cloudData.second);
        }
    }

    public static void forceLoadFromCloud() {
        new Thread(new Runnable() {
            public void run() {
                loadFromCloud(false);
            }
        }).start();
    }

    private static void saveToCloud(Context context, Snapshot snapshot) {
        if (!IsConnected()) {
            return;
        }

        byte[] data = createBackup();
        String desc = String.format(context.getString(R.string.cloudSaveCaption), Player_Info.getPlayerLevel(), Inventory.getCoins());
        Bitmap cover = BitmapFactory.decodeResource(context.getResources(), R.drawable.wallpaper);

        snapshot.getSnapshotContents().writeBytes(data);

        SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder()
                .setDescription(desc)
                .setCoverImage(cover)
                .build();

        // Commit the operation
        Games.Snapshots.commitAndClose(mGoogleApiClient, snapshot, metadataChange);
    }

    public static boolean AreGooglePlayServicesInstalled(Activity activity) {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(activity);
        return code == ConnectionResult.SUCCESS;
    }

    public static boolean IsConnected() {
        return GooglePlayHelper.mGoogleApiClient != null && GooglePlayHelper.mGoogleApiClient.isConnected();
    }

    private static byte[] createBackup() {
        Gson gson = new Gson();
        String backupString;

        List<Inventory> inventories = Inventory.listAll(Inventory.class);
        List<Player_Info> player_infos = Player_Info.listAll(Player_Info.class);
        List<Setting> settings = Setting.listAll(Setting.class);
        List<Trader> traders = Trader.listAll(Trader.class);
        List<Upgrade> upgrades = Upgrade.listAll(Upgrade.class);
        List<Visitor> visitors = Visitor.listAll(Visitor.class);
        List<Visitor_Stats> visitor_stats = Visitor_Stats.listAll(Visitor_Stats.class);
        List<Visitor_Type> visitor_types = Visitor_Type.listAll(Visitor_Type.class);
        List<Visitor_Demand> visitor_demands = Visitor_Demand.listAll(Visitor_Demand.class);
        List<Worker> workers = Worker.listAll(Worker.class);

        backupString = gson.toJson(inventories) + GooglePlayHelper.SAVE_DELIMITER;
        backupString += gson.toJson(player_infos) + GooglePlayHelper.SAVE_DELIMITER;
        backupString += gson.toJson(settings) + GooglePlayHelper.SAVE_DELIMITER;
        backupString += gson.toJson(traders) + GooglePlayHelper.SAVE_DELIMITER;
        backupString += gson.toJson(upgrades) + GooglePlayHelper.SAVE_DELIMITER;
        backupString += gson.toJson(visitors) + GooglePlayHelper.SAVE_DELIMITER;
        backupString += gson.toJson(visitor_stats) + GooglePlayHelper.SAVE_DELIMITER;
        backupString += gson.toJson(visitor_types) + GooglePlayHelper.SAVE_DELIMITER;
        backupString += gson.toJson(visitor_demands) + GooglePlayHelper.SAVE_DELIMITER;
        backupString += gson.toJson(workers);

        return backupString.getBytes();
    }

    private static void applyBackup(String backupData) {
        Gson gson = new Gson();

        String[] splitData = splitBackupData(backupData);

        Inventory[] inventories = gson.fromJson(splitData[0], Inventory[].class);
        Inventory.deleteAll(Inventory.class);
        for (Inventory inventory : inventories) {
            inventory.save();
        }

        Player_Info[] player_infos = gson.fromJson(splitData[1], Player_Info[].class);
        Player_Info.deleteAll(Player_Info.class);
        for (Player_Info player_info : player_infos) {
            player_info.save();
        }

        Setting[] settings = gson.fromJson(splitData[2], Setting[].class);
        Setting.deleteAll(Setting.class);
        for (Setting setting : settings) {
            setting.save();
        }

        Trader[] traders = gson.fromJson(splitData[3], Trader[].class);
        Trader.deleteAll(Trader.class);
        for (Trader trader : traders) {
            trader.save();
        }

        Upgrade[] upgrades = gson.fromJson(splitData[4], Upgrade[].class);
        Upgrade.deleteAll(Upgrade.class);
        for (Upgrade upgrade : upgrades) {
            upgrade.save();
        }

        Visitor[] visitors = gson.fromJson(splitData[5], Visitor[].class);
        Visitor.deleteAll(Visitor.class);
        for (Visitor visitor : visitors) {
            visitor.save();
        }

        Visitor_Stats[] visitor_stats = gson.fromJson(splitData[6], Visitor_Stats[].class);
        Visitor_Stats.deleteAll(Visitor_Stats.class);
        for (Visitor_Stats visitor_stat : visitor_stats) {
            visitor_stat.save();
        }

        Visitor_Type[] visitor_types = gson.fromJson(splitData[7], Visitor_Type[].class);
        Visitor_Type.deleteAll(Visitor_Type.class);
        for (Visitor_Type visitor_type : visitor_types) {
            visitor_type.save();
        }

        if (splitData.length >= 8) {
            Visitor_Demand[] visitor_demands = gson.fromJson(splitData[8], Visitor_Demand[].class);
            if (visitor_demands.length > 0) {
                Visitor_Demand.deleteAll(Visitor_Demand.class);
                for (Visitor_Demand visitor_demand : visitor_demands) {
                    visitor_demand.save();
                }
            }
        }

        if (splitData.length >= 9) {
            Worker[] workers = gson.fromJson(splitData[9], Worker[].class);
            if (workers.length > 0) {
                Worker.deleteAll(Worker.class);
                for (Worker worker : workers) {
                    worker.save();
                }
            }
        }

        callingActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastHelper.showPositiveToast(callingActivity, Toast.LENGTH_LONG, R.string.cloudLoadSuccess, true);
            }
        });

    }

    private static Pair<Integer, Integer> getPrestigeAndXPFromSave(byte[] saveBytes) {
        int prestige = 0;
        int xp = 0;
        Gson gson = new Gson();

        String[] splitData = splitBackupData(new String(saveBytes));
        Player_Info[] saveInfos = gson.fromJson(splitData[1], Player_Info[].class);
        for (Player_Info saveInfo : saveInfos) {
            if (saveInfo.getName().equals("Prestige")) {
                prestige = saveInfo.getIntValue();
            } else if (saveInfo.getName().equals("XP")) {
                xp = saveInfo.getIntValue();
            }
        }

        return new Pair<>(prestige, xp);
    }

    private static boolean cloudSaveIsBetter(Pair<Integer, Integer> cloudValues) {
        boolean isCloudSaveBetter;
        if (cloudValues.first <= Player_Info.getPrestige() && cloudValues.second <= Player_Info.getXp()) {
            isCloudSaveBetter = false;
        } else {
            isCloudSaveBetter = true;
        }
        return isCloudSaveBetter;
    }

    private static String[] splitBackupData(String backupData) {
        String[] splitData = backupData.split(GooglePlayHelper.SAVE_DELIMITER);
        for (int i = 0; i < splitData.length; i++) {
            splitData[i] = splitData[i].replace(GooglePlayHelper.SAVE_DELIMITER, "");
        }

        return splitData;
    }
}
