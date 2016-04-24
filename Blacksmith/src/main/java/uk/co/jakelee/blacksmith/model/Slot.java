package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

/**
 * `_id` INTEGER PRIMARY KEY AUTOINCREMENT, `location_id` INTEGER NOT NULL, `level_req` INTEGER NOT NULL, `premium` INTEGER NOT NULL);
 * Created by Jake on 01/12/2015.
 */
public class Slot extends SugarRecord {
    private int location;
    private int level;
    private boolean premium;

    public Slot() {

    }

    public Slot(int location, int level, boolean premium) {
        this.location = location;
        this.level = level;
        this.premium = premium;
    }

    public static boolean hasAvailableSlot(Long locationID) {
        List<Pending_Inventory> pendingItems = Pending_Inventory.getPendingItems(locationID, false);
        int availableSlots = getUnlockedSlots(locationID);

        return (availableSlots > pendingItems.size());
    }

    public static int getUnlockedSlots(Long locationID) {
        int playerLevel = Player_Info.getPlayerLevel();
        boolean playerIsPremium = Player_Info.isPremium();
        int availableSlots = 0;

        List<Slot> allSlots = Location.getSlots(locationID);
        for (Slot slot : allSlots) {
            if (slot.getLevel() <= playerLevel && (!slot.isPremium() || (slot.isPremium() && playerIsPremium))) {
                availableSlots++;
            }
        }

        return availableSlots;
    }

    public static int getUnlockedCount() {
        int playerLevel = Player_Info.getPlayerLevel();
        boolean playerIsPremium = Player_Info.isPremium();
        int availableSlots = 0;

        List<Slot> slots = Select.from(Slot.class).where(
                Condition.prop("level").lt(playerLevel + 1)).list();

        for (Slot slot : slots) {
            if (!slot.isPremium() || (slot.isPremium() && playerIsPremium)) {
                availableSlots++;
            }
        }

        return availableSlots;
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
