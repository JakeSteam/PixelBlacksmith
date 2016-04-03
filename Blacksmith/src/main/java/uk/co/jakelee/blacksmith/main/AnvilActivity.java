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
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;

public class AnvilActivity extends Activity {
    public static DisplayHelper dh;
    public static GestureHelper gh;

    public int displayedTier = Constants.TIER_MIN;
    private int numberOfItems;
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anvil);
        dh = DisplayHelper.getInstance(getApplicationContext());
        gh = new GestureHelper(getApplicationContext());

        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);

        createAnvilInterface(false);
    }

    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    public void createAnvilInterface(boolean clearExisting) {
        List<Item> items = Select.from(Item.class).where(
                Condition.prop("type").gt(Constants.TYPE_ANVIL_MIN - 1),
                Condition.prop("type").lt(Constants.TYPE_ANVIL_MAX + 1),
                Condition.prop("tier").eq(displayedTier)).orderBy("level").list();
        numberOfItems = items.size();

        dh.createItemSelector(
                (ViewFlipper) findViewById(R.id.viewFlipper),
                clearExisting,
                items);

        dh.createCraftingInterface(
                (RelativeLayout) findViewById(R.id.anvil),
                (TableLayout) findViewById(R.id.ingredientsTable),
                (HorizontalDots) findViewById(R.id.horizontalIndicator),
                mViewFlipper,
                numberOfItems,
                Constants.STATE_UNFINISHED);

        dh.drawArrows(this.displayedTier, Constants.TIER_MIN, Constants.TIER_MAX, findViewById(R.id.downButton), findViewById(R.id.upButton));
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

    public void craft(Long itemID, int maxCrafts) {
        boolean successful = true;
        int quantityCrafted = 0;

        while (successful && quantityCrafted < maxCrafts) {
            int craftResponse = Inventory.tryCreateItem(itemID, Constants.STATE_UNFINISHED, Constants.LOCATION_ANVIL);
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
            createAnvilInterface(false);
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

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent startXY, MotionEvent finishXY, float velocityX, float velocityY) {
            gh.swipe(mViewFlipper, startXY, finishXY);

            dh.createCraftingInterface(
                    (RelativeLayout) findViewById(R.id.anvil),
                    (TableLayout) findViewById(R.id.ingredientsTable),
                    (HorizontalDots) findViewById(R.id.horizontalIndicator),
                    mViewFlipper,
                    numberOfItems,
                    Constants.STATE_UNFINISHED);

            return super.onFling(startXY, finishXY, velocityX, velocityY);
        }
    }
}
