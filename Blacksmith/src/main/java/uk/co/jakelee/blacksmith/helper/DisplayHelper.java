package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.main.MainActivity;
import uk.co.jakelee.blacksmith.main.VisitorActivity;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Location;
import uk.co.jakelee.blacksmith.model.Pending_Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Recipe;
import uk.co.jakelee.blacksmith.model.Slot;
import uk.co.jakelee.blacksmith.model.State;
import uk.co.jakelee.blacksmith.model.Trader;
import uk.co.jakelee.blacksmith.model.Upgrade;
import uk.co.jakelee.blacksmith.model.Visitor;

public class DisplayHelper {
    public final static String VISITOR_TO_LOAD = "uk.co.jakelee.blacksmith.visitortoload";
    public final static String DEMAND_TO_LOAD = "uk.co.jakelee.blacksmith.tradetoload";
    private static final int[] slotIDs = {
            0,
            R.id.slots_anvil,
            R.id.slots_furnace,
            R.id.slots_inventory,
            R.id.slots_market,
            R.id.slots_table,
            R.id.slots_enchanting
    };
    private static DisplayHelper dhInstance = null;
    private final Context context;

    public DisplayHelper(Context context) {
        this.context = context;
    }

    public static DisplayHelper getInstance(Context ctx) {
        if (dhInstance == null) {
            dhInstance = new DisplayHelper(ctx.getApplicationContext());
        }
        return dhInstance;
    }

    private static int getItemDrawableID(Context context, long item) {
        return context.getResources().getIdentifier("item" + item, "drawable", context.getPackageName());
    }

    private static RelativeLayout createSlotRoot(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View inflatedView = inflater.inflate(R.layout.custom_slot, null);
        return (RelativeLayout) inflatedView.findViewById(R.id.slot_root);
    }

    public String getString(int ID) {
        return this.context.getString(ID);
    }

    public void createAllSlots(Activity activity) {
        List<Location> locations = Select.from(Location.class).list();
        int playerLevel = Player_Info.getPlayerLevel();

        for (Location location : locations) {
            GridLayout slotContainer = (GridLayout) activity.findViewById(slotIDs[location.getId().intValue()]);
            slotContainer.removeAllViews();

            // If user is premium, we want premium slots first so they can see them. Otherwise, at the very end.
            String sortOrder = Player_Info.isPremium() ? "DESC" : "ASC";
            List<Slot> slots = Select.from(Slot.class).where(
                    Condition.prop("location").eq(location.getId())).orderBy("premium " + sortOrder + ", level ASC").list();

            boolean displayedNextSlot = false;
            for (Slot slot : slots) {
                RelativeLayout slotRoot = createSlotRoot(activity.getApplicationContext());

                ImageView slotBackground = (ImageView) slotRoot.findViewById(R.id.slot_background);
                ImageView slotForeground = (ImageView) slotRoot.findViewById(R.id.slot_foreground);
                TextViewPixel slotCount = (TextViewPixel) slotRoot.findViewById(R.id.slot_count);
                TextViewPixel slotOverflow = (TextViewPixel) slotRoot.findViewById(R.id.slot_overflow);

                if (!displayedNextSlot) {
                    slotBackground.setBackgroundResource(R.drawable.slot);
                    slotCount.setVisibility(View.VISIBLE);
                    if (slot.getLevel() > playerLevel) {
                        slotForeground.setBackgroundResource(R.drawable.lock);
                        slotCount.setText(String.format(activity.getString(R.string.slotLevel), slot.getLevel()));
                        slotOverflow.setVisibility(View.VISIBLE);
                        displayedNextSlot = true;
                    } else if (slot.isPremium() && !Player_Info.isPremium()) {
                        slotForeground.setBackgroundResource(R.drawable.item52);
                        slotCount.setText(activity.getString(R.string.slotPremium));
                        slotOverflow.setVisibility(View.VISIBLE);
                        displayedNextSlot = true;
                    } else {
                        slotRoot.setTag(true);
                    }
                }
                slotContainer.addView(slotRoot);
            }
        }
    }

    public void populateSlots(View parentView) {
        List<Location> locations = Location.listAll(Location.class);
        for (Location location : locations) {
            populateSlot(location.getId(), parentView);
        }
    }

    private void populateSlot(long locationID, View parentView) {
        List<Pending_Inventory> pendingItems = Pending_Inventory.getPendingItems(locationID, false);
        int numItems = Pending_Inventory.getPendingItems(locationID, true).size();
        int numSlots = Slot.getUnlockedSlots(locationID);

        GridLayout slotContainer = (GridLayout) parentView.findViewById(slotIDs[(int) locationID]);
        emptySlotContainer(slotContainer);

        int slotIndex = 0;
        int finishedItems = 0;
        final List<Pending_Inventory> completedItems = new ArrayList<>();
        for (Pending_Inventory pendingItem : pendingItems) {
            RelativeLayout slot = (RelativeLayout) slotContainer.getChildAt(slotIndex);
            ImageView slotItem = (ImageView) slot.findViewById(R.id.slot_foreground);
            TextViewPixel slotCount = (TextViewPixel) slot.findViewById(R.id.slot_count);

            long itemFinishTime = pendingItem.getTimeCreated() + pendingItem.getCraftTime();
            long currentTime = System.currentTimeMillis();

            if (itemFinishTime <= currentTime) {
                completedItems.add(pendingItem);
                finishedItems++;
            } else {
                int seconds = DateHelper.getSecondsRoundUp(itemFinishTime - currentTime);

                slotItem.setImageResource(getItemDrawableID(context, pendingItem.getItem()));
                slotCount.setText(String.format(slotContainer.getContext().getString(R.string.slotSeconds), seconds));
                slotIndex++;
            }
        }
        RelativeLayout lockedSlot = (RelativeLayout) slotContainer.getChildAt(numSlots);
        displayOverflow(lockedSlot, numItems, numSlots, finishedItems);

        new Thread(new Runnable() {
            public void run() {
                for (Pending_Inventory item : completedItems) {
                    Inventory.addItem(item);
                }
                Pending_Inventory.deleteInTx(completedItems);
            }
        }).start();
    }

    private void displayOverflow(RelativeLayout lockedSlot, int numItems, int numSlots, int finishedItems) {
        if (lockedSlot != null) {
            TextViewPixel overflowDisplay = (TextViewPixel) lockedSlot.findViewById(R.id.slot_overflow);
            int overflow = (numItems - numSlots) - finishedItems;
            if (overflow > 0) {
                overflowDisplay.setText(String.format(getString(R.string.overflowText), overflow));
            } else {
                overflowDisplay.setText("");
            }
        }
    }

    private void emptySlotContainer(GridLayout slotContainer) {
        int numSlots = slotContainer.getChildCount();

        for (int i = 0; i < numSlots; i++) {
            RelativeLayout slot = (RelativeLayout) slotContainer.getChildAt(i);
            if (slot.getTag() != null) {
                ImageView slotForeground = (ImageView) slot.findViewById(R.id.slot_foreground);
                slotForeground.setImageResource(R.drawable.transparent);

                TextViewPixel slotCount = (TextViewPixel) slot.findViewById(R.id.slot_count);
                slotCount.setText("");
            }
        }
    }

    public void populateVisitorsContainer(final Context context, final MainActivity activity, LinearLayout visitorsContainer, LinearLayout visitorsContainerOverflow) {
        int displayedVisitors = 0;
        List<Visitor> visitors = Visitor.listAll(Visitor.class);
        if (visitors.size() == 0) {
            VisitorHelper.tryCreateVisitor();
        }

        int yPadding = convertDpToPixel(4);
        int xPadding = convertDpToPixel(7);

        for (final Visitor visitor : visitors) {
            // Creating visitor image
            ImageView visitorImage = createImageView("visitor", visitor.getType().toString(), 51, 51);
            visitorImage.setPadding(xPadding, yPadding, xPadding, yPadding);
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

        if (targetContainer != null && (visitorsContainer.getChildCount() + visitorsContainerOverflow.getChildCount()) < Upgrade.getValue("Maximum Visitors") && !TutorialHelper.currentlyInTutorial) {
            ImageView addVisitorButton = createImageView("add", "", 51, 51);
            addVisitorButton.setPadding(xPadding, yPadding, xPadding, yPadding);
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

    public RelativeLayout createItemSelectorElement(long itemID, long state) {
        RelativeLayout.LayoutParams countParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        countParams.setMargins(0, convertDpToPixel(60), 0, 0);

        RelativeLayout itemBox = new RelativeLayout(context);

        ImageView image = createItemImage(itemID, 80, 80, Inventory.haveSeen(itemID, state));
        TextView count = createItemCount(itemID, state, Color.WHITE, Color.BLACK);
        count.setWidth(convertDpToPixel(80));

        itemBox.addView(image);
        itemBox.addView(count, countParams);
        itemBox.setTag(itemID);

        return itemBox;
    }

    public TextViewPixel createItemCount(Long itemId, long state, int textColour, int backColour) {
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
        text.setText(String.format("%d", numberOwned));
        return text;
    }

    public void displayItemInfo(Long itemID, long state, View itemArea) {
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
            itemName.setText(String.format("%s%s",
                    item.getPrefix(state),
                    item.getName()));
            itemDesc.setText(item.getDescription());
            itemCount.setText(String.format("%d", numberOwned));
        } else {
            itemName.setText(R.string.unknownText);
            itemDesc.setText(R.string.unknownText);
            itemCount.setText(R.string.unknownText);
        }
    }

    public ImageView createItemImage(Long itemId, int width, int height, boolean haveSeen) {
        int viewId = context.getResources().getIdentifier("img" + Long.toString(itemId), "id", context.getPackageName());

        int drawableId = getItemDrawableID(context, itemId);
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
        try {
            Bitmap rawImage = BitmapFactory.decodeResource(context.getResources(), drawableId);
            int adjustedWidth = convertDpToPixel(width);
            int adjustedHeight = convertDpToPixel(height);
            Bitmap resizedImage = Bitmap.createScaledBitmap(rawImage, adjustedWidth, adjustedHeight, false);
            return new BitmapDrawable(context.getResources(), resizedImage);
        } catch (OutOfMemoryError e) {
            ToastHelper.showErrorToast(context, Toast.LENGTH_SHORT, context.getString(R.string.lowMemory), false);
            return new BitmapDrawable();
        }
    }

    public int convertDpToPixel(int dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    public ImageView createImageView(String type, Long value, int width, int height) {
        return createImageView(type, value.toString(), width, height);
    }

    public ImageView createImageView(String type, String value, int width, int height) {
        int viewId = context.getResources().getIdentifier("img" + value, "id", context.getPackageName());
        int drawableId = context.getResources().getIdentifier(type + value, "drawable", context.getPackageName());
        int adjustedWidth = convertDpToPixel(width);
        int adjustedHeight = convertDpToPixel(height);

        Bitmap rawImage = BitmapFactory.decodeResource(context.getResources(), drawableId);
        Bitmap resizedImage = Bitmap.createScaledBitmap(rawImage, adjustedWidth, adjustedHeight, false);
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

    public void createCraftingInterface(RelativeLayout main, TableLayout ingredientsTable, ViewFlipper viewFlipper, long state) {
        long currentItemID = (long) viewFlipper.getCurrentView().getTag();
        displayItemInfo(currentItemID, state, main);
        createItemIngredientsTable(currentItemID, state, ingredientsTable);
    }

    public boolean updateLevelText(Context context) {
        TextViewPixel levelCount = MainActivity.level;
        levelCount.setText(String.format("%d", Player_Info.getPlayerLevel()));

        ProgressBar levelProgress = MainActivity.levelProgress;
        levelProgress.setProgress(Player_Info.getLevelProgress());

        TextViewPixel levelPercent = MainActivity.levelPercent;
        levelPercent.setText(String.format("%d%%", Player_Info.getLevelProgress()));

        if (Player_Info.getPlayerLevel() > Player_Info.getPlayerLevelFromDB()) {
            ToastHelper.showPositiveToast(context, Toast.LENGTH_LONG, getLevelUpText(Player_Info.getPlayerLevelFromDB() + 1), true);
            Player_Info.increaseByOne(Player_Info.Statistic.SavedLevel);
            return true;
        }
        return false;
    }

    public String getLevelUpText(int newLevel) {
        Long numItems = Select.from(Item.class).where(
                Condition.prop("level").eq(newLevel)).count();
        Long numTraders = Select.from(Trader.class).where(
                Condition.prop("level").eq(newLevel)).count();
        Long numSlots = Select.from(Slot.class).where(
                Condition.prop("level").eq(newLevel)).count();
        Long numStates = Select.from(State.class).where(
                Condition.prop("minimum_level").eq(newLevel)).count();

        return String.format(this.context.getString(R.string.levelUpText), newLevel, numItems, numTraders, numSlots, numStates);
    }

    public void createItemIngredientsTable(Long itemID, long state, TableLayout ingredientsTable) {
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
        levelRow.addView(createImageView("levels", "", 25, 25));
        levelRow.addView(createTextView("Level", 22, Color.BLACK));
        levelRow.addView(createTextView(Integer.toString(item.getLevel()), 22, Color.DKGRAY));
        levelRow.addView(createTextView(Integer.toString(Player_Info.getPlayerLevel()), 22, Color.DKGRAY));
        ingredientsTable.addView(levelRow);

        // Add a row for each ingredient
        for (Recipe ingredient : ingredients) {
            Item itemIngredient = Item.findById(Item.class, ingredient.getIngredient());
            Inventory owned = Inventory.getInventory(ingredient.getIngredient(), ingredient.getIngredientState());
            TableRow row = new TableRow(context);

            String itemName = itemIngredient.getPrefix(ingredient.getIngredientState()) + itemIngredient.getName();
            TextViewPixel itemNameView = createTextView(itemName, 22, Color.BLACK);
            itemNameView.setSingleLine(false);
            itemNameView.setPadding(0, 10, 0, 0);

            row.addView(createItemImage(ingredient.getIngredient(), 25, 25, true));
            row.addView(itemNameView);
            row.addView(createTextView(Integer.toString(ingredient.getQuantity()), 22, Color.DKGRAY));
            row.addView(createTextView(Integer.toString(owned.getQuantity()), 22, Color.DKGRAY));

            ingredientsTable.addView(row);
        }
    }

    public void createItemSelector(ViewFlipper itemSelector, boolean clearExisting, List<Item> items, long state, int selectedPosition) {
        if (clearExisting) {
            itemSelector.removeAllViews();
        }

        for (Item item : items) {
            itemSelector.addView(createItemSelectorElement(item.getId(), state));
        }

        if (clearExisting) {
            itemSelector.setDisplayedChild(selectedPosition);
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
