package uk.co.jakelee.blacksmith.helper;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Pending_Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Upgrade;
import uk.co.jakelee.blacksmith.model.Visitor;
import uk.co.jakelee.blacksmith.model.Visitor_Demand;
import uk.co.jakelee.blacksmith.model.Visitor_Stats;
import uk.co.jakelee.blacksmith.model.Visitor_Type;

public class PrestigeHelper {
    public static void prestigeAccount() {
        increasePrestige();
        removeAllItems();
        resetUpgrades();
        resetCoins();
        resetXP();
        resetAllVisitors();
    }

    private static void increasePrestige() {
        Player_Info.increaseByOne(Player_Info.Statistic.Prestige);
    }

    private static void removeAllItems() {
        Inventory.deleteAll(Inventory.class);
        Pending_Inventory.deleteAll(Pending_Inventory.class);
    }

    private static void resetUpgrades() {
        List<Upgrade> upgrades = Upgrade.listAll(Upgrade.class);
        for (Upgrade upgrade : upgrades) {
            upgrade.setCurrent(upgrade.getMinimum());
            upgrade.save();
        }
    }

    private static void resetCoins() {
        Inventory coinStock = Inventory.getInventory(Constants.ITEM_COINS, Constants.STATE_NORMAL);
        coinStock.setQuantity(Constants.DEFAULT_COINS);
        coinStock.save();
    }

    private static void resetXP() {
        Player_Info xp = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("XP")).first();
        xp.setIntValue(Constants.DEFAULT_XP);
        xp.save();
    }

    private static void resetAllVisitors() {
        Visitor.deleteAll(Visitor.class);
        Visitor_Demand.deleteAll(Visitor_Demand.class);

        List<Visitor_Type> visitorTypes = Visitor_Type.listAll(Visitor_Type.class);
        for (Visitor_Type visitorType : visitorTypes) {
            visitorType.setTierDiscovered(false);
            visitorType.setTypeDiscovered(false);
            visitorType.setStateDiscovered(false);
            visitorType.save();
        }

        List<Visitor_Stats> visitorStats = Visitor_Stats.listAll(Visitor_Stats.class);
        for (Visitor_Stats visitorStat : visitorStats) {
            visitorStat = new Visitor_Stats(visitorStat.getVisitorType(),  0, 52L, 1L, 0, 0L, 0L);
            visitorStat.save();
        }
    }
}
