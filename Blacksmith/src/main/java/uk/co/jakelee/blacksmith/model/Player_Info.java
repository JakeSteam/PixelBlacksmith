package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.VisitorHelper;

public class Player_Info extends SugarRecord {
    private String name;
    private String textValue;
    private int intValue;
    private Long longValue;
    private int lastSentValue;

    public Player_Info() {

    }

    public Player_Info(String name, Long longValue) {
        this.name = name;
        this.longValue = longValue;
        this.lastSentValue = -1;
    }

    public Player_Info(String name, String textValue) {
        this.name = name;
        this.textValue = textValue;
        this.lastSentValue = -1;
    }

    public Player_Info(String name, int intValue) {
        this.name = name;
        this.intValue = intValue;
        this.lastSentValue = -1;
    }

    public Player_Info(String name, int intValue, int lastSentValue) {
        this.name = name;
        this.intValue = intValue;
        this.lastSentValue = lastSentValue;
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
        // Level = 0.1 * sqrt(xp)
        return (int) (Constants.LEVEL_MODIFIER * Math.sqrt(xp));
    }

    public static int convertLevelToXp(int level) {
        // XP = (Level / 0.1) ^ 2
        return (int) Math.pow(level / Constants.LEVEL_MODIFIER, 2);
    }

    public static int getLevelProgress() {
        int currentXP = Player_Info.getXp();
        int currentLevelXP = Player_Info.convertLevelToXp(Player_Info.getPlayerLevel());
        int nextLevelXP = Player_Info.convertLevelToXp(Player_Info.getPlayerLevel() + 1);

        double neededXP = nextLevelXP - currentLevelXP;
        double earnedXP = nextLevelXP - currentXP;

        return 100 - (int) Math.ceil((earnedXP / neededXP) * 100);
    }

    public static int getVisitorsCompleted() {
        Player_Info xpInfo = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("VisitorsCompleted")).first();

        return xpInfo.getIntValue();
    }

    public static boolean isPremium() {
        Player_Info premium = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("Premium")).first();

        return premium.getIntValue() == 1;
    }

    public static boolean displayAds() {
        Player_Info premium = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("Premium")).first();
        Setting hideAllAds = Setting.findById(Setting.class, Constants.SETTING_DISABLE_ADS);

        // Return true unless the player is premium + has hidden ads.
        return !(premium.getIntValue() == 1 && hideAllAds.getBoolValue());
    }

    public static boolean isBonusReady() {
        return timeUntilBonusReady() <= 0;
    }

    public static long timeUntilBonusReady() {
        long lastClaimedTime = System.currentTimeMillis();

        Player_Info lastBonusClaim = Select.from(Player_Info.class).where(Condition.prop("name").eq("LastBonusClaimed")).first();
        if (lastBonusClaim != null) {
            lastClaimedTime = lastBonusClaim.getLongValue();
        }

        long bonusRechargeTime = Player_Info.isPremium() ? Constants.BONUS_TIME_PREMIUM : Constants.BONUS_TIME_NON_PREMIUM;
        long timeBonusReady = lastClaimedTime + bonusRechargeTime;

        return timeBonusReady - System.currentTimeMillis();
    }

    public static int getPrestige() {
        Player_Info prestige = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("Prestige")).first();

        return prestige.getIntValue();
    }

    public static int getHighestLevel() {
        Player_Info level = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("HighestLevel")).first();

        return level.getIntValue();
    }

    public static int getCollectionsCrafted() {
        Player_Info collections = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("CollectionsCreated")).first();

        return collections.getIntValue();
    }

    public static double getCompletionPercent() {
        /*
            Level * 100
            Upgrades * 10
            Traders * 10
            Slots * 10
            Trader Stocks * 1
            Item * 1
            Visitor Preferences * 1
            Trophies * 1
            Workers * 100
         */
        int currentlyComplete = getCurrentCompletion();
        int totalToComplete = getFullCompletion();

        double completionPercentage = (((double) currentlyComplete / (double) totalToComplete) * 100);
        return completionPercentage > 100 ? 100 : completionPercentage;
    }

    private static int getCurrentCompletion() {
        int currentLevelPoints = 100 * (Player_Info.getPlayerLevel() > 70 ? 70 : Player_Info.getPlayerLevel());
        int currentUpgradePoints = (10 * Select.from(Player_Info.class).where(Condition.prop("name").eq("UpgradesBought")).first().getIntValue());
        int currentTraderPoints = (10 * (int) Select.from(Trader.class).where(Condition.prop("level").lt(Player_Info.getPlayerLevel() + 1)).count());
        int currentSlotPoints = (10 * Slot.getUnlockedCount());
        int currentTraderStockPoints = Trader_Stock.getUnlockedCount();
        int currentItemPoints = Inventory.findWithQuery(Inventory.class, "SELECT * FROM inventory GROUP BY item").size();
        int currentPreferences = Visitor_Type.getPreferencesDiscovered();
        int currentTrophies = (int) Select.from(Visitor_Stats.class).where(Condition.prop("trophy_achieved").gt(0)).count();
        int currentWorkers = (int) Select.from(Worker.class).where(Condition.prop("purchased").eq(1)).count();

        return currentLevelPoints + currentUpgradePoints + currentTraderPoints + currentSlotPoints + currentTraderStockPoints + currentItemPoints + currentPreferences + currentTrophies + currentWorkers;
    }

    private static int getFullCompletion() {
        int maxLevelPoints = (100 * Constants.PRESTIGE_LEVEL_REQUIRED);
        int maxUpgradePoints = (10 * Upgrade.getMaximumUpgrades());
        int maxTraderPoints = (10 * (int) Trader.count(Trader.class));
        int maxSlotPoints = (10 * ((int) Slot.count(Slot.class) - (int)Location.count(Location.class))); // 1 overflow slot per location
        int maxTraderStockPoints = (int) Trader_Stock.count(Trader_Stock.class);
        int maxItemPoints = (int) Item.count(Item.class);
        int maxPreferences = (int) Visitor_Type.count(Visitor_Type.class) * 3;
        int maxTrophies = (int) Visitor_Stats.count(Visitor_Stats.class);
        int maxWorkers = Worker.listAll(Worker.class).size();

        return maxLevelPoints + maxUpgradePoints + maxTraderPoints + maxSlotPoints + maxTraderStockPoints + maxItemPoints + maxPreferences + maxTrophies + maxWorkers;
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
        double xpMultiplierPrestige = xpMultiplier + Player_Info.getPrestige();
        double modifiedXp = xpMultiplierPrestige * xp;

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
        CollectionsCreated, HighestLevel, ItemsSmelted, ItemsCrafted, ItemsTraded, ItemsEnchanted, ItemsBought, ItemsSold, VisitorsCompleted, CoinsEarned, SavedLevel, UpgradesBought, Prestige, QuestsCompleted
    }
}
