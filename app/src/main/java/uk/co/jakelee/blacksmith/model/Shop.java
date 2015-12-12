package uk.co.jakelee.blacksmith.model;

public class Shop {
    int id;
    int shopkeeper;
    int location;
    String name;
    String description;
    int level;
    int discovered;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
