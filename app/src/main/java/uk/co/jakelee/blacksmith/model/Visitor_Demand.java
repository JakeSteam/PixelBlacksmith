package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Visitor_Demand extends SugarRecord{
    Long item;
    int state;
    int quantity;
    boolean required;

    public Visitor_Demand() {
    }

    public Visitor_Demand(Long item, int state, int quantity, boolean required) {
        this.item = item;
        this.state = state;
        this.quantity = quantity;
        this.required = required;
    }

    public Long getItem() {
        return item;
    }

    public void setItem(Long item) {
        this.item = item;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
