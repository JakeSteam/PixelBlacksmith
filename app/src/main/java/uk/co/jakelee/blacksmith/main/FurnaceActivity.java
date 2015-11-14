package uk.co.jakelee.blacksmith.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.List;

import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Recipe;
import uk.co.jakelee.blacksmith.sqlite.DatabaseHelper;

public class FurnaceActivity extends AppCompatActivity {
    public static DatabaseHelper dbh;
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furnace);
        dbh = new DatabaseHelper(getApplicationContext());

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

    public boolean createItem(int itemId) {
        if (dbh.canCreateItem(itemId)) {
            // Remove ingredients
            List<Recipe> ingredients = dbh.getIngredientsForItemById(itemId);
            for (Recipe ingredient : ingredients) {
                Inventory ownedItems = dbh.getInventoryByItem(ingredient.getIngredient());
                ownedItems.setQuantity(ownedItems.getQuantity() - ingredient.getQuantity());
                dbh.updateInventory(ownedItems);
            }

            // Add crafted item
            Inventory craftedItem = dbh.getInventoryByItem(itemId);
            craftedItem.setQuantity(craftedItem.getQuantity() + 1);
            dbh.updateInventory(craftedItem);
            return true;
        } else {
            return false;
        }
    }

    public void createFurnaceInterface() {
        ViewFlipper barSelector = (ViewFlipper) findViewById(R.id.viewFlipper);

        // Add all bars to the selector
        List<Item> bars = dbh.getItemsByType(2);
        for (Item bar : bars) {
            RelativeLayout barItem = new RelativeLayout(this);
            barItem.addView(createItemImage(bar.getId()));
            barItem.addView(createItemCount(bar.getId()));
            barSelector.addView(barItem);
        }
    }

    public ImageView createItemImage(int itemId) {
        int viewId = getResources().getIdentifier("img" + Integer.toString(itemId), "id", getPackageName());
        int drawableId = getResources().getIdentifier("item" + itemId, "drawable", getPackageName());

        Bitmap bMap = BitmapFactory.decodeResource(getResources(), drawableId);
        bMap = Bitmap.createScaledBitmap(bMap, 300, 230, true);
        Drawable imageResource = new BitmapDrawable(getResources(), bMap);

        ImageView image = new ImageView(this);
        image.setId(viewId);
        image.setTag(itemId);
        image.setImageDrawable(imageResource);
        /*image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int itemId = (int) v.getTag();

                Item item = dbh.getItemById(itemId);
                if (createItem(itemId)) {
                    Toast.makeText(getApplicationContext(), item.getName() + " created", Toast.LENGTH_SHORT).show();
                    updateItemViewById(itemId, true);
                } else {
                    Toast.makeText(getApplicationContext(), "Not enough materials", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
        return image;
    }

    public void updateItemViewById(int itemId, boolean updateIngredients) {
        int itemCountId = getResources().getIdentifier("text" + itemId, "id", getPackageName());
        TextView itemCount = (TextView) findViewById(itemCountId);

        // Update the item's count
        itemCount.setText(Integer.toString(dbh.getInventoryByItem(itemId).getQuantity()));

        if (updateIngredients) {
            List<Recipe> ingredients = dbh.getIngredientsForItemById(itemId);
            for (Recipe ingredient : ingredients) {
                int ingredientCountId = getResources().getIdentifier("text" + ingredient.getIngredient(), "id", getPackageName());
                TextView ingredientCount = (TextView) findViewById(ingredientCountId);

                // Update the ingredient's count
                ingredientCount.setText(Integer.toString(dbh.getInventoryByItem(ingredient.getIngredient()).getQuantity()));
            }
        }
    }

    public TextView createItemCount(int itemId) {
        int viewId = getResources().getIdentifier("text" + Integer.toString(itemId), "id", getPackageName());

        TextView text = new TextView(this);
        text.setId(viewId);
        text.setBackgroundColor(Color.BLACK);
        text.setTextColor(Color.WHITE);
        text.setText(" Have: " + Integer.toString(dbh.getInventoryByItem(itemId).getQuantity()) + " ");
        return text;
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

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
