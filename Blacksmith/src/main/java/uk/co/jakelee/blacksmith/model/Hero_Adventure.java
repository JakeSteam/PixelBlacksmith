package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

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

    public int getAdventureId() {
        return adventureId;
    }

    public void setAdventureId(int adventureId) {
        this.adventureId = adventureId;
    }

    public String getName() {
        return name;
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
}
