package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Visitor_Stats extends SugarRecord {
    private Long visitorType;
    private int visits;
    private Long bestItem;
    private Long bestItemState;
    private int bestItemValue;
    private Long firstSeen;
    private Long trophyAchieved;

    public Visitor_Stats() {
    }

    public Visitor_Stats(Long visitorType, int visits, Long bestItem, Long bestItemState, int bestItemValue, Long firstSeen, Long trophyAchieved) {
        this.visitorType = visitorType;
        this.visits = visits;
        this.bestItem = bestItem;
        this.bestItemState = bestItemState;
        this.bestItemValue = bestItemValue;
        this.firstSeen = firstSeen;
        this.trophyAchieved = trophyAchieved;
        this.save();
    }

    public Long getVisitorType() {
        return visitorType;
    }

    public void setVisitorType(Long visitorType) {
        this.visitorType = visitorType;
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public Long getBestItem() {
        return bestItem;
    }

    public void setBestItem(Long bestItem) {
        this.bestItem = bestItem;
    }

    public Long getBestItemState() {
        return bestItemState;
    }

    public void setBestItemState(Long bestItemState) {
        this.bestItemState = bestItemState;
    }

    public int getBestItemValue() {
        return bestItemValue;
    }

    public void setBestItemValue(int bestItemValue) {
        this.bestItemValue = bestItemValue;
    }

    public Long getFirstSeen() {
        return firstSeen;
    }

    public void setFirstSeen(Long firstSeen) {
        this.firstSeen = firstSeen;
    }

    public Long getTrophyAchieved() {
        return trophyAchieved;
    }

    public void setTrophyAchieved(Long trophyAchieved) {
        this.trophyAchieved = trophyAchieved;
    }
}
