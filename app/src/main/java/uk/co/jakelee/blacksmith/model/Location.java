package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

import java.util.List;

public class Location extends SugarRecord {
    Long id;
    String name;

    public Location() {

    }

    public Location(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<Slot> getSlots(String location) {
        return Slot.findWithQuery(Slot.class, "SELECT SLOTS.id, SLOTS.location, SLOTS.level, SLOTS.premium FROM SLOTS INNER JOIN LOCATION ON SLOTS.location = LOCATION.id WHERE LOCATION.name = ?", location);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
