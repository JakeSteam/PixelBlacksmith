package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;
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
import uk.co.jakelee.blacksmith.helper.VisitorHelper;
import uk.co.jakelee.blacksmith.model.Setting;
import uk.co.jakelee.blacksmith.model.Trader_Stock;
import uk.co.jakelee.blacksmith.model.Upgrade;
import uk.co.jakelee.blacksmith.model.Visitor;
import uk.co.jakelee.blacksmith.service.MusicService;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public static DisplayHelper dh;
    public static Handler handler = new Handler();
    public static TextViewPixel coins;
    public static LinearLayout visitorContainer;
    public static LinearLayout visitorContainerOverflow;
    public static TextViewPixel level;
    public static ProgressBar levelProgress;
    public static TextViewPixel levelPercent;
    public int newVisitors;
    public Intent musicService;
    public boolean musicServiceIsStarted = false;

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
        TourGuide.init(this)
                .with(TourGuide.Technique.Click)
                .setToolTip(new ToolTip().setTitle("Visitors").setDescription("Tap the visitor to get started"))
                .setOverlay(new Overlay())
                .playOn(coins);
    }

    private void assignUIElements() {
        coins = (TextViewPixel) findViewById(R.id.coinCount);
        visitorContainer = (LinearLayout) findViewById(R.id.visitors_container);
        visitorContainerOverflow = (LinearLayout) findViewById(R.id.visitors_container_overflow);

        level = (TextViewPixel) findViewById(R.id.currentLevel);
        levelProgress = (ProgressBar) findViewById(R.id.currentLevelProgress);
        levelPercent = (TextViewPixel) findViewById(R.id.currentLevelPercent);
    }

    private void checkFirstRun() {
        SharedPreferences prefs = getSharedPreferences("uk.co.jakelee.blacksmith", MODE_PRIVATE);
        if (prefs.getBoolean("firstrun", true)) {
            DatabaseHelper.initialSQL();
            prefs.edit().putBoolean("firstrun", false).apply();
        }
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
            ToastHelper.showToast(context, Toast.LENGTH_SHORT, String.format(getString(R.string.visitorArrived), newVisitors));
            newVisitors = 0;
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
                    ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_LONG, String.format(getString(R.string.visitorArriving), newVisitors));
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
                    ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_LONG, getRestockText(taxPaid));
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

    public void updateVisitors() {
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
