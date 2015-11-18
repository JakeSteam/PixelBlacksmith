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

public class AnvilActivity extends Activity {
    public static DatabaseHelper dbh;
    public static DisplayHelper dh;
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anvil);
        dbh = new DatabaseHelper(getApplicationContext());
        dh = new DisplayHelper(getApplicationContext());

        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        mViewFlipper.setInAnimation(this, android.R.anim.fade_in);
        mViewFlipper.setOutAnimation(this, android.R.anim.fade_out);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);

        createAnvilInterface();
    }

    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    public void createAnvilInterface() {
        ViewFlipper barSelector = (ViewFlipper) findViewById(R.id.viewFlipper);

        // Get all non-bar items that are also bronze
        List<Item> items = dbh.getItemsByTypeAndTier(3, 18, 1, 1);
        for (Item item : items) {
            RelativeLayout bronzeItem = new RelativeLayout(this);
            bronzeItem.setTag(item.getId());
            bronzeItem.addView(dh.CreateItemImage(item.getId(), 300, 230));
            bronzeItem.addView(dh.CreateItemCount(item.getId(), "Have: ", " ", Color.WHITE, Color.BLACK));
            barSelector.addView(bronzeItem);
        }

        // Display item name and description
        DisplayItemInfo((int) mViewFlipper.getCurrentView().getTag());

        // Display item ingredients
        TableLayout ingredientsTable = (TableLayout) findViewById(R.id.ingredientsTable);
        dh.CreateItemIngredientsTable((int) mViewFlipper.getCurrentView().getTag(), ingredientsTable);
    }

    public void CloseAnvil(View view) {
        finish();
    }

    public void DisplayItemInfo(int itemId) {
        View anvil = findViewById(R.id.anvil);
        Item item = dbh.getItemById(itemId);
        Inventory count = dbh.getInventoryByItem(itemId);

        TextView itemName = (TextView) findViewById(R.id.itemName);
        TextView itemDesc = (TextView) findViewById(R.id.itemDesc);
        TextView itemCount = (TextView) anvil.findViewWithTag(itemId + "Count");

        itemName.setText(item.getName());
        itemDesc.setText(item.getDescription());
        itemCount.setText("Have: " + Integer.toString(count.getQuantity()) + " ");
    }

    public void Smelt1(View v) {
        int itemId = (int) mViewFlipper.getCurrentView().getTag();

        Item item = dbh.getItemById(itemId);
        if (dbh.createItem(itemId)) {
            Toast.makeText(getApplicationContext(), item.getName() + " created", Toast.LENGTH_SHORT).show();
            createAnvilInterface();
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
