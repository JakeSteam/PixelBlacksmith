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

import com.google.android.gms.games.Games;
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
import uk.co.jakelee.blacksmith.model.Achievement;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Pending_Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Setting;

public class TableActivity extends Activity {
    private static final Handler handler = new Handler();
    private static DisplayHelper dh;
    private static GestureHelper gh;
    private int displayedTier;
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;
    private TextView craft1;
    private TextView craft10;
    private TextView craft100;
    private boolean booksSelected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        dh = DisplayHelper.getInstance(getApplicationContext());
        dh.updateFullscreen(this);

        gh = new GestureHelper(getApplicationContext());
        displayedTier = MainActivity.prefs.getInt("tableTier", booksSelected ? Constants.TIER_NONE : Constants.TIER_MIN);
        if (displayedTier > Constants.TIER_MAX && displayedTier != Constants.TIER_PREMIUM) displayedTier = Constants.TIER_PREMIUM;
        booksSelected = MainActivity.prefs.getBoolean("tableTab", false);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        craft1 = (TextView) findViewById(R.id.craft1);
        craft10 = (TextView) findViewById(R.id.craft10);
        craft100 = (TextView) findViewById(R.id.craft100);

        createTableInterface(true, false);

        if (TutorialHelper.currentlyInTutorial && TutorialHelper.currentStage <= Constants.STAGE_10_TABLE) {
            startTutorial();
        }

        final Runnable everySecond = new Runnable() {
            @Override
            public void run() {
                dh.createCraftingInterface(
                        (RelativeLayout) findViewById(R.id.table),
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
                (RelativeLayout) findViewById(R.id.table),
                (TableLayout) findViewById(R.id.ingredientsTable),
                mViewFlipper,
                Constants.STATE_NORMAL);
    }

    @Override
    public void onStop() {
        super.onStop();

        MainActivity.prefs.edit().putInt("tableTier", displayedTier).apply();
        MainActivity.prefs.edit().putInt("tablePosition", mViewFlipper.getDisplayedChild()).apply();
        MainActivity.prefs.edit().putBoolean("tableTab", booksSelected).apply();
        handler.removeCallbacksAndMessages(null);
    }

    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    private void startTutorial() {
        TutorialHelper th = new TutorialHelper(Constants.STAGE_10_TABLE);
        th.addTutorial(this, findViewById(R.id.viewFlipper), R.string.tutorialTable, R.string.tutorialTableText, false);
        th.addTutorialRectangle(this, findViewById(R.id.ingredientsTable), R.string.tutorialTableIngredients, R.string.tutorialTableIngredientsText, false);
        th.addTutorialRectangle(this, findViewById(R.id.craft1), R.string.tutorialTableCraft, R.string.tutorialTableCraftText, true, Gravity.TOP);
        th.start(this);
    }

    private void createTableInterface(boolean clearExisting, boolean resetTier) {
        updateTabs();

        if (resetTier) {
            displayedTier = booksSelected ? Constants.TIER_NONE : Constants.TIER_MIN;
        }

        if (booksSelected) {
            createBooksInterface(clearExisting);
        } else {
            createItemsInterface(clearExisting);
        }

        if (Setting.getSafeBoolean(Constants.SETTING_HANDLE_MAX)) {
            craft100.setText(R.string.maxText);
        } else {
            craft100.setText(R.string.craft100Text);
        }
    }

    private void updateButtons() {
        if (MainActivity.vh.tableBusy) {
            dimButtons();
        } else {
            brightenButtons();
        }
    }

    private void createItemsInterface(boolean clearExisting) {
        List<Item> items = Select.from(Item.class).where(
                Condition.prop("type").gt(Constants.TYPE_ANVIL_MIN - 1),
                Condition.prop("type").lt(Constants.TYPE_ANVIL_MAX + 1),
                Condition.prop("tier").eq(displayedTier)).list();

        dh.createItemSelector(
                (ViewFlipper) findViewById(R.id.viewFlipper),
                (HorizontalDots) findViewById(R.id.horizontalIndicator),
                clearExisting,
                items,
                Constants.STATE_NORMAL,
                MainActivity.prefs.getInt("tablePosition", 0));

        dh.createCraftingInterface(
                (RelativeLayout) findViewById(R.id.table),
                (TableLayout) findViewById(R.id.ingredientsTable),
                mViewFlipper,
                Constants.STATE_NORMAL);

        dh.drawArrows(this.displayedTier, Constants.TIER_MIN, Constants.TIER_PREMIUM, findViewById(R.id.downButton), findViewById(R.id.upButton));

        HorizontalDots horizontalIndicator = (HorizontalDots) findViewById(R.id.horizontalIndicator);
        horizontalIndicator.addDots(dh, mViewFlipper.getChildCount(), mViewFlipper.getDisplayedChild());
    }

    private void createBooksInterface(boolean clearExisting) {
        List<Item> items = Select.from(Item.class).where(
                Condition.prop("type").eq(Constants.TYPE_BOOK)).orderBy("level").list();

        dh.createItemSelector(
                (ViewFlipper) findViewById(R.id.viewFlipper),
                (HorizontalDots) findViewById(R.id.horizontalIndicator),
                clearExisting,
                items,
                Constants.STATE_NORMAL,
                MainActivity.prefs.getInt("tablePosition", 0));

        dh.createCraftingInterface(
                (RelativeLayout) findViewById(R.id.table),
                (TableLayout) findViewById(R.id.ingredientsTable),
                mViewFlipper,
                Constants.STATE_NORMAL);

        dh.drawArrows(this.displayedTier, Constants.TIER_NONE, Constants.TIER_NONE, findViewById(R.id.downButton), findViewById(R.id.upButton));

        HorizontalDots horizontalIndicator = (HorizontalDots) findViewById(R.id.horizontalIndicator);
        horizontalIndicator.addDots(dh, mViewFlipper.getChildCount(), mViewFlipper.getDisplayedChild());
    }

    public void craft1(View v) {
        Long itemID = (Long) mViewFlipper.getCurrentView().getTag();
        craft(itemID, 1);
    }

    public void craft10(View v) {
        Long itemID = (Long) mViewFlipper.getCurrentView().getTag();
        int numCraftable = Inventory.getNumberCreatable(itemID, Constants.STATE_NORMAL);
        craft(itemID, numCraftable >= 10 ? 10 : numCraftable);
    }

    public void craft100(View v) {
        Long itemID = (Long) mViewFlipper.getCurrentView().getTag();
        int numCraftable = Inventory.getNumberCreatable(itemID, Constants.STATE_NORMAL);
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
        MainActivity.vh.tableBusy = false;
        brightenButtons();
    }

    private void craft(Long itemID, int quantity) {
        int quantityCrafted = 0;
        List<Pair<Long, Integer>> itemsToAdd = new ArrayList<>();

        int canCreate = Inventory.canCreateBulkItem(itemID, Constants.STATE_NORMAL, quantity);
        if (MainActivity.vh.tableBusy) {
            canCreate = Constants.ERROR_BUSY;
        } else if (canCreate == Constants.SUCCESS) {
            quantityCrafted = quantity;
            Inventory.removeItemIngredients(itemID, Constants.STATE_NORMAL, quantity);
            for (int i = 1; i <= quantity; i++) {
                itemsToAdd.add(new Pair<>(itemID, Constants.STATE_NORMAL));
            }

            if (itemID.equals(Constants.ITEM_THE_COLLECTION)) {
                Player_Info.increaseByOne(Player_Info.Statistic.CollectionsCreated);
                Achievement achievement = Select.from(Achievement.class).where(Condition.prop("name").eq("The Collector")).first();
                Games.Achievements.unlock(GooglePlayHelper.mGoogleApiClient, achievement.getRemoteID());
                GooglePlayHelper.UpdateLeaderboards(Constants.LEADERBOARD_COLLECTIONS, Player_Info.getCollectionsCrafted());
            }
        }

        if (quantityCrafted > 0) {
            Item item = Item.findById(Item.class, itemID);
            SoundHelper.playSound(this, SoundHelper.smithingSounds);
            ToastHelper.showToast(findViewById(R.id.table), ToastHelper.SHORT, String.format(getString(R.string.craftSuccess), quantityCrafted, item.getFullName(Constants.STATE_NORMAL)), false);
            Player_Info.increaseByX(Player_Info.Statistic.ItemsCrafted, quantityCrafted);
            if (!booksSelected) {
                GooglePlayHelper.UpdateEvent(Constants.EVENT_CREATE_FINISHED, quantityCrafted);
            }

            Pending_Inventory.addScheduledItems(this, Constants.LOCATION_TABLE, itemsToAdd);
            MainActivity.vh.tableBusy = true;
            dimButtons();
        } else {
            ToastHelper.showErrorToast(findViewById(R.id.table), ToastHelper.SHORT, ErrorHelper.errors.get(canCreate), false);
        }

    }

    public void goUpTier(View view) {
        int maxTier = booksSelected ? Constants.TIER_NONE : Constants.TIER_MAX;

        if (displayedTier == Constants.TIER_MAX) {
            MainActivity.prefs.edit().putInt("tablePosition", mViewFlipper.getDisplayedChild()).apply();
            displayedTier = Constants.TIER_PREMIUM;
            createTableInterface(true, false);
        } else if (displayedTier < maxTier) {
            MainActivity.prefs.edit().putInt("tablePosition", mViewFlipper.getDisplayedChild()).apply();
            displayedTier++;
            createTableInterface(true, false);
        }
    }

    public void goDownTier(View view) {
        int minTier = booksSelected ? Constants.TIER_NONE : Constants.TIER_MIN;

        if (displayedTier == Constants.TIER_PREMIUM) {
            MainActivity.prefs.edit().putInt("tablePosition", mViewFlipper.getDisplayedChild()).apply();
            displayedTier = Constants.TIER_MAX;
            createTableInterface(true, false);
        } else if (displayedTier > minTier) {
            MainActivity.prefs.edit().putInt("tablePosition", mViewFlipper.getDisplayedChild()).apply();
            displayedTier--;
            createTableInterface(true, false);
        }
    }

    public void toggleTab(View view) {
        MainActivity.prefs.edit().putInt("tablePosition", 0).apply();
        booksSelected = !booksSelected;
        updateTabs();
        createTableInterface(true, true);
    }

    private void updateTabs() {
        if (booksSelected) {
            (findViewById(R.id.itemsTab)).setAlpha(1f);
            (findViewById(R.id.ringsTab)).setAlpha(0.3f);
        } else {
            (findViewById(R.id.itemsTab)).setAlpha(0.3f);
            (findViewById(R.id.ringsTab)).setAlpha(1f);
        }
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Table);
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
                    (RelativeLayout) findViewById(R.id.table),
                    (TableLayout) findViewById(R.id.ingredientsTable),
                    mViewFlipper,
                    Constants.STATE_NORMAL);

            HorizontalDots horizontalIndicator = (HorizontalDots) findViewById(R.id.horizontalIndicator);
            horizontalIndicator.addDots(dh, mViewFlipper.getChildCount(), mViewFlipper.getDisplayedChild());

            return super.onFling(startXY, finishXY, velocityX, velocityY);
        }
    }
}
