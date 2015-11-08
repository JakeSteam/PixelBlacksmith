package uk.co.jakelee.blacksmith.main;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
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

    public void createInterface() {
        TableLayout furnaceView = (TableLayout) findViewById(R.id.furnace);
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);

        // Add all ores to a row.
        List<Item> ores = dbh.getItemsByType(1);
        TableRow oreRow = new TableRow(this);
        oreRow.setLayoutParams(params);
        for (Item ore : ores) {
            oreRow.addView(createItemImage(ore.getId()));
            oreRow.addView(createItemCount(ore.getId()));
        }

        // Add all bars to a row.
        List<Item> bars = dbh.getItemsByType(2);
        TableRow barRow = new TableRow(this);
        barRow.setLayoutParams(params);
        for (Item bar : bars) {
            barRow.addView(createItemImage(bar.getId()));
            barRow.addView(createItemCount(bar.getId()));
        }

        furnaceView.addView(oreRow);
        furnaceView.addView(barRow);
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
                int itemCountId = getResources().getIdentifier("text" + itemId, "id", getPackageName());

                TextView itemCount = (TextView) findViewById(itemCountId);
                Item item = dbh.getItemById(itemId);
                if (createItem(itemId)) {
                    itemCount.setText(Integer.parseInt(itemCount.getText().toString()) + 1);
                    Toast.makeText(getApplicationContext(), item.getName() + " created", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Not enough materials", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return image;
    }

    public TextView createItemCount(int itemId) {
        int viewId = getResources().getIdentifier("text" + Integer.toString(itemId), "id", getPackageName());

        TextView text = new TextView(this);
        text.setId(viewId);
        text.setText(Integer.toString(dbh.getInventoryByItem(itemId).getQuantity()));
        return text;
    }

    public void updateInterface() {

    }
}
