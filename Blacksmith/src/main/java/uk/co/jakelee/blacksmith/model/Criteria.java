package uk.co.jakelee.blacksmith.model;

import android.content.Context;

import com.orm.SugarRecord;

import uk.co.jakelee.blacksmith.helper.TextHelper;

public class Criteria extends SugarRecord {
    private Long criteriaID;
    private String name;

    public Criteria() {
    }

    public Criteria(Long criteriaID, String name) {
        this.criteriaID = criteriaID;
        this.name = name;
    }

    public Long getCriteriaID() {
        return criteriaID;
    }

    public void setCriteriaID(Long criteriaID) {
        this.criteriaID = criteriaID;
    }

    public String getName() {
        return name;
    }

    public String getName(Context context) {
        return TextHelper.getInstance(context).getText("criteria_" + criteriaID);
    }

    public void setName(String name) {
        this.name = name;
    }
}
