package uk.co.jakelee.blacksmith.helper;

import com.google.android.gms.games.Games;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.main.MainActivity;
import uk.co.jakelee.blacksmith.model.Achievement;
import uk.co.jakelee.blacksmith.model.Hero;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Pending_Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Trader;
import uk.co.jakelee.blacksmith.model.Trader_Stock;
import uk.co.jakelee.blacksmith.model.Upgrade;
import uk.co.jakelee.blacksmith.model.Visitor;
import uk.co.jakelee.blacksmith.model.Visitor_Demand;
import uk.co.jakelee.blacksmith.model.Worker;

public class PrestigeHelper {
    public static void prestigeAccount() {
        if (!MainActivity.vh.prestiging) {
            MainActivity.vh.prestiging = true;
            increasePrestige();

            resetItems();
            resetUpgrades();
            resetXP();
            resetAllVisitors();
            resetTraders();
            resetWorkers();
            resetCraftingInterface();

            GooglePlayHelper.UpdateLeaderboards(Constants.LEADERBOARD_TIMES_PRESTIGED, Player_Info.getPrestige() + 1);
        }
    }

    private static void increasePrestige() {
        Player_Info.increaseByOne(Player_Info.Statistic.Prestige);
        if (GooglePlayHelper.IsConnected()) {
            Achievement achievement = Select.from(Achievement.class).where(Condition.prop("name").eq("The Fun Never Stops")).first();
            Games.Achievements.unlock(GooglePlayHelper.mGoogleApiClient, achievement.getRemoteID());
        }

        Player_Info prestigeDate = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("DateLastPrestiged")).first();
        prestigeDate.setLongValue(System.currentTimeMillis());
        prestigeDate.save();
    }

    private static void resetItems() {
        Inventory.executeQuery("DELETE FROM inventory WHERE item NOT IN (SELECT id FROM item WHERE type = " + Constants.TYPE_PAGE + " OR type = " + Constants.TYPE_BOOK + ")");
        Pending_Inventory.deleteAll(Pending_Inventory.class);

        new DatabaseHelper().createInventory();
    }

    private static void resetUpgrades() {
        List<Upgrade> upgrades = Upgrade.listAll(Upgrade.class);
        for (Upgrade upgrade : upgrades) {
            if (upgrade.getName().equals("Coins Bonus") || upgrade.getName().equals("XP Bonus")) {
                if (Player_Info.isPremium()) {
                    upgrade.setCurrent(20);
                    upgrade.setMaximum(100);
                } else {
                    upgrade.setCurrent(0);
                    upgrade.setMaximum(50);
                }
            } else {
                upgrade.setCurrent(upgrade.getMinimum());
            }
            upgrade.save();
        }
    }

    private static void resetXP() {
        Player_Info xp = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("XP")).first();
        xp.setIntValue(Constants.STARTING_XP);
        xp.save();
    }

    private static void resetAllVisitors() {
        Visitor.deleteAll(Visitor.class);
        Visitor_Demand.deleteAll(Visitor_Demand.class);
    }

    private static void resetTraders() {
        Trader.executeQuery("UPDATE trader SET purchases = 0, fixed = 0");
        Trader_Stock.restockTraders();
    }

    private static void resetWorkers() {
        Worker.executeQuery("UPDATE worker SET purchased = 0, tool_used = 32, tool_state = 1, food_used = 0, time_started = 0");
        Hero.executeQuery("UPDATE hero SET current_adventure = 0, time_started = 0, purchased = 0, visitor_id = 0, " +
                "food_item = 0, food_state = 0, helmet_item = 0, helmet_state = 0, armour_item = 0, armour_state = 0, " +
                "weapon_item = 0, weapon_state = 0, shield_item = 0, shield_state = 0, gloves_item = 0, gloves_state = 0, " +
                "boots_item = 0, boots_state = 0, ring_item = 0, ring_state = 0");
    }

    public static void resetCraftingInterface() {
        MainActivity.prefs.edit().putInt("furnacePosition", 0).apply();
        MainActivity.prefs.edit().putInt("anvilTier", Constants.TIER_MIN).apply();
        MainActivity.prefs.edit().putInt("anvilPosition", 0).apply();
        MainActivity.prefs.edit().putInt("tableTier", Constants.TIER_MIN).apply();
        MainActivity.prefs.edit().putInt("tablePosition", 0).apply();
        MainActivity.prefs.edit().putInt("enchantingTier", Constants.TIER_MIN).apply();
        MainActivity.prefs.edit().putInt("enchantingPosition", 0).apply();
    }
}
