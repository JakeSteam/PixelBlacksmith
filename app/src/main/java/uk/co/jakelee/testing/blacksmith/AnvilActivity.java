package uk.co.jakelee.testing.blacksmith;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class AnvilActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anvil);
    }

    public void updateInterface() {
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
