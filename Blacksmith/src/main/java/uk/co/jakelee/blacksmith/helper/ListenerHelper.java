package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;
import android.view.View;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.ItemTable;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;

public class ListenerHelper {
    public static View.OnLongClickListener getItemLongClick(final Activity activity, final ItemTable table) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                long itemId = (long) view.getTag(R.id.itemID);
                long itemState = (long) view.getTag(R.id.itemState);
                Inventory inventory = Inventory.getInventory(itemId, itemState);
                Item item = Item.findById(Item.class, inventory.getItem());

                ToastHelper.showPositiveToast(view, ToastHelper.SHORT, String.format(activity.getString(R.string.alert_marked_unsellable),
                        item.getFullName(activity, inventory.getState()),
                        inventory.isUnsellable() ? "" : "un"), true);
                inventory.setUnsellable(!inventory.isUnsellable());
                inventory.save();

                table.displayItemsTable();
                return true;
            }
        };
    }
}
