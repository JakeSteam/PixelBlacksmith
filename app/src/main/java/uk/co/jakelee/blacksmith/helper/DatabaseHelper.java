package uk.co.jakelee.blacksmith.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import uk.co.jakelee.blacksmith.model.Item;

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

    public List<Item> getItemsByType(int typeMin, int typeMax) {
        return Item.findWithQuery(Item.class, "SELECT * FROM Item WHERE type BETWEEN " + typeMin + " AND " + typeMax);
    }

    public List<Item> getSmithableItems(int typeMin, int typeMax, int tierMin, int tierMax) {
        return Item.findWithQuery(Item.class, "SELECT * FROM item WHERE type BETWEEN " + typeMin + " AND " + typeMax + " AND tier BETWEEN " + tierMin + " AND " + tierMax + " ORDER BY level ASC");
    }
}
