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

        createFurnaceInterface();
    }

    public boolean createItem(int itemId) {
        if (dbh.canCreateItem(itemId)) {
            // Remove ingredients
            List<Recipe> ingredients = dbh.getIngredientsForItemById(itemId);
            for (Recipe ingredient : ingredients) {
                Inventory ownedItems = dbh.getInventoryByItem(ingredient.getIngredient());
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

    public void createFurnaceInterface() {
        LinearLayout furnaceView = (LinearLayout) findViewById(R.id.furnace);
        LinearLayout barSelector = (LinearLayout) findViewById(R.id.barSelector);

        // Add all ores to a row.
        List<Item> ores = dbh.getItemsByType(1);
        LinearLayout oreLayout = new LinearLayout(this);
        for (Item ore : ores) {
            oreLayout.addView(createItemImage(ore.getId()));
            oreLayout.addView(createItemCount(ore.getId()));
        }

        // Add all bars to a row.
        List<Item> bars = dbh.getItemsByType(2);
        LinearLayout barLayout = new LinearLayout(this);
        for (Item bar : bars) {
            barLayout.addView(createItemImage(bar.getId()));
            barLayout.addView(createItemCount(bar.getId()));
            barSelector.addView(createItemImage(bar.getId()));
        }

        furnaceView.addView(oreLayout);
        furnaceView.addView(barLayout);
    }

    public ImageView createItemImage(int itemId) {
        int viewId = getResources().getIdentifier("img" + Integer.toString(itemId), "id", getPackageName());
        int drawableId = getResources().getIdentifier("item" + itemId, "drawable", getPackageName());
        Drawable imageResource = ResourcesCompat.getDrawable(getResources(), drawableId, null);

        ImageView image = new ImageView(this);
        image.setId(viewId);
        image.setTag(itemId);
        image.setImageDrawable(imageResource);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemId = (int) v.getTag();

                Item item = dbh.getItemById(itemId);
                if (createItem(itemId)) {
                    Toast.makeText(getApplicationContext(), item.getName() + " created", Toast.LENGTH_SHORT).show();
                    updateItemViewById(itemId, true);
                } else {
                    Toast.makeText(getApplicationContext(), "Not enough materials", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return image;
    }

    public void updateItemViewById(int itemId, boolean updateIngredients) {
        int itemCountId = getResources().getIdentifier("text" + itemId, "id", getPackageName());
        TextView itemCount = (TextView) findViewById(itemCountId);

        // Update the item's count
        itemCount.setText(Integer.toString(dbh.getInventoryByItem(itemId).getQuantity()));

        if (updateIngredients) {
            List<Recipe> ingredients = dbh.getIngredientsForItemById(itemId);
            for (Recipe ingredient : ingredients) {
                int ingredientCountId = getResources().getIdentifier("text" + ingredient.getIngredient(), "id", getPackageName());
                TextView ingredientCount = (TextView) findViewById(ingredientCountId);

                // Update the ingredient's count
                ingredientCount.setText(Integer.toString(dbh.getInventoryByItem(ingredient.getIngredient()).getQuantity()));
            }
        }
    }

    public TextView createItemCount(int itemId) {
        int viewId = getResources().getIdentifier("text" + Integer.toString(itemId), "id", getPackageName());

        TextView text = new TextView(this);
        text.setId(viewId);
        text.setText(Integer.toString(dbh.getInventoryByItem(itemId).getQuantity()));
        return text;
    }
}
