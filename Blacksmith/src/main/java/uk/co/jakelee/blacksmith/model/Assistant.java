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

    public Assistant() {
    }

    public Assistant(int assistantId, int levelRequired, int coinsRequired, double levelModifier, int currentXp, int maxLevel, long obtained) {
        this.assistantId = assistantId;
        this.levelRequired = levelRequired;
        this.coinsRequired = coinsRequired;
        this.levelModifier = levelModifier;
        this.currentXp = currentXp;
        this.maxLevel = maxLevel;
        this.obtained = obtained;
    }

    public static Assistant get(int assistantId) {
        return Select.from(Assistant.class).where(Condition.prop("assistant_id").eq(assistantId)).first();
    }

    public String getName(Context context) {
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
}
