package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Pair;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
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

import uk.co.jakelee.blacksmith.BuildConfig;
import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.main.MainActivity;
import uk.co.jakelee.blacksmith.model.Achievement;
import uk.co.jakelee.blacksmith.model.Hero;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Setting;
import uk.co.jakelee.blacksmith.model.Trader;
import uk.co.jakelee.blacksmith.model.Upgrade;
import uk.co.jakelee.blacksmith.model.Visitor;
import uk.co.jakelee.blacksmith.model.Visitor_Demand;
import uk.co.jakelee.blacksmith.model.Visitor_Log;
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
    private static Snapshot loadedSnapshot;

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
        if (!IsConnected()) {
            return;
        }

        PendingResult<Quests.LoadQuestsResult> quests = Games.Quests.load(mGoogleApiClient, new int[] {Quest.STATE_ACCEPTED}, Quests.SORT_ORDER_ENDING_SOON_FIRST, false);
        quests.setResultCallback(this);
    }

    public static void UpdateEvent(String eventId, int quantity) {
        if (!IsConnected() || quantity <= 0) {
            return;
        }

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
            if (achievement.getMaximumValue() == 1 || achievement.getPlayerInfoID() == 17 || achievement.getPlayerInfoID() == 23) {
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

                while (!result.getStatus().isSuccess()) {
                    callingActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DisplayHelper.getInstance(callingActivity).updateFullscreen(callingActivity);
                            ToastHelper.showErrorToast(activity.findViewById(R.id.help), ToastHelper.LONG, ErrorHelper.errors.get(Constants.ERROR_RESOLVING_CONFLICT), true);
                        }
                    });

                    if (result.getStatus().getStatusCode() == GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT) {
                        Snapshot snapshot = result.getSnapshot();
                        Snapshot conflictSnapshot = result.getConflictingSnapshot();
                        Snapshot mResolvedSnapshot = snapshot;

                        if (snapshot.getMetadata().getLastModifiedTimestamp() < conflictSnapshot.getMetadata().getLastModifiedTimestamp()) {
                            mResolvedSnapshot = conflictSnapshot;
                        }

                        result = Games.Snapshots.resolveConflict(mGoogleApiClient, result.getConflictId(), mResolvedSnapshot).await();
                    }
                }

                Snapshot snapshot = result.getSnapshot();
                try {
                    if (intent.hasExtra(Snapshots.EXTRA_SNAPSHOT_METADATA)) {
                        cloudSaveData = snapshot.getSnapshotContents().readFully();
                        loadFromCloud(true);
                        currentTask = "loading";
                    } else if (intent.hasExtra(Snapshots.EXTRA_SNAPSHOT_NEW)) {
                        loadedSnapshot = snapshot;
                        saveToCloud();
                        currentTask = "saving";
                    }
                } catch (final IOException e) {
                    callingActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DisplayHelper.getInstance(callingActivity).updateFullscreen(callingActivity);
                            ToastHelper.showErrorToast(activity.findViewById(R.id.help), ToastHelper.SHORT, String.format(context.getString(R.string.cloudLocalFailure), currentTask, e.toString()), true);
                        }
                    });
                }
                return result.getStatus().getStatusCode();
            }
        };

        task.execute();
    }

    private static void loadFromCloud(final boolean checkIsImprovement) {
        if (!IsConnected() || callingContext == null || callingActivity == null || cloudSaveData == null) {
            return;
        }


        if (!checkIsImprovement) {
            callingActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastHelper.showToast(callingActivity.findViewById(R.id.help), ToastHelper.LONG, callingActivity.getString(R.string.cloudLoadBeginning), false);
                }
            });
        }

        Pair<Integer, Integer> cloudData = getPrestigeAndXPFromSave(cloudSaveData);

        if (!checkIsImprovement || newSaveIsBetter(cloudData)) {
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

    public static void forceSaveToCloud() {
        callingActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastHelper.showToast(callingActivity.findViewById(R.id.help), ToastHelper.LONG, callingActivity.getString(R.string.cloudSaveBeginning), false);
            }
        });

        new Thread(new Runnable() {
            public void run() {
                byte[] data = createBackup();
                String desc = String.format(callingContext.getString(R.string.cloudSaveCaption),
                        Player_Info.getPrestige(),
                        Player_Info.getPlayerLevel(),
                        Inventory.getCoins(),
                        BuildConfig.VERSION_NAME);
                Bitmap cover = BitmapFactory.decodeResource(callingContext.getResources(), R.drawable.promo);

                loadedSnapshot.getSnapshotContents().writeBytes(data);

                SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder()
                        .setDescription(desc)
                        .setCoverImage(cover)
                        .build();

                // Commit the operation
                Games.Snapshots.commitAndClose(mGoogleApiClient, loadedSnapshot, metadataChange);

                callingActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DisplayHelper.getInstance(callingActivity).updateFullscreen(callingActivity);
                        ToastHelper.showPositiveToast(callingActivity.findViewById(R.id.help), ToastHelper.LONG, callingActivity.getString(R.string.cloudSaveSuccess), true);
                    }
                });
            }
        }).start();
    }

    private static void saveToCloud() {
        if (!IsConnected() || callingContext == null || callingActivity == null || loadedSnapshot == null) {
            return;
        }

        if (loadedSnapshot.getMetadata().getDeviceName() == null) {
            forceSaveToCloud();
        } else {
            AlertDialogHelper.confirmCloudSave(callingContext, callingActivity,
                    loadedSnapshot.getMetadata().getDescription(),
                    loadedSnapshot.getMetadata().getLastModifiedTimestamp(),
                    loadedSnapshot.getMetadata().getDeviceName());
        }

    }

    public static boolean AreGooglePlayServicesInstalled(Activity activity) {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(activity);
        return code == ConnectionResult.SUCCESS;
    }

    public static boolean IsConnected() {
        return GooglePlayHelper.mGoogleApiClient != null && GooglePlayHelper.mGoogleApiClient.isConnected();
    }

    public static byte[] createBackup() {
        Gson gson = new Gson();

        String backupString = MainActivity.prefs.getInt("databaseVersion", DatabaseHelper.DB_LATEST) + GooglePlayHelper.SAVE_DELIMITER;
        backupString += gson.toJson(Inventory.listAll(Inventory.class)) + GooglePlayHelper.SAVE_DELIMITER;
        backupString += gson.toJson(Player_Info.listAll(Player_Info.class)) + GooglePlayHelper.SAVE_DELIMITER;
        backupString += gson.toJson(Setting.listAll(Setting.class)) + GooglePlayHelper.SAVE_DELIMITER;
        backupString += gson.toJson(Trader.listAll(Trader.class)) + GooglePlayHelper.SAVE_DELIMITER;
        backupString += gson.toJson(Upgrade.listAll(Upgrade.class)) + GooglePlayHelper.SAVE_DELIMITER;
        backupString += gson.toJson(Visitor.listAll(Visitor.class)) + GooglePlayHelper.SAVE_DELIMITER;
        backupString += gson.toJson(Visitor_Stats.listAll(Visitor_Stats.class)) + GooglePlayHelper.SAVE_DELIMITER;
        backupString += gson.toJson(Visitor_Type.listAll(Visitor_Type.class)) + GooglePlayHelper.SAVE_DELIMITER;
        backupString += gson.toJson(Visitor_Demand.listAll(Visitor_Demand.class)) + GooglePlayHelper.SAVE_DELIMITER;
        backupString += gson.toJson(Worker.listAll(Worker.class)) + GooglePlayHelper.SAVE_DELIMITER;
        backupString += gson.toJson(Hero.listAll(Hero.class)) + GooglePlayHelper.SAVE_DELIMITER;
        backupString += gson.toJson(Visitor_Log.listAll(Visitor_Log.class)) + GooglePlayHelper.SAVE_DELIMITER;

        return backupString.getBytes();
    }

    public static void applyBackup(String backupData) {
        Gson gson = new Gson();

        String[] splitData = splitBackupData(backupData);
        MainActivity.prefs.edit().putInt("databaseVersion", Integer.parseInt(splitData[0])).apply();

        Inventory[] inventories = gson.fromJson(splitData[1], Inventory[].class);
        Inventory.deleteAll(Inventory.class);
        for (Inventory inventory : inventories) {
            inventory.save();
        }

        Player_Info[] player_infos = gson.fromJson(splitData[2], Player_Info[].class);
        Player_Info.deleteAll(Player_Info.class);
        for (Player_Info player_info : player_infos) {
            player_info.save();
        }

        Setting[] settings = gson.fromJson(splitData[3], Setting[].class);
        Setting.deleteAll(Setting.class);
        for (Setting setting : settings) {
            setting.save();
        }

        Trader[] traders = gson.fromJson(splitData[4], Trader[].class);
        Trader.deleteAll(Trader.class);
        for (Trader trader : traders) {
            trader.save();
        }

        Upgrade[] upgrades = gson.fromJson(splitData[5], Upgrade[].class);
        Upgrade.deleteAll(Upgrade.class);
        for (Upgrade upgrade : upgrades) {
            upgrade.save();
        }

        Visitor[] visitors = gson.fromJson(splitData[6], Visitor[].class);
        Visitor.deleteAll(Visitor.class);
        for (Visitor visitor : visitors) {
            visitor.save();
        }

        Visitor_Stats[] visitor_stats = gson.fromJson(splitData[7], Visitor_Stats[].class);
        Visitor_Stats.deleteAll(Visitor_Stats.class);
        for (Visitor_Stats visitor_stat : visitor_stats) {
            visitor_stat.save();
        }

        Visitor_Type[] visitor_types = gson.fromJson(splitData[8], Visitor_Type[].class);
        Visitor_Type.deleteAll(Visitor_Type.class);
        for (Visitor_Type visitor_type : visitor_types) {
            visitor_type.save();
        }

        if (splitData.length > 9) {
            Visitor_Demand[] visitor_demands = gson.fromJson(splitData[9], Visitor_Demand[].class);
            if (visitor_demands.length > 0) {
                Visitor_Demand.deleteAll(Visitor_Demand.class);
                for (Visitor_Demand visitor_demand : visitor_demands) {
                    visitor_demand.save();
                }
            }
        }

        if (splitData.length > 10) {
            Worker[] workers = gson.fromJson(splitData[10], Worker[].class);
            if (workers.length > 0) {
                Worker.deleteAll(Worker.class);
                for (Worker worker : workers) {
                    worker.save();
                }
            }
        }

        if (splitData.length > 11) {
            Hero[] heroes = gson.fromJson(splitData[11], Hero[].class);
            if (heroes.length > 0) {
                Hero.deleteAll(Hero.class);
                for (Hero hero : heroes) {
                    hero.save();
                }
            }
        }

        if (splitData.length > 12) {
            Visitor_Log[] visitorLogs = gson.fromJson(splitData[12], Visitor_Log[].class);
            if (visitorLogs.length > 0) {
                Visitor_Log.deleteAll(Visitor_Log.class);
                for (Visitor_Log visitorLog : visitorLogs) {
                    visitorLog.save();
                }
            }
        }

        new DatabaseHelper(this, false).execute();

        if (callingActivity != null) {
            callingActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DisplayHelper.getInstance(callingActivity).updateFullscreen(callingActivity);
                    ToastHelper.showPositiveToast(callingActivity.findViewById(R.id.help), ToastHelper.LONG, callingActivity.getString(R.string.cloudLoadSuccess), true);
                }
            });
        }
    }

    public static Pair<Integer, Integer> getPrestigeAndXPFromSave(byte[] saveBytes) {
        int prestige = 0;
        int xp = 0;
        Gson gson = new Gson();

        String[] splitData = splitBackupData(new String(saveBytes));
        if (splitData.length >= 2) {
            Player_Info[] saveInfos = gson.fromJson(splitData[2], Player_Info[].class);
            for (Player_Info saveInfo : saveInfos) {
                if (saveInfo.getName().equals("Prestige")) {
                    prestige = saveInfo.getIntValue();
                } else if (saveInfo.getName().equals("XP")) {
                    xp = saveInfo.getIntValue();
                }
            }
        }

        return new Pair<>(prestige, xp);
    }

    public static boolean newSaveIsBetter(Pair<Integer, Integer> newValues) {
        return !(newValues.first <= Player_Info.getPrestige() && newValues.second <= Player_Info.getXp());
    }

    private static String[] splitBackupData(String backupData) {
        String[] splitData = backupData.split(GooglePlayHelper.SAVE_DELIMITER);
        for (int i = 0; i < splitData.length; i++) {
            splitData[i] = splitData[i].replace(GooglePlayHelper.SAVE_DELIMITER, "");
        }

        return splitData;
    }
}
