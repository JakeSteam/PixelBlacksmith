package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Shop extends SugarRecord {
    Long id;
    int shopkeeper;
    int location;
    String name;
    String description;
    int level;
    int discovered;

    public Shop() {
    }

    public Shop(Long id, int shopkeeper, int location, String name, String description, int level, int discovered) {
        this.id = id;
        this.shopkeeper = shopkeeper;
        this.location = location;
        this.name = name;
        this.description = description;
        this.level = level;
        this.discovered = discovered;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getDiscovered() {
        return discovered;
    }

    public void setDiscovered(int discovered) {
        this.discovered = discovered;
    }
}
