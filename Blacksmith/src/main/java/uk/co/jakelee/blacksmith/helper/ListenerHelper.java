package uk.co.jakelee.blacksmith.helper;

import android.view.View;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.ItemTable;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;

public class ListenerHelper {
    public static View.OnLongClickListener getItemLongClick(final ItemTable activity) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                long itemId = (long)view.getTag(R.id.itemID);
                long itemState = (long)view.getTag(R.id.itemState);
                Inventory inventory = Inventory.getInventory(itemId, itemState);
                Item item = Item.findById(Item.class, inventory.getItem());

                ToastHelper.showPositiveToast(view, ToastHelper.SHORT, String.format("Marked %1$s as %2$ssellable!",
                        item.getFullName(inventory.getState()),
                        inventory.isUnsellable() ? "" : "un"), true);
                inventory.setUnsellable(!inventory.isUnsellable());
                inventory.save();

                activity.displayItemsTable();
                return true;
            }
        };
    }
}
