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
