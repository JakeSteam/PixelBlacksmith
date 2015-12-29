package uk.co.jakelee.blacksmith.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.DatabaseHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.model.Inventory;

public class MainActivity extends AppCompatActivity {
    public static DatabaseHelper dbh;
    public static DisplayHelper dh;
    public static Handler handler = new Handler();

    public static TextView coins;
    public static TextView level;

    public static RelativeLayout sellingSlots;
    public static RelativeLayout furnaceSlots;
    public static RelativeLayout anvilSlots;
    public static RelativeLayout mineSlots;
    public static RelativeLayout tableSlots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbh = DatabaseHelper.getInstance(getApplicationContext());
        dh = DisplayHelper.getInstance(getApplicationContext());

        coins = (TextView) findViewById(R.id.coinCount);
        level = (TextView) findViewById(R.id.currentLevel);
        sellingSlots = (RelativeLayout) findViewById(R.id.slots_inventory);
        furnaceSlots = (RelativeLayout) findViewById(R.id.slots_furnace);
        anvilSlots = (RelativeLayout) findViewById(R.id.slots_anvil);
        mineSlots = (RelativeLayout) findViewById(R.id.slots_mine);
        tableSlots = (RelativeLayout) findViewById(R.id.slots_table);

        dbh.updateCoinsGUI();
        dbh.updateLevelText();
        createSlots();

        /*Shop shop = new Shop(0L, 0, 3, "Poor Ore", "Full of low quality ore.", 1, 1);
        Shop shop2 = new Shop(1L, 0, 3, "Less Poor Ore", "The ore here is not so poor.", 5, 1);
        Shop shop3 = new Shop(2L, 0, 3, "Rare Ore", "This shop is too rare to be found.", 5, 0);
        Shop shop4 = new Shop(3L, 0, 3, "Average Ore", "The ore in store is not too poor.", 10, 1);
        Shop shop5 = new Shop(4L, 0, 3, "Silver Miner", "Cor, ore!", 20, 1);

        shop.save();
        shop2.save();
        shop3.save();
        shop4.save();
        shop5.save();*/

        Inventory inventory = new Inventory(52L, 1500, 1);
        inventory.save();
    }

    @Override
    protected void onStart() {
        super.onStart();
        final Runnable updateTask = new Runnable() {
            @Override
            public void run() {
                updateSlots();
                dbh.updateCoinsGUI();
                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(updateTask, 1000);
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
        dh.createSlotContainer(sellingSlots, dbh.getSlots("Selling"));
        dh.createSlotContainer(furnaceSlots, dbh.getSlots("Furnace"));
        dh.createSlotContainer(anvilSlots, dbh.getSlots("Anvil"));
        dh.createSlotContainer(mineSlots, dbh.getSlots("Mine"));
        dh.createSlotContainer(tableSlots, dbh.getSlots("Table"));
    }

    public void updateSlots() {
        dh.depopulateSlotContainer(sellingSlots);
        dh.depopulateSlotContainer(furnaceSlots);
        dh.depopulateSlotContainer(anvilSlots);
        dh.depopulateSlotContainer(mineSlots);
        dh.depopulateSlotContainer(tableSlots);

        dh.populateSlotContainer(sellingSlots, "Selling");
        dh.populateSlotContainer(furnaceSlots, "Furnace");
        dh.populateSlotContainer(anvilSlots, "Anvil");
        dh.populateSlotContainer(mineSlots, "Mine");
        dh.populateSlotContainer(tableSlots, "Table");
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
}
