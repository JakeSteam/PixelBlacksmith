package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.quest.Quests;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.GooglePlayHelper;
import uk.co.jakelee.blacksmith.helper.TutorialHelper;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Setting;

public class SettingsActivity extends Activity {
    private static DisplayHelper dh;
    final Handler handler = new Handler();

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
        final Runnable everySecond = new Runnable() {
            @Override
            public void run() {
                updateSignInVisibility();
                handler.postDelayed(this, DateHelper.MILLISECONDS_IN_SECOND);
            }
        };
        handler.post(everySecond);

        updateAdToggleVisibility();
    }

    @Override
    public void onStop() {
        super.onStop();

        handler.removeCallbacksAndMessages(null);
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

    private void updateAdToggleVisibility() {
        TableRow adRow = (TableRow) findViewById(R.id.adToggleLayout);
        adRow.setVisibility(Player_Info.isPremium() ? View.VISIBLE : View.GONE);
    }

    private void updateSignInVisibility() {
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

    private void displaySettingsList() {
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

        ImageView workerNotificationToggle = (ImageView) findViewById(R.id.workerNotificationToggleButton);
        boolean workerNotificationToggleValue = Setting.findById(Setting.class, Constants.SETTING_WORKER_NOTIFICATIONS).getBoolValue();
        workerNotificationToggle.setImageDrawable(workerNotificationToggleValue ? tick : cross);

        ImageView bonusNotificationToggle = (ImageView) findViewById(R.id.bonusNotificationToggleButton);
        boolean bonusNotificationToggleValue = Setting.findById(Setting.class, Constants.SETTING_BONUS_NOTIFICATIONS).getBoolValue();
        bonusNotificationToggle.setImageDrawable(bonusNotificationToggleValue ? tick : cross);

        ImageView notificationSoundToggle = (ImageView) findViewById(R.id.notificationSoundToggleButton);
        boolean notificationSoundToggleValue = Setting.findById(Setting.class, Constants.SETTING_NOTIFICATION_SOUNDS).getBoolValue();
        notificationSoundToggle.setImageDrawable(notificationSoundToggleValue ? tick : cross);

        ImageView adToggle = (ImageView) findViewById(R.id.turnOffAdsButton);
        boolean adToggleValue = Setting.findById(Setting.class, Constants.SETTING_DISABLE_ADS).getBoolValue();
        adToggle.setImageDrawable(adToggleValue ? tick : cross);

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
            case R.id.workerNotificationToggleButton:
                settingID = Constants.SETTING_WORKER_NOTIFICATIONS;
                break;
            case R.id.visitorNotificationToggleButton:
                settingID = Constants.SETTING_VISITOR_NOTIFICATIONS;
                break;
            case R.id.bonusNotificationToggleButton:
                settingID = Constants.SETTING_BONUS_NOTIFICATIONS;
                break;
            case R.id.notificationSoundToggleButton:
                settingID = Constants.SETTING_NOTIFICATION_SOUNDS;
                break;
            case R.id.turnOffAdsButton:
                settingID = Constants.SETTING_DISABLE_ADS;
        }

        if (settingID != null) {
            Setting settingToToggle = Setting.findById(Setting.class, settingID);
            settingToToggle.setBoolValue(!settingToToggle.getBoolValue());
            settingToToggle.save();

            displaySettingsList();
        }
    }

    public void prestigeClick(View view) {
        if (Player_Info.isPremium()) {
            AlertDialogHelper.confirmPrestige(getApplicationContext(), this);
        } else {
            Intent intent = new Intent(getApplicationContext(), PremiumActivity.class);
            startActivity(intent);
        }
    }

    public void openMessages(View view) {
        Intent intent = new Intent(getApplicationContext(), MessagesActivity.class);
        startActivity(intent);
    }

    public void openRating(View view) {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public void openTutorial(View view) {
        this.finish();
        TutorialHelper.currentlyInTutorial = true;
        this.finish();
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

    public void openQuests(View view) {
        if (GooglePlayHelper.mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Quests.getQuestsIntent(GooglePlayHelper.mGoogleApiClient, Quests.SELECT_ALL_QUESTS), GooglePlayHelper.RC_QUESTS);
        }
    }

    public void openSupportCode(View view) {
        AlertDialogHelper.enterSupportCode(getApplicationContext(), this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        GooglePlayHelper.SavedGamesIntent(getApplicationContext(), this, intent);
    }


    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Settings);
        startActivity(intent);
    }

    public void openSocialMedia(View view) {
        AlertDialogHelper.openSocialMedia(getApplicationContext(), this);
    }

    public void closePopup(View view) {
        finish();
    }
}
