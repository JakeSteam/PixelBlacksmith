package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.games.Games;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.GooglePlayHelper;
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

        ImageView restockNotificationToggle = (ImageView) findViewById(R.id.restockNotificationToggleButton);
        boolean restockNotificationToggleValue = Setting.findById(Setting.class, Constants.SETTING_RESTOCK_NOTIFICATIONS).getBoolValue();
        restockNotificationToggle.setImageDrawable(restockNotificationToggleValue ? tick : cross);

        ImageView visitorNotificationToggle = (ImageView) findViewById(R.id.visitorNotificationToggleButton);
        boolean visitorNotificationToggleValue = Setting.findById(Setting.class, Constants.SETTING_VISITOR_NOTIFICATIONS).getBoolValue();
        visitorNotificationToggle.setImageDrawable(visitorNotificationToggleValue ? tick : cross);

        ImageView notificationSoundToggle = (ImageView) findViewById(R.id.notificationSoundToggleButton);
        boolean notificationSoundToggleValue = Setting.findById(Setting.class, Constants.SETTING_NOTIFICATION_SOUNDS).getBoolValue();
        notificationSoundToggle.setImageDrawable(notificationSoundToggleValue ? tick : cross);
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
            case R.id.restockNotificationToggleButton:
                settingID = Constants.SETTING_RESTOCK_NOTIFICATIONS;
                break;
            case R.id.notificationSoundToggleButton:
                settingID = Constants.SETTING_NOTIFICATION_SOUNDS;
                break;
            case R.id.visitorNotificationToggleButton:
                settingID = Constants.SETTING_VISITOR_NOTIFICATIONS;
                break;
        }

        if (settingID != null) {
            Setting settingToToggle = Setting.findById(Setting.class, settingID);
            settingToToggle.setBoolValue(!settingToToggle.getBoolValue());
            settingToToggle.save();

            displaySettingsList();
        }
    }

    public void openCredits (View view) {
        Intent intent = new Intent(getApplicationContext(), CreditsActivity.class);
        startActivity(intent);
    }

    public void openAchievements (View view) {
        startActivityForResult(Games.Achievements.getAchievementsIntent(GooglePlayHelper.mGoogleApiClient), 123);
    }

    public void openLeaderboards (View view) {
        startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(GooglePlayHelper.mGoogleApiClient), 234);
    }

    public void openSavedGames (View view) {
        Intent savedGamesIntent = Games.Snapshots.getSelectSnapshotIntent(GooglePlayHelper.mGoogleApiClient,
                "Cloud Saves", false, false, 1);
        startActivityForResult(savedGamesIntent, 12345);
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Settings);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
