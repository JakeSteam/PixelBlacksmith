package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

public class Visitor extends SugarRecord {
    private Long arrivalTime;
    private Long type;

    public Visitor() {
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

    public boolean isVisitorComplete() {
        List<Visitor_Demand> visitorDemands = Select.from(Visitor_Demand.class).where(
                Condition.prop("visitor_id").eq(this.getId())).list();

        for (Visitor_Demand demand : visitorDemands) {
            if (demand.isRequired() && !demand.isDemandFulfilled()) {
                return false;
            }
        }
        return true;
    }

    public boolean isVisitorFullyComplete() {
        List<Visitor_Demand> visitorDemands = Select.from(Visitor_Demand.class).where(
                Condition.prop("visitor_id").eq(this.getId())).list();

        for (Visitor_Demand demand : visitorDemands) {
            if (!demand.isDemandFulfilled()) {
                return false;
            }
        }
        return true;
    }
}
