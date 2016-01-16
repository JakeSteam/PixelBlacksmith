package uk.co.jakelee.blacksmith.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.main.MainActivity;
import uk.co.jakelee.blacksmith.main.VisitorActivity;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Pending_Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Recipe;
import uk.co.jakelee.blacksmith.model.Slot;
import uk.co.jakelee.blacksmith.model.Visitor;

public class DisplayHelper {
    private static DisplayHelper dhInstance = null;
    private static Context context;
    public final static String VISITOR_TO_LOAD = "uk.co.jakelee.blacksmith.visitortoload";
    public final static String DEMAND_TO_LOAD = "uk.co.jakelee.blacksmith.tradetoload";

    public DisplayHelper(Context context) {
        DisplayHelper.context = context;
    }

    public static DisplayHelper getInstance(Context ctx) {
        if (dhInstance == null) {
            dhInstance = new DisplayHelper(ctx.getApplicationContext());
        }
        return dhInstance;
    }

    public void createSlotContainer(RelativeLayout slotContainer, List<Slot> slots) {
        // Basic setting up
        slotContainer.removeAllViews();
        int playerLevel = Player_Info.getPlayerLevel();
        LinearLayout.LayoutParams slotParams = new LinearLayout.LayoutParams(180, 180);

        // Creating the 3 layouts
        LinearLayout backContainer = new LinearLayout(context);
        LinearLayout frontContainer = new LinearLayout(context);
        LinearLayout countContainer = new LinearLayout(context);

        // Foreach slot, create the background image and set usable tag
        // Also set text for now.
        for (Slot slot : slots) {
            ImageView slotBackground = new ImageView(context);
            slotBackground.setLayoutParams(slotParams);
            slotBackground.setAlpha(0.6F);

            ImageView slotForeground = new ImageView(context);
            slotForeground.setLayoutParams(slotParams);
            slotForeground.setImageResource(R.drawable.transparent);

            TextView slotCountdown = new TextView(context);
            slotCountdown.setText("");
            slotCountdown.setTextSize(36);
            slotCountdown.setTextColor(Color.WHITE);
            slotCountdown.setShadowLayer(5, 0, 0, Color.BLACK);
            slotCountdown.setLayoutParams(slotParams);

            if (slot.getLevel() > playerLevel) {
                slotBackground.setBackgroundResource(R.drawable.close);
                slotBackground.setTag(false);
            } else if (slot.getPremium() == 1) {
                slotBackground.setBackgroundResource(R.drawable.item52);
                slotBackground.setTag(false);
            } else {
                slotBackground.setBackgroundResource(R.drawable.slot);
                slotBackground.setTag(true);
            }

            backContainer.addView(slotBackground);
            frontContainer.addView(slotForeground);
            countContainer.addView(slotCountdown);
        }

        slotContainer.addView(backContainer);
        slotContainer.addView(frontContainer);
        slotContainer.addView(countContainer);
    }

    public void populateSlotContainer(RelativeLayout slotContainer, String location) {
        List<Pending_Inventory> pendingItems = Pending_Inventory.getPendingItems(location);
        LinearLayout frontContainer = (LinearLayout) slotContainer.getChildAt(1);
        LinearLayout countContainer = (LinearLayout) slotContainer.getChildAt(2);

        int i = 0;
        for (Pending_Inventory pendingItem : pendingItems) {
            long itemFinishTime = pendingItem.getTimeCreated() + pendingItem.getCraftTime();
            long currentTime = System.currentTimeMillis();
            int drawableId = context.getResources().getIdentifier("item" + pendingItem.getItem(), "drawable", context.getPackageName());

            ImageView slotItem = (ImageView) frontContainer.getChildAt(i);
            TextView slotCount = (TextView) countContainer.getChildAt(i);

            if (itemFinishTime <= currentTime) {
                // If the item has finished crafting
                Inventory.addItem(pendingItem.getItem(), pendingItem.getState(), pendingItem.getQuantity());
                Pending_Inventory.delete(pendingItem);
            } else {
                // Add 500 so we always round up
                long timeLeft = TimeUnit.MILLISECONDS.toSeconds((itemFinishTime - currentTime) + 500);
                slotItem.setImageResource(drawableId);
                slotCount.setText(Long.toString(timeLeft));
                slotCount.setGravity(Gravity.CENTER);
                i++;
            }
        }
    }

    public void depopulateSlotContainer(RelativeLayout slotContainer) {
        LinearLayout frontContainer = (LinearLayout) slotContainer.getChildAt(1);
        LinearLayout countContainer = (LinearLayout) slotContainer.getChildAt(2);

        for (int i = 0; i < frontContainer.getChildCount(); i++) {
            TextView count = (TextView) countContainer.getChildAt(i);
            count.setText("");

            ImageView slot = (ImageView) frontContainer.getChildAt(i);
            slot.setImageResource(R.drawable.transparent);
        }
    }

    public void populateVisitorsContainer(final Context context, LinearLayout visitorsContainer) {
        List<Visitor> visitors = Visitor.listAll(Visitor.class);

        for (final Visitor visitor : visitors) {
            ImageView visitorImage = createVisitorImage(visitor, 100, 100);
            visitorImage.setPadding(20, 20, 20, 20);
            visitorImage.setTag(visitor.getId().toString());
            visitorImage.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(context, VisitorActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(VISITOR_TO_LOAD, (String)v.getTag());
                    context.startActivity(intent);
                }
            });
            visitorsContainer.addView(visitorImage);
        }
    }

    public TextView createTextView(String text, int size, int color) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(size);
        textView.setTextColor(color);
        return textView;
    }

    public TextView createItemCount(Long itemId, int state, int textColour, int backColour) {
        int viewId = context.getResources().getIdentifier("text" + Long.toString(itemId), "id", context.getPackageName());

        List<Inventory> inventories = Inventory.find(Inventory.class, "state = " + state + " AND id = " + itemId);
        Inventory item;
        if (inventories.size() > 0) {
            item = inventories.get(0);
        } else {
            item = new Inventory(itemId, state, 0);
        }

        TextView text = new TextView(context);
        text.setId(viewId);
        text.setTag(itemId + "Count");
        text.setTextColor(textColour);
        text.setShadowLayer(5, 0, 0, backColour);
        text.setTextSize(22);
        text.setText(Integer.toString(item.getQuantity()));
        return text;
    }

    public ImageView createItemImage(Long itemId, int width, int height, int canCraft) {
        int viewId = context.getResources().getIdentifier("img" + Long.toString(itemId), "id", context.getPackageName());
        int drawableId = context.getResources().getIdentifier("item" + itemId, "drawable", context.getPackageName());

        Bitmap bMap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        Drawable imageResource = new BitmapDrawable(context.getResources(), bMap);

        if (canCraft != 1) {
            imageResource.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        } else {
            imageResource.clearColorFilter();
        }

        ImageView image = new ImageView(context);
        //image.setAdjustViewBounds(true);
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

    public ImageView createCharacterImage(Long charId, int width, int height) {
        int viewId = context.getResources().getIdentifier("img" + Long.toString(charId), "id", context.getPackageName());
        int drawableId = context.getResources().getIdentifier("character" + charId, "drawable", context.getPackageName());

        Bitmap bMap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        Drawable imageResource = new BitmapDrawable(context.getResources(), bMap);

        ImageView image = new ImageView(context);
        //image.setAdjustViewBounds(true);
        image.setId(viewId);
        image.setTag(charId);
        image.setImageDrawable(imageResource);
        image.setMaxWidth(width);
        image.setMinimumWidth(width);
        image.setMaxHeight(height);
        image.setMinimumHeight(height);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);

        return image;
    }

    public ImageView createVisitorImage(Visitor visitor, int width, int height) {
        int viewId = context.getResources().getIdentifier("img" + visitor.getType(), "id", context.getPackageName());
        int drawableId = context.getResources().getIdentifier("visitor" + visitor.getType(), "drawable", context.getPackageName());

        Bitmap bMap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        Drawable imageResource = new BitmapDrawable(context.getResources(), bMap);

        ImageView image = new ImageView(context);
        //image.setAdjustViewBounds(true);
        image.setId(viewId);
        image.setTag(visitor.getId());
        image.setImageDrawable(imageResource);
        image.setMaxWidth(width);
        image.setMinimumWidth(width);
        image.setMaxHeight(height);
        image.setMinimumHeight(height);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);

        return image;
    }

    public int getCoins() {
        List<Inventory> inventories = Inventory.find(Inventory.class, "STATE = 1 AND ITEM = 52");
        Inventory inventory = inventories.get(0);
        return inventory.getQuantity();
    }

    public void updateCoins(int coins) {
        List<Inventory> inventories = Inventory.findWithQuery(Inventory.class, "SELECT * FROM inventory WHERE item = 52");
        Inventory foundInventory = inventories.get(0);
        foundInventory.setQuantity(coins);
        foundInventory.save();

        updateCoinsGUI();
    }

    public void updateCoinsGUI() {
        String coinCountString = String.format("%,d", getCoins());
        MainActivity.coins.setText(coinCountString + " coins");
    }

    public void updateLevelText() {
        TextView levelCount = MainActivity.level;
        levelCount.setText("Level" + Player_Info.getPlayerLevel() + " (" + Player_Info.getXp() + "xp)");
    }

    public void createItemIngredientsTable(Long itemId, int state, TableLayout ingredientsTable) {
        // Prepare the ingredients table and retrieve the list of ingredients
        List<Recipe> ingredients = Recipe.getIngredients(itemId, state);
        ingredientsTable.removeAllViews();
        ingredientsTable.setColumnStretchable(1, true);

        // Add a header row
        TableRow headerRow = new TableRow(context);
        headerRow.addView(createTextView("", 15, Color.DKGRAY));
        headerRow.addView(createTextView("", 15, Color.DKGRAY));
        headerRow.addView(createTextView("Need", 15, Color.DKGRAY));
        headerRow.addView(createTextView("Have", 15, Color.DKGRAY));
        ingredientsTable.addView(headerRow);

        // Add the level requirement row
        TableRow levelRow = new TableRow(context);
        Item item = Item.findById(Item.class, itemId);
        levelRow.addView(createTextView("", 15, Color.DKGRAY));
        levelRow.addView(createTextView("Level", 15, Color.DKGRAY));
        levelRow.addView(createTextView(Integer.toString(item.getLevel()), 15, Color.DKGRAY));
        levelRow.addView(createTextView(Integer.toString(Player_Info.getPlayerLevel()), 15, Color.DKGRAY));
        ingredientsTable.addView(levelRow);


        // Add a row for each ingredient
        for (Recipe ingredient : ingredients) {
            Item itemIngredient = Item.findById(Item.class, ingredient.getIngredient());
            Inventory owned = Inventory.getInventory(ingredient.getIngredient(), ingredient.getIngredientState());
            TableRow row = new TableRow(context);

            String itemName = itemIngredient.getName();
            if (ingredient.getIngredientState() == 2) {
                itemName = "(unf) " + itemName;
            }
            TextView itemNameView = createTextView(itemName, 15, Color.DKGRAY);
            itemNameView.setSingleLine(false);

            row.addView(createItemImage(ingredient.getIngredient(), 66, 62, 1));
            row.addView(itemNameView);
            row.addView(createTextView(Integer.toString(ingredient.getQuantity()), 15, Color.DKGRAY));
            row.addView(createTextView(Integer.toString(owned.getQuantity()), 15, Color.DKGRAY));

            ingredientsTable.addView(row);
        }
    }


}
