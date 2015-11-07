package uk.co.jakelee.blacksmith.model;

/**
 * Created by Jake on 07/11/2015.
 */
public class Type {
    int id;
    String name;
    int category;

    public Type() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
