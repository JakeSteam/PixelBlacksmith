package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Visitor_Type extends SugarRecord{
    Long visitorId;
    String name;
    String desc;
    Long preferredTier;
    Long preferredType;
    Long preferredState;
    int tierMultiplier;
    int typeMultiplier;
    int stateMultiplier;

    public Visitor_Type() {
    }

    public Visitor_Type(Long visitorId, String name, String desc, Long preferredTier, Long preferredType, Long preferredState, int tierMultiplier, int typeMultiplier, int stateMultiplier) {
        this.visitorId = visitorId;
        this.name = name;
        this.desc = desc;
        this.preferredTier = preferredTier;
        this.preferredType = preferredType;
        this.preferredState = preferredState;
        this.tierMultiplier = tierMultiplier;
        this.typeMultiplier = typeMultiplier;
        this.stateMultiplier = stateMultiplier;
    }

    public Long getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(Long visitorId) {
        this.visitorId = visitorId;
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

    public int getTierMultiplier() {
        return tierMultiplier;
    }

    public void setTierMultiplier(int tierMultiplier) {
        this.tierMultiplier = tierMultiplier;
    }

    public int getTypeMultiplier() {
        return typeMultiplier;
    }

    public void setTypeMultiplier(int typeMultiplier) {
        this.typeMultiplier = typeMultiplier;
    }

    public int getStateMultiplier() {
        return stateMultiplier;
    }

    public void setStateMultiplier(int stateMultiplier) {
        this.stateMultiplier = stateMultiplier;
    }
}
