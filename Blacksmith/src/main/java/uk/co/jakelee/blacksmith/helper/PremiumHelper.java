package uk.co.jakelee.blacksmith.helper;

import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;

public class PremiumHelper {
    public static boolean payOutTax() {
        if (Player_Info.isPremium()) {
            int taxAmount = getTaxAmount();
            Inventory.addItem(Constants.ITEM_COINS, Constants.STATE_NORMAL, taxAmount);
            return true;
        } else {
            return false;
        }
    }

    public static int getTaxAmount() {
        return Player_Info.getPlayerLevelFromDB() * Constants.TRADER_TAX_MULTIPLIER;
    }
}
