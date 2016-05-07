package uk.co.jakelee.blacksmith.model;


import com.orm.SugarRecord;

public class Worker extends SugarRecord {
    private long workerID;
    private long characterID;
    private int levelUnlocked;
    private long timeStarted;
    private int timesCompleted;

    public Worker() {
    }

    public Worker(long workerID, long characterID, int levelUnlocked, long timeStarted, int timesCompleted) {
        this.workerID = workerID;
        this.characterID = characterID;
        this.levelUnlocked = levelUnlocked;
        this.timeStarted = timeStarted;
        this.timesCompleted = timesCompleted;
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
}
