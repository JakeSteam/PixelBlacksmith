package uk.co.jakelee.blacksmith.model;

import android.content.Context;

import com.orm.SugarRecord;

import uk.co.jakelee.blacksmith.helper.TextHelper;

public class Tier extends SugarRecord {
    private Long id;
    private String name;
    private int minimumLevel;
    private int weighting;

    public Tier() {
    }

    public Tier(Long id, String name, int minimumLevel, int weighting) {
        this.id = id;
        this.name = name;
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
        return TextHelper.getInstance(context).getText("tier_" + id);
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
