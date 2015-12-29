package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Tier extends SugarRecord {
    Long id;
    String name;

    public Tier() {
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
