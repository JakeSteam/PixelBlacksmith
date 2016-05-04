package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.HorizontalDots;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ErrorHelper;
import uk.co.jakelee.blacksmith.helper.GestureHelper;
import uk.co.jakelee.blacksmith.helper.SoundHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.helper.TutorialHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;

public class TableActivity extends Activity {
    private static final Handler handler = new Handler();
    private static DisplayHelper dh;
    private static GestureHelper gh;
    private int displayedTier;
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        dh = DisplayHelper.getInstance(getApplicationContext());
        gh = new GestureHelper(getApplicationContext());
        displayedTier = MainActivity.prefs.getInt("tableTier", Constants.TIER_MIN);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        createTableInterface(true);

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
                handler.postDelayed(this, DateHelper.MILLISECONDS_IN_SECOND);
            }
        };
        handler.post(everySecond);
    }

    @Override
    public void onStop() {
        super.onStop();

        MainActivity.prefs.edit().putInt("tableTier", displayedTier).apply();
        MainActivity.prefs.edit().putInt("tablePosition", mViewFlipper.getDisplayedChild()).apply();
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

    private void createTableInterface(boolean clearExisting) {
        String[] parameters = {
                String.valueOf(Constants.TYPE_ANVIL_MIN),
                String.valueOf(Constants.TYPE_ANVIL_MAX),
                String.valueOf(Constants.TYPE_RING),
                String.valueOf(displayedTier)};
        List<Item> items = Item.find(Item.class, "(type BETWEEN ? AND ? OR type = ?) AND tier = ?", parameters, "", "level ASC", "");

        dh.createItemSelector(
                (ViewFlipper) findViewById(R.id.viewFlipper),
                clearExisting,
                items,
                Constants.STATE_NORMAL,
                MainActivity.prefs.getInt("tablePosition", 0));

        dh.createCraftingInterface(
                (RelativeLayout) findViewById(R.id.table),
                (TableLayout) findViewById(R.id.ingredientsTable),
                mViewFlipper,
                Constants.STATE_NORMAL);

        dh.drawArrows(this.displayedTier, Constants.TIER_TABLE_MIN, Constants.TIER_TABLE_MAX, findViewById(R.id.downButton), findViewById(R.id.upButton));

        HorizontalDots horizontalIndicator = (HorizontalDots) findViewById(R.id.horizontalIndicator);
        horizontalIndicator.addDots(dh, mViewFlipper.getChildCount(), mViewFlipper.getDisplayedChild());
    }

    public void craft1(View v) {
        Long itemID = (Long) mViewFlipper.getCurrentView().getTag();
        craft(itemID, 1);
    }

    public void craftMax(View v) {
        Long itemID = (Long) mViewFlipper.getCurrentView().getTag();
        craft(itemID, Constants.MAX_CRAFTS);
    }

    private void craft(Long itemID, int maxCrafts) {
        boolean successful = true;
        int quantityCrafted = 0;

        while (successful && quantityCrafted < maxCrafts) {
            int craftResponse = Inventory.tryCreateItem(itemID, Constants.STATE_NORMAL, Constants.LOCATION_TABLE);
            if (craftResponse != Constants.SUCCESS) {
                ToastHelper.showErrorToast(getApplicationContext(), Toast.LENGTH_SHORT, ErrorHelper.errors.get(craftResponse), false);
                successful = false;
            } else {
                quantityCrafted++;
            }
        }

        if (quantityCrafted > 0) {
            Item item = Item.findById(Item.class, itemID);
            SoundHelper.playSound(this, SoundHelper.smithingSounds);
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, String.format(getString(R.string.craftSuccess), quantityCrafted, item.getFullName(Constants.STATE_NORMAL)), false);
            Player_Info.increaseByX(Player_Info.Statistic.ItemsCrafted, quantityCrafted);
        }
    }

    public void goUpTier(View view) {
        if (displayedTier < Constants.TIER_TABLE_MAX) {
            MainActivity.prefs.edit().putInt("tablePosition", mViewFlipper.getDisplayedChild()).apply();
            displayedTier++;
            createTableInterface(true);
        }
    }

    public void goDownTier(View view) {
        if (displayedTier > Constants.TIER_TABLE_MIN) {
            MainActivity.prefs.edit().putInt("tablePosition", mViewFlipper.getDisplayedChild()).apply();
            displayedTier--;
            createTableInterface(true);
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
