package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

import java.util.List;

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
    private int adventuresCompleted;
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
        this.adventuresCompleted = 0;
        this.weighting = weighting;
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

    public void setTierDiscovered(boolean tierDiscovered) {
        this.tierDiscovered = tierDiscovered;
    }

    public boolean isTypeDiscovered() {
        return typeDiscovered;
    }

    public void setTypeDiscovered(boolean typeDiscovered) {
        this.typeDiscovered = typeDiscovered;
    }

    public boolean isStateDiscovered() {
        return stateDiscovered;
    }

    public void setStateDiscovered(boolean stateDiscovered) {
        this.stateDiscovered = stateDiscovered;
    }

    public int getAdventuresCompleted() {
        return adventuresCompleted;
    }

    public void setAdventuresCompleted(int adventuresCompleted) {
        this.adventuresCompleted = adventuresCompleted;
    }

    public int getWeighting() {
        return weighting;
    }

    public void setWeighting(int weighting) {
        this.weighting = weighting;
    }

    public double getDisplayedBonus(Inventory invent) {
        Item item = Item.findById(Item.class, invent.getItem());
        double bonus = 100;

        if (invent.getState() == getStatePreferred() && isStateDiscovered()) {
            bonus *= getStateMultiplier();
        }
        if (item.getTier() == getTierPreferred() && isTierDiscovered()) {
            bonus *= getTierMultiplier();
        }
        if (item.getType() == getTypePreferred() && isTypeDiscovered()) {
            bonus *= getTypeMultiplier();
        }

        return bonus / (double) 100;
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

    public double getHeroBonus(int itemId, int state) {
        Item item = Item.findById(Item.class, itemId);
        int bonus = 100;

        if (state == getStatePreferred()) {
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

    public static int getPreferencesDiscovered() {
        List<Visitor_Type> visitors = Visitor_Type.listAll(Visitor_Type.class);
        int preferencesDiscovered = 0;

        for (Visitor_Type visitor : visitors) {
            if (visitor.isTierDiscovered()) {
                preferencesDiscovered++;
            }
            if (visitor.isTypeDiscovered()) {
                preferencesDiscovered++;
            }
            if (visitor.isStateDiscovered()) {
                preferencesDiscovered++;
            }
        }

        return preferencesDiscovered;
    }
}


