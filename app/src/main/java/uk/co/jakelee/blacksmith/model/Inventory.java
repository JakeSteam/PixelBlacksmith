package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

import java.util.List;

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

    public static void addItem(Long itemId, int state, int quantity) {
        Inventory craftedItem = getInventory(itemId, state);
        craftedItem.setQuantity(craftedItem.getQuantity() + quantity);
        craftedItem.save();

        Player_Info.addXp(Item.findById(Item.class, craftedItem.getItem()).getValue());
        Player_Info.updateLevelText();
    }

    public static Inventory getInventory(Long id, int state) {
        List<Inventory> inventories = Inventory.find(Inventory.class, "state = " + state + " AND item = " + id);

        // If nothing is returned, return a default count of 0.
        if (inventories.size() > 0) {
            return inventories.get(0);
        } else {
            return new Inventory(id, state, 0);
        }
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
