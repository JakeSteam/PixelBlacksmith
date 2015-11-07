package uk.co.jakelee.blacksmith.model;

/**
 * Created by Jake on 07/11/2015.
 */
public class Recipe {
    int id;
    int ingredient;
    int quantity;
    boolean required;

    public Recipe() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIngredient() {
        return ingredient;
    }

    public void setIngredient(int ingredient) {
        this.ingredient = ingredient;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
