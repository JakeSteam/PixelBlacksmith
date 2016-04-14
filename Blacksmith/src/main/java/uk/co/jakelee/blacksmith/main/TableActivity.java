package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
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
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ErrorHelper;
import uk.co.jakelee.blacksmith.helper.GestureHelper;
import uk.co.jakelee.blacksmith.helper.SoundHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;

public class TableActivity extends Activity {
    private static DisplayHelper dh;
    private static GestureHelper gh;
    private int displayedTier = Constants.TIER_MIN;
    private int numberOfItems;
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        dh = DisplayHelper.getInstance(getApplicationContext());
        gh = new GestureHelper(getApplicationContext());

        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);

        createTableInterface(false);
    }

    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    private void createTableInterface(boolean clearExisting) {
        String[] parameters = {
                String.valueOf(Constants.TYPE_ANVIL_MIN),
                String.valueOf(Constants.TYPE_ANVIL_MAX),
                String.valueOf(Constants.TYPE_RING),
                String.valueOf(displayedTier)};
        List<Item> items = Item.find(Item.class, "(type BETWEEN ? AND ? OR type = ?) AND tier = ?", parameters, "", "level ASC", "");

        numberOfItems = items.size();
        dh.createItemSelector(
                (ViewFlipper) findViewById(R.id.viewFlipper),
                clearExisting,
                items);

        dh.createCraftingInterface(
                (RelativeLayout) findViewById(R.id.table),
                (TableLayout) findViewById(R.id.ingredientsTable),
                (HorizontalDots) findViewById(R.id.horizontalIndicator),
                mViewFlipper,
                numberOfItems,
                Constants.STATE_NORMAL);

        dh.drawArrows(this.displayedTier, Constants.TIER_TABLE_MIN, Constants.TIER_TABLE_MAX, findViewById(R.id.downButton), findViewById(R.id.upButton));
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
                ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, ErrorHelper.errors.get(craftResponse));
                successful = false;
            } else {
                quantityCrafted++;
            }
        }

        if (quantityCrafted > 0) {
            SoundHelper.playSound(this, SoundHelper.smithingSounds);
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, String.format(getString(R.string.craftSuccess), quantityCrafted));
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

    private class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent startXY, MotionEvent finishXY, float velocityX, float velocityY) {
            gh.swipe(mViewFlipper, startXY, finishXY);

            dh.createCraftingInterface(
                    (RelativeLayout) findViewById(R.id.table),
                    (TableLayout) findViewById(R.id.ingredientsTable),
                    (HorizontalDots) findViewById(R.id.horizontalIndicator),
                    mViewFlipper,
                    numberOfItems,
                    Constants.STATE_NORMAL);

            return super.onFling(startXY, finishXY, velocityX, velocityY);
        }
    }
}
