package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

public class Recipe extends SugarRecord {
    long item;
    long itemState;
    long ingredient;
    long ingredientState;
    int quantity;

    public Recipe() {
    }

    public Recipe(long item, long itemState, long ingredient, long ingredientState, int quantity) {
        this.item = item;
        this.itemState = itemState;
        this.ingredient = ingredient;
        this.ingredientState = ingredientState;
        this.quantity = quantity;
        this.save();
    }

    public static List<Recipe> getIngredients(Long id, long state) {
        return Select.from(Recipe.class).where(
                Condition.prop("item").eq(id),
                Condition.prop("item_state").eq(state)).list();
    }

    public long getItem() {
        return item;
    }

    public void setItem(long item) {
        this.item = item;
    }

    public long getItemState() {
        return itemState;
    }

    public void setItemState(long itemState) {
        this.itemState = itemState;
    }

    public long getIngredient() {
        return ingredient;
    }

    public void setIngredient(long ingredient) {
        this.ingredient = ingredient;
    }

    public long getIngredientState() {
        return ingredientState;
    }

    public void setIngredientState(long ingredientState) {
        this.ingredientState = ingredientState;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
