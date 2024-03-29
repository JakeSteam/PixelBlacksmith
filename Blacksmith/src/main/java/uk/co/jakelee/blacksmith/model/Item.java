package uk.co.jakelee.blacksmith.model;

import android.content.Context;

import com.orm.SugarRecord;

import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.TextHelper;

public class Item extends SugarRecord {
    private Long id;
    private String name;
    private String description;
    private int type;
    private int tier;
    private int value;
    private int level;

    public Item() {
    }

    public Item(Long id, String name, String description, int type, int tier, int value, int level) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.tier = tier;
        this.value = value;
        this.level = level;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName(Context context) {
        return TextHelper.getInstance(context).getText("item_name_" + id);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription(Context context) {
        return TextHelper.getInstance(context).getText("item_desc_" + id);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getFullName(Context context, long state) {
        return getPrefix(state) + getName(context);
    }

    public String getPrefix(int id) {
        return getPrefix((long) id);
    }

    public String getPrefix(Long id) {
        State state = State.findById(State.class, id);
        return state.prefix;
    }

    public int getModifiedValue(int state) {
        return getModifiedValue((long) state);
    }

    public int getModifiedValue(Long itemState) {
        int itemValue = value;
        if (itemState >= Constants.STATE_ENCHANTED_MIN && itemState <= Constants.STATE_ENCHANTED_MAX) {
            State state = State.findById(State.class, itemState);
            Item initiatingItem = Item.findById(Item.class, state.getInitiatingItem());
            itemValue += initiatingItem.getValue();
        } else if (itemState == Constants.STATE_UNFINISHED) {
            itemValue = (int) (itemValue * Constants.STATE_UNFINISHED_MODIFIER);
        }

        return itemValue;
    }
}
