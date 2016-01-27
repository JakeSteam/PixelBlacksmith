package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Character extends SugarRecord {
    Long id;
    String name;
    String description;
    String intro;
    String sold;

    public Character() {
    }

    public Character(Long id, String name, String description, String intro, String sold) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.intro = intro;
        this.sold = sold;
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

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getSold() {
        return sold;
    }

    public void setSold(String sold) {
        this.sold = sold;
    }
}
