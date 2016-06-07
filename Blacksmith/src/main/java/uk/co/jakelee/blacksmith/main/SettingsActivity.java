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
import android.widget.TextView;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.quest.Quests;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.GooglePlayHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
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
        dh.updateFullscreen(this);

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
        LinearLayout adRow = (LinearLayout) findViewById(R.id.turnOffAdsToggle);
        adRow.setVisibility(Player_Info.isPremium() ? View.VISIBLE : View.GONE);
    }

    private void updateSignInVisibility() {
        TextView playHeader = (TextView) findViewById(R.id.googlePlayHeader);
        RelativeLayout signInButton = (RelativeLayout) findViewById(R.id.signInButton);
        RelativeLayout signOutButton = (RelativeLayout) findViewById(R.id.signOutButton);
        LinearLayout playButtons = (LinearLayout) findViewById(R.id.playShortcuts);

        if (GooglePlayHelper.IsConnected()) {
            playHeader.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.GONE);
            signOutButton.setVisibility(View.VISIBLE);
            playButtons.setVisibility(View.VISIBLE);
        } else if (GooglePlayHelper.AreGooglePlayServicesInstalled(this)) {
            playHeader.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
            playButtons.setVisibility(View.GONE);
        } else {
            playHeader.setVisibility(View.GONE);
            signInButton.setVisibility(View.GONE);
            signOutButton.setVisibility(View.GONE);
            playButtons.setVisibility(View.GONE);
        }

    }

    private void displaySettingsList() {
        Drawable tick = dh.createDrawable(R.drawable.tick, 50, 50);
        Drawable cross = dh.createDrawable(R.drawable.cross, 50, 50);

        ImageView soundToggle = (ImageView) findViewById(R.id.soundToggleButton);
        boolean soundToggleValue = Setting.getSafeBoolean(Constants.SETTING_SOUNDS);
        soundToggle.setImageDrawable(soundToggleValue ? tick : cross);

        ImageView musicToggle = (ImageView) findViewById(R.id.musicToggleButton);
        boolean musicToggleValue = Setting.getSafeBoolean(Constants.SETTING_MUSIC);
        musicToggle.setImageDrawable(musicToggleValue ? tick : cross);

        ImageView restockNotificationToggle = (ImageView) findViewById(R.id.restockNotificationToggleButton);
        boolean restockNotificationToggleValue = Setting.getSafeBoolean(Constants.SETTING_RESTOCK_NOTIFICATIONS);
        restockNotificationToggle.setImageDrawable(restockNotificationToggleValue ? tick : cross);

        ImageView visitorNotificationToggle = (ImageView) findViewById(R.id.visitorNotificationToggleButton);
        boolean visitorNotificationToggleValue = Setting.getSafeBoolean(Constants.SETTING_VISITOR_NOTIFICATIONS);
        visitorNotificationToggle.setImageDrawable(visitorNotificationToggleValue ? tick : cross);

        ImageView workerNotificationToggle = (ImageView) findViewById(R.id.workerNotificationToggleButton);
        boolean workerNotificationToggleValue = Setting.getSafeBoolean(Constants.SETTING_WORKER_NOTIFICATIONS);
        workerNotificationToggle.setImageDrawable(workerNotificationToggleValue ? tick : cross);

        ImageView bonusNotificationToggle = (ImageView) findViewById(R.id.bonusNotificationToggleButton);
        boolean bonusNotificationToggleValue = Setting.getSafeBoolean(Constants.SETTING_BONUS_NOTIFICATIONS);
        bonusNotificationToggle.setImageDrawable(bonusNotificationToggleValue ? tick : cross);

        ImageView notificationSoundToggle = (ImageView) findViewById(R.id.notificationSoundToggleButton);
        boolean notificationSoundToggleValue = Setting.getSafeBoolean(Constants.SETTING_NOTIFICATION_SOUNDS);
        notificationSoundToggle.setImageDrawable(notificationSoundToggleValue ? tick : cross);

        ImageView adToggle = (ImageView) findViewById(R.id.turnOffAdsButton);
        boolean adToggleValue = Setting.getSafeBoolean(Constants.SETTING_DISABLE_ADS);
        adToggle.setImageDrawable(adToggleValue ? tick : cross);

        ImageView clickChangeToggle = (ImageView) findViewById(R.id.clickChangeToggleButton);
        boolean clickChangeToggleValue = Setting.getSafeBoolean(Constants.SETTING_CLICK_CHANGE);
        clickChangeToggle.setImageDrawable(clickChangeToggleValue ? tick : cross);

        ImageView messageLogToggle = (ImageView) findViewById(R.id.messageLogToggleButton);
        boolean messageLogToggleValue = Setting.getSafeBoolean(Constants.SETTING_MESSAGE_LOG);
        messageLogToggle.setImageDrawable(messageLogToggleValue ? tick : cross);

        ImageView fullscreenToggle = (ImageView) findViewById(R.id.fullscreenToggleButton);
        boolean fullscreenToggleValue = Setting.getSafeBoolean(Constants.SETTING_FULLSCREEN);
        fullscreenToggle.setImageDrawable(fullscreenToggleValue ? tick : cross);

        LinearLayout prestigeButton = (LinearLayout) findViewById(R.id.prestigeButton);
        if (Player_Info.getPlayerLevel() >= Constants.PRESTIGE_LEVEL_REQUIRED) {
            prestigeButton.setVisibility(View.VISIBLE);
        }
    }

    public void toggleSetting(View v) {
        Long settingID = null;
        String settingName = "";
        switch (v.getId()) {
            case R.id.soundToggle:
                settingID = Constants.SETTING_SOUNDS;
                settingName = "Game Sound";
                break;
            case R.id.musicToggle:
                settingID = Constants.SETTING_MUSIC;
                settingName = "Game Music";
                break;
            case R.id.restockNotificationToggle:
                settingID = Constants.SETTING_RESTOCK_NOTIFICATIONS;
                settingName = "Restock Notifications";
                break;
            case R.id.workerNotificationToggle:
                settingID = Constants.SETTING_WORKER_NOTIFICATIONS;
                settingName = "Worker Notifications";
                break;
            case R.id.visitorNotificationToggle:
                settingID = Constants.SETTING_VISITOR_NOTIFICATIONS;
                settingName = "Visitor Notifications";
                break;
            case R.id.bonusNotificationToggle:
                settingID = Constants.SETTING_BONUS_NOTIFICATIONS;
                settingName = "Bonus Notifications";
                break;
            case R.id.notificationSoundToggle:
                settingID = Constants.SETTING_NOTIFICATION_SOUNDS;
                settingName = "Notification Sounds";
                break;
            case R.id.turnOffAdsToggle:
                settingID = Constants.SETTING_DISABLE_ADS;
                settingName = "Disable Ads";
                break;
            case R.id.clickChangeToggle:
                settingID = Constants.SETTING_CLICK_CHANGE;
                settingName = "Quick Item Select";
                break;
            case R.id.messageLogToggle:
                settingID = Constants.SETTING_MESSAGE_LOG;
                settingName = "Quick Log Access";
                break;
            case R.id.fullscreenToggle:
                settingID = Constants.SETTING_FULLSCREEN;
                settingName = "Fullscreen Mode";
                break;
        }

        if (settingID != null) {
            Setting settingToToggle = Setting.findById(Setting.class, settingID);
            settingToToggle.setBoolValue(!settingToToggle.getBoolValue());
            settingToToggle.save();

            ToastHelper.showPositiveToast(v, ToastHelper.SHORT, String.format(getString(R.string.settingChanged),
                    settingName,
                    settingToToggle.getBoolValue() ? "on" : "off"), true);
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
        final String appPackageName = getPackageName(); 
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
