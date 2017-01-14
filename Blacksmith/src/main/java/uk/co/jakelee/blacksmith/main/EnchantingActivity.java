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
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.HorizontalDots;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DateHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.ErrorHelper;
import uk.co.jakelee.blacksmith.helper.GestureHelper;
import uk.co.jakelee.blacksmith.helper.GooglePlayHelper;
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
    private boolean powderSelected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enchanting);
        dh = DisplayHelper.getInstance(getApplicationContext());
        dh.updateFullscreen(this);

        gh = new GestureHelper(getApplicationContext());
        displayedTier = MainActivity.prefs.getInt("enchantingTier", Constants.TIER_MIN);
        powderSelected = MainActivity.prefs.getBoolean("enchantingTab", false);

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        createInterface(true, false);

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
    public void onResume() {
        super.onResume();
        MainActivity.prefs.edit().putInt("enchantingPosition", mViewFlipper.getDisplayedChild()).apply();
        createInterface(true, false);
    }

    @Override
    public void onStop() {
        super.onStop();

        MainActivity.prefs.edit().putInt("enchantingTier", displayedTier).apply();
        MainActivity.prefs.edit().putInt("enchantingPosition", mViewFlipper.getDisplayedChild()).apply();
        MainActivity.prefs.edit().putBoolean("enchantingTab", powderSelected).apply();
    }

    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    private void createInterface(boolean clearExisting, boolean resetTier) {
        updateTabs();

        if (resetTier) {
            displayedTier = powderSelected ? Constants.TIER_NONE : Constants.TIER_MIN;
        }

        if (powderSelected) {
            createPowderInterface(clearExisting);
        } else {
            createEnchantingInterface(clearExisting);
        }
    }

    private void createEnchantingInterface(boolean clearExisting) {
        // Get all items that are of the correct tier
        List<Item> items = Select.from(Item.class).where(
                Condition.prop("type").gt(Constants.TYPE_ANVIL_MIN - 1),
                Condition.prop("type").lt(Constants.TYPE_ANVIL_MAX + 1),
                Condition.prop("tier").eq(displayedTier)).orderBy("level").list();

        dh.createItemSelector(
                (ViewFlipper) findViewById(R.id.viewFlipper),
                (HorizontalDots) findViewById(R.id.horizontalIndicator),
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
        TableLayout gemsTable = (TableLayout) findViewById(R.id.itemsTable);
        createGemsTable(gemsTable);

        dh.drawArrows(this.displayedTier, Constants.TIER_MIN, Constants.TIER_MAX, findViewById(R.id.downButton), findViewById(R.id.upButton));
    }

    private void createPowderInterface(boolean clearExisting) {
        List<Item> items = Select.from(Item.class).where(
                Condition.prop("type").eq(Constants.TYPE_POWDERS)).orderBy("level").list();

        dh.createItemSelector(
                (ViewFlipper) findViewById(R.id.viewFlipper),
                (HorizontalDots) findViewById(R.id.horizontalIndicator),
                clearExisting,
                items,
                Constants.STATE_NORMAL,
                MainActivity.prefs.getInt("enchantingPosition", 0));

        dh.createCraftingInterface(
                (RelativeLayout) findViewById(R.id.enchanting),
                (TableLayout) findViewById(R.id.itemsTable),
                mViewFlipper,
                Constants.STATE_NORMAL);

        dh.drawArrows(this.displayedTier, Constants.TIER_NONE, Constants.TIER_NONE, findViewById(R.id.downButton), findViewById(R.id.upButton));

        HorizontalDots horizontalIndicator = (HorizontalDots) findViewById(R.id.horizontalIndicator);
        horizontalIndicator.addDots(dh, mViewFlipper.getChildCount(), mViewFlipper.getDisplayedChild());
    }

    public void goUpTier(View view) {
        if (displayedTier < Constants.TIER_PREMIUM) {
            if (displayedTier == Constants.TIER_MAX) {
                displayedTier = Constants.TIER_PREMIUM;
            } else {
                displayedTier++;
            }
            MainActivity.prefs.edit().putInt("enchantingPosition", mViewFlipper.getDisplayedChild()).apply();
            createEnchantingInterface(true);
        }
    }

    public void goDownTier(View view) {
        if (displayedTier > Constants.TIER_MIN) {
            if (displayedTier == Constants.TIER_PREMIUM) {
                displayedTier = Constants.TIER_MAX;
            } else {
                displayedTier--;
            }
            MainActivity.prefs.edit().putInt("enchantingPosition", mViewFlipper.getDisplayedChild()).apply();
            createEnchantingInterface(true);
        }
    }

    public void toggleTab(View view) {
        MainActivity.prefs.edit().putInt("enchantingPosition", 0).apply();
        powderSelected = !powderSelected;
        updateTabs();
        createInterface(true, true);
    }

    private void updateTabs() {
        if (powderSelected) {
            (findViewById(R.id.enchantTab)).setAlpha(1f);
            (findViewById(R.id.powderTab)).setAlpha(0.3f);
            (findViewById(R.id.crushGem)).setVisibility(View.VISIBLE);
        } else {
            (findViewById(R.id.enchantTab)).setAlpha(0.3f);
            (findViewById(R.id.powderTab)).setAlpha(1f);
            (findViewById(R.id.crushGem)).setVisibility(View.GONE);
        }
    }

    private void createGemsTable(final TableLayout gemsTable) {
        gemsTable.removeAllViews();
        List<Item> allGems = Select.from(Item.class).where(
                Condition.prop("type").eq(Constants.TYPE_GEM)).list();

        List<TableRow> rows = new ArrayList<>();
        for (Item gem : allGems) {
            rows.add(createEnchantingButton(gem));
        }

        for (TableRow row : rows) {
            gemsTable.addView(row);
        }
    }

    private TableRow createEnchantingButton(Item item) {
        TableRow itemButton = new TableRow(getApplicationContext());
        itemButton.setBackgroundResource(R.drawable.button_extra_wide);
        itemButton.setGravity(Gravity.CENTER);

        ImageView itemImage = dh.createItemImage(item.getId(), 20, 20, true, true);
        itemImage.setPadding(dh.convertDpToPixel(16), 0, dh.convertDpToPixel(2), 0);
        itemButton.addView(itemImage);

        int quantityOwned = Inventory.getInventory(item.getId(), Constants.STATE_NORMAL).getQuantity();
        String itemName = item.getName();
        String buttonText = String.format(getString(R.string.enchantingButtonText), itemName, quantityOwned);

        int textSize = 30;
        itemButton.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    clickEnchantButton(v);
                }
            });
        TextView buttonTextView = dh.createTextView(buttonText, textSize);

        itemButton.addView(buttonTextView);
        itemButton.setTag(item.getId());

        return itemButton;
    }

    public void clickPowderButton(View v) {
        Long powderID = (Long) mViewFlipper.getCurrentView().getTag();
        Item powder = Item.findById(Item.class, powderID);

        int powderResponse = Inventory.tryPowderGem(powderID, Constants.STATE_NORMAL, Constants.LOCATION_ENCHANTING);
        if (powderResponse == Constants.SUCCESS) {
            SoundHelper.playSound(this, SoundHelper.enchantingSounds);
            ToastHelper.showToast(v, ToastHelper.SHORT, String.format(getString(R.string.powderAdd), powder.getName()), false);
            GooglePlayHelper.UpdateEvent(Constants.EVENT_CREATE_POWDER, 1);

            dh.createCraftingInterface(
                    (RelativeLayout) findViewById(R.id.enchanting),
                    (TableLayout) findViewById(R.id.itemsTable),
                    mViewFlipper,
                    Constants.STATE_NORMAL);
        } else {
            ToastHelper.showErrorToast(v, ToastHelper.SHORT, ErrorHelper.errors.get(powderResponse), false);
        }
    }

    public void clickEnchantButton(View v) {
        View enchantingItemInfo = findViewById(R.id.enchanting);

        Long itemId = (Long) mViewFlipper.getCurrentView().getTag();
        Long gemId = (Long) v.getTag();

        Item item = Item.findById(Item.class, itemId);
        Item gem = Item.findById(Item.class, gemId);

        int enchantResponse = Inventory.enchantItem(itemId, gemId, Constants.LOCATION_ENCHANTING);
        if (enchantResponse == Constants.SUCCESS) {
            SoundHelper.playSound(this, SoundHelper.enchantingSounds);
            ToastHelper.showToast(v, ToastHelper.SHORT, String.format(getString(R.string.enchantAdd),
                    gem.getName(),
                    item.getName()), false);
            Player_Info.increaseByOne(Player_Info.Statistic.ItemsEnchanted);
            GooglePlayHelper.UpdateEvent(Constants.EVENT_CREATE_ENCHANTED, 1);

            dh.displayItemInfo((Long) mViewFlipper.getCurrentView().getTag(), Constants.STATE_NORMAL, enchantingItemInfo);
            createGemsTable((TableLayout) findViewById(R.id.itemsTable));
        } else {
            ToastHelper.showErrorToast(v, ToastHelper.SHORT, ErrorHelper.errors.get(enchantResponse), false);
        }
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Gem_Table);
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

            if (powderSelected) {
                dh.createCraftingInterface(
                        (RelativeLayout) findViewById(R.id.enchanting),
                        (TableLayout) findViewById(R.id.itemsTable),
                        mViewFlipper,
                        Constants.STATE_NORMAL);
            }

            HorizontalDots horizontalBar = (HorizontalDots) findViewById(R.id.horizontalIndicator);
            horizontalBar.addDots(dh, mViewFlipper.getChildCount(), mViewFlipper.getDisplayedChild());

            return super.onFling(startXY, finishXY, velocityX, velocityY);
        }
    }
}
