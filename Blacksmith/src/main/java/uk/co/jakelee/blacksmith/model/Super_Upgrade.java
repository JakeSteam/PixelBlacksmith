package uk.co.jakelee.blacksmith.model;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.TextHelper;

public class Super_Upgrade extends SugarRecord {
    private int superUpgradeId;
    private String name;
    private int prestigeLevel;
    private boolean enabled;

    public Super_Upgrade() {
    }

    public Super_Upgrade(int superUpgradeId, String name, int prestigeLevel, boolean enabled) {
        this.superUpgradeId = superUpgradeId;
        this.name = name;
        this.prestigeLevel = prestigeLevel;
        this.enabled = enabled;
    }

    public static boolean isEnabled(int superUpgradeId) {
        List<Super_Upgrade> upgrades = Select.from(Super_Upgrade.class).where(Condition.prop("super_upgrade_id").eq(superUpgradeId)).list();

        return upgrades.size() > 0 && upgrades.get(0).isEnabled();
    }

    public static Super_Upgrade find(int superUpgradeId) {
        return Select.from(Super_Upgrade.class).where(Condition.prop("super_upgrade_id").eq(superUpgradeId)).first();
    }

    public static int totalEnabled() {
        int totalEnabled = 0;
        List<Super_Upgrade> upgrades = Super_Upgrade.listAll(Super_Upgrade.class);
        for (Super_Upgrade upgrade : upgrades) {
            if (upgrade.isEnabled()) {
                totalEnabled++;
            }
        }

        return totalEnabled;
    }

    public static int maxEnabled() {
        if (Player_Info.getCollectionsCrafted() > Constants.MAX_SUPGRADES_ENABLED) {
            return Constants.MAX_SUPGRADES_ENABLED;
        }
        return Player_Info.getCollectionsCrafted();
    }

    public static int totalUnlocked() {
        int totalUnlocked = 0;
        List<Super_Upgrade> upgrades = Super_Upgrade.listAll(Super_Upgrade.class);
        for (Super_Upgrade upgrade : upgrades) {
            if (Player_Info.getPrestige() >= upgrade.getPrestigeLevel()) {
                totalUnlocked++;
            }
        }

        return totalUnlocked;
    }

    public static int total() {
        return Super_Upgrade.listAll(Super_Upgrade.class).size();
    }

    public int getSuperUpgradeId() {
        return superUpgradeId;
    }

    public void setSuperUpgradeId(int superUpgradeId) {
        this.superUpgradeId = superUpgradeId;
    }

    public String getName(Context context) {
        return TextHelper.getInstance(context).getText("su_" + superUpgradeId);
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrestigeLevel() {
        return prestigeLevel;
    }

    public void setPrestigeLevel(int prestigeLevel) {
        this.prestigeLevel = prestigeLevel;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean havePrestigeLevel() {
        return Player_Info.getPrestige() >= prestigeLevel;
    }
}

