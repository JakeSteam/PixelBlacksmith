package uk.co.jakelee.blacksmith.helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmith.main.MainActivity;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Pending_Inventory;
import uk.co.jakelee.blacksmith.model.Recipe;
import uk.co.jakelee.blacksmith.model.Slots;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "blacksmith";

    private static Context context;
    private static SQLiteDatabase database;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        DatabaseHelper.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        database = db;
        try {
            insertFromFile(context, "databaseSetup");
        } catch (IOException e) {
            Log.e(LOG, e.toString());
        }
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
    }

    public boolean createItem(int itemId) {
        if (canCreateItem(itemId)) {
            AddPendingItem(itemId);
            return true;
        } else {
            return false;
        }
    }

    public void AddPendingItem(int itemId) {
        List<Recipe> ingredients = getIngredientsForItemById(itemId);
        for (Recipe ingredient : ingredients) {
            Inventory ownedItems = getInventoryByItem(ingredient.getIngredient());
            ownedItems.setQuantity(ownedItems.getQuantity() - ingredient.getQuantity());
            updateInventory(ownedItems);
        }

        // Add to pending inventory
    }

    public void AddItem(int itemId) {
        Inventory craftedItem = getInventoryByItem(itemId);
        craftedItem.setQuantity(craftedItem.getQuantity() + 1);
        updateInventory(craftedItem);

        AddXP(getItemById(craftedItem.getItem()).getValue());
        UpdateLevelText();
    }

    public void UpdateLevelText() {
        TextView levelCount = MainActivity.level;
        levelCount.setText("Level" + GetPlayerLevel() + " (" + GetXP() + "xp)");
    }

    public int GetPlayerLevel() {
        int xp = GetXP();
        return xp / 100;
    }

    public int GetXP() {
        String query = "SELECT int_value FROM player_info WHERE name = 'XP'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        Log.d(LOG, "Current XP:" + c.getString(c.getColumnIndex("int_value")));
        return c.getInt(c.getColumnIndex("int_value"));
    }

    public void AddXP(int xp) {
        String query = "UPDATE player_info SET int_value = int_value + " + xp + " WHERE name = 'XP'";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
        Log.d(LOG, "Added XP: " + xp);
    }

    public Item getItemById(int id) {
        String query = "SELECT * FROM item WHERE _id = " + id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        Item item = new Item();
        if (c != null) {
            c.moveToFirst();

            item.setId(c.getInt(c.getColumnIndex("_id")));
            item.setName(c.getString(c.getColumnIndex("name")));
            item.setDescription(c.getString(c.getColumnIndex("description")));
            item.setType(c.getInt(c.getColumnIndex("type")));
            item.setTier(c.getInt(c.getColumnIndex("tier")));
            item.setValue(c.getInt(c.getColumnIndex("value")));
            item.setLevel(c.getInt(c.getColumnIndex("level")));
            item.setCanCraft(c.getString(c.getColumnIndex("can_craft")));
        }
        return item;
    }

    public List<Item> getItemsByType(int typeMin, int typeMax) {
        List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM item WHERE type BETWEEN " + typeMin + " AND " + typeMax;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(c.getInt(c.getColumnIndex("_id")));
                item.setName(c.getString(c.getColumnIndex("name")));
                item.setDescription(c.getString(c.getColumnIndex("description")));
                item.setType(c.getInt(c.getColumnIndex("type")));
                item.setTier(c.getInt(c.getColumnIndex("tier")));
                item.setValue(c.getInt(c.getColumnIndex("value")));
                item.setLevel(c.getInt(c.getColumnIndex("level")));
                item.setCanCraft(c.getString(c.getColumnIndex("can_craft")));

                items.add(item);
            } while (c.moveToNext());
        }
        return items;
    }

    public List<Item> getItemsByTypeAndTier(int typeMin, int typeMax, int tierMin, int tierMax) {
        List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM item WHERE type BETWEEN " + typeMin + " AND " + typeMax + " AND tier BETWEEN " + tierMin + " AND " + tierMax;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(c.getInt(c.getColumnIndex("_id")));
                item.setName(c.getString(c.getColumnIndex("name")));
                item.setDescription(c.getString(c.getColumnIndex("description")));
                item.setType(c.getInt(c.getColumnIndex("type")));
                item.setTier(c.getInt(c.getColumnIndex("tier")));
                item.setValue(c.getInt(c.getColumnIndex("value")));
                item.setLevel(c.getInt(c.getColumnIndex("level")));
                item.setCanCraft(c.getString(c.getColumnIndex("can_craft")));

                items.add(item);
            } while (c.moveToNext());
        }
        return items;
    }

    public int getCoins() {
        Inventory coins = getInventoryByItem(52);
        return coins.getQuantity();
    }

    public List<Inventory> getAllInventoryItems() {
        List<Inventory> items = new ArrayList<>();
        String query = "SELECT * FROM inventory WHERE item <> 52 AND quantity > 0";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        if (c != null && c.moveToFirst()) {
            do {
                Inventory inventoryItem = new Inventory();
                inventoryItem.setItem(c.getInt(c.getColumnIndex("item")));
                inventoryItem.setQuantity(c.getInt(c.getColumnIndex("quantity")));

                items.add(inventoryItem);
            } while (c.moveToNext());
        }
        return items;
    }

    public Inventory getInventoryByItem(int id) {
        String query = "SELECT * FROM inventory WHERE item = " + id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        Inventory inventory = new Inventory();
        if (c != null && c.getCount() > 0) {
            // It's an existing item.
            c.moveToFirst();
            inventory.setItem(c.getInt(c.getColumnIndex("item")));
            inventory.setQuantity(c.getInt(c.getColumnIndex("quantity")));
        } else {
            // It's a new item.
            inventory.setItem(id);
            inventory.setQuantity(0);
        }
        return inventory;
    }

    public boolean canCreateItem(int itemID) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Check we've got a high enough level
        Item item = getItemById(itemID);
        if (item.getLevel() > GetPlayerLevel() || !item.getCanCraft().equals("T")) {
            return false;
        }

        String query = "SELECT item.name, recipe.quantity AS 'recipe', inventory.quantity AS 'inventory'\n" +
                "FROM recipe \n" +
                "INNER JOIN item ON recipe.ingredient = item._id\n" +
                "INNER JOIN inventory ON item._id = inventory.item\n" +
                "WHERE recipe.item = " + itemID;

        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                int inventoryCount = c.getInt(c.getColumnIndex("inventory"));
                int recipeCount = c.getInt(c.getColumnIndex("recipe"));

                if (recipeCount > inventoryCount) {
                    // Recipe requires more than exists in inventory.
                    return false;
                }
            } while (c.moveToNext());
            // No problems when looking at all ingredients, we're good to go.
            return true;
        } else {
            // No recipe found, or another error occurred.
            return false;
        }
    }

    public List<Recipe> getIngredientsForItemById(int id) {
        List<Recipe> ingredients = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT _id, item, ingredient, quantity FROM recipe WHERE item = " + id;

        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            do {
                Recipe recipe = new Recipe();
                recipe.setId(c.getInt(c.getColumnIndex("_id")));
                recipe.setItem(c.getInt(c.getColumnIndex("item")));
                recipe.setIngredient(c.getInt(c.getColumnIndex("ingredient")));
                recipe.setQuantity(c.getInt(c.getColumnIndex("quantity")));

                ingredients.add(recipe);
            } while (c.moveToNext());
        }

        return ingredients;
    }

    public void updateInventory(Inventory inventory) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("item", inventory.getItem());
        values.put("quantity", inventory.getQuantity());
        db.insertWithOnConflict("inventory", "item", values, SQLiteDatabase.CONFLICT_REPLACE);
        Log.d(LOG, "Inserted " + inventory.getQuantity() + "x item ID " + inventory.getItem());
    }

    public boolean canSellItem(int itemId, int quantity) {
        Inventory inventory = getInventoryByItem(itemId);
        return inventory.getQuantity() > 0;
    }

    public void sellItem(int itemId, int quantity, int price) {
        Inventory inventory = getInventoryByItem(itemId);
        inventory.setQuantity(inventory.getQuantity() - quantity);

        updateInventory(inventory);
        updateCoins(getCoins() + (quantity * price));
    }

    public void updateCoins(int coins) {
        String query = "UPDATE inventory SET quantity = " + coins + " WHERE item = 52";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);

        updateCoinsGUI();
    }

    public void updateCoinsGUI() {
        String coinCountString = String.format("%,d", getCoins());
        MainActivity.coins.setText(coinCountString + " coins");
    }

    public List<Pending_Inventory> getPendingItemsByLocation(String location) {
        List<Pending_Inventory> items = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT item, time_created, craft_time, location_id " +
                "FROM pending_inventory INNER JOIN locations ON pending_inventory.location_id = locations._id " +
                "WHERE locations._id = '" + location + "'";

        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            do {
                Pending_Inventory item = new Pending_Inventory();
                item.setItem(c.getInt(c.getColumnIndex("item")));
                item.setTimeCreated(c.getInt(c.getColumnIndex("time_created")));
                item.setCraftTime(c.getInt(c.getColumnIndex("craft_time")));
                item.setLocationID(c.getInt(c.getColumnIndex("location_id")));

                items.add(item);
            } while (c.moveToNext());
        }

        return items;
    }

    public List<Slots> getSlots(String location) {
        List<Slots> slots = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT slots._id, location_id, level_req, premium " +
                "FROM slots " +
                "INNER JOIN locations ON slots.location_id = locations._id " +
                "WHERE locations.name = '" + location + "'";

        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            do {
                Slots slot = new Slots();
                slot.setId(c.getInt(c.getColumnIndex("_id")));
                slot.setLevel(c.getInt(c.getColumnIndex("level_req")));
                slot.setLocation(c.getInt(c.getColumnIndex("location_id")));
                slot.setPremium(c.getInt(c.getColumnIndex("premium")));

                slots.add(slot);
            } while (c.moveToNext());
        }

        return slots;
    }
}
