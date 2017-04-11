package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.quest.Quests;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.GooglePlayHelper;
import uk.co.jakelee.blacksmith.helper.LanguageHelper;
import uk.co.jakelee.blacksmith.helper.StorageHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.helper.TutorialHelper;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Setting;

public class SettingsActivity extends Activity {
    private static DisplayHelper dh;
    final Handler handler = new Handler();
    private int spinnersInitialised = 0;
    private int totalSpinners = 1;

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
        spinnersInitialised = 0;
        createLanguagePicker((Spinner)findViewById(R.id.languagePicker));
    }

    private void createLanguagePicker(Spinner spinner) {
        Setting setting = Setting.findById(Setting.class, Constants.SETTING_LANGUAGE);
        ArrayAdapter<String> envAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner);
        envAdapter.setDropDownViewResource(R.layout.custom_spinner_item);

        for (int i = 1; i <= Constants.NUM_LANGUAGES; i++) {
            String text = LanguageHelper.getFlagById(i) + " " + LanguageHelper.getLanguageById(this, i);
            envAdapter.add(text);
        }

        spinner.setAdapter(envAdapter);
        spinner.setSelection(setting.getIntValue() - 1);
        spinner.setOnItemSelectedListener(getListener(setting));
    }

    private AdapterView.OnItemSelectedListener getListener(final Setting setting) {
        final Activity activity = this;
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spinnersInitialised < totalSpinners) {
                    spinnersInitialised++;
                } else {
                    setting.setIntValue(position + 1);
                    setting.save();

                    LanguageHelper.changeLanguage(activity, position + 1);
                    ToastHelper.showPositiveToast(parentView, ToastHelper.SHORT, parentView.getSelectedItem().toString(), true);
                    onResume();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        };
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

        ((ImageView) findViewById(R.id.soundToggleButton)).setImageDrawable(Setting.getSafeBoolean(Constants.SETTING_SOUNDS) ? tick : cross);
        ((ImageView) findViewById(R.id.musicToggleButton)).setImageDrawable(Setting.getSafeBoolean(Constants.SETTING_MUSIC) ? tick : cross);
        ((ImageView) findViewById(R.id.restockNotificationToggleButton)).setImageDrawable(Setting.getSafeBoolean(Constants.SETTING_RESTOCK_NOTIFICATIONS) ? tick : cross);
        ((ImageView) findViewById(R.id.visitorNotificationToggleButton)).setImageDrawable(Setting.getSafeBoolean(Constants.SETTING_VISITOR_NOTIFICATIONS) ? tick : cross);
        ((ImageView) findViewById(R.id.workerNotificationToggleButton)).setImageDrawable(Setting.getSafeBoolean(Constants.SETTING_WORKER_NOTIFICATIONS) ? tick : cross);
        ((ImageView) findViewById(R.id.bonusNotificationToggleButton)).setImageDrawable(Setting.getSafeBoolean(Constants.SETTING_BONUS_NOTIFICATIONS) ? tick : cross);
        ((ImageView) findViewById(R.id.finishedNotificationToggleButton)).setImageDrawable(Setting.getSafeBoolean(Constants.SETTING_FINISHED_NOTIFICATIONS) ? tick : cross);
        ((ImageView) findViewById(R.id.assistantNotificationToggleButton)).setImageDrawable(Setting.getSafeBoolean(Constants.SETTING_ASSISTANT_NOTIFICATIONS) ? tick : cross);
        ((ImageView) findViewById(R.id.notificationSoundToggleButton)).setImageDrawable(Setting.getSafeBoolean(Constants.SETTING_NOTIFICATION_SOUNDS) ? tick : cross);
        ((ImageView) findViewById(R.id.turnOffAdsButton)).setImageDrawable(Setting.getSafeBoolean(Constants.SETTING_DISABLE_ADS) ? tick : cross);
        ((ImageView) findViewById(R.id.clickChangeToggleButton)).setImageDrawable(Setting.getSafeBoolean(Constants.SETTING_CLICK_CHANGE) ? tick : cross);
        ((ImageView) findViewById(R.id.messageLogToggleButton)).setImageDrawable(Setting.getSafeBoolean(Constants.SETTING_MESSAGE_LOG) ? tick : cross);
        ((ImageView) findViewById(R.id.autoRefreshToggleButton)).setImageDrawable(Setting.getSafeBoolean(Constants.SETTING_AUTOREFRESH) ? tick : cross);
        ((ImageView) findViewById(R.id.fullscreenCheckToggleButton)).setImageDrawable(Setting.getSafeBoolean(Constants.SETTING_CHECK_FULLSCREEN) ? tick : cross);
        ((ImageView) findViewById(R.id.updateSlotsToggleButton)).setImageDrawable(Setting.getSafeBoolean(Constants.SETTING_UPDATE_SLOTS) ? tick : cross);
        ((ImageView) findViewById(R.id.fullscreenToggleButton)).setImageDrawable(Setting.getSafeBoolean(Constants.SETTING_FULLSCREEN) ? tick : cross);
        ((ImageView) findViewById(R.id.longToastToggleButton)).setImageDrawable(Setting.getSafeBoolean(Constants.SETTING_LONG_TOAST) ? tick : cross);
        ((ImageView) findViewById(R.id.handleMaxToggleButton)).setImageDrawable(Setting.getSafeBoolean(Constants.SETTING_HANDLE_MAX) ? tick : cross);
        ((ImageView) findViewById(R.id.bulkStackToggleButton)).setImageDrawable(Setting.getSafeBoolean(Constants.SETTING_BULK_STACK) ? tick : cross);

        populateOrientation();

        if (Player_Info.getPlayerLevel() >= Constants.PRESTIGE_LEVEL_REQUIRED) {
            findViewById(R.id.prestigeButton).setVisibility(View.VISIBLE);
        }

        ((TextView) findViewById(R.id.settingsCodeHeader)).setText(getSettingsCode());
    }

    private void populateOrientation() {
        Drawable tick = dh.createDrawable(R.drawable.tick, 50, 50);
        Drawable cross = dh.createDrawable(R.drawable.cross, 50, 50);
        try {
            int orientation = Setting.findById(Setting.class, Constants.SETTING_ORIENTATION).getIntValue();
            ((ImageView) findViewById(R.id.orientationAutoCheckbox)).setImageDrawable(orientation == Constants.ORIENTATION_AUTO ? tick : cross);
            ((ImageView) findViewById(R.id.orientationPortraitCheckbox)).setImageDrawable(orientation == Constants.ORIENTATION_PORTRAIT ? tick : cross);
            ((ImageView) findViewById(R.id.orientationLandscapeCheckbox)).setImageDrawable(orientation == Constants.ORIENTATION_LANDSCAPE ? tick : cross);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public String getSettingsCode() {
        List<Setting> settings = Setting.listAll(Setting.class);
        String settingsCode = "1";
        for (Setting setting : settings) {
            settingsCode += (setting.getBoolValue() ? "1" : "0");
        }

        return String.format(getString(R.string.settingsCode),
                Integer.valueOf(settingsCode, 2));
    }

    public void toggleOrientation(View v) {
        Setting orientationSetting = Setting.findById(Setting.class, Constants.SETTING_ORIENTATION);
        orientationSetting.setIntValue(Integer.valueOf((String)v.getTag()));
        orientationSetting.save();
        populateOrientation();
    }

    public void toggleSetting(View v) {
        Long settingID = null;
        switch (v.getId()) {
            case R.id.soundToggle:
                settingID = Constants.SETTING_SOUNDS;
                break;
            case R.id.musicToggle:
                settingID = Constants.SETTING_MUSIC;
                break;
            case R.id.restockNotificationToggle:
                settingID = Constants.SETTING_RESTOCK_NOTIFICATIONS;
                break;
            case R.id.workerNotificationToggle:
                settingID = Constants.SETTING_WORKER_NOTIFICATIONS;
                break;
            case R.id.visitorNotificationToggle:
                settingID = Constants.SETTING_VISITOR_NOTIFICATIONS;
                break;
            case R.id.bonusNotificationToggle:
                settingID = Constants.SETTING_BONUS_NOTIFICATIONS;
                break;
            case R.id.notificationSoundToggle:
                settingID = Constants.SETTING_NOTIFICATION_SOUNDS;
                break;
            case R.id.finishedNotificationToggle:
                settingID = Constants.SETTING_FINISHED_NOTIFICATIONS;
                break;
            case R.id.assistantNotificationToggle:
                settingID = Constants.SETTING_ASSISTANT_NOTIFICATIONS;
                break;
            case R.id.turnOffAdsToggle:
                settingID = Constants.SETTING_DISABLE_ADS;
                break;
            case R.id.clickChangeToggle:
                settingID = Constants.SETTING_CLICK_CHANGE;
                break;
            case R.id.messageLogToggle:
                settingID = Constants.SETTING_MESSAGE_LOG;
                break;
            case R.id.fullscreenToggle:
                settingID = Constants.SETTING_FULLSCREEN;
                break;
            case R.id.autoRefreshToggle:
                settingID = Constants.SETTING_AUTOREFRESH;
                break;
            case R.id.fullscreenCheckToggle:
                settingID = Constants.SETTING_CHECK_FULLSCREEN;
                break;
            case R.id.updateSlotsToggle:
                settingID = Constants.SETTING_UPDATE_SLOTS;
                break;
            case R.id.longToastToggle:
                settingID = Constants.SETTING_LONG_TOAST;
                break;
            case R.id.handleMaxToggle:
                settingID = Constants.SETTING_HANDLE_MAX;
                break;
            case R.id.bulkStackToggle:
                settingID = Constants.SETTING_BULK_STACK;
                break;
        }

        if (settingID != null) {
            Setting settingToToggle = Setting.findById(Setting.class, settingID);
            settingToToggle.setBoolValue(!settingToToggle.getBoolValue());
            settingToToggle.save();

            ToastHelper.showPositiveToast(v, ToastHelper.SHORT, String.format(getString(R.string.settingChanged),
                    settingToToggle.getName(this),
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
        TutorialHelper.currentStage  = Constants.STAGE_1_MAIN;
        this.finish();
    }

    public void openCredits(View view) {
        Intent intent = new Intent(getApplicationContext(), CreditsActivity.class);
        startActivity(intent);
    }

    public void launchHotfixes(View view) {
        AlertDialogHelper.hotfixMenu(this);
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

    public void importSave(View view) {
        String readResult = StorageHelper.loadLocalSave(this, true);
        if (readResult.startsWith("PixelBlacksmith")) {
            ToastHelper.showPositiveToast(view, ToastHelper.LONG, getString(R.string.saveImportSuccess), true);
        } else {
            ToastHelper.showErrorToast(view, ToastHelper.LONG, String.format(getString(R.string.saveImportFailure),
                    readResult), true);
        }
    }

    public void exportSave(View view) {
        String writeResult = StorageHelper.saveLocalSave(this);
        if (writeResult.startsWith("PixelBlacksmith")) {
            ToastHelper.showPositiveToast(view, ToastHelper.LONG, String.format(getString(R.string.saveExportSuccess),
                    writeResult), true);
        } else {
            ToastHelper.showErrorToast(view, ToastHelper.LONG, String.format(getString(R.string.saveExportFailure),
                    writeResult), true);
        }
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

    public void openTranslationSheet(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/spreadsheets/d/1SIvUBTR2kT1UFBcVbXrpMxF5A1jBZz5rVFCgVgyRMnE/"));
        startActivity(browserIntent);
    }
}
