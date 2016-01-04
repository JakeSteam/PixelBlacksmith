package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Visitor extends SugarRecord{
    Long arrivalTime;
    Long type;

    public Visitor(){
    }

    public Visitor(Long arrivalTime, Long type) {
        this.arrivalTime = arrivalTime;
        this.type = type;
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
