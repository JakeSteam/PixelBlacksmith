package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Setting extends SugarRecord {
    long settingId;
    String settingName;
    boolean boolValue;
    int intValue;
    String strValue;

    public Setting() {
    }

    public Setting(long settingId, String settingName, boolean boolValue) {
        this.settingId = settingId;
        this.settingName = settingName;
        this.boolValue = boolValue;
        this.save();
    }

    public Setting(long settingId, String name, int intValue) {
        this.settingId = settingId;
        this.settingName = settingName;
        this.intValue = intValue;
        this.save();
    }

    public Setting(long settingId, String name, String strValue) {
        this.settingId = settingId;
        this.settingName = settingName;
        this.strValue = strValue;
        this.save();
    }

    public long getSettingId() {
        return settingId;
    }

    public void setSettingId(long settingId) {
        this.settingId = settingId;
    }

    public String getSettingName() {
        return settingName;
    }

    public void setSettingName(String settingName) {
        this.settingName = settingName;
    }

    public boolean getBoolValue() {
        return boolValue;
    }

    public void setBoolValue(boolean boolValue) {
        this.boolValue = boolValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public String getStrValue() {
        return strValue;
    }

    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }

}
