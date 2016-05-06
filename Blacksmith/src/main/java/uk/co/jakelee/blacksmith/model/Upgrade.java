package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.helper.Constants;

public class Upgrade extends SugarRecord {
    private String name;
    private String units;
    private int modifier;
    private int increment;
    private int minimum;
    private int maximum;
    private int current;

    public Upgrade() {
    }

    public Upgrade(String name, String units, int modifier, int increment, int minimum, int maximum, int current) {
        this.name = name;
        this.units = units;
        this.modifier = modifier;
        this.increment = increment;
        this.minimum = minimum;
        this.maximum = maximum;
        this.current = current;
    }

    public static int getValue(String name) {
        Upgrade upgrade = Select.from(Upgrade.class).where(
                Condition.prop("name").eq(name)).first();
        return upgrade.getCurrent();
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

    public int getIncrement() {
        return increment;
    }

    public void setIncrement(int increment) {
        this.increment = increment;
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
            return ((minimum - current) + increment) * modifier;
        } else {
            return ((current - minimum) + increment) * modifier;
        }
    }

    public static int getMaximumUpgrades() {
        int possibleUpgrades = 0;
        List<Upgrade> upgrades = Upgrade.listAll(Upgrade.class);
        for (Upgrade upgrade : upgrades) {
            if (upgrade.increases()) {
                possibleUpgrades += ((upgrade.getMaximum() - upgrade.getMinimum()) / upgrade.getIncrement());
            } else {
                possibleUpgrades += ((upgrade.getMinimum() - upgrade.getMaximum()) / upgrade.getIncrement());
            }
        }

        return possibleUpgrades;
    }

    public boolean increases() {
        return maximum > minimum;
    }

    public int tryUpgrade() {
        Inventory coins = Inventory.getInventory(Constants.ITEM_COINS, Constants.STATE_NORMAL);

        if (coins.getQuantity() < getUpgradeCost()) {
            return Constants.ERROR_NOT_ENOUGH_COINS;
        } else if (isAtMaximum()) {
            return Constants.ERROR_MAXIMUM_UPGRADE;
        } else {
            upgrade(coins);
            return Constants.SUCCESS;
        }
    }

    public boolean isAtMaximum() {
        return (minimum > maximum && current <= maximum) || (maximum > minimum && current >= maximum);
    }

    private void upgrade(Inventory coins) {
        coins.setQuantity(coins.getQuantity() - getUpgradeCost());
        coins.save();

        if (minimum > maximum) {
            // If lower values are better.
            setCurrent(getCurrent() - increment);
        } else {
            // If higher values are better.
            setCurrent(getCurrent() + increment);
        }
        save();
    }
}


