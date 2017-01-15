package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;

import com.google.android.gms.games.Games;
import com.orm.query.Condition;
import com.orm.query.Select;

import uk.co.jakelee.blacksmith.BuildConfig;
import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.AlertDialogHelper;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.GooglePlayHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.helper.VisitorHelper;
import uk.co.jakelee.blacksmith.model.Hero_Adventure;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Location;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Slot;
import uk.co.jakelee.blacksmith.model.Trader;
import uk.co.jakelee.blacksmith.model.Trader_Stock;
import uk.co.jakelee.blacksmith.model.Upgrade;
import uk.co.jakelee.blacksmith.model.Visitor_Stats;
import uk.co.jakelee.blacksmith.model.Visitor_Type;
import uk.co.jakelee.blacksmith.model.Worker;

public class StatisticsActivity extends Activity {
    private double completionPercent = 0.00;
    private static DisplayHelper dh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        dh = DisplayHelper.getInstance(getApplicationContext());
        dh.updateFullscreen(this);

        displayStatistics();

        GooglePlayHelper.UpdateLeaderboards(Constants.LEADERBOARD_COMPLETION, (int) (completionPercent * 100));
    }

    private void displayStatistics() {
        completionPercent = (Player_Info.getPrestige() * 100) + Player_Info.getCompletionPercent();
        ((TextViewPixel) findViewById(R.id.totalCompletion)).setText(String.format("%.2f%%", completionPercent));
        ((TextViewPixel) findViewById(R.id.totalCompletion)).setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        int currentXP = Player_Info.getXp();
        ((TextViewPixel) findViewById(R.id.currentXP)).setText(String.format("%,d", currentXP));

        int nextLevelXP = Player_Info.convertLevelToXp(Player_Info.getPlayerLevel() + 1);
        int nextLevelIn = nextLevelXP - Player_Info.getXp();
        ((TextViewPixel) findViewById(R.id.nextLevelIn)).setText(String.format("%,d", nextLevelIn));

        int highestLevel = Select.from(Player_Info.class).where(Condition.prop("name").eq("HighestLevel")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.highestLevel)).setText(String.format("%,d", highestLevel));

        int itemsSmelted = Select.from(Player_Info.class).where(Condition.prop("name").eq("ItemsSmelted")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.itemsSmelted)).setText(String.format("%,d", itemsSmelted));

        int itemsCrafted = Select.from(Player_Info.class).where(Condition.prop("name").eq("ItemsCrafted")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.itemsCrafted)).setText(String.format("%,d", itemsCrafted));

        int itemsTraded = Select.from(Player_Info.class).where(Condition.prop("name").eq("ItemsTraded")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.itemsTraded)).setText(String.format("%,d", itemsTraded));

        int itemsBought = Select.from(Player_Info.class).where(Condition.prop("name").eq("ItemsBought")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.itemsBought)).setText(String.format("%,d", itemsBought));

        int itemsSold = Select.from(Player_Info.class).where(Condition.prop("name").eq("ItemsSold")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.itemsSold)).setText(String.format("%,d", itemsSold));

        int itemsEnchanted = Select.from(Player_Info.class).where(Condition.prop("name").eq("ItemsEnchanted")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.itemsEnchanted)).setText(String.format("%,d", itemsEnchanted));

        int visitorsCompleted = Select.from(Player_Info.class).where(Condition.prop("name").eq("VisitorsCompleted")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.visitorsCompleted)).setText(String.format("%,d", visitorsCompleted));

        int coinsEarned = Select.from(Player_Info.class).where(Condition.prop("name").eq("CoinsEarned")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.coinsEarned)).setText(String.format("%,d", coinsEarned));

        ((TextViewPixel) findViewById(R.id.nextRestock)).setText(Trader_Stock.getRestockTimeLeft());

        long millisecondsUntilVisitor = VisitorHelper.getTimeUntilSpawn();
        if (millisecondsUntilVisitor > 0) {
            ((TextViewPixel) findViewById(R.id.nextVisitorCheck)).setText(DateHelper.getMinsSecsRemaining(millisecondsUntilVisitor));
        } else {
            ((TextViewPixel) findViewById(R.id.nextVisitorCheck)).setText(getString(R.string.visitorsFullRestockTime));
        }

        long unixRestarted = Select.from(Player_Info.class).where(Condition.prop("name").eq("DateStarted")).first().getLongValue();
        String dateRestarted = DateHelper.displayTime(unixRestarted, DateHelper.date);
        ((TextViewPixel) findViewById(R.id.dateStarted)).setText(dateRestarted);

        int biggestTrade = Select.from(Visitor_Stats.class).orderBy("best_item_value DESC").first().getBestItemValue();
        ((TextViewPixel) findViewById(R.id.biggestTrade)).setText(String.format("%,d", biggestTrade));

        int upgradesBought = Select.from(Player_Info.class).where(Condition.prop("name").eq("UpgradesBought")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.upgradesBought)).setText(String.format("%,d", upgradesBought));

        int tradersUnlocked = (int) Select.from(Trader.class).where(Condition.prop("level").lt(Player_Info.getPlayerLevel() + 1)).count();
        int totalTraders = (int) Trader.count(Trader.class);
        ((TextViewPixel) findViewById(R.id.traders)).setText(String.format(getString(R.string.genericProgress), tradersUnlocked, totalTraders));

        int traderStocksUnlocked = Trader_Stock.getUnlockedCount();
        int totalStocks = (int) Trader_Stock.count(Trader_Stock.class);
        ((TextViewPixel) findViewById(R.id.traderStocks)).setText(String.format(getString(R.string.genericProgress), traderStocksUnlocked, totalStocks));

        int slotsUnlocked = Slot.getUnlockedCount();
        int totalSlots = (int) Slot.count(Slot.class) - (int) Location.count(Location.class); // Exclude 1 slot per location, for overflow.
        ((TextViewPixel) findViewById(R.id.slotsUnlocked)).setText(String.format(getString(R.string.genericProgress), slotsUnlocked, totalSlots));

        int itemsSeen = Inventory.findWithQuery(Inventory.class, "SELECT * FROM inventory GROUP BY item").size();
        int totalItems = (int) Item.count(Item.class);
        ((TextViewPixel) findViewById(R.id.itemsSeen)).setText(String.format(getString(R.string.genericProgress), itemsSeen, totalItems));

        int preferencesUnlocked = Visitor_Type.getTotalPreferencesDiscovered();
        int totalPreferences = (int) Visitor_Type.count(Visitor_Type.class) * 3;
        ((TextViewPixel) findViewById(R.id.preferencesDiscovered)).setText(String.format(getString(R.string.genericProgress), preferencesUnlocked, totalPreferences));

        int trophiesEarned = (int) Select.from(Visitor_Stats.class).where(Condition.prop("trophy_achieved").gt(0)).count();
        int totalTrophies = (int) Visitor_Stats.count(Visitor_Stats.class);
        ((TextViewPixel) findViewById(R.id.trophies)).setText(String.format(getString(R.string.genericProgress), trophiesEarned, totalTrophies));

        int workersOwned = (int) Select.from(Worker.class).where(Condition.prop("purchased").eq(1)).count();
        ((TextViewPixel) findViewById(R.id.workersOwned)).setText(String.format(getString(R.string.genericProgress),
                workersOwned,
                Worker.listAll(Worker.class).size()));

        int workersTrips = Worker.getTotalTrips();
        ((TextViewPixel) findViewById(R.id.workersTrips)).setText(String.format("%,d", workersTrips));

        int adventuresCompleted = Hero_Adventure.getTotalCompleted();
        int adventuresCount = (int) Hero_Adventure.count(Hero_Adventure.class);
        int adventuresPercent = (int) (((double)adventuresCompleted / (double)adventuresCount) * 100);
        ((TextViewPixel) findViewById(R.id.heroAdventuresCompleted)).setText(String.format(getString(R.string.genericProgressPercent),
                adventuresCompleted,
                adventuresCount,
                adventuresPercent));

        Pair<Integer, Integer> adventuresInfo = Visitor_Type.getAdventureAttempts();
        int adventuresSuccessPercent = (int) (((double) (int) adventuresInfo.second / (double) (int) adventuresInfo.first) * 100);
        ((TextViewPixel) findViewById(R.id.heroAdventuresSuccessful)).setText(String.format(getString(R.string.genericProgressPercent),
                adventuresInfo.first,
                adventuresInfo.second,
                adventuresSuccessPercent));

        int collectionsCrafted = Player_Info.getCollectionsCrafted();
        ((TextViewPixel) findViewById(R.id.collectionsCrafted)).setText(String.format("%,d", collectionsCrafted));

        int questsCompleted = Select.from(Player_Info.class).where(Condition.prop("name").eq("QuestsCompleted")).first().getIntValue();
        ((TextViewPixel) findViewById(R.id.questsCompleted)).setText(String.format("%,d", questsCompleted));

        int prestigeLevel = Player_Info.getPrestige();
        int prestigeGoldPercent = prestigeLevel * 50;
        int bonusGoldPercent = prestigeGoldPercent + Select.from(Upgrade.class).where(Condition.prop("name").eq("Coins Bonus")).first().getCurrent();

        double bonusXPMultiplier = VisitorHelper.percentToMultiplier(Upgrade.getValue("XP Bonus")) * (Math.pow(0.75, prestigeLevel));
        if (bonusXPMultiplier >= 1) {
            int xpPercent = (int) Math.ceil(100 * (bonusXPMultiplier - 1));
            ((TextViewPixel) findViewById(R.id.totalBonusXP)).setText(String.format(getString(R.string.genericPercent), "+", xpPercent));
        } else {
            int xpPercent = (int) Math.ceil(100 - (100 * bonusXPMultiplier));
            ((TextViewPixel) findViewById(R.id.totalBonusXP)).setText(String.format(getString(R.string.genericPercent), "-", xpPercent));
        }

        ((TextViewPixel) findViewById(R.id.totalBonusGold)).setText(String.format("+%,d%%", bonusGoldPercent));
        ((TextViewPixel) findViewById(R.id.prestigeLevel)).setText(String.format(getString(R.string.statisticsPrestigeValue),
                prestigeLevel + 1,
                Player_Info.getPrestige() * 50,
                (int) (100 * (1 - Math.pow(0.75, Player_Info.getPrestige())))));

        long lastPrestiged = Select.from(Player_Info.class).where(Condition.prop("name").eq("DateLastPrestiged")).first().getLongValue();
        if (lastPrestiged > 0) {
            String datePrestiged = DateHelper.displayTime(lastPrestiged, DateHelper.date);
            ((TextViewPixel) findViewById(R.id.lastPrestiged)).setText(datePrestiged);
        } else if (lastPrestiged == 0 && Player_Info.getPlayerLevel() >= Constants.PRESTIGE_LEVEL_REQUIRED) {
            ((TextViewPixel) findViewById(R.id.lastPrestiged)).setText(R.string.statisticsLastPrestigedNever);
        } else {
            ((TextViewPixel) findViewById(R.id.lastPrestiged)).setText(String.format(getString(R.string.statisticsLastPrestigedLowLevel), Constants.PRESTIGE_LEVEL_REQUIRED));
        }

        ((TextViewPixel) findViewById(R.id.coinsPurchased)).setText("" + Player_Info.getCoinsPurchased());

        String version = String.format(getString(R.string.versionNumber), BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
        ((TextViewPixel) findViewById(R.id.version)).setText(version);
    }

    public void openLeaderboards(View view) {
        if (GooglePlayHelper.mGoogleApiClient.isConnected()) {
            if (view.getTag() == null) {
                startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(GooglePlayHelper.mGoogleApiClient), GooglePlayHelper.RC_LEADERBOARDS);
            } else if (view.getTag().equals("bestitem")) {
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(GooglePlayHelper.mGoogleApiClient, Constants.LEADERBOARD_ITEM_VALUE), GooglePlayHelper.RC_LEADERBOARDS);
            } else if (view.getTag().equals("visitors")) {
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(GooglePlayHelper.mGoogleApiClient, Constants.LEADERBOARD_VISITORS), GooglePlayHelper.RC_LEADERBOARDS);
            } else if (view.getTag().equals("trophies")) {
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(GooglePlayHelper.mGoogleApiClient, Constants.LEADERBOARD_TROPHIES), GooglePlayHelper.RC_LEADERBOARDS);
            } else if (view.getTag().equals("prestiged")) {
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(GooglePlayHelper.mGoogleApiClient, Constants.LEADERBOARD_TIMES_PRESTIGED), GooglePlayHelper.RC_LEADERBOARDS);
            } else if (view.getTag().equals("completionpercent")) {
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(GooglePlayHelper.mGoogleApiClient, Constants.LEADERBOARD_COMPLETION), GooglePlayHelper.RC_LEADERBOARDS);
            } else if (view.getTag().equals("collectionscrafted")) {
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(GooglePlayHelper.mGoogleApiClient, Constants.LEADERBOARD_COLLECTIONS), GooglePlayHelper.RC_LEADERBOARDS);
            } else if (view.getTag().equals("highestlevel")) {
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(GooglePlayHelper.mGoogleApiClient, Constants.LEADERBOARD_HIGHEST_LEV), GooglePlayHelper.RC_LEADERBOARDS);
            }
        } else {
            ToastHelper.showErrorToast(view, ToastHelper.SHORT, getString(R.string.leaderboardsNoConnection), false);
        }
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Statistics);
        startActivity(intent);
    }

    public void openCompletionPercent(View view) {
        AlertDialogHelper.displayCompletion(this, this);
    }

    public void closePopup(View view) {
        finish();
    }
}
