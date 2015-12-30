package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

import java.util.List;

public class Pending_Inventory extends SugarRecord {
    Long item;
    int state;
    long timeCreated;
    int quantity;
    int craftTime;
    Long locationID;

    public Pending_Inventory() {
    }

    public Pending_Inventory(Long item, int state, long timeCreated, int quantity, int craftTime, Long locationID) {
        this.item = item;
        this.state = state;
        this.timeCreated = timeCreated;
        this.quantity = quantity;
        this.craftTime = craftTime;
        this.locationID = locationID;
    }

    public static List<Pending_Inventory> getPendingItems(Long locationID) {
        return Pending_Inventory.find(Pending_Inventory.class, "location_id = ?", Long.toString(locationID));
    }

    public static void addItem(Long itemId, int state, int quantity, Long location) {
        Item item = Item.findById(Item.class, itemId);
        long time = System.currentTimeMillis();
        int craftTimeMultiplier = 3000;
        int craftTime = item.getValue() * craftTimeMultiplier;

        Pending_Inventory newItem = new Pending_Inventory(itemId, state, time, quantity, craftTime, location);
        newItem.save();
    }

    public static List<Pending_Inventory> getPendingItems(String location) {
        List<Location> locations = Location.find(Location.class, "name = ?", location);
        Location itemLocation = locations.get(0);
        return Pending_Inventory.getPendingItems(itemLocation.getId());
    }

    public Long getItem() {
        return item;
    }

    public void setItem(Long item) {
        this.item = item;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCraftTime() {
        return craftTime;
    }

    public void setCraftTime(int craftTime) {
        this.craftTime = craftTime;
    }

    public Long getLocationID() {
        return locationID;
    }

    public void setLocationID(Long locationID) {
        this.locationID = locationID;
    }
}
