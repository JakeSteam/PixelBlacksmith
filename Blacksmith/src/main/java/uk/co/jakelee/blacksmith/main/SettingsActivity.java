package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.model.Setting;

public class SettingsActivity extends Activity {
    public static DisplayHelper dh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        dh = DisplayHelper.getInstance(getApplicationContext());

        displaySettingsList();
    }

    public void displaySettingsList() {
        Drawable tick = dh.createDrawable(R.drawable.tick, 64, 64);
        Drawable cross = dh.createDrawable(R.drawable.cross, 64, 64);

        ImageView soundToggle = (ImageView) findViewById(R.id.soundToggleButton);
        boolean soundToggleValue = Setting.findById(Setting.class, Constants.SETTING_SOUNDS).getBoolValue();
        soundToggle.setImageDrawable(soundToggleValue ? tick : cross);

        ImageView musicToggle = (ImageView) findViewById(R.id.musicToggleButton);
        boolean musicToggleValue = Setting.findById(Setting.class, Constants.SETTING_MUSIC).getBoolValue();
        musicToggle.setImageDrawable(musicToggleValue ? tick : cross);
    }

    public void toggleSetting(View v) {
        Long settingID = null;
        switch (v.getId()) {
            case R.id.soundToggleButton:
                settingID = Constants.SETTING_SOUNDS;
                break;
            case R.id.musicToggleButton:
                settingID = Constants.SETTING_MUSIC;
                break;
        }

        if (settingID != null) {
            Setting settingToToggle = Setting.findById(Setting.class, settingID);
            settingToToggle.setBoolValue(!settingToToggle.getBoolValue());
            settingToToggle.save();

            displaySettingsList();
        }
    }

    public void closePopup(View view) {
        finish();
    }
}
