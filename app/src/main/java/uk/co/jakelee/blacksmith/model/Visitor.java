package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

import java.util.List;

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

    public Visitor(Long arrivalTime, Long type) {
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

    public boolean isVisitorComplete() {
        List<Visitor_Demand> visitorDemands = Visitor_Demand.find(Visitor_Demand.class, "visitor_id = ?", Long.toString(this.getId()));
        for (Visitor_Demand demand : visitorDemands) {
            if (demand.isRequired() && !demand.isDemandFulfilled()) {
                return false;
            }
        }
        return true;
    }
}
