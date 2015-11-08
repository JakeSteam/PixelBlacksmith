package uk.co.jakelee.blacksmith.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Recipe;
import uk.co.jakelee.blacksmith.sqlite.DatabaseHelper;

public class FurnaceActivity extends AppCompatActivity {
    public static DatabaseHelper dbh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furnace);

        dbh = new DatabaseHelper(getApplicationContext());
        updateInterface();
        createItem(3);
    }

    public void createItem(int itemId) {
        if (dbh.canCreateItem(itemId)) {
            // Remove ingredients
            List<Recipe> ingredients = dbh.getIngredientsForItemById(itemId);
            for (Recipe ingredient : ingredients) {
                Inventory ownedItems = dbh.getInventoryByItem(ingredient.getId());
                ownedItems.setQuantity(ownedItems.getQuantity() - ingredient.getQuantity());
                dbh.updateInventory(ownedItems);
            }

            // Add crafted item
            Inventory craftedItem = dbh.getInventoryByItem(itemId);
            craftedItem.setQuantity(craftedItem.getQuantity() + 1);
            dbh.updateInventory(craftedItem);
        }
    }

    public void createBronzeBar(View view) {
        int copperCount = getIntSetting("copperOreCount", 0);
        int tinCount = getIntSetting("tinOreCount", 0);
        int bronzeCount = getIntSetting("bronzeBarCount", 0);

        if (copperCount >= 1 && tinCount >= 1) {
            copperCount--;
            tinCount--;
            bronzeCount++;

            setIntSetting("copperOreCount", copperCount);
            setIntSetting("tinOreCount", tinCount);
            setIntSetting("bronzeBarCount", bronzeCount);

            Toast.makeText(getApplicationContext(), "Bronze bar created", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Not enough materials", Toast.LENGTH_SHORT).show();
        }

        updateInterface();
    }

    public void updateInterface() {
        TextView copperOreCount = (TextView) findViewById(R.id.copperOreCountLabel);
        copperOreCount.setText(Integer.toString(getIntSetting("copperOreCount", 0)));

        TextView tinOreCount = (TextView) findViewById(R.id.tinOreCountLabel);
        tinOreCount.setText(Integer.toString(getIntSetting("tinOreCount", 0)));

        TextView bronzeBarCount = (TextView) findViewById(R.id.bronzeBarCountLabel);
        bronzeBarCount.setText(Integer.toString(getIntSetting("bronzeBarCount", 0)));
    }

    public int getIntSetting(String variableName, int defaultValue) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getInt(variableName, defaultValue);
    }

    public void setIntSetting(String variableName, int value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(variableName, value);
        editor.commit();
    }
}
