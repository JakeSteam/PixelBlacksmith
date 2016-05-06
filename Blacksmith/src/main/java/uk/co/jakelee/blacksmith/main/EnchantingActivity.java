package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.HorizontalDots;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ErrorHelper;
import uk.co.jakelee.blacksmith.helper.GestureHelper;
import uk.co.jakelee.blacksmith.helper.SoundHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;

public class EnchantingActivity extends Activity {
    private static final Handler handler = new Handler();
    private static DisplayHelper dh;
    private static GestureHelper gh;
    private int displayedTier;
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enchanting);
        dh = DisplayHelper.getInstance(getApplicationContext());
        gh = new GestureHelper(getApplicationContext());
        displayedTier = MainActivity.prefs.getInt("enchantingTier", Constants.TIER_MIN);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        createEnchantingInterface(true);

        final Runnable everySecond = new Runnable() {
            @Override
            public void run() {
                View enchanting = findViewById(R.id.enchanting);
                dh.displayItemInfo((Long) mViewFlipper.getCurrentView().getTag(), Constants.STATE_NORMAL, enchanting);
                handler.postDelayed(this, DateHelper.MILLISECONDS_IN_SECOND);
            }
        };
        handler.post(everySecond);
    }

    @Override
    public void onStop() {
        super.onStop();

        MainActivity.prefs.edit().putInt("enchantingTier", displayedTier).apply();
        MainActivity.prefs.edit().putInt("enchantingPosition", mViewFlipper.getDisplayedChild()).apply();
    }

    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    private void createEnchantingInterface(boolean clearExisting) {
        // Get all items that are of the correct tier
        List<Item> items = Select.from(Item.class).where(
                Condition.prop("type").gt(Constants.TYPE_ANVIL_MIN - 1),
                Condition.prop("type").lt(Constants.TYPE_ANVIL_MAX + 1),
                Condition.prop("tier").eq(displayedTier)).orderBy("level").list();

        dh.createItemSelector(
                (ViewFlipper) findViewById(R.id.viewFlipper),
                clearExisting,
                items,
                Constants.STATE_NORMAL,
                MainActivity.prefs.getInt("enchantingPosition", 0));

        // Horizontal selector
        int currentItemPosition = mViewFlipper.getDisplayedChild();
        HorizontalDots horizontalBar = (HorizontalDots) findViewById(R.id.horizontalIndicator);
        horizontalBar.addDots(dh, mViewFlipper.getChildCount(), currentItemPosition);

        // Display item name and description
        View enchanting = findViewById(R.id.enchanting);
        dh.displayItemInfo((Long) mViewFlipper.getCurrentView().getTag(), Constants.STATE_NORMAL, enchanting);

        // Display gem buttons
        LinearLayout gemsTable = (LinearLayout) findViewById(R.id.gemsTable);
        createGemsTable(gemsTable);

        dh.drawArrows(this.displayedTier, Constants.TIER_MIN, Constants.TIER_MAX, findViewById(R.id.downButton), findViewById(R.id.upButton));
    }

    public void goUpTier(View view) {
        if (displayedTier < Constants.TIER_MAX) {
            MainActivity.prefs.edit().putInt("enchantingPosition", mViewFlipper.getDisplayedChild()).apply();
            displayedTier++;
            createEnchantingInterface(true);
        }
    }

    public void goDownTier(View view) {
        if (displayedTier > Constants.TIER_MIN) {
            MainActivity.prefs.edit().putInt("enchantingPosition", mViewFlipper.getDisplayedChild()).apply();
            displayedTier--;
            createEnchantingInterface(true);
        }
    }

    private void createGemsTable(final LinearLayout gemsTable) {
        gemsTable.removeAllViews();
        List<Item> allGems = Select.from(Item.class).where(
                Condition.prop("type").eq(Constants.TYPE_GEM)).list();

        List<Item> allPowders = Select.from(Item.class).where(
                Condition.prop("type").eq(Constants.TYPE_POWDERS)).list();

        for (Item gem : allGems) {
            gemsTable.addView(createEnchantingButton(gem));
        }

        for (Item powder : allPowders) {
            gemsTable.addView(createEnchantingButton(powder));
        }
    }

    private LinearLayout createEnchantingButton(Item item) {
        boolean displayInfo = Inventory.haveSeen(item.getId(), Constants.STATE_NORMAL) || item.getType() == Constants.TYPE_POWDERS;

        LinearLayout itemButton = new LinearLayout(getApplicationContext());
        itemButton.setBackgroundResource(R.drawable.button_extra_wide);
        itemButton.setGravity(Gravity.CENTER);

        ImageView itemImage = dh.createItemImage(item.getId(), 20, 20, displayInfo);
        itemImage.setPadding(0, 0, dh.convertDpToPixel(2), 0);
        itemButton.addView(itemImage);

        int quantityOwned = 0;
        String itemName = getString(R.string.unknownText);
        if (displayInfo) {
            quantityOwned = Inventory.getInventory(item.getId(), Constants.STATE_NORMAL).getQuantity();
            itemName = item.getName();
        }
        String buttonText = String.format(getString(R.string.enchantingButtonText), itemName, quantityOwned);

        int textSize = 18;
        if (item.getType() == Constants.TYPE_GEM) {
            itemButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    clickEnchantButton(v);
                }
            });
            textSize = 30;
        } else if (item.getType() == Constants.TYPE_POWDERS) {
            itemButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    clickPowderButton(v);
                }
            });
        }

        TextView buttonTextView = dh.createTextView(buttonText, textSize);

        itemButton.addView(buttonTextView);
        itemButton.setTag(item.getId());

        return itemButton;
    }

    private void clickPowderButton(View v) {

        Long powderID = (Long) v.getTag();
        Item powder = Item.findById(Item.class, powderID);

        int powderResponse = Inventory.tryPowderGem(powderID, Constants.STATE_NORMAL, Constants.LOCATION_ENCHANTING);
        if (powderResponse == Constants.SUCCESS) {
            SoundHelper.playSound(this, SoundHelper.enchantingSounds);
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, String.format(getString(R.string.powderAdd), powder.getName()), false);

            createGemsTable((LinearLayout) findViewById(R.id.gemsTable));
        } else {
            ToastHelper.showErrorToast(getApplicationContext(), Toast.LENGTH_SHORT, ErrorHelper.errors.get(powderResponse), false);
        }
    }

    private void clickEnchantButton(View v) {
        View enchantingItemInfo = findViewById(R.id.enchanting);

        Long itemId = (Long) mViewFlipper.getCurrentView().getTag();
        Long gemId = (Long) v.getTag();

        Item item = Item.findById(Item.class, itemId);
        Item gem = Item.findById(Item.class, gemId);

        int enchantResponse = Inventory.enchantItem(itemId, gemId, Constants.LOCATION_ENCHANTING);
        if (enchantResponse == Constants.SUCCESS) {
            SoundHelper.playSound(this, SoundHelper.enchantingSounds);
            ToastHelper.showToast(getApplicationContext(), Toast.LENGTH_SHORT, String.format(getString(R.string.enchantAdd),
                    item.getName(),
                    gem.getName()), false);
            Player_Info.increaseByOne(Player_Info.Statistic.ItemsEnchanted);

            dh.displayItemInfo((Long) mViewFlipper.getCurrentView().getTag(), Constants.STATE_NORMAL, enchantingItemInfo);
            createGemsTable((LinearLayout) findViewById(R.id.gemsTable));
        } else {
            ToastHelper.showErrorToast(getApplicationContext(), Toast.LENGTH_SHORT, ErrorHelper.errors.get(enchantResponse), false);
        }
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Enchanting);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }

    private class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent startXY, MotionEvent finishXY, float velocityX, float velocityY) {
            gh.swipe(mViewFlipper, startXY, finishXY);

            View enchanting = findViewById(R.id.enchanting);
            dh.displayItemInfo((Long) mViewFlipper.getCurrentView().getTag(), Constants.STATE_NORMAL, enchanting);

            HorizontalDots horizontalBar = (HorizontalDots) findViewById(R.id.horizontalIndicator);
            horizontalBar.addDots(dh, mViewFlipper.getChildCount(), mViewFlipper.getDisplayedChild());

            return super.onFling(startXY, finishXY, velocityX, velocityY);
        }
    }
}
