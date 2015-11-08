package uk.co.jakelee.blacksmith.model;

/**
 * Created by Jake on 07/11/2015.
 */
public class Inventory {
    int item;
    int quantity;

    public Inventory() {
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
