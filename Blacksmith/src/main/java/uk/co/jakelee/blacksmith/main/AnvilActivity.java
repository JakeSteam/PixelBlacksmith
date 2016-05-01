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

import com.orm.query.Condition;
import com.orm.query.Select;

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

public class AnvilActivity extends Activity {
    private static final Handler handler = new Handler();
    private static DisplayHelper dh;
    private static GestureHelper gh;
    private int displayedTier;
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anvil);
        dh = DisplayHelper.getInstance(getApplicationContext());
        gh = new GestureHelper(getApplicationContext());
        displayedTier = MainActivity.prefs.getInt("anvilTier", Constants.TIER_MIN);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        createAnvilInterface(true);

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
                        Constants.STATE_UNFINISHED);
                handler.postDelayed(this, DateHelper.MILLISECONDS_IN_SECOND);
            }
        };
        handler.post(everySecond);
    }

    @Override
    public void onStop() {
        super.onStop();

        MainActivity.prefs.edit().putInt("anvilTier", displayedTier).apply();
        MainActivity.prefs.edit().putInt("anvilPosition", mViewFlipper.getDisplayedChild()).apply();
        handler.removeCallbacksAndMessages(null);
    }

    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    private void startTutorial() {
        TutorialHelper th = new TutorialHelper(Constants.STAGE_8_ANVIL);
        th.addTutorial(this, findViewById(R.id.viewFlipper), R.string.tutorialAnvil, R.string.tutorialAnvilText, false);
        th.addTutorial(this, findViewById(R.id.upButton), R.string.tutorialAnvilUp, R.string.tutorialAnvilUpText, false);
        th.addTutorialRectangle(this, findViewById(R.id.ingredientsTable), R.string.tutorialAnvilIngredients, R.string.tutorialAnvilIngredientsText, false);
        th.addTutorialRectangle(this, findViewById(R.id.craft1), R.string.tutorialAnvilCraft, R.string.tutorialAnvilCraftText, true, Gravity.TOP);
        th.start(this);
    }

    private void createAnvilInterface(boolean clearExisting) {
        List<Item> items = Select.from(Item.class).where(
                Condition.prop("type").gt(Constants.TYPE_ANVIL_MIN - 1),
                Condition.prop("type").lt(Constants.TYPE_ANVIL_MAX + 1),
                Condition.prop("tier").eq(displayedTier)).orderBy("level").list();

        dh.createItemSelector(
                (ViewFlipper) findViewById(R.id.viewFlipper),
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

    public void closePopup(View view) {
        finish();
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
            int craftResponse = Inventory.tryCreateItem(itemID, Constants.STATE_UNFINISHED, Constants.LOCATION_ANVIL);
            if (craftResponse != Constants.SUCCESS) {
                ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, ErrorHelper.errors.get(craftResponse), false);
                successful = false;
            } else {
                quantityCrafted++;
            }
        }

        if (quantityCrafted > 0) {
            Item item = Item.findById(Item.class, itemID);
            SoundHelper.playSound(this, SoundHelper.smithingSounds);
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, String.format(getString(R.string.craftSuccess), quantityCrafted, item.getFullName(Constants.STATE_UNFINISHED)), false);
        }
    }

    public void goUpTier(View view) {
        if (displayedTier < Constants.TIER_MAX) {
            displayedTier++;
            createAnvilInterface(true);
        }
    }

    public void goDownTier(View view) {
        if (displayedTier > Constants.TIER_MIN) {
            displayedTier--;
            createAnvilInterface(true);
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
                    Constants.STATE_UNFINISHED);

            HorizontalDots horizontalIndicator = (HorizontalDots) findViewById(R.id.horizontalIndicator);
            horizontalIndicator.addDots(dh, mViewFlipper.getChildCount(), mViewFlipper.getDisplayedChild());

            return super.onFling(startXY, finishXY, velocityX, velocityY);
        }
    }
}
