package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.helper.Constants;

public class Pending_Inventory extends SugarRecord {
    Long item;
    long state;
    long timeCreated;
    int quantity;
    int craftTime;
    Long locationID;

    public Pending_Inventory() {
    }

    public Pending_Inventory(Long item, long state, long timeCreated, int quantity, int craftTime, Long locationID) {
        this.item = item;
        this.state = state;
        this.timeCreated = timeCreated;
        this.quantity = quantity;
        this.craftTime = craftTime;
        this.locationID = locationID;
    }

    public static List<Pending_Inventory> getPendingItems(Long locationID) {
        return Select.from(Pending_Inventory.class).where(
                Condition.prop("location_id").eq(locationID)).list();
    }

    public static void addItem(Long itemId, long state, int quantity, Long location) {
        Item item = Item.findById(Item.class, itemId);
        long time = System.currentTimeMillis();
        int craftTimeMultiplier = Constants.CRAFT_TIME_MULTIPLIER;
        int craftTime = item.getModifiedValue(state) * craftTimeMultiplier;

        Pending_Inventory newItem = new Pending_Inventory(itemId, state, time, quantity, craftTime, location);
        newItem.save();
    }

    public Long getItem() {
        return item;
    }

    public void setItem(Long item) {
        this.item = item;
    }

    public long getState() {
        return state;
    }

    public void setState(long state) {
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
