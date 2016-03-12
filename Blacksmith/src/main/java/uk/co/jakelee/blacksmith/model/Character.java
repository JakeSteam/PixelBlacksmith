package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

public class Character extends SugarRecord {
    Long id;
    String name;
    String intro;

    public Character() {
    }

    public Character(Long id, String name, String intro) {
        this.id = id;
        this.name = name;
        this.intro = intro;
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

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }
}
