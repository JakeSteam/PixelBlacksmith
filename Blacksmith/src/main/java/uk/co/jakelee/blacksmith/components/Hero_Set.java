package uk.co.jakelee.blacksmith.components;

public class Hero_Set {
    private String name;
    private String description;
    private int bonus;

    public Hero_Set(String name, String description, int bonus) {
        this.name = name;
        this.description = description;
        this.bonus = bonus;
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

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }
}
