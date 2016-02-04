package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Setting extends SugarRecord {
    long settingId;
    boolean boolValue;
    int intValue;
    String strValue;

    public Setting() {
    }

    public Setting(long settingId, boolean boolValue) {
        this.settingId = settingId;
        this.boolValue = boolValue;
    }

    public Setting(long settingId, int intValue) {
        this.settingId = settingId;
        this.intValue = intValue;
    }

    public Setting(long settingId, String strValue) {
        this.settingId = settingId;
        this.strValue = strValue;
    }

    public long getSettingId() {
        return settingId;
    }

    public void setSettingId(long settingId) {
        this.settingId = settingId;
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
