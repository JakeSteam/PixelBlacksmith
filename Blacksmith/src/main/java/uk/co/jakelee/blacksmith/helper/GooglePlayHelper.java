package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Pair;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.games.PlayGames;
import com.google.android.gms.games.PlayGamesSdk;
import com.google.android.gms.games.SnapshotsClient;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadata;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.gson.Gson;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.IOException;
import java.util.List;

import uk.co.jakelee.blacksmith.BuildConfig;
import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.main.MainActivity;
import uk.co.jakelee.blacksmith.model.Achievement;
import uk.co.jakelee.blacksmith.model.Assistant;
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

/**
 * Play Games Services v2. Unlike v1 there is no GoogleApiClient to connect or disconnect, and no
 * sign-out: the SDK signs the player in automatically, and all we do is record whether that
 * succeeded so writes can be skipped when it didn't.
 *
 * Every v2 client needs an Activity. Fire-and-forget writes are called from all over the codebase,
 * including places with only a Context, so MainActivity registers itself here on creation and those
 * calls borrow it.
 */
public class GooglePlayHelper {
    public static final int RC_ACHIEVEMENTS = 9002;
    public static final int RC_LEADERBOARDS = 9003;
    public static final int RC_SAVED_GAMES = 9004;
    private static final int MAX_COVER_BYTES = 800 * 1024;
    private static final String SAVE_DELIMITER = "UNIQUEDELIMITINGSTRING";
    private static final String mCurrentSaveName = "blacksmithCloudSave";
    private static Activity gamesActivity;
    private static boolean authenticated = false;
    private static byte[] cloudSaveData;
    private static Context callingContext;
    private static Activity callingActivity;
    private static Snapshot loadedSnapshot;

    /** Called once from MainActivity. Authentication is automatic, so this only records the result. */
    public static void initialise(final Activity activity) {
        gamesActivity = activity;
        PlayGamesSdk.initialize(activity.getApplicationContext());
        refreshAuthentication(activity);
    }

    public static void refreshAuthentication(final Activity activity) {
        if (!isUsable(activity)) {
            return;
        }

        PlayGames.getGamesSignInClient(activity).isAuthenticated().addOnCompleteListener(task ->
                authenticated = task.isSuccessful() && task.getResult().isAuthenticated());
    }

    /** v2 has no sign-out, so this is only ever needed when automatic sign-in didn't take. */
    public static void signIn(final Activity activity) {
        if (!isUsable(activity)) {
            return;
        }

        PlayGames.getGamesSignInClient(activity).signIn().addOnCompleteListener(task ->
                authenticated = task.isSuccessful() && task.getResult().isAuthenticated());
    }

    private static boolean isUsable(Activity activity) {
        return activity != null && !activity.isFinishing() && !activity.isDestroyed();
    }

    public static boolean AreGooglePlayServicesInstalled(Activity activity) {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(activity);
        return code == ConnectionResult.SUCCESS;
    }

    public static boolean IsConnected() {
        return authenticated && isUsable(gamesActivity);
    }

    public static void UpdateEvent(String eventId, int quantity) {
        if (!IsConnected() || quantity <= 0) {
            return;
        }

        PlayGames.getEventsClient(gamesActivity).increment(eventId, quantity);
    }

    public static void UpdateLeaderboards(String leaderboardID, int value) {
        if (!IsConnected()) {
            return;
        }

        PlayGames.getLeaderboardsClient(gamesActivity).submitScore(leaderboardID, value);
    }

    public static void UnlockAchievement(String achievementID) {
        if (!IsConnected()) {
            return;
        }

        PlayGames.getAchievementsClient(gamesActivity).unlock(achievementID);
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
        if (hasChanged && !isAchieved) {
            int difference = currentValue - lastSentValue;
            if (achievement.getMaximumValue() == 1 || achievement.getPlayerInfoID() == 17 || achievement.getPlayerInfoID() == 23) {
                PlayGames.getAchievementsClient(gamesActivity).unlock(achievement.getRemoteID());
            } else {
                PlayGames.getAchievementsClient(gamesActivity).increment(achievement.getRemoteID(), difference);
            }
        }
    }

    private static void UpdateStatistic(Player_Info statistic, int currentValue, int lastSentValue) {
        if (currentValue > lastSentValue) {
            statistic.setLastSentValue(currentValue);
            statistic.save();
        }
    }

    /** The intent getters are asynchronous in v2, hence launching from the callback. */
    public static void ShowAchievements(final Activity activity) {
        if (!IsConnected()) {
            return;
        }

        PlayGames.getAchievementsClient(activity).getAchievementsIntent().addOnSuccessListener(intent ->
                activity.startActivityForResult(intent, RC_ACHIEVEMENTS));
    }

    public static void ShowAllLeaderboards(final Activity activity) {
        if (!IsConnected()) {
            return;
        }

        PlayGames.getLeaderboardsClient(activity).getAllLeaderboardsIntent().addOnSuccessListener(intent ->
                activity.startActivityForResult(intent, RC_LEADERBOARDS));
    }

    public static void ShowLeaderboard(final Activity activity, String leaderboardID) {
        if (!IsConnected()) {
            return;
        }

        PlayGames.getLeaderboardsClient(activity).getLeaderboardIntent(leaderboardID).addOnSuccessListener(intent ->
                activity.startActivityForResult(intent, RC_LEADERBOARDS));
    }

    public static void ShowSavedGames(final Activity activity) {
        if (!IsConnected()) {
            return;
        }

        PlayGames.getSnapshotsClient(activity)
                .getSelectSnapshotIntent(activity.getString(R.string.cloudSaveTitle), true, true, 1)
                .addOnSuccessListener(intent -> activity.startActivityForResult(intent, RC_SAVED_GAMES));
    }

    /**
     * Handles the result of the saved-games picker. Whether the player chose an existing snapshot or
     * asked for a new one decides between loading and saving.
     */
    public static void SavedGamesIntent(final Context context, final Activity activity, final Intent intent) {
        if (intent == null || !IsConnected()) {
            return;
        }
        callingContext = context;
        callingActivity = activity;

        final boolean loading = intent.hasExtra(SnapshotsClient.EXTRA_SNAPSHOT_METADATA);
        if (!loading && !intent.hasExtra(SnapshotsClient.EXTRA_SNAPSHOT_NEW)) {
            return;
        }

        openSnapshot(activity, loading);
    }

    private static void openSnapshot(final Activity activity, final boolean loading) {
        PlayGames.getSnapshotsClient(activity).open(mCurrentSaveName, true)
                .addOnSuccessListener(result -> {
                    if (result.isConflict()) {
                        resolveConflict(activity, result.getConflict(), loading);
                    } else {
                        useSnapshot(result.getData(), loading);
                    }
                })
                .addOnFailureListener(e -> reportCloudFailure(loading ? "loading" : "saving", e));
    }

    /** Keeps whichever side was modified most recently, matching the previous behaviour. */
    private static void resolveConflict(final Activity activity, SnapshotsClient.SnapshotConflict conflict, final boolean loading) {
        Snapshot mine = conflict.getSnapshot();
        Snapshot theirs = conflict.getConflictingSnapshot();
        Snapshot newest = mine.getMetadata().getLastModifiedTimestamp() >= theirs.getMetadata().getLastModifiedTimestamp() ? mine : theirs;

        if (callingActivity != null) {
            callingActivity.runOnUiThread(() -> ToastHelper.showErrorToast(callingActivity.findViewById(android.R.id.content),
                    ToastHelper.LONG, callingActivity.getString(ErrorHelper.errors.get(Constants.ERROR_RESOLVING_CONFLICT)), true));
        }

        PlayGames.getSnapshotsClient(activity).resolveConflict(conflict.getConflictId(), newest)
                .addOnSuccessListener(result -> {
                    if (result.isConflict()) {
                        resolveConflict(activity, result.getConflict(), loading);
                    } else {
                        useSnapshot(result.getData(), loading);
                    }
                })
                .addOnFailureListener(e -> reportCloudFailure(loading ? "loading" : "saving", e));
    }

    /**
     * v2's Task callbacks arrive on the main thread, unlike the AsyncTask this replaced, so reading
     * the snapshot and applying it have to be pushed onto a background thread by hand. applyBackup
     * rewrites fourteen tables a row at a time, which is far too slow for the UI thread.
     */
    private static void useSnapshot(final Snapshot snapshot, final boolean loading) {
        if (snapshot == null) {
            return;
        }

        if (loading) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        cloudSaveData = snapshot.getSnapshotContents().readFully();
                    } catch (IOException e) {
                        reportCloudFailure("loading", e);
                        return;
                    }
                    loadFromCloud(true);
                }
            }).start();
        } else {
            loadedSnapshot = snapshot;
            saveToCloud();
        }
    }

    private static void reportCloudFailure(final String currentTask, final Exception e) {
        if (callingActivity == null || callingContext == null) {
            return;
        }

        callingActivity.runOnUiThread(() -> {
            DisplayHelper.getInstance(callingActivity).updateFullscreen(callingActivity);
            ToastHelper.showErrorToast(callingActivity.findViewById(android.R.id.content), ToastHelper.SHORT,
                    String.format(callingContext.getString(R.string.cloudLocalFailure), currentTask, e.toString()), true);
        });
    }

    private static void loadFromCloud(final boolean checkIsImprovement) {
        if (!IsConnected() || callingContext == null || callingActivity == null || cloudSaveData == null) {
            return;
        }

        if (!checkIsImprovement) {
            callingActivity.runOnUiThread(() -> ToastHelper.showToast(callingActivity.findViewById(android.R.id.content),
                    ToastHelper.LONG, callingActivity.getString(R.string.cloudLoadBeginning), false));
        }

        Pair<Integer, Integer> cloudData = getPrestigeAndXPFromSave(cloudSaveData);

        if (!checkIsImprovement || newSaveIsBetter(cloudData)) {
            applyBackup(new String(cloudSaveData));
        } else {
            // Reached from a background thread, and a dialog has to be shown from the main one.
            final Pair<Integer, Integer> values = cloudData;
            callingActivity.runOnUiThread(() -> AlertDialogHelper.confirmWorseCloudLoad(callingContext, callingActivity,
                    Player_Info.getPrestige(),
                    Player_Info.getXp(),
                    values.first,
                    values.second));
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
        if (callingActivity == null || callingContext == null || loadedSnapshot == null) {
            return;
        }

        callingActivity.runOnUiThread(() -> ToastHelper.showToast(callingActivity.findViewById(android.R.id.content),
                ToastHelper.LONG, callingActivity.getString(R.string.cloudSaveBeginning), false));

        // Serialising the save reads fourteen tables, so it stays off the main thread. Committing is
        // handed back to the main thread, since that's where the Play Games client expects to be used.
        new Thread(new Runnable() {
            public void run() {
                byte[] data = createBackup();
                String desc = String.format(callingContext.getString(R.string.cloudSaveCaption),
                        Player_Info.getPrestige(),
                        Player_Info.getPlayerLevel(),
                        Inventory.getCoins(),
                        BuildConfig.VERSION_NAME);
                Bitmap cover = decodeCoverImage(callingContext);

                loadedSnapshot.getSnapshotContents().writeBytes(data);

                final SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder()
                        .setDescription(desc)
                        .setCoverImage(cover)
                        .build();

                callingActivity.runOnUiThread(() -> commitSnapshot(metadataChange));
            }
        }).start();
    }

    private static void commitSnapshot(SnapshotMetadataChange metadataChange) {
        PlayGames.getSnapshotsClient(callingActivity).commitAndClose(loadedSnapshot, metadataChange)
                .addOnSuccessListener(metadata -> {
                    DisplayHelper.getInstance(callingActivity).updateFullscreen(callingActivity);
                    ToastHelper.showPositiveToast(callingActivity.findViewById(android.R.id.content),
                            ToastHelper.LONG, callingActivity.getString(R.string.cloudSaveSuccess), true);
                })
                .addOnFailureListener(e -> reportCloudFailure("saving", e));
    }

    /**
     * Play caps snapshot cover images at 800KB and scales anything larger itself, but the full
     * promo image decodes to roughly 25MB, which is a lot of heap to ask for mid-save on an old
     * device. Downsampling first keeps it comfortably under the cap.
     */
    private static Bitmap decodeCoverImage(Context context) {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), R.drawable.promo, bounds);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        while ((bounds.outWidth / options.inSampleSize) * (bounds.outHeight / options.inSampleSize) * 4 > MAX_COVER_BYTES) {
            options.inSampleSize *= 2;
        }

        return BitmapFactory.decodeResource(context.getResources(), R.drawable.promo, options);
    }

    private static void saveToCloud() {
        if (!IsConnected() || callingContext == null || callingActivity == null || loadedSnapshot == null) {
            return;
        }

        SnapshotMetadata metadata = loadedSnapshot.getMetadata();
        if (metadata.getDeviceName() == null) {
            forceSaveToCloud();
        } else {
            AlertDialogHelper.confirmCloudSave(callingContext, callingActivity,
                    metadata.getDescription(),
                    metadata.getLastModifiedTimestamp(),
                    metadata.getDeviceName());
        }
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
        backupString += gson.toJson(Assistant.listAll(Assistant.class)) + GooglePlayHelper.SAVE_DELIMITER;

        return backupString.getBytes();
    }

    /**
     * A save is only worth applying if it has all the mandatory sections and a readable database
     * version. applyBackup deletes each table before repopulating it, so a truncated or empty
     * snapshot would otherwise destroy the player's game partway through.
     */
    public static boolean isPlausibleSave(String backupData) {
        if (backupData == null || backupData.isEmpty()) {
            return false;
        }

        String[] splitData = splitBackupData(backupData);
        if (splitData.length < 9) {
            return false;
        }

        try {
            Integer.parseInt(splitData[0].trim());
            Player_Info[] infos = new Gson().fromJson(splitData[2], Player_Info[].class);
            return infos != null && infos.length > 0;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static void applyBackup(String backupData) {
        if (!isPlausibleSave(backupData)) {
            reportCloudFailure("loading", new IOException("Save data was incomplete, so nothing was changed"));
            return;
        }

        Gson gson = new Gson();

        String[] splitData = splitBackupData(backupData);
        MainActivity.prefs.edit().putInt("databaseVersion", Integer.parseInt(splitData[0].trim())).apply();

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

        if (splitData.length > 13) {
            Assistant[] assistants = gson.fromJson(splitData[13], Assistant[].class);
            if (assistants.length > 0) {
                Assistant.deleteAll(Assistant.class);
                for (Assistant assistant : assistants) {
                    assistant.save();
                }
            }
        }

        new DatabaseHelper().execute();

        if (callingActivity != null) {
            callingActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DisplayHelper.getInstance(callingActivity).updateFullscreen(callingActivity);
                    ToastHelper.showPositiveToast(callingActivity.findViewById(android.R.id.content), ToastHelper.LONG, callingActivity.getString(R.string.cloudLoadSuccess), true);
                }
            });
        }
    }

    public static Pair<Integer, Integer> getPrestigeAndXPFromSave(byte[] saveBytes) {
        int prestige = 0;
        int xp = 0;
        Gson gson = new Gson();

        String[] splitData = splitBackupData(new String(saveBytes));
        // >= 3, not >= 2: index 2 is read below, and this runs before applyBackup's plausibility
        // check, so a two-section save used to throw here before that guard could reject it.
        if (splitData.length >= 3) {
            Player_Info[] saveInfos = gson.fromJson(splitData[2], Player_Info[].class);
            for (Player_Info saveInfo : saveInfos) {
                if (saveInfo.getName() != null) {
                    if (saveInfo.getName().equals("Prestige")) {
                        prestige = saveInfo.getIntValue();
                    } else if (saveInfo.getName().equals("XP")) {
                        xp = saveInfo.getIntValue();
                    }
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
