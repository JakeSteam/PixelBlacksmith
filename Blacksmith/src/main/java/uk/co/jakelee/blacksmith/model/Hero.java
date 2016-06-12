package uk.co.jakelee.blacksmith.model;


import android.util.Pair;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.co.jakelee.blacksmith.helper.Constants;

public class Hero extends SugarRecord {
    private int heroId;
    private int levelUnlocked;
    private int currentAdventure;
    private long timeStarted;
    private boolean purchased;
    private int visitorId;
    private int foodItem;
    private int foodState;
    private int helmetItem;
    private int helmetState;
    private int armourItem;
    private int armourState;
    private int weaponItem;
    private int weaponState;
    private int shieldItem;
    private int shieldState;
    private int glovesItem;
    private int glovesState;
    private int bootsItem;
    private int bootsState;
    private int ringItem;
    private int ringState;

    public Hero() {
    }

    public Hero(int heroId, int levelUnlocked) {
        this.heroId = heroId;
        this.levelUnlocked = levelUnlocked;
        this.currentAdventure = 0;
        this.timeStarted = 0L;
        this.purchased = false;
        this.visitorId = 0;
        this.foodItem = 0;
        this.helmetItem = 0;
        this.armourItem = 0;
        this.weaponItem = 0;
        this.shieldItem = 0;
        this.glovesItem = 0;
        this.bootsItem = 0;
        this.ringItem = 0;
    }

    public static Hero findById(int id) {
        return Select.from(Hero.class).where(
                Condition.prop("hero_id").eq(id)).first();
    }

    public Hero(int heroId, int levelUnlocked, int currentAdventure, long adventureStarted, boolean purchased, int visitorId, int foodItem, int helmetItem, int armourItem, int weaponItem, int shieldItem, int glovesItem, int bootsItem, int ringItem) {
        this.heroId = heroId;
        this.levelUnlocked = levelUnlocked;
        this.currentAdventure = currentAdventure;
        this.timeStarted = adventureStarted;
        this.purchased = purchased;
        this.visitorId = visitorId;
        this.foodItem = foodItem;
        this.foodState = Constants.STATE_NORMAL;
        this.helmetItem = helmetItem;
        this.helmetState = Constants.STATE_NORMAL;
        this.armourItem = armourItem;
        this.armourState = Constants.STATE_NORMAL;
        this.weaponItem = weaponItem;
        this.weaponState = Constants.STATE_NORMAL;
        this.shieldItem = shieldItem;
        this.shieldState = Constants.STATE_NORMAL;
        this.glovesItem = glovesItem;
        this.glovesState = Constants.STATE_NORMAL;
        this.bootsItem = bootsItem;
        this.bootsState = Constants.STATE_NORMAL;
        this.ringItem = ringItem;
        this.ringState = Constants.STATE_NORMAL;
    }

    public int getTotalItemBonusPercent() {
        return (int) getTotalItemBonus();
    }

    public double getTotalItemBonus() {
        Visitor_Type visitor = Visitor_Type.findById(Visitor_Type.class, getVisitorId());
        // Build array of equipped items
        List<Pair<Integer, Integer>> equippedItems = new ArrayList<>(
                Arrays.asList(
                        new Pair<> (getFoodItem(), getFoodState()),
                        new Pair<> (getHelmetItem(), getHelmetState()),
                        new Pair<> (getArmourItem(), getArmourState()),
                        new Pair<> (getWeaponItem(), getWeaponState()),
                        new Pair<> (getShieldItem(), getShieldState()),
                        new Pair<> (getGlovesItem(), getGlovesState()),
                        new Pair<> (getBootsItem(), getBootsState()),
                        new Pair<> (getRingItem(), getRingState()))
        );

        // Build array of equipped item bonuses
        List<Double> equippedItemBonuses = new ArrayList<>();
        for (Pair<Integer, Integer> pair : equippedItems) {
            if (pair.first > 0 && pair.second > 0) {
                equippedItemBonuses.add(visitor.getBonus(pair.first, pair.second));
            }
        }

        // Calculate total bonus
        double totalBonus = 0;
        for (Double bonus : equippedItemBonuses) {
            totalBonus += bonus;
        }

        return totalBonus;
    }

    public static int getAvailableHeroesCount() {
        return getAvailableHeroes().size();
    }

    public static List<Hero> getAvailableHeroes() {
        return Select.from(Hero.class).where(
                Condition.prop("purchased").eq(1),
                Condition.prop("time_started").eq(0)).list();
    }

    public int getHeroId() {
        return heroId;
    }

    public void setHeroId(int heroId) {
        this.heroId = heroId;
    }

    public int getLevelUnlocked() {
        return levelUnlocked;
    }

    public void setLevelUnlocked(int levelUnlocked) {
        this.levelUnlocked = levelUnlocked;
    }

    public int getCurrentAdventure() {
        return currentAdventure;
    }

    public void setCurrentAdventure(int currentAdventure) {
        this.currentAdventure = currentAdventure;
    }

    public long getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(long timeStarted) {
        this.timeStarted = timeStarted;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public int getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(int visitorId) {
        this.visitorId = visitorId;
    }

    public int getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(int foodItem) {
        this.foodItem = foodItem;
    }

    public int getFoodState() {
        return foodState;
    }

    public void setFoodState(int foodState) {
        this.foodState = foodState;
    }

    public int getHelmetItem() {
        return helmetItem;
    }

    public void setHelmetItem(int helmetItem) {
        this.helmetItem = helmetItem;
    }

    public int getHelmetState() {
        return helmetState;
    }

    public void setHelmetState(int helmetState) {
        this.helmetState = helmetState;
    }

    public int getArmourItem() {
        return armourItem;
    }

    public void setArmourItem(int armourItem) {
        this.armourItem = armourItem;
    }

    public int getArmourState() {
        return armourState;
    }

    public void setArmourState(int armourState) {
        this.armourState = armourState;
    }

    public int getWeaponItem() {
        return weaponItem;
    }

    public void setWeaponItem(int weaponItem) {
        this.weaponItem = weaponItem;
    }

    public int getWeaponState() {
        return weaponState;
    }

    public void setWeaponState(int weaponState) {
        this.weaponState = weaponState;
    }

    public int getShieldItem() {
        return shieldItem;
    }

    public void setShieldItem(int shieldItem) {
        this.shieldItem = shieldItem;
    }

    public int getShieldState() {
        return shieldState;
    }

    public void setShieldState(int shieldState) {
        this.shieldState = shieldState;
    }

    public int getGlovesItem() {
        return glovesItem;
    }

    public void setGlovesItem(int glovesItem) {
        this.glovesItem = glovesItem;
    }

    public int getGlovesState() {
        return glovesState;
    }

    public void setGlovesState(int glovesState) {
        this.glovesState = glovesState;
    }

    public int getBootsItem() {
        return bootsItem;
    }

    public void setBootsItem(int bootsItem) {
        this.bootsItem = bootsItem;
    }

    public int getBootsState() {
        return bootsState;
    }

    public void setBootsState(int bootsState) {
        this.bootsState = bootsState;
    }

    public int getRingItem() {
        return ringItem;
    }

    public void setRingItem(int ringItem) {
        this.ringItem = ringItem;
    }

    public int getRingState() {
        return ringState;
    }

    public void setRingState(int ringState) {
        this.ringState = ringState;
    }
}
