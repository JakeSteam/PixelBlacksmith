package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.quest.Quest;
import com.google.android.gms.games.quest.Quests;
import com.orm.query.Condition;
import com.orm.query.Select;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.GooglePlayHelper;
import uk.co.jakelee.blacksmith.model.Player_Info;

public class QuestActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest);
        DisplayHelper dh = DisplayHelper.getInstance(getApplicationContext());
        dh.updateFullscreen(this);

        int questsCompleted = Select.from(Player_Info.class).where(Condition.prop("name").eq("QuestsCompleted")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.questsCompleted)).setText(String.format(getString(R.string.questsCompleted), questsCompleted));
    }

    public void currentQuests(View view) {
        if (GooglePlayHelper.mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Quests.getQuestsIntent(GooglePlayHelper.mGoogleApiClient, new int[]{Quests.SELECT_ACCEPTED}), GooglePlayHelper.RC_QUESTS);
        }
    }

    public void availableQuests(View view) {
        if (GooglePlayHelper.mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Quests.getQuestsIntent(GooglePlayHelper.mGoogleApiClient, new int[]{Quests.SELECT_OPEN}), GooglePlayHelper.RC_QUESTS);
        }
    }

    public void completedQuests(View view) {
        if (GooglePlayHelper.mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Quests.getQuestsIntent(GooglePlayHelper.mGoogleApiClient, new int[]{Quests.SELECT_COMPLETED}), GooglePlayHelper.RC_QUESTS);
        }
    }

    public void failedQuests(View view) {
        if (GooglePlayHelper.mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Quests.getQuestsIntent(GooglePlayHelper.mGoogleApiClient, new int[]{Quests.SELECT_FAILED, Quest.STATE_EXPIRED}), GooglePlayHelper.RC_QUESTS);
        }
    }

    public void claimUnclaimed(View view) {
        if (GooglePlayHelper.mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Quests.getQuestsIntent(GooglePlayHelper.mGoogleApiClient, new int[]{Quests.SELECT_COMPLETED_UNCLAIMED}), GooglePlayHelper.RC_QUESTS);
        }
    }

    public void upcomingQuests(View view) {
        if (GooglePlayHelper.mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Quests.getQuestsIntent(GooglePlayHelper.mGoogleApiClient, new int[]{Quests.SELECT_UPCOMING}), GooglePlayHelper.RC_QUESTS);
        }
    }

    public void redditSchedule(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.reddit.com/r/PixelBlacksmith/wiki/quests"));
        startActivity(browserIntent);
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Quests);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
