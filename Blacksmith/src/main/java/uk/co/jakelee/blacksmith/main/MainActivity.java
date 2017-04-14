package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.batch.android.Batch;
import com.batch.android.BatchUnlockListener;
import com.batch.android.Config;
import com.batch.android.Offer;
import com.batch.android.Resource;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.quest.Quest;
import com.google.android.gms.games.quest.QuestUpdateListener;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;
import java.util.Locale;

import hotchemi.android.rate.AppRate;
import uk.co.jakelee.blacksmith.BuildConfig;
import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.AdvertHelper;
import uk.co.jakelee.blacksmith.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.EventHelper;
import uk.co.jakelee.blacksmith.helper.GooglePlayHelper;
import uk.co.jakelee.blacksmith.helper.LanguageHelper;
import uk.co.jakelee.blacksmith.helper.NotificationHelper;
import uk.co.jakelee.blacksmith.helper.PremiumHelper;
import uk.co.jakelee.blacksmith.helper.TextHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.helper.TutorialHelper;
import uk.co.jakelee.blacksmith.helper.VariableHelper;
import uk.co.jakelee.blacksmith.helper.VisitorHelper;
import uk.co.jakelee.blacksmith.helper.WorkerHelper;
import uk.co.jakelee.blacksmith.model.Assistant;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Pending_Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Setting;
import uk.co.jakelee.blacksmith.model.Trader_Stock;
import uk.co.jakelee.blacksmith.model.Upgrade;
import uk.co.jakelee.blacksmith.model.Visitor;
import uk.co.jakelee.blacksmith.service.MusicService;

import static uk.co.jakelee.blacksmith.R.id.mainScroller;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        QuestUpdateListener,
        BatchUnlockListener {
    private static final Handler handler = new Handler();
    public static RelativeLayout questContainer;
    public static VariableHelper vh;
    public static boolean needToRedrawVisitors = false;
    public static boolean needToRedrawSlots = false;
    public static SharedPreferences prefs;
    public AdvertHelper ah;
    private DisplayHelper dh;
    private int newVisitors;
    private Intent musicService;
    private boolean musicServiceIsStarted = false;
    private GooglePlayHelper gph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Batch.Push.setGCMSenderId("484982205674");
        //Batch.setConfig(new Config("DEV587E86C2DC0F0FE0EE90C49321B"));
        Batch.setConfig(new Config("587E86C2DBE524C8EB318A0E517579"));

        dh = DisplayHelper.getInstance(getApplicationContext());
        vh = new VariableHelper();
        gph = new GooglePlayHelper();
        musicService = new Intent(this, MusicService.class);
        prefs = getSharedPreferences("uk.co.jakelee.blacksmith", MODE_PRIVATE);
        LanguageHelper.updateLanguage(getApplicationContext());

        if (prefs.getInt("tutorialStage", 0) > 0) {
            TutorialHelper.currentlyInTutorial = true;
            TutorialHelper.currentStage = prefs.getInt("tutorialStage", 0);
        }

        assignUIElements();

        GooglePlayHelper.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .addApi(Drive.API).addScope(Drive.SCOPE_APPFOLDER)
                .setViewForPopups(findViewById(android.R.id.content))
                .build();

        dh.createAllSlots(this);
        ratingPrompt();

        if (Player_Info.displayAds()) {
            ah = AdvertHelper.getInstance(this);
        }

        Player_Info savedVersion = Select.from(Player_Info.class).where(Condition.prop("name").eq("SavedVersion")).first();
        if (savedVersion != null &&
                savedVersion.getIntValue() != BuildConfig.VERSION_CODE &&
                BuildConfig.VERSION_NAME.length() > 0 &&
                BuildConfig.VERSION_NAME.endsWith(".0")) {
            savedVersion.setIntValue(BuildConfig.VERSION_CODE);
            savedVersion.save();

            if (!TutorialHelper.currentlyInTutorial) {
                AlertDialogHelper.displayUpdateMessage(this, this);
            }
        }

        gph.UpdateQuest();
    }

    private void ratingPrompt() {
        AppRate.with(this)
                .setInstallDays(5)
                .setLaunchTimes(4)
                .setRemindInterval(3)
                .setShowLaterButton(true)
                .monitor();

        AppRate.showRateDialogIfMeetsConditions(this);
    }

    private void assignUIElements() {
        questContainer = (RelativeLayout) findViewById(R.id.questContainer);
    }

    public void startFirstTutorial() {
        // Stage 1
        findViewById(mainScroller).scrollTo(0, 0);

        TutorialHelper th = new TutorialHelper(this, Constants.STAGE_1_MAIN);
        th.addTutorialNoOverlay(findViewById(R.id.visitors_container), R.string.tutorialIntro, R.string.tutorialIntroText, false);
        th.addTutorial(findViewById(R.id.coinCount), R.string.tutorialCoins, R.string.tutorialCoinsText, false);
        th.addTutorial(findViewById(R.id.questContainer), R.string.tutorialQuest, R.string.tutorialQuestText, false);
        th.addTutorial(findViewById(R.id.currentLevel), R.string.tutorialLevel, R.string.tutorialLevelText, false);
        th.addTutorialRectangle(findViewById(R.id.visitors_container), R.string.tutorialVisitor, R.string.tutorialVisitorText, true);
        th.start();
    }

    private void startSecondTutorial() {
        // Stage 5
        findViewById(mainScroller).scrollTo(dh.convertDpToPixel(420), 0);

        TutorialHelper th = new TutorialHelper(this, Constants.STAGE_5_MAIN);
        th.addTutorialRectangle(findViewById(R.id.slots_furnace), R.string.tutorialFurnaceSlots, R.string.tutorialFurnaceSlotsText, false);
        th.addTutorial(findViewById(R.id.open_furnace), R.string.tutorialFurnace, R.string.tutorialFurnaceText, true);
        th.start();
    }

    private void startThirdTutorial() {
        // Stage 7
        findViewById(mainScroller).scrollTo(dh.convertDpToPixel(680), 0);

        TutorialHelper th = new TutorialHelper(this, Constants.STAGE_7_MAIN);
        th.addTutorial(findViewById(R.id.open_anvil), R.string.tutorialMainAnvil, R.string.tutorialMainAnvilText, true);
        th.addTutorial(findViewById(R.id.open_anvil), R.string.tutorialMainAnvil, R.string.tutorialMainAnvilText, true);
        th.start();
    }

    private void startFourthTutorial() {
        // Stage 9
        findViewById(mainScroller).scrollTo(dh.convertDpToPixel(400), 0);

        TutorialHelper th = new TutorialHelper(this, Constants.STAGE_9_MAIN);
        th.addTutorial(findViewById(R.id.open_table), R.string.tutorialMainTable, R.string.tutorialMainTableText, true);
        th.addTutorial(findViewById(R.id.open_table), R.string.tutorialMainTable, R.string.tutorialMainTableText, true);
        th.start();
    }

    private void startFifthTutorial() {
        // Stage 11
        findViewById(mainScroller).scrollTo(0, 0);

        TutorialHelper th = new TutorialHelper(this, Constants.STAGE_11_MAIN);
        th.addTutorialRectangle(findViewById(R.id.visitors_container), R.string.tutorialMainVisitorFinish, R.string.tutorialMainVisitorFinishText, true);
        th.addTutorialRectangle(findViewById(R.id.visitors_container), R.string.tutorialMainVisitorFinish, R.string.tutorialMainVisitorFinishText, true);
        th.start();
    }

    private void startSixthTutorial() {
        // Stage 13
        findViewById(mainScroller).scrollTo(dh.convertDpToPixel(1160), 0);

        TutorialHelper th = new TutorialHelper(this, Constants.STAGE_13_MAIN);
        th.addTutorialNoOverlay(findViewById(R.id.open_workers), R.string.tutorialMainInfo, R.string.tutorialMainInfoText, false, Gravity.BOTTOM);
        th.addTutorial(findViewById(R.id.open_workers), R.string.tutorialMainWorker, R.string.tutorialMainWorkerText, false, Gravity.BOTTOM);
        th.addTutorial(findViewById(R.id.open_market), R.string.tutorialMainMarket, R.string.tutorialMainMarketText, true, Gravity.BOTTOM);
        th.start();
    }

    private void startSeventhTutorial() {
        // Stage 15
        findViewById(mainScroller).scrollTo(dh.convertDpToPixel(900), 0);

        TutorialHelper th = new TutorialHelper(this, Constants.STAGE_15_MAIN);
        th.addTutorial(findViewById(R.id.open_settings), R.string.tutorialMainSettings, R.string.tutorialMainSettingsText, false, Gravity.TOP);
        th.addTutorial(findViewById(R.id.open_inventory), R.string.tutorialMainInventory, R.string.tutorialMainInventoryText, false, Gravity.TOP);
        th.addTutorial(findViewById(R.id.open_help), R.string.tutorialMainHelp, R.string.tutorialMainHelpText, true, Gravity.TOP);
        th.start();

        TutorialHelper.currentlyInTutorial = false;
        TutorialHelper.currentStage = 0;
        prefs.edit().putInt("tutorialStage", TutorialHelper.currentStage).apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Batch.Unlock.setUnlockListener(this);
        Batch.onStart(this);

        // Run background tasks and organise music
        new Thread(new Runnable() {
            public void run() {
                setupRecurringEvents();
                NotificationHelper.clearNotifications(getApplicationContext());
                if (Setting.getSafeBoolean(Constants.SETTING_MUSIC) && !musicServiceIsStarted) {
                    startService(musicService);
                    musicServiceIsStarted = true;
                }
            }
        }).start();

        if (Setting.getSafeBoolean(Constants.SETTING_SIGN_IN) && GooglePlayHelper.AreGooglePlayServicesInstalled(this)) {
            GooglePlayHelper.mGoogleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (TutorialHelper.currentlyInTutorial && TutorialHelper.chainTourGuide != null) {
            try {
                TutorialHelper.chainTourGuide.cleanUp();
            } catch (NullPointerException e) {
                Log.d("Blacksmith", "Failed to pause tutorial");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Setting setting = Setting.findById(Setting.class, Constants.SETTING_ORIENTATION);
        //noinspection ResourceType
        if (setting != null && setting.getIntValue() != getRequestedOrientation()) {
            //noinspection ResourceType
            setRequestedOrientation(setting.getIntValue());
        }

        new Thread(new Runnable() {
            public void run() {
                newVisitors = VisitorHelper.tryCreateRequiredVisitors();

                if (Setting.getSafeBoolean(Constants.SETTING_MUSIC) && !musicServiceIsStarted) {
                    startService(musicService);
                    musicServiceIsStarted = true;
                } else if (!Setting.getSafeBoolean(Constants.SETTING_MUSIC) && musicServiceIsStarted) {
                    stopService(musicService);
                    musicServiceIsStarted = false;
                }
            }
        }).start();

        if (newVisitors > 0) {
            ToastHelper.showToast(null, ToastHelper.SHORT, String.format(getString(R.string.visitorArrived), newVisitors), true);
            newVisitors = 0;
        }

        if (needToRedrawVisitors) {
            updateVisitors();
        }

        dh.updateFullscreen(this);
        View exitTutorialButton = findViewById(R.id.exitTutorial);
        if (TutorialHelper.currentlyInTutorial) {
            if (exitTutorialButton != null) {
                exitTutorialButton.setVisibility(View.VISIBLE);
            }

            if (TutorialHelper.currentStage <= Constants.STAGE_1_MAIN) {
                startFirstTutorial();
            } else if (TutorialHelper.currentStage == Constants.STAGE_4_VISITOR || TutorialHelper.currentStage == Constants.STAGE_5_MAIN) {
                startSecondTutorial();
            } else if (TutorialHelper.currentStage == Constants.STAGE_6_FURNACE || TutorialHelper.currentStage == Constants.STAGE_7_MAIN) {
                startThirdTutorial();
            } else if (TutorialHelper.currentStage == Constants.STAGE_8_ANVIL || TutorialHelper.currentStage == Constants.STAGE_9_MAIN) {
                startFourthTutorial();
            } else if (TutorialHelper.currentStage == Constants.STAGE_10_TABLE || TutorialHelper.currentStage == Constants.STAGE_11_MAIN) {
                startFifthTutorial();
            } else if (TutorialHelper.currentStage == Constants.STAGE_12_VISITOR || TutorialHelper.currentStage == Constants.STAGE_13_MAIN) {
                startSixthTutorial();
            } else if (TutorialHelper.currentStage == Constants.STAGE_14_MARKET || TutorialHelper.currentStage == Constants.STAGE_15_MAIN) {
                startSeventhTutorial();
            }
        }

        findViewById(R.id.buyCoins).setVisibility(Setting.getSafeBoolean(Constants.SETTING_DISABLE_ADS) ? View.INVISIBLE : View.VISIBLE);
        DisplayHelper.updateAssistantDisplay((RelativeLayout) findViewById(R.id.assistant_container));
    }

    public void exitTutorial(View v) {
        TutorialHelper.currentlyInTutorial = false;
        TutorialHelper.currentStage = 0;
        prefs.edit().putInt("tutorialStage", 0).apply();
        ToastHelper.showToast(findViewById(R.id.exitTutorial), ToastHelper.LONG, getString(R.string.exitTutorialText), true);
        v.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        Batch.onStop(this);
        super.onStop();

        handler.removeCallbacksAndMessages(null);
        boolean notificationSound = Setting.getSafeBoolean(Constants.SETTING_NOTIFICATION_SOUNDS);

        if (Setting.getSafeBoolean(Constants.SETTING_RESTOCK_NOTIFICATIONS)) {
            NotificationHelper.addRestockNotification(getApplicationContext(), notificationSound);
        }

        if (Setting.getSafeBoolean(Constants.SETTING_WORKER_NOTIFICATIONS)) {
            NotificationHelper.addHelperNotification(getApplicationContext(), notificationSound);
            NotificationHelper.addHeroNotification(getApplicationContext(), notificationSound);
        }

        if (Setting.getSafeBoolean(Constants.SETTING_VISITOR_NOTIFICATIONS) && Visitor.count(Visitor.class) < Upgrade.getValue("Maximum Visitors")) {
            NotificationHelper.addVisitorNotification(getApplicationContext(), notificationSound);
        }

        if (Setting.getSafeBoolean(Constants.SETTING_BONUS_NOTIFICATIONS) && !Player_Info.isBonusReady() && Player_Info.displayAds()) {
            NotificationHelper.addBonusNotification(getApplicationContext(), notificationSound);
        }

        if (Setting.getSafeBoolean(Constants.SETTING_FINISHED_NOTIFICATIONS) && Pending_Inventory.listAll(Pending_Inventory.class).size() > 0) {
            NotificationHelper.addFinishedNotification(getApplicationContext(), notificationSound);
        }

        if (Setting.getSafeBoolean(Constants.SETTING_ASSISTANT_NOTIFICATIONS)) {
            NotificationHelper.addAssistantNotification(getApplicationContext(), notificationSound);
        }

        if (musicServiceIsStarted) {
            stopService(musicService);
            musicServiceIsStarted = false;
        }

        GooglePlayHelper.mGoogleApiClient.disconnect();

        prefs.edit().putInt("tutorialStage", (TutorialHelper.currentStage > 0 ? TutorialHelper.currentStage : 0)).apply();
    }

    private void setupRecurringEvents() {
        final Activity activity = this;

        final TextView currentLevel = (TextView) findViewById(R.id.currentLevel);
        final ProgressBar levelProgress = (ProgressBar) findViewById(R.id.currentLevelProgress);
        final TextView levelPercent = (TextView) findViewById(R.id.currentLevelPercent);
        final TextView coinCount = (TextView) findViewById(R.id.coinCount);

        handler.post(new Runnable() {
            @Override
            public void run() {
                dh.createAllSlots(activity);
            }
        });

        final Runnable easterEvent = new Runnable() {
            @Override
            public void run() {
                EventHelper.checkForEasterEggs(activity);
                handler.postDelayed(this, DateHelper.MILLISECONDS_IN_SECOND * 60);
            }
        };
        handler.postDelayed(easterEvent, DateHelper.MILLISECONDS_IN_SECOND * 60);

        final Runnable everySecond = new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    public void run() {
                        dh.populateSlots(activity, findViewById(mainScroller));
                        updateVisitors();
                    }
                }).start();
                dh.updateCoinsGUI(coinCount);
                if (dh.updateLevelText(currentLevel, levelProgress, levelPercent) || needToRedrawSlots) {
                    dh.createAllSlots(activity);
                    needToRedrawSlots = false;
                }
                dh.updateFullscreen(activity);
                handler.postDelayed(this, DateHelper.MILLISECONDS_IN_SECOND);
            }
        };
        handler.post(everySecond);

        final Runnable everyTenSeconds = new Runnable() {
            @Override
            public void run() {
                int newVisitors = VisitorHelper.tryCreateRequiredVisitors();
                if (newVisitors == 1) {
                    ToastHelper.showToast(null, ToastHelper.LONG, getString(R.string.visitorArriving), true);
                } else if (newVisitors > 1) {
                    ToastHelper.showToast(null, ToastHelper.LONG, String.format(getString(R.string.visitorsArriving), newVisitors), true);
                }
                DisplayHelper.updateBonusChest((ImageView) activity.findViewById(R.id.bonus_chest));
                gph.UpdateQuest();
                handler.postDelayed(this, DateHelper.MILLISECONDS_IN_SECOND * 10);
            }
        };
        handler.postDelayed(everyTenSeconds, DateHelper.MILLISECONDS_IN_SECOND * 10);

        final Runnable everyMinute = new Runnable() {
            @Override
            public void run() {
                if (Trader_Stock.shouldRestock()) {
                    boolean taxPaid = PremiumHelper.payOutTax();
                    Trader_Stock.restockTraders();
                    ToastHelper.showPositiveToast(null, ToastHelper.LONG, getRestockText(taxPaid), true);
                }
                GooglePlayHelper.UpdateAchievements();
                WorkerHelper.checkForFinishedWorkers(activity);
                WorkerHelper.checkForFinishedHeroes(activity);
                dh.createAllSlots(activity);
                DisplayHelper.updateAssistantDisplay((RelativeLayout) activity.findViewById(R.id.assistant_container));
                handler.postDelayed(this, DateHelper.MILLISECONDS_IN_SECOND * 60);
            }
        };
        handler.postDelayed(everyMinute, DateHelper.MILLISECONDS_IN_SECOND * 5);
    }

    @Override
    protected void onDestroy() {
        Batch.onDestroy(this);
        super.onDestroy();
    }

    private String getRestockText(boolean taxPaid) {
        if (taxPaid) {
            return String.format(getString(R.string.restockTextWithTax),
                    PremiumHelper.getTaxAmount());
        } else {
            return getString(R.string.restockTextNoTax);
        }
    }

    public void openEventInfo(View v) {
        AlertDialogHelper.displayEventInfo(this);
    }

    private void updateVisitors() {
        dh.populateVisitorsContainer(getApplicationContext(), this, (LinearLayout) findViewById(R.id.visitors_container), (LinearLayout) findViewById(R.id.visitors_container_overflow));
    }

    public void claimAssistant(View v) {
        Player_Info lastClaimed = Select.from(Player_Info.class).where(Condition.prop("name").eq("LastAssistantClaim")).first();
        Player_Info totalClaimed = Select.from(Player_Info.class).where(Condition.prop("name").eq("TotalAssistantClaims")).first();
        Assistant assistant = Assistant.get(Select.from(Player_Info.class).where(Condition.prop("name").eq("ActiveAssistant")).first().getIntValue());
        if (assistant != null && lastClaimed != null) {
            if (lastClaimed.getLongValue() + assistant.getRewardFrequency() <= System.currentTimeMillis()) {
                Inventory.addItem((long) assistant.getRewardItem(), assistant.getRewardState(), assistant.getRewardQuantity() * assistant.getLevel(), false);
                lastClaimed.setLongValue(System.currentTimeMillis());
                lastClaimed.save();
                totalClaimed.setIntValue(totalClaimed.getIntValue() + 1);
                totalClaimed.save();

                Item item = Item.findById(Item.class, assistant.getRewardItem());
                ToastHelper.showPositiveToast(v, ToastHelper.SHORT, String.format(Locale.ENGLISH, getString(R.string.assistantClaimed),
                        assistant.getRewardQuantity() * assistant.getLevel(),
                        item.getFullName(this, assistant.getRewardState())), true);
                DisplayHelper.updateAssistantDisplay((RelativeLayout) findViewById(R.id.assistant_container));
            } else {
                ToastHelper.showTipToast(v, ToastHelper.SHORT, String.format(Locale.ENGLISH, getString(R.string.assistantTimeLeft),
                        DateHelper.getHoursMinsSecsRemaining(((lastClaimed.getLongValue() + assistant.getRewardFrequency()) - System.currentTimeMillis()))), false);
            }
        } else {
            startActivity(new Intent(this, AssistantActivity.class).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
        }
    }

    public void openMarket(View view) {
        startActivity(new Intent(this, MarketActivity.class).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
    }

    public void openInventory(View view) {
        startActivity(new Intent(this, InventoryActivity.class).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
    }

    public void openFurnace(View view) {
        startActivity(new Intent(this, FurnaceActivity.class).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
    }

    public void openAnvil(View view) {
        startActivity(new Intent(this, AnvilActivity.class).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
    }

    public void openTable(View view) {
        startActivity(new Intent(this, TableActivity.class).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
    }

    public void openEnchanting(View view) {
        startActivity(new Intent(this, EnchantingActivity.class).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
    }

    public void openSettings(View view) {
        startActivity(new Intent(this, SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
    }

    public void openTrophies(View view) {
        startActivity(new Intent(this, TrophyActivity.class).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
    }

    public void openStatistics(View view) {
        startActivity(new Intent(this, StatisticsActivity.class).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
    }

    public void openHelp(View view) {
        startActivity(new Intent(this, HelpActivity.class).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
    }

    public void openUpgrades(View view) {
        startActivity(new Intent(this, UpgradeActivity.class).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
    }

    public void openPremium(View view) {
        startActivity(new Intent(this, PremiumActivity.class).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
    }

    public void openAssistants(View view) {
        startActivity(new Intent(this, AssistantActivity.class).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
    }

    public void openWorkers(View view) {
        startActivity(new Intent(this, WorkerActivity.class).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
    }

    public void openBuyCoins(View view) {
        startActivity(new Intent(this, BuyCoinsActivity.class).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
    }

    public void openQuests(View view) {
        if (GooglePlayHelper.mGoogleApiClient.isConnected()) {
            startActivity(new Intent(this, QuestActivity.class).addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
        } else {
            ToastHelper.showErrorToast(null, ToastHelper.LONG, getString(R.string.questsNoConnection), false);
        }
    }

    public void openMessages(View view) {
        Intent intent = new Intent(this, MessagesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    public void clickBonusChest(View view) {
        if (Player_Info.isBonusReady()) {
            if (Player_Info.isPremium()) {
                callbackBonus();
            } else {
                AlertDialogHelper.confirmBonusAdvert(this, this);
            }
        } else {
            ToastHelper.showToast(null, ToastHelper.SHORT, String.format(getString(R.string.bonusTimeLeft),
                    DateHelper.getHoursMinsSecsRemaining(Player_Info.timeUntilBonusReady())), false);
        }
    }

    public void clickWindow(View view) {
        ToastHelper.showToast(null, ToastHelper.SHORT, getString(R.string.windowClick), false);
    }

    public void clickBookcase(View view) {
        int thisTip = prefs.getInt("nextTip", 0);
        List<String> tips = TextHelper.getTips(this);
        if (thisTip >= tips.size()) {
            thisTip = 0;
        }

        String tipMessage = "Tip " + (thisTip + 1) + "/" + tips.size() + ": " + tips.get(thisTip);
        ToastHelper.showTipToast(null, ToastHelper.LONG, tipMessage, false);
        prefs.edit().putInt("nextTip", ++thisTip).apply();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (GooglePlayHelper.IsConnected()) {
            Games.Quests.registerQuestUpdateListener(GooglePlayHelper.mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        GooglePlayHelper.ConnectionFailed(this, connectionResult);
    }

    @Override
    public void onConnectionSuspended(int i) {
        GooglePlayHelper.mGoogleApiClient.connect();
    }

    public void onQuestCompleted(Quest quest) {
        ToastHelper.showPositiveToast(null, ToastHelper.LONG, GooglePlayHelper.CompleteQuest(quest), true);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        GooglePlayHelper.ActivityResult(this, requestCode, resultCode);
    }

    public void callbackSpawn() {
        if (VisitorHelper.tryCreateVisitor()) {
            ToastHelper.showToast(null, ToastHelper.LONG, getString(R.string.bribeAdvertComplete), true);
        }
    }

    public void callbackBonus() {
        String rewardText = AdvertHelper.createAdvertReward(this);
        ToastHelper.showToast(null, ToastHelper.LONG, rewardText, true);

        Player_Info lastClaimed = Select.from(Player_Info.class).where(Condition.prop("name").eq("LastBonusClaimed")).first();
        lastClaimed.setLongValue(System.currentTimeMillis());
        lastClaimed.save();

        Player_Info timesClaimed = Select.from(Player_Info.class).where(Condition.prop("name").eq("BonusesClaimed")).first();
        timesClaimed.setIntValue(timesClaimed.getIntValue() + 1);
        timesClaimed.save();

        GooglePlayHelper.UpdateEvent(Constants.EVENT_CLAIM_BONUS, 1);
        DisplayHelper.updateBonusChest((ImageView) findViewById(R.id.bonus_chest));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Batch.onNewIntent(this, intent);
        super.onNewIntent(intent);
    }

    @Override
    public void onRedeemAutomaticOffer(Offer offer) {
        // Give resources & features contained in the campaign to the user
        String rewardMessage = offer.getOfferAdditionalParameters().get("reward_message");
        for (Resource resource : offer.getResources()) {
            if (resource.getReference().equals("LARGE_COIN_PACK")) {
                Inventory.addItem(Constants.ITEM_COINS, Constants.STATE_NORMAL, 3000);
                ToastHelper.showPositiveToast(null, Toast.LENGTH_SHORT, rewardMessage != null ? rewardMessage : "1000 coins rewarded!", true);
            }
        }
    }
}
