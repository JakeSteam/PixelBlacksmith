package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.VisitorHelper;

import static uk.co.jakelee.blacksmith.R.id.coinsPurchased;

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
        Player_Info completedVisitors = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("VisitorsCompleted")).first();

        return completedVisitors.getIntValue();
    }

    public static boolean isPremium() {
        Player_Info premium = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("Premium")).first();

        return premium != null && premium.getIntValue() == 1;
    }

    public static boolean displayAds() {
        Player_Info premium = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("Premium")).first();
        boolean hideAllAds = Setting.getSafeBoolean(Constants.SETTING_DISABLE_ADS);

        // Return true unless the player is premium + has hidden ads.
        return !(premium.getIntValue() == 1 && hideAllAds);
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
        bonusRechargeTime = bonusRechargeTime / (Super_Upgrade.isEnabled(Constants.SU_HALF_BONUS_CHEST) ? 2 : 1);
        long timeBonusReady = lastClaimedTime + bonusRechargeTime;

        return timeBonusReady - System.currentTimeMillis();
    }

    public static int getPrestige() {
        Player_Info prestige = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("Prestige")).first();

        return prestige != null ? prestige.getIntValue() : 0;
    }

    public static int getHighestLevel() {
        Player_Info level = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("HighestLevel")).first();

        return level != null ? level.getIntValue() : 0;
    }

    public static int getCollectionsCrafted() {
        Player_Info collections = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("CollectionsCreated")).first();

        return collections != null ? collections.getIntValue() : 0;
    }

    public static int getCoinsPurchased() {
        Player_Info coinsPurchased = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("CoinsPurchased")).first();

        return coinsPurchased != null ? coinsPurchased.getIntValue() : 0;
    }

    public static int getActivePet() {
        Player_Info activePet = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("ActivePet")).first();

        return activePet != null ? activePet.getIntValue() : 0;
    }

    public static String getLastContributed() {
        Player_Info lastContributed = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("LastDonated")).first();

        return lastContributed != null ? lastContributed.getTextValue() : "never";
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
            Adventures * 1
         */
        int currentLevelPoints = 100 * Player_Info.getPlayerLevel();
        int currentUpgradePoints = (10 * Select.from(Player_Info.class).where(Condition.prop("name").eq("UpgradesBought")).first().getIntValue());
        int currentTraderPoints = (10 * (int) Select.from(Trader.class).where(Condition.prop("level").lt(Player_Info.getPlayerLevel() + 1)).count());
        int currentSlotPoints = (10 * Slot.getUnlockedCount());
        int currentTraderStockPoints = Trader_Stock.getUnlockedCount();
        int currentItemPoints = Inventory.findWithQuery(Inventory.class, "SELECT * FROM inventory GROUP BY item").size();
        int currentPreferencePoints = Visitor_Type.getTotalPreferencesDiscovered();
        int currentTrophyPoints = (int) Select.from(Visitor_Stats.class).where(Condition.prop("trophy_achieved").gt(0)).count();
        int currentWorkerPoints = (int) Select.from(Worker.class).where(Condition.prop("purchased").eq(1)).count();
        int currentAdventurePoints = Visitor_Type.getAdventureAttempts().second;

        int maxLevelPoints = (100 * Constants.PRESTIGE_LEVEL_REQUIRED);
        int maxUpgradePoints = (10 * Upgrade.getMaximumUpgrades());
        int maxTraderPoints = (10 * (int) Trader.count(Trader.class));
        int maxSlotPoints = (10 * ((int) Slot.count(Slot.class) - (int)Location.count(Location.class))); // 1 overflow slot per location
        int maxTraderStockPoints = (int) Trader_Stock.count(Trader_Stock.class);
        int maxItemPoints = (int) Item.count(Item.class);
        int maxPreferencePoints = (int) Visitor_Type.count(Visitor_Type.class) * 3;
        int maxTrophyPoints = (int) Visitor_Stats.count(Visitor_Stats.class);
        int maxWorkerPoints = (int) Worker.count(Worker.class);
        int maxAdventurePoints = (int) Hero_Adventure.count(Hero_Adventure.class);
        
        int adjustedLevelPoints = currentLevelPoints > maxLevelPoints ? maxLevelPoints : currentLevelPoints;
        int adjustedUpgradePoints = currentUpgradePoints > maxUpgradePoints ? maxUpgradePoints : currentUpgradePoints;
        int adjustedTraderPoints = currentTraderPoints > maxTraderPoints ? maxTraderPoints : currentTraderPoints;
        int adjustedSlotPoints = currentSlotPoints > maxSlotPoints ? maxSlotPoints : currentSlotPoints;
        int adjustedTraderStockPoints = currentTraderStockPoints > maxTraderStockPoints ? maxTraderStockPoints : currentTraderStockPoints;
        int adjustedItemPoints = currentItemPoints > maxItemPoints ? maxItemPoints : currentItemPoints;
        int adjustedPreferencePoints = currentPreferencePoints > maxPreferencePoints ? maxPreferencePoints : currentPreferencePoints;
        int adjustedTrophyPoints = currentTrophyPoints > maxTrophyPoints ? maxTrophyPoints : currentTrophyPoints;
        int adjustedWorkerPoints = currentWorkerPoints > maxWorkerPoints ? maxWorkerPoints : currentWorkerPoints;
        int adjustedAdventurePoints = currentAdventurePoints > maxAdventurePoints ? maxAdventurePoints : currentAdventurePoints;

        int adjustedComplete = adjustedLevelPoints + adjustedUpgradePoints + adjustedTraderPoints + adjustedSlotPoints + adjustedTraderStockPoints + adjustedItemPoints + adjustedPreferencePoints + adjustedTrophyPoints + adjustedWorkerPoints + adjustedAdventurePoints;
        int totalToComplete = maxLevelPoints + maxUpgradePoints + maxTraderPoints + maxSlotPoints + maxTraderStockPoints + maxItemPoints + maxPreferencePoints + maxTrophyPoints + maxWorkerPoints + maxAdventurePoints;

        double completionPercentage = (((double) adjustedComplete / (double) totalToComplete) * 100);
        return completionPercentage > 100 ? 100 : completionPercentage;
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
        double xpMultiplierPrestige = xpMultiplier * Math.pow(0.75, Player_Info.getPrestige());
        int modifiedXp = (int) Math.ceil(xpMultiplierPrestige * xp);

        if (Super_Upgrade.isEnabled(Constants.SU_BONUS_XP)) {
            modifiedXp = modifiedXp * 2;
        }

        xpInfo.setIntValue(xpInfo.getIntValue() + modifiedXp);
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
        CollectionsCreated, HighestLevel, ItemsSmelted, ItemsCrafted, ItemsTraded, ItemsEnchanted, ItemsBought, ItemsSold, VisitorsCompleted, CoinsEarned, SavedLevel, UpgradesBought, Prestige, QuestsCompleted, CoinsPurchased
    }
}
