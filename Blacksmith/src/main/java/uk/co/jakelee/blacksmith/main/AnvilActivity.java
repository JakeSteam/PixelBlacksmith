package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

public class AnvilActivity extends Activity {
    private static final Handler handler = new Handler();
    private static DisplayHelper dh;
    private static GestureHelper gh;
    private int displayedTier;
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;
    private TextView craft1;
    private TextView craft10;
    private TextView craft100;
    private boolean ringsSelected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anvil);
        dh = DisplayHelper.getInstance(getApplicationContext());
        dh.updateFullscreen(this);

        gh = new GestureHelper(getApplicationContext());
        displayedTier = MainActivity.prefs.getInt("anvilTier", ringsSelected ? Constants.TIER_SILVER : Constants.TIER_MIN);
        ringsSelected = MainActivity.prefs.getBoolean("anvilTab", false);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        craft1 = (TextView) findViewById(R.id.craft1);
        craft10 = (TextView) findViewById(R.id.craft10);
        craft100 = (TextView) findViewById(R.id.craft100);

        createAnvilInterface(true, false);

        if (TutorialHelper.currentlyInTutorial && TutorialHelper.currentStage <= Constants.STAGE_8_ANVIL) {
            startTutorial();
        }

        final Runnable everySecond = new Runnable() {
            @Override
            public void run() {
                dh.createCraftingInterface(
                        (RelativeLayout) findViewById(R.id.anvil),
                        (TableLayout) findViewById(R.id.ingredientsTable),
                        mViewFlipper,
                        ringsSelected ? Constants.STATE_NORMAL : Constants.STATE_UNFINISHED);
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
                (RelativeLayout) findViewById(R.id.anvil),
                (TableLayout) findViewById(R.id.ingredientsTable),
                mViewFlipper,
                ringsSelected ? Constants.STATE_NORMAL : Constants.STATE_UNFINISHED);
    }

    @Override
    public void onStop() {
        super.onStop();

        MainActivity.prefs.edit().putInt("anvilTier", displayedTier).apply();
        MainActivity.prefs.edit().putInt("anvilPosition", mViewFlipper.getDisplayedChild()).apply();
        MainActivity.prefs.edit().putBoolean("anvilTab", ringsSelected).apply();
        handler.removeCallbacksAndMessages(null);
    }

    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void startTutorial() {
        TutorialHelper th = new TutorialHelper(this, Constants.STAGE_8_ANVIL);
        th.addTutorial(findViewById(R.id.viewFlipper), R.string.tutorialAnvil, R.string.tutorialAnvilText, false);
        th.addTutorial(findViewById(R.id.upButton), R.string.tutorialAnvilUp, R.string.tutorialAnvilUpText, false);
        th.addTutorialRectangle(findViewById(R.id.ingredientsTable), R.string.tutorialAnvilIngredients, R.string.tutorialAnvilIngredientsText, false);
        th.addTutorialRectangle(findViewById(R.id.craft1), R.string.tutorialAnvilCraft, R.string.tutorialAnvilCraftText, true, Gravity.TOP);
        th.start();
    }

    private void createAnvilInterface(boolean clearExisting, boolean resetTier) {
        updateTabs();

        if (resetTier) {
            displayedTier = ringsSelected ? Constants.TIER_SILVER : Constants.TIER_MIN;
        }

        if (ringsSelected) {
            if (displayedTier < Constants.TIER_SILVER || displayedTier > Constants.TIER_GOLD) {
                displayedTier = Constants.TIER_GOLD;
            }
            createRingsInterface(clearExisting);
        } else {
            if (displayedTier < Constants.TIER_MIN || displayedTier > Constants.TIER_MAX) {
                displayedTier = Constants.TIER_MAX;
            }
            createItemsInterface(clearExisting);
        }

        if (Setting.getSafeBoolean(Constants.SETTING_HANDLE_MAX)) {
            craft100.setText(R.string.maxText);
        } else {
            craft100.setText(R.string.craft100Text);
        }
    }

    private void updateButtons() {
        if (MainActivity.vh.anvilBusy) {
            dimButtons();
        } else {
            brightenButtons();
        }
    }

    private void createItemsInterface(boolean clearExisting) {
        List<Item> items = Select.from(Item.class).where(
                Condition.prop("type").gt(Constants.TYPE_ANVIL_MIN - 1),
                Condition.prop("type").lt(Constants.TYPE_ANVIL_MAX + 1),
                Condition.prop("tier").eq(displayedTier)).orderBy("level").list();

        dh.createItemSelector(
                (ViewFlipper) findViewById(R.id.viewFlipper),
                (HorizontalDots) findViewById(R.id.horizontalIndicator),
                clearExisting,
                items,
                Constants.STATE_UNFINISHED,
                MainActivity.prefs.getInt("anvilPosition", 0));

        dh.createCraftingInterface(
                (RelativeLayout) findViewById(R.id.anvil),
                (TableLayout) findViewById(R.id.ingredientsTable),
                mViewFlipper,
                Constants.STATE_UNFINISHED);

        dh.drawArrows(this.displayedTier, Constants.TIER_MIN, Constants.TIER_MAX, findViewById(R.id.downButton), findViewById(R.id.upButton));

        HorizontalDots horizontalIndicator = (HorizontalDots) findViewById(R.id.horizontalIndicator);
        horizontalIndicator.addDots(dh, mViewFlipper.getChildCount(), mViewFlipper.getDisplayedChild());
    }

    private void createRingsInterface(boolean clearExisting) {
        List<Item> items = Select.from(Item.class).where(
                Condition.prop("type").eq(Constants.TYPE_RING),
                Condition.prop("tier").eq(displayedTier)).orderBy("level").list();

        dh.createItemSelector(
                (ViewFlipper) findViewById(R.id.viewFlipper),
                (HorizontalDots) findViewById(R.id.horizontalIndicator),
                clearExisting,
                items,
                Constants.STATE_NORMAL,
                MainActivity.prefs.getInt("anvilPosition", 0));

        dh.createCraftingInterface(
                (RelativeLayout) findViewById(R.id.anvil),
                (TableLayout) findViewById(R.id.ingredientsTable),
                mViewFlipper,
                Constants.STATE_NORMAL);

        dh.drawArrows(this.displayedTier, Constants.TIER_SILVER, Constants.TIER_GOLD, findViewById(R.id.downButton), findViewById(R.id.upButton));

        HorizontalDots horizontalIndicator = (HorizontalDots) findViewById(R.id.horizontalIndicator);
        horizontalIndicator.addDots(dh, mViewFlipper.getChildCount(), mViewFlipper.getDisplayedChild());
    }

    public void closePopup(View view) {
        finish();
    }

    public void craft1(View v) {
        Long itemID = (Long) mViewFlipper.getCurrentView().getTag();
        craft(itemID, 1);
    }

    public void craft10(View v) {
        Long itemID = (Long) mViewFlipper.getCurrentView().getTag();
        int state = ringsSelected ? Constants.STATE_NORMAL : Constants.STATE_UNFINISHED;
        int numCraftable = Inventory.getNumberCreatable(itemID, state);
        craft(itemID, numCraftable >= 10 ? 10 : numCraftable);
    }

    public void craft100(View v) {
        Long itemID = (Long) mViewFlipper.getCurrentView().getTag();
        int state = ringsSelected ? Constants.STATE_NORMAL : Constants.STATE_UNFINISHED;
        int numCraftable = Inventory.getNumberCreatable(itemID, state);
        if (Setting.getSafeBoolean(Constants.SETTING_HANDLE_MAX)) {
            craft(itemID, numCraftable);
        } else {
            craft(itemID, numCraftable >= 100 ? 100 : numCraftable);
        }
    }

    public void brightenButtons() {
        craft1.setAlpha(1);
        craft10.setAlpha(1);
        craft100.setAlpha(1);
    }

    public void dimButtons() {
        craft1.setAlpha(0.3f);
        craft10.setAlpha(0.3f);
        craft100.setAlpha(0.3f);
    }

    public void calculatingComplete() {
        MainActivity.vh.anvilBusy = false;
        brightenButtons();
    }

    private void craft(Long itemID, int quantity) {
        int quantityCrafted = 0;
        List<Pair<Long, Integer>> itemsToAdd = new ArrayList<>();

        int canCreate = Inventory.canCreateBulkItem(itemID, ringsSelected ? Constants.STATE_NORMAL : Constants.STATE_UNFINISHED, quantity);
        if (MainActivity.vh.anvilBusy) {
            canCreate = Constants.ERROR_BUSY;
        } else if (canCreate == Constants.SUCCESS) {
            Inventory.removeItemIngredients(itemID, ringsSelected ? Constants.STATE_NORMAL : Constants.STATE_UNFINISHED, quantity);

            if (Super_Upgrade.isEnabled(Constants.SU_DOUBLE_ANVIL_CRAFTS)) {
                quantity = quantity * 2;
            }

            quantityCrafted = quantity;
            for (int i = 1; i <= quantity; i++) {
                itemsToAdd.add(new Pair<>(itemID, ringsSelected ? Constants.STATE_NORMAL : Constants.STATE_UNFINISHED));
            }
        }

        if (quantityCrafted > 0) {
            Item item = Item.findById(Item.class, itemID);
            SoundHelper.playSound(this, SoundHelper.smithingSounds);
            ToastHelper.showToast(findViewById(R.id.anvil), ToastHelper.SHORT, String.format(getString(R.string.craftSuccess), quantityCrafted, item.getFullName(this, ringsSelected ? Constants.STATE_NORMAL : Constants.STATE_UNFINISHED)), false);
            Player_Info.increaseByX(Player_Info.Statistic.ItemsCrafted, quantityCrafted);
            if (!ringsSelected) {
                GooglePlayHelper.UpdateEvent(Constants.EVENT_CREATE_UNFINISHED, quantityCrafted);
            }

            Pending_Inventory.addScheduledItems(this, Constants.LOCATION_ANVIL, itemsToAdd);
            MainActivity.vh.anvilBusy = true;
            dimButtons();
        } else {
            ToastHelper.showErrorToast(findViewById(R.id.anvil), ToastHelper.SHORT, getString(ErrorHelper.errors.get(canCreate)), false);
        }
    }

    public void goUpTier(View view) {
        int maxTier = ringsSelected ? Constants.TIER_GOLD : Constants.TIER_MAX;
        if (displayedTier < maxTier) {
            MainActivity.prefs.edit().putInt("anvilPosition", mViewFlipper.getDisplayedChild()).apply();
            displayedTier++;
            createAnvilInterface(true, false);
        }
    }

    public void goDownTier(View view) {
        int minTier = ringsSelected ? Constants.TIER_SILVER : Constants.TIER_MIN;
        if (displayedTier > minTier) {
            MainActivity.prefs.edit().putInt("anvilPosition", mViewFlipper.getDisplayedChild()).apply();
            displayedTier--;
            createAnvilInterface(true, false);
        }
    }

    public void toggleTab(View view) {
        MainActivity.prefs.edit().putInt("anvilTier", displayedTier).apply();
        ringsSelected = !ringsSelected;
        updateTabs();
        createAnvilInterface(true, true);
    }

    private void updateTabs() {
        if (ringsSelected) {
            (findViewById(R.id.itemsTab)).setAlpha(1f);
            (findViewById(R.id.ringsTab)).setAlpha(0.3f);
        } else {
            (findViewById(R.id.itemsTab)).setAlpha(0.3f);
            (findViewById(R.id.ringsTab)).setAlpha(1f);
        }
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Anvil);
        startActivity(intent);
    }

    private class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent startXY, MotionEvent finishXY, float velocityX, float velocityY) {
            gh.swipe(mViewFlipper, startXY, finishXY);

            dh.createCraftingInterface(
                    (RelativeLayout) findViewById(R.id.anvil),
                    (TableLayout) findViewById(R.id.ingredientsTable),
                    mViewFlipper,
                    ringsSelected ? Constants.STATE_NORMAL : Constants.STATE_UNFINISHED);

            HorizontalDots horizontalIndicator = (HorizontalDots) findViewById(R.id.horizontalIndicator);
            horizontalIndicator.addDots(dh, mViewFlipper.getChildCount(), mViewFlipper.getDisplayedChild());

            return super.onFling(startXY, finishXY, velocityX, velocityY);
        }
    }
}
