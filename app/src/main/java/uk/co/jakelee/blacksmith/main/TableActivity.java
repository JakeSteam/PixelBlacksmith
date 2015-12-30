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

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.DatabaseHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;

public class TableActivity extends Activity {
    public static DatabaseHelper dbh;
    public static DisplayHelper dh;
    public int displayedTier = 1;
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        dbh = DatabaseHelper.getInstance(getApplicationContext());
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
        List<Item> items = dbh.getSmithableItems(3, 18, displayedTier, displayedTier);
        for (Item item : items) {
            RelativeLayout itemBox = new RelativeLayout(this);

            ImageView image = dh.createItemImage(item.getId(), 300, 230, item.getCanCraft());
            TextView count = dh.createItemCount(item.getId(), 2, Color.WHITE, Color.BLACK);
            count.setPadding(0, 150, 0, 0);

            itemBox.addView(image);
            itemBox.addView(count);
            itemBox.setTag(item.getId());
            itemSelector.addView(itemBox);
        }

        // Display item name and description
        displayItemInfo((Long) mViewFlipper.getCurrentView().getTag(), 1);

        // Display item ingredients
        TableLayout ingredientsTable = (TableLayout) findViewById(R.id.ingredientsTable);
        dh.createItemIngredientsTable((Long) mViewFlipper.getCurrentView().getTag(), 1, ingredientsTable);
    }

    public void closeTable(View view) {
        finish();
    }

    public void displayItemInfo(Long itemId, int state) {
        View table = findViewById(R.id.table);
        List<Item> items = Item.find(Item.class, "id = " + itemId);
        Item item = items.get(0);

        List<Inventory> invent2 = Inventory.listAll(Inventory.class);
        List<Inventory> inventories = Inventory.find(Inventory.class, "item = " + itemId + " AND state = " + state);
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

        if (item.getCanCraft() == 1) {
            itemName.setText(item.getName());
            itemDesc.setText(item.getDescription());
            itemCount.setText(Integer.toString(count.getQuantity()));
        } else {
            itemName.setText("???");
            itemDesc.setText("???");
            itemCount.setText("???");
        }

    }

    public void craft1(View v) {
        Long itemId = (Long) mViewFlipper.getCurrentView().getTag();

        Item item = Item.findById(Item.class, itemId);
        if (dbh.createItem(itemId, 1, 1, 5L)) {
            Toast.makeText(getApplicationContext(), item.getName() + " added to pending invent", Toast.LENGTH_SHORT).show();
            createTableInterface(false);
        } else {
            Toast.makeText(getApplicationContext(), "You cannot craft this", Toast.LENGTH_SHORT).show();
        }
    }

    public void goUpTier(View view) {
        if (displayedTier < 3) {
            displayedTier++;
            createTableInterface(true);
        }
    }

    public void goDownTier(View view) {
        if (displayedTier > 1) {
            displayedTier--;
            createTableInterface(true);
        }
    }

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // Swipe left (next)
            if (e1.getX() > e2.getX()) {
                mViewFlipper.showNext();
            }

            // Swipe right (previous)
            if (e1.getX() < e2.getX()) {
                mViewFlipper.showPrevious();
            }

            displayItemInfo((Long) mViewFlipper.getCurrentView().getTag(), 1);

            TableLayout ingredientsTable = (TableLayout) findViewById(R.id.ingredientsTable);
            dh.createItemIngredientsTable((Long) mViewFlipper.getCurrentView().getTag(), 1, ingredientsTable);

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
