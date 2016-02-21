package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Criteria extends SugarRecord{
    Long criteriaID;
    String name;

    public Criteria() {
    }

    public Criteria(Long criteriaID, String name) {
        this.criteriaID = criteriaID;
        this.name = name;
        this.save();
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

    public void setName(String name) {
        this.name = name;
    }
}
