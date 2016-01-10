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

        if (canCreateItem(itemId, state) && Slot.hasAvailableSlot(location.getName())) {
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
        List<Inventory> inventories = Inventory.find(Inventory.class, "state = " + state + " AND id = " + itemId);

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

        if (canSellItem(itemId, state, quantity) && Slot.hasAvailableSlot(locationName)) {
            // Remove item
            Inventory itemStock = Inventory.getInventory(itemId, state);
            itemStock.setQuantity(itemStock.getQuantity() - quantity);
            itemStock.save();

            // Add coins
            Pending_Inventory.addItem(coinId, 1, price, locationId);

            return true;
        } else {
            return false;
        }
    }

    public static boolean tradeItem(Long itemId, int state, int quantity, int price) {
        Long coinId = 52L;

        Inventory itemStock = Inventory.getInventory(itemId, state);
        itemStock.setQuantity(itemStock.getQuantity() - quantity);
        itemStock.save();
        Inventory.addItem(coinId, 1, price);

        return true;
    }

    public static boolean canBuyItem(Long itemId, int state, Long shopId, int price) {
        Long coinId = 52L;

        // Can it be afforded?
        List<Inventory> coinsList = Inventory.find(Inventory.class, "item = ?", Long.toString(coinId));
        Inventory coins = coinsList.get(0);

        // Is there stock?
        List<Shop_Stock> itemStocks = Shop_Stock.find(Shop_Stock.class, "shop_id = " + shopId + " AND item_id = " + itemId + " AND state = " + state);
        Shop_Stock itemStock = itemStocks.get(0);

        return ((coins.getQuantity() - price) >= 0) && (itemStock.getStock() > 0);
    }

    public static boolean buyItem(Long itemId, int state, Long shopId, int price) {
        Long locationId = 4L;
        Long coinId = 52L;
        String locationName = "Mine";

        if (canBuyItem(itemId, state, shopId, price) && Slot.hasAvailableSlot(locationName)) {
            // Remove coins
            Inventory coinStock = Inventory.getInventory(coinId, state);
            coinStock.setQuantity(coinStock.getQuantity() - price);
            coinStock.save();

            // Remove stock
            List<Shop_Stock> itemStocks = Shop_Stock.find(Shop_Stock.class, "shop_id = " + shopId + " AND item_id = " + itemId + " AND state = " + state);
            Shop_Stock itemStock = itemStocks.get(0);
            itemStock.setStock(itemStock.getStock() - 1);
            itemStock.save();

            // Add item
            Pending_Inventory.addItem(itemId, 1, 1, locationId);

            return true;
        } else {
            return false;
        }
    }
}
