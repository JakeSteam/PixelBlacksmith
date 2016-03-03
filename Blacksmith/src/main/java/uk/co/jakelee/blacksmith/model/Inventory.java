package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.helper.Constants;

public class Inventory extends SugarRecord {
    Long item;
    int quantity;
    long state;

    public Inventory() {
    }

    public Inventory(Long item, long state, int quantity) {
        this.item = item;
        this.state = state;
        this.quantity = quantity;
        this.save();
    }

    public Inventory(Long item, long state, int quantity, boolean shouldSave) {
        this.item = item;
        this.state = state;
        this.quantity = quantity;

        if (shouldSave) {
            this.save();
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

    public long getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public static void addItem(Long itemId, long state, int quantity) {
        Inventory craftedItem = getInventory(itemId, state);
        craftedItem.setQuantity(craftedItem.getQuantity() + quantity);
        craftedItem.save();

        Player_Info.addXp(Item.findById(Item.class, craftedItem.getItem()).getValue());
    }

    public static int canCreateItem(Long itemID, long state) {
        Item item = Item.findById(Item.class, itemID);
        if (item.getLevel() > Player_Info.getPlayerLevel()) {
            return Constants.ERROR_PLAYER_LEVEL;
        }

        List<Recipe> ingredients = Select.from(Recipe.class).where(
                Condition.prop("item_state").eq(state),
                Condition.prop("item").eq(item)).list();

        for (Recipe recipe : ingredients) {
            List<Inventory> inventories = Select.from(Inventory.class).where(
                    Condition.prop("item").eq(recipe.getIngredient()),
                    Condition.prop("state").eq(recipe.getIngredientState())).list();

            if (inventories.size() == 0 || recipe.getQuantity() > inventories.get(0).getQuantity()) {
                return Constants.ERROR_NOT_ENOUGH_INGREDIENTS;
            }
        }

        return Constants.SUCCESS;
    }

    public static void removeItemIngredients(Long itemId, long state) {
        List<Recipe> ingredients = Recipe.getIngredients(itemId, state);
        for (Recipe ingredient : ingredients) {
            Inventory ownedItems = Inventory.getInventory(ingredient.getIngredient(), ingredient.getIngredientState());
            ownedItems.setQuantity(ownedItems.getQuantity() - ingredient.getQuantity());
            ownedItems.save();
        }
    }

    public static int tryCreateItem(Long itemId, long state, Long locationID) {
        Item item = Item.findById(Item.class, itemId);

        if (!Slot.hasAvailableSlot(locationID)) {
            return Constants.ERROR_NO_SPARE_SLOTS;
        }

        int canCreate = canCreateItem(itemId, state);
        if (canCreate != Constants.SUCCESS) {
            return canCreate;
        }

        removeItemIngredients(itemId, state);
        Pending_Inventory.addItem(itemId, state, 1, locationID);
        return Constants.SUCCESS;
    }

    public static int enchantItem(Long itemId, Long gemId, Long locationID) {
        int quantity = 1;

        Inventory itemInventory = Inventory.getInventory(itemId, Constants.STATE_NORMAL);
        Inventory gemInventory = Inventory.getInventory(gemId, Constants.STATE_NORMAL);

        if (!Slot.hasAvailableSlot(locationID)) {
            return Constants.ERROR_NO_SPARE_SLOTS;
        } else if (itemInventory.getQuantity() <= 0) {
            return Constants.ERROR_NO_ITEMS;
        } else if (gemInventory.getQuantity() <= 0) {
            return Constants.ERROR_NO_GEMS;
        } else {
            itemInventory.setQuantity(itemInventory.getQuantity() - 1);
            itemInventory.save();

            gemInventory.setQuantity(gemInventory.getQuantity() - 1);
            gemInventory.save();

            State enchantedItemState = Select.from(State.class).where(
                    Condition.prop("initiating_item").eq(gemId)).first();

            Pending_Inventory.addItem(itemId, enchantedItemState.getId().intValue(), quantity, locationID);
            return Constants.SUCCESS;
        }
    }

    public static Inventory getInventory(Long id, long state) {
        List<Inventory> inventories = Select.from(Inventory.class).where(
                Condition.prop("state").eq(state),
                Condition.prop("item").eq(id)).list();

        // If nothing is returned, return a default count of 0.
        if (inventories.size() > 0) {
            return inventories.get(0);
        } else {
            return new Inventory(id, state, 0, false);
        }
    }

    public boolean haveSeen() {
        long itemFound = Select.from(Inventory.class).where(
                Condition.prop("state").eq(state),
                Condition.prop("item").eq(item)).count();

        return itemFound > 0L;
    }

    public static boolean haveSeen(long item, long state) {
        long itemFound = Select.from(Inventory.class).where(
                Condition.prop("state").eq(state),
                Condition.prop("item").eq(item)).count();

        return itemFound > 0L;
    }

    public static boolean canSellItem(Long itemId, long state, int quantity) {
        Inventory foundInventory = getInventory(itemId, state);

        return (foundInventory.getQuantity() - quantity) >= 0;
    }

    public static int sellItem(Long itemId, long state, int quantity, int price) {
        if (!canSellItem(itemId, state, quantity)) {
            return Constants.ERROR_NOT_ENOUGH_ITEMS;
        } else if (!Slot.hasAvailableSlot(Constants.LOCATION_SELLING)) {
            return Constants.ERROR_NO_SPARE_SLOTS;
        } else {
            Inventory itemStock = Inventory.getInventory(itemId, state);
            itemStock.setQuantity(itemStock.getQuantity() - quantity);
            itemStock.save();

            Pending_Inventory.addItem(Constants.ITEM_COINS, Constants.STATE_NORMAL, price, Constants.LOCATION_SELLING);

            return Constants.SUCCESS;
        }
    }

    public static int tradeItem(Long itemId, long state, int quantity, int price) {
        Inventory itemStock = Inventory.getInventory(itemId, state);
        itemStock.setQuantity(itemStock.getQuantity() - quantity);
        itemStock.save();
        Inventory.addItem(Constants.ITEM_COINS, Constants.STATE_NORMAL, price);

        return Constants.SUCCESS;
    }

    public static int canBuyItem(Long itemID, int state, Long traderType, int price) {
        Inventory coins = Select.from(Inventory.class).where(
                Condition.prop("item").eq(Constants.ITEM_COINS)).first();

        Trader_Stock itemStock = Select.from(Trader_Stock.class).where(
                Condition.prop("trader_type").eq(traderType),
                Condition.prop("item_id").eq(itemID),
                Condition.prop("state").eq(state)).first();

        if ((coins.getQuantity() - price) <= 0) {
            return Constants.ERROR_NOT_ENOUGH_COINS;
        } else if (itemStock.getStock() <= 0) {
            return Constants.ERROR_TRADER_RUN_OUT;
        } else {
            return Constants.SUCCESS;
        }
    }

    public static int buyItem(Long itemID, int state, Long traderType, int price) {
        int canBuyResponse = canBuyItem(itemID, state, traderType, price);
        if (canBuyResponse != Constants.SUCCESS) {
            return canBuyResponse;
        } else if (!Slot.hasAvailableSlot(Constants.LOCATION_MARKET)) {
            return Constants.ERROR_NO_SPARE_SLOTS;
        } else {
            // Remove coins
            Inventory coinStock = Inventory.getInventory(Constants.ITEM_COINS, state);
            coinStock.setQuantity(coinStock.getQuantity() - price);
            coinStock.save();

            // Remove stock
            Trader_Stock itemStock = Select.from(Trader_Stock.class).where(
                    Condition.prop("trader_type").eq(traderType),
                    Condition.prop("item_id").eq(itemID),
                    Condition.prop("state").eq(state)).first();
            itemStock.setStock(itemStock.getStock() - 1);
            itemStock.save();

            // Add item
            Pending_Inventory.addItem(itemID, Constants.STATE_NORMAL, 1, Constants.LOCATION_MARKET);

            return Constants.SUCCESS;
        }
    }
}
