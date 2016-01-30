package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;

public class EnchantingActivity extends Activity {
    public static DisplayHelper dh;
    public int displayedTier = Constants.TIER_MIN;
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enchanting);
        dh = DisplayHelper.getInstance(getApplicationContext());

        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        mViewFlipper.setInAnimation(this, android.R.anim.slide_in_left);
        mViewFlipper.setOutAnimation(this, android.R.anim.slide_out_right);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);

        createEnchantingInterface(false);
    }

    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    public void createEnchantingInterface(boolean clearExisting) {
        ViewFlipper itemSelector = (ViewFlipper) findViewById(R.id.viewFlipper);

        RelativeLayout.LayoutParams countParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        countParams.setMargins(0, 180, 0, 0);

        // If we're switching tiers, we have to clear the selector first
        if (clearExisting) {
            itemSelector.removeAllViews();
        }

        // Get all items that are of the correct tier
        List<Item> items = Select.from(Item.class).where(
                Condition.prop("type").gt(Constants.TYPE_ANVIL_MIN - 1),
                Condition.prop("type").lt(Constants.TYPE_ANVIL_MAX + 1),
                Condition.prop("tier").eq(displayedTier)).orderBy("level").list();
        for (Item item : items) {
            RelativeLayout itemBox = new RelativeLayout(this);

            ImageView image = dh.createItemImage(item.getId(), 230, 230, item.getCanCraft());
            TextViewPixel count = dh.createItemCount(item.getId(), Constants.STATE_UNFINISHED, Color.WHITE, Color.BLACK);
            count.setWidth(230);

            itemBox.addView(image);
            itemBox.addView(count, countParams);
            itemBox.setTag(item.getId());
            itemSelector.addView(itemBox);
        }

        // Display item name and description
        View enchanting = findViewById(R.id.enchanting);
        dh.displayItemInfo((Long) mViewFlipper.getCurrentView().getTag(), Constants.STATE_UNFINISHED, enchanting);

        // Display gem buttons
        TableLayout gemsTable = (TableLayout) findViewById(R.id.gemsTable);
        dh.createGemsTable(gemsTable);

        // Sort out the tier arrows
        Button upArrow = (Button) findViewById(R.id.upButton);
        Button downArrow = (Button) findViewById(R.id.downButton);
        dh.drawArrows(this.displayedTier, Constants.TIER_MIN, Constants.TIER_MAX, downArrow, upArrow);
    }

    public void closePopup(View view) {
        finish();
    }

    public void smelt1(View v) {
        int quantity = 1;
        Long itemId = (Long) mViewFlipper.getCurrentView().getTag();

        if (Inventory.createItem(itemId, Constants.STATE_UNFINISHED, quantity, Constants.LOCATION_ANVIL)) {
            Toast.makeText(getApplicationContext(), R.string.craftAdd, Toast.LENGTH_SHORT).show();
            createEnchantingInterface(false);
        } else {
            Toast.makeText(getApplicationContext(), R.string.craftFailure, Toast.LENGTH_SHORT).show();
        }
    }

    public void goUpTier(View view) {
        if (displayedTier < Constants.TIER_MAX) {
            displayedTier++;
            createEnchantingInterface(true);
        }
    }

    public void goDownTier(View view) {
        if (displayedTier > Constants.TIER_MIN) {
            displayedTier--;
            createEnchantingInterface(true);
        }
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
            }

            // Swipe right (previous)
            if (startXY.getX() < finishXY.getX()) {
                mViewFlipper.setInAnimation(slide_in_left);
                mViewFlipper.setOutAnimation(slide_out_right);
                mViewFlipper.showPrevious();
            }

            View enchanting = findViewById(R.id.enchanting);
            dh.displayItemInfo((Long) mViewFlipper.getCurrentView().getTag(), Constants.STATE_UNFINISHED, enchanting);

            return super.onFling(startXY, finishXY, velocityX, velocityY);
        }
    }
}
