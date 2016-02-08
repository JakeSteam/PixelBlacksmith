package uk.co.jakelee.blacksmith.model;

import android.widget.TextView;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.main.MainActivity;

public class Player_Info extends SugarRecord {
    Long id;
    String name;
    String textValue;
    int intValue;

    public Player_Info() {

    }

    public Player_Info(Long id, String name, String textValue) {
        this.id = id;
        this.name = name;
        this.textValue = textValue;
    }

    public Player_Info(Long id, String name, int intValue) {
        this.id = id;
        this.name = name;
        this.intValue = intValue;
    }

    public Player_Info(Long id, String name, String textValue, int intValue) {
        this.id = id;
        this.name = name;
        this.textValue = textValue;
        this.intValue = intValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public static int getPlayerLevel() {
        return convertXpToLevel(getXp());
    }

    public static int convertXpToLevel(int xp) {
        // Level = 0.25 * sqrt(xp)
        return (int) (Constants.LEVEL_MODIFIER * Math.sqrt(xp));
    }

    public static int convertLevelToXp(int level) {
        // XP = (Level / 0.25) ^ 2
        return (int) Math.pow(level / Constants.LEVEL_MODIFIER, 2);
    }

    public static int getXp() {
        Player_Info xpInfo = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("XP")).first();

        return xpInfo.getIntValue();
    }

    public static void addXp(int xp) {
        Player_Info xpInfo = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("XP")).first();

        xpInfo.setIntValue(xpInfo.getIntValue() + xp);
        xpInfo.save();
    }

    public static void updateLevelText() {
        TextView levelCount = MainActivity.level;
        levelCount.setText("Level" + Player_Info.getPlayerLevel() + " (" + Player_Info.getXp() + "xp)");
    }
}
