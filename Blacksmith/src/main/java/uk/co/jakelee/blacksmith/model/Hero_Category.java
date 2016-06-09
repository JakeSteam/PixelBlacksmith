package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Hero_Category extends SugarRecord {
    private int categoryId;
    private String name;
    private int parent;

    public Hero_Category() {
    }

    public Hero_Category(int categoryId, String name, int parent) {
        this.categoryId = categoryId;
        this.name = name;
        this.parent = parent;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }
}
