package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Inventory extends SugarRecord {
    Long item;
    int quantity;
    int state;

    public Inventory() {
    }

    public Inventory(Long item, int state, int quantity) {
        this.item = item;
        this.state = state;
        this.quantity = quantity;
    }

    public Long getItem() {
        return item;
    }

    public void setItem(Long item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
