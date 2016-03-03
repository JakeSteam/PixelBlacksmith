package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Trader_Type extends SugarRecord {
    int shopkeeper;
    int location;
    String name;
    String description;
    int level;
    int weighting;

    public Trader_Type() {
    }

    public Trader_Type(int shopkeeper, int location, String name, String description, int level, int weighting) {
        this.shopkeeper = shopkeeper;
        this.location = location;
        this.name = name;
        this.description = description;
        this.level = level;
        this.weighting = weighting;
        this.save();
    }

    public int getShopkeeper() {
        return shopkeeper;
    }

    public void setShopkeeper(int shopkeeper) {
        this.shopkeeper = shopkeeper;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getWeighting() {
        return weighting;
    }

    public void setWeighting(int weighting) {
        this.weighting = weighting;
    }
}
