package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.games.Games;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.GooglePlayHelper;
import uk.co.jakelee.blacksmith.model.Player_Info;
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

    @Override
    public void onResume() {
        super.onResume();

        final Handler handler = new Handler();
        final Runnable everySecond = new Runnable() {
            @Override
            public void run() {
                updateSignInVisibility();
                handler.postDelayed(this, DateHelper.MILLISECONDS_IN_SECOND);
            }
        };
        handler.post(everySecond);
    }

    public void signIn(View v) {
        GooglePlayHelper.mGoogleApiClient.connect();

        Setting signIn = Setting.findById(Setting.class, Constants.SETTING_SIGN_IN);
        signIn.setBoolValue(true);
        signIn.save();

        finish();
    }

    public void signOut(View v) {
        Games.signOut(GooglePlayHelper.mGoogleApiClient);
        GooglePlayHelper.mGoogleApiClient.disconnect();

        Setting signIn = Setting.findById(Setting.class, Constants.SETTING_SIGN_IN);
        signIn.setBoolValue(false);
        signIn.save();
    }

    public void updateSignInVisibility() {
        RelativeLayout signInButton = (RelativeLayout) findViewById(R.id.signInButton);
        RelativeLayout signOutButton = (RelativeLayout) findViewById(R.id.signOutButton);
        LinearLayout playButtons = (LinearLayout) findViewById(R.id.playShortcuts);

        if (GooglePlayHelper.IsConnected()) {
            signInButton.setVisibility(View.GONE);
            signOutButton.setVisibility(View.VISIBLE);
            playButtons.setVisibility(View.VISIBLE);
        } else if (GooglePlayHelper.AreGooglePlayServicesInstalled(this)) {
            signInButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
            playButtons.setVisibility(View.GONE);
        } else {
            signInButton.setVisibility(View.GONE);
            signOutButton.setVisibility(View.GONE);
            playButtons.setVisibility(View.GONE);
        }

    }

    public void displaySettingsList() {
        Drawable tick = dh.createDrawable(R.drawable.tick, 50, 50);
        Drawable cross = dh.createDrawable(R.drawable.cross, 50, 50);

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

        TextView prestigeButton = (TextViewPixel) findViewById(R.id.prestigeButton);
        if (Player_Info.getPlayerLevel() >= Constants.PRESTIGE_LEVEL_REQUIRED) {
            prestigeButton.setVisibility(View.VISIBLE);
        }
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

    public void prestigeClick(View view) {
        AlertDialogHelper.confirmPrestige(getApplicationContext(), this);
    }

    public void openMessages(View view) {
        Intent intent = new Intent(getApplicationContext(), MessagesActivity.class);
        startActivity(intent);
    }

    public void openTutorial(View view) {
        this.finish();
        MainActivity.startTutorial();
    }

    public void openCredits(View view) {
        Intent intent = new Intent(getApplicationContext(), CreditsActivity.class);
        startActivity(intent);
    }

    public void openAchievements(View view) {
        if (GooglePlayHelper.mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Achievements.getAchievementsIntent(GooglePlayHelper.mGoogleApiClient), GooglePlayHelper.RC_ACHIEVEMENTS);
        }
    }

    public void openLeaderboards(View view) {
        if (GooglePlayHelper.mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(GooglePlayHelper.mGoogleApiClient), GooglePlayHelper.RC_LEADERBOARDS);
        }
    }

    public void openSavedGames(View view) {
        if (GooglePlayHelper.mGoogleApiClient.isConnected()) {
            Intent savedGamesIntent = Games.Snapshots.getSelectSnapshotIntent(GooglePlayHelper.mGoogleApiClient,
                    "Cloud Saves", true, true, 1);
            startActivityForResult(savedGamesIntent, GooglePlayHelper.RC_SAVED_GAMES);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        GooglePlayHelper.SavedGamesIntent(getApplicationContext(), intent);
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
