package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

import uk.co.jakelee.blacksmith.helper.Constants;

public class Item extends SugarRecord {
    Long id;
    String name;
    String description;
    int type;
    int tier;
    int value;
    int level;
    int can_craft;

    public Item() {
    }

    public Item(Long id, String name, String description, int type, int tier, int value, int level, int can_craft) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.tier = tier;
        this.value = value;
        this.level = level;
        this.can_craft = can_craft;
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

    public String getDescription() {
        return description;
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

    public int getCanCraft() {
        return can_craft;
    }

    public void setCanCraft(int can_craft) {
        this.can_craft = can_craft;
    }

    public String getPrefix(int id) {
        return getPrefix(Long.valueOf(id));
    }

    public String getPrefix(Long id) {
        State state = State.findById(State.class, id);
        return state.prefix;
    }

    public int getModifiedValue(int state) {
        return getModifiedValue(Long.valueOf(state));
    }

    public int getModifiedValue(Long state) {
        if (state == Constants.STATE_UNFINISHED) {
            return (int) (value * Constants.STATE_UNFINISHED_MODIFIER);
        } else if (state >= Constants.STATE_ENCHANTED_MIN && state <= Constants.STATE_ENCHANTED_MAX) {
            return value + Constants.STATE_ENCHANTED_ADDER;
        }
        return value;
    }
}