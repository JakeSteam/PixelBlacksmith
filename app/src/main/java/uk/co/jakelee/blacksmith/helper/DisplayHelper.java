package uk.co.jakelee.blacksmith.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import uk.co.jakelee.blacksmith.main.R;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Pending_Inventory;
import uk.co.jakelee.blacksmith.model.Recipe;
import uk.co.jakelee.blacksmith.model.Slots;

public class DisplayHelper {
    private static Context context;
    private static DatabaseHelper dbh;

    public DisplayHelper(Context context) {
        DisplayHelper.context = context;
        DisplayHelper.dbh = new DatabaseHelper(context);
    }

    public void CreateSlotContainer(LinearLayout slotContainer, List<Slots> slots) {
        slotContainer.removeAllViews();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(180, 180);
        int playerLevel = dbh.GetPlayerLevel();

        for (Slots slot : slots) {
            ImageView slotView = new ImageView(context);
            slotView.setLayoutParams(params);

            if (slot.getLevel() > playerLevel) {
                slotView.setBackgroundResource(R.drawable.close);
                slotView.setTag(false);
            } else if (slot.getPremium() == 1) {
                slotView.setBackgroundResource(R.drawable.item52);
                slotView.setTag(false);
            } else {
                slotView.setBackgroundResource(R.drawable.slot);
                slotView.setTag(true);
            }

            slotContainer.addView(slotView);
        }
    }

    public void PopulateSlotContainer(LinearLayout slotContainer, String location) {
        List<Pending_Inventory> pendingItems = dbh.getPendingItemsByLocation(location);

        int i = 0;
        for (Pending_Inventory pendingItem : pendingItems) {
            long itemFinishTime = pendingItem.getTimeCreated() + pendingItem.getCraftTime();
            long currentTime = System.currentTimeMillis();
            int drawableId = context.getResources().getIdentifier("item" + pendingItem.getItem(), "drawable", context.getPackageName());

            if (itemFinishTime <= currentTime) {
                // If the item has finished crafting
                dbh.AddItem(pendingItem.getItem());
                dbh.DeletePendingItem(pendingItem);
            } else {
                // If there's time remaining
                Toast.makeText(context, "Time left: " + (itemFinishTime - currentTime), Toast.LENGTH_SHORT).show();
                ImageView slot = (ImageView) slotContainer.getChildAt(i);
                slot.setImageResource(drawableId);
                i++;
            }
        }
    }

    public void DepopulateSlotContainer(LinearLayout slotContainer) {
        for (int i = 0; i < slotContainer.getChildCount(); i++) {
            ImageView slot = (ImageView) slotContainer.getChildAt(i);
            slot.setImageResource(R.drawable.slot);
        }
    }

    public TextView CreateTextView(String text, int size, int color) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(size);
        textView.setTextColor(color);
        return textView;
    }

    public TextView CreateItemCount(int itemId, int textColour, int backColour) {
        int viewId = context.getResources().getIdentifier("text" + Integer.toString(itemId), "id", context.getPackageName());

        TextView text = new TextView(context);
        text.setId(viewId);
        text.setTag(itemId + "Count");
        text.setTextColor(textColour);
        text.setShadowLayer(5, 0, 0, backColour);
        text.setTextSize(22);
        text.setText(Integer.toString(dbh.getInventoryByItem(itemId).getQuantity()));
        return text;
    }

    public ImageView CreateItemImage(int itemId, int width, int height, String canCraft) {
        int viewId = context.getResources().getIdentifier("img" + Integer.toString(itemId), "id", context.getPackageName());
        int drawableId = context.getResources().getIdentifier("item" + itemId, "drawable", context.getPackageName());

        Bitmap bMap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        Drawable imageResource = new BitmapDrawable(context.getResources(), bMap);

        if (!canCraft.equals("T")) {
            imageResource.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        } else {
            imageResource.clearColorFilter();
        }

        ImageView image = new ImageView(context);
        image.setId(viewId);
        image.setTag(itemId);
        image.setImageDrawable(imageResource);
        image.setMaxWidth(width);
        image.setMinimumWidth(width);
        image.setMaxHeight(height);
        image.setMinimumHeight(height);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);

        return image;
    }

    public void CreateItemIngredientsTable(int itemId, TableLayout ingredientsTable) {
        // Prepare the ingredients table and retrieve the list of ingredients
        List<Recipe> ingredients = dbh.getIngredientsForItemById(itemId);
        ingredientsTable.removeAllViews();

        // Add a header row
        TableRow headerRow = new TableRow(context);
        headerRow.addView(CreateTextView("", 15, Color.DKGRAY));
        headerRow.addView(CreateTextView("", 15, Color.DKGRAY));
        headerRow.addView(CreateTextView("Need", 15, Color.DKGRAY));
        headerRow.addView(CreateTextView("Have", 15, Color.DKGRAY));
        ingredientsTable.addView(headerRow);

        // Add the level requirement row
        TableRow levelRow = new TableRow(context);
        Item item = dbh.getItemById(itemId);
        levelRow.addView(CreateTextView("", 15, Color.DKGRAY));
        levelRow.addView(CreateTextView("Level", 15, Color.DKGRAY));
        levelRow.addView(CreateTextView(Integer.toString(item.getLevel()), 15, Color.DKGRAY));
        levelRow.addView(CreateTextView(Integer.toString(dbh.GetPlayerLevel()), 15, Color.DKGRAY));
        ingredientsTable.addView(levelRow);


        // Add a row for each ingredient
        for (Recipe ingredient : ingredients) {
            Item itemIngredient = dbh.getItemById(ingredient.getIngredient());
            Inventory owned = dbh.getInventoryByItem(ingredient.getIngredient());
            TableRow row = new TableRow(context);

            row.addView(CreateItemImage(ingredient.getIngredient(), 66, 62, "T"));
            row.addView(CreateTextView(itemIngredient.getName(), 15, Color.DKGRAY));
            row.addView(CreateTextView(Integer.toString(ingredient.getQuantity()), 15, Color.DKGRAY));
            row.addView(CreateTextView(Integer.toString(owned.getQuantity()), 15, Color.DKGRAY));

            ingredientsTable.addView(row);
        }
    }


}
