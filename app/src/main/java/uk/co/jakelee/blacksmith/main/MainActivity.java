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

        dbh.updateCoinsGUI();
        dbh.UpdateLevelText();
        CreateSlots();

        final Runnable updateTask = new Runnable() {
            @Override
            public void run() {
                UpdateSlots();
                dbh.updateCoinsGUI();
            }
        };

        handler.postDelayed(updateTask, 1000);
    }

    private void CreateSlots() {
        dh.CreateSlotContainer(sellingSlots, dbh.getSlots("Selling"));
        dh.CreateSlotContainer(furnaceSlots, dbh.getSlots("Furnace"));
        dh.CreateSlotContainer(anvilSlots, dbh.getSlots("Anvil"));
        dh.CreateSlotContainer(mineSlots, dbh.getSlots("Mine"));
    }

    public void UpdateSlots() {
        dh.DepopulateSlotContainer(sellingSlots);
        dh.DepopulateSlotContainer(furnaceSlots);
        dh.DepopulateSlotContainer(anvilSlots);
        dh.DepopulateSlotContainer(mineSlots);

        dh.PopulateSlotContainer(sellingSlots, "Selling");
        dh.PopulateSlotContainer(furnaceSlots, "Furnace");
        dh.PopulateSlotContainer(anvilSlots, "Anvil");
        dh.PopulateSlotContainer(mineSlots, "Mine");
    }

    @Override
    protected void onResume() {
        super.onResume();
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
}
