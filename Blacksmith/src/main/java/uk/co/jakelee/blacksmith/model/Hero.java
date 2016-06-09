package uk.co.jakelee.blacksmith.model;


import com.orm.SugarRecord;

public class Hero extends SugarRecord {
    private int heroId;
    private int levelUnlocked;
    private int currentAdventure;
    private long timeStarted;
    private boolean purchased;
    private int visitorId;
    private int foodItem;
    private int helmetItem;
    private int armourItem;
    private int weaponItem;
    private int shieldItem;
    private int glovesItem;
    private int bootsItem;
    private int ringItem;

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

    public Hero(int heroId, int levelUnlocked, int currentAdventure, long adventureStarted, boolean purchased, int visitorId, int foodItem, int helmetItem, int armourItem, int weaponItem, int shieldItem, int glovesItem, int bootsItem, int ringItem) {
        this.heroId = heroId;
        this.levelUnlocked = levelUnlocked;
        this.currentAdventure = currentAdventure;
        this.timeStarted = adventureStarted;
        this.purchased = purchased;
        this.visitorId = visitorId;
        this.foodItem = foodItem;
        this.helmetItem = helmetItem;
        this.armourItem = armourItem;
        this.weaponItem = weaponItem;
        this.shieldItem = shieldItem;
        this.glovesItem = glovesItem;
        this.bootsItem = bootsItem;
        this.ringItem = ringItem;
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

    public int getHelmetItem() {
        return helmetItem;
    }

    public void setHelmetItem(int helmetItem) {
        this.helmetItem = helmetItem;
    }

    public int getArmourItem() {
        return armourItem;
    }

    public void setArmourItem(int armourItem) {
        this.armourItem = armourItem;
    }

    public int getWeaponItem() {
        return weaponItem;
    }

    public void setWeaponItem(int weaponItem) {
        this.weaponItem = weaponItem;
    }

    public int getShieldItem() {
        return shieldItem;
    }

    public void setShieldItem(int shieldItem) {
        this.shieldItem = shieldItem;
    }

    public int getGlovesItem() {
        return glovesItem;
    }

    public void setGlovesItem(int glovesItem) {
        this.glovesItem = glovesItem;
    }

    public int getBootsItem() {
        return bootsItem;
    }

    public void setBootsItem(int bootsItem) {
        this.bootsItem = bootsItem;
    }

    public int getRingItem() {
        return ringItem;
    }

    public void setRingItem(int ringItem) {
        this.ringItem = ringItem;
    }

    public int getTotalItemBonusPercent() {
        return (int) getTotalItemBonus();
    }

    public double getTotalItemBonus() {
        return 1.11;
    }
}
