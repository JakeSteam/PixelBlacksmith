package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Trader extends SugarRecord {
    Long id;
    int trader;
    int location;
    String name;
    String description;
    int level;
    int discovered;

    public Trader() {
    }

    public Trader(Long id, int trader, int location, String name, String description, int level, int discovered) {
        this.id = id;
        this.trader = trader;
        this.location = location;
        this.name = name;
        this.description = description;
        this.level = level;
        this.discovered = discovered;
        this.save();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTrader() {
        return trader;
    }

    public void setTrader(int trader) {
        this.trader = trader;
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

    public int getDiscovered() {
        return discovered;
    }

    public void setDiscovered(int discovered) {
        this.discovered = discovered;
    }
}