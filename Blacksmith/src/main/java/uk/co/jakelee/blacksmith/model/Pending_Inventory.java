package uk.co.jakelee.blacksmith.model;

import android.text.format.DateUtils;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pending_Inventory extends SugarRecord {
    private Long item;
    private long state;
    private long timeCreated;
    private int quantity;
    private int craftTime;
    private Long locationID;

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

    public static List<Pending_Inventory> getPendingItems(Long locationID, boolean includeFuture) {
        long maxTime = System.currentTimeMillis() + (includeFuture ? DateUtils.YEAR_IN_MILLIS : 0);

        return Select.from(Pending_Inventory.class).where(
                Condition.prop("location_id").eq(locationID),
                Condition.prop("time_created").lt(maxTime))
                .orderBy("time_created ASC").list();
    }

    public static void addItem(Long itemId, long state, int quantity, Long location) {
        Item item = Item.findById(Item.class, itemId);
        int craftTimeMultiplier = Upgrade.getValue("Craft Time");
        int craftTime = item.getModifiedValue(state) * craftTimeMultiplier;

        long time = System.currentTimeMillis();

        Pending_Inventory newItem = new Pending_Inventory(itemId, state, time, quantity, craftTime, location);
        newItem.save();
    }

    public static void addScheduledItem(Long itemId, long state, int quantity, Long location) {
        Item item = Item.findById(Item.class, itemId);
        int craftTimeMultiplier = Upgrade.getValue("Craft Time");
        int craftTime = item.getModifiedValue(state) * craftTimeMultiplier;

        long timeSlotAvailable = getTimeSlotAvailable(location);

        Pending_Inventory newScheduledItem = new Pending_Inventory(itemId, state, timeSlotAvailable, quantity, craftTime, location);
        newScheduledItem.save();
    }

    public static long getTimeSlotAvailable(Long location) {
        List<Pending_Inventory> pendingItems = getPendingItems(location, true);
        int numSlots = Slot.getUnlockedSlots(location);

        // Add all of the times a slot will become available to a list
        List<Long> finishTimes = new ArrayList<>();
        for (Pending_Inventory pending_inventory : pendingItems) {
            long finishTime = pending_inventory.getTimeCreated() + pending_inventory.getCraftTime();
             finishTimes.add(finishTime);
        }

        // Sort these times so the latest time is first
        Collections.sort(finishTimes, Collections.<Long>reverseOrder());

        if (finishTimes.size() >= numSlots) {
            // If we're all full up, get the first time a slot will become available
            return finishTimes.get(numSlots-1);
        } else {
            // Otherwise, it can go in now
            return System.currentTimeMillis();
        }
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
