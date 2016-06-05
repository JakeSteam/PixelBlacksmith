package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.WorkerHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Worker;

public class FoodActivity extends Activity {
    private static DisplayHelper dh;
    private Worker worker;
    private long favouriteFood;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
        dh = DisplayHelper.getInstance(getApplicationContext());
        dh.updateFullscreen(this);

        Intent intent = getIntent();
        int workerID = (int) (long) intent.getLongExtra(WorkerHelper.INTENT_ID, 0);
        worker = Worker.findById(Worker.class, workerID);
        favouriteFood = worker.getFavouriteFood();

        populateFood();
    }

    private void populateFood() {
        TableLayout foodHolder = (TableLayout) findViewById(R.id.foodHolder);
        foodHolder.removeAllViews();
        TextView noFoodMessage = (TextView)findViewById(R.id.noFood);

        List<Inventory> foods = Inventory.findWithQuery(Inventory.class,
                "SELECT * " +
                "FROM inventory " +
                "INNER JOIN item on inventory.item = item.id " +
                "WHERE item.type IN (" + Constants.TYPE_FOOD + "," + Constants.TYPE_PROCESSED_FOOD + ") " +
                "AND quantity > 0 " +
                "ORDER BY value DESC");
        if (foods.size() > 0) {
            noFoodMessage.setVisibility(View.GONE);
            for (Inventory food : foods) {
                Item foodItem = Item.findById(Item.class, food.getItem());
                ImageView itemImage = dh.createItemImage(food.getItem(), 25, 25, true, true);
                TextView itemName = dh.createTextView(String.format(getString(R.string.genericQuantity),
                        food.getQuantity(),
                        foodItem.getName()), 26);
                ImageView selectImage = new ImageView(this);
                selectImage.setImageDrawable(dh.createDrawable(R.drawable.open, 35, 35));
                TextView itemBonus = createBonusTextView(foodItem, worker.isFavouriteFoodDiscovered());

                TableRow row = new TableRow(this);
                row.setTag(food.getItem());
                row.addView(itemImage);
                row.addView(itemName);
                row.addView(selectImage);
                row.addView(itemBonus);
                row.setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        selectFood(v);
                    }
                });
                foodHolder.addView(row);
            }
        } else {
            noFoodMessage.setVisibility(View.VISIBLE);
        }
    }

    public TextView createBonusTextView(Item foodItem, boolean favouriteDiscovered) {
        boolean isFavourite = foodItem.getId() == favouriteFood && favouriteDiscovered;
        String bonusText = "+" + (isFavourite ? 2 : 1) * foodItem.getValue() + "%";
        int bonusColour = isFavourite ? Color.parseColor("#267c18") : Color.BLACK;
        return dh.createTextView(bonusText, 22, bonusColour);
    }

    public void selectFood(View v) {
        long itemID = (long) v.getTag();

        if (worker.getFoodUsed() > 0) {
            Inventory oldItem = Inventory.getInventory(worker.getFoodUsed(), Constants.STATE_NORMAL);
            oldItem.setQuantity(oldItem.getQuantity() + 1);
            oldItem.save();
        }

        Inventory newItem = Inventory.getInventory(itemID, Constants.STATE_NORMAL);
        newItem.setQuantity(newItem.getQuantity() - 1);
        newItem.save();

        worker.setFoodUsed(itemID);
        worker.save();

        this.finish();
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Worker_Food);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
