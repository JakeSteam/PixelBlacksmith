package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Hero_Adventure extends SugarRecord {
    private int adventureId;
    private int category;
    private int subcategory;
    private int difficulty;
    private String name;

    public Hero_Adventure() {
    }

    public Hero_Adventure(int adventureId, int category, int subcategory, int difficulty, String name) {
        this.adventureId = adventureId;
        this.category = category;
        this.subcategory = subcategory;
        this.difficulty = difficulty;
        this.name = name;
    }

    public int getAdventureId() {
        return adventureId;
    }

    public void setAdventureId(int adventureId) {
        this.adventureId = adventureId;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
