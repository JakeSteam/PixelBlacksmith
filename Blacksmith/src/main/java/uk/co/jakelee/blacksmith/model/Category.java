package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Category extends SugarRecord {
    Long id;
    String name;
    String description;

    public Category() {
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
        this.save();
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
}
