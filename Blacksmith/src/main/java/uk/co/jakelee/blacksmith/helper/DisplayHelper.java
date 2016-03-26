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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.main.MainActivity;
import uk.co.jakelee.blacksmith.main.VisitorActivity;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Pending_Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Recipe;
import uk.co.jakelee.blacksmith.model.Slot;
import uk.co.jakelee.blacksmith.model.Trader;
import uk.co.jakelee.blacksmith.model.Upgrade;
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
        slotContainer.removeAllViews();
        int playerLevel = Player_Info.getPlayerLevel();

        LinearLayout.LayoutParams slotParams = new LinearLayout.LayoutParams(180, 180);
        LinearLayout.LayoutParams countParams = new LinearLayout.LayoutParams(180, LinearLayout.LayoutParams.WRAP_CONTENT);
        countParams.setMargins(0, 110, 0, 0);

        LinearLayout backContainer = new LinearLayout(context);
        LinearLayout frontContainer = new LinearLayout(context);
        LinearLayout countContainer = new LinearLayout(context);

        boolean displayedNextUnlock = false;

        for (Slot slot : slots) {
            ImageView slotBackground = new ImageView(context);
            slotBackground.setLayoutParams(slotParams);
            slotBackground.setAlpha(0.6F);

            ImageView slotForeground = new ImageView(context);
            slotForeground.setLayoutParams(slotParams);
            slotForeground.setImageResource(R.drawable.transparent);

            TextViewPixel slotCountdown = new TextViewPixel(context);
            slotCountdown.setTextSize(30);
            slotCountdown.setTextColor(Color.WHITE);
            slotCountdown.setBackgroundColor(Color.BLACK);
            slotCountdown.setAlpha(0.6F);
            slotCountdown.setGravity(Gravity.CENTER);
            slotCountdown.setTag(0);

            if (slot.getLevel() > playerLevel) {
                if (!displayedNextUnlock) {
                    slotBackground.setBackgroundResource(R.drawable.close);
                    slotBackground.setTag(false);
                    slotCountdown.setTag(slot.getLevel());
                } else {
                    slotCountdown.setVisibility(View.INVISIBLE);
                }
                displayedNextUnlock = true;
            } else if (slot.getPremium() == Constants.TRUE) {
                slotBackground.setBackgroundResource(R.drawable.item52);
                slotBackground.setTag(false);
            } else {
                slotBackground.setBackgroundResource(R.drawable.slot);
                slotBackground.setTag(true);
            }

            backContainer.addView(slotBackground);
            frontContainer.addView(slotForeground);
            countContainer.addView(slotCountdown, countParams);
        }

        slotContainer.addView(backContainer);
        slotContainer.addView(frontContainer);
        slotContainer.addView(countContainer);
    }

    public void populateSlotContainer(RelativeLayout slotContainer, Long locationID) {
        List<Pending_Inventory> pendingItems = Pending_Inventory.getPendingItems(locationID);
        LinearLayout frontContainer = (LinearLayout) slotContainer.getChildAt(1);
        LinearLayout countContainer = (LinearLayout) slotContainer.getChildAt(2);

        int i = 0;
        for (Pending_Inventory pendingItem : pendingItems) {
            long itemFinishTime = pendingItem.getTimeCreated() + pendingItem.getCraftTime();
            long currentTime = System.currentTimeMillis();
            int drawableId = context.getResources().getIdentifier("item" + pendingItem.getItem(), "drawable", context.getPackageName());

            ImageView slotItem = (ImageView) frontContainer.getChildAt(i);
            TextViewPixel slotCount = (TextViewPixel) countContainer.getChildAt(i);

            if (itemFinishTime <= currentTime) {
                // If the item has finished crafting
                Inventory.addItem(pendingItem.getItem(), pendingItem.getState(), pendingItem.getQuantity());
                Pending_Inventory.delete(pendingItem);
            } else {
                long millisecondsLeft = (itemFinishTime - currentTime);
                double secondsLeft = (double) millisecondsLeft / DateHelper.MILLISECONDS_IN_SECOND;
                String roundedSecondsLeft = Integer.toString((int) Math.ceil(secondsLeft));

                slotItem.setImageResource(drawableId);
                slotCount.setText(roundedSecondsLeft);
                slotCount.setVisibility(View.VISIBLE);
                i++;
            }
        }

        for (int j = 0; j < countContainer.getChildCount(); j++) {
            TextViewPixel slotCount = (TextViewPixel) countContainer.getChildAt(j);
            if ((int)slotCount.getTag() > j) {
                slotCount.setText(String.valueOf((int)slotCount.getTag()));
            }
        }
    }

    public void depopulateSlotContainer(RelativeLayout slotContainer) {
        LinearLayout frontContainer = (LinearLayout) slotContainer.getChildAt(1);
        LinearLayout countContainer = (LinearLayout) slotContainer.getChildAt(2);

        for (int i = 0; i < frontContainer.getChildCount(); i++) {
            TextViewPixel count = (TextViewPixel) countContainer.getChildAt(i);
            count.setText("");

            ImageView slot = (ImageView) frontContainer.getChildAt(i);
            slot.setImageResource(R.drawable.transparent);
        }
    }

    public void populateVisitorsContainer(final Context context, final MainActivity activity, LinearLayout visitorsContainer, LinearLayout visitorsContainerOverflow) {
        List<Visitor> visitors = Visitor.listAll(Visitor.class);
        int displayedVisitors = 0;

        for (final Visitor visitor : visitors) {

            // Creating visitor image
            ImageView visitorImage = createImageView("visitor", visitor.getType().toString(), 170, 170);
            visitorImage.setPadding(10, 10, 10, 10);
            visitorImage.setTag(visitor.getId().toString());
            visitorImage.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(context, VisitorActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(VISITOR_TO_LOAD, (String) v.getTag());
                    context.startActivity(intent);
                }
            });

            // Adding to appropriate container
            if (displayedVisitors < Constants.MAXIMUM_VISITORS_PER_ROW) {
                visitorsContainer.addView(visitorImage);
            } else {
                visitorsContainerOverflow.addView(visitorImage);
            }
            displayedVisitors++;
        }

        LinearLayout targetContainer = null;
        if (visitorsContainer.getChildCount() < Constants.MAXIMUM_VISITORS_PER_ROW) {
            targetContainer = visitorsContainer;
        } else if (visitorsContainerOverflow.getChildCount() < Constants.MAXIMUM_VISITORS_PER_ROW) {
            targetContainer = visitorsContainerOverflow;
        }

        if (targetContainer != null && (visitorsContainer.getChildCount() + visitorsContainerOverflow.getChildCount()) < Upgrade.getValue("Maximum Visitors")) {
            ImageView addVisitorButton = createImageView("add", "", 200, 200);
            addVisitorButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    AlertDialogHelper.confirmVisitorAdd(context, activity);
                }
            });
            targetContainer.addView(addVisitorButton);
        }
    }

    public Space createSpace() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.weight = 1;

        Space space = new Space(context);
        space.setLayoutParams(params);

        return space;
    }

    public TextViewPixel createTextView(String text, int size) {
        return createTextView(text, size, Color.BLACK, Gravity.LEFT);
    }

    public TextViewPixel createTextView(String text, int size, int color) {
        return createTextView(text, size, color, Gravity.LEFT);
    }

    public TextViewPixel createTextView(String text, int size, int color, int gravity) {
        TextViewPixel textView = new TextViewPixel(context);
        textView.setText(text);
        textView.setTextSize(size);
        textView.setTextColor(color);
        textView.setGravity(gravity);
        return textView;
    }

    public TextViewPixel createItemCount(Long itemId, int state, int textColour, int backColour) {
        int viewId = context.getResources().getIdentifier("text" + Long.toString(itemId), "id", context.getPackageName());

        Inventory inventory = Select.from(Inventory.class).where(
                Condition.prop("state").eq(state),
                Condition.prop("id").eq(itemId)).first();

        int numberOwned = 0;
        if (inventory != null) {
            numberOwned = inventory.getQuantity();
        }

        TextViewPixel text = new TextViewPixel(context);
        text.setId(viewId);
        text.setTag(itemId + "Count");
        text.setTextColor(textColour);
        text.setBackgroundColor(backColour);
        text.setAlpha(0.6F);
        text.setGravity(Gravity.CENTER);
        text.setTextSize(25);
        text.setText(Integer.toString(numberOwned));
        return text;
    }

    public void displayItemInfo(Long itemID, int state, View itemArea) {
        Item item = Item.findById(Item.class, itemID);
        Inventory inventory = Select.from(Inventory.class).where(
                Condition.prop("item").eq(itemID),
                Condition.prop("state").eq(state)).first();

        int numberOwned = 0;
        if (inventory != null) {
            numberOwned = inventory.getQuantity();
        }

        TextViewPixel itemName = (TextViewPixel) itemArea.findViewById(R.id.itemName);
        TextViewPixel itemDesc = (TextViewPixel) itemArea.findViewById(R.id.itemDesc);
        TextViewPixel itemCount = (TextViewPixel) itemArea.findViewWithTag(itemID + "Count");

        if (Inventory.haveSeen(itemID, state)) {
            itemName.setText(item.getPrefix(state) + item.getName());
            itemDesc.setText(item.getDescription());
            itemCount.setText(Integer.toString(numberOwned));
        } else {
            itemName.setText(R.string.unknownText);
            itemDesc.setText(R.string.unknownText);
            itemCount.setText(R.string.unknownText);
        }
    }

    public ImageView createItemImage(Long itemId, int width, int height, boolean haveSeen) {
        int viewId = context.getResources().getIdentifier("img" + Long.toString(itemId), "id", context.getPackageName());

        int drawableId = context.getResources().getIdentifier("item" + itemId, "drawable", context.getPackageName());
        Drawable imageResource = createDrawable(drawableId, width, height);

        if (haveSeen) {
            imageResource.clearColorFilter();
        } else {
            imageResource.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        }

        ImageView image = new ImageView(context);
        image.setId(viewId);
        image.setTag(itemId);
        image.setImageDrawable(imageResource);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);

        return image;
    }

    public Drawable createDrawable(int drawableId, int width, int height) {
        Bitmap rawImage = BitmapFactory.decodeResource(context.getResources(), drawableId);
        Bitmap resizedImage = Bitmap.createScaledBitmap(rawImage, width, height, false);
        Drawable drawableImage = new BitmapDrawable(context.getResources(), resizedImage);

        return drawableImage;
    }

    public ImageView createImageView(String type, Long value, int width, int height) {
        return createImageView(type, value.toString(), width, height);
    }

    public ImageView createImageView(String type, String value, int width, int height) {
        int viewId = context.getResources().getIdentifier("img" + value, "id", context.getPackageName());
        int drawableId = context.getResources().getIdentifier(type + value, "drawable", context.getPackageName());

        Bitmap rawImage = BitmapFactory.decodeResource(context.getResources(), drawableId);
        Bitmap resizedImage = Bitmap.createScaledBitmap(rawImage, width, height, false);
        Drawable imageResource = new BitmapDrawable(context.getResources(), resizedImage);

        ImageView image = new ImageView(context);
        image.setId(viewId);
        image.setTag(type + value);
        image.setImageDrawable(imageResource);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);

        return image;
    }

    public void updateCoins(int coins) {
        Inventory ownedCoins = Select.from(Inventory.class).where(
                Condition.prop("state").eq(Constants.STATE_NORMAL),
                Condition.prop("item").eq(Constants.ITEM_COINS)).first();
        ownedCoins.setQuantity(coins);
        ownedCoins.save();

        updateCoinsGUI();
    }

    public void updateCoinsGUI() {
        String coinCountString = String.format("%,d", Inventory.getCoins());
        MainActivity.coins.setText(coinCountString);
    }

    public void updateLevelText(Context context) {
        TextViewPixel levelCount = MainActivity.level;
        levelCount.setText(String.format("%d", Player_Info.getPlayerLevel()));

        ProgressBar levelProgress = MainActivity.levelProgress;
        levelProgress.setProgress(Player_Info.getLevelProgress());

        TextViewPixel levelPercent = MainActivity.levelPercent;
        levelPercent.setText(String.format("%d%%", Player_Info.getLevelProgress()));

        if (Player_Info.getPlayerLevel() > Player_Info.getPlayerLevelFromDB()) {
            ToastHelper.showToast(context, Toast.LENGTH_SHORT, getLevelUpText(Player_Info.getPlayerLevel()));
            Player_Info.increaseByOne(Player_Info.Statistic.SavedLevel);
        }
    }

    public String getLevelUpText(int newLevel) {
        Long numItems = Select.from(Item.class).where(
                Condition.prop("level").eq(newLevel)).count();
        Long numTraders = Select.from(Trader.class).where(
                Condition.prop("level").eq(newLevel)).count();
        Long numSlots = Select.from(Slot.class).where(
                Condition.prop("level").eq(newLevel)).count();

        return String.format("Levelled up to %d! Unlocked %d item(s), %d trader(s), and %d slot(s).", newLevel, numItems, numTraders, numSlots);
    }

    public void createItemIngredientsTable(Long itemID, int state, TableLayout ingredientsTable) {
        Context context = ingredientsTable.getContext();
        // Prepare the ingredients table and retrieve the list of ingredients
        List<Recipe> ingredients = Recipe.getIngredients(itemID, state);
        ingredientsTable.removeAllViews();
        ingredientsTable.setColumnStretchable(1, true);

        // Add a header row
        TableRow headerRow = new TableRow(context);
        headerRow.addView(createTextView("", 22, Color.BLACK));
        headerRow.addView(createTextView("", 22, Color.BLACK));
        headerRow.addView(createTextView("Need ", 22, Color.BLACK));
        headerRow.addView(createTextView("Have", 22, Color.BLACK));
        ingredientsTable.addView(headerRow);

        // Add the level requirement row
        TableRow levelRow = new TableRow(context);
        Item item = Item.findById(Item.class, itemID);
        levelRow.addView(createImageView("levels", "", 54, 54));
        levelRow.addView(createTextView("Level", 22, Color.BLACK));
        levelRow.addView(createTextView(Integer.toString(item.getLevel()), 22, Color.DKGRAY));
        levelRow.addView(createTextView(Integer.toString(Player_Info.getPlayerLevel()), 22, Color.DKGRAY));
        ingredientsTable.addView(levelRow);

        // Add a row for each ingredient
        for (Recipe ingredient : ingredients) {
            Item itemIngredient = Item.findById(Item.class, ingredient.getIngredient());
            Inventory owned = Inventory.getInventory(ingredient.getIngredient(), ingredient.getIngredientState());
            TableRow row = new TableRow(context);

            String itemName = "???";
            boolean haveSeen = Inventory.haveSeen(ingredient.getIngredient(), ingredient.getIngredientState());
            if (haveSeen) {
                itemName = itemIngredient.getPrefix(ingredient.getIngredientState()) + itemIngredient.getName();
            }
            TextViewPixel itemNameView = createTextView(itemName, 22, Color.BLACK);
            itemNameView.setSingleLine(false);
            itemNameView.setPadding(0, 10, 0, 0);

            row.addView(createItemImage(ingredient.getIngredient(), 66, 66, haveSeen));
            row.addView(itemNameView);
            row.addView(createTextView(Integer.toString(ingredient.getQuantity()), 22, Color.DKGRAY));
            row.addView(createTextView(Integer.toString(owned.getQuantity()), 22, Color.DKGRAY));

            ingredientsTable.addView(row);
        }
    }

    public void drawArrows(int current, int min, int max, View downArrow, View upArrow) {
        if (current == max) {
            upArrow.setVisibility(View.INVISIBLE);
        } else {
            upArrow.setVisibility(View.VISIBLE);
        }

        if (current == min) {
            downArrow.setVisibility(View.INVISIBLE);
        } else {
            downArrow.setVisibility(View.VISIBLE);
        }
    }

}
