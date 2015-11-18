package uk.co.jakelee.blacksmith.helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmith.model.Category;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Recipe;
import uk.co.jakelee.blacksmith.model.Tier;
import uk.co.jakelee.blacksmith.model.Type;

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
            // Remove ingredients
            List<Recipe> ingredients = getIngredientsForItemById(itemId);
            for (Recipe ingredient : ingredients) {
                Inventory ownedItems = getInventoryByItem(ingredient.getIngredient());
                ownedItems.setQuantity(ownedItems.getQuantity() - ingredient.getQuantity());
                updateInventory(ownedItems);
            }

            // Add crafted item
            Inventory craftedItem = getInventoryByItem(itemId);
            craftedItem.setQuantity(craftedItem.getQuantity() + 1);
            updateInventory(craftedItem);
            return true;
        } else {
            return false;
        }
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
        return c.getInt(c.getColumnIndex("int_value"));
    }

    public void AddXP() {

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

                items.add(item);
            } while (c.moveToNext());
        }
        return items;
    }

    public List<Item> getItemsByTier(int tierMin, int tierMax) {
        List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM item WHERE tier BETWEEN " + tierMin + " AND " + tierMax;

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

                items.add(item);
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
        if (item.getLevel() > GetPlayerLevel()) {
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

    public void increaseInventoryQuantity(int itemId, int quantity) {
        Inventory inventory = getInventoryByItem(itemId);
        inventory.setQuantity(inventory.getQuantity() + quantity);
        updateInventory(inventory);
    }

    public void updateCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        db.update("category", args, "_id = " + category.getId(), null);
    }

    public void updateInventory(Inventory inventory) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("item", inventory.getItem());
        values.put("quantity", inventory.getQuantity());
        db.insertWithOnConflict("inventory", "item", values, SQLiteDatabase.CONFLICT_REPLACE);
        Log.i(LOG, "Inserted " + inventory.getQuantity() + "x item ID " + inventory.getItem());
    }

    public void updateItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        db.update("item", args, "_id = " + item.getId(), null);
    }

    public void updateRecipe(Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        db.update("recipe", args, "_id = " + recipe.getId(), null);
    }

    public void updateTier(Tier tier) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        db.update("tier", args, "_id = " + tier.getId(), null);
    }

    public void updateType(Type type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        db.update("inventory", args, "_id = " + type.getId(), null);
    }
}
