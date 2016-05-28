package uk.co.jakelee.blacksmith.helper;

import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Trader;
import uk.co.jakelee.blacksmith.model.Upgrade;

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
        int taxAmount = Player_Info.getPlayerLevelFromDB() * Trader.outOfStockTraders();
        double multiplier = VisitorHelper.percentToMultiplier(Upgrade.getValue("Coins Bonus")) + Player_Info.getPrestige();
        return (int) (taxAmount * multiplier);
    }
}
