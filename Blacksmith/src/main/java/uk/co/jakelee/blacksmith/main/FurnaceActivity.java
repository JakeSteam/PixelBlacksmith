package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.HorizontalDots;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ErrorHelper;
import uk.co.jakelee.blacksmith.helper.GestureHelper;
import uk.co.jakelee.blacksmith.helper.GooglePlayHelper;
import uk.co.jakelee.blacksmith.helper.SoundHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.helper.TutorialHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Pending_Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Setting;
import uk.co.jakelee.blacksmith.model.Super_Upgrade;

public class FurnaceActivity extends Activity {
    private static final Handler handler = new Handler();
    private static DisplayHelper dh;
    private static GestureHelper gh;
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;
    private TextView smelt1;
    private TextView smelt10;
    private TextView smelt100;
    private boolean foodSelected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furnace);

        dh = DisplayHelper.getInstance(getApplicationContext());
        dh.updateFullscreen(this);

        gh = new GestureHelper(getApplicationContext());
        foodSelected = MainActivity.prefs.getBoolean("furnaceTab", false);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        smelt1 = (TextView) findViewById(R.id.smelt1);
        smelt10 = (TextView) findViewById(R.id.smelt10);
        smelt100 = (TextView) findViewById(R.id.smelt100);

        createInterface(true);

        if (TutorialHelper.currentlyInTutorial && TutorialHelper.currentStage <= Constants.STAGE_6_FURNACE) {
            startTutorial();
        }

        final Runnable everySecond = new Runnable() {
            @Override
            public void run() {
                dh.createCraftingInterface(
                        (RelativeLayout) findViewById(R.id.furnace),
                        (TableLayout) findViewById(R.id.ingredientsTable),
                        mViewFlipper,
                        Constants.STATE_NORMAL);
                updateButtons();
                handler.postDelayed(this, DateHelper.MILLISECONDS_IN_SECOND);
            }
        };
        handler.post(everySecond);
    }

    @Override
    public void onResume() {
        super.onResume();
        dh.createCraftingInterface(
                (RelativeLayout) findViewById(R.id.furnace),
                (TableLayout) findViewById(R.id.ingredientsTable),
                mViewFlipper,
                Constants.STATE_NORMAL);
    }

    @Override
    public void onStop() {
        super.onStop();
        MainActivity.prefs.edit().putInt("furnacePosition", mViewFlipper.getDisplayedChild()).apply();
        MainActivity.prefs.edit().putBoolean("furnaceTab", foodSelected).apply();
        handler.removeCallbacksAndMessages(null);
    }

    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void startTutorial() {
        TutorialHelper th = new TutorialHelper(this, Constants.STAGE_6_FURNACE);
        th.addTutorial(findViewById(R.id.viewFlipper), R.string.tutorialFurnaceItems, R.string.tutorialFurnaceItemsText, false);
        th.addTutorialRectangle(findViewById(R.id.horizontalIndicator), R.string.tutorialFurnaceScroll, R.string.tutorialFurnaceScrollText, false);
        th.addTutorialRectangle(findViewById(R.id.ingredientsTable), R.string.tutorialFurnaceIngredients, R.string.tutorialFurnaceIngredientsText, false);
        th.addTutorialRectangle(findViewById(R.id.smelt1), R.string.tutorialFurnaceSmelt, R.string.tutorialFurnaceSmeltText, true, Gravity.TOP);
        th.addTutorialRectangle(findViewById(R.id.close), R.string.tutorialFurnaceClose, R.string.tutorialFurnaceCloseText, true, Gravity.BOTTOM);
        th.start();
    }

    private void createInterface(boolean clearExisting) {
        updateTabs();

        if (foodSelected) {
            createFoodInterface(clearExisting);
        } else {
            createFurnaceInterface(clearExisting);
        }

        updateButtons();
    }

    private void updateButtons() {
        if (MainActivity.vh.furnaceBusy) {
            dimButtons();
        } else {
            brightenButtons();
        }
    }

    private void createFurnaceInterface(boolean clearExisting) {
        List<Item> items = Select.from(Item.class).where(
                Condition.prop("type").eq(Constants.TYPE_BAR)).list();

        dh.createItemSelector(
                (ViewFlipper) findViewById(R.id.viewFlipper),
                (HorizontalDots) findViewById(R.id.horizontalIndicator),
                clearExisting,
                items,
                Constants.STATE_NORMAL,
                MainActivity.prefs.getInt("furnacePosition", 0));

        dh.createCraftingInterface(
                (RelativeLayout) findViewById(R.id.furnace),
                (TableLayout) findViewById(R.id.ingredientsTable),
                mViewFlipper,
                Constants.STATE_NORMAL);

        HorizontalDots horizontalIndicator = (HorizontalDots) findViewById(R.id.horizontalIndicator);
        horizontalIndicator.addDots(dh, mViewFlipper.getChildCount(), mViewFlipper.getDisplayedChild());

        ((TextView) findViewById(R.id.smelt1)).setText(R.string.smelt1Text);
        if (Setting.getSafeBoolean(Constants.SETTING_HANDLE_MAX)) {
            ((TextView) findViewById(R.id.smelt100)).setText(R.string.maxText);
        } else {
            ((TextView) findViewById(R.id.smelt100)).setText(R.string.smelt100Text);
        }
    }

    private void createFoodInterface(boolean clearExisting) {
        List<Item> items = new ArrayList<>();
        items.addAll(Select.from(Item.class).where(
                Condition.prop("type").eq(Constants.TYPE_PROCESSED_FOOD),
                Condition.prop("id").notEq(231)).orderBy("level").list());

        dh.createItemSelector(
                (ViewFlipper) findViewById(R.id.viewFlipper),
                (HorizontalDots) findViewById(R.id.horizontalIndicator),
                clearExisting,
                items,
                Constants.STATE_NORMAL,
                MainActivity.prefs.getInt("furnacePosition", 0));

        dh.createCraftingInterface(
                (RelativeLayout) findViewById(R.id.furnace),
                (TableLayout) findViewById(R.id.ingredientsTable),
                mViewFlipper,
                Constants.STATE_NORMAL);

        HorizontalDots horizontalIndicator = (HorizontalDots) findViewById(R.id.horizontalIndicator);
        horizontalIndicator.addDots(dh, mViewFlipper.getChildCount(), mViewFlipper.getDisplayedChild());

        ((TextView) findViewById(R.id.smelt1)).setText(R.string.cook1Text);
        if (Setting.getSafeBoolean(Constants.SETTING_HANDLE_MAX)) {
            ((TextView) findViewById(R.id.smelt100)).setText(R.string.maxText);
        } else {
            ((TextView) findViewById(R.id.smelt100)).setText(R.string.smelt100Text);
        }
    }

    public void smelt1(View v) {
        Long itemID = (Long) mViewFlipper.getCurrentView().getTag();
        smelt(itemID, 1);
    }

    public void smelt10(View v) {
        Long itemID = (Long) mViewFlipper.getCurrentView().getTag();
        int numCraftable = Inventory.getNumberCreatable(itemID, Constants.STATE_NORMAL);
        smelt(itemID, numCraftable >= 10 ? 10 : numCraftable);
    }

    public void smelt100(View v) {
        Long itemID = (Long) mViewFlipper.getCurrentView().getTag();
        int numCraftable = Inventory.getNumberCreatable(itemID, Constants.STATE_NORMAL);
        if (Setting.getSafeBoolean(Constants.SETTING_HANDLE_MAX)) {
            smelt(itemID, numCraftable);
        } else {
            smelt(itemID, numCraftable >= 100 ? 100 : numCraftable);
        }
    }

    private void smelt(Long itemID, int quantity) {
        int quantitySmelted = 0;
        List<Pair<Long, Integer>> itemsToAdd = new ArrayList<>();

        int canCreate = Inventory.canCreateBulkItem(itemID, Constants.STATE_NORMAL, quantity);
        if (MainActivity.vh.furnaceBusy) {
            canCreate = Constants.ERROR_BUSY;
        } else if (canCreate == Constants.SUCCESS) {
            Inventory.removeItemIngredients(itemID, Constants.STATE_NORMAL, quantity);

            if (Super_Upgrade.isEnabled(Constants.SU_DOUBLE_FURNACE_CRAFTS)) {
                quantity = quantity * 2;
            }

            quantitySmelted = quantity;
            for (int i = 1; i <= quantity; i++) {
                itemsToAdd.add(new Pair<>(itemID, Constants.STATE_NORMAL));
            }
        }

        try {
            if (quantitySmelted > 0) {
                if (itemID.equals(231L)) {
                    // Golden egg achievement
                    GooglePlayHelper.UnlockAchievement("CgkI6tnE2Y4OEAIQWw");
                }
                Item item = Item.findById(Item.class, itemID);
                SoundHelper.playSound(this, SoundHelper.smithingSounds);
                ToastHelper.showToast(findViewById(R.id.furnace), ToastHelper.SHORT, String.format(getString(R.string.craftSuccess), quantitySmelted, item.getFullName(this, Constants.STATE_NORMAL)), false);
                Player_Info.increaseByX(Player_Info.Statistic.ItemsSmelted, quantitySmelted);
                GooglePlayHelper.UpdateEvent(foodSelected ? Constants.EVENT_CREATE_FOOD : Constants.EVENT_CREATE_BAR, quantitySmelted);

                Pending_Inventory.addScheduledItems(this, Constants.LOCATION_FURNACE, itemsToAdd);
                MainActivity.vh.furnaceBusy = true;
                dimButtons();
            } else {
                ToastHelper.showErrorToast(findViewById(R.id.furnace), ToastHelper.SHORT, getString(ErrorHelper.errors.get(canCreate)), false);
            }
        } catch (NullPointerException e) {
            Log.d("Blacksmith", "Er, error whilst crafting at the furnace?");
        }
    }

    public void brightenButtons() {
        smelt1.setAlpha(1);
        smelt10.setAlpha(1);
        smelt100.setAlpha(1);
    }

    public void dimButtons() {
        smelt1.setAlpha(0.3f);
        smelt10.setAlpha(0.3f);
        smelt100.setAlpha(0.3f);
    }

    public void calculatingComplete() {
        MainActivity.vh.furnaceBusy = false;
        brightenButtons();
    }

    public void toggleTab(View view) {
        MainActivity.prefs.edit().putInt("furnacePosition", 0).apply();
        foodSelected = !foodSelected;
        updateTabs();
        createInterface(true);
    }

    private void updateTabs() {
        if (foodSelected) {
            (findViewById(R.id.itemsTab)).setAlpha(1f);
            (findViewById(R.id.foodTab)).setAlpha(0.3f);
        } else {
            (findViewById(R.id.itemsTab)).setAlpha(0.3f);
            (findViewById(R.id.foodTab)).setAlpha(1f);
        }
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Furnace);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }

    private class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent startXY, MotionEvent finishXY, float velocityX, float velocityY) {
            gh.swipe(mViewFlipper, startXY, finishXY);

            dh.createCraftingInterface(
                    (RelativeLayout) findViewById(R.id.furnace),
                    (TableLayout) findViewById(R.id.ingredientsTable),
                    mViewFlipper,
                    Constants.STATE_NORMAL);

            HorizontalDots horizontalIndicator = (HorizontalDots) findViewById(R.id.horizontalIndicator);
            horizontalIndicator.addDots(dh, mViewFlipper.getChildCount(), mViewFlipper.getDisplayedChild());

            return super.onFling(startXY, finishXY, velocityX, velocityY);
        }
    }
}
