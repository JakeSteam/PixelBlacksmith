package uk.co.jakelee.blacksmith.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.helper.WorkerHelper;
import uk.co.jakelee.blacksmith.model.Hero;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Visitor_Type;

public class EquipmentSelectActivity extends Activity {
    private static DisplayHelper dh;
    private Hero hero;
    private Visitor_Type vType;
    private String itemType;
    private String allowedTypes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipmentselect);
        dh = DisplayHelper.getInstance(getApplicationContext());

        Intent intent = getIntent();
        setAllowedTypes(intent.getStringExtra(WorkerHelper.INTENT_TYPE));
        hero = Hero.findById(intent.getIntExtra(WorkerHelper.INTENT_HERO, 0));
        vType = Visitor_Type.findById(Visitor_Type.class, hero.getVisitorId());
    }

    @Override
    public void onResume() {
        super.onResume();
        dh.updateFullscreen(this);

        populateEquipment();
    }

    private void setAllowedTypes(String type) {
        itemType = type;
        switch (type) {
            case ("food"):
                allowedTypes = "21, 27";
                break;
            case ("helmet"):
                allowedTypes = "11, 12";
                break;
            case ("armour"):
                allowedTypes = "9, 10";
                break;
            case ("weapon"):
                allowedTypes = "3, 4, 5, 6, 15, 16, 18";
                break;
            case ("shield"):
                allowedTypes = "7, 8";
                break;
            case ("gloves"):
                allowedTypes = "14";
                break;
            case ("boots"):
                allowedTypes = "13";
                break;
            case ("ring"):
                allowedTypes = "24";
                break;
        }
    }

    private void populateEquipment() {
        TableLayout equipmentTable = (TableLayout) findViewById(R.id.equipmentTable);
        List<TableRow> rowsToAdd = new ArrayList<>();
        List<Inventory> matchedItems = Inventory.findWithQuery(Inventory.class,
                "SELECT * " +
                        "FROM inventory " +
                        "INNER JOIN item on inventory.item = item.id " +
                        "WHERE item.type IN (" + allowedTypes + ") " +
                        "AND quantity > 0 " +
                        "ORDER BY tier, state ASC");

        

        for (Inventory inventory : matchedItems) {
            Item item = Item.findById(Item.class, inventory.getItem());

            ImageView itemImage = dh.createItemImage(item.getId(), (int)inventory.getState(), 35, 35, true, true);
            TextView itemName = dh.createTextView(item.getFullName(this, inventory.getState()), 20, Color.BLACK);

            TextViewPixel itemStrength = dh.createTextView("", 24, Color.BLACK);
            WorkerHelper.setStrengthText(vType, itemStrength, (int) (long) item.getId(), (int) inventory.getState(), true);

            ImageView itemSelect = new ImageView(this);
            itemSelect.setImageDrawable(dh.createDrawable(R.drawable.open, 35, 35));

            TableRow itemRow = new TableRow(this);
            itemRow.addView(itemImage);
            itemRow.addView(itemName);
            itemRow.addView(itemStrength);
            itemRow.addView(itemSelect);

            itemRow.setTag(R.id.itemID, inventory.getItem());
            itemRow.setTag(R.id.itemState, inventory.getState());
            itemRow.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    equipmentClick(v);
                }
            });
            rowsToAdd.add(itemRow);
        }

        if (rowsToAdd.size() > 0) {
            equipmentTable.removeAllViews();
            for (TableRow row : rowsToAdd) {
                equipmentTable.addView(row);
            }
        }
    }

    public void equipmentClick(View view) {
        int itemId = (int) (long) view.getTag(R.id.itemID);
        int itemState = (int) (long) view.getTag(R.id.itemState);

        Inventory inventory = Inventory.getInventory(itemId, itemState);
        inventory.setQuantity(inventory.getQuantity() - 1);
        inventory.save();

        switch (itemType) {
            case ("food"):
                if (hero.getFoodItem() > 0) {
                    Inventory.addItem((long) hero.getFoodItem(), hero.getFoodState(), 1);
                }
                hero.setFoodItem(itemId);
                hero.setFoodState(itemState);
                break;
            case ("helmet"):
                if (hero.getHelmetItem() > 0) {
                    Inventory.addItem((long) hero.getHelmetItem(), hero.getHelmetState(), 1);
                }
                hero.setHelmetItem(itemId);
                hero.setHelmetState(itemState);
                break;
            case ("armour"):
                if (hero.getArmourItem() > 0) {
                    Inventory.addItem((long) hero.getArmourItem(), hero.getArmourState(), 1);
                }
                hero.setArmourItem(itemId);
                hero.setArmourState(itemState);
                break;
            case ("weapon"):
                if (hero.getWeaponItem() > 0) {
                    Inventory.addItem((long) hero.getWeaponItem(), hero.getWeaponState(), 1);
                }
                hero.setWeaponItem(itemId);
                hero.setWeaponState(itemState);
                break;
            case ("shield"):
                if (hero.getShieldItem() > 0) {
                    Inventory.addItem((long) hero.getShieldItem(), hero.getShieldState(), 1);
                }
                hero.setShieldItem(itemId);
                hero.setShieldState(itemState);
                break;
            case ("gloves"):
                if (hero.getGlovesItem() > 0) {
                    Inventory.addItem((long) hero.getGlovesItem(), hero.getGlovesState(), 1);
                }
                hero.setGlovesItem(itemId);
                hero.setGlovesState(itemState);
                break;
            case ("boots"):
                if (hero.getBootsItem() > 0) {
                    Inventory.addItem((long) hero.getBootsItem(), hero.getBootsState(), 1);
                }
                hero.setBootsItem(itemId);
                hero.setBootsState(itemState);
                break;
            case ("ring"):
                if (hero.getRingItem() > 0) {
                    Inventory.addItem((long) hero.getRingItem(), hero.getRingState(), 1);
                }
                hero.setRingItem(itemId);
                hero.setRingState(itemState);
                break;
        }
        hero.save();

        finish();
    }


    public void openHelp(View view) {
        Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.INTENT_ID, HelpActivity.TOPICS.Hero_Equipment);
        startActivity(intent);
    }

    public void closePopup(View view) {
        finish();
    }
}
