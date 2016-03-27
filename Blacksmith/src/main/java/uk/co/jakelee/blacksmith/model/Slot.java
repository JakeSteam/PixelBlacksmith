package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

import java.util.List;

/**
 * `_id` INTEGER PRIMARY KEY AUTOINCREMENT, `location_id` INTEGER NOT NULL, `level_req` INTEGER NOT NULL, `premium` INTEGER NOT NULL);
 * Created by Jake on 01/12/2015.
 */
public class Slot extends SugarRecord {
    int location;
    int level;
    boolean premium;

    public Slot() {

    }

    public Slot(int location, int level, boolean premium) {
        this.location = location;
        this.level = level;
        this.premium = premium;
        this.save();
    }

    public static boolean hasAvailableSlot(Long locationID) {
        int availableSlots = 0;
        int playerLevel = Player_Info.getPlayerLevel();

        List<Pending_Inventory> pendingItems = Pending_Inventory.getPendingItems(locationID);
        List<Slot> allSlots = Location.getSlots(locationID);
        for (Slot slot : allSlots) {
            if (slot.getLevel() <= playerLevel && !slot.isPremium()) {
                availableSlots++;
            }
        }

        return (availableSlots > pendingItems.size());
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }
}
