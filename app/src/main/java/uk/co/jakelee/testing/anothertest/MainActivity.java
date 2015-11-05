package uk.co.jakelee.testing.anothertest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "uk.co.jakelee.testing.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setIntSetting("copperOreCount", 1);
        setIntSetting("tinOreCount", 2);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView copperOreCount = (TextView) findViewById(R.id.copperOreCountLabel);
        copperOreCount.setText(Integer.toString(getIntSetting("copperOreCount", 0)));

        TextView tinOreCount = (TextView) findViewById(R.id.tinOreCountLabel);
        tinOreCount.setText(Integer.toString(getIntSetting("tinOreCount", 0)));
    }

    public void openFurnace(View view) {
        Intent intent = new Intent(this, FurnaceActivity.class);
        String message = "This is a furnace";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void openAnvil(View view) {
        Intent intent = new Intent(this, FurnaceActivity.class);
        String message = "This is an anvil";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
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
