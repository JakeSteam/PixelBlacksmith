package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class State extends SugarRecord {
    Long id;
    String name;
    Long initiatingItem;
    int weighting;

    public State() {
    }

    public State(Long id, String name, Long initiatingItem, int weighting) {
        this.id = id;
        this.name = name;
        this.initiatingItem = initiatingItem;
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

    public Long getInitiatingItem() {
        return initiatingItem;
    }

    public void setInitiatingItem(Long initiatingItem) {
        this.initiatingItem = initiatingItem;
    }

    public int getWeighting() {
        return weighting;
    }

    public void setWeighting(int weighting) {
        this.weighting = weighting;
    }
}
