package uk.co.jakelee.testing.anothertest;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class FurnaceActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furnace);

        updateInterface();
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

    public void createBronzeBar(View view) {
        int copperCount = getIntSetting("copperOreCount", 0);
        int tinCount = getIntSetting("tinOreCount", 0);
        int bronzeCount = getIntSetting("bronzeBarCount", 0);

        if (copperCount >= 1 && tinCount >= 1) {
            copperCount--;
            tinCount--;
            bronzeCount++;
        }

        setIntSetting("copperOreCount", copperCount);
        setIntSetting("tinOreCount", tinCount);
        setIntSetting("bronzeBarCount", bronzeCount);

        updateInterface();
    }

    public void updateInterface() {
        TextView copperOreCount = (TextView) findViewById(R.id.copperOreCountLabel);
        copperOreCount.setText(Integer.toString(getIntSetting("copperOreCount", 0)));

        TextView tinOreCount = (TextView) findViewById(R.id.tinOreCountLabel);
        tinOreCount.setText(Integer.toString(getIntSetting("tinOreCount", 0)));

        TextView bronzeBarCount = (TextView) findViewById(R.id.bronzeBarCountLabel);
        bronzeBarCount.setText(Integer.toString(getIntSetting("bronzeBarCount", 0)));
    }
}
