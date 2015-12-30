package uk.co.jakelee.blacksmith.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.DatabaseHelper;
import uk.co.jakelee.blacksmith.helper.DisplayHelper;
import uk.co.jakelee.blacksmith.model.Category;
import uk.co.jakelee.blacksmith.model.Character;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Location;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Recipe;
import uk.co.jakelee.blacksmith.model.Shop;
import uk.co.jakelee.blacksmith.model.Shop_Stock;
import uk.co.jakelee.blacksmith.model.Slots;
import uk.co.jakelee.blacksmith.model.State;
import uk.co.jakelee.blacksmith.model.Tier;
import uk.co.jakelee.blacksmith.model.Type;

public class MainActivity extends AppCompatActivity {
    public static DatabaseHelper dbh;
    public static DisplayHelper dh;
    public static Handler handler = new Handler();

    public static TextView coins;
    public static TextView level;

    public static RelativeLayout sellingSlots;
    public static RelativeLayout furnaceSlots;
    public static RelativeLayout anvilSlots;
    public static RelativeLayout mineSlots;
    public static RelativeLayout tableSlots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbh = DatabaseHelper.getInstance(getApplicationContext());
        dh = DisplayHelper.getInstance(getApplicationContext());

        coins = (TextView) findViewById(R.id.coinCount);
        level = (TextView) findViewById(R.id.currentLevel);
        sellingSlots = (RelativeLayout) findViewById(R.id.slots_inventory);
        furnaceSlots = (RelativeLayout) findViewById(R.id.slots_furnace);
        anvilSlots = (RelativeLayout) findViewById(R.id.slots_anvil);
        mineSlots = (RelativeLayout) findViewById(R.id.slots_mine);
        tableSlots = (RelativeLayout) findViewById(R.id.slots_table);

        if (Item.listAll(Item.class).size() == 0) {
            execSQL();
        }

        dbh.updateCoinsGUI();
        dbh.updateLevelText();
        createSlots();
    }

    public void execSQL() {
        Category category1 = new Category(0L, "Unknown", "Item type category could not be found.");
        Category category2 = new Category(1L, "Crafting", "An item that can be used for crafting.");
        Category category3 = new Category(2L, "Weapon", "An item that can be used as a weapon.");
        Category category4 = new Category(3L, "Armour", "An item that can be used as armour.");
        Category category5 = new Category(4L, "Skill", "An item that can be used to train a skill.");
        category1.save();
        category2.save();
        category3.save();
        category4.save();
        category5.save();

        Character character = new Character(0L, "S. Keeper", "He looks a little bit shifty...", "Greetings! See anything you like?", "Remember, no refunds!");
        character.save();

        Inventory inventory = new Inventory(1L, 1, 101);
        Inventory inventory1 = new Inventory(2L, 1, 102);
        Inventory inventory2 = new Inventory(3L, 1, 303);
        Inventory inventory3 = new Inventory(4L, 1, 104);
        Inventory inventory4 = new Inventory(5L, 1, 105);
        Inventory inventory5 = new Inventory(6L, 1, 106);
        Inventory inventory6 = new Inventory(7L, 1, 107);
        Inventory inventory7 = new Inventory(8L, 1, 108);
        Inventory inventory8 = new Inventory(9L, 1, 109);
        Inventory inventory9 = new Inventory(10L, 1, 110);
        Inventory inventory10 = new Inventory(52L, 1, 1234);
        Inventory inventory11 = new Inventory(69L, 1, 10);
        Inventory inventory12 = new Inventory(70L, 1, 10);
        inventory.save();
        inventory1.save();
        inventory2.save();
        inventory3.save();
        inventory4.save();
        inventory5.save();
        inventory6.save();
        inventory7.save();
        inventory8.save();
        inventory9.save();
        inventory10.save();
        inventory11.save();
        inventory12.save();

        Player_Info info1 = new Player_Info(1L, "Name", "Jake", 0);
        Player_Info info2 = new Player_Info(2L, "XP", "", 1200);
        info1.save();
        info2.save();

        Item item1 = new Item(1L, "Copper ore", "A piece of copper ore.", 1, 10, 1, 0, 1);
        Item item2 = new Item(2L, "Tin ore", "A piece of tin ore.", 1, 10, 1, 0, 1);
        Item item3 = new Item(3L, "Coal", "A piece of coal.", 1, 10, 1, 0, 1);
        Item item4 = new Item(4L, "Iron ore", "A piece of iron ore.", 1, 10, 1, 0, 1);
        Item item5 = new Item(5L, "Mithril ore", "A piece of mithril ore.", 1, 10, 1, 0, 1);
        Item item6 = new Item(6L, "Adamantite ore", "A piece of adamantite ore.", 1, 10, 1, 0, 1);
        Item item7 = new Item(7L, "Runite ore", "A piece of runite ore.", 1, 10, 1, 0, 1);
        Item item8 = new Item(8L, "Gold ore", "A piece of gold ore.", 1, 10, 1, 0, 1);
        Item item9 = new Item(9L, "Silver ore", "A piece of silver ore.", 1, 10, 1, 0, 1);
        Item item10 = new Item(10L, "Dragonite ore", "A piece of dragonite ore.", 1, 10, 1, 0, 1);
        Item item11 = new Item(11L, "Bronze bar", "A fresh bar of bronze.", 2, 10, 3, 1, 1);
        Item item12 = new Item(12L, "Iron bar", "A fresh bar of iron.", 2, 10, 3, 5, 1);
        Item item13 = new Item(13L, "Steel bar", "A fresh bar of steel.", 2, 10, 3, 10, 1);
        Item item14 = new Item(14L, "Mithril bar", "A fresh bar of mithril.", 2, 10, 3, 20, 1);
        Item item15 = new Item(15L, "Adamant bar", "A fresh bar of adamant.", 2, 10, 3, 30, 1);
        Item item16 = new Item(16L, "Rune bar", "A fresh bar of rune.", 2, 10, 3, 40, 1);
        Item item17 = new Item(17L, "Silver bar", "A fresh bar of silver.", 2, 10, 3, 35, 1);
        Item item18 = new Item(18L, "Gold bar", "A fresh bar of gold.", 2, 10, 3, 45, 1);
        Item item19 = new Item(19L, "Dragon bar", "A fresh bar of dragon.", 2, 10, 3, 50, 1);
        Item item20 = new Item(20L, "Bronze dagger", "A blunt bronze dagger.", 3, 1, 5, 1, 1);
        Item item21 = new Item(21L, "Bronze sword", "A fairly blunt bronze sword.", 4, 1, 10, 1, 0);
        Item item22 = new Item(22L, "Bronze longsword", "A longer bronze sword.", 5, 1, 10, 1, 0);
        Item item23 = new Item(23L, "Bronze crossbow", "A crossbow with bronze elements.", 6, 1, 10, 1, 1);
        Item item24 = new Item(24L, "Bronze platelegs", "A set of bronze legs.", 7, 1, 10, 2, 1);
        Item item25 = new Item(25L, "Bronze plateskirt", "A set of bronze legs, with added drafts.", 8, 1, 10, 2, 1);
        Item item26 = new Item(26L, "Bronze chainbody", "A light set of bronze body armour.", 9, 1, 10, 2, 1);
        Item item27 = new Item(27L, "Bronze platebody", "A heavy set of bronze body armour.", 10, 1, 15, 3, 1);
        Item item28 = new Item(28L, "Bronze half helmet", "A no-frills approach to protection.", 11, 1, 10, 3, 1);
        Item item29 = new Item(29L, "Bronze full helmet", "A frills approach to protection.", 12, 1, 15, 4, 1);
        Item item30 = new Item(30L, "Bronze boots", "A set of boots for those that like the muddy look.", 13, 1, 5, 4, 1);
        Item item31 = new Item(31L, "Bronze gloves", "A set of gloves for those that like the muddy look.", 14, 1, 5, 4, 1);
        Item item32 = new Item(32L, "Bronze pickaxe", "A weak pickaxe for mining rocks.", 15, 1, 10, 4, 1);
        Item item33 = new Item(33L, "Bronze hatchet", "A weak axe for cutting down trees.", 16, 1, 10, 4, 1);
        Item item34 = new Item(34L, "Bronze fishing rod", "A weak rod for catching fish.", 17, 1, 10, 4, 1);
        Item item35 = new Item(35L, "Bronze hammer", "A weak hammer who misses his anvil.", 18, 1, 5, 4, 1);
        Item item36 = new Item(36L, "Iron dagger", "A blunt iron dagger.", 3, 2, 7, 5, 1);
        Item item37 = new Item(37L, "Iron sword", "A bluntish iron sword.", 4, 2, 14, 5, 1);
        Item item38 = new Item(38L, "Iron longsword", "A longer iron sword.", 5, 2, 14, 5, 1);
        Item item39 = new Item(39L, "Iron crossbow", "A crossbow with iron elements.", 6, 2, 14, 5, 1);
        Item item40 = new Item(40L, "Iron platelegs", "A set of iron legs.", 7, 2, 14, 6, 1);
        Item item41 = new Item(41L, "Iron plateskirt", "A set of iron legs, offering some protection.", 8, 2, 14, 6, 1);
        Item item42 = new Item(42L, "Iron chainbody", "A light set of iron body armour.", 9, 2, 14, 6, 1);
        Item item43 = new Item(43L, "Iron platebody", "A heavy set of iron body armour.", 10, 2, 21, 7, 1);
        Item item44 = new Item(44L, "Iron half helmet", "A tiny bit of frills approach to protection.", 11, 2, 14, 7, 1);
        Item item45 = new Item(45L, "Iron full helmet", "A very frilly approach to protection.", 12, 2, 21, 8, 1);
        Item item46 = new Item(46L, "Iron boots", "A set of boots for those that like the grey look.", 13, 2, 7, 8, 1);
        Item item47 = new Item(47L, "Iron gloves", "A set of gloves for those that like the grey look.", 14, 2, 7, 8, 1);
        Item item48 = new Item(48L, "Iron pickaxe", "A standard pickaxe for mining rocks.", 15, 2, 14, 9, 1);
        Item item49 = new Item(49L, "Iron hatchet", "A standard axe for cutting down trees.", 16, 2, 14, 9, 1);
        Item item50 = new Item(50L, "Iron fishing rod", "A standard rod for catching fish.", 17, 2, 14, 9, 1);
        Item item51 = new Item(51L, "Iron hammer", "A standard hammer who misses his anvil.", 18, 2, 7, 9, 1);
        Item item52 = new Item(52L, "Coins", "Coins! Glorious coins!", 19, 10, 1, 1, 1);
        Item item53 = new Item(53L, "Steel dagger", "An average steel dagger.", 3, 3, 14, 10, 1);
        Item item54 = new Item(54L, "Steel sword", "A slightly blunt steel sword.", 4, 3, 28, 10, 1);
        Item item55 = new Item(55L, "Steel longsword", "An even longer steel sword.", 5, 3, 28, 10, 1);
        Item item56 = new Item(56L, "Steel crossbow", "A crossbow with steel elements.", 6, 3, 28, 10, 0);
        Item item57 = new Item(57L, "Steel platelegs", "A set of steel legs.", 7, 3, 28, 12, 0);
        Item item58 = new Item(58L, "Steel plateskirt", "A set of steel legs, offering more protection.", 8, 3, 28, 12, 1);
        Item item59 = new Item(59L, "Steel chainbody", "A light set of steel body armour.", 9, 3, 28, 12, 1);
        Item item60 = new Item(60L, "Steel platebody", "A heavy set of steel body armour.", 10, 3, 42, 14, 1);
        Item item61 = new Item(61L, "Steel half helmet", "A little bit frilly approach to protection.", 11, 3, 28, 14, 1);
        Item item62 = new Item(62L, "Steel full helmet", "A rather frilly approach to protection.", 12, 3, 42, 16, 1);
        Item item63 = new Item(63L, "Steel boots", "A set of boots for those that like the grey look, now with added shine.", 13, 3, 14, 16, 1);
        Item item64 = new Item(64L, "Steel gloves", "A set of gloves for those that like the grey look, now with added shine.", 14, 3, 14, 16, 1);
        Item item65 = new Item(65L, "Steel pickaxe", "A sharper pickaxe for mining rocks.", 15, 3, 28, 18, 1);
        Item item66 = new Item(66L, "Steel hatchet", "A sharper axe for cutting down trees.", 16, 3, 28, 18, 1);
        Item item67 = new Item(67L, "Steel fishing rod", "A sharper rod for catching fish.", 17, 3, 28, 18, 1);
        Item item68 = new Item(68L, "Steel hammer", "A heavier hammer that tends to hit the anvil. Just.", 18, 3, 4, 18, 1);
        Item item69 = new Item(69L, "Flax", "A strand of flax.", 20, 10, 1, 1, 1);
        Item item70 = new Item(70L, "Silk", "A fine strip of silk.", 20, 10, 1, 1, 1);
        item1.save();
        item2.save();
        item3.save();
        item4.save();
        item5.save();
        item6.save();
        item7.save();
        item8.save();
        item9.save();
        item10.save();
        item11.save();
        item12.save();
        item13.save();
        item14.save();
        item15.save();
        item16.save();
        item17.save();
        item18.save();
        item19.save();
        item20.save();
        item21.save();
        item22.save();
        item23.save();
        item24.save();
        item25.save();
        item26.save();
        item27.save();
        item28.save();
        item29.save();
        item30.save();
        item31.save();
        item32.save();
        item33.save();
        item34.save();
        item35.save();
        item36.save();
        item37.save();
        item38.save();
        item39.save();
        item40.save();
        item41.save();
        item42.save();
        item43.save();
        item44.save();
        item45.save();
        item46.save();
        item47.save();
        item48.save();
        item49.save();
        item50.save();
        item51.save();
        item52.save();
        item53.save();
        item54.save();
        item55.save();
        item56.save();
        item57.save();
        item58.save();
        item59.save();
        item60.save();
        item61.save();
        item62.save();
        item63.save();
        item64.save();
        item65.save();
        item66.save();
        item67.save();
        item68.save();
        item69.save();
        item70.save();

        Location location = new Location(1L, "Anvil");
        Location location1 = new Location(2L, "Furnace");
        Location location2 = new Location(3L, "Selling");
        Location location3 = new Location(4L, "Mine");
        Location location4 = new Location(5L, "Table");
        location.save();
        location1.save();
        location2.save();
        location3.save();
        location4.save();

        Recipe recipe1 = new Recipe(1L, 11L, 1, 1L, 1, 1);
        Recipe recipe2 = new Recipe(2L, 11L, 1, 2L, 1, 1);
        Recipe recipe3 = new Recipe(3L, 12L, 1, 4L, 1, 2);
        Recipe recipe4 = new Recipe(4L, 13L, 1, 4L, 1, 2);
        Recipe recipe5 = new Recipe(5L, 13L, 1, 3L, 1, 1);
        Recipe recipe6 = new Recipe(6L, 14L, 1, 5L, 1, 1);
        Recipe recipe7 = new Recipe(7L, 14L, 1, 3L, 1, 2);
        Recipe recipe8 = new Recipe(8L, 15L, 1, 6L, 1, 1);
        Recipe recipe9 = new Recipe(9L, 15L, 1, 3L, 1, 4);
        Recipe recipe10 = new Recipe(10L, 16L, 1, 7L, 1, 1);
        Recipe recipe11 = new Recipe(11L, 16L, 1, 3L, 1, 8);
        Recipe recipe12 = new Recipe(12L, 17L, 1, 9L, 1, 1);
        Recipe recipe13 = new Recipe(13L, 18L, 1, 8L, 1, 1);
        Recipe recipe14 = new Recipe(14L, 19L, 1, 10L, 1, 1);
        Recipe recipe15 = new Recipe(15L, 19L, 1, 3L, 1, 12);
        Recipe recipe16 = new Recipe(16L, 20L, 2, 11L, 1, 1);
        Recipe recipe17 = new Recipe(17L, 21L, 2, 11L, 1, 2);
        Recipe recipe18 = new Recipe(18L, 22L, 2, 11L, 1, 2);
        Recipe recipe19 = new Recipe(19L, 23L, 2, 11L, 1, 2);
        Recipe recipe20 = new Recipe(20L, 24L, 2, 11L, 1, 3);
        Recipe recipe21 = new Recipe(21L, 25L, 2, 11L, 1, 3);
        Recipe recipe22 = new Recipe(22L, 26L, 2, 11L, 1, 3);
        Recipe recipe23 = new Recipe(23L, 27L, 2, 11L, 1, 5);
        Recipe recipe24 = new Recipe(24L, 28L, 2, 11L, 1, 3);
        Recipe recipe25 = new Recipe(25L, 29L, 2, 11L, 1, 4);
        Recipe recipe26 = new Recipe(26L, 30L, 2, 11L, 1, 2);
        Recipe recipe27 = new Recipe(27L, 31L, 2, 11L, 1, 2);
        Recipe recipe28 = new Recipe(28L, 32L, 2, 11L, 1, 2);
        Recipe recipe29 = new Recipe(29L, 33L, 2, 11L, 1, 2);
        Recipe recipe30 = new Recipe(30L, 34L, 2, 11L, 1, 2);
        Recipe recipe31 = new Recipe(31L, 35L, 2, 11L, 1, 1);
        Recipe recipe32 = new Recipe(32L, 36L, 2, 12L, 1, 1);
        Recipe recipe33 = new Recipe(33L, 37L, 2, 12L, 1, 2);
        Recipe recipe34 = new Recipe(34L, 38L, 2, 12L, 1, 2);
        Recipe recipe35 = new Recipe(35L, 39L, 2, 12L, 1, 2);
        Recipe recipe36 = new Recipe(36L, 40L, 2, 12L, 1, 3);
        Recipe recipe37 = new Recipe(37L, 41L, 2, 12L, 1, 3);
        Recipe recipe38 = new Recipe(38L, 42L, 2, 12L, 1, 3);
        Recipe recipe39 = new Recipe(39L, 43L, 2, 12L, 1, 5);
        Recipe recipe40 = new Recipe(40L, 44L, 2, 12L, 1, 3);
        Recipe recipe41 = new Recipe(41L, 45L, 2, 12L, 1, 4);
        Recipe recipe42 = new Recipe(42L, 46L, 2, 12L, 1, 2);
        Recipe recipe43 = new Recipe(43L, 47L, 2, 12L, 1, 2);
        Recipe recipe44 = new Recipe(44L, 48L, 2, 12L, 1, 2);
        Recipe recipe45 = new Recipe(45L, 49L, 2, 12L, 1, 2);
        Recipe recipe46 = new Recipe(46L, 50L, 2, 12L, 1, 2);
        Recipe recipe47 = new Recipe(47L, 51L, 2, 12L, 1, 1);
        Recipe recipe48 = new Recipe(48L, 53L, 2, 13L, 1, 1);
        Recipe recipe49 = new Recipe(49L, 54L, 2, 13L, 1, 2);
        Recipe recipe50 = new Recipe(50L, 55L, 2, 13L, 1, 2);
        Recipe recipe51 = new Recipe(51L, 56L, 2, 13L, 1, 2);
        Recipe recipe52 = new Recipe(52L, 57L, 2, 13L, 1, 3);
        Recipe recipe53 = new Recipe(53L, 58L, 2, 13L, 1, 3);
        Recipe recipe54 = new Recipe(54L, 59L, 2, 13L, 1, 3);
        Recipe recipe55 = new Recipe(55L, 60L, 2, 13L, 1, 5);
        Recipe recipe56 = new Recipe(56L, 61L, 2, 13L, 1, 3);
        Recipe recipe57 = new Recipe(57L, 62L, 2, 13L, 1, 4);
        Recipe recipe58 = new Recipe(58L, 63L, 2, 13L, 1, 2);
        Recipe recipe59 = new Recipe(59L, 64L, 2, 13L, 1, 2);
        Recipe recipe60 = new Recipe(60L, 65L, 2, 13L, 1, 2);
        Recipe recipe61 = new Recipe(61L, 66L, 2, 13L, 1, 2);
        Recipe recipe62 = new Recipe(62L, 67L, 2, 13L, 1, 2);
        Recipe recipe63 = new Recipe(63L, 68L, 2, 13L, 1, 1);
        Recipe recipe64 = new Recipe(64L, 20L, 1, 20L, 2, 1);
        Recipe recipe65 = new Recipe(65L, 20L, 1, 69L, 1, 1);
        Recipe recipe66 = new Recipe(66L, 21L, 1, 21L, 2, 1);
        Recipe recipe67 = new Recipe(67L, 21L, 1, 70L, 1, 1);
        recipe1.save();
        recipe2.save();
        recipe3.save();
        recipe4.save();
        recipe5.save();
        recipe6.save();
        recipe7.save();
        recipe8.save();
        recipe9.save();
        recipe10.save();
        recipe11.save();
        recipe12.save();
        recipe13.save();
        recipe14.save();
        recipe15.save();
        recipe16.save();
        recipe17.save();
        recipe18.save();
        recipe19.save();
        recipe20.save();
        recipe21.save();
        recipe22.save();
        recipe23.save();
        recipe24.save();
        recipe25.save();
        recipe26.save();
        recipe27.save();
        recipe28.save();
        recipe29.save();
        recipe30.save();
        recipe31.save();
        recipe32.save();
        recipe33.save();
        recipe34.save();
        recipe35.save();
        recipe36.save();
        recipe37.save();
        recipe38.save();
        recipe39.save();
        recipe40.save();
        recipe41.save();
        recipe42.save();
        recipe43.save();
        recipe44.save();
        recipe45.save();
        recipe46.save();
        recipe47.save();
        recipe48.save();
        recipe49.save();
        recipe50.save();
        recipe51.save();
        recipe52.save();
        recipe53.save();
        recipe54.save();
        recipe55.save();
        recipe56.save();
        recipe57.save();
        recipe58.save();
        recipe59.save();
        recipe60.save();
        recipe61.save();
        recipe62.save();
        recipe63.save();
        recipe64.save();
        recipe65.save();
        recipe66.save();
        recipe67.save();

        Shop shop = new Shop(0L, 0, 3, "Poor Ore", "Full of low quality ore.", 1, 1);
        Shop shop1 = new Shop(1L, 0, 3, "Less Poor Ore", "The ore here is not so poor.", 5, 1);
        Shop shop2 = new Shop(2L, 0, 3, "Rare Ore", "This shop is too rare to be found.", 5, 0);
        Shop shop3 = new Shop(3L, 0, 3, "Average Ore", "The ore in store is not too poor.", 10, 1);
        Shop shop4 = new Shop(4L, 0, 3, "Silver Miner", "Cor, ore!", 20, 1);
        shop.save();
        shop1.save();
        shop2.save();
        shop3.save();
        shop4.save();

        Shop_Stock shopStock = new Shop_Stock(1L, 1L, 4, 1, 5);
        Shop_Stock shopStock1 = new Shop_Stock(1L, 2L, 4, 1, 5);
        Shop_Stock shopStock2 = new Shop_Stock(2L, 1L, 3, 1, 10);
        Shop_Stock shopStock3 = new Shop_Stock(2L, 2L, 3, 1, 10);
        Shop_Stock shopStock4 = new Shop_Stock(3L, 3L, 8, 1, 10);
        Shop_Stock shopStock5 = new Shop_Stock(4L, 2L, 3, 1, 10);
        Shop_Stock shopStock6 = new Shop_Stock(5L, 9L, 8, 1, 1);
        Shop_Stock shopStock7 = new Shop_Stock(5L, 8L, 8, 1, 1);
        shopStock.save();
        shopStock1.save();
        shopStock2.save();
        shopStock3.save();
        shopStock4.save();
        shopStock5.save();
        shopStock6.save();
        shopStock7.save();

        Slots slots = new Slots(0L, 1, 0, 0);
        Slots slots1 = new Slots(1L, 1, 10, 0);
        Slots slots2 = new Slots(2L, 1, 20, 0);
        Slots slots3 = new Slots(3L, 2, 0, 0);
        Slots slots4 = new Slots(4L, 2, 5, 0);
        Slots slots5 = new Slots(5L, 2, 5, 0);
        Slots slots6 = new Slots(6L, 3, 0, 0);
        Slots slots7 = new Slots(7L, 3, 10, 0);
        Slots slots8 = new Slots(8L, 3, 20, 0);
        Slots slots9 = new Slots(9L, 4, 0, 0);
        Slots slots10 = new Slots(10L, 4, 5, 0);
        Slots slots11 = new Slots(11L, 4, 10, 0);
        Slots slots12 = new Slots(12L, 5, 1, 0);
        Slots slots13 = new Slots(13L, 5, 1, 0);
        Slots slots14 = new Slots(14L, 5, 1, 0);
        Slots slots15 = new Slots(15L, 5, 1, 0);
        slots.save();
        slots1.save();
        slots2.save();
        slots3.save();
        slots4.save();
        slots5.save();
        slots6.save();
        slots7.save();
        slots8.save();
        slots9.save();
        slots10.save();
        slots11.save();
        slots12.save();
        slots13.save();
        slots14.save();
        slots15.save();

        State state1 = new State(1L, "Normal");
        State state2 = new State(2L, "Unfinished");
        State state3 = new State(3L, "Red Enchant");
        State state4 = new State(4L, "Blue Enchant");
        state1.save();
        state2.save();
        state3.save();
        state4.save();

        Tier tier1 = new Tier(1L, "Bronze");
        Tier tier2 = new Tier(2L, "Iron");
        Tier tier3 = new Tier(3L, "Steel");
        Tier tier4 = new Tier(4L, "Mithril");
        Tier tier5 = new Tier(5L, "Adamant");
        Tier tier6 = new Tier(6L, "Rune");
        Tier tier7 = new Tier(7L, "Dragon");
        Tier tier8 = new Tier(8L, "Silver");
        Tier tier9 = new Tier(9L, "Gold");
        Tier tier10 = new Tier(10L, "None");
        tier1.save();
        tier2.save();
        tier3.save();
        tier4.save();
        tier5.save();
        tier6.save();
        tier7.save();
        tier8.save();
        tier9.save();
        tier10.save();

        Type type1 = new Type(1L, "Ore", 1);
        Type type2 = new Type(2L, "Bar", 1);
        Type type3 = new Type(3L, "Dagger", 2);
        Type type4 = new Type(4L, "Sword", 2);
        Type type5 = new Type(5L, "Longsword", 2);
        Type type6 = new Type(6L, "Bow", 2);
        Type type7 = new Type(7L, "Platelegs", 3);
        Type type8 = new Type(8L, "Plateskirt", 3);
        Type type9 = new Type(9L, "Chainbody", 3);
        Type type10 = new Type(10L, "Platebody", 3);
        Type type11 = new Type(11L, "Half Helmet", 3);
        Type type12 = new Type(12L, "Full Helmet", 3);
        Type type13 = new Type(13L, "Boots", 3);
        Type type14 = new Type(14L, "Gloves", 3);
        Type type15 = new Type(15L, "Pickaxe", 4);
        Type type16 = new Type(16L, "Hatchet", 4);
        Type type17 = new Type(17L, "Fishing Rod", 4);
        Type type18 = new Type(18L, "Hammer", 4);
        Type type19 = new Type(19L, "Internal", 0);
        Type type20 = new Type(20L, "Secondary", 1);
        type1.save();
        type2.save();
        type3.save();
        type4.save();
        type5.save();
        type6.save();
        type7.save();
        type8.save();
        type9.save();
        type10.save();
        type11.save();
        type12.save();
        type13.save();
        type14.save();
        type15.save();
        type16.save();
        type17.save();
        type18.save();
        type19.save();
        type20.save();
    }

    @Override
    protected void onStart() {
        super.onStart();
        final Runnable updateTask = new Runnable() {
            @Override
            public void run() {
                updateSlots();
                dbh.updateCoinsGUI();
                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(updateTask, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }

    private void createSlots() {
        dh.createSlotContainer(sellingSlots, Location.getSlots("Selling"));
        dh.createSlotContainer(furnaceSlots, Location.getSlots("Furnace"));
        dh.createSlotContainer(anvilSlots, Location.getSlots("Anvil"));
        dh.createSlotContainer(mineSlots, Location.getSlots("Mine"));
        dh.createSlotContainer(tableSlots, Location.getSlots("Table"));
    }

    public void updateSlots() {
        dh.depopulateSlotContainer(sellingSlots);
        dh.depopulateSlotContainer(furnaceSlots);
        dh.depopulateSlotContainer(anvilSlots);
        dh.depopulateSlotContainer(mineSlots);
        dh.depopulateSlotContainer(tableSlots);

        dh.populateSlotContainer(sellingSlots, "Selling");
        dh.populateSlotContainer(furnaceSlots, "Furnace");
        dh.populateSlotContainer(anvilSlots, "Anvil");
        dh.populateSlotContainer(mineSlots, "Mine");
        dh.populateSlotContainer(tableSlots, "Table");
    }

    public void openMenu(View view) {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    public void openMine(View view) {
        Intent intent = new Intent(this, MineActivity.class);
        startActivity(intent);
    }

    public void openInventory(View view) {
        Intent intent = new Intent(this, InventoryActivity.class);
        startActivity(intent);
    }

    public void openFurnace(View view) {
        Intent intent = new Intent(this, FurnaceActivity.class);
        startActivity(intent);
    }

    public void openAnvil(View view) {
        Intent intent = new Intent(this, AnvilActivity.class);
        startActivity(intent);
    }

    public void openTable(View view) {
        Intent intent = new Intent(this, TableActivity.class);
        startActivity(intent);
    }
}
