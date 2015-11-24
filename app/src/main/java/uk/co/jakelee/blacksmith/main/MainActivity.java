package uk.co.jakelee.blacksmith.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import uk.co.jakelee.blacksmith.helper.DatabaseHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;

public class MainActivity extends AppCompatActivity {
    public static DatabaseHelper dbh;
    public static DisplayHelper dh;
    public static TextView coins;
    public static TextView level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbh = new DatabaseHelper(getApplicationContext());
        dh = new DisplayHelper(getApplicationContext());

        coins = (TextView) findViewById(R.id.coinCount);
        coins.setText(dbh.getCoins() + " coins");
        level = (TextView) findViewById(R.id.currentLevel);
        dbh.UpdateLevelText();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
