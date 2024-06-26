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
import android.os.Build;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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
import android.widget.ViewFlipper;

import androidx.core.content.ContextCompat;

import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.HorizontalDots;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.main.ItemSelectActivity;
import uk.co.jakelee.blacksmith.main.MainActivity;
import uk.co.jakelee.blacksmith.main.VisitorActivity;
import uk.co.jakelee.blacksmith.model.Assistant;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Location;
import uk.co.jakelee.blacksmith.model.Pending_Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Recipe;
import uk.co.jakelee.blacksmith.model.Setting;
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
    public ViewFlipper itemSelectionFlipper;
    public HorizontalDots itemSelectionDots;
    public List<Item> itemSelectionItems;
    public long itemSelectionState;
    public boolean itemSelectionInventoryCheck;
    private boolean isProcessingPendingInventory = false;
    private Picasso picasso;

    public DisplayHelper(Context context) {
        this.context = context;
        this.picasso = new Picasso.Builder(context).build();
    }

    public static DisplayHelper getInstance(Context ctx) {
        if (dhInstance == null) {
            dhInstance = new DisplayHelper(ctx.getApplicationContext());
        }
        return dhInstance;
    }

    public static int getItemDrawableID(Context context, long item) {
        return context.getResources().getIdentifier("item" + item, "drawable", context.getPackageName());
    }

    public static int getAdventureDrawableID(Context context, long adventure) {
        return context.getResources().getIdentifier("adventure" + adventure, "drawable", context.getPackageName());
    }

    public static int getCharacterDrawableID(Context context, long character) {
        return context.getResources().getIdentifier("character" + character, "drawable", context.getPackageName());
    }

    public static int getVisitorDrawableID(Context context, int visitor) {
        return context.getResources().getIdentifier("visitor" + visitor, "drawable", context.getPackageName());
    }

    public static int getAssistantDrawableID(Context context, Assistant assistant) {
        if (assistant != null) {
            return context.getResources().getIdentifier("assistant" + assistant.getAssistantId() + "_" + assistant.getTier(), "drawable", context.getPackageName());
        } else {
            return R.drawable.help;
        }
    }

    private static RelativeLayout createSlotRoot(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View inflatedView = inflater.inflate(R.layout.custom_slot, null);
        return (RelativeLayout) inflatedView.findViewById(R.id.slot_root);
    }

    private static int getEventDrawableID(String eventID) {
        switch (eventID) {
            case Constants.EVENT_VISITOR_COMPLETED:
                return R.drawable.visitor26;
            case Constants.EVENT_VISITOR_FULLY_COMPLETED:
                return R.drawable.visitor20;
            case Constants.EVENT_BOUGHT_ITEM:
                return R.drawable.character8;
            case Constants.EVENT_CREATE_BAR:
                return R.drawable.item15;
            case Constants.EVENT_CREATE_UNFINISHED:
                return R.drawable.state2;
            case Constants.EVENT_CREATE_FINISHED:
                return R.drawable.item89;
            case Constants.EVENT_CREATE_ENCHANTED:
                return R.drawable.item72;
            case Constants.EVENT_CREATE_POWDER:
                return R.drawable.item129;
            case Constants.EVENT_CREATE_FOOD:
                return R.drawable.item218;
            case Constants.EVENT_SOLD_ITEM:
                return R.drawable.sell_small;
            case Constants.EVENT_TRADE_ITEM:
                return R.drawable.item52;
            case Constants.EVENT_BUY_ALL_ITEM:
                return R.drawable.character15;
            case Constants.EVENT_CONTRIBUTE:
                return R.drawable.uparrow;
            case Constants.EVENT_CLAIM_BONUS:
                return R.drawable.bonus_chest_full;
            case Constants.EVENT_HELPER_TRIPS:
                return R.drawable.visitor3;
            case Constants.EVENT_HERO_TRIPS:
                return R.drawable.visitor43;
            default:
                return R.drawable.quests;
        }
    }

    private static String formatLargeNumber(int number) {
        String numberString = Integer.toString(number);
        if (numberString.length() > 3) {
            numberString = numberString.substring(0, numberString.length() - 3) + "k";
        }
        return numberString;
    }

    public static void updateBonusChest(ImageView chest) {
        if (chest != null) {
            Picasso.with(chest.getContext())
                    .load(Player_Info.isBonusReady() ? R.drawable.bonus_chest_full : R.drawable.bonus_chest_empty)
                    .into(chest);
        }
    }

    public static void updateAssistantDisplay(RelativeLayout assistantContainer) {
        int activeAssistant = Select.from(Player_Info.class).where(Condition.prop("name").eq("ActiveAssistant")).first().getIntValue();
        long lastClaimTime = Select.from(Player_Info.class).where(Condition.prop("name").eq("LastAssistantClaim")).first().getLongValue();

        String timeLeftText;
        if (activeAssistant > 0) {
            Assistant assistant = Assistant.get(activeAssistant);
            ((ImageView) assistantContainer.findViewById(R.id.assistant_image)).setImageResource(DisplayHelper.getAssistantDrawableID(
                    assistantContainer.getContext(),
                    assistant));

            if (lastClaimTime + assistant.getRewardFrequency() <= System.currentTimeMillis()) {
                timeLeftText = assistantContainer.getContext().getString(R.string.assistantReady);
            } else {
                timeLeftText = assistantContainer.getContext().getString(R.string.assistantNotReady) + DateHelper.getHoursMinsRemaining((lastClaimTime + assistant.getRewardFrequency()) - System.currentTimeMillis());
            }
        } else {
            timeLeftText = assistantContainer.getContext().getString(R.string.assistantTeaser);
        }
        ((TextView) assistantContainer.findViewById(R.id.assistant_time)).setText(timeLeftText);
    }

    public String getString(int ID) {
        if (this.context != null) {
            return this.context.getString(ID);
        }
        return "???";
    }

    public void createAllSlots(final Activity activity) {
        List<Location> locations = Select.from(Location.class).list();
        int playerLevel = Player_Info.getPlayerLevel();

        if (Player_Info.isPremium()) {
            Slot.executeQuery("UPDATE slot SET premium = 0 WHERE level = 9999");
        }

        for (final Location location : locations) {
            if (location.getId().intValue() < slotIDs.length) {
                final GridLayout slotContainer = (GridLayout) activity.findViewById(slotIDs[location.getId().intValue()]);
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
                        if (slot.isPremium() && !Player_Info.isPremium()) {
                            slotForeground.setBackgroundResource(R.drawable.item52);
                            slotCount.setText(activity.getString(R.string.slotPremium));
                            slotOverflow.setVisibility(View.VISIBLE);
                            slotBackground.setOnClickListener(new Button.OnClickListener() {
                                public void onClick(View v) {
                                    ToastHelper.showPositiveToast(slotContainer, ToastHelper.SHORT, Pending_Inventory.getPendingItemsText(context, location.getId()), false);
                                }
                            });
                            displayedNextSlot = true;
                        } else if (slot.getLevel() > playerLevel) {
                            if (slot.getLevel() < 9999) {
                                slotForeground.setBackgroundResource(R.drawable.lock);
                                slotCount.setText(String.format(activity.getString(R.string.slotLevel), slot.getLevel()));
                            }
                            slotOverflow.setVisibility(View.VISIBLE);
                            slotBackground.setOnClickListener(new Button.OnClickListener() {
                                public void onClick(View v) {
                                    ToastHelper.showPositiveToast(slotContainer, ToastHelper.SHORT, Pending_Inventory.getPendingItemsText(context, location.getId()), false);
                                }
                            });
                            displayedNextSlot = true;
                        } else {
                            slotRoot.setTag(true);
                        }
                    }
                    slotContainer.addView(slotRoot);
                }
            }
        }
    }

    public void populateSlots(Activity activity, View parentView) {
        boolean updateUI = Setting.getSafeBoolean(Constants.SETTING_UPDATE_SLOTS);

        if (Pending_Inventory.count(Pending_Inventory.class) > 0) {
            List<Location> locations = Location.listAll(Location.class);
            for (Location location : locations) {
                populateSlot(activity, location.getId(), parentView, updateUI);
            }
        }
    }

    private void populateSlot(final Activity activity, final long locationID, View parentView, boolean updateUI) {
        List<Pending_Inventory> pendingItems = Pending_Inventory.getPendingItems(locationID, false);
        if (pendingItems.size() > 0 && !isProcessingPendingInventory) {
            final int numItems = Pending_Inventory.getPendingItems(locationID, true).size();
            final int numSlots = Slot.getUnlockedSlots(locationID);

            final GridLayout slotContainer = (GridLayout) parentView.findViewById(slotIDs[(int) locationID]);
            if (updateUI) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        emptySlotContainer(slotContainer);
                    }
                });
            }

            int slotIndex = 0;
            int finishedItems = 0;
            final List<Pending_Inventory> completedItems = new ArrayList<>();
            for (final Pending_Inventory pendingItem : pendingItems) {
                RelativeLayout slot = (RelativeLayout) slotContainer.getChildAt(slotIndex);
                if (slot != null) {
                    final ImageView slotItem = (ImageView) slot.findViewById(R.id.slot_foreground);
                    final TextViewPixel slotCount = (TextViewPixel) slot.findViewById(R.id.slot_count);

                    long itemFinishTime = pendingItem.getTimeCreated() + pendingItem.getCraftTime();
                    long currentTime = System.currentTimeMillis();

                    if (itemFinishTime <= currentTime) {
                        completedItems.add(pendingItem);
                        finishedItems++;
                    } else {
                        final int seconds = DateHelper.getSecondsRoundUp(itemFinishTime - currentTime);

                        if (updateUI) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    picasso.load(getItemDrawableID(context, pendingItem.getItem()))
                                            .into(slotItem);
                                    slotCount.setText(String.format(slotContainer.getContext().getString(R.string.slotSeconds), seconds));
                                }
                            });
                        }
                        slotIndex++;
                    }
                }
            }
            final RelativeLayout lockedSlot = (RelativeLayout) slotContainer.getChildAt(numSlots);
            final int totalFinishedItems = finishedItems;
            if (updateUI) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displayOverflow(lockedSlot, numItems, numSlots, totalFinishedItems);
                    }
                });
            }

            if (completedItems.size() > 0) {
                isProcessingPendingInventory = true;
                final boolean shouldGiveXP = (locationID != Constants.LOCATION_MARKET) && (locationID != Constants.LOCATION_SELLING);
                new Thread(new Runnable() {
                    public void run() {
                        for (Pending_Inventory item : completedItems) {
                            Inventory.addItem(item, shouldGiveXP); // if location = market or inventory, false boolean
                        }

                    }
                }).start();
                Pending_Inventory.deleteInTx(completedItems);
                isProcessingPendingInventory = false;
            }
        }
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

    public void updateFullscreen(Activity activity) {
        if (!Setting.getSafeBoolean(Constants.SETTING_CHECK_FULLSCREEN)) {
            return;
        }

        boolean shouldBeFullscreen = Setting.getSafeBoolean(Constants.SETTING_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= 19) {
            if (shouldBeFullscreen) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            } else {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        } else {
            if (shouldBeFullscreen) {
                activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

    public void populateVisitorsContainer(final Context context, final MainActivity activity, final LinearLayout visitorsContainer, final LinearLayout visitorsContainerOverflow) {
        final List<Visitor> visitors = Visitor.listAll(Visitor.class);
        List<ImageView> visitorImages = new ArrayList<>();
        if (visitors.size() == 0) {
            VisitorHelper.tryCreateVisitor();
        }

        int yPadding = convertDpToPixel(4);
        int xPadding = convertDpToPixel(6.5f);

        for (final Visitor visitor : visitors) {
            // Creating visitor image
            ImageView visitorImage = createImageView("visitor", visitor.getType().toString(), 51, 51);
            visitorImage.setPadding(xPadding, yPadding, xPadding, yPadding);
            visitorImage.setTag(visitor.getId().toString());
            visitorImage.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - MainActivity.vh.lastVisitorClick < 500) {
                        return;
                    } else {
                        MainActivity.vh.lastVisitorClick = SystemClock.elapsedRealtime();
                    }

                    Intent intent = new Intent(context, VisitorActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(VISITOR_TO_LOAD, (String) v.getTag());
                    context.startActivity(intent);
                }
            });

            visitorImages.add(visitorImage);
        }

        LinearLayout targetContainer = null;
        if (visitors.size() < Constants.MAXIMUM_VISITORS_PER_ROW) {
            targetContainer = visitorsContainer;
        } else if (visitors.size() < (Constants.MAXIMUM_VISITORS_PER_ROW * 2)) {
            targetContainer = visitorsContainerOverflow;
        }


        ImageView addVisitorButton = null;
        if (targetContainer != null && !TutorialHelper.currentlyInTutorial) {
            addVisitorButton = createImageView("add", "", 51, 51);
            addVisitorButton.setPadding(xPadding, yPadding, xPadding, yPadding);
            addVisitorButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    AlertDialogHelper.confirmVisitorAdd(context, activity);
                }
            });
        }

        final LinearLayout finalTargetContainer = targetContainer;
        final ImageView finalImageView = addVisitorButton;
        final List<ImageView> finalVisitorImages = visitorImages;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                visitorsContainer.removeAllViews();
                visitorsContainerOverflow.removeAllViews();
                boolean shouldDrawAddButton = false;

                if (visitors.size() < Upgrade.getValue("Maximum Visitors")) {
                    shouldDrawAddButton = true;
                }

                int displayedVisitors = 0;
                for (ImageView visitorImage : finalVisitorImages) {
                    if (displayedVisitors < Constants.MAXIMUM_VISITORS_PER_ROW) {
                        visitorsContainer.addView(visitorImage);
                    } else {
                        visitorsContainerOverflow.addView(visitorImage);
                    }
                    displayedVisitors++;
                }

                if (shouldDrawAddButton && finalImageView != null) {
                    finalTargetContainer.addView(finalImageView);
                }
            }
        });
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

    public RelativeLayout createItemSelectorElement(final long itemID, long state) {
        RelativeLayout.LayoutParams countParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        countParams.setMargins(0, convertDpToPixel(60), 0, 0);

        RelativeLayout itemBox = new RelativeLayout(context);

        ImageView image = createItemImage(itemID, (int) state, 80, 80, Inventory.haveSeen(itemID, state), Inventory.haveLevelFor(itemID));
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
                    item.getName(context)));
            itemDesc.setText(item.getDescription(context));
            itemCount.setText(String.format("%d", numberOwned));
        } else if (Inventory.haveLevelFor(itemID)) {
            itemName.setText(String.format("%s%s",
                    item.getPrefix(state),
                    item.getName(context)));
            itemDesc.setText(R.string.unknownText);
            itemCount.setText(R.string.unknownText);
        } else {
            itemName.setText(R.string.unknownText);
            itemDesc.setText(R.string.unknownText);
            itemCount.setText(R.string.unknownText);
        }
    }

    public ImageView createItemImage(Long itemId, int itemState, int width, int height, boolean haveSeen, boolean canCreate) {
        return createItemImage(itemId, itemState, width, height, haveSeen, canCreate, false);
    }

    public ImageView createItemImage(Long itemId, int itemState, int width, int height, boolean haveSeen, boolean canCreate, boolean isUnsellable) {
        int viewId = context.getResources().getIdentifier("img" + Long.toString(itemId), "id", context.getPackageName());

        ImageView image = new ImageView(context);
        image.setId(viewId);
        image.setTag(itemId);
        try {
            image.setImageDrawable(isUnsellable ? createDrawable(R.drawable.lock, width, height) : createItemImageDrawable(itemId, itemState, width, height, haveSeen, canCreate));
        } catch (Exception e) {
            image.setImageResource(R.drawable.help);
        }
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);

        return image;
    }

    public Drawable createItemImageDrawable(Long itemId, int itemState, int width, int height, boolean haveSeen, boolean canCreate) {
        int drawableId = getItemDrawableID(context, itemId);
        Drawable imageResource = createDrawable(drawableId, width, height);
        if (haveSeen) {
            switch (itemState) {
                case Constants.STATE_RED:
                    imageResource.setColorFilter(ContextCompat.getColor(context, R.color.redOverlay), PorterDuff.Mode.MULTIPLY);
                    break;
                case Constants.STATE_BLUE:
                    imageResource.setColorFilter(ContextCompat.getColor(context, R.color.blueOverlay), PorterDuff.Mode.MULTIPLY);
                    break;
                case Constants.STATE_GREEN:
                    imageResource.setColorFilter(ContextCompat.getColor(context, R.color.greenOverlay), PorterDuff.Mode.MULTIPLY);
                    break;
                case Constants.STATE_WHITE:
                    imageResource.setColorFilter(ContextCompat.getColor(context, R.color.whiteOverlay), PorterDuff.Mode.MULTIPLY);
                    break;
                case Constants.STATE_BLACK:
                    imageResource.setColorFilter(ContextCompat.getColor(context, R.color.blackOverlay), PorterDuff.Mode.MULTIPLY);
                    break;
                case Constants.STATE_PURPLE:
                    imageResource.setColorFilter(ContextCompat.getColor(context, R.color.purpleOverlay), PorterDuff.Mode.MULTIPLY);
                    break;
                case Constants.STATE_YELLOW:
                    imageResource.setColorFilter(ContextCompat.getColor(context, R.color.yellowOverlay), PorterDuff.Mode.MULTIPLY);
                    break;
                default:
                    imageResource.clearColorFilter();
                    break;
            }
        } else if (canCreate) {
            imageResource.setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
        } else {
            imageResource.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
        }

        return imageResource;
    }

    public Drawable createDrawable(int drawableId, int width, int height) {
        try {
            Bitmap rawImage = BitmapFactory.decodeResource(context.getResources(), drawableId);
            int adjustedWidth = convertDpToPixel(width);
            int adjustedHeight = convertDpToPixel(height);
            Bitmap resizedImage = Bitmap.createScaledBitmap(rawImage, adjustedWidth, adjustedHeight, false);
            return new BitmapDrawable(context.getResources(), resizedImage);
        } catch (OutOfMemoryError e) {
            ToastHelper.showErrorToast(null, ToastHelper.SHORT, context.getString(R.string.lowMemory), false);
        } catch (NullPointerException e) {
            ToastHelper.showErrorToast(null, ToastHelper.LONG, context.getString(R.string.unknownError), false);
        }
        return new BitmapDrawable();
    }

    public int convertDpToPixel(int dp) {
        return convertDpToPixel((float) dp);
    }

    public int convertDpToPixel(float dp) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    public ImageView createImageView(String type, Long value, int width, int height) {
        return createImageView(type, value.toString(), width, height);
    }

    public ImageView createImageView(String type, String value, int width, int height) {
        ImageView image = new ImageView(context);
        int viewId = context.getResources().getIdentifier("img" + value, "id", context.getPackageName());
        int drawableId = context.getResources().getIdentifier(type + value, "drawable", context.getPackageName());
        int adjustedWidth = convertDpToPixel(width);
        int adjustedHeight = convertDpToPixel(height);

        try {
            Bitmap rawImage = BitmapFactory.decodeResource(context.getResources(), drawableId);
            Bitmap resizedImage = Bitmap.createScaledBitmap(rawImage, adjustedWidth, adjustedHeight, false);
            Drawable imageResource = new BitmapDrawable(context.getResources(), resizedImage);


            image.setId(viewId);
            image.setTag(type + value);
            image.setImageDrawable(imageResource);
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } catch (OutOfMemoryError e) {
            ToastHelper.showErrorToast(null, ToastHelper.SHORT, context.getString(R.string.error_image_load_fail), true);
        }

        return image;
    }

    public void updateCoins(int coins) {
        Inventory ownedCoins = Select.from(Inventory.class).where(
                Condition.prop("state").eq(Constants.STATE_NORMAL),
                Condition.prop("item").eq(Constants.ITEM_COINS)).first();
        ownedCoins.setQuantity(coins);
        ownedCoins.save();
    }

    public void updateCoinsGUI(TextView coins) {
        String coinCountString = String.format("%,d", Inventory.getCoins());
        coins.setText(coinCountString);
    }

    public void createCraftingInterface(RelativeLayout main, TableLayout ingredientsTable, ViewFlipper viewFlipper, long state) {
        long currentItemID = (long) viewFlipper.getCurrentView().getTag();
        displayItemInfo(currentItemID, state, main);
        createItemIngredientsTable(currentItemID, state, ingredientsTable);
    }

    public boolean updateLevelText(TextView level, ProgressBar levelProgress, TextView levelPercent) {
        level.setText(String.format("%d", Player_Info.getPlayerLevel()));
        levelProgress.setProgress(Player_Info.getLevelProgress());
        levelPercent.setText(String.format("%d%%", Player_Info.getLevelProgress()));

        int highestLevel = Player_Info.getHighestLevel();
        int playerLevel = Player_Info.getPlayerLevel();

        if (playerLevel > Player_Info.getPlayerLevelFromDB()) {
            ToastHelper.showPositiveToast(level, ToastHelper.LONG, getLevelUpText(playerLevel), true);
            Player_Info.increaseByX(Player_Info.Statistic.SavedLevel, playerLevel - Player_Info.getPlayerLevelFromDB());
            if (playerLevel > highestLevel) {
                Player_Info.increaseByX(Player_Info.Statistic.HighestLevel, playerLevel - highestLevel);
                GooglePlayHelper.UpdateLeaderboards(Constants.LEADERBOARD_HIGHEST_LEV, playerLevel);
            }
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

        if (newLevel >= Constants.PRESTIGE_LEVEL_REQUIRED && newLevel % 5 == 0) {
            return String.format(this.context.getString(Player_Info.isPremium() ? R.string.levelUpPremiumPrestigeText : R.string.levelUpPrestigeText),
                    newLevel);
        }
        return String.format(this.context.getString(R.string.levelUpText), newLevel, numItems, numTraders, numSlots, numStates);
    }

    public void createItemIngredientsTable(Long itemID, long state, final TableLayout ingredientsTable) {
        final Context context = ingredientsTable.getContext();
        // Prepare the ingredients table and retrieve the list of ingredients
        List<Recipe> ingredients = Recipe.getIngredients(itemID, state);
        ingredientsTable.removeAllViews();
        ingredientsTable.setColumnStretchable(1, true);

        // Add a header row
        TableRow headerRow = new TableRow(context);
        headerRow.addView(createTextView("", 22, Color.BLACK));
        headerRow.addView(createTextView("", 22, Color.BLACK));
        headerRow.addView(createTextView(context.getString(R.string.need) + " ", 22, Color.BLACK));
        headerRow.addView(createTextView(context.getString(R.string.have) + " ", 22, Color.BLACK));
        ingredientsTable.addView(headerRow);

        // Add the level requirement row
        TableRow levelRow = new TableRow(context);
        Item item = Item.findById(Item.class, itemID);
        levelRow.addView(createImageView("levels", "", 25, 25));
        levelRow.addView(createTextView(context.getString(R.string.level), 22, Color.BLACK));
        levelRow.addView(createTextView(Integer.toString(item.getLevel()), 22, Color.DKGRAY));
        levelRow.addView(createTextView(Integer.toString(Player_Info.getPlayerLevel()), 22, Color.DKGRAY));
        ingredientsTable.addView(levelRow);

        // Add a row for each ingredient
        for (Recipe ingredient : ingredients) {
            final Item itemIngredient = Item.findById(Item.class, ingredient.getIngredient());
            Inventory owned = Inventory.getInventory(ingredient.getIngredient(), ingredient.getIngredientState());
            TableRow row = new TableRow(context);

            String itemName = itemIngredient.getPrefix(ingredient.getIngredientState()) + itemIngredient.getName(context);
            TextViewPixel itemNameView = createTextView(itemName, 22, Color.BLACK);
            itemNameView.setSingleLine(false);
            itemNameView.setPadding(0, 10, 0, 0);
            itemNameView.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    ToastHelper.showToast(ingredientsTable, ToastHelper.SHORT, itemIngredient.getDescription(context), false);
                }
            });

            row.addView(createItemImage(ingredient.getIngredient(), (int) ingredient.getIngredientState(), 25, 25, true, true));
            row.addView(itemNameView);
            row.addView(createTextView(formatLargeNumber(ingredient.getQuantity()), 22, Color.DKGRAY));
            row.addView(createTextView(formatLargeNumber(owned.getQuantity()), 22, Color.DKGRAY));

            ingredientsTable.addView(row);
        }
    }

    public void createItemSelector(ViewFlipper itemSelector, HorizontalDots dots, boolean clearExisting, final List<Item> items, long state, int selectedPosition) {
        createItemSelector(itemSelector, dots, clearExisting, items, state, selectedPosition, false);
    }

    public void createItemSelector(ViewFlipper itemSelector, HorizontalDots dots, boolean clearExisting, final List<Item> items, long state, int selectedPosition, boolean inventoryOverride) {
        this.itemSelectionFlipper = itemSelector;
        this.itemSelectionDots = dots;
        this.itemSelectionItems = items;
        this.itemSelectionState = state;
        this.itemSelectionInventoryCheck = inventoryOverride;

        if (clearExisting) {
            itemSelector.removeAllViews();
        }

        for (Item item : items) {
            itemSelector.addView(createItemSelectorElement(item.getId(), state));
        }

        if (clearExisting) {
            itemSelector.setDisplayedChild(selectedPosition);
        }

        if (Setting.getSafeBoolean(Constants.SETTING_CLICK_CHANGE)) {
            itemSelector.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(context, ItemSelectActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }

    public void drawArrows(int current, int min, int max, View downArrow, View upArrow) {
        drawArrows(current, min, max, downArrow, upArrow, false);
    }

    public void drawArrows(int current, int min, int max, View downArrow, View upArrow, boolean forceUpArrow) {
        if (current < min) {
            current = min;
        }

        if (current > max && !forceUpArrow) {
            current = max;
        }

        if (current == max && !forceUpArrow) {
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
