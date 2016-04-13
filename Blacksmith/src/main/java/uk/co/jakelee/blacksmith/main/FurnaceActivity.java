package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ErrorHelper;
import uk.co.jakelee.blacksmith.helper.GestureHelper;
import uk.co.jakelee.blacksmith.helper.SoundHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.helper.TutorialHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;

public class FurnaceActivity extends Activity {
    public static DisplayHelper dh;
    public static GestureHelper gh;
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;
    private int numberOfItems;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furnace);
        dh = DisplayHelper.getInstance(getApplicationContext());
        gh = new GestureHelper(getApplicationContext());

        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);

        createFurnaceInterface();

        if (TutorialHelper.currentlyInTutorial && TutorialHelper.currentStage <= Constants.STAGE_6_FURNACE) {
            startTutorial();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void startTutorial() {
        TutorialHelper th = new TutorialHelper(Constants.STAGE_6_FURNACE);
        th.addTutorial(this, findViewById(R.id.viewFlipper), R.string.tutorialFurnaceItems, R.string.tutorialFurnaceItemsText, false);
        th.addTutorialRectangle(this, findViewById(R.id.horizontalIndicator), R.string.tutorialFurnaceScroll, R.string.tutorialFurnaceScrollText, false);
        th.addTutorialRectangle(this, findViewById(R.id.ingredientsTable), R.string.tutorialFurnaceIngredients, R.string.tutorialFurnaceIngredientsText, false);
        th.addTutorial(this, findViewById(R.id.smelt1), R.string.tutorialFurnaceSmelt, R.string.tutorialFurnaceSmeltText, true, Gravity.TOP);
        th.start(this);
    }

    public void createFurnaceInterface() {
        List<Item> items = Select.from(Item.class).where(
                Condition.prop("type").eq(Constants.TYPE_BAR)).list();
        numberOfItems = items.size();

        dh.createItemSelector(
                (ViewFlipper) findViewById(R.id.viewFlipper),
                false,
                items);

        dh.createCraftingInterface(
                (RelativeLayout) findViewById(R.id.furnace),
                (TableLayout) findViewById(R.id.ingredientsTable),
                (HorizontalDots) findViewById(R.id.horizontalIndicator),
                mViewFlipper,
                numberOfItems,
                Constants.STATE_NORMAL);
    }

    public void smelt1(View v) {
        Long itemID = (Long) mViewFlipper.getCurrentView().getTag();
        smelt(itemID, 1);
    }

    public void smeltMax(View v) {
        Long itemID = (Long) mViewFlipper.getCurrentView().getTag();
        smelt(itemID, Constants.MAX_CRAFTS);
    }

    public void smelt(Long itemID, int maxCrafts) {
        boolean successful = true;
        int quantitySmelted = 0;

        while (successful && quantitySmelted < maxCrafts) {
            int smeltResponse = Inventory.tryCreateItem(itemID, Constants.STATE_NORMAL, Constants.LOCATION_FURNACE);
            if (smeltResponse != Constants.SUCCESS) {
                ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, ErrorHelper.errors.get(smeltResponse));
                successful = false;
            } else {
                quantitySmelted++;
            }
        }

        if (quantitySmelted > 0) {
            SoundHelper.playSound(this, SoundHelper.smithingSounds);
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, String.format(getString(R.string.craftSuccess), quantitySmelted));
            Player_Info.increaseByX(Player_Info.Statistic.ItemsSmelted, quantitySmelted);
            createFurnaceInterface();
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

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent startXY, MotionEvent finishXY, float velocityX, float velocityY) {
            gh.swipe(mViewFlipper, startXY, finishXY);

            dh.createCraftingInterface(
                    (RelativeLayout) findViewById(R.id.furnace),
                    (TableLayout) findViewById(R.id.ingredientsTable),
                    (HorizontalDots) findViewById(R.id.horizontalIndicator),
                    mViewFlipper,
                    numberOfItems,
                    Constants.STATE_NORMAL);

            return super.onFling(startXY, finishXY, velocityX, velocityY);
        }
    }
}
