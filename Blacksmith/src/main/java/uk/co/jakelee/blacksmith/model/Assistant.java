package uk.co.jakelee.blacksmith.model;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import uk.co.jakelee.blacksmith.helper.TextHelper;

public class Assistant extends SugarRecord {
    private int assistantId;
    private int levelRequired;
    private int coinsRequired;
    private double levelModifier;
    private int currentXp;
    private int maxLevel;
    private long obtained;
    private String name;
    private int rewardItem;
    private int rewardState;
    private int rewardQuantity;
    private long rewardFrequency;
    private double boost;

    public Assistant() {
    }

    public Assistant(int assistantId, int levelRequired, int coinsRequired, double levelModifier, int maxLevel, long obtained, int rewardItem, int rewardState, int rewardQuantity, long rewardFrequency, double boost) {
        this.assistantId = assistantId;
        this.levelRequired = levelRequired;
        this.coinsRequired = coinsRequired;
        this.levelModifier = levelModifier;
        this.currentXp = 0;
        this.maxLevel = maxLevel;
        this.obtained = obtained;
        this.name = "";
        this.rewardItem = rewardItem;
        this.rewardState = rewardState;
        this.rewardQuantity = rewardQuantity;
        this.rewardFrequency = rewardFrequency;
        this.boost = boost;
    }

    public static Assistant get(int assistantId) {
        return Select.from(Assistant.class).where(Condition.prop("assistant_id").eq(assistantId)).first();
    }

    public String getTypeName(Context context) {
        return TextHelper.getInstance(context).getText("assistant_name_" + assistantId);
    }

    public String getDesc(Context context) {
        return TextHelper.getInstance(context).getText("assistant_desc_" + assistantId);
    }

    public int getAssistantId() {
        return assistantId;
    }

    public void setAssistantId(int assistantId) {
        this.assistantId = assistantId;
    }

    public int getLevelRequired() {
        return levelRequired;
    }

    public void setLevelRequired(int levelRequired) {
        this.levelRequired = levelRequired;
    }

    public int getCoinsRequired() {
        return coinsRequired;
    }

    public void setCoinsRequired(int coinsRequired) {
        this.coinsRequired = coinsRequired;
    }

    public double getLevelModifier() {
        return levelModifier;
    }

    public void setLevelModifier(double levelModifier) {
        this.levelModifier = levelModifier;
    }

    public int getCurrentXp() {
        return currentXp;
    }

    public void setCurrentXp(int currentXp) {
        this.currentXp = currentXp;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public long getObtained() {
        return obtained;
    }

    public void setObtained(long obtained) {
        this.obtained = obtained;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRewardItem() {
        return rewardItem;
    }

    public void setRewardItem(int rewardItem) {
        this.rewardItem = rewardItem;
    }

    public int getRewardState() {
        return rewardState;
    }

    public void setRewardState(int rewardState) {
        this.rewardState = rewardState;
    }

    public int getRewardQuantity() {
        return rewardQuantity;
    }

    public void setRewardQuantity(int rewardQuantity) {
        this.rewardQuantity = rewardQuantity;
    }

    public long getRewardFrequency() {
        return rewardFrequency;
    }

    public void setRewardFrequency(long rewardFrequency) {
        this.rewardFrequency = rewardFrequency;
    }

    public double getBoost() {
        return getLevel() * boost;
    }

    public double getBoost(int level) {
        return level * boost;
    }

    public void setBoost(double boost) {
        this.boost = boost;
    }

    public int getLevel() {
        int level = (int) (getLevelModifier() * Math.sqrt(getCurrentXp()));
        return level > maxLevel ? maxLevel : level;
    }

    public int getTier() {
        int level = getLevel();
        return (level > getMaxLevel() ? getMaxLevel() : level) / 10;
    }

    public int getLevelProgress() {
        int currentXP = getCurrentXp();
        int currentLevelXP = getXpForLevel(getLevelModifier(), getLevel());
        int nextLevelXP = getXpForLevel(getLevelModifier(), getLevel() + 1);

        double neededXP = nextLevelXP - currentLevelXP;
        double earnedXP = nextLevelXP - currentXP;

        return 100 - (int) Math.ceil((earnedXP / neededXP) * 100);
    }

    public static int getXpForLevel(double levelModifier, int level) {
        return (int) Math.ceil(Math.pow(level / levelModifier, 2));
    }

}
