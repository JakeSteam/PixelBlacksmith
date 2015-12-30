package uk.co.jakelee.blacksmith.model;

import android.widget.TextView;

import com.orm.SugarRecord;

import java.util.List;

import uk.co.jakelee.blacksmith.main.MainActivity;

public class Player_Info extends SugarRecord {
    Long id;
    String name;
    String textValue;
    int intValue;

    public Player_Info() {

    }

    public Player_Info(Long id, String name, String textValue, int intValue) {
        this.id = id;
        this.name = name;
        this.textValue = textValue;
        this.intValue = intValue;
    }

    public static int getPlayerLevel() {
        int xp = getXp();
        return convertXpToLevel(xp);
    }

    public static int convertXpToLevel(int xp) {
        return xp / 100;
    }

    public static int getXp() {
        List<Player_Info> xpInfos = Player_Info.find(Player_Info.class, "name = ?", "XP");
        Player_Info xpInfo = xpInfos.get(0);

        return xpInfo.getIntValue();
    }

    public static void addXp(int xp) {
        List<Player_Info> xpInfos = Player_Info.find(Player_Info.class, "name = ?", "XP");
        Player_Info xpInfo = xpInfos.get(0);
        xpInfo.setIntValue(xpInfo.getIntValue() + xp);
        xpInfo.save();
    }

    public static void updateLevelText() {
        TextView levelCount = MainActivity.level;
        levelCount.setText("Level" + Player_Info.getPlayerLevel() + " (" + Player_Info.getXp() + "xp)");
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

}
