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

        furnaceView.addView(createItemCount(1));
        furnaceView.addView(createItemCount(2));
        furnaceView.addView(createItemCount(11));

        furnaceView.addView(createItemImage(1));
        furnaceView.addView(createItemImage(2));
        furnaceView.addView(createItemImage(11));
    }

    public ImageView createItemImage(int itemId) {
        ImageView image = new ImageView(this);
        int viewId = getResources().getIdentifier("img" + Integer.toString(itemId), "id", getPackageName());
        int imageId = getResources().getIdentifier(Integer.toString(itemId), "drawable", getPackageName());

        image.setId(viewId);
        image.setImageResource(imageId);
        return image;
    }

    public TextView createItemCount(int itemId) {
        TextView text = new TextView(this);
        int viewId = getResources().getIdentifier("text" + Integer.toString(itemId), "id", getPackageName());

        text.setId(itemId);
        text.setText(Integer.toString(dbh.getInventoryByItem(itemId).getQuantity()));
        return text;
    }

    public void updateInterface() {
        TextView copperOreCount = (TextView) findViewById(getResources().getIdentifier("text" + Integer.toString(1), "id", getPackageName()));
        copperOreCount.setText(Integer.toString(dbh.getInventoryByItem(1).getQuantity()));

        TextView tinOreCount = (TextView) findViewById(getResources().getIdentifier("text" + Integer.toString(2), "id", getPackageName()));
        tinOreCount.setText(Integer.toString(dbh.getInventoryByItem(2).getQuantity()));

        TextView bronzeBarCount = (TextView) findViewById(getResources().getIdentifier("text" + Integer.toString(11), "id", getPackageName()));
        bronzeBarCount.setText(Integer.toString(dbh.getInventoryByItem(11).getQuantity()));
    }
}
