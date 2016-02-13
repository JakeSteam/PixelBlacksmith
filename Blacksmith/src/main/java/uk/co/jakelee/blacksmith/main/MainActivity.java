package uk.co.jakelee.blacksmith.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DatabaseHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.NotificationHelper;
import uk.co.jakelee.blacksmith.helper.VisitorHelper;
import uk.co.jakelee.blacksmith.model.Location;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Setting;
import uk.co.jakelee.blacksmith.model.Shop_Stock;
import uk.co.jakelee.blacksmith.service.MusicService;

public class MainActivity extends AppCompatActivity {
    public static DisplayHelper dh;
    public static Handler handler = new Handler();

    public Intent musicService;
    public boolean musicServiceIsStarted = false;

    public static TextViewPixel coins;
    public static TextViewPixel level;
    public static LinearLayout visitorContainer;
    public static LinearLayout visitorContainerOverflow;

    public static RelativeLayout sellingSlots;
    public static RelativeLayout furnaceSlots;
    public static RelativeLayout anvilSlots;
    public static RelativeLayout mineSlots;
    public static RelativeLayout tableSlots;
    public static RelativeLayout enchantingSlots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dh = DisplayHelper.getInstance(getApplicationContext());
        musicService = new Intent(this, MusicService.class);

        coins = (TextViewPixel) findViewById(R.id.coinCount);
        level = (TextViewPixel) findViewById(R.id.currentLevel);
        visitorContainer = (LinearLayout) findViewById(R.id.visitors_container);
        visitorContainerOverflow = (LinearLayout) findViewById(R.id.visitors_container_overflow);

        sellingSlots = (RelativeLayout) findViewById(R.id.slots_inventory);
        furnaceSlots = (RelativeLayout) findViewById(R.id.slots_furnace);
        anvilSlots = (RelativeLayout) findViewById(R.id.slots_anvil);
        mineSlots = (RelativeLayout) findViewById(R.id.slots_mine);
        tableSlots = (RelativeLayout) findViewById(R.id.slots_table);
        enchantingSlots = (RelativeLayout) findViewById(R.id.slots_enchanting);

        if (Player_Info.listAll(Player_Info.class).size() == 0) {
            DatabaseHelper.initialSQL();
        }

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
                VisitorHelper.tryCreateVisitor();

                if (Setting.findById(Setting.class, Constants.SETTING_MUSIC).getBoolValue() && !musicServiceIsStarted) {
                    startService(musicService);
                    musicServiceIsStarted = true;
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Thread(new Runnable() {
            public void run() {
                if (Setting.findById(Setting.class, Constants.SETTING_MUSIC).getBoolValue() && !musicServiceIsStarted) {
                    startService(musicService);
                    musicServiceIsStarted = true;
                } else if (!Setting.findById(Setting.class, Constants.SETTING_MUSIC).getBoolValue() && musicServiceIsStarted) {
                    stopService(musicService);
                    musicServiceIsStarted = false;
                }
            }
        }).start();
    }

    @Override
    protected void onStop() {
        super.onStop();

        handler.removeCallbacksAndMessages(null);

        if (Setting.findById(Setting.class, Constants.SETTING_NOTIFICATIONS).getBoolValue()) {
            boolean notificationSound = Setting.findById(Setting.class, Constants.SETTING_NOTIFICATION_SOUNDS).getBoolValue();
            NotificationHelper.addRestockNotification(getApplicationContext(), notificationSound);
        }


        if (musicServiceIsStarted) {
            stopService(musicService);
            musicServiceIsStarted = false;
        }
    }

    private void setupRecurringEvents() {
        final Runnable updateMainUI = new Runnable() {
            @Override
            public void run() {
                updateSlots();
                updateVisitors();
                dh.updateCoinsGUI();
                dh.updateLevelText();
                handler.postDelayed(this, Constants.MILLISECONDS_BETWEEN_UI_REFRESHES);
            }
        };
        handler.post(updateMainUI);

        final Runnable checkRestocks = new Runnable() {
            @Override
            public void run() {
                if (Shop_Stock.shouldRestock()) {
                    Shop_Stock.restockShops();
                }
                handler.postDelayed(this, Constants.MILLISECONDS_BETWEEN_RESTOCK_CHECKS);
            }
        };
        handler.post(checkRestocks);
    }

    private void createSlots() {
        dh.createSlotContainer(sellingSlots, Location.getSlots(Constants.LOCATION_SELLING));
        dh.createSlotContainer(furnaceSlots, Location.getSlots(Constants.LOCATION_FURNACE));
        dh.createSlotContainer(anvilSlots, Location.getSlots(Constants.LOCATION_ANVIL));
        dh.createSlotContainer(mineSlots, Location.getSlots(Constants.LOCATION_MINE));
        dh.createSlotContainer(tableSlots, Location.getSlots(Constants.LOCATION_TABLE));
        dh.createSlotContainer(enchantingSlots, Location.getSlots(Constants.LOCATION_ENCHANTING));
    }

    public void updateSlots() {
        dh.depopulateSlotContainer(sellingSlots);
        dh.depopulateSlotContainer(furnaceSlots);
        dh.depopulateSlotContainer(anvilSlots);
        dh.depopulateSlotContainer(mineSlots);
        dh.depopulateSlotContainer(tableSlots);
        dh.depopulateSlotContainer(enchantingSlots);

        dh.populateSlotContainer(sellingSlots, Constants.LOCATION_SELLING);
        dh.populateSlotContainer(furnaceSlots, Constants.LOCATION_FURNACE);
        dh.populateSlotContainer(anvilSlots, Constants.LOCATION_ANVIL);
        dh.populateSlotContainer(mineSlots, Constants.LOCATION_MINE);
        dh.populateSlotContainer(tableSlots, Constants.LOCATION_TABLE);
        dh.populateSlotContainer(enchantingSlots, Constants.LOCATION_ENCHANTING);
    }

    public void updateVisitors() {
        visitorContainer.removeAllViews();
        visitorContainerOverflow.removeAllViews();
        dh.populateVisitorsContainer(getApplicationContext(), visitorContainer, visitorContainerOverflow);
    }

    public void openMine(View view) {
        Intent intent = new Intent(this, MineActivity.class);
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
}
