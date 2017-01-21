package uk.co.jakelee.blacksmith.helper;

import java.util.HashMap;

public class ErrorHelper {
    public static final HashMap<Integer, String> errors = createErrorMap();

    private static HashMap<Integer, String> createErrorMap() {
        HashMap<Integer, String> errors = new HashMap<>();
        errors.put(Constants.ERROR_PLAYER_LEVEL, "This item requires a higher level!");
        errors.put(Constants.ERROR_UNDISCOVERED, "This item's recipe has not been discovered yet!");
        errors.put(Constants.ERROR_TRADER_RUN_OUT, "This trader has run out of stock for this item!");
        errors.put(Constants.ERROR_NOT_ENOUGH_INGREDIENTS, "The item cannot be created without all ingredients!");
        errors.put(Constants.ERROR_NOT_ENOUGH_COINS, "You don't have enough money to do that!");
        errors.put(Constants.ERROR_NOT_ENOUGH_ITEMS, "You don't have enough of these to sell!");
        errors.put(Constants.ERROR_NO_SPARE_SLOTS, "There are no slots currently available.");
        errors.put(Constants.ERROR_NO_ITEMS, "You cannot enchant an item you do not have!");
        errors.put(Constants.ERROR_NO_GEMS, "You cannot enchant an item using non-existent gems!");
        errors.put(Constants.ERROR_MAXIMUM_UPGRADE, "There's no further upgrades to purchase!");
        errors.put(Constants.ERROR_NO_SLOTS_ENCHANTING, "There's no gem table slots unlocked! The first one unlocks at level 10.");
        errors.put(Constants.ERROR_BUSY, "Currently calculating... please try again!");
        errors.put(Constants.ERROR_MAXIMUM_SUPER_UPGRADE, "No more super upgrades can be enabled! Create another collection to increase the maximum, or disable an enabled super upgrade.");
        errors.put(Constants.ERROR_VISITOR_IN_USE, "This visitor is already in use by another hero slot!");
        errors.put(Constants.ERROR_RESOLVING_CONFLICT, "Cloud save conflict detected! Resolving, please wait, this might take a while...");
        errors.put(Constants.ERROR_UNSELLABLE, "This item is marked as unsellable! Long press it to turn off.");
        errors.put(Constants.ERROR_MAX_LOCKED_TRADERS, "The maximum number of traders have already been locked!");

        return errors;
    }
}
