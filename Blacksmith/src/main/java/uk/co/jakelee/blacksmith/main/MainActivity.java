package uk.co.jakelee.blacksmith.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DatabaseHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.NotificationHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.helper.VisitorHelper;
import uk.co.jakelee.blacksmith.model.Location;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Setting;
import uk.co.jakelee.blacksmith.model.Trader_Stock;
import uk.co.jakelee.blacksmith.model.Upgrade;
import uk.co.jakelee.blacksmith.model.Visitor;
import uk.co.jakelee.blacksmith.service.MusicService;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    public static DisplayHelper dh;
    public static Handler handler = new Handler();
    public int newVisitors;

    public Intent musicService;
    public boolean musicServiceIsStarted = false;

    public static TextViewPixel coins;
    public static LinearLayout visitorContainer;
    public static LinearLayout visitorContainerOverflow;

    public static TextViewPixel level;
    public static ProgressBar levelProgress;
    public static TextViewPixel levelPercent;

    public static RelativeLayout sellingSlots;
    public static RelativeLayout furnaceSlots;
    public static RelativeLayout anvilSlots;
    public static RelativeLayout marketSlots;
    public static RelativeLayout tableSlots;
    public static RelativeLayout enchantingSlots;

    private GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 9001;
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = true;
    private boolean mSignInClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dh = DisplayHelper.getInstance(getApplicationContext());
        musicService = new Intent(this, MusicService.class);

        coins = (TextViewPixel) findViewById(R.id.coinCount);
        visitorContainer = (LinearLayout) findViewById(R.id.visitors_container);
        visitorContainerOverflow = (LinearLayout) findViewById(R.id.visitors_container_overflow);

        level = (TextViewPixel) findViewById(R.id.currentLevel);
        levelProgress = (ProgressBar) findViewById(R.id.currentLevelProgress);
        levelPercent = (TextViewPixel) findViewById(R.id.currentLevelPercent);

        sellingSlots = (RelativeLayout) findViewById(R.id.slots_inventory);
        furnaceSlots = (RelativeLayout) findViewById(R.id.slots_furnace);
        anvilSlots = (RelativeLayout) findViewById(R.id.slots_anvil);
        marketSlots = (RelativeLayout) findViewById(R.id.slots_market);
        tableSlots = (RelativeLayout) findViewById(R.id.slots_table);
        enchantingSlots = (RelativeLayout) findViewById(R.id.slots_enchanting);

        if (Player_Info.listAll(Player_Info.class).size() == 0) {
            DatabaseHelper.initialSQL();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .addApi(Drive.API).addScope(Drive.SCOPE_APPFOLDER)
                .build();

        createSlots();
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

        mGoogleApiClient.connect();
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
            ToastHelper.showToast(context, Toast.LENGTH_SHORT, String.format("Whilst you've been gone, %d visitor(s) have arrived.", newVisitors));
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

        mGoogleApiClient.disconnect();
    }

    private void setupRecurringEvents() {
        final Runnable updateMainUI = new Runnable() {
            @Override
            public void run() {
                updateSlots();
                updateVisitors();
                dh.updateCoinsGUI();
                dh.updateLevelText(getApplicationContext());
                handler.postDelayed(this, Constants.MILLISECONDS_BETWEEN_UI_REFRESHES);
            }
        };
        handler.post(updateMainUI);

        final Runnable checkRestocks = new Runnable() {
            @Override
            public void run() {
                if (Trader_Stock.shouldRestock()) {
                    Trader_Stock.restockTraders();
                }
                handler.postDelayed(this, Constants.MILLISECONDS_BETWEEN_RESTOCK_CHECKS);
            }
        };
        handler.post(checkRestocks);

        final Runnable checkVisitorSpawns = new Runnable() {
            @Override
            public void run() {
                int newVisitors = VisitorHelper.tryCreateRequiredVisitors();
                if (newVisitors > 0) {
                    ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, String.format("A visitor wanders into the shop...", newVisitors));
                }
                handler.postDelayed(this, Constants.MILLISECONDS_BETWEEN_VISITOR_SPAWN_CHECKS);
            }
        };
        handler.postDelayed(checkVisitorSpawns, Constants.MILLISECONDS_BETWEEN_VISITOR_SPAWN_CHECKS);
    }

    private void createSlots() {
        dh.createSlotContainer(sellingSlots, Location.getSlots(Constants.LOCATION_SELLING));
        dh.createSlotContainer(furnaceSlots, Location.getSlots(Constants.LOCATION_FURNACE));
        dh.createSlotContainer(anvilSlots, Location.getSlots(Constants.LOCATION_ANVIL));
        dh.createSlotContainer(marketSlots, Location.getSlots(Constants.LOCATION_MARKET));
        dh.createSlotContainer(tableSlots, Location.getSlots(Constants.LOCATION_TABLE));
        dh.createSlotContainer(enchantingSlots, Location.getSlots(Constants.LOCATION_ENCHANTING));
    }

    public void updateSlots() {
        dh.depopulateSlotContainer(sellingSlots);
        dh.depopulateSlotContainer(furnaceSlots);
        dh.depopulateSlotContainer(anvilSlots);
        dh.depopulateSlotContainer(marketSlots);
        dh.depopulateSlotContainer(tableSlots);
        dh.depopulateSlotContainer(enchantingSlots);

        dh.populateSlotContainer(sellingSlots, Constants.LOCATION_SELLING);
        dh.populateSlotContainer(furnaceSlots, Constants.LOCATION_FURNACE);
        dh.populateSlotContainer(anvilSlots, Constants.LOCATION_ANVIL);
        dh.populateSlotContainer(marketSlots, Constants.LOCATION_MARKET);
        dh.populateSlotContainer(tableSlots, Constants.LOCATION_TABLE);
        dh.populateSlotContainer(enchantingSlots, Constants.LOCATION_ENCHANTING);
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

    public void achieveTest (View v) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            // Call a Play Games services API method, for example:
            Games.Achievements.unlock(mGoogleApiClient, "CgkI6tnE2Y4OEAIQAg");
        } else {
            // Alternative implementation (or warn user that they must
            // sign in to use this feature)
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // The player is signed in. Hide the sign-in button and allow the
        // player to proceed.
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, "Sign in error")) {
                mResolvingConnectionFailure = false;
            }
        }

        // Put code here to display the sign-in button
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                BaseGameUtils.showActivityResultError(this,
                        requestCode, resultCode, R.string.unknown_error);
            }
        }
    }

    // Call when the sign-in button is clicked
    public void signInClicked(View v) {
        mSignInClicked = true;
        mGoogleApiClient.connect();
    }

    // Call when the sign-out button is clicked
    private void signOutClicked() {
        mSignInClicked = false;
        Games.signOut(mGoogleApiClient);
    }
}
