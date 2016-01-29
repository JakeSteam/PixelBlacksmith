package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Tier extends SugarRecord {
    Long id;
    String name;
    int weighting;

    public Tier() {
    }

    public Tier(Long id, String name, int weighting) {
        this.id = id;
        this.name = name;
        this.weighting = weighting;
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

    public int getWeighting() {
        return weighting;
    }

    public void setWeighting(int weighting) {
        this.weighting = weighting;
    }
}
