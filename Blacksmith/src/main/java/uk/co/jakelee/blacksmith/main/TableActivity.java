package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
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

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.HorizontalDots;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ErrorHelper;
import uk.co.jakelee.blacksmith.helper.SoundHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;

public class TableActivity extends Activity {
    public static DisplayHelper dh;
    public int displayedTier = Constants.TIER_MIN;
    private int numberOfItems;
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        dh = DisplayHelper.getInstance(getApplicationContext());

        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        mViewFlipper.setInAnimation(this, android.R.anim.fade_in);
        mViewFlipper.setOutAnimation(this, android.R.anim.fade_out);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);

        createTableInterface(false);
    }

    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    public void createTableInterface(boolean clearExisting) {
        ViewFlipper itemSelector = (ViewFlipper) findViewById(R.id.viewFlipper);

        RelativeLayout.LayoutParams countParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        countParams.setMargins(0, dh.convertDpToPixel(60), 0, 0);

        // If we're switching tiers, we have to clear the selector first
        if (clearExisting) {
            itemSelector.removeAllViews();
        }

        // Get all items that are of the correct tier
        String[] parameters = {
                String.valueOf(Constants.TYPE_ANVIL_MIN),
                String.valueOf(Constants.TYPE_ANVIL_MAX),
                String.valueOf(Constants.TYPE_RING),
                String.valueOf(displayedTier)};
        List<Item> items = Item.find(Item.class, "(type BETWEEN ? AND ? OR type = ?) AND tier = ?", parameters, "", "level ASC", "");

        numberOfItems = items.size();
        for (Item item : items) {
            RelativeLayout itemBox = new RelativeLayout(this);

            ImageView image = dh.createItemImage(item.getId(), 80, 80, Inventory.haveSeen(item.getId(), Constants.STATE_NORMAL));
            TextViewPixel count = dh.createItemCount(item.getId(), Constants.STATE_UNFINISHED, Color.WHITE, Color.BLACK);
            count.setWidth(dh.convertDpToPixel(80));

            itemBox.addView(image);
            itemBox.addView(count, countParams);
            itemBox.setTag(item.getId());
            itemSelector.addView(itemBox);
        }

        // Horizontal selector
        int currentItemPosition = mViewFlipper.getDisplayedChild();
        HorizontalDots horizontalBar = (HorizontalDots) findViewById(R.id.horizontalIndicator);
        horizontalBar.addDots(dh, numberOfItems, currentItemPosition);

        // Display item name and description
        View table = findViewById(R.id.table);
        dh.displayItemInfo((Long) mViewFlipper.getCurrentView().getTag(), Constants.STATE_NORMAL, table);

        // Display item ingredients
        TableLayout ingredientsTable = (TableLayout) findViewById(R.id.ingredientsTable);
        dh.createItemIngredientsTable((Long) mViewFlipper.getCurrentView().getTag(), Constants.STATE_NORMAL, ingredientsTable);

        // Sort out the tier arrows
        Button upArrow = (Button) findViewById(R.id.upButton);
        Button downArrow = (Button) findViewById(R.id.downButton);
        dh.drawArrows(this.displayedTier, Constants.TIER_TABLE_MIN, Constants.TIER_TABLE_MAX, downArrow, upArrow);
    }

    public void craft1(View v) {
        Long itemID = (Long) mViewFlipper.getCurrentView().getTag();
        craft(itemID, 1);
    }

    public void craftMax(View v) {
        Long itemID = (Long) mViewFlipper.getCurrentView().getTag();
        craft(itemID, Constants.MAX_CRAFTS);
    }

    public void craft(Long itemID, int maxCrafts) {
        boolean successful = true;
        int quantityCrafted = 0;

        while (successful && quantityCrafted < maxCrafts) {
            int craftResponse = Inventory.tryCreateItem(itemID, Constants.STATE_NORMAL, Constants.LOCATION_TABLE);
            if (craftResponse != Constants.SUCCESS) {
                ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, ErrorHelper.errors.get(craftResponse));
                successful = false;
            } else {
                quantityCrafted++;
            }
        }

        if (quantityCrafted > 0) {
            SoundHelper.playSound(this, SoundHelper.smithingSounds);
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, "Successfully added " + quantityCrafted + " item(s) to craft queue.");
            Player_Info.increaseByX(Player_Info.Statistic.ItemsCrafted, quantityCrafted);
            createTableInterface(false);
        }
    }

    public void goUpTier(View view) {
        if (displayedTier < Constants.TIER_TABLE_MAX) {
            displayedTier++;
            createTableInterface(true);
        }
    }

    public void goDownTier(View view) {
        if (displayedTier > Constants.TIER_TABLE_MIN) {
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

            View table = findViewById(R.id.table);
            dh.displayItemInfo((Long) mViewFlipper.getCurrentView().getTag(), Constants.STATE_NORMAL, table);

            TableLayout ingredientsTable = (TableLayout) findViewById(R.id.ingredientsTable);
            dh.createItemIngredientsTable((Long) mViewFlipper.getCurrentView().getTag(), Constants.STATE_NORMAL, ingredientsTable);

            HorizontalDots horizontalBar = (HorizontalDots) findViewById(R.id.horizontalIndicator);
            horizontalBar.addDots(dh, numberOfItems, mViewFlipper.getDisplayedChild());

            return super.onFling(startXY, finishXY, velocityX, velocityY);
        }
    }
}
