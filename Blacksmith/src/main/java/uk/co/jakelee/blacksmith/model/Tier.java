package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Tier extends SugarRecord {
    Long id;
    String name;
    int minimumLevel;
    int weighting;

    public Tier() {
    }

    public Tier(Long id, String name, int minimumLevel, int weighting) {
        this.id = id;
        this.name = name;
        this.minimumLevel = minimumLevel;
        this.weighting = weighting;
        this.save();
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

    public int getMinimumLevel() {
        return minimumLevel;
    }

    public void setMinimumLevel(int minimumLevel) {
        this.minimumLevel = minimumLevel;
    }

    public int getWeighting() {
        return weighting;
    }

    public void setWeighting(int weighting) {
        this.weighting = weighting;
    }
}
