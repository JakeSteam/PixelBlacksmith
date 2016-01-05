package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Visitor_Type extends SugarRecord{
    Long visitorID;
    String name;
    String desc;
    Long preferredTier;
    Long preferredType;
    Long preferredState;
    double tierMultiplier;
    double typeMultiplier;
    double stateMultiplier;

    public Visitor_Type() {
    }

    public Visitor_Type(Long visitorID, String name, String desc, Long preferredTier, Long preferredType, Long preferredState, double tierMultiplier, double typeMultiplier, double stateMultiplier) {
        this.visitorID = visitorID;
        this.name = name;
        this.desc = desc;
        this.preferredTier = preferredTier;
        this.preferredType = preferredType;
        this.preferredState = preferredState;
        this.tierMultiplier = tierMultiplier;
        this.typeMultiplier = typeMultiplier;
        this.stateMultiplier = stateMultiplier;
    }

    public Long getVisitorID() {
        return visitorID;
    }

    public void setVisitorID(Long visitorID) {
        this.visitorID = visitorID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getPreferredTier() {
        return preferredTier;
    }

    public void setPreferredTier(Long preferredTier) {
        this.preferredTier = preferredTier;
    }

    public Long getPreferredType() {
        return preferredType;
    }

    public void setPreferredType(Long preferredType) {
        this.preferredType = preferredType;
    }

    public Long getPreferredState() {
        return preferredState;
    }

    public void setPreferredState(Long preferredState) {
        this.preferredState = preferredState;
    }

    public double getTierMultiplier() {
        return tierMultiplier;
    }

    public void setTierMultiplier(double tierMultiplier) {
        this.tierMultiplier = tierMultiplier;
    }

    public double getTypeMultiplier() {
        return typeMultiplier;
    }

    public void setTypeMultiplier(double typeMultiplier) {
        this.typeMultiplier = typeMultiplier;
    }

    public double getStateMultiplier() {
        return stateMultiplier;
    }

    public void setStateMultiplier(double stateMultiplier) {
        this.stateMultiplier = stateMultiplier;
    }
}
