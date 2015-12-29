package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Location extends SugarRecord {
    Long id;
    String name;

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
