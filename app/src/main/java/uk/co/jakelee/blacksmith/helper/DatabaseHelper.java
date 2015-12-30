package uk.co.jakelee.blacksmith.helper;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import uk.co.jakelee.blacksmith.main.MainActivity;
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

    public void insertFromFile(Context context, String resourceId) throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream fileContents = assetManager.open(resourceId);
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileContents));

        while (fileReader.ready()) {
            String statement = fileReader.readLine();
            database.execSQL(statement);
        }
        fileReader.close();
        fileContents.close();
    }

    public void deletePendingItem(Pending_Inventory pendingItem) {
        Pending_Inventory.deleteAll(Pending_Inventory.class, "item = " + pendingItem.getItem() + " AND time_created = " + pendingItem.getTimeCreated());
    }

    public boolean createItem(Long itemId, int state, int quantity, Long locationId) {
        Location location = getLocation(locationId);
        if (canCreateItem(itemId, state) && hasAvailableSlot(location.getName())) {
            removeItemIngredients(itemId, state);
            addPendingItem(itemId, state, quantity, locationId);
            return true;
        } else {
            return false;
        }
    }

    public void removeItemIngredients(Long itemId, int state) {
        List<Recipe> ingredients = getIngredients(itemId, state);
        for (Recipe ingredient : ingredients) {
            Inventory ownedItems = getInventory(ingredient.getIngredient(), ingredient.getIngredientState());
            ownedItems.setQuantity(ownedItems.getQuantity() - ingredient.getQuantity());
            ownedItems.save();
        }
    }

    public void removeItem(Long itemId, int state, int quantity) {
        Inventory itemStock = getInventory(itemId, state);
        itemStock.setQuantity(itemStock.getQuantity() - quantity);
        itemStock.save();
    }

    public void addPendingItem(Long itemId, int state, int quantity, Long location) {
        Item item = getItem(itemId);
        long time = System.currentTimeMillis();
        int craftTimeMultiplier = 3000;
        int craftTime = item.getValue() * craftTimeMultiplier;
        Pending_Inventory newItem = new Pending_Inventory(itemId, state, time, quantity, craftTime, location);
        newItem.save();

        Log.d(LOG, "Added " + itemId + " to pending inventory at location " + location + " at time " + time + " with state " + state);
    }

    public void addItem(Long itemId, int state, int quantity) {
        Inventory craftedItem = getInventory(itemId, state);
        craftedItem.setQuantity(craftedItem.getQuantity() + quantity);
        craftedItem.save();

        addXp(getItem(craftedItem.getItem()).getValue());
        updateLevelText();
    }

    public void updateLevelText() {
        TextView levelCount = MainActivity.level;
        levelCount.setText("Level" + getPlayerLevel() + " (" + getXp() + "xp)");
    }

    public int getPlayerLevel() {
        int xp = getXp();
        return convertXpToLevel(xp);
    }

    public int convertXpToLevel(int xp) {
        return xp / 100;
    }

    public int getXp() {
        List<Player_Info> xpInfos = Player_Info.find(Player_Info.class, "name = ?", "XP");
        Player_Info xpInfo = xpInfos.get(0);

        return xpInfo.getIntValue();
    }

    public void addXp(int xp) {
        List<Player_Info> xpInfos = Player_Info.find(Player_Info.class, "name = ?", "XP");
        Player_Info xpInfo = xpInfos.get(0);
        xpInfo.setIntValue(xpInfo.getIntValue() + xp);
        xpInfo.save();

        Log.d(LOG, "Added XP: " + xp);
    }

    public Item getItem(Long id) {
        return Item.findById(Item.class, id);
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

    public int getCoins() {
        List<Inventory> inventories = Inventory.find(Inventory.class, "STATE = 1 AND ITEM = 52");
        Inventory inventory = inventories.get(0);
        return inventory.getQuantity();
    }

    public List<Inventory> getAllInventoryItems() {
        return Inventory.find(Inventory.class, "quantity > 0 AND item <> ?", "52");
    }

    public Inventory getInventory(Long id, int state) {
        List<Inventory> inventories = Inventory.find(Inventory.class, "state = " + state + " AND item = " + id);

        // If nothing is returned, return a default count of 0.
        if (inventories.size() > 0) {
            return inventories.get(0);
        } else {
            return new Inventory(id, state, 0);
        }
    }

    public boolean canCreateItem(Long itemID, int state) {
        // 1: Check we've got a high enough level
        Item item = Item.findById(Item.class, itemID);
        if (item.getLevel() > getPlayerLevel() || item.getCanCraft() != 1) {
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

    public List<Recipe> getIngredients(Long id, int state) {
        return Recipe.findWithQuery(Recipe.class, "SELECT * FROM recipe WHERE item = " + id + " AND item_state = " + state);
    }

    public void updateInventory(Inventory inventory) {
        List<Inventory> inventories = Inventory.find(Inventory.class, "state = " + inventory.getState() + " AND id = " + inventory.getItem());

        Inventory foundInventory;
        if (inventories.size() > 0) {
            foundInventory = inventories.get(0);
        } else {
            foundInventory = new Inventory(inventory.getItem(), inventory.getState(), 0);
        }

        foundInventory.setQuantity(inventory.getQuantity());
        foundInventory.save();

        Log.d(LOG, "Inserted " + inventory.getQuantity() + "x item ID " + inventory.getItem());
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

        if (canSellItem(itemId, state, quantity) && hasAvailableSlot(locationName)) {
            removeItem(itemId, state, quantity);
            addPendingItem(coinId, 1, price, locationId);
            return true;
        } else {
            return false;
        }
    }

    //public List<Shop> getAllDiscoveredShops(int locationID) {
    //return Shop.listAll(Shop.class);
    //}

    public void updateCoins(int coins) {
        List<Inventory> inventories = Inventory.findWithQuery(Inventory.class, "SELECT * FROM inventory WHERE item = 52");
        Inventory foundInventory = inventories.get(0);
        foundInventory.setQuantity(coins);
        foundInventory.save();

        updateCoinsGUI();
    }

    public void updateCoinsGUI() {
        String coinCountString = String.format("%,d", getCoins());
        MainActivity.coins.setText(coinCountString + " coins");
    }

    public List<Pending_Inventory> getPendingItems(String location) {
        List<Location> locations = Location.find(Location.class, "name = ?", location);
        Location itemLocation = locations.get(0);
        return Pending_Inventory.getPendingItems(itemLocation.getId());
    }

    public boolean hasAvailableSlot(String location) {
        int availableSlots = 0;
        int playerLevel = getPlayerLevel();

        List<Pending_Inventory> pendingItems = getPendingItems(location);
        List<Slots> allSlots = Location.getSlots(location);
        for (Slots slot : allSlots) {
            if (slot.getLevel() <= playerLevel && slot.getPremium() != 1) {
                availableSlots++;
            }
        }

        return (availableSlots > pendingItems.size());
    }

    //public List<Slots> getSlots(String location) {
    //    return Slots.findWithQuery(Slots.class, "SELECT SLOTS.id, SLOTS.location, SLOTS.level, SLOTS.premium FROM SLOTS INNER JOIN LOCATION ON SLOTS.location = LOCATION.id WHERE LOCATION.name = ?", location);
    //}
}
