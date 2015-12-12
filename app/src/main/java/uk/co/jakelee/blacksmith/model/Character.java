package uk.co.jakelee.blacksmith.model;

public class Character {
    int id;
    int name;
    int description;
    int intro;
    int sold;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getDescription() {
        return description;
    }

    public void setDescription(int description) {
        this.description = description;
    }

    public int getIntro() {
        return intro;
    }

    public void setIntro(int intro) {
        this.intro = intro;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }
}
