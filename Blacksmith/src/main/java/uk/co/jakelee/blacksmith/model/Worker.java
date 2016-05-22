package uk.co.jakelee.blacksmith.model;


import com.orm.SugarRecord;

import java.util.List;

public class Worker extends SugarRecord {
    private long workerID;
    private long characterID;
    private int levelUnlocked;
    private long toolUsed;
    private long toolState;
    private long timeStarted;
    private int timesCompleted;
    private boolean purchased;
    private long foodUsed;
    private long favouriteFood;
    private boolean favouriteFoodDiscovered;

    public Worker() {
    }

    public Worker(long workerID, long characterID, int levelUnlocked, long toolUsed, long toolState, long timeStarted, int timesCompleted, boolean purchased) {
        this.workerID = workerID;
        this.characterID = characterID;
        this.levelUnlocked = levelUnlocked;
        this.toolUsed = toolUsed;
        this.toolState = toolState;
        this.timeStarted = timeStarted;
        this.timesCompleted = timesCompleted;
        this.purchased = purchased;
    }

    public Worker(long workerID, long characterID, int levelUnlocked, long toolUsed, long toolState, long timeStarted, int timesCompleted, boolean purchased, long foodUsed, long favouriteFood, boolean favouriteFoodDiscovered) {
        this.workerID = workerID;
        this.characterID = characterID;
        this.levelUnlocked = levelUnlocked;
        this.toolUsed = toolUsed;
        this.toolState = toolState;
        this.timeStarted = timeStarted;
        this.timesCompleted = timesCompleted;
        this.purchased = purchased;
        this.foodUsed = foodUsed;
        this.favouriteFood = favouriteFood;
        this.favouriteFoodDiscovered = favouriteFoodDiscovered;
    }

    public long getWorkerID() {
        return workerID;
    }

    public void setWorkerID(long workerID) {
        this.workerID = workerID;
    }

    public long getCharacterID() {
        return characterID;
    }

    public void setCharacterID(long characterID) {
        this.characterID = characterID;
    }

    public int getLevelUnlocked() {
        return levelUnlocked;
    }

    public void setLevelUnlocked(int levelUnlocked) {
        this.levelUnlocked = levelUnlocked;
    }

    public long getToolUsed() {
        return toolUsed;
    }

    public void setToolUsed(long toolUsed) {
        this.toolUsed = toolUsed;
    }

    public long getToolState() {
        return toolState;
    }

    public void setToolState(long toolState) {
        this.toolState = toolState;
    }

    public long getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(long timeStarted) {
        this.timeStarted = timeStarted;
    }

    public int getTimesCompleted() {
        return timesCompleted;
    }

    public void setTimesCompleted(int timesCompleted) {
        this.timesCompleted = timesCompleted;
    }

    public boolean isPurchased() {
        return purchased;
    }

    public void setPurchased(boolean purchased) {
        this.purchased = purchased;
    }

    public long getFoodUsed() {
        return foodUsed;
    }

    public void setFoodUsed(long foodUsed) {
        this.foodUsed = foodUsed;
    }

    public long getFavouriteFood() {
        return favouriteFood;
    }

    public void setFavouriteFood(long favouriteFood) {
        this.favouriteFood = favouriteFood;
    }

    public boolean isFavouriteFoodDiscovered() {
        return favouriteFoodDiscovered;
    }

    public void setFavouriteFoodDiscovered(boolean favouriteFoodDiscovered) {
        this.favouriteFoodDiscovered = favouriteFoodDiscovered;
    }

    public static int getTotalTrips() {
        int trips = 0;
        List<Worker> workers = Worker.listAll(Worker.class);

        for (Worker worker : workers) {
            trips += worker.getTimesCompleted();
        }

        return trips;

    }
}
