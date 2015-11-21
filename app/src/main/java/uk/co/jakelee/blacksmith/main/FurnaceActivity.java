package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.List;

import uk.co.jakelee.blacksmith.helper.DatabaseHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;

public class FurnaceActivity extends Activity {
    public static DatabaseHelper dbh;
    public static DisplayHelper dh;
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furnace);
        dbh = new DatabaseHelper(getApplicationContext());
        dh = new DisplayHelper(getApplicationContext());

        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        mViewFlipper.setInAnimation(this, android.R.anim.fade_in);
        mViewFlipper.setOutAnimation(this, android.R.anim.fade_out);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);

        createFurnaceInterface();
    }

    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    public void createFurnaceInterface() {
        ViewFlipper barSelector = (ViewFlipper) findViewById(R.id.viewFlipper);

        // Add all bars to the selector
        List<Item> bars = dbh.getItemsByType(2, 2);
        for (Item bar : bars) {
            RelativeLayout barItem = new RelativeLayout(this);
            barItem.setTag(bar.getId());
            barItem.addView(dh.CreateItemImage(bar.getId(), 300, 230));
            barItem.addView(dh.CreateItemCount(bar.getId(), "Have: ", " ", Color.WHITE, Color.BLACK));
            barSelector.addView(barItem);
        }

        // Display item name and description
        DisplayItemInfo((int) mViewFlipper.getCurrentView().getTag());

        // Display item ingredients
        TableLayout ingredientsTable = (TableLayout) findViewById(R.id.ingredientsTable);
        dh.CreateItemIngredientsTable((int) mViewFlipper.getCurrentView().getTag(), ingredientsTable);
    }

    public void CloseFurnace(View view) {
        finish();
    }

    public void DisplayItemInfo(int itemId) {
        View furnace = findViewById(R.id.furnace);
        Item item = dbh.getItemById(itemId);
        Inventory count = dbh.getInventoryByItem(itemId);

        TextView itemName = (TextView) findViewById(R.id.itemName);
        TextView itemDesc = (TextView) findViewById(R.id.itemDesc);
        TextView itemCount = (TextView) furnace.findViewWithTag(itemId + "Count");

        itemName.setText(item.getName());
        itemDesc.setText(item.getDescription());
        itemCount.setText("Have: " + Integer.toString(count.getQuantity()) + " ");
    }

    public void Smelt1(View v) {
        int itemId = (int) mViewFlipper.getCurrentView().getTag();

        Item item = dbh.getItemById(itemId);
        if (dbh.createItem(itemId)) {
            Toast.makeText(getApplicationContext(), item.getName() + " created, +" + item.getValue() + "XP", Toast.LENGTH_SHORT).show();
            createFurnaceInterface();
        } else {
            Toast.makeText(getApplicationContext(), "Not enough materials", Toast.LENGTH_SHORT).show();
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

            DisplayItemInfo((int) mViewFlipper.getCurrentView().getTag());

            TableLayout ingredientsTable = (TableLayout) findViewById(R.id.ingredientsTable);
            dh.CreateItemIngredientsTable((int) mViewFlipper.getCurrentView().getTag(), ingredientsTable);

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
