package uk.co.jakelee.blacksmith.model;

public class Recipe {
    int id;
    int item;
    int itemState;
    int ingredient;
    int ingredientState;
    int quantity;

    public Recipe() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public int getItemState() {
        return itemState;
    }

    public void setItemState(int itemState) {
        this.itemState = itemState;
    }

    public int getIngredient() {
        return ingredient;
    }

    public void setIngredient(int ingredient) {
        this.ingredient = ingredient;
    }

    public int getIngredientState() {
        return ingredientState;
    }

    public void setIngredientState(int ingredientState) {
        this.ingredientState = ingredientState;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
