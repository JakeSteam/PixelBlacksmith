package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;

import java.util.List;

public class Recipe extends SugarRecord {
    Long id;
    Long item;
    int itemState;
    Long ingredient;
    int ingredientState;
    int quantity;

    public Recipe() {
    }

    public Recipe(Long item, int itemState, Long ingredient, int ingredientState, int quantity) {
        this.item = item;
        this.itemState = itemState;
        this.ingredient = ingredient;
        this.ingredientState = ingredientState;
        this.quantity = quantity;
        this.save();
    }

    public static List<Recipe> getIngredients(Long id, int state) {
        return Recipe.findWithQuery(Recipe.class, "SELECT * FROM recipe WHERE item = " + id + " AND item_state = " + state);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getItem() {
        return item;
    }

    public void setItem(Long item) {
        this.item = item;
    }

    public int getItemState() {
        return itemState;
    }

    public void setItemState(int itemState) {
        this.itemState = itemState;
    }

    public Long getIngredient() {
        return ingredient;
    }

    public void setIngredient(Long ingredient) {
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
