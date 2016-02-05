package uk.co.jakelee.blacksmith.helper;

import java.util.HashMap;

public class ErrorHelper {
    public static final HashMap<Integer, String> errors = createErrorMap();

    private static HashMap<Integer, String> createErrorMap() {
        HashMap<Integer, String> errors = new HashMap<>();
        errors.put(Constants.ERROR_PLAYER_LEVEL, "This item requires a higher level.");
        errors.put(Constants.ERROR_UNDISCOVERED, "This item's recipe has not been discovered yet.");
        errors.put(Constants.ERROR_SHOP_RUN_OUT, "This shop has run out of stock for this item.");
        errors.put(Constants.ERROR_NOT_ENOUGH_INGREDIENTS, "The item cannot be created without all ingredients.");
        errors.put(Constants.ERROR_NOT_ENOUGH_COINS, "You don't have enough money for this item!");
        errors.put(Constants.ERROR_NOT_ENOUGH_ITEMS,"You don't have any of these to sell!");
        errors.put(Constants.ERROR_NO_SPARE_SLOTS, "There are no slots currently available.");
        errors.put(Constants.ERROR_NO_ITEMS, "You cannot enchant an item you do not have!");
        errors.put(Constants.ERROR_NO_GEMS, "You cannot enchant an item using non-existent gems!");

        return errors;
    }
}
