package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.VisitorHelper;

public class Player_Info extends SugarRecord {
    String name;
    String textValue;
    int intValue;
    Long longValue;
    int lastSentValue;

    public Player_Info() {

    }

    public Player_Info(String name, Long longValue) {
        this.name = name;
        this.longValue = longValue;
        this.lastSentValue = -1;
        this.save();
    }

    public Player_Info(String name, String textValue) {
        this.name = name;
        this.textValue = textValue;
        this.lastSentValue = -1;
        this.save();
    }

    public Player_Info(String name, int intValue) {
        this.name = name;
        this.intValue = intValue;
        this.lastSentValue = -1;
        this.save();
    }

    public Player_Info(String name, int intValue, int lastSentValue) {
        this.name = name;
        this.intValue = intValue;
        this.lastSentValue = lastSentValue;
        this.save();
    }

    public static int getPlayerLevel() {
        return convertXpToLevel(getXp());
    }

    public static int getPlayerLevelFromDB() {
        Player_Info playerLevel = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("SavedLevel")).first();

        if (playerLevel != null) {
            return playerLevel.getIntValue();
        } else {
            return 0;
        }
    }

    public static int convertXpToLevel(int xp) {
        // Level = 0.25 * sqrt(xp)
        return (int) (Constants.LEVEL_MODIFIER * Math.sqrt(xp));
    }

    public static int convertLevelToXp(int level) {
        // XP = (Level / 0.25) ^ 2
        return (int) Math.pow(level / Constants.LEVEL_MODIFIER, 2);
    }

    public static int getLevelProgress() {
        int currentXP = Player_Info.getXp();
        int currentLevelXP = Player_Info.convertLevelToXp(Player_Info.getPlayerLevel());
        int nextLevelXP = Player_Info.convertLevelToXp(Player_Info.getPlayerLevel() + 1);

        double neededXP = nextLevelXP - currentLevelXP;
        double earnedXP = nextLevelXP - currentXP;

        return 100 - (int)Math.ceil((earnedXP / neededXP) * 100);
    }

    public static int getVisitorsCompleted() {
        Player_Info xpInfo = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("VisitorsCompleted")).first();

        return xpInfo.getIntValue();
    }

    public static boolean isPremium() {
        Player_Info xpInfo = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("Premium")).first();

        return xpInfo.getIntValue() == 1;
    }

    public static int getXp() {
        Player_Info xpInfo = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("XP")).first();

        if (xpInfo != null) {
            return xpInfo.getIntValue();
        } else {
            return 0;
        }
    }

    public static void addXp(int xp) {
        Player_Info xpInfo = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("XP")).first();

        double xpMultiplier = VisitorHelper.percentToMultiplier(Upgrade.getValue("XP Bonus"));
        double modifiedXp = xpMultiplier * xp;

        xpInfo.setIntValue(xpInfo.getIntValue() + (int) modifiedXp);
        xpInfo.save();
    }

    public static void increaseByOne(Statistic statistic) {
        Player_Info statToIncrease = Select.from(Player_Info.class).where(
                Condition.prop("name").eq(statistic)).first();

        if (statToIncrease != null) {
            statToIncrease.setIntValue(statToIncrease.getIntValue() + 1);
            statToIncrease.save();
        }
    }

    public static void increaseByX(Statistic statistic, int value) {
        Player_Info statToIncrease = Select.from(Player_Info.class).where(
                Condition.prop("name").eq(statistic)).first();

        if (statToIncrease != null) {
            statToIncrease.setIntValue(statToIncrease.getIntValue() + value);
            statToIncrease.save();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public int getLastSentValue() {
        return lastSentValue;
    }

    public void setLastSentValue(int lastSentValue) {
        this.lastSentValue = lastSentValue;
    }

    public enum Statistic {
        ItemsSmelted, ItemsCrafted, ItemsTraded, ItemsEnchanted, ItemsBought, ItemsSold, VisitorsCompleted, CoinsEarned, SavedLevel, UpgradesBought
    }
}
