package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import uk.co.jakelee.blacksmith.helper.SoundHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;

public class FurnaceActivity extends Activity {
    public static DisplayHelper dh;
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;
    private int numberOfItems;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furnace);
        dh = DisplayHelper.getInstance(getApplicationContext());

        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);

        createFurnaceInterface();
    }

    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
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
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, "Successfully added " + quantitySmelted + " item(s) to craft queue.");
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
        Animation slide_in_left = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_left);
        Animation slide_out_right = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        Animation slide_in_right = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out_left = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_left);

        @Override
        public boolean onFling(MotionEvent startXY, MotionEvent finishXY, float velocityX, float velocityY) {
            // Swipe left (next)
            if (startXY.getX() > finishXY.getX()) {
                mViewFlipper.setInAnimation(slide_in_right);
                mViewFlipper.setOutAnimation(slide_out_left);
                mViewFlipper.showNext();
                SoundHelper.playSound(getApplicationContext(), SoundHelper.transitionSounds);
            }

            // Swipe right (previous)
            if (startXY.getX() < finishXY.getX()) {
                mViewFlipper.setInAnimation(slide_in_left);
                mViewFlipper.setOutAnimation(slide_out_right);
                mViewFlipper.showPrevious();
                SoundHelper.playSound(getApplicationContext(), SoundHelper.transitionSounds);
            }

            View furnace = findViewById(R.id.furnace);
            dh.displayItemInfo((Long) mViewFlipper.getCurrentView().getTag(), Constants.STATE_NORMAL, furnace);

            TableLayout ingredientsTable = (TableLayout) findViewById(R.id.ingredientsTable);
            dh.createItemIngredientsTable((Long) mViewFlipper.getCurrentView().getTag(), Constants.STATE_NORMAL, ingredientsTable);

            HorizontalDots horizontalBar = (HorizontalDots) findViewById(R.id.horizontalIndicator);
            horizontalBar.addDots(dh, numberOfItems, mViewFlipper.getDisplayedChild());

            return super.onFling(startXY, finishXY, velocityX, velocityY);
        }
    }
}
