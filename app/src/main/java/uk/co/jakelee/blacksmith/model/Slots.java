package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

/**
 * `_id` INTEGER PRIMARY KEY AUTOINCREMENT, `location_id` INTEGER NOT NULL, `level_req` INTEGER NOT NULL, `premium` INTEGER NOT NULL);
 * Created by Jake on 01/12/2015.
 */
public class Slots extends SugarRecord {
    Long id;
    int location;
    int level;
    int premium;

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
