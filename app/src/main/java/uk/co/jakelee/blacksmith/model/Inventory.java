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

    public static boolean canCreateItem(Long itemID, int state) {
        // 1: Check we've got a high enough level
        Item item = Item.findById(Item.class, itemID);
        if (item.getLevel() > Player_Info.getPlayerLevel() || item.getCanCraft() != 1) {
            return false;
        }

        // 2: Check we've got enough of all ingredients
        List<Recipe> ingredients = Recipe.find(Recipe.class, "item_state = " + state + " AND item = " + itemID);
        for (Recipe recipe : ingredients) {
            List<Inventory> inventories = Inventory.find(Inventory.class, "state = " + recipe.getIngredientState() + " AND item = " + recipe.getIngredient());

            Inventory inventory;
            if (inventories.size() > 0) {
                inventory = inventories.get(0);
            } else {
                inventory = new Inventory(recipe.getIngredient(), state, 0);
            }

            if (recipe.getQuantity() > inventory.getQuantity()) {
                return false;
            }
        }

        return true;
    }

    public static void removeItemIngredients(Long itemId, int state) {
        List<Recipe> ingredients = Recipe.getIngredients(itemId, state);
        for (Recipe ingredient : ingredients) {
            Inventory ownedItems = Inventory.getInventory(ingredient.getIngredient(), ingredient.getIngredientState());
            ownedItems.setQuantity(ownedItems.getQuantity() - ingredient.getQuantity());
            ownedItems.save();
        }
    }

    public static boolean createItem(Long itemId, int state, int quantity, Long locationId) {
        List<Location> locations = Location.find(Location.class, "id = " + locationId);
        Location location = locations.get(0);

        if (canCreateItem(itemId, state) && Slots.hasAvailableSlot(location.getName())) {
            removeItemIngredients(itemId, state);
            Pending_Inventory.addItem(itemId, state, quantity, locationId);
            return true;
        } else {
            return false;
        }
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

    public static boolean canSellItem(Long itemId, int state, int quantity) {
        List<Inventory> inventories = Inventory.findWithQuery(Inventory.class, "SELECT * FROM inventory WHERE state = " + state + " AND id = " + itemId);

        Inventory foundInventory;
        if (inventories.size() > 0) {
            foundInventory = inventories.get(0);
        } else {
            foundInventory = new Inventory(itemId, state, 0);
        }

        return (foundInventory.getQuantity() - quantity) >= 0;
    }

    public static boolean sellItem(Long itemId, int state, int quantity, int price) {
        Long locationId = 3L;
        Long coinId = 52L;
        String locationName = "Selling";

        if (canSellItem(itemId, state, quantity) && Slots.hasAvailableSlot(locationName)) {
            // Remove item
            Inventory itemStock = Inventory.getInventory(itemId, state);
            itemStock.setQuantity(itemStock.getQuantity() - quantity);
            itemStock.save();

            Pending_Inventory.addItem(coinId, 1, price, locationId);
            return true;
        } else {
            return false;
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
