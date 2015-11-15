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
import android.widget.TableLayout;
import android.widget.TableRow;
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

    public void createFurnaceInterface() {
        ViewFlipper barSelector = (ViewFlipper) findViewById(R.id.viewFlipper);

        // Add all bars to the selector
        List<Item> bars = dbh.getItemsByType(2);
        for (Item bar : bars) {
            RelativeLayout barItem = new RelativeLayout(this);
            barItem.setTag(bar.getId());
            barItem.addView(createItemImage(bar.getId(), 300, 230));
            barItem.addView(createItemCount(bar.getId(), "Have: ", " ", Color.WHITE, Color.BLACK));
            barSelector.addView(barItem);
        }

        // Display item name and description
        DisplayItemInfo((int) mViewFlipper.getCurrentView().getTag());

        // Display item ingredients
        DisplayItemIngredients((int) mViewFlipper.getCurrentView().getTag());
    }

    public ImageView createItemImage(int itemId, int width, int height) {
        int viewId = getResources().getIdentifier("img" + Integer.toString(itemId), "id", getPackageName());
        int drawableId = getResources().getIdentifier("item" + itemId, "drawable", getPackageName());

        Bitmap bMap = BitmapFactory.decodeResource(getResources(), drawableId);
        bMap = Bitmap.createScaledBitmap(bMap, width, height, true);
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

    public TextView createItemCount(int itemId, String prefix, String suffix, int textColour, int backColour) {
        int viewId = getResources().getIdentifier("text" + Integer.toString(itemId), "id", getPackageName());

        TextView text = new TextView(this);
        text.setId(viewId);
        text.setBackgroundColor(backColour);
        text.setTextColor(textColour);
        text.setText(prefix + Integer.toString(dbh.getInventoryByItem(itemId).getQuantity()) + suffix);
        return text;
    }

    private void DisplayItemInfo(int itemId) {
        Item item = dbh.getItemById(itemId);
        TextView itemName = (TextView) findViewById(R.id.itemName);
        TextView itemDesc = (TextView) findViewById(R.id.itemDesc);
        itemName.setText(item.getName());
        itemDesc.setText(item.getDescription());
    }

    private void DisplayItemIngredients(int itemId) {
        TableLayout ingredientsTable = (TableLayout) findViewById(R.id.ingredientsTable);
        List<Recipe> ingredients = dbh.getIngredientsForItemById(itemId);

        for (Recipe ingredient : ingredients) {
            Item item = dbh.getItemById(ingredient.getIngredient());
            Inventory owned = dbh.getInventoryByItem(ingredient.getId());
            TableRow row = new TableRow(this);
            TextView pic = new TextView(this);
            TextView name = new TextView(this);
            TextView need = new TextView(this);
            TextView have = new TextView(this);

            pic.setText("@");
            name.setText(item.getName());
            need.setText("Test");//need.setText(ingredient.getQuantity());
            have.setText("Again");//have.setText(owned.getQuantity());

            row.addView(pic);
            row.addView(name);
            row.addView(need);
            row.addView(have);

            ingredientsTable.addView(row);
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
            DisplayItemIngredients((int) mViewFlipper.getCurrentView().getTag());

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }
}
