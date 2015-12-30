package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

import java.util.List;

/**
 * `_id` INTEGER PRIMARY KEY AUTOINCREMENT, `location_id` INTEGER NOT NULL, `level_req` INTEGER NOT NULL, `premium` INTEGER NOT NULL);
 * Created by Jake on 01/12/2015.
 */
public class Slots extends SugarRecord {
    Long id;
    int location;
    int level;
    int premium;

    public Slots() {

    }

    public Slots(Long id, int location, int level, int premium) {
        this.id = id;
        this.location = location;
        this.level = level;
        this.premium = premium;
    }

    public static boolean hasAvailableSlot(String location) {
        int availableSlots = 0;
        int playerLevel = Player_Info.getPlayerLevel();

        List<Pending_Inventory> pendingItems = Pending_Inventory.getPendingItems(location);
        List<Slots> allSlots = Location.getSlots(location);
        for (Slots slot : allSlots) {
            if (slot.getLevel() <= playerLevel && slot.getPremium() != 1) {
                availableSlots++;
            }
        }

        return (availableSlots > pendingItems.size());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getPremium() {
        return premium;
    }

    public void setPremium(int premium) {
        this.premium = premium;
    }
}
