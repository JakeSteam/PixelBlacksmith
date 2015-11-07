package uk.co.jakelee.blacksmith.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.sqlite.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper dbh = new DatabaseHelper(getApplicationContext());
        Item item = dbh.getItemById(2);
        updateInterface();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateInterface();
    }

    public void addCopperOre(View view) {
        int count = getIntSetting("copperOreCount", 0);
        setIntSetting("copperOreCount", ++count);

        updateInterface();
    }

    public void addTinOre(View view) {
        int count = getIntSetting("tinOreCount", 0);
        setIntSetting("tinOreCount", ++count);

        updateInterface();
    }

    public void addBronzeBar(View view) {
        int count = getIntSetting("bronzeBarCount", 0);
        setIntSetting("bronzeBarCount", ++count);

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
        copperOreCount.setText(Integer.toString(getIntSetting("copperOreCount", 0)));

        TextView tinOreCount = (TextView) findViewById(R.id.tinOreCountLabel);
        tinOreCount.setText(Integer.toString(getIntSetting("tinOreCount", 0)));

        TextView bronzeBarCount = (TextView) findViewById(R.id.bronzeBarCountLabel);
        bronzeBarCount.setText(Integer.toString(getIntSetting("bronzeBarCount", 0)));
    }

    public int getIntSetting(String variableName, int defaultValue) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getInt(variableName, defaultValue);
    }

    public void setIntSetting(String variableName, int value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(variableName, value);
        editor.commit();
    }

}
