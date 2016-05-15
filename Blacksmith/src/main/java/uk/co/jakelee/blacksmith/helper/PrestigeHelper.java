package uk.co.jakelee.blacksmith.helper;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.main.MainActivity;
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

    private static void increasePrestige() {
        Player_Info.increaseByOne(Player_Info.Statistic.Prestige);

        Player_Info prestigeDate = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("DateLastPrestiged")).first();
        prestigeDate.setLongValue(System.currentTimeMillis());
        prestigeDate.save();
    }

    private static void resetItems() {
        Inventory.deleteAll(Inventory.class);
        Pending_Inventory.deleteAll(Pending_Inventory.class);

        DatabaseHelper.createInventory();
    }

    private static void resetUpgrades() {
        List<Upgrade> upgrades = Upgrade.listAll(Upgrade.class);
        for (Upgrade upgrade : upgrades) {
            if (upgrade.getName().equals("Gold Bonus") || upgrade.getName().equals("XP Bonus")) {
                if (Player_Info.isPremium()) {
                    upgrade.setCurrent(20);
                } else {
                    upgrade.setCurrent(0);
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
        Trader.executeQuery("UPDATE trader SET purchases = 0");
        Trader_Stock.restockTraders();
    }

    private static void resetWorkers() {
        Worker.executeQuery("UPDATE worker SET purchased = 0, tool_used = 32, tool_state = 1, time_started = 0");
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
