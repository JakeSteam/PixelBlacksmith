package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ErrorHelper;
import uk.co.jakelee.blacksmith.helper.SoundHelper;
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
            TextViewPixel count = dh.createItemCount(item.getId(), Constants.STATE_NORMAL, Color.WHITE, Color.BLACK);
            count.setWidth(230);

            itemBox.addView(image);
            itemBox.addView(count, countParams);
            itemBox.setTag(item.getId());
            itemSelector.addView(itemBox);
        }

        // Display item name and description
        View enchanting = findViewById(R.id.enchanting);
        dh.displayItemInfo((Long) mViewFlipper.getCurrentView().getTag(), Constants.STATE_NORMAL, enchanting);

        // Display gem buttons
        LinearLayout gemsTable = (LinearLayout) findViewById(R.id.gemsTable);
        createGemsTable(gemsTable);

        // Sort out the tier arrows
        Button upArrow = (Button) findViewById(R.id.upButton);
        Button downArrow = (Button) findViewById(R.id.downButton);
        dh.drawArrows(this.displayedTier, Constants.TIER_MIN, Constants.TIER_MAX, downArrow, upArrow);
    }

    public void closePopup(View view) {
        finish();
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
                SoundHelper.playSound(getApplicationContext(), SoundHelper.transitionSounds);
            }

            // Swipe right (previous)
            if (startXY.getX() < finishXY.getX()) {
                mViewFlipper.setInAnimation(slide_in_left);
                mViewFlipper.setOutAnimation(slide_out_right);
                mViewFlipper.showPrevious();
                SoundHelper.playSound(getApplicationContext(), SoundHelper.transitionSounds);
            }

            View enchanting = findViewById(R.id.enchanting);
            dh.displayItemInfo((Long) mViewFlipper.getCurrentView().getTag(), Constants.STATE_NORMAL, enchanting);

            return super.onFling(startXY, finishXY, velocityX, velocityY);
        }
    }

    public void createGemsTable(final LinearLayout gemsTable) {
        gemsTable.removeAllViews();
        List<Item> allGems = Select.from(Item.class).where(
                Condition.prop("type").eq(Constants.TYPE_GEMS)).list();

        for (final Item gem : allGems) {
            Inventory gemInventory = Inventory.getInventory(gem.getId(), Constants.STATE_NORMAL);

            LinearLayout gemButton = new LinearLayout(getApplicationContext());
            gemButton.setBackgroundResource(R.drawable.button_extra_wide);
            gemButton.setGravity(Gravity.CENTER);

            ImageView gemImage = dh.createItemImage(gem.getId(), 90, 90, 1);
            gemImage.setPadding(0, 0, 10, 0);
            gemButton.addView(gemImage);

            String buttonText = gem.getName() + " (x" + gemInventory.getQuantity() + ")";
            TextView buttonTextView = dh.createTextView(buttonText, 30);
            gemButton.addView(buttonTextView);
            gemButton.setTag(gem.getId());

            gemButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    clickEnchantButton(v);
                }
            });

            gemsTable.addView(gemButton);
        }
    }

    public void clickEnchantButton (View v) {
        LinearLayout gemsTable = (LinearLayout) findViewById(R.id.gemsTable);
        View enchantingItemInfo = findViewById(R.id.enchanting);

        Long itemId = (Long) mViewFlipper.getCurrentView().getTag();
        Long gemId = (Long) v.getTag();

        int enchantResponse = Inventory.enchantItem(itemId, gemId, Constants.LOCATION_ENCHANTING);
        if (enchantResponse == Constants.SUCCESS) {
            SoundHelper.playSound(this, SoundHelper.enchantingSounds);
            Toast.makeText(getApplicationContext(), R.string.enchantAdd, Toast.LENGTH_SHORT).show();

            dh.displayItemInfo((Long) mViewFlipper.getCurrentView().getTag(), Constants.STATE_NORMAL, enchantingItemInfo);
            createGemsTable(gemsTable);
        } else {
            Toast.makeText(getApplicationContext(), ErrorHelper.errors.get(enchantResponse), Toast.LENGTH_SHORT).show();
        }


    }
}
