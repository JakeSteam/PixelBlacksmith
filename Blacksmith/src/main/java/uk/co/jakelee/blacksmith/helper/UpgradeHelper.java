package uk.co.jakelee.blacksmith.helper;

import uk.co.jakelee.blacksmith.model.Category;
import uk.co.jakelee.blacksmith.model.Character;
import uk.co.jakelee.blacksmith.model.Criteria;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Location;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Recipe;
import uk.co.jakelee.blacksmith.model.Shop;
import uk.co.jakelee.blacksmith.model.Shop_Stock;
import uk.co.jakelee.blacksmith.model.Slot;
import uk.co.jakelee.blacksmith.model.State;
import uk.co.jakelee.blacksmith.model.Tier;
import uk.co.jakelee.blacksmith.model.Type;
import uk.co.jakelee.blacksmith.model.Visitor_Stats;
import uk.co.jakelee.blacksmith.model.Visitor_Type;

public class UpgradeHelper {

    public static void initialSQL() {
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

        Character character1 = new Character(1L, "Sean Keeper", "He looks a little bit shifty...", "Greetings! See anything you like?", "Remember, no refunds!");
        Character character2 = new Character(2L, "Mr Hellfire", "Hot stuff.", "Hey! Do you like my armour?", "No refunds, comrade.");
        character1.save();
        character2.save();

        Criteria criteria1 = new Criteria(1L, "State");
        Criteria criteria2 = new Criteria(2L, "Tier");
        Criteria criteria3 = new Criteria(3L, "Type");
        criteria1.save();
        criteria2.save();
        criteria3.save();

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
        Inventory inventory13 = new Inventory(72L, 1, 25);
        Inventory inventory14 = new Inventory(73L, 1, 18);
        Inventory inventory15 = new Inventory(74L, 1, 6);
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
        inventory13.save();
        inventory14.save();
        inventory15.save();

        Player_Info info1 = new Player_Info(1L, "Name", "Jake", 0);
        Player_Info info2 = new Player_Info(2L, "XP", "", 1200);
        Player_Info info3 = new Player_Info(3L, "DatabaseVersion", "", 1);
        info1.save();
        info2.save();
        info3.save();

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
        Item item23 = new Item(23L, "Bronze bow", "A bow with bronze elements.", 6, 1, 10, 1, 1);
        Item item24 = new Item(24L, "Bronze half shield", "A fairly weak bronze shield.", 7, 1, 10, 2, 1);
        Item item25 = new Item(25L, "Bronze full shield", "A slightly stronger bronze shield.", 8, 1, 10, 2, 1);
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
        Item item39 = new Item(39L, "Iron bow", "A bow with iron elements.", 6, 2, 14, 5, 1);
        Item item40 = new Item(40L, "Iron half shield", "An iron half shield.", 7, 2, 14, 6, 1);
        Item item41 = new Item(41L, "Iron full shield", "An improved iron shield.", 8, 2, 14, 6, 1);
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
        Item item52 = new Item(52L, "Coins", "Coins! Glorious coins!", 100, 10, 1, 1, 1);
        Item item53 = new Item(53L, "Steel dagger", "An average steel dagger.", 3, 3, 14, 10, 1);
        Item item54 = new Item(54L, "Steel sword", "A slightly blunt steel sword.", 4, 3, 28, 10, 1);
        Item item55 = new Item(55L, "Steel longsword", "An even longer steel sword.", 5, 3, 28, 10, 1);
        Item item56 = new Item(56L, "Steel bow", "A bow with steel elements.", 6, 3, 28, 10, 0);
        Item item57 = new Item(57L, "Steel half shield", "Steel shield, but only half.", 7, 3, 28, 12, 0);
        Item item58 = new Item(58L, "Steel full shield", "Steel shield, the fullest.", 8, 3, 28, 12, 1);
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
        Item item69 = new Item(69L, "Spidersilk", "A stand of spidersilk.", 19, 10, 1, 1, 1);
        Item item70 = new Item(70L, "Silk", "A fine strip of silk.", 19, 10, 1, 1, 1);
        Item item71 = new Item(71L, "Logs", "Some basic logs.",19, 10, 1, 1, 1);
        Item item72 = new Item(72L, "Ruby", "A red gem", 20, 10, 150, 12, 1);
        Item item73 = new Item(73L, "Sapphire", "A blue gem", 20, 10, 150, 12, 1);
        Item item74 = new Item(74L, "Emerald", "A green gem", 20, 10, 150, 12, 1);
        Item item75 = new Item(75L, "Diamond", "A white gem", 20, 10, 250, 15, 1);
        Item item76 = new Item(76L, "Onyx", "A black gem", 19, 10, 250, 15, 1);
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
        item71.save();
        item72.save();
        item73.save();
        item74.save();
        item75.save();
        item76.save();

        Location location1 = new Location(1L, "Anvil");
        Location location2 = new Location(2L, "Furnace");
        Location location3 = new Location(3L, "Selling");
        Location location4 = new Location(4L, "Mine");
        Location location5 = new Location(5L, "Table");
        Location location6 = new Location(6L, "Enchanting");
        location1.save();
        location2.save();
        location3.save();
        location4.save();
        location5.save();
        location6.save();

        // Unfinished items
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

        // Finishing bronze items
        Recipe recipe64 = new Recipe(64L, 20L, 1, 20L, 2, 1);
        Recipe recipe65 = new Recipe(65L, 20L, 1, 69L, 1, 1);
        Recipe recipe66 = new Recipe(66L, 21L, 1, 21L, 2, 1);
        Recipe recipe67 = new Recipe(67L, 21L, 1, 69L, 1, 1);
        Recipe recipe68 = new Recipe(68L, 22L, 1, 22L, 2, 1);
        Recipe recipe69 = new Recipe(69L, 22L, 1, 69L, 1, 1);
        Recipe recipe70 = new Recipe(70L, 23L, 1, 23L, 2, 1);
        Recipe recipe71 = new Recipe(71L, 23L, 1, 69L, 1, 1);
        Recipe recipe72 = new Recipe(72L, 24L, 1, 24L, 2, 1);
        Recipe recipe73 = new Recipe(73L, 24L, 1, 69L, 1, 1);
        Recipe recipe74 = new Recipe(74L, 25L, 1, 25L, 2, 1);
        Recipe recipe75 = new Recipe(75L, 25L, 1, 69L, 1, 1);
        Recipe recipe76 = new Recipe(76L, 26L, 1, 26L, 2, 1);
        Recipe recipe77 = new Recipe(77L, 26L, 1, 69L, 1, 1);
        Recipe recipe78 = new Recipe(78L, 27L, 1, 27L, 2, 1);
        Recipe recipe79 = new Recipe(79L, 27L, 1, 69L, 1, 1);
        Recipe recipe80 = new Recipe(80L, 28L, 1, 28L, 2, 1);
        Recipe recipe81 = new Recipe(81L, 28L, 1, 69L, 1, 1);
        Recipe recipe82 = new Recipe(82L, 29L, 1, 29L, 2, 1);
        Recipe recipe83 = new Recipe(83L, 29L, 1, 69L, 1, 1);
        Recipe recipe84 = new Recipe(84L, 30L, 1, 30L, 2, 1);
        Recipe recipe85 = new Recipe(85L, 30L, 1, 69L, 1, 1);
        Recipe recipe86 = new Recipe(86L, 31L, 1, 31L, 2, 1);
        Recipe recipe87 = new Recipe(87L, 31L, 1, 69L, 1, 1);
        Recipe recipe88 = new Recipe(88L, 32L, 1, 32L, 2, 1);
        Recipe recipe89 = new Recipe(89L, 32L, 1, 69L, 1, 1);
        Recipe recipe90 = new Recipe(90L, 33L, 1, 33L, 2, 1);
        Recipe recipe91 = new Recipe(91L, 33L, 1, 69L, 1, 1);
        Recipe recipe92 = new Recipe(92L, 34L, 1, 34L, 2, 1);
        Recipe recipe93 = new Recipe(93L, 34L, 1, 69L, 1, 1);
        Recipe recipe94 = new Recipe(94L, 35L, 1, 35L, 2, 1);
        Recipe recipe95 = new Recipe(95L, 35L, 1, 69L, 1, 1);
        recipe64.save();
        recipe65.save();
        recipe66.save();
        recipe67.save();
        recipe68.save();
        recipe69.save();
        recipe70.save();
        recipe71.save();
        recipe72.save();
        recipe73.save();
        recipe74.save();
        recipe75.save();
        recipe76.save();
        recipe77.save();
        recipe78.save();
        recipe79.save();
        recipe80.save();
        recipe81.save();
        recipe82.save();
        recipe83.save();
        recipe84.save();
        recipe85.save();
        recipe86.save();
        recipe87.save();
        recipe88.save();
        recipe89.save();
        recipe90.save();
        recipe91.save();
        recipe92.save();
        recipe93.save();
        recipe94.save();
        recipe95.save();

        // Finishing iron items
        Recipe recipe96 = new Recipe(96L, 36L, 1, 36L, 2, 1);
        Recipe recipe97 = new Recipe(97L, 36L, 1, 70L, 1, 1);
        Recipe recipe98 = new Recipe(98L, 37L, 1, 37L, 2, 1);
        Recipe recipe99 = new Recipe(99L, 37L, 1, 70L, 1, 1);
        Recipe recipe100 = new Recipe(100L, 38L, 1, 38L, 2, 1);
        Recipe recipe101 = new Recipe(101L, 38L, 1, 70L, 1, 1);
        Recipe recipe102 = new Recipe(102L, 39L, 1, 39L, 2, 1);
        Recipe recipe103 = new Recipe(103L, 39L, 1, 70L, 1, 1);
        Recipe recipe104 = new Recipe(104L, 40L, 1, 40L, 2, 1);
        Recipe recipe105 = new Recipe(105L, 40L, 1, 70L, 1, 1);
        Recipe recipe106 = new Recipe(106L, 41L, 1, 41L, 2, 1);
        Recipe recipe107 = new Recipe(107L, 41L, 1, 70L, 1, 1);
        Recipe recipe108 = new Recipe(108L, 42L, 1, 42L, 2, 1);
        Recipe recipe109 = new Recipe(109L, 42L, 1, 70L, 1, 1);
        Recipe recipe110 = new Recipe(110L, 43L, 1, 43L, 2, 1);
        Recipe recipe111 = new Recipe(111L, 43L, 1, 70L, 1, 1);
        Recipe recipe112 = new Recipe(112L, 44L, 1, 44L, 2, 1);
        Recipe recipe113 = new Recipe(113L, 44L, 1, 70L, 1, 1);
        Recipe recipe114 = new Recipe(114L, 45L, 1, 45L, 2, 1);
        Recipe recipe115 = new Recipe(115L, 45L, 1, 70L, 1, 1);
        Recipe recipe116 = new Recipe(116L, 46L, 1, 46L, 2, 1);
        Recipe recipe117 = new Recipe(117L, 46L, 1, 70L, 1, 1);
        Recipe recipe118 = new Recipe(118L, 47L, 1, 47L, 2, 1);
        Recipe recipe119 = new Recipe(119L, 47L, 1, 70L, 1, 1);
        Recipe recipe120 = new Recipe(120L, 48L, 1, 48L, 2, 1);
        Recipe recipe121 = new Recipe(121L, 48L, 1, 70L, 1, 1);
        Recipe recipe122 = new Recipe(122L, 49L, 1, 49L, 2, 1);
        Recipe recipe123 = new Recipe(123L, 49L, 1, 70L, 1, 1);
        Recipe recipe124 = new Recipe(124L, 50L, 1, 50L, 2, 1);
        Recipe recipe125 = new Recipe(125L, 50L, 1, 70L, 1, 1);
        Recipe recipe126 = new Recipe(126L, 51L, 1, 51L, 2, 1);
        Recipe recipe127 = new Recipe(127L, 51L, 1, 70L, 1, 1);
        recipe96.save();
        recipe97.save();
        recipe98.save();
        recipe99.save();
        recipe100.save();
        recipe101.save();
        recipe102.save();
        recipe103.save();
        recipe104.save();
        recipe105.save();
        recipe106.save();
        recipe107.save();
        recipe108.save();
        recipe109.save();
        recipe110.save();
        recipe111.save();
        recipe112.save();
        recipe113.save();
        recipe114.save();
        recipe115.save();
        recipe116.save();
        recipe117.save();
        recipe118.save();
        recipe119.save();
        recipe120.save();
        recipe121.save();
        recipe122.save();
        recipe123.save();
        recipe124.save();
        recipe125.save();
        recipe126.save();
        recipe127.save();

        // Finishing steel items
        Recipe recipe128 = new Recipe(128L, 53L, 1, 53L, 2, 1);
        Recipe recipe129 = new Recipe(129L, 53L, 1, 71L, 1, 1);
        Recipe recipe130 = new Recipe(130L, 54L, 1, 54L, 2, 1);
        Recipe recipe131 = new Recipe(131L, 54L, 1, 71L, 1, 1);
        Recipe recipe132 = new Recipe(132L, 55L, 1, 55L, 2, 1);
        Recipe recipe133 = new Recipe(133L, 55L, 1, 71L, 1, 1);
        Recipe recipe134 = new Recipe(134L, 56L, 1, 56L, 2, 1);
        Recipe recipe135 = new Recipe(135L, 56L, 1, 71L, 1, 1);
        Recipe recipe136 = new Recipe(136L, 57L, 1, 57L, 2, 1);
        Recipe recipe137 = new Recipe(137L, 57L, 1, 71L, 1, 1);
        Recipe recipe138 = new Recipe(138L, 58L, 1, 58L, 2, 1);
        Recipe recipe139 = new Recipe(139L, 58L, 1, 71L, 1, 1);
        Recipe recipe140 = new Recipe(140L, 59L, 1, 59L, 2, 1);
        Recipe recipe141 = new Recipe(141L, 59L, 1, 71L, 1, 1);
        Recipe recipe142 = new Recipe(142L, 60L, 1, 60L, 2, 1);
        Recipe recipe143 = new Recipe(143L, 60L, 1, 71L, 1, 1);
        Recipe recipe144 = new Recipe(144L, 61L, 1, 61L, 2, 1);
        Recipe recipe145 = new Recipe(145L, 61L, 1, 71L, 1, 1);
        Recipe recipe146 = new Recipe(146L, 62L, 1, 62L, 2, 1);
        Recipe recipe147 = new Recipe(147L, 62L, 1, 71L, 1, 1);
        Recipe recipe148 = new Recipe(148L, 63L, 1, 63L, 2, 1);
        Recipe recipe149 = new Recipe(149L, 63L, 1, 71L, 1, 1);
        Recipe recipe150 = new Recipe(150L, 64L, 1, 64L, 2, 1);
        Recipe recipe151 = new Recipe(151L, 64L, 1, 71L, 1, 1);
        Recipe recipe152 = new Recipe(152L, 65L, 1, 65L, 2, 1);
        Recipe recipe153 = new Recipe(153L, 65L, 1, 71L, 1, 1);
        Recipe recipe154 = new Recipe(154L, 66L, 1, 66L, 2, 1);
        Recipe recipe155 = new Recipe(155L, 66L, 1, 71L, 1, 1);
        Recipe recipe156 = new Recipe(156L, 67L, 1, 67L, 2, 1);
        Recipe recipe157 = new Recipe(157L, 67L, 1, 71L, 1, 1);
        Recipe recipe158 = new Recipe(158L, 68L, 1, 68L, 2, 1);
        Recipe recipe159 = new Recipe(159L, 68L, 1, 71L, 1, 1);
        recipe128.save();
        recipe129.save();
        recipe130.save();
        recipe131.save();
        recipe132.save();
        recipe133.save();
        recipe134.save();
        recipe135.save();
        recipe136.save();
        recipe137.save();
        recipe138.save();
        recipe139.save();
        recipe140.save();
        recipe141.save();
        recipe142.save();
        recipe143.save();
        recipe144.save();
        recipe145.save();
        recipe146.save();
        recipe147.save();
        recipe148.save();
        recipe149.save();
        recipe150.save();
        recipe151.save();
        recipe152.save();
        recipe153.save();
        recipe154.save();
        recipe155.save();
        recipe156.save();
        recipe157.save();
        recipe158.save();
        recipe159.save();

        Shop shop = new Shop(0L, 1, 3, "Poor Ore", "Full of low quality ore.", 1, 1);
        Shop shop1 = new Shop(1L, 2, 3, "Less Poor Ore", "The ore here is not so poor.", 5, 1);
        Shop shop2 = new Shop(2L, 1, 3, "Rare Ore", "This shop is too rare to be found.", 5, 1);
        Shop shop3 = new Shop(3L, 1, 3, "Average Ore", "The ore in store is not too poor.", 10, 1);
        Shop shop4 = new Shop(4L, 2, 3, "Silver Miner", "Cor, ore!", 10, 1);
        shop.save();
        shop1.save();
        shop2.save();
        shop3.save();
        shop4.save();

        Shop_Stock shopStock = new Shop_Stock(1L, 1L, 1, 1, 5);
        Shop_Stock shopStock1 = new Shop_Stock(1L, 2L, 1, 1, 5);
        Shop_Stock shopStock2 = new Shop_Stock(2L, 1L, 1, 1, 10);
        Shop_Stock shopStock3 = new Shop_Stock(2L, 2L, 1, 1, 10);
        Shop_Stock shopStock4 = new Shop_Stock(3L, 3L, 1, 1, 10);
        Shop_Stock shopStock5 = new Shop_Stock(4L, 2L, 1, 1, 10);
        Shop_Stock shopStock6 = new Shop_Stock(5L, 9L, 1, 1, 1);
        Shop_Stock shopStock7 = new Shop_Stock(5L, 8L, 1, 1, 1);
        shopStock.save();
        shopStock1.save();
        shopStock2.save();
        shopStock3.save();
        shopStock4.save();
        shopStock5.save();
        shopStock6.save();
        shopStock7.save();

        Slot slots = new Slot(0L, 1, 0, 0);
        Slot slots1 = new Slot(1L, 1, 5, 0);
        Slot slots2 = new Slot(2L, 1, 10, 0);
        Slot slots3 = new Slot(3L, 2, 0, 0);
        Slot slots4 = new Slot(4L, 2, 5, 0);
        Slot slots5 = new Slot(5L, 2, 10, 0);
        Slot slots6 = new Slot(6L, 3, 0, 0);
        Slot slots7 = new Slot(7L, 3, 5, 0);
        Slot slots8 = new Slot(8L, 3, 10, 0);
        Slot slots9 = new Slot(9L, 4, 0, 0);
        Slot slots10 = new Slot(10L, 4, 5, 0);
        Slot slots11 = new Slot(11L, 4, 10, 0);
        Slot slots12 = new Slot(12L, 5, 1, 0);
        Slot slots13 = new Slot(13L, 5, 5, 0);
        Slot slots14 = new Slot(14L, 5, 5, 0);
        Slot slots15 = new Slot(15L, 5, 10, 0);
        Slot slots16 = new Slot(16L, 6, 10, 0);
        Slot slots17 = new Slot(17L, 6, 10, 0);
        Slot slots18 = new Slot(18L, 6, 10, 0);
        Slot slots19 = new Slot(19L, 6, 10, 0);
        Slot slots20 = new Slot(20L, 6, 10, 0);
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
        slots16.save();
        slots17.save();
        slots18.save();
        slots19.save();
        slots20.save();

        State state1 = new State(1L, "Normal", 0L, 15);
        State state2 = new State(2L, "Unfinished", 0L, 15);
        State state3 = new State(3L, "Red Enchant", 72L, 2);
        State state4 = new State(4L, "Blue Enchant", 73L, 2);
        State state5 = new State(5L, "Green Enchant", 74L, 1);
        State state6 = new State(6L, "White Enchant", 75L, 1);
        State state7 = new State(7L, "Black Enchant", 76L, 1);
        state1.save();
        state2.save();
        state3.save();
        state4.save();
        state5.save();
        state6.save();
        state7.save();

        Tier tier1 = new Tier(1L, "Bronze", 30);
        Tier tier2 = new Tier(2L, "Iron", 25);
        Tier tier3 = new Tier(3L, "Steel", 15);
        Tier tier4 = new Tier(4L, "Mithril", 10);
        Tier tier5 = new Tier(5L, "Adamant", 7);
        Tier tier6 = new Tier(6L, "Rune", 4);
        Tier tier7 = new Tier(7L, "Dragon", 2);
        Tier tier8 = new Tier(8L, "Silver", 10);
        Tier tier9 = new Tier(9L, "Gold", 10);
        Tier tier10 = new Tier(10L, "None", 35);
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

        Type type1 = new Type(1L, "Ore", 1, 30);
        Type type2 = new Type(2L, "Bar", 1, 30);
        Type type3 = new Type(3L, "Dagger", 2, 25);
        Type type4 = new Type(4L, "Sword", 2, 25);
        Type type5 = new Type(5L, "Longsword", 2, 25);
        Type type6 = new Type(6L, "Bow", 2, 20);
        Type type7 = new Type(7L, "Half Shield", 3, 20);
        Type type8 = new Type(8L, "Full Shield", 3, 20);
        Type type9 = new Type(9L, "Chainbody", 3, 20);
        Type type10 = new Type(10L, "Platebody", 3, 15);
        Type type11 = new Type(11L, "Half Helmet", 3, 15);
        Type type12 = new Type(12L, "Full Helmet", 3, 15);
        Type type13 = new Type(13L, "Boots", 3, 15);
        Type type14 = new Type(14L, "Gloves", 3, 10);
        Type type15 = new Type(15L, "Pickaxe", 4, 10);
        Type type16 = new Type(16L, "Hatchet", 4, 10);
        Type type17 = new Type(17L, "Fishing Rod", 4, 10);
        Type type18 = new Type(18L, "Hammer", 4, 10);
        Type type19 = new Type(19L, "Secondary", 1, 30);
        Type type20 = new Type(20L, "Gem", 1, 5);
        Type type21 = new Type(100L, "Internal", 0, 0);
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
        type21.save();

        Visitor_Stats vStats1 = new Visitor_Stats(1L, 0, 52L, 1L, 1, 1452022352000L);
        Visitor_Stats vStats2 = new Visitor_Stats(2L, 0, 52L, 1L, 1, 1452022352000L);
        Visitor_Stats vStats3 = new Visitor_Stats(3L, 0, 52L, 1L, 1, 1452022352000L);
        Visitor_Stats vStats4 = new Visitor_Stats(4L, 0, 52L, 1L, 1, 1452022352000L);
        Visitor_Stats vStats5 = new Visitor_Stats(5L, 0, 52L, 1L, 1, 1452022352000L);
        Visitor_Stats vStats6 = new Visitor_Stats(6L, 0, 52L, 1L, 1, 1452022352000L);
        vStats1.save();
        vStats2.save();
        vStats3.save();
        vStats4.save();
        vStats5.save();
        vStats6.save();

        Visitor_Type vType1 = new Visitor_Type(1L, "Senor Spicy Hot", "I like unfinished things, they burn better!", 1L, 14L, 2L, 1.1, 1.1, 3.0, false, false, false, 3);
        Visitor_Type vType2 = new Visitor_Type(2L, "Mister Hatchet", "If only I was a woodcutter...", 3L, 16L, 1L, 1.2, 1.2, 1.2, false, false, false, 6);
        Visitor_Type vType3 = new Visitor_Type(3L, "Lord of the Junk", "It's not rubbish, it's treasure!", 1L, 3L, 2L, 1.05, 1.05, 1.05, false, false, false, 10);
        Visitor_Type vType4 = new Visitor_Type(4L, "Monsieur Fancypants", "Only the best for me, old chap.", 7L, 10L, 4L, 1.6, 1.55, 1.5, false, false, false, 1);
        Visitor_Type vType5 = new Visitor_Type(5L, "Grumbling Rock", "Me hungry. Tummy rumbling.", 10L, 1L, 1L, 1.05, 1.4, 1.05, false, false, false, 5);
        Visitor_Type vType6 = new Visitor_Type(6L, "Large Grumbling Rock", "Me very hungry. Tummy rumbling loud.", 10L, 1L, 1L, 1.6, 1.8, 1.05, false, false, false, 1);
        vType1.save();
        vType2.save();
        vType3.save();
        vType4.save();
        vType5.save();
        vType6.save();
    }

}

