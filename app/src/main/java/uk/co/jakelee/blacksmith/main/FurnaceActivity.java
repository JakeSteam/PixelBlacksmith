package uk.co.jakelee.blacksmith.main;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
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

        List<Item> ores = dbh.getItemsByType(1);
        for (Item ore : ores) {
            furnaceView.addView(createItemImage(ore.getId()));
            furnaceView.addView(createItemCount(ore.getId()));
        }

        List<Item> bars = dbh.getItemsByType(2);
        for (Item bar : bars) {
            furnaceView.addView(createItemImage(bar.getId()));
            furnaceView.addView(createItemCount(bar.getId()));
        }
    }

    public ImageView createItemImage(int itemId) {
        ImageView image = new ImageView(this);

        // Get the ID of the view we're creating
        int viewId = getResources().getIdentifier("img" + Integer.toString(itemId), "id", getPackageName());

        // Get the ID of the image we want to use.
        int drawableId = getResources().getIdentifier("item" + itemId, "drawable", getPackageName());

        // Get actual image using the ID.
        Drawable imageResource = ResourcesCompat.getDrawable(getResources(), drawableId, null);

        image.setId(viewId);
        image.setImageDrawable(imageResource);
        return image;
    }

    public TextView createItemCount(int itemId) {
        TextView text = new TextView(this);
        int viewId = getResources().getIdentifier("text" + Integer.toString(itemId), "id", getPackageName());

        text.setId(viewId);
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
