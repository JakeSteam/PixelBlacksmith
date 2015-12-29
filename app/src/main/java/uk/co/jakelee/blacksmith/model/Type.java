package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Type extends SugarRecord {
    Long id;
    String name;
    int category;

    public Type() {
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

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
