package uk.co.jakelee.blacksmith.sqlite;

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
            Log.i(LOG, statement);
        }
        fileReader.close();
    }

    public Item getItemById(int id) {
        String query = "SELECT * FROM " + TABLE_NAME_ITEM + " WHERE _id = " + id;
        Log.e(LOG, query);

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
        }
        return item;
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
