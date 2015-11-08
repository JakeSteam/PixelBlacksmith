package uk.co.jakelee.blacksmith.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import uk.co.jakelee.blacksmith.sqlite.DatabaseHelper;

public class MainActivity extends AppCompatActivity {
    public static DatabaseHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbh = new DatabaseHelper(getApplicationContext());

        updateInterface();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateInterface();
    }

    public void addCopperOre(View view) {
        dbh.increaseInventoryQuantity(1, 1);

        updateInterface();
    }

    public void addTinOre(View view) {
        dbh.increaseInventoryQuantity(2, 1);

        updateInterface();
    }

    public void addBronzeBar(View view) {
        dbh.increaseInventoryQuantity(11, 1);

        updateInterface();
    }

    public void openFurnace(View view) {
        Intent intent = new Intent(this, FurnaceActivity.class);
        startActivity(intent);
    }

    public void openAnvil(View view) {
        Intent intent = new Intent(this, AnvilActivity.class);
        startActivity(intent);
    }

    public void updateInterface() {
        TextView copperOreCount = (TextView) findViewById(R.id.copperOreCountLabel);
        copperOreCount.setText(Integer.toString(dbh.getInventoryByItem(1).getQuantity()));

        TextView tinOreCount = (TextView) findViewById(R.id.tinOreCountLabel);
        tinOreCount.setText(Integer.toString(dbh.getInventoryByItem(2).getQuantity()));

        TextView bronzeBarCount = (TextView) findViewById(R.id.bronzeBarCountLabel);
        bronzeBarCount.setText(Integer.toString(dbh.getInventoryByItem(11).getQuantity()));
    }


}
