package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

public class Location extends SugarRecord {
    Long id;
    String name;

    public Location() {

    }

    public Location(Long id, String name) {
        this.id = id;
        this.name = name;
        this.save();
    }

    public static List<Slot> getSlots(Long locationID) {
        return Select.from(Slot.class).where(
                Condition.prop("location").eq(locationID)).list();
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
