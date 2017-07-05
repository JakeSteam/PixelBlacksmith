package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.WorkerHelper;
import uk.co.jakelee.blacksmith.model.Hero;
import uk.co.jakelee.blacksmith.model.Hero_Adventure;
import uk.co.jakelee.blacksmith.model.Hero_Category;
import uk.co.jakelee.blacksmith.model.Visitor_Type;

public class AdventureActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private static DisplayHelper dh;
    private Hero hero;
    private boolean haveLoadedSavedSubs = false;
    private int dropdownsToCreate = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adventure);
        dh = DisplayHelper.getInstance(getApplicationContext());

        Intent intent = getIntent();
        int heroID = intent.getIntExtra(WorkerHelper.INTENT_ID, 0);
        hero = Hero.findById(heroID);

        TextView totalStrength = (TextView) findViewById(R.id.totalStrengthMessage);
        int heroStrength = WorkerHelper.getTotalStrength(hero, Visitor_Type.findById(Visitor_Type.class, hero.getVisitorId()));
        totalStrength.setText(String.format(getString(R.string.heroTotalStrength), heroStrength));

        createCategoryDropdown(false);
        createCategoryDropdown(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        dh.updateFullscreen(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("uk.co.jakelee.blacksmith", MODE_PRIVATE);
        prefs.edit().putInt("adventureCategory", ((Spinner) findViewById(R.id.adventureCategories)).getSelectedItemPosition()).apply();
        prefs.edit().putInt("adventureSubcategory", ((Spinner) findViewById(R.id.adventureSubcategories)).getSelectedItemPosition()).apply();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (dropdownsToCreate > 0) {
            return;
        }
        if (parent.getTag().equals("CategorySelect")) {
            String selectedItem = (String) parent.getItemAtPosition(pos);
            if (!selectedItem.equals(getString(R.string.please_select))) {
                populateSubcategories(selectedItem);
            }
        } else if (parent.getTag().equals("SubcategorySelect")) {
            String selectedItem = (String) parent.getItemAtPosition(pos);
            populateAdventures(selectedItem);
        }
        dh.updateFullscreen(this);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        if (dropdownsToCreate > 0) {
            return;
        }
        populateAdventures(getString(R.string.please_select));
    }

    private void createCategoryDropdown(boolean subcategories) {
        Spinner categorySelector = (Spinner) findViewById(subcategories ? R.id.adventureSubcategories : R.id.adventureCategories);
        List<String> categories = getCategories(subcategories ? 999 : 0);
        categorySelector.getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner, categories);
        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        categorySelector.setAdapter(adapter);
        categorySelector.setOnItemSelectedListener(this);
        if (!subcategories) {
            categorySelector.setSelection(getSharedPreferences("uk.co.jakelee.blacksmith", MODE_PRIVATE).getInt("adventureCategory", 0), true);
        }
        dropdownsToCreate--;
    }

    private void populateSubcategories(String selectedCategory) {
        if (selectedCategory.equals(getString(R.string.please_select))) {
            (findViewById(R.id.selectCategoriesMessage)).setVisibility(View.VISIBLE);
        } else {
            (findViewById(R.id.selectCategoriesMessage)).setVisibility(View.GONE);
            Hero_Category category = Select.from(Hero_Category.class).where(Condition.prop("name").eq(selectedCategory)).first();

            Spinner categorySelector = (Spinner) findViewById(R.id.adventureSubcategories);
            List<String> categories = getCategories(category.getCategoryId());
            categorySelector.getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner, categories);
            adapter.setDropDownViewResource(R.layout.custom_spinner_item);
            categorySelector.setAdapter(adapter);
            categorySelector.setOnItemSelectedListener(this);
            if (!haveLoadedSavedSubs) {
                try {
                    categorySelector.setSelection(getSharedPreferences("uk.co.jakelee.blacksmith", MODE_PRIVATE).getInt("adventureSubcategory", 0), true);
                } catch (IndexOutOfBoundsException e) {
                    categorySelector.setSelection(0);
                    Toast.makeText(this, "An error occured! Please try reopening the interface, or try the hotfixes in the settings.", Toast.LENGTH_SHORT).show();
                }
                haveLoadedSavedSubs = true;

            }
        }
    }

    private List<String> getCategories(int categoryId) {
        List<String> categoryNames = new ArrayList<>();
        List<Hero_Category> categories = Select.from(Hero_Category.class).where(
                Condition.prop("parent").eq(categoryId)).orderBy("name").list();

        categoryNames.add(getString(R.string.please_select));
        for (Hero_Category category : categories) {
            categoryNames.add(category.getName(this));
        }

        return categoryNames;
    }

    private void populateAdventures(String selection) {
        if (dropdownsToCreate > 0) {
            return;
        }

        TableLayout adventureHolder = (TableLayout) findViewById(R.id.adventureHolder);
        adventureHolder.removeAllViews();

        if (selection.equals(getString(R.string.please_select))) {
            findViewById(R.id.selectCategoriesMessage).setVisibility(View.VISIBLE);
            return;
        } else {
            findViewById(R.id.selectCategoriesMessage).setVisibility(View.GONE);
        }

        Hero_Category category = Select.from(Hero_Category.class).where(Condition.prop("name").eq(selection)).first();
        List<Hero_Adventure> adventures = new ArrayList<>();

        if (category != null) {
            adventures = Select.from(Hero_Adventure.class).where(Condition.prop("subcategory").eq(category.getCategoryId())).list();
        }

        TableRow titleRow = new TableRow(this);
        TextView adventureTitle = dh.createTextView(getString(R.string.adventure), 20);
        TextView difficultyTitle = dh.createTextView(getString(R.string.difficulty_short), 20);
        titleRow.addView(adventureTitle);
        titleRow.addView(difficultyTitle);
        adventureHolder.addView(titleRow);

        for (Hero_Adventure adventure : adventures) {
            TextView difficulty = dh.createTextView(Integer.toString(adventure.getDifficulty()), 30);
            TextView name = dh.createTextView(adventure.getName(this), 20);
            if (adventure.isCompleted()) {
                name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

            ImageView selectImage = new ImageView(this);
            selectImage.setImageDrawable(dh.createDrawable(R.drawable.open, 35, 35));

            TableRow row = new TableRow(this);
            row.setTag(R.id.adventure, adventure.getAdventureId());
            row.addView(name);
            row.addView(difficulty);
            row.addView(selectImage);
            row.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    selectAdventure(v);
                }
            });
            adventureHolder.addView(row);
        }
    }

    public void selectAdventure(View v) {
        int adventureId = (int) v.getTag(R.id.adventure);

        hero.setCurrentAdventure(adventureId);
        hero.save();

        this.finish();
    }

    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Hero_Adventures);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
