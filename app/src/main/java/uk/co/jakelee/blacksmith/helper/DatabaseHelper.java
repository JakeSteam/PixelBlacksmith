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
import uk.co.jakelee.blacksmith.model.Shop;
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
        //database = db;
        //try {
        //    insertFromFile(context, "1.sql");
        //} catch (IOException e) {
        //    Log.e(LOG, e.toString());
        //}
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
        Pending_Inventory.deleteAll(Pending_Inventory.class, "SELECT * FROM pending_inventory WHERE item = " + pendingItem.getItem() + " AND time_created = " + pendingItem.getTimeCreated());
        /*SQLiteDatabase db = this.getWritableDatabase();

        String query = "DELETE FROM pending_inventory WHERE item = " + pendingItem.getItem() + " AND time_created = " + pendingItem.getTimeCreated() + ";";
        db.execSQL(query);

        Log.d(LOG, "Deleted " + pendingItem.getItem() + " from pending inventory at location " + pendingItem.getLocationID());*/
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
            updateInventory(ownedItems);
        }
    }

    public void removeItem(Long itemId, int state, int quantity) {
        Inventory itemStock = getInventory(itemId, state);
        itemStock.setQuantity(itemStock.getQuantity() - quantity);
        updateInventory(itemStock);
    }

    public void addPendingItem(Long itemId, int state, int quantity, Long location) {
        Item item = getItem(itemId);
        long time = System.currentTimeMillis();
        int craftTimeMultiplier = 3000;
        int craftTime = item.getValue() * craftTimeMultiplier;
        Pending_Inventory newItem = new Pending_Inventory(itemId, state, time, quantity, craftTime, location);

        /*SQLiteDatabase db = this.getWritableDatabase();

        String query = "INSERT INTO pending_inventory (item, state, time_created, quantity, craft_time, location_id) VALUES (" + itemId + "," + state + "," + time + "," + quantity + "," + craftTime + "," + location + ")";
        db.execSQL(query);*/

        Log.d(LOG, "Added " + itemId + " to pending inventory at location " + location + " at time " + time + " with state " + state);
    }

    public void addItem(Long itemId, int state, int quantity) {
        Inventory craftedItem = getInventory(itemId, state);
        craftedItem.setQuantity(craftedItem.getQuantity() + quantity);
        updateInventory(craftedItem);

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

        /*String query = "SELECT int_value FROM player_info WHERE name = 'XP'";
        int xp;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        Log.d(LOG, "Current XP:" + c.getString(c.getColumnIndex("int_value")));
        xp = c.getInt(c.getColumnIndex("int_value"));
        c.close();

        return xp;*/
    }

    public void addXp(int xp) {
        List<Player_Info> xpInfos = Player_Info.findWithQuery(Player_Info.class, "SELECT * FROM PLAYER_INFO WHERE name = ?", "XP");
        Player_Info xpInfo = xpInfos.get(0);
        xpInfo.setIntValue(xp);
        xpInfo.save();

        /*String query = "UPDATE player_info SET int_value = int_value + " + xp + " WHERE name = 'XP'";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);*/
        Log.d(LOG, "Added XP: " + xp);
    }

    public Item getItem(Long id) {
        return Item.findById(Item.class, id);
        /*
        String query = "SELECT * FROM item WHERE _id = " + id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        Item item = new Item();
        if (c != null) {
            c.moveToFirst();

            item.setId(c.getLong(c.getColumnIndex("_id")));
            item.setName(c.getString(c.getColumnIndex("name")));
            item.setDescription(c.getString(c.getColumnIndex("description")));
            item.setType(c.getInt(c.getColumnIndex("type")));
            item.setTier(c.getInt(c.getColumnIndex("tier")));
            item.setValue(c.getInt(c.getColumnIndex("value")));
            item.setLevel(c.getInt(c.getColumnIndex("level")));
            item.setCanCraft(c.getInt(c.getColumnIndex("can_craft")));
        }
        c.close();
        return item;*/
    }

    public Location getLocation(Long id) {
        return Location.findById(Location.class, id);
        /*String query = "SELECT * FROM locations WHERE _id = " + id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        Location location = new Location();
        if (c != null) {
            c.moveToFirst();

            location.setId(c.getLong(c.getColumnIndex("_id")));
            location.setName(c.getString(c.getColumnIndex("name")));


        }
        c.close();*/
    }

    public List<Item> getItemsByType(int typeMin, int typeMax) {
        return Item.findWithQuery(Item.class, "SELECT * FROM Item WHERE type BETWEEN " + typeMin + " AND " + typeMax);
        /*List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM item WHERE type BETWEEN " + typeMin + " AND " + typeMax;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(c.getLong(c.getColumnIndex("_id")));
                item.setName(c.getString(c.getColumnIndex("name")));
                item.setDescription(c.getString(c.getColumnIndex("description")));
                item.setType(c.getInt(c.getColumnIndex("type")));
                item.setTier(c.getInt(c.getColumnIndex("tier")));
                item.setValue(c.getInt(c.getColumnIndex("value")));
                item.setLevel(c.getInt(c.getColumnIndex("level")));
                item.setCanCraft(c.getInt(c.getColumnIndex("can_craft")));

                items.add(item);
            } while (c.moveToNext());


        }
        c.close();*/
    }

    public List<Item> getSmithableItems(int typeMin, int typeMax, int tierMin, int tierMax) {
        return Item.findWithQuery(Item.class, "SELECT * FROM item WHERE type BETWEEN " + typeMin + " AND " + typeMax + " AND tier BETWEEN " + tierMin + " AND " + tierMax + " ORDER BY level ASC");

        /*List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM item WHERE type BETWEEN " + typeMin + " AND " + typeMax + " AND tier BETWEEN " + tierMin + " AND " + tierMax + " ORDER BY level ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(c.getLong(c.getColumnIndex("_id")));
                item.setName(c.getString(c.getColumnIndex("name")));
                item.setDescription(c.getString(c.getColumnIndex("description")));
                item.setType(c.getInt(c.getColumnIndex("type")));
                item.setTier(c.getInt(c.getColumnIndex("tier")));
                item.setValue(c.getInt(c.getColumnIndex("value")));
                item.setLevel(c.getInt(c.getColumnIndex("level")));
                item.setCanCraft(c.getInt(c.getColumnIndex("can_craft")));

                items.add(item);
            } while (c.moveToNext());


        }
        c.close();
        return items;*/
    }

    public int getCoins() {
        List<Inventory> inventories = Inventory.find(Inventory.class, "state = 1 AND item = 52");
        Inventory inventory = inventories.get(0);
        return inventory.getQuantity();
    }

    public List<Inventory> getAllInventoryItems() {
        return Inventory.find(Inventory.class, "quantity > 0 AND item <> ?", "0", "52");
        /*List<Inventory> items = new ArrayList<>();
        String query = "SELECT * FROM inventory WHERE item <> 52 AND quantity > 0";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            do {
                Inventory inventoryItem = new Inventory();
                inventoryItem.setItem(c.getLong(c.getColumnIndex("item")));
                inventoryItem.setState(c.getInt(c.getColumnIndex("state")));
                inventoryItem.setQuantity(c.getInt(c.getColumnIndex("quantity")));

                items.add(inventoryItem);
            } while (c.moveToNext());


        }
        c.close();*/
    }

    public Inventory getInventory(Long id, int state) {
        List<Inventory> inventories = Inventory.find(Inventory.class, "state = " + state + " AND id = " + id);
        return inventories.get(0);

        /*String query = "SELECT * FROM inventory WHERE item = " + id + " AND state = " + state;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        Inventory inventory = new Inventory();
        inventory.setItem(id);
        inventory.setState(state);
        inventory.setQuantity(0);
        if (c != null && c.getCount() > 0) {
            // It's an existing item.
            c.moveToFirst();
            inventory.setItem(c.getLong(c.getColumnIndex("item")));
            inventory.setState(c.getInt(c.getColumnIndex("state")));
            inventory.setQuantity(c.getInt(c.getColumnIndex("quantity")));
        }
        c.close();*/
    }

    public boolean canCreateItem(Long itemID, int state) {
        // 1: Check we've got a high enough level
        Item item = Item.findById(Item.class, itemID);
        if (item.getLevel() > getPlayerLevel() || item.getCanCraft() != 1) {
            return false;
        }

        // 2: Check we've got enough of all ingredients
        List<Recipe> ingredients = Recipe.findWithQuery(Recipe.class, "SELECT * FROM recipe WHERE state = " + state + " + item = " + itemID);
        for (Recipe recipe : ingredients) {
            List<Inventory> inventories = Inventory.findWithQuery(Inventory.class, "SELECT * FROM inventory WHERE state = " + recipe.getIngredientState() + " AND id = " + recipe.getIngredient());
            Inventory inventory = inventories.get(0);
            if (recipe.getQuantity() > inventory.getQuantity()) {
                return false;
            }
        }

        return true;

        /*SQLiteDatabase db = this.getReadableDatabase();
        // 3: Check inventory has enough of each
        String query = "SELECT item.name, recipe.quantity AS 'recipe', inventory.quantity AS 'inventory'\n" +
                "FROM recipe \n" +
                "INNER JOIN item ON recipe.ingredient = item._id\n" +
                "INNER JOIN inventory ON item._id = inventory.item\n" +
                "WHERE recipe.item = " + itemID + "\n" +
                "AND recipe.item_state = " + state;

        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                int inventoryCount = c.getInt(c.getColumnIndex("inventory"));
                int recipeCount = c.getInt(c.getColumnIndex("recipe"));

                if (recipeCount > inventoryCount) {
                    // Recipe requires more than exists in inventory.
                    c.close();
                    return false;
                }
            } while (c.moveToNext());
            // No problems when looking at all ingredients, we're good to go.
            c.close();

            return true;
        } else {
            // No recipe found, or another error occurred.
            c.close();

            return false;
        }*/
    }

    public List<Recipe> getIngredients(Long id, int state) {
        return Recipe.findWithQuery(Recipe.class, "SELECT * FROM recipe WHERE item = " + id + " AND item_state = " + state);

       /* List<Recipe> ingredients = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT _id, item, item_state, ingredient, ingredient_state, quantity FROM recipe WHERE item = " + id + " AND item_state = " + state;

        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            do {
                Recipe recipe = new Recipe();
                recipe.setId(c.getLong(c.getColumnIndex("_id")));
                recipe.setItem(c.getLong(c.getColumnIndex("item")));
                recipe.setItemState(c.getInt(c.getColumnIndex("item_state")));
                recipe.setIngredient(c.getLong(c.getColumnIndex("ingredient")));
                recipe.setIngredientState(c.getInt(c.getColumnIndex("ingredient_state")));
                recipe.setQuantity(c.getInt(c.getColumnIndex("quantity")));

                ingredients.add(recipe);
            } while (c.moveToNext());


        }
        c.close();*/
    }

    public void updateInventory(Inventory inventory) {
        List<Inventory> inventories = Inventory.findWithQuery(Inventory.class, "SELECT * FROM inventory WHERE state = " + inventory.getState() + " AND id = " + inventory.getItem());
        Inventory foundInventory = inventories.get(0);
        foundInventory.setQuantity(inventory.getQuantity());
        foundInventory.save();

        /*SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT COUNT(*) AS 'exists' FROM inventory WHERE item = " + inventory.getItem() + " AND state = " + inventory.getState();
        String insertQuery = "INSERT INTO inventory (item, state, quantity) VALUES (" + inventory.getItem() + "," + inventory.getState() + "," + inventory.getQuantity() + ")";

        // Check if the item already exists in invent, so needs updating instead of inserting
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            if (c.getInt(c.getColumnIndex("exists")) > 0) {
                insertQuery = "UPDATE inventory SET quantity = " + inventory.getQuantity() + " WHERE item = " + inventory.getItem() + " AND state = " + inventory.getState();
            }
        }
        c.close();

        db.execSQL(insertQuery);*/
        Log.d(LOG, "Inserted " + inventory.getQuantity() + "x item ID " + inventory.getItem());
    }

    public boolean canSellItem(Long itemId, int state, int quantity) {
        List<Inventory> inventories = Inventory.findWithQuery(Inventory.class, "SELECT * FROM inventory WHERE state = " + state + " AND id = " + itemId);
        Inventory foundInventory = inventories.get(0);
        return (foundInventory.getQuantity() - quantity) >= 0;

        /*Inventory inventory = getInventory(itemId, state);
        return (inventory.getQuantity() - quantity) >= 0;*/
    }

    public boolean sellItem(Long itemId, int state, int quantity, int price) {
        Long locationId = 2L;
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

    public List<Shop> getAllDiscoveredShops(int locationID) {

        return Shop.listAll(Shop.class);
        /*List<Shop> shops = new ArrayList<>(); Shop.listAll(Shop.class);

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT _id, shopkeeper, location, name, description, level, discovered FROM shop " +
                "WHERE discovered = 1 AND location = " + locationID;

        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            do {
                Shop shop = new Shop();
                shop.setId(c.getLong(c.getColumnIndex("_id")));
                shop.setName(c.getString(c.getColumnIndex("name")));
                shop.setDescription(c.getString(c.getColumnIndex("description")));
                shop.setDiscovered(c.getInt(c.getColumnIndex("discovered")));
                shop.setLevel(c.getInt(c.getColumnIndex("level")));
                shop.setLocation(c.getInt(c.getColumnIndex("location")));
                shop.setShopkeeper(c.getInt(c.getColumnIndex("shopkeeper")));

                shops.add(shop);
            } while (c.moveToNext());


        }
        c.close();*/
    }

    public void updateCoins(int coins) {
        List<Inventory> inventories = Inventory.findWithQuery(Inventory.class, "SELECT * FROM inventory WHERE item = 52");
        Inventory foundInventory = inventories.get(0);
        foundInventory.setQuantity(coins);
        foundInventory.save();

        /*
        String query = "UPDATE inventory SET quantity = " + coins + " WHERE item = 52";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);*/

        updateCoinsGUI();
    }

    public void updateCoinsGUI() {
        String coinCountString = String.format("%,d", getCoins());
        MainActivity.coins.setText(coinCountString + " coins");
    }

    public List<Pending_Inventory> getPendingItems(String location) {
        return Pending_Inventory.findWithQuery(Pending_Inventory.class, "SELECT item, state, time_created, quantity, craft_time, location_id FROM pendinginventory INNER JOIN location ON pendinginventory.location_id = location.id WHERE location.name = ?", location);

        /*List<Pending_Inventory> items = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT item, state, time_created, quantity, craft_time, location_id " +
                "FROM pending_inventory INNER JOIN locations ON pending_inventory.location_id = locations._id " +
                "WHERE locations.name = '" + location + "'";

        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            do {
                Pending_Inventory item = new Pending_Inventory();
                item.setItem(c.getLong(c.getColumnIndex("item")));
                item.setState(c.getInt(c.getColumnIndex("state")));
                item.setTimeCreated(c.getLong(c.getColumnIndex("time_created")));
                item.setQuantity(c.getInt(c.getColumnIndex("quantity")));
                item.setCraftTime(c.getInt(c.getColumnIndex("craft_time")));
                item.setLocationID(c.getInt(c.getColumnIndex("location_id")));

                items.add(item);
            } while (c.moveToNext());
        }
        c.close();*/
    }

    public boolean hasAvailableSlot(String location) {
        int availableSlots = 0;
        int playerLevel = getPlayerLevel();

        List<Pending_Inventory> pendingItems = getPendingItems(location);
        List<Slots> allSlots = getSlots(location);
        for (Slots slot : allSlots) {
            if (slot.getLevel() <= playerLevel && slot.getPremium() != 1) {
                availableSlots++;
            }
        }

        return (availableSlots > pendingItems.size());
    }

    public List<Slots> getSlots(String location) {
        return Slots.findWithQuery(Slots.class, "SELECT SLOTS.id, SLOTS.location, SLOTS.level, SLOTS.premium FROM SLOTS INNER JOIN LOCATION ON SLOTS.location = LOCATION.id WHERE LOCATION.name = ?", location);
        /*List<Slots> slots = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT slots._id, location_id, level_req, premium " +
                "FROM slots " +
                "INNER JOIN locations ON slots.location_id = locations._id " +
                "WHERE locations.name = '" + location + "'";

        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            do {
                Slots slot = new Slots();
                slot.setId(c.getLong(c.getColumnIndex("_id")));
                slot.setLevel(c.getInt(c.getColumnIndex("level_req")));
                slot.setLocation(c.getInt(c.getColumnIndex("location_id")));
                slot.setPremium(c.getInt(c.getColumnIndex("premium")));

                slots.add(slot);
            } while (c.moveToNext());
        }
        c.close();*/
    }
}
