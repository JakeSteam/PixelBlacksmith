package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;

import java.util.Locale;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;

public class EventHelper {
    public static void checkForEventItem(Activity activity) {
        if (VisitorHelper.getRandomBoolean(70)) {
            int state = VisitorHelper.getRandomNumber(Constants.STATE_RED, Constants.STATE_YELLOW);
            Item item = Item.findById(Item.class, 232);
            Inventory.addItem(item.getId(), state, 1, false);
            ToastHelper.showPositiveToast(null, ToastHelper.EXTRALONG, String.format(Locale.ENGLISH, activity.getString(R.string.eventChristmasItem),
                    item.getFullName(activity, state)), true);
        }
    }
}
