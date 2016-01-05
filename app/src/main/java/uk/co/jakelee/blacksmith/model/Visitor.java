package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Visitor extends SugarRecord{
    Long visitorID;
    Long arrivalTime;
    Long type;

    public Visitor(){
    }

    public Visitor(Long visitorID, Long arrivalTime, Long type) {
        this.visitorID = visitorID;
        this.arrivalTime = arrivalTime;
        this.type = type;
    }

    @Override
    public Long getId() {
        return visitorID;
    }

    @Override
    public void setId(Long visitorID) {
        this.visitorID = visitorID;
    }

    public Long getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }
}
