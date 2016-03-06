package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

import uk.co.jakelee.blacksmith.helper.Constants;

public class Upgrade extends SugarRecord {
    String name;
    String units;
    int modifier;
    int minimum;
    int maximum;
    int current;

    public Upgrade() {
    }

    public Upgrade(String name, String units, int modifier, int minimum, int maximum, int current) {
        this.name = name;
        this.units = units;
        this.modifier = modifier;
        this.minimum = minimum;
        this.maximum = maximum;
        this.current = current;
        this.save();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public int getModifier() {
        return modifier;
    }

    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getUpgradeCost() {
        if (minimum > maximum) {
            return ((current - maximum) + 1) * modifier;
        } else {
            return ((current - minimum) + 1) * modifier;
        }
    }

    public int tryUpgrade() {
        Inventory coins = Inventory.getInventory(Constants.ITEM_COINS, Constants.STATE_NORMAL);

        if (coins.getQuantity() < getUpgradeCost()) {
            return Constants.ERROR_NOT_ENOUGH_COINS;
        } else if (minimum > maximum && current == maximum) {
            return Constants.ERROR_MAXIMUM_UPGRADE;
        } else if (maximum > minimum && current == maximum) {
            return Constants.ERROR_MAXIMUM_UPGRADE;
        } else {
            upgrade(coins);
            return Constants.SUCCESS;
        }
    }

    public void upgrade(Inventory coins) {
        coins.setQuantity(coins.getQuantity() - getUpgradeCost());
        coins.save();

        if (minimum > maximum) {
            // If lower values are better.
            setCurrent(getCurrent() - 1);
        } else {
            // If higher values are better.
            setCurrent(getCurrent() + 1);
        }
        save();
    }
}


