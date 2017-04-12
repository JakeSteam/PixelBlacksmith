package uk.co.jakelee.blacksmith.model;

import android.content.Context;

import com.orm.SugarRecord;

import uk.co.jakelee.blacksmith.helper.TextHelper;

public class Setting extends SugarRecord {
    private long settingId;
    private String settingName;
    private boolean boolValue;
    private int intValue;
    private String strValue;

    public Setting() {
    }

    public Setting(long settingId, String settingName, boolean boolValue) {
        this.settingId = settingId;
        this.settingName = settingName;
        this.boolValue = boolValue;
    }

    public Setting(long settingId, String settingName, int intValue) {
        this.settingId = settingId;
        this.settingName = settingName;
        this.intValue = intValue;
    }

    public Setting(long settingId, String settingName, String strValue) {
        this.settingId = settingId;
        this.settingName = settingName;
        this.strValue = strValue;
    }

    public static boolean getSafeBoolean(long settingId) {
        Setting setting = Setting.findById(Setting.class, settingId);

        return setting != null && setting.getBoolValue();
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

    public String getName(Context context) {
        return TextHelper.getInstance(context).getText("setting_" + settingId);
    }
}
