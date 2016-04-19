package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Achievement extends SugarRecord {
    private String name;
    private String description;
    private int maximumValue;
    private long playerInfoID;
    private String remoteID;

    public Achievement() {
    }

    public Achievement(String name, String description, int maximumValue, long playerInfoID, String remoteID) {
        this.name = name;
        this.description = description;
        this.maximumValue = maximumValue;
        this.playerInfoID = playerInfoID;
        this.remoteID = remoteID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaximumValue() {
        return maximumValue;
    }

    public void setMaximumValue(int maximumValue) {
        this.maximumValue = maximumValue;
    }

    public long getPlayerInfoID() {
        return playerInfoID;
    }

    public void setPlayerInfoID(long playerInfoID) {
        this.playerInfoID = playerInfoID;
    }

    public String getRemoteID() {
        return remoteID;
    }

    public void setRemoteID(String remoteID) {
        this.remoteID = remoteID;
    }
}
