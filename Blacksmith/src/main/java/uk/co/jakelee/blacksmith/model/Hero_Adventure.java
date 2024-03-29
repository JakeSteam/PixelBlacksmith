package uk.co.jakelee.blacksmith.model;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.helper.TextHelper;

public class Hero_Adventure extends SugarRecord {
    private int adventureId;
    private int subcategory;
    private String name;
    private int difficulty;
    private boolean completed;

    public Hero_Adventure() {
    }

    public Hero_Adventure(int adventureId, int subcategory, String name, int difficulty) {
        this.adventureId = adventureId;
        this.subcategory = subcategory;
        this.name = name;
        this.difficulty = difficulty;
        this.completed = false;
    }

    public static int getTotalCompleted() {
        List<Hero_Adventure> adventures = Hero_Adventure.listAll(Hero_Adventure.class);
        int totalCompleted = 0;

        for (Hero_Adventure adventure : adventures) {
            if (adventure.isCompleted()) totalCompleted++;
        }

        return totalCompleted;
    }

    public static Hero_Adventure getAdventure(int id) {
        return Select.from(Hero_Adventure.class).where(Condition.prop("adventure_id").eq(id)).first();
    }

    public int getAdventureId() {
        return adventureId;
    }

    public void setAdventureId(int adventureId) {
        this.adventureId = adventureId;
    }

    public String getName(Context context) {
        return TextHelper.getInstance(context).getText("hero_adventure_" + adventureId);
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(int subcategory) {
        this.subcategory = subcategory;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Hero_Category getSubcategoryObject() {
        return Select.from(Hero_Category.class).where(Condition.prop("category_id").eq(this.getSubcategory())).first();
    }
}
