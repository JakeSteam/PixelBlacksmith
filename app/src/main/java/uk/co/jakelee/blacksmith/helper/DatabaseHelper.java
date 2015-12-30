package uk.co.jakelee.blacksmith.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Location;
import uk.co.jakelee.blacksmith.model.Pending_Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Recipe;
import uk.co.jakelee.blacksmith.model.Slots;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String LOG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "blacksmith";
    private static DatabaseHelper dbhInstance = null;
    private static Context context;
    private static SQLiteDatabase database;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        DatabaseHelper.context = context;
    }

    public static DatabaseHelper getInstance(Context ctx) {
        if (dbhInstance == null) {
            dbhInstance = new DatabaseHelper(ctx.getApplicationContext());
        }
        return dbhInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean createItem(Long itemId, int state, int quantity, Long locationId) {
        Location location = getLocation(locationId);
        if (canCreateItem(itemId, state) && Slots.hasAvailableSlot(location.getName())) {
            removeItemIngredients(itemId, state);
            Pending_Inventory.addItem(itemId, state, quantity, locationId);
            return true;
        } else {
            return false;
        }
    }

    public void removeItemIngredients(Long itemId, int state) {
        List<Recipe> ingredients = Recipe.getIngredients(itemId, state);
        for (Recipe ingredient : ingredients) {
            Inventory ownedItems = Inventory.getInventory(ingredient.getIngredient(), ingredient.getIngredientState());
            ownedItems.setQuantity(ownedItems.getQuantity() - ingredient.getQuantity());
            ownedItems.save();
        }
    }

    public Location getLocation(Long id) {
        List<Location> locations = Location.find(Location.class, "id = " + id);
        return locations.get(0);
    }

    public List<Item> getItemsByType(int typeMin, int typeMax) {
        return Item.findWithQuery(Item.class, "SELECT * FROM Item WHERE type BETWEEN " + typeMin + " AND " + typeMax);
    }

    public List<Item> getSmithableItems(int typeMin, int typeMax, int tierMin, int tierMax) {
        return Item.findWithQuery(Item.class, "SELECT * FROM item WHERE type BETWEEN " + typeMin + " AND " + typeMax + " AND tier BETWEEN " + tierMin + " AND " + tierMax + " ORDER BY level ASC");
    }

    public boolean canCreateItem(Long itemID, int state) {
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

    public boolean canSellItem(Long itemId, int state, int quantity) {
        List<Inventory> inventories = Inventory.findWithQuery(Inventory.class, "SELECT * FROM inventory WHERE state = " + state + " AND id = " + itemId);

        Inventory foundInventory;
        if (inventories.size() > 0) {
            foundInventory = inventories.get(0);
        } else {
            foundInventory = new Inventory(itemId, state, 0);
        }

        return (foundInventory.getQuantity() - quantity) >= 0;
    }

    public boolean sellItem(Long itemId, int state, int quantity, int price) {
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
}
