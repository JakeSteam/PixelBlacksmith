package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DatabaseHelper;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.GooglePlayHelper;
import uk.co.jakelee.blacksmith.helper.NotificationHelper;
import uk.co.jakelee.blacksmith.helper.PremiumHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.helper.TutorialHelper;
import uk.co.jakelee.blacksmith.helper.VisitorHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Setting;
import uk.co.jakelee.blacksmith.model.Trader_Stock;
import uk.co.jakelee.blacksmith.model.Upgrade;
import uk.co.jakelee.blacksmith.model.Visitor;
import uk.co.jakelee.blacksmith.service.MusicService;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final Handler handler = new Handler();
    public static TextViewPixel coins;
    public static TextViewPixel level;
    public static ProgressBar levelProgress;
    public static TextViewPixel levelPercent;
    private static DisplayHelper dh;
    private static Activity mainActivity;
    private static HorizontalScrollView mainScroller;
    private static LinearLayout visitorContainer;
    private static LinearLayout visitorContainerOverflow;
    private int newVisitors;
    private Intent musicService;
    private boolean musicServiceIsStarted = false;

    public static int ANVIL_TIER = Constants.TIER_MIN;
    public static int ANVIL_ITEM = 0;

    public static void startFirstTutorial() {
        mainScroller.scrollTo(0, 0);

        TutorialHelper th = new TutorialHelper(Constants.STAGE_1_MAIN);
        th.addTutorialNoOverlay(mainActivity, visitorContainer, R.string.tutorialIntro, R.string.tutorialIntroText, false);
        th.addTutorial(mainActivity, coins, R.string.tutorialCoins, R.string.tutorialCoinsText, false);
        th.addTutorial(mainActivity, level, R.string.tutorialLevel, R.string.tutorialLevelText, false);
        th.addTutorialRectangle(mainActivity, visitorContainer, R.string.tutorialVisitor, R.string.tutorialVisitorText, true);
        th.start(mainActivity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dh = DisplayHelper.getInstance(getApplicationContext());
        musicService = new Intent(this, MusicService.class);

        assignUIElements();
        checkFirstRun();

        GooglePlayHelper.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .addApi(Drive.API).addScope(Drive.SCOPE_APPFOLDER)
                .build();

        dh.createAllSlots(this);
        ratingPrompt();
    }

    private void ratingPrompt() {
        AppRate.with(this)
                .setInstallDays(5)
                .setLaunchTimes(4)
                .setRemindInterval(3)
                .setShowLaterButton(true)
                .setOnClickButtonListener(new OnClickButtonListener() {
                    @Override
                    public void onClickButton(int which) {
                        if (which == -1) {
                            Inventory.addItem(Constants.ITEM_COINS, Constants.STATE_NORMAL, 500);
                        }
                    }
                })
                .monitor();

        AppRate.showRateDialogIfMeetsConditions(this);
    }

    private void assignUIElements() {
        mainActivity = this;
        mainScroller = (HorizontalScrollView) findViewById(R.id.mainScroller);

        coins = (TextViewPixel) findViewById(R.id.coinCount);
        visitorContainer = (LinearLayout) findViewById(R.id.visitors_container);
        visitorContainerOverflow = (LinearLayout) findViewById(R.id.visitors_container_overflow);

        level = (TextViewPixel) findViewById(R.id.currentLevel);
        levelProgress = (ProgressBar) findViewById(R.id.currentLevelProgress);
        levelPercent = (TextViewPixel) findViewById(R.id.currentLevelPercent);
    }

    private void checkFirstRun() {
        SharedPreferences prefs = getSharedPreferences("uk.co.jakelee.blacksmith", MODE_PRIVATE);
        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) == DatabaseHelper.DB_EMPTY) {
            DatabaseHelper.initialSQL();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V1_0_0).apply();

            TutorialHelper.currentlyInTutorial = true;
        }

        /*if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) == DatabaseHelper.DB_V1_0_0) {
            DatabaseHelper.patch100to101();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V1_0_1).apply();
        }*/
    }

    private void startSecondTutorial(View v) {
        findViewById(R.id.mainScroller).scrollTo(dh.convertDpToPixel(400), 0);

        TutorialHelper th = new TutorialHelper(Constants.STAGE_5_MAIN);
        th.addTutorialRectangle(mainActivity, findViewById(R.id.slots_furnace), R.string.tutorialFurnaceSlots, R.string.tutorialFurnaceSlotsText, false);
        th.addTutorial(mainActivity, findViewById(R.id.open_furnace), R.string.tutorialFurnace, R.string.tutorialFurnaceText, true);
        th.start(mainActivity);
    }

    private void startThirdTutorial() {
        findViewById(R.id.mainScroller).scrollTo(dh.convertDpToPixel(680), 0);

        TutorialHelper th = new TutorialHelper(Constants.STAGE_7_MAIN);
        th.addTutorial(mainActivity, findViewById(R.id.slots_anvil), R.string.tutorialMainAnvil, R.string.tutorialMainAnvilText, false);
        th.addTutorial(mainActivity, findViewById(R.id.open_anvil), R.string.tutorialMainFurther, R.string.tutorialMainFurtherText, true);
        th.start(mainActivity);
    }

    private void startFourthTutorial() {
        findViewById(R.id.mainScroller).scrollTo(dh.convertDpToPixel(230), 0);

        TutorialHelper th = new TutorialHelper(Constants.STAGE_9_MAIN);
        th.addTutorialNoOverlay(mainActivity, findViewById(R.id.open_market), R.string.tutorialMainInfo, R.string.tutorialMainInfoText, false, Gravity.TOP);
        th.addTutorial(mainActivity, findViewById(R.id.open_market), R.string.tutorialMainMarket, R.string.tutorialMainMarketText, true, Gravity.TOP);
        th.start(mainActivity);

        TutorialHelper.currentStage = Constants.STAGE_10_MAIN;
    }

    private void startFifthTutorial() {
        findViewById(R.id.mainScroller).scrollTo(dh.convertDpToPixel(860), 0);

        TutorialHelper th = new TutorialHelper(Constants.STAGE_10_MAIN);
        th.addTutorial(mainActivity, findViewById(R.id.open_settings), R.string.tutorialMainSettings, R.string.tutorialMainSettingsText, false, Gravity.TOP);
        th.addTutorial(mainActivity, findViewById(R.id.open_help), R.string.tutorialMainHelp, R.string.tutorialMainHelpText, true, Gravity.TOP);
        th.start(mainActivity);

        TutorialHelper.currentlyInTutorial = false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Run background tasks and organise music
        new Thread(new Runnable() {
            public void run() {
                setupRecurringEvents();
                NotificationHelper.clearNotifications(getApplicationContext());
                if (Setting.findById(Setting.class, Constants.SETTING_MUSIC).getBoolValue() && !musicServiceIsStarted) {
                    startService(musicService);
                    musicServiceIsStarted = true;
                }
            }
        }).start();

        if (Setting.findById(Setting.class, Constants.SETTING_SIGN_IN).getBoolValue() && GooglePlayHelper.AreGooglePlayServicesInstalled(this)) {
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
        final Context context = this;

        new Thread(new Runnable() {
            public void run() {
                newVisitors = VisitorHelper.tryCreateRequiredVisitors();

                if (Setting.findById(Setting.class, Constants.SETTING_MUSIC).getBoolValue() && !musicServiceIsStarted) {
                    startService(musicService);
                    musicServiceIsStarted = true;
                } else if (!Setting.findById(Setting.class, Constants.SETTING_MUSIC).getBoolValue() && musicServiceIsStarted) {
                    stopService(musicService);
                    musicServiceIsStarted = false;
                }
            }
        }).start();

        if (newVisitors > 0) {
            ToastHelper.showToast(context, Toast.LENGTH_SHORT, String.format(getString(R.string.visitorArrived), newVisitors), true);
            newVisitors = 0;
        }

        if (TutorialHelper.currentlyInTutorial) {
            if (TutorialHelper.currentStage <= Constants.STAGE_1_MAIN) {
                startFirstTutorial();
            } else if (TutorialHelper.currentStage == Constants.STAGE_4_VISITOR) {
                startSecondTutorial(new View(this));
            } else if (TutorialHelper.currentStage == Constants.STAGE_6_FURNACE) {
                startThirdTutorial();
            } else if (TutorialHelper.currentStage == Constants.STAGE_8_ANVIL) {
                startFourthTutorial();
            } else if (TutorialHelper.currentStage == Constants.STAGE_9_MAIN) {
                startFifthTutorial();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        handler.removeCallbacksAndMessages(null);

        if (Setting.findById(Setting.class, Constants.SETTING_RESTOCK_NOTIFICATIONS).getBoolValue()) {
            boolean notificationSound = Setting.findById(Setting.class, Constants.SETTING_NOTIFICATION_SOUNDS).getBoolValue();
            NotificationHelper.addRestockNotification(getApplicationContext(), notificationSound);
        }

        if (Setting.findById(Setting.class, Constants.SETTING_VISITOR_NOTIFICATIONS).getBoolValue() && Visitor.count(Visitor.class) < Upgrade.getValue("Maximum Visitors")) {
            boolean notificationSound = Setting.findById(Setting.class, Constants.SETTING_NOTIFICATION_SOUNDS).getBoolValue();
            NotificationHelper.addVisitorNotification(getApplicationContext(), notificationSound);
        }


        if (musicServiceIsStarted) {
            stopService(musicService);
            musicServiceIsStarted = false;
        }

        GooglePlayHelper.mGoogleApiClient.disconnect();
    }

    private void setupRecurringEvents() {
        final Activity activity = this;

        final Runnable everySecond = new Runnable() {
            @Override
            public void run() {
                dh.populateSlots(findViewById(R.id.mainScroller));
                updateVisitors();
                dh.updateCoinsGUI();
                dh.updateLevelText(getApplicationContext());
                handler.postDelayed(this, DateHelper.MILLISECONDS_IN_SECOND);
            }
        };
        handler.post(everySecond);

        final Runnable everyTenSeconds = new Runnable() {
            @Override
            public void run() {
                int newVisitors = VisitorHelper.tryCreateRequiredVisitors();
                if (newVisitors > 0) {
                    ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_LONG, String.format(getString(R.string.visitorArriving), newVisitors), true);
                }
                handler.postDelayed(this, DateHelper.MILLISECONDS_IN_SECOND * 10);
            }
        };
        handler.postDelayed(everyTenSeconds, DateHelper.MILLISECONDS_IN_SECOND * 10);

        final Runnable everyMinute = new Runnable() {
            @Override
            public void run() {
                dh.createAllSlots(activity);
                if (Trader_Stock.shouldRestock()) {
                    Trader_Stock.restockTraders();
                    boolean taxPaid = PremiumHelper.payOutTax();
                    ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_LONG, getRestockText(taxPaid), true);
                }
                GooglePlayHelper.UpdateAchievements();
                handler.postDelayed(this, DateHelper.MILLISECONDS_IN_SECOND * 60);
            }
        };
        handler.postDelayed(everyMinute, DateHelper.MILLISECONDS_IN_SECOND * 60);
    }

    private String getRestockText(boolean taxPaid) {
        if (taxPaid) {
            return String.format(getString(R.string.restockTextWithPremium),
                    PremiumHelper.getTaxAmount());
        } else {
            return getString(R.string.restockTextNoPremium);
        }
    }

    private void updateVisitors() {
        visitorContainer.removeAllViews();
        visitorContainerOverflow.removeAllViews();
        dh.populateVisitorsContainer(getApplicationContext(), MainActivity.this, visitorContainer, visitorContainerOverflow);
    }

    public void openMarket(View view) {
        Intent intent = new Intent(this, MarketActivity.class);
        startActivity(intent);
    }

    public void openInventory(View view) {
        Intent intent = new Intent(this, InventoryActivity.class);
        startActivity(intent);
    }

    public void openFurnace(View view) {
        Intent intent = new Intent(this, FurnaceActivity.class);
        startActivity(intent);
    }

    public void openAnvil(View view) {
        Intent intent = new Intent(this, AnvilActivity.class);
        startActivity(intent);
    }

    public void openTable(View view) {
        Intent intent = new Intent(this, TableActivity.class);
        startActivity(intent);
    }

    public void openEnchanting(View view) {
        Intent intent = new Intent(this, EnchantingActivity.class);
        startActivity(intent);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void openTrophies(View view) {
        Intent intent = new Intent(this, TrophyActivity.class);
        startActivity(intent);
    }

    public void openStatistics(View view) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

    public void openUpgrades(View view) {
        Intent intent = new Intent(this, UpgradeActivity.class);
        startActivity(intent);
    }

    public void openPremium(View view) {
        Intent intent = new Intent(this, PremiumActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        GooglePlayHelper.ConnectionFailed(this, connectionResult);
    }

    @Override
    public void onConnectionSuspended(int i) {
        GooglePlayHelper.mGoogleApiClient.connect();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        GooglePlayHelper.ActivityResult(this, requestCode, resultCode);
    }
}
