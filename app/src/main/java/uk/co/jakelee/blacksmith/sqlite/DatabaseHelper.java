package uk.co.jakelee.blacksmith.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import uk.co.jakelee.blacksmith.model.Item;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String LOG = "DatabaseHelper";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "blacksmith";

    private static final String TABLE_NAME_CATEGORY = "category";
    private static final String TABLE_NAME_ITEM = "item";
    private static final String TABLE_NAME_RECIPE = "recipe";
    private static final String TABLE_NAME_TIER = "tier";
    private static final String TABLE_NAME_TYPE = "type";


    private static final String CREATE_TABLE_CATEGORY = "CREATE TABLE " + TABLE_NAME_CATEGORY + " ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "name TEXT NOT NULL,"
            + "description TEXT DEFAULT '')";

    private static final String CREATE_TABLE_ITEM = "CREATE TABLE " + TABLE_NAME_ITEM + " ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "name TEXT NOT NULL,"
            + "description TEXT NOT NULL,"
            + "type INTEGER NOT NULL,"
            + "tier INTEGER NOT NULL,"
            + "value INTEGER NOT NULL DEFAULT 0)";

    private static final String CREATE_TABLE_RECIPE = "CREATE TABLE " + TABLE_NAME_RECIPE + " ("
            + "item INTEGER NOT NULL,"
            + "ingredient INTEGER NOT NULL,"
            + "quantity INTEGER NOT NULL DEFAULT 1,"
            + "required INTEGER NOT NULL DEFAULT 1)";

    private static final String CREATE_TABLE_TIER = "CREATE TABLE " + TABLE_NAME_TIER + " ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "name TEXT NOT NULL)";

    private static final String CREATE_TABLE_TYPE = "CREATE TABLE " + TABLE_NAME_TYPE + " ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "name TEXT NOT NULL,"
            + "category INTEGER NOT NULL DEFAULT 0)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CATEGORY);
        db.execSQL(CREATE_TABLE_ITEM);
        db.execSQL(CREATE_TABLE_RECIPE);
        db.execSQL(CREATE_TABLE_TIER);
        db.execSQL(CREATE_TABLE_TYPE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ITEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_RECIPE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TIER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TYPE);

        onCreate(db);
    }

    public Item getItemById(int id) {
        String query = "SELECT * FROM " + TABLE_NAME_ITEM + " WHERE id = " + id;
        Log.e(LOG, query);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        Item item = new Item();
        if (c != null) {
            c.moveToFirst();

            item.setId(c.getInt(c.getColumnIndex("id")));
            item.setName(c.getString(c.getColumnIndex("name")));
            item.setDescription(c.getString(c.getColumnIndex("description")));
            item.setType(c.getInt(c.getColumnIndex("type")));
            item.setTier(c.getInt(c.getColumnIndex("tier")));
            item.setValue(c.getInt(c.getColumnIndex("value")));
        }
        return item;
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
