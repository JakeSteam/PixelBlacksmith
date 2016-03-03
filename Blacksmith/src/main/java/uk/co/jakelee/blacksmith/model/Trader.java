package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Trader extends SugarRecord {
    long arrivalTime;
    long departureTime;
    long visitorType;

    public Trader() {
    }

    public Trader(long arrivalTime, long departureTime, long visitorType) {
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.visitorType = visitorType;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public long getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(long departureTime) {
        this.departureTime = departureTime;
    }

    public long getVisitorType() {
        return visitorType;
    }

    public void setVisitorType(long visitorType) {
        this.visitorType = visitorType;
    }
}
