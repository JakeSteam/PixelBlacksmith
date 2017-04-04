package uk.co.jakelee.blacksmith.helper;

import java.util.HashMap;

import uk.co.jakelee.blacksmith.R;

public class ErrorHelper {
    public static final HashMap<Integer, Integer> errors = createErrorMap();

    private static HashMap<Integer, Integer> createErrorMap() {
        HashMap<Integer, Integer> errors = new HashMap<>();
        errors.put(Constants.ERROR_PLAYER_LEVEL, R.string.error_player_level);
        errors.put(Constants.ERROR_UNDISCOVERED, R.string.error_undiscovered);
        errors.put(Constants.ERROR_TRADER_RUN_OUT, R.string.error_trader_run_out);
        errors.put(Constants.ERROR_NOT_ENOUGH_INGREDIENTS, R.string.error_not_enough_ingredients);
        errors.put(Constants.ERROR_NOT_ENOUGH_COINS, R.string.error_not_enough_coins);
        errors.put(Constants.ERROR_NOT_ENOUGH_ITEMS, R.string.error_not_enough_items);
        errors.put(Constants.ERROR_NO_SPARE_SLOTS, R.string.error_no_spare_slots);
        errors.put(Constants.ERROR_NO_ITEMS, R.string.error_no_items);
        errors.put(Constants.ERROR_NO_GEMS, R.string.error_no_gems);
        errors.put(Constants.ERROR_MAXIMUM_UPGRADE, R.string.error_maximum_upgrade);
        errors.put(Constants.ERROR_NO_SLOTS_ENCHANTING, R.string.error_no_slots_enchanting);
        errors.put(Constants.ERROR_BUSY, R.string.error_busy);
        errors.put(Constants.ERROR_MAXIMUM_SUPER_UPGRADE, R.string.error_maximum_super_upgrade);
        errors.put(Constants.ERROR_VISITOR_IN_USE, R.string.error_visitor_in_use);
        errors.put(Constants.ERROR_RESOLVING_CONFLICT, R.string.error_resolving_conflict);
        errors.put(Constants.ERROR_UNSELLABLE, R.string.error_unsellable);
        errors.put(Constants.ERROR_MAX_LOCKED_TRADERS, R.string.error_max_locked_traders);

        return errors;
    }
}
