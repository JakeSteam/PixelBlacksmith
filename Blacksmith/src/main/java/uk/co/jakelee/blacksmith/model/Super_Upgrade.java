package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Super_Upgrade extends SugarRecord {
    private int superUpgradeId;
    private String name;
    private boolean enabled;

    public Super_Upgrade() {
    }

    public Super_Upgrade(int superUpgradeId, String name, boolean enabled) {
        this.superUpgradeId = superUpgradeId;
        this.name = name;
        this.enabled = enabled;
    }

    public int getSuperUpgradeId() {
        return superUpgradeId;
    }

    public void setSuperUpgradeId(int superUpgradeId) {
        this.superUpgradeId = superUpgradeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static boolean isEnabled(int superUpgradeId) {
        Super_Upgrade upgrade = Super_Upgrade.findById(Super_Upgrade.class, superUpgradeId);

        return upgrade != null && upgrade.isEnabled();
    }
}

