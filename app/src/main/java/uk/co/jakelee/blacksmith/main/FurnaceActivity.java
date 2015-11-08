package uk.co.jakelee.blacksmith.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

        createInterface();
        updateInterface();
    }

    public boolean createItem(int itemId) {
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
            return true;
        } else {
            return false;
        }
    }

    public void createBronzeBar(View view) {
        if (createItem(3)) {
            Toast.makeText(getApplicationContext(), "Bronze bar created", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Not enough materials", Toast.LENGTH_SHORT).show();
        }

        updateInterface();
    }

    public void createInterface() {
        LinearLayout furnaceView = (LinearLayout) findViewById(R.id.furnace);

        ImageView copperOreIcon = new ImageView(this);
        copperOreIcon.setId(R.id.copperOreImage);
        copperOreIcon.setImageResource(R.drawable.copper_ore);
        furnaceView.addView(copperOreIcon);

        TextView copperOreCountLabel = new TextView(this);
        copperOreCountLabel.setId(R.id.copperOreCountLabel);
        copperOreCountLabel.setText(Integer.toString(dbh.getInventoryByItem(1).getQuantity()));
        furnaceView.addView(copperOreCountLabel);

        ImageView tinOreIcon = new ImageView(this);
        tinOreIcon.setId(R.id.tinOreImage);
        tinOreIcon.setImageResource(R.drawable.tin_ore);
        furnaceView.addView(tinOreIcon);

        TextView tinOreCountLabel = new TextView(this);
        tinOreCountLabel.setId(R.id.tinOreCountLabel);
        tinOreCountLabel.setText(Integer.toString(dbh.getInventoryByItem(2).getQuantity()));
        furnaceView.addView(tinOreCountLabel);

        ImageView bronzeBarIcon = new ImageView(this);
        bronzeBarIcon.setId(R.id.bronzeBarImage);
        bronzeBarIcon.setImageResource(R.drawable.bronze_bar);
        furnaceView.addView(bronzeBarIcon);

        TextView bronzeBarCountLabel = new TextView(this);
        bronzeBarCountLabel.setId(R.id.bronzeBarCountLabel);
        bronzeBarCountLabel.setText(Integer.toString(dbh.getInventoryByItem(3).getQuantity()));
        furnaceView.addView(bronzeBarCountLabel);
    }

    public void updateInterface() {
        TextView copperOreCount = (TextView) findViewById(R.id.copperOreCountLabel);
        copperOreCount.setText(Integer.toString(dbh.getInventoryByItem(1).getQuantity()));

        TextView tinOreCount = (TextView) findViewById(R.id.tinOreCountLabel);
        tinOreCount.setText(Integer.toString(dbh.getInventoryByItem(2).getQuantity()));

        TextView bronzeBarCount = (TextView) findViewById(R.id.bronzeBarCountLabel);
        bronzeBarCount.setText(Integer.toString(dbh.getInventoryByItem(3).getQuantity()));
    }
}
