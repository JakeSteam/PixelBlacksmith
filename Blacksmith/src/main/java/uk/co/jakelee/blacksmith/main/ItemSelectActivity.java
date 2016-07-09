package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Setting;

public class ItemSelectActivity extends Activity {
    public static String INTENT_ID = "uk.co.jakelee.blacksmith.itemSelectID";
    private DisplayHelper dh;
    private List<Item> items;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemselect);
        dh = DisplayHelper.getInstance(getApplicationContext());
        dh.updateFullscreen(this);

        this.items = dh.itemSelectionItems;
        displayItemsList();
    }

    private void displayItemsList() {
        TableLayout itemsContainer = (TableLayout) findViewById(R.id.itemsTable);
        itemsContainer.removeAllViews();

        boolean onlyDisplayAvailable = Setting.getSafeBoolean(Constants.SETTING_ONLY_AVAILABLE);
        boolean enchantingOverride = dh.itemSelectionInventoryCheck;
        int itemIndex = 0;
        for (Item item : items) {
            if (!onlyDisplayAvailable ||
                    (onlyDisplayAvailable && !enchantingOverride && Inventory.canCreateBulkItem(item.getId(), dh.itemSelectionState, 1) == Constants.SUCCESS) ||
                    (onlyDisplayAvailable && enchantingOverride && Inventory.getInventory(item.getId(), dh.itemSelectionState).getQuantity() > 0)) {
                TableRow itemRow = createItemRow(item, itemIndex);
                itemsContainer.addView(itemRow);
            }
            itemIndex++;
        }
        updateIndicator();
    }

    public void toggleOnlyAvailable(View view) {
        Setting onlyAvailable = Setting.findById(Setting.class, Constants.SETTING_ONLY_AVAILABLE);
        if (onlyAvailable != null) {
            onlyAvailable.setBoolValue(!onlyAvailable.getBoolValue());
            onlyAvailable.save();

            displayItemsList();
        }
    }

    private void updateIndicator() {
        ImageView onlyAvailableIndicator = (ImageView) findViewById(R.id.onlyAvailableIndicator);

        Setting onlyAvailableSetting = Setting.findById(Setting.class, Constants.SETTING_ONLY_AVAILABLE);
        if (onlyAvailableSetting != null) {
            Drawable tick = dh.createDrawable(R.drawable.tick, 30, 30);
            Drawable cross = dh.createDrawable(R.drawable.cross, 30, 30);

            boolean onlyAvailableIndicatorValue = onlyAvailableSetting.getBoolValue();
            onlyAvailableIndicator.setImageDrawable(onlyAvailableIndicatorValue ? cross : tick);
        }
    }

    private TableRow createItemRow(Item item, int itemIndex) {
        // Get item owned count
        Inventory inventory = Select.from(Inventory.class).where(
                Condition.prop("item").eq(item.getId()),
                Condition.prop("state").eq(dh.itemSelectionState)).first();
        int numberOwned = 0;
        if (inventory != null) {
            numberOwned = inventory.getQuantity();
        }

        // Inflate layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View inflatedView = inflater.inflate(R.layout.custom_item_select, null);
        TableRow row = (TableRow) inflatedView.findViewById(R.id.row);

        row.setTag(itemIndex);
        TextViewPixel itemName = (TextViewPixel) row.findViewById(R.id.itemName);
        TextViewPixel itemCount = (TextViewPixel) row.findViewById(R.id.itemCount);
        ImageView itemImage = (ImageView) row.findViewById(R.id.itemImage);

        // Update info visibility
        boolean haveSeen = Inventory.haveSeen(item.getId(), dh.itemSelectionState);
        boolean canCreate = Inventory.haveLevelFor(item.getId());
        if (haveSeen) {
            itemName.setText(String.format("%s", item.getFullName(dh.itemSelectionState)));
            itemCount.setText(String.format("%d", numberOwned));
        } else if (canCreate) {
            itemName.setText(String.format("%s", item.getFullName(dh.itemSelectionState)));
            itemCount.setText(R.string.unknownText);
        } else {
            itemName.setText(R.string.unknownText);
            itemCount.setText(R.string.unknownText);
        }
        itemImage.setImageDrawable(dh.createItemImageDrawable(item.getId(), 40, 40, haveSeen, canCreate));

        return row;
    }

    public void clickItem(View view) {
        int position = (int)view.getTag();
        dh.itemSelectionFlipper.setDisplayedChild(position);
        dh.itemSelectionDots.addDots(dh, dh.itemSelectionFlipper.getChildCount(), dh.itemSelectionFlipper.getDisplayedChild());
        this.finish();
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Item_Picker);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
