package uk.co.jakelee.blacksmith.model;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import uk.co.jakelee.blacksmith.helper.TextHelper;

public class Pet extends SugarRecord {
    private int petId;
    private int levelRequired;
    private int coinsRequired;
    private double levelModifier;
    private int currentXp;
    private int maxLevel;
    private long obtained;

    public Pet() {
    }

    public Pet(int petId, int levelRequired, int coinsRequired, double levelModifier, int currentXp, int maxLevel, long obtained) {
        this.petId = petId;
        this.levelRequired = levelRequired;
        this.coinsRequired = coinsRequired;
        this.levelModifier = levelModifier;
        this.currentXp = currentXp;
        this.maxLevel = maxLevel;
        this.obtained = obtained;
    }

    public static Pet get(int petId) {
        return Select.from(Pet.class).where(Condition.prop("pet_id").eq(petId)).first();
    }

    public String getName(Context context) {
        return TextHelper.getInstance(context).getText("pet_name_" + petId);
    }

    public String getDesc(Context context) {
        return TextHelper.getInstance(context).getText("pet_desc_" + petId);
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
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
