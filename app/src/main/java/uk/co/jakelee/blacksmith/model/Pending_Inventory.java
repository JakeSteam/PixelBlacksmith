package uk.co.jakelee.blacksmith.model;

public class Pending_Inventory {
    int item;
    long timeCreated;
    int quantity;
    int craftTime;
    int locationID;

    public Pending_Inventory() {

    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
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

    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }
}
