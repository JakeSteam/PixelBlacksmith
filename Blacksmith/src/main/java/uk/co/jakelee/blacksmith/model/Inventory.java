package uk.co.jakelee.blacksmith.model;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.VisitorHelper;

public class Inventory extends SugarRecord implements Serializable {
    private Long item;
    private int quantity;
    private long state;
    private boolean unsellable;

    public Inventory() {
    }

    public Inventory(Long item, long state, int quantity) {
        this.item = item;
        this.state = state;
        this.quantity = quantity;
        this.unsellable = false;
    }

    public static void addItem(Pending_Inventory item, boolean rewardXp) {
        addItem(item.getItem(), item.getState(), item.getQuantity(), rewardXp);
    }

    public static void addItem(Long itemId, long state, int quantity) {
        addItem(itemId, state, quantity, true);
    }

    public static void addItem(Long itemId, long state, int quantity, boolean rewardXp) {
        Inventory craftedItem = getInventory(itemId, state);
        craftedItem.setQuantity(craftedItem.getQuantity() + quantity);
        craftedItem.save();

        if (rewardXp) {
            Player_Info.addXp(Item.findById(Item.class, craftedItem.getItem()).getModifiedValue(state));
        }
    }

    public static boolean haveLevelFor(Long itemID) {
        Item item = Item.findById(Item.class, itemID);
        return Player_Info.getPlayerLevel() >= item.getLevel();
    }

    public static int getNumberCreatable(Long itemID, long state) {
        Item item = Item.findById(Item.class, itemID);
        if (item.getLevel() > Player_Info.getPlayerLevel()) {
            return 0;
        }

        List<Recipe> ingredients = Select.from(Recipe.class).where(
                Condition.prop("item_state").eq(state),
                Condition.prop("item").eq(item)).list();

        List<Integer> quantities = new ArrayList<>();
        for (Recipe recipe : ingredients) {
            Inventory ingredientInventory = Select.from(Inventory.class).where(
                    Condition.prop("item").eq(recipe.getIngredient()),
                    Condition.prop("state").eq(recipe.getIngredientState())).first();

            if (ingredientInventory == null) {
                quantities.add(0);
            } else {
                quantities.add((ingredientInventory.getQuantity() / recipe.getQuantity()));
            }
        }

        return Collections.min(quantities);
    }

    public static int canCreateBulkItem(Long itemID, long state, int quantity) {
        Item item = Item.findById(Item.class, itemID);
        if (item.getLevel() > Player_Info.getPlayerLevel()) {
            return Constants.ERROR_PLAYER_LEVEL;
        }

        List<Recipe> ingredients = Select.from(Recipe.class).where(
                Condition.prop("item_state").eq(state),
                Condition.prop("item").eq(item)).list();

        for (Recipe recipe : ingredients) {
            Inventory ingredientInventory = Select.from(Inventory.class).where(
                    Condition.prop("item").eq(recipe.getIngredient()),
                    Condition.prop("state").eq(recipe.getIngredientState())).first();

            if (ingredientInventory == null || ingredientInventory.getQuantity() == 0 || (recipe.getQuantity() * quantity) > ingredientInventory.getQuantity()) {
                return Constants.ERROR_NOT_ENOUGH_INGREDIENTS;
            }
        }

        return Constants.SUCCESS;
    }

    private static int canCreateItem(Long itemID, long state) {
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

    public static void removeItemIngredients(Long itemID, long state, int quantity) {
        List<Recipe> ingredients = Recipe.getIngredients(itemID, state);
        for (Recipe ingredient : ingredients) {
            Inventory ownedItems = Inventory.getInventory(ingredient.getIngredient(), ingredient.getIngredientState());
            ownedItems.setQuantity(ownedItems.getQuantity() - (ingredient.getQuantity() * quantity));
            ownedItems.save();
        }
    }

    private static void removeItemIngredients(Long itemId, long state) {
        List<Recipe> ingredients = Recipe.getIngredients(itemId, state);
        for (Recipe ingredient : ingredients) {
            Inventory ownedItems = Inventory.getInventory(ingredient.getIngredient(), ingredient.getIngredientState());
            ownedItems.setQuantity(ownedItems.getQuantity() - ingredient.getQuantity());
            ownedItems.save();
        }
    }

    public static int tryPowderGem(Long itemId, long state, Long locationID) {
        int quantity = Constants.POWDERS_PER_GEM;
        int canCreate = canCreateItem(itemId, state);
        if (canCreate != Constants.SUCCESS) {
            return canCreate;
        }

        removeItemIngredients(itemId, state);

        if (Super_Upgrade.isEnabled(Constants.SU_DOUBLE_ENCHANT_CRAFTS)) {
            quantity = quantity * 2;
        }

        if (Slot.hasAvailableSlot(locationID)) {
            Pending_Inventory.addItem(itemId, state, quantity, locationID);
        } else {
            Pending_Inventory.addScheduledItem(itemId, state, quantity, locationID);
        }

        return Constants.SUCCESS;
    }

    public static int enchantItem(Long itemId, Long gemId, Long locationID) {
        Inventory itemInventory = Inventory.getInventory(itemId, Constants.STATE_NORMAL);
        Inventory gemInventory = Inventory.getInventory(gemId, Constants.STATE_NORMAL);

        if (itemInventory.getQuantity() <= 0) {
            return Constants.ERROR_NO_ITEMS;
        } else if (gemInventory.getQuantity() <= 0) {
            return Constants.ERROR_NO_GEMS;
        } else if (Slot.getUnlockedSlots(locationID) == 0) {
            return Constants.ERROR_NO_SLOTS_ENCHANTING;
        } else {
            itemInventory.setQuantity(itemInventory.getQuantity() - 1);
            itemInventory.save();

            gemInventory.setQuantity(gemInventory.getQuantity() - 1);
            gemInventory.save();

            State enchantedItemState = Select.from(State.class).where(
                    Condition.prop("initiating_item").eq(gemId)).first();

            if (Slot.hasAvailableSlot(locationID)) {
                Pending_Inventory.addItem(itemId, enchantedItemState.getId().intValue(), 1, locationID);
                if (Super_Upgrade.isEnabled(Constants.SU_DOUBLE_ENCHANT_CRAFTS)) {
                    Pending_Inventory.addItem(itemId, enchantedItemState.getId().intValue(), 1, locationID);
                }
            } else {
                Pending_Inventory.addScheduledItem(itemId, enchantedItemState.getId().intValue(), 1, locationID);
                if (Super_Upgrade.isEnabled(Constants.SU_DOUBLE_ENCHANT_CRAFTS)) {
                    Pending_Inventory.addScheduledItem(itemId, enchantedItemState.getId().intValue(), 1, locationID);
                }
            }
            return Constants.SUCCESS;
        }
    }

    public static Inventory getInventory(int id, long state) {
        return getInventory((long) id, state);
    }

    public static Inventory getInventory(Long id, long state) {
        List<Inventory> inventories = Select.from(Inventory.class).where(
                Condition.prop("state").eq(state),
                Condition.prop("item").eq(id)).list();

        // If nothing is returned, return a default count of 0.
        if (inventories.size() > 0) {
            return inventories.get(0);
        } else {
            return new Inventory(id, state, 0);
        }
    }

    public static String exchangePages(Context context, Inventory pageInventory, int quantity) {
        pageInventory.setQuantity(pageInventory.getQuantity() - (quantity * Constants.PAGE_EXCHANGE_QTY));
        pageInventory.save();

        List<Item> pages = Select.from(Item.class).where(
                Condition.prop("type").eq(Constants.TYPE_PAGE),
                Condition.prop("id").notEq(pageInventory.getItem())).list();
        Item rewardPage = VisitorHelper.pickRandomItemFromList(pages);
        Inventory.addItem(rewardPage.getId(), Constants.STATE_NORMAL, quantity);

        return rewardPage.getName(context);
    }

    public static boolean haveSeen(long item, long state) {
        long itemFound = Select.from(Inventory.class).where(
                Condition.prop("state").eq(state),
                Condition.prop("item").eq(item)).count();

        return itemFound > 0L;
    }

    public static int tradeItem(Long itemId, long state, int price) {
        Inventory itemStock = Inventory.getInventory(itemId, state);

        if (Super_Upgrade.isEnabled(Constants.SU_BONUS_GOLD) && Super_Upgrade.isEnabled(Constants.SU_DOUBLE_TRADE_PRICE)) {
            price = price * 4;
        } else if (Super_Upgrade.isEnabled(Constants.SU_BONUS_GOLD) || Super_Upgrade.isEnabled(Constants.SU_DOUBLE_TRADE_PRICE)) {
            price = price * 2;
        }

        int activeAssistant = Select.from(Player_Info.class).where(Condition.prop("name").eq("ActiveAssistant")).first().getIntValue();
        if (activeAssistant > 0) {
            Assistant assistant = Assistant.get(activeAssistant);
            price = (int) Math.floor((double) price * (1 + assistant.getBoost()));
        }

        if (itemStock.isUnsellable()) {
            return Constants.ERROR_UNSELLABLE;
        } else if (itemStock.getQuantity() > 0) {
            itemStock.setQuantity(itemStock.getQuantity() - 1);
            itemStock.save();
            Inventory.addItem(Constants.ITEM_COINS, Constants.STATE_NORMAL, price);

            return Constants.SUCCESS;
        } else {
            return Constants.ERROR_NOT_ENOUGH_ITEMS;
        }
    }

    private static int canBuyItem(Trader_Stock itemStock) {
        Inventory coins = Select.from(Inventory.class).where(
                Condition.prop("item").eq(Constants.ITEM_COINS)).first();

        Item item = Item.findById(Item.class, itemStock.getItemID());

        if ((coins.getQuantity() - item.getModifiedValue(itemStock.getState())) < 0) {
            return Constants.ERROR_NOT_ENOUGH_COINS;
        } else if (itemStock.getStock() <= 0) {
            return Constants.ERROR_TRADER_RUN_OUT;
        } else {
            return Constants.SUCCESS;
        }
    }

    public static void clearDuplicates() {
        Inventory.executeQuery("DELETE FROM inventory WHERE quantity = 0 AND item IN (SELECT i2.item FROM inventory AS i2 GROUP BY i2.item, i2.state HAVING COUNT(*) > 1)");
    }

    public static int buyItem(Trader_Stock itemStock) {
        int canBuyResponse = canBuyItem(itemStock);
        if (canBuyResponse != Constants.SUCCESS) {
            return canBuyResponse;
        } else {
            Item item = Item.findById(Item.class, itemStock.getItemID());

            // Remove coins
            Inventory coinStock = Inventory.getInventory(Constants.ITEM_COINS, Constants.STATE_NORMAL);
            coinStock.setQuantity(coinStock.getQuantity() - item.getModifiedValue(itemStock.getState()));
            coinStock.save();

            // Remove stock
            itemStock.setStock(itemStock.getStock() - 1);
            itemStock.save();

            // Add item
            if (Slot.hasAvailableSlot(Constants.LOCATION_MARKET)) {
                Pending_Inventory.addItem(itemStock.getItemID(), itemStock.getState(), 1, Constants.LOCATION_MARKET);
            } else {
                Pending_Inventory.addScheduledItem(itemStock.getItemID(), itemStock.getState(), 1, Constants.LOCATION_MARKET);
            }

            return Constants.SUCCESS;
        }
    }

    public static int getCoins() {
        Inventory coins = Select.from(Inventory.class).where(
                Condition.prop("state").eq(Constants.STATE_NORMAL),
                Condition.prop("item").eq(Constants.ITEM_COINS)).first();

        if (coins != null) {
            return coins.getQuantity();
        } else {
            return 0;
        }
    }

    public boolean isUnsellable() {
        return unsellable;
    }

    public void setUnsellable(boolean unsellable) {
        this.unsellable = unsellable;
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

    public boolean haveSeen() {
        long itemFound = Select.from(Inventory.class).where(
                Condition.prop("state").eq(state),
                Condition.prop("item").eq(item)).count();

        return itemFound > 0L;
    }
}
