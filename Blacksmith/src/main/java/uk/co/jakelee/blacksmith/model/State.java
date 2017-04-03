package uk.co.jakelee.blacksmith.model;

import android.content.Context;

import com.orm.SugarRecord;

import uk.co.jakelee.blacksmith.helper.TextHelper;

public class State extends SugarRecord {
    String prefix;
    private Long id;
    private String name;
    private Long initiatingItem;
    private int minimumLevel;
    private int weighting;

    public State() {
    }

    public State(Long id, String name, String prefix, Long initiatingItem, int minimumLevel, int weighting) {
        this.id = id;
        this.name = name;
        this.prefix = prefix;
        this.initiatingItem = initiatingItem;
        this.minimumLevel = minimumLevel;
        this.weighting = weighting;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName(Context context) {
        return TextHelper.getInstance(context).getText("state_" + id);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Long getInitiatingItem() {
        return initiatingItem;
    }

    public void setInitiatingItem(Long initiatingItem) {
        this.initiatingItem = initiatingItem;
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
