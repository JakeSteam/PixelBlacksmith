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
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.UpgradeHelper;
import uk.co.jakelee.blacksmith.helper.VisitorHelper;
import uk.co.jakelee.blacksmith.model.Location;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Visitor;

public class MainActivity extends AppCompatActivity {
    public static DisplayHelper dh;
    public static Handler handler = new Handler();

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
            UpgradeHelper.initialSQL();
        }

        dh.updateCoinsGUI();
        dh.updateLevelText();
        createSlots();
        updateVisitors();
    }

    @Override
    protected void onStart() {
        super.onStart();
        final Runnable updateTask = new Runnable() {
            @Override
            public void run() {
                updateSlots();
                updateVisitors();
                dh.updateCoinsGUI();
                handler.postDelayed(this, Constants.MILLISECONDS_BETWEEN_REFRESHES);
            }
        };

        if (Visitor.count(Visitor.class) < Constants.MAXIMUM_VISITORS) {
            VisitorHelper.createNewVisitor();
        }

        handler.postDelayed(updateTask, Constants.MILLISECONDS_BETWEEN_REFRESHES);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
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

    public void openMenu(View view) {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
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
}
