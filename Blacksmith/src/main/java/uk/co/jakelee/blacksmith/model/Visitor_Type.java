package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.GooglePlayHelper;

public class Visitor_Type extends SugarRecord {
    private Long visitorID;
    private String name;
    private String desc;
    private Long tierPreferred;
    private Long typePreferred;
    private Long statePreferred;
    private double tierMultiplier;
    private double typeMultiplier;
    private double stateMultiplier;
    private boolean tierDiscovered;
    private boolean typeDiscovered;
    private boolean stateDiscovered;
    private int weighting;

    public Visitor_Type() {
    }

    public Visitor_Type(Long visitorID, String name, String desc, Long tierPreferred, Long typePreferred, Long statePreferred, double tierMultiplier, double typeMultiplier, double stateMultiplier, boolean tierDiscovered, boolean typeDiscovered, boolean stateDiscovered, int weighting) {
        this.visitorID = visitorID;
        this.name = name;
        this.desc = desc;
        this.tierPreferred = tierPreferred;
        this.typePreferred = typePreferred;
        this.statePreferred = statePreferred;
        this.tierMultiplier = tierMultiplier;
        this.typeMultiplier = typeMultiplier;
        this.stateMultiplier = stateMultiplier;
        this.tierDiscovered = tierDiscovered;
        this.typeDiscovered = typeDiscovered;
        this.stateDiscovered = stateDiscovered;
        this.weighting = weighting;
        this.save();
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

    public Long getTierPreferred() {
        return tierPreferred;
    }

    public void setTierPreferred(Long tierPreferred) {
        this.tierPreferred = tierPreferred;
    }

    public Long getTypePreferred() {
        return typePreferred;
    }

    public void setTypePreferred(Long typePreferred) {
        this.typePreferred = typePreferred;
    }

    public Long getStatePreferred() {
        return statePreferred;
    }

    public void setStatePreferred(Long statePreferred) {
        this.statePreferred = statePreferred;
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

    public boolean isTierDiscovered() {
        return tierDiscovered;
    }

    private void setTierDiscovered(boolean tierDiscovered) {
        this.tierDiscovered = tierDiscovered;
    }

    public boolean isTypeDiscovered() {
        return typeDiscovered;
    }

    private void setTypeDiscovered(boolean typeDiscovered) {
        this.typeDiscovered = typeDiscovered;
    }

    public boolean isStateDiscovered() {
        return stateDiscovered;
    }

    private void setStateDiscovered(boolean stateDiscovered) {
        this.stateDiscovered = stateDiscovered;
    }

    public int getWeighting() {
        return weighting;
    }

    public void setWeighting(int weighting) {
        this.weighting = weighting;
    }

    public double getDisplayedBonus(Inventory invent) {
        Item item = Item.findById(Item.class, invent.getItem());
        int bonus = 100;

        if (invent.getState() == getStatePreferred() && isStateDiscovered()) {
            bonus *= getStateMultiplier();
        }
        if (item.getTier() == getTierPreferred() && isTierDiscovered()) {
            bonus *= getTierMultiplier();
        }
        if (item.getType() == getTypePreferred() && isTypeDiscovered()) {
            bonus *= getTypeMultiplier();
        }

        return (double) bonus / (double) 100;
    }

    public double getBonus(Inventory invent) {
        Item item = Item.findById(Item.class, invent.getItem());
        int bonus = 100;

        if (invent.getState() == getStatePreferred()) {
            bonus *= getStateMultiplier();
        }
        if (item.getTier() == getTierPreferred()) {
            bonus *= getTierMultiplier();
        }
        if (item.getType() == getTypePreferred()) {
            bonus *= getTypeMultiplier();
        }

        return (double) bonus / (double) 100;
    }


    public void updateUnlockedPreferences(Item item, long state) {
        if (state == getStatePreferred()) {
            setStateDiscovered(true);
        }
        if (item.getType() == getTypePreferred()) {
            setTypeDiscovered(true);
        }
        if (item.getTier() == getTierPreferred()) {
            setTierDiscovered(true);
        }
        save();
    }

    public void updateBestItem(Item item, long state, int value) {
        Visitor_Stats vStats = Visitor_Stats.findById(Visitor_Stats.class, this.getVisitorID());
        if (value >= vStats.getBestItemValue()) {
            vStats.setBestItem(item.getId());
            vStats.setBestItemState(state);
            vStats.setBestItemValue(value);
            vStats.save();

            GooglePlayHelper.UpdateLeaderboards(Constants.LEADERBOARD_ITEM_VALUE, value);
        }
    }
}


