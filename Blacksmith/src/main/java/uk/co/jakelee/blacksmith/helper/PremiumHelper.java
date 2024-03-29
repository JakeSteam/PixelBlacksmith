package uk.co.jakelee.blacksmith.helper;

import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Super_Upgrade;
import uk.co.jakelee.blacksmith.model.Trader;
import uk.co.jakelee.blacksmith.model.Upgrade;

public class PremiumHelper {
    public static boolean payOutTax() {
        if (Player_Info.isPremium()) {
            int taxAmount = getTaxAmount();
            Inventory.addItem(Constants.ITEM_COINS, Constants.STATE_NORMAL, taxAmount);
            return true;
        }
        return false;
    }

    public static int getTaxAmount() {
        int taxAmount = Player_Info.getPlayerLevel() * Trader.outOfStockTraders();
        double multiplier = VisitorHelper.percentToMultiplier(Upgrade.getValue("Coins Bonus")) + (0.5 * Player_Info.getPrestige());
        int adjustedAmount = (int) (taxAmount * multiplier) + 1000;
        return (Super_Upgrade.isEnabled(Constants.SU_BONUS_GOLD) ? 2 : 1) * adjustedAmount;
    }
}
