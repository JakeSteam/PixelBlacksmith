package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;

public class TableActivity extends Activity {
    public static DisplayHelper dh;
    public int displayedTier = Constants.TIER_MIN;
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

            ImageView image = dh.createItemImage(item.getId(), 300, 230, item.getCanCraft());
            TextView count = dh.createItemCount(item.getId(), Constants.STATE_UNFINISHED, Color.WHITE, Color.BLACK);
            count.setPadding(0, 150, 0, 0);

            itemBox.addView(image);
            itemBox.addView(count);
            itemBox.setTag(item.getId());
            itemSelector.addView(itemBox);
        }

        // Display item name and description
        displayItemInfo((Long) mViewFlipper.getCurrentView().getTag(), Constants.STATE_NORMAL);

        // Display item ingredients
        TableLayout ingredientsTable = (TableLayout) findViewById(R.id.ingredientsTable);
        dh.createItemIngredientsTable((Long) mViewFlipper.getCurrentView().getTag(), Constants.STATE_NORMAL, ingredientsTable);
    }

    public void closeTable(View view) {
        finish();
    }

    public void displayItemInfo(Long itemId, int state) {
        View table = findViewById(R.id.table);
        Item item = Item.findById(Item.class, itemId);
        List<Inventory> inventories = Select.from(Inventory.class).where(
                Condition.prop("item").eq(itemId),
                Condition.prop("state").eq(state)).list();

        Inventory count = new Inventory();
        if (inventories.size() > 0) {
            count = inventories.get(0);
        } else {
            count.setItem(itemId);
            count.setState(state);
            count.setQuantity(0);
        }

        TextView itemName = (TextView) findViewById(R.id.itemName);
        TextView itemDesc = (TextView) findViewById(R.id.itemDesc);
        TextView itemCount = (TextView) table.findViewWithTag(itemId + "Count");

        if (item.getCanCraft() == Constants.TRUE) {
            itemName.setText(item.getName());
            itemDesc.setText(item.getDescription());
            itemCount.setText(Integer.toString(count.getQuantity()));
        } else {
            itemName.setText(R.string.unknownText);
            itemDesc.setText(R.string.unknownText);
            itemCount.setText(R.string.unknownText);
        }

    }

    public void craft1(View v) {
        int quantity = 1;
        Long itemId = (Long) mViewFlipper.getCurrentView().getTag();

        if (Inventory.createItem(itemId, Constants.STATE_NORMAL, quantity, Constants.LOCATION_TABLE)) {
            Toast.makeText(getApplicationContext(), R.string.craftAdd, Toast.LENGTH_SHORT).show();
            createTableInterface(false);
        } else {
            Toast.makeText(getApplicationContext(), R.string.craftFailure, Toast.LENGTH_SHORT).show();
        }
    }

    public void goUpTier(View view) {
        if (displayedTier < Constants.TIER_MAX) {
            displayedTier++;
            createTableInterface(true);
        }
    }

    public void goDownTier(View view) {
        if (displayedTier > Constants.TIER_MIN) {
            displayedTier--;
            createTableInterface(true);
        }
    }

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent startXY, MotionEvent finishXY, float velocityX, float velocityY) {

            // Swipe left (next)
            if (startXY.getX() > finishXY.getX()) {
                mViewFlipper.showNext();
            }

            // Swipe right (previous)
            if (startXY.getX() < finishXY.getX()) {
                mViewFlipper.showPrevious();
            }

            displayItemInfo((Long) mViewFlipper.getCurrentView().getTag(), Constants.STATE_NORMAL);

            TableLayout ingredientsTable = (TableLayout) findViewById(R.id.ingredientsTable);
            dh.createItemIngredientsTable((Long) mViewFlipper.getCurrentView().getTag(), Constants.STATE_NORMAL, ingredientsTable);

            return super.onFling(startXY, finishXY, velocityX, velocityY);
        }
    }
}
