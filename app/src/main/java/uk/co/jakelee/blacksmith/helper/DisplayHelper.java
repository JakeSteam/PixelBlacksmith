package uk.co.jakelee.blacksmith.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Recipe;

public class DisplayHelper {
    private static Context context;
    private static DatabaseHelper dbh;

    public DisplayHelper(Context context) {
        DisplayHelper.context = context;
        DisplayHelper.dbh = new DatabaseHelper(context);
    }

    public TextView CreateTextView(String text) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(20);
        return textView;
    }

    public TextView CreateItemCount(int itemId, String prefix, String suffix, int textColour, int backColour) {
        int viewId = context.getResources().getIdentifier("text" + Integer.toString(itemId), "id", context.getPackageName());

        TextView text = new TextView(context);
        text.setId(viewId);
        text.setTag(itemId + "Count");
        text.setBackgroundColor(backColour);
        text.setTextColor(textColour);
        text.setText(prefix + Integer.toString(dbh.getInventoryByItem(itemId).getQuantity()) + suffix);
        return text;
    }

    public ImageView CreateItemImage(int itemId, int width, int height) {
        int viewId = context.getResources().getIdentifier("img" + Integer.toString(itemId), "id", context.getPackageName());
        int drawableId = context.getResources().getIdentifier("item" + itemId, "drawable", context.getPackageName());

        Bitmap bMap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        bMap = Bitmap.createScaledBitmap(bMap, width, height, true);
        Drawable imageResource = new BitmapDrawable(context.getResources(), bMap);

        ImageView image = new ImageView(context);
        image.setId(viewId);
        image.setTag(itemId);
        image.setImageDrawable(imageResource);
        /*image.setOnClickListener(new View.OnClickListener() {
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
        });*/
        return image;
    }

    public void CreateItemIngredientsTable(int itemId, TableLayout ingredientsTable) {
        // Prepare the ingredients table and retrieve the list of ingredients
        List<Recipe> ingredients = dbh.getIngredientsForItemById(itemId);
        ingredientsTable.removeAllViews();

        // Add a header row
        TableRow headerRow = new TableRow(context);
        headerRow.addView(CreateTextView(""));
        headerRow.addView(CreateTextView(""));
        headerRow.addView(CreateTextView("Need"));
        headerRow.addView(CreateTextView("Have"));
        ingredientsTable.addView(headerRow);

        // Add a row for each ingredient
        for (Recipe ingredient : ingredients) {
            Item item = dbh.getItemById(ingredient.getIngredient());
            Inventory owned = dbh.getInventoryByItem(ingredient.getIngredient());
            TableRow row = new TableRow(context);

            row.addView(CreateItemImage(ingredient.getIngredient(), 66, 62));
            row.addView(CreateTextView(item.getName()));
            row.addView(CreateTextView(Integer.toString(ingredient.getQuantity())));
            row.addView(CreateTextView(Integer.toString(owned.getQuantity())));

            ingredientsTable.addView(row);
        }
    }
}
