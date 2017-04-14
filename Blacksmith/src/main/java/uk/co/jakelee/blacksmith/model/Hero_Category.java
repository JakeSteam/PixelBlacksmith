package uk.co.jakelee.blacksmith.model;

import android.content.Context;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import uk.co.jakelee.blacksmith.helper.TextHelper;

public class Hero_Category extends SugarRecord {
    private int categoryId;
    private String name;
    private int parent;

    public Hero_Category() {
    }

    public Hero_Category(int categoryId, String name, int parent) {
        this.categoryId = categoryId;
        this.name = name;
        this.parent = parent;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName(Context context) {
        return TextHelper.getInstance(context).getText("hero_category_" + categoryId);
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public Hero_Category getParentObject() {
        return Select.from(Hero_Category.class).where(Condition.prop("category_id").eq(this.parent)).first();
    }
}
