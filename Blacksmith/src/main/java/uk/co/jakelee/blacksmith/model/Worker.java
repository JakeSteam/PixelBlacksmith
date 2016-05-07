package uk.co.jakelee.blacksmith.model;


import com.orm.SugarRecord;

import java.util.List;

public class Worker extends SugarRecord {
    private long workerID;
    private long characterID;
    private int levelUnlocked;
    private long toolUsed;
    private long timeStarted;
    private int timesCompleted;
    private boolean purchased;

    public Worker() {
    }

    public Worker(long workerID, long characterID, int levelUnlocked, long toolUsed, long timeStarted, int timesCompleted, boolean purchased) {
        this.workerID = workerID;
        this.characterID = characterID;
        this.levelUnlocked = levelUnlocked;
        this.toolUsed = toolUsed;
        this.timeStarted = timeStarted;
        this.timesCompleted = timesCompleted;
        this.purchased = purchased;
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

    public static int getTotalTrips() {
        int trips = 0;
        List<Worker> workers = Worker.listAll(Worker.class);

        for (Worker worker : workers) {
            trips += worker.getTimesCompleted();
        }

        return trips;

    }
}
