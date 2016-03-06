package uk.co.jakelee.blacksmith.helper;

import uk.co.jakelee.blacksmith.model.Category;
import uk.co.jakelee.blacksmith.model.Character;
import uk.co.jakelee.blacksmith.model.Criteria;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Location;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Recipe;
import uk.co.jakelee.blacksmith.model.Setting;
import uk.co.jakelee.blacksmith.model.Slot;
import uk.co.jakelee.blacksmith.model.State;
import uk.co.jakelee.blacksmith.model.Tier;
import uk.co.jakelee.blacksmith.model.Trader;
import uk.co.jakelee.blacksmith.model.Trader_Stock;
import uk.co.jakelee.blacksmith.model.Type;
import uk.co.jakelee.blacksmith.model.Upgrade;
import uk.co.jakelee.blacksmith.model.Visitor_Stats;
import uk.co.jakelee.blacksmith.model.Visitor_Type;

public class DatabaseHelper {

    public static void initialSQL() {
        new Category("Unknown", "Item type category could not be found.");
        new Category("Crafting", "An item that can be used for crafting.");
        new Category("Weapon", "An item that can be used as a weapon.");
        new Category("Armour", "An item that can be used as armour.");
        new Category("Skill", "An item that can be used to train a skill.");

        new Character(1L, "Sean Keeper", "He looks a little bit shifty...", "Greetings! See anything you like?", "Remember, no refunds!");
        new Character(2L, "Mr Hellfire", "Hot stuff.", "Hey! Do you like my armour?", "No refunds, comrade.");

        new Criteria(1L, "State");
        new Criteria(2L, "Tier");
        new Criteria(3L, "Type");

        new Inventory(1L, 1, 101);
        new Inventory(2L, 1, 102);
        new Inventory(3L, 1, 303);
        new Inventory(4L, 1, 104);
        new Inventory(5L, 1, 105);
        new Inventory(6L, 1, 106);
        new Inventory(7L, 1, 107);
        new Inventory(8L, 1, 108);
        new Inventory(9L, 1, 109);
        new Inventory(10L, 1, 110);
        new Inventory(52L, 1, 999999);
        new Inventory(69L, 1, 10);
        new Inventory(70L, 1, 10);
        new Inventory(72L, 1, 25);
        new Inventory(73L, 1, 18);
        new Inventory(74L, 1, 6);
        new Inventory(20L, 1, 50);
        new Inventory(30L, 5, 10);
        new Inventory(77L, 1, 10);
        new Inventory(78L, 1, 10);
        new Inventory(79L, 1, 10);
        new Inventory(80L, 1, 10);

        new Item(1L, "Copper ore", "A piece of copper ore.", 1, 10, 1, 0);
        new Item(2L, "Tin ore", "A piece of tin ore.", 1, 10, 1, 0);
        new Item(3L, "Coal", "A piece of coal.", 1, 10, 1, 0);
        new Item(4L, "Iron ore", "A piece of iron ore.", 1, 10, 1, 0);
        new Item(5L, "Mithril ore", "A piece of mithril ore.", 1, 10, 1, 0);
        new Item(6L, "Adamantite ore", "A piece of adamantite ore.", 1, 10, 1, 0);
        new Item(7L, "Runite ore", "A piece of runite ore.", 1, 10, 1, 0);
        new Item(8L, "Gold ore", "A piece of gold ore.", 1, 10, 1, 0);
        new Item(9L, "Silver ore", "A piece of silver ore.", 1, 10, 1, 0);
        new Item(10L, "Dragonite ore", "A piece of dragonite ore.", 1, 10, 1, 0);
        new Item(11L, "Bronze bar", "A fresh bar of bronze.", 2, 10, 3, 1);
        new Item(12L, "Iron bar", "A fresh bar of iron.", 2, 10, 3, 5);
        new Item(13L, "Steel bar", "A fresh bar of steel.", 2, 10, 3, 10);
        new Item(14L, "Mithril bar", "A fresh bar of mithril.", 2, 10, 3, 20);
        new Item(15L, "Adamant bar", "A fresh bar of adamant.", 2, 10, 3, 30);
        new Item(16L, "Rune bar", "A fresh bar of rune.", 2, 10, 3, 40);
        new Item(17L, "Silver bar", "A fresh bar of silver.", 2, 10, 3, 35);
        new Item(18L, "Gold bar", "A fresh bar of gold.", 2, 10, 3, 45);
        new Item(19L, "Dragon bar", "A fresh bar of dragon.", 2, 10, 3, 60);
        new Item(20L, "Bronze dagger", "A blunt bronze dagger.", 3, 1, 5, 1);
        new Item(21L, "Bronze sword", "A fairly blunt bronze sword.", 4, 1, 10, 1);
        new Item(22L, "Bronze longsword", "A longer bronze sword.", 5, 1, 10, 1);
        new Item(23L, "Bronze bow", "A bow with bronze elements.", 6, 1, 10, 1);
        new Item(24L, "Bronze half shield", "A fairly weak bronze shield.", 7, 1, 10, 2);
        new Item(25L, "Bronze full shield", "A slightly stronger bronze shield.", 8, 1, 10, 2);
        new Item(26L, "Bronze chainmail", "A light set of bronze body armour.", 9, 1, 10, 2);
        new Item(27L, "Bronze platebody", "A heavy set of bronze body armour.", 10, 1, 15, 3);
        new Item(28L, "Bronze half helmet", "A no-frills approach to protection.", 11, 1, 10, 3);
        new Item(29L, "Bronze full helmet", "A frills approach to protection.", 12, 1, 15, 4);
        new Item(30L, "Bronze boots", "A set of boots for those that like the muddy look.", 13, 1, 5, 4);
        new Item(31L, "Bronze gloves", "A set of gloves for those that like the muddy look.", 14, 1, 5, 4);
        new Item(32L, "Bronze pickaxe", "A weak pickaxe for mining rocks.", 15, 1, 10, 4);
        new Item(33L, "Bronze hatchet", "A weak axe for cutting down trees.", 16, 1, 10, 4);
        new Item(34L, "Bronze fishing rod", "A weak rod for catching fish.", 17, 1, 10, 4);
        new Item(35L, "Bronze hammer", "A weak hammer who misses his anvil.", 18, 1, 5, 4);
        new Item(36L, "Iron dagger", "A blunt iron dagger.", 3, 2, 7, 5);
        new Item(37L, "Iron sword", "A bluntish iron sword.", 4, 2, 14, 5);
        new Item(38L, "Iron longsword", "A longer iron sword.", 5, 2, 14, 5);
        new Item(39L, "Iron bow", "A bow with iron elements.", 6, 2, 14, 5);
        new Item(40L, "Iron half shield", "An iron half shield.", 7, 2, 14, 6);
        new Item(41L, "Iron full shield", "An improved iron shield.", 8, 2, 14, 6);
        new Item(42L, "Iron chainmail", "A light set of iron body armour.", 9, 2, 14, 6);
        new Item(43L, "Iron platebody", "A heavy set of iron body armour.", 10, 2, 21, 7);
        new Item(44L, "Iron half helmet", "A tiny bit of frills approach to protection.", 11, 2, 14, 7);
        new Item(45L, "Iron full helmet", "A very frilly approach to protection.", 12, 2, 21, 8);
        new Item(46L, "Iron boots", "A set of boots for those that like the grey look.", 13, 2, 7, 8);
        new Item(47L, "Iron gloves", "A set of gloves for those that like the grey look.", 14, 2, 7, 8);
        new Item(48L, "Iron pickaxe", "A standard pickaxe for mining rocks.", 15, 2, 14, 9);
        new Item(49L, "Iron hatchet", "A standard axe for cutting down trees.", 16, 2, 14, 9);
        new Item(50L, "Iron fishing rod", "A standard rod for catching fish.", 17, 2, 14, 9);
        new Item(51L, "Iron hammer", "A standard hammer who misses his anvil.", 18, 2, 7, 9);
        new Item(52L, "Coins", "Coins! Glorious coins!", 100, 10, 1, 1);
        new Item(53L, "Steel dagger", "An average steel dagger.", 3, 3, 14, 10);
        new Item(54L, "Steel sword", "A slightly blunt steel sword.", 4, 3, 28, 10);
        new Item(55L, "Steel longsword", "An even longer steel sword.", 5, 3, 28, 10);
        new Item(56L, "Steel bow", "A bow with steel elements.", 6, 3, 28, 10);
        new Item(57L, "Steel half shield", "Steel shield, but only half.", 7, 3, 28, 12);
        new Item(58L, "Steel full shield", "Steel shield, the fullest.", 8, 3, 28, 12);
        new Item(59L, "Steel chainmail", "A light set of steel body armour.", 9, 3, 28, 12);
        new Item(60L, "Steel platebody", "A heavy set of steel body armour.", 10, 3, 42, 14);
        new Item(61L, "Steel half helmet", "A little bit frilly approach to protection.", 11, 3, 28, 14);
        new Item(62L, "Steel full helmet", "A rather frilly approach to protection.", 12, 3, 42, 16);
        new Item(63L, "Steel boots", "A set of boots for those that like the grey look, now with added shine.", 13, 3, 14, 16);
        new Item(64L, "Steel gloves", "A set of gloves for those that like the grey look, now with added shine.", 14, 3, 14, 16);
        new Item(65L, "Steel pickaxe", "A sharper pickaxe for mining rocks.", 15, 3, 28, 18);
        new Item(66L, "Steel hatchet", "A sharper axe for cutting down trees.", 16, 3, 28, 18);
        new Item(67L, "Steel fishing rod", "A sharper rod for catching fish.", 17, 3, 28, 18);
        new Item(68L, "Steel hammer", "A heavier hammer that tends to hit the anvil. Just.", 18, 3, 4, 18);
        new Item(69L, "Spidersilk", "A stand of spidersilk.", 19, 10, 1, 1);
        new Item(70L, "Silk", "A fine strip of silk.", 19, 10, 1, 1);
        new Item(71L, "Logs", "Some basic logs.",19, 10, 1, 1);
        new Item(72L, "Ruby", "A red gem.", 20, 10, 150, 12);
        new Item(73L, "Sapphire", "A blue gem.", 20, 10, 150, 12);
        new Item(74L, "Emerald", "A green gem.", 20, 10, 150, 12);
        new Item(75L, "Diamond", "A white gem.", 20, 10, 250, 15);
        new Item(76L, "Onyx", "A black gem.", 20, 10, 250, 15);
        new Item(77L, "Apple", "A nice, shiny apple.", 21, 10, 5, 1);
        new Item(78L, "Cheese", "Smells a bit cheesy.", 21, 10, 5, 1);
        new Item(79L, "Bread", "A bit stale, but edible.", 21, 10, 5, 1);
        new Item(80L, "Raw Meat", "Still dripping. Yuck.", 21, 10, 5, 1);
        new Item(81L, "Mithril dagger", "A pretty good blue-tinted dagger.", 3, 4, 40, 20);
        new Item(82L, "Mithril sword", "A slightly sharp sword, with a blue hue.", 4, 4, 70, 20);
        new Item(83L, "Mithril longsword", "item = 'long' + previousItem;", 5, 4, 70, 20);
        new Item(84L, "Mithril bow", "Use to fire arrows out of the blue.", 6, 4, 70, 20);
        new Item(85L, "Mithril half shield", "A bl shi.", 7, 4, 70, 22);
        new Item(86L, "Mithril full shield", "A blue shield.", 8, 4, 70, 22);
        new Item(87L, "Mithril chainmail", "A set of mithril chainmail, seemingly for the smaller gentleman.", 9, 4, 70, 22);
        new Item(88L, "Mithril platebody", "A more complete approach to sword-stopping.", 10, 4, 100, 24);
        new Item(89L, "Mithril half helmet", "Blue on blue for you.", 11, 4, 70, 24);
        new Item(90L, "Mithril full helmet", "Blue on more blue, still for you.", 12, 4, 100, 26);
        new Item(91L, "Mithril boots", "Note: Despite appearances, you are not walking on water.", 13, 4, 40, 26);
        new Item(92L, "Mithril gloves", "Note: Despite appearances, you are not washing your hands.", 14, 4, 40, 26);
        new Item(93L, "Mithril pickaxe", "This pick is no myth!", 15, 4, 70, 28);
        new Item(94L, "Mithril hatchet", "It's a shame magical blue trees don't exist.", 16, 4, 70, 28);
        new Item(95L, "Mithril fishing rod", "The fish seem oddly attracted to the blue metal.", 17, 4, 70, 28);
        new Item(96L, "Mithril hammer", "A heavier hammer that tends to hit the anvil. Just.", 18, 4, 40, 28);
        new Item(97L, "Adamant dagger", "A scarily sharp green dagger.", 3, 5, 40, 30);
        new Item(98L, "Adamant sword", "A rather sharp sword, with a green tint.", 4, 5, 70, 30);
        new Item(99L, "Adamant longsword", "I'm adamant that this sword is longer.", 5, 5, 70, 30);
        new Item(100L, "Adamant bow", "A fine bow, with an intriguing green hue.", 6, 5, 70, 30);
        new Item(101L, "Adamant half shield", "Don't let anyone know this shield looks like a pea.", 7, 5, 70, 32);
        new Item(102L, "Adamant full shield", "A rather impressive green shield.", 8, 5, 70, 32);
        new Item(103L, "Adamant chainmail", "A fine set of chainmail, even considering the colour.", 9, 5, 70, 32);
        new Item(104L, "Adamant platebody", "The best platebody for those that can't kill dragons.", 10, 5, 100, 34);
        new Item(105L, "Adamant half helmet", "Green on green, lookin' mean.", 11, 5, 70, 34);
        new Item(106L, "Adamant full helmet", "Gold on green, still lookin' mean.", 12, 5, 100, 36);
        new Item(107L, "Adamant boots", "Ugh, how do these even get so mould- ... oh!", 13, 5, 40, 36);
        new Item(108L, "Adamant gloves", "Maybe you should take up gardening.", 14, 5, 40, 36);
        new Item(109L, "Adamant pickaxe", "Mean, green, mining machine.", 15, 5, 70, 38);
        new Item(110L, "Adamant hatchet", "Powerful, but has a habit of blending into the target.", 16, 5, 70, 38);
        new Item(111L, "Adamant fishing rod", "The green rod seems to lure the fishies in...", 17, 5, 70, 38);
        new Item(112L, "Adamant hammer", "A very solid hammer, used for crafting powerful equipment.", 18, 5, 45, 38);
        new Item(113L, "Rune dagger", "A nifty little blade with a very sharp point.", 3, 6, 80, 40);
        new Item(114L, "Rune sword", "The rune rune rune, rune is the (s)word.", 4, 6, 140, 40);
        new Item(115L, "Rune longsword", "A very long, and very sharp, sword.", 5, 6, 140, 40);
        new Item(116L, "Rune bow", "The shortest of shortbows, the longest of longbows.", 6, 6, 140, 40);
        new Item(117L, "Rune half shield", "Looks rather like a crystal, but isn't.", 7, 6, 140, 42);
        new Item(118L, "Rune full shield", "A very nice shield, used by professional adventurers.", 8, 6, 140, 42);
        new Item(119L, "Rune chainmail", "Well, it's not a platebody, but it's still pretty good.", 9, 6, 140, 42);
        new Item(120L, "Rune platebody", "Made famous by DR4G0NK1LL3R, truly a brave adventurer.", 10, 6, 200, 44);
        new Item(121L, "Rune half helmet", "A very protective helmet, without all the trimmings.", 11, 6, 140, 44);
        new Item(122L, "Rune full helmet", "An especially protective helmet, with all the trimmings.", 12, 6, 200, 46);
        new Item(123L, "Rune boots", "Walk the streets in style with these super-comfy boots.", 13, 6, 80, 46);
        new Item(124L, "Rune gloves", "Brand new Rune gloves, now with extra protection!", 14, 6, 80, 46);
        new Item(125L, "Rune pickaxe", "A pickaxe any miner would be proud to use.", 15, 6, 140, 48);
        new Item(126L, "Rune hatchet", "An extremely sharp axe, for the mightiest of trees.", 16, 6, 140, 48);
        new Item(127L, "Rune fishing rod", "The fish won't rune away from this rod!", 17, 6, 140, 48);
        new Item(128L, "Rune hammer", "The mightiest of hammers! Or at least a very mighty one.", 18, 6, 95, 48);

        new Location(1L, "Anvil");
        new Location(2L, "Furnace");
        new Location(3L, "Selling");
        new Location(4L, "Market");
        new Location(5L, "Table");
        new Location(6L, "Enchanting");

        new Player_Info("XP", 5600);
        new Player_Info("DatabaseVersion", 1);
        new Player_Info("ItemsSmelted", 100);
        new Player_Info("ItemsCrafted", 100);
        new Player_Info("ItemsTraded", 100);
        new Player_Info("ItemsSold", 100);
        new Player_Info("ItemsBought", 1);
        new Player_Info("VisitorsCompleted", 100);
        new Player_Info("DateRestocked", System.currentTimeMillis());
        new Player_Info("DateVisitorSpawned", System.currentTimeMillis());
        new Player_Info("CoinsEarned", 100);
        new Player_Info("DateStarted", System.currentTimeMillis());
        new Player_Info("SavedLevel", 10);
        new Player_Info("UpgradesBought", 5);

        // Bars
        new Recipe(11L, 1L, 1L, 1L, 1);
        new Recipe(11L, 1L, 2L, 1L, 1);
        new Recipe(12L, 1L, 4L, 1L, 2);
        new Recipe(13L, 1L, 4L, 1L, 2);
        new Recipe(13L, 1L, 3L, 1L, 1);
        new Recipe(14L, 1L, 5L, 1L, 1);
        new Recipe(14L, 1L, 3L, 1L, 2);
        new Recipe(15L, 1L, 6L, 1L, 1);
        new Recipe(15L, 1L, 3L, 1L, 4);
        new Recipe(16L, 1L, 7L, 1L, 1);
        new Recipe(16L, 1L, 3L, 1L, 8);
        new Recipe(17L, 1L, 9L, 1L, 1);
        new Recipe(18L, 1L, 8L, 1L, 1);
        new Recipe(19L, 1L, 10L, 1L, 1);
        new Recipe(19L, 1L, 3L, 1L, 12);

        // Bronze unfinished
        new Recipe(20L, 2L, 11L, 1L, 1);
        new Recipe(21L, 2L, 11L, 1L, 2);
        new Recipe(22L, 2L, 11L, 1L, 2);
        new Recipe(23L, 2L, 11L, 1L, 2);
        new Recipe(24L, 2L, 11L, 1L, 3);
        new Recipe(25L, 2L, 11L, 1L, 3);
        new Recipe(26L, 2L, 11L, 1L, 3);
        new Recipe(27L, 2L, 11L, 1L, 5);
        new Recipe(28L, 2L, 11L, 1L, 3);
        new Recipe(29L, 2L, 11L, 1L, 4);
        new Recipe(30L, 2L, 11L, 1L, 2);
        new Recipe(31L, 2L, 11L, 1L, 2);
        new Recipe(32L, 2L, 11L, 1L, 2);
        new Recipe(33L, 2L, 11L, 1L, 2);
        new Recipe(34L, 2L, 11L, 1L, 2);
        new Recipe(35L, 2L, 11L, 1L, 1);

        // Iron unfinished
        new Recipe(36L, 2L, 12L, 1L, 1);
        new Recipe(37L, 2L, 12L, 1L, 2);
        new Recipe(38L, 2L, 12L, 1L, 2);
        new Recipe(39L, 2L, 12L, 1L, 2);
        new Recipe(40L, 2L, 12L, 1L, 3);
        new Recipe(41L, 2L, 12L, 1L, 3);
        new Recipe(42L, 2L, 12L, 1L, 3);
        new Recipe(43L, 2L, 12L, 1L, 5);
        new Recipe(44L, 2L, 12L, 1L, 3);
        new Recipe(45L, 2L, 12L, 1L, 4);
        new Recipe(46L, 2L, 12L, 1L, 2);
        new Recipe(47L, 2L, 12L, 1L, 2);
        new Recipe(48L, 2L, 12L, 1L, 2);
        new Recipe(49L, 2L, 12L, 1L, 2);
        new Recipe(50L, 2L, 12L, 1L, 2);
        new Recipe(51L, 2L, 12L, 1L, 1);

        // Steel unfinished
        new Recipe(53L, 2L, 13L, 1L, 1);
        new Recipe(54L, 2L, 13L, 1L, 2);
        new Recipe(55L, 2L, 13L, 1L, 2);
        new Recipe(56L, 2L, 13L, 1L, 2);
        new Recipe(57L, 2L, 13L, 1L, 3);
        new Recipe(58L, 2L, 13L, 1L, 3);
        new Recipe(59L, 2L, 13L, 1L, 3);
        new Recipe(60L, 2L, 13L, 1L, 5);
        new Recipe(61L, 2L, 13L, 1L, 3);
        new Recipe(62L, 2L, 13L, 1L, 4);
        new Recipe(63L, 2L, 13L, 1L, 2);
        new Recipe(64L, 2L, 13L, 1L, 2);
        new Recipe(65L, 2L, 13L, 1L, 2);
        new Recipe(66L, 2L, 13L, 1L, 2);
        new Recipe(67L, 2L, 13L, 1L, 2);
        new Recipe(68L, 2L, 13L, 1L, 1);

        // Mithril unfinished
        new Recipe(81L, 2L, 14L, 1L, 1);
        new Recipe(82L, 2L, 14L, 1L, 2);
        new Recipe(83L, 2L, 14L, 1L, 2);
        new Recipe(84L, 2L, 14L, 1L, 2);
        new Recipe(85L, 2L, 14L, 1L, 3);
        new Recipe(86L, 2L, 14L, 1L, 3);
        new Recipe(87L, 2L, 14L, 1L, 3);
        new Recipe(88L, 2L, 14L, 1L, 5);
        new Recipe(89L, 2L, 14L, 1L, 3);
        new Recipe(90L, 2L, 14L, 1L, 4);
        new Recipe(91L, 2L, 14L, 1L, 2);
        new Recipe(92L, 2L, 14L, 1L, 2);
        new Recipe(93L, 2L, 14L, 1L, 2);
        new Recipe(94L, 2L, 14L, 1L, 2);
        new Recipe(95L, 2L, 14L, 1L, 2);
        new Recipe(96L, 2L, 14L, 1L, 1);

        // Adamant unfinished
        new Recipe(97L, 2L, 15L, 1L, 1);
        new Recipe(98L, 2L, 15L, 1L, 2);
        new Recipe(99L, 2L, 15L, 1L, 2);
        new Recipe(100L, 2L, 15L, 1L, 2);
        new Recipe(101L, 2L, 15L, 1L, 3);
        new Recipe(102L, 2L, 15L, 1L, 3);
        new Recipe(103L, 2L, 15L, 1L, 3);
        new Recipe(104L, 2L, 15L, 1L, 5);
        new Recipe(105L, 2L, 15L, 1L, 3);
        new Recipe(106L, 2L, 15L, 1L, 4);
        new Recipe(107L, 2L, 15L, 1L, 2);
        new Recipe(108L, 2L, 15L, 1L, 2);
        new Recipe(109L, 2L, 15L, 1L, 2);
        new Recipe(110L, 2L, 15L, 1L, 2);
        new Recipe(111L, 2L, 15L, 1L, 2);
        new Recipe(112L, 2L, 15L, 1L, 1);

        // Rune unfinished
        new Recipe(113L, 2L, 16L, 1L, 1);
        new Recipe(114L, 2L, 16L, 1L, 2);
        new Recipe(115L, 2L, 16L, 1L, 2);
        new Recipe(116L, 2L, 16L, 1L, 2);
        new Recipe(117L, 2L, 16L, 1L, 3);
        new Recipe(118L, 2L, 16L, 1L, 3);
        new Recipe(119L, 2L, 16L, 1L, 3);
        new Recipe(120L, 2L, 16L, 1L, 5);
        new Recipe(121L, 2L, 16L, 1L, 3);
        new Recipe(122L, 2L, 16L, 1L, 4);
        new Recipe(123L, 2L, 16L, 1L, 2);
        new Recipe(124L, 2L, 16L, 1L, 2);
        new Recipe(125L, 2L, 16L, 1L, 2);
        new Recipe(126L, 2L, 16L, 1L, 2);
        new Recipe(127L, 2L, 16L, 1L, 2);
        new Recipe(128L, 2L, 16L, 1L, 1);

        // Bronze finished
        new Recipe(20L, 1L, 20L, 2L, 1);
        new Recipe(20L, 1L, 69L, 1L, 1);
        new Recipe(21L, 1L, 21L, 2L, 1);
        new Recipe(21L, 1L, 69L, 1L, 1);
        new Recipe(22L, 1L, 22L, 2L, 1);
        new Recipe(22L, 1L, 69L, 1L, 1);
        new Recipe(23L, 1L, 23L, 2L, 1);
        new Recipe(23L, 1L, 69L, 1L, 1);
        new Recipe(24L, 1L, 24L, 2L, 1);
        new Recipe(24L, 1L, 69L, 1L, 1);
        new Recipe(25L, 1L, 25L, 2L, 1);
        new Recipe(25L, 1L, 69L, 1L, 1);
        new Recipe(26L, 1L, 26L, 2L, 1);
        new Recipe(26L, 1L, 69L, 1L, 1);
        new Recipe(27L, 1L, 27L, 2L, 1);
        new Recipe(27L, 1L, 69L, 1L, 1);
        new Recipe(28L, 1L, 28L, 2L, 1);
        new Recipe(28L, 1L, 69L, 1L, 1);
        new Recipe(29L, 1L, 29L, 2L, 1);
        new Recipe(29L, 1L, 69L, 1L, 1);
        new Recipe(30L, 1L, 30L, 2L, 1);
        new Recipe(30L, 1L, 69L, 1L, 1);
        new Recipe(31L, 1L, 31L, 2L, 1);
        new Recipe(31L, 1L, 69L, 1L, 1);
        new Recipe(32L, 1L, 32L, 2L, 1);
        new Recipe(32L, 1L, 69L, 1L, 1);
        new Recipe(33L, 1L, 33L, 2L, 1);
        new Recipe(33L, 1L, 69L, 1L, 1);
        new Recipe(34L, 1L, 34L, 2L, 1);
        new Recipe(34L, 1L, 69L, 1L, 1);
        new Recipe(35L, 1L, 35L, 2L, 1);
        new Recipe(35L, 1L, 69L, 1L, 1);

        // Iron finished
        new Recipe(36L, 1L, 36L, 2L, 1);
        new Recipe(36L, 1L, 70L, 1L, 1);
        new Recipe(37L, 1L, 37L, 2L, 1);
        new Recipe(37L, 1L, 70L, 1L, 1);
        new Recipe(38L, 1L, 38L, 2L, 1);
        new Recipe(38L, 1L, 70L, 1L, 1);
        new Recipe(39L, 1L, 39L, 2L, 1);
        new Recipe(39L, 1L, 70L, 1L, 1);
        new Recipe(40L, 1L, 40L, 2L, 1);
        new Recipe(40L, 1L, 70L, 1L, 1);
        new Recipe(41L, 1L, 41L, 2L, 1);
        new Recipe(41L, 1L, 70L, 1L, 1);
        new Recipe(42L, 1L, 42L, 2L, 1);
        new Recipe(42L, 1L, 70L, 1L, 1);
        new Recipe(43L, 1L, 43L, 2L, 1);
        new Recipe(43L, 1L, 70L, 1L, 1);
        new Recipe(44L, 1L, 44L, 2L, 1);
        new Recipe(44L, 1L, 70L, 1L, 1);
        new Recipe(45L, 1L, 45L, 2L, 1);
        new Recipe(45L, 1L, 70L, 1L, 1);
        new Recipe(46L, 1L, 46L, 2L, 1);
        new Recipe(46L, 1L, 70L, 1L, 1);
        new Recipe(47L, 1L, 47L, 2L, 1);
        new Recipe(47L, 1L, 70L, 1L, 1);
        new Recipe(48L, 1L, 48L, 2L, 1);
        new Recipe(48L, 1L, 70L, 1L, 1);
        new Recipe(49L, 1L, 49L, 2L, 1);
        new Recipe(49L, 1L, 70L, 1L, 1);
        new Recipe(50L, 1L, 50L, 2L, 1);
        new Recipe(50L, 1L, 70L, 1L, 1);
        new Recipe(51L, 1L, 51L, 2L, 1);
        new Recipe(51L, 1L, 70L, 1L, 1);

        // Steel finished
        new Recipe(53L, 1L, 53L, 2L, 1);
        new Recipe(53L, 1L, 71L, 1L, 1);
        new Recipe(54L, 1L, 54L, 2L, 1);
        new Recipe(54L, 1L, 71L, 1L, 1);
        new Recipe(55L, 1L, 55L, 2L, 1);
        new Recipe(55L, 1L, 71L, 1L, 1);
        new Recipe(56L, 1L, 56L, 2L, 1);
        new Recipe(56L, 1L, 71L, 1L, 1);
        new Recipe(57L, 1L, 57L, 2L, 1);
        new Recipe(57L, 1L, 71L, 1L, 1);
        new Recipe(58L, 1L, 58L, 2L, 1);
        new Recipe(58L, 1L, 71L, 1L, 1);
        new Recipe(59L, 1L, 59L, 2L, 1);
        new Recipe(59L, 1L, 71L, 1L, 1);
        new Recipe(60L, 1L, 60L, 2L, 1);
        new Recipe(60L, 1L, 71L, 1L, 1);
        new Recipe(61L, 1L, 61L, 2L, 1);
        new Recipe(61L, 1L, 71L, 1L, 1);
        new Recipe(62L, 1L, 62L, 2L, 1);
        new Recipe(62L, 1L, 71L, 1L, 1);
        new Recipe(63L, 1L, 63L, 2L, 1);
        new Recipe(63L, 1L, 71L, 1L, 1);
        new Recipe(64L, 1L, 64L, 2L, 1);
        new Recipe(64L, 1L, 71L, 1L, 1);
        new Recipe(65L, 1L, 65L, 2L, 1);
        new Recipe(65L, 1L, 71L, 1L, 1);
        new Recipe(66L, 1L, 66L, 2L, 1);
        new Recipe(66L, 1L, 71L, 1L, 1);
        new Recipe(67L, 1L, 67L, 2L, 1);
        new Recipe(67L, 1L, 71L, 1L, 1);
        new Recipe(68L, 1L, 68L, 2L, 1);
        new Recipe(68L, 1L, 71L, 1L, 1);

        // Mithril finished
        new Recipe(81L, 1L, 81L, 2L, 1);
        new Recipe(81L, 1L, 77L, 1L, 1);
        new Recipe(82L, 1L, 82L, 2L, 1);
        new Recipe(82L, 1L, 77L, 1L, 1);
        new Recipe(83L, 1L, 83L, 2L, 1);
        new Recipe(83L, 1L, 77L, 1L, 1);
        new Recipe(84L, 1L, 84L, 2L, 1);
        new Recipe(84L, 1L, 77L, 1L, 1);
        new Recipe(85L, 1L, 85L, 2L, 1);
        new Recipe(85L, 1L, 77L, 1L, 1);
        new Recipe(86L, 1L, 86L, 2L, 1);
        new Recipe(86L, 1L, 77L, 1L, 1);
        new Recipe(87L, 1L, 87L, 2L, 1);
        new Recipe(87L, 1L, 77L, 1L, 1);
        new Recipe(88L, 1L, 88L, 2L, 1);
        new Recipe(88L, 1L, 77L, 1L, 1);
        new Recipe(89L, 1L, 89L, 2L, 1);
        new Recipe(89L, 1L, 77L, 1L, 1);
        new Recipe(90L, 1L, 90L, 2L, 1);
        new Recipe(90L, 1L, 77L, 1L, 1);
        new Recipe(91L, 1L, 91L, 2L, 1);
        new Recipe(91L, 1L, 77L, 1L, 1);
        new Recipe(92L, 1L, 92L, 2L, 1);
        new Recipe(92L, 1L, 77L, 1L, 1);
        new Recipe(93L, 1L, 93L, 2L, 1);
        new Recipe(93L, 1L, 77L, 1L, 1);
        new Recipe(94L, 1L, 94L, 2L, 1);
        new Recipe(94L, 1L, 77L, 1L, 1);
        new Recipe(95L, 1L, 95L, 2L, 1);
        new Recipe(95L, 1L, 77L, 1L, 1);
        new Recipe(96L, 1L, 96L, 2L, 1);
        new Recipe(96L, 1L, 77L, 1L, 1);

        // Adamant finished
        new Recipe(97L, 1L, 97L, 2L, 1);
        new Recipe(97L, 1L, 79L, 1L, 1);
        new Recipe(98L, 1L, 98L, 2L, 1);
        new Recipe(98L, 1L, 79L, 1L, 1);
        new Recipe(99L, 1L, 99L, 2L, 1);
        new Recipe(99L, 1L, 79L, 1L, 1);
        new Recipe(100L, 1L, 100L, 2L, 1);
        new Recipe(100L, 1L, 79L, 1L, 1);
        new Recipe(101L, 1L, 101L, 2L, 1);
        new Recipe(101L, 1L, 79L, 1L, 1);
        new Recipe(102L, 1L, 102L, 2L, 1);
        new Recipe(102L, 1L, 79L, 1L, 1);
        new Recipe(103L, 1L, 103L, 2L, 1);
        new Recipe(103L, 1L, 79L, 1L, 1);
        new Recipe(104L, 1L, 104L, 2L, 1);
        new Recipe(104L, 1L, 79L, 1L, 1);
        new Recipe(105L, 1L, 105L, 2L, 1);
        new Recipe(105L, 1L, 79L, 1L, 1);
        new Recipe(106L, 1L, 106L, 2L, 1);
        new Recipe(106L, 1L, 79L, 1L, 1);
        new Recipe(107L, 1L, 107L, 2L, 1);
        new Recipe(107L, 1L, 79L, 1L, 1);
        new Recipe(108L, 1L, 108L, 2L, 1);
        new Recipe(108L, 1L, 79L, 1L, 1);
        new Recipe(109L, 1L, 109L, 2L, 1);
        new Recipe(109L, 1L, 79L, 1L, 1);
        new Recipe(110L, 1L, 110L, 2L, 1);
        new Recipe(110L, 1L, 79L, 1L, 1);
        new Recipe(111L, 1L, 111L, 2L, 1);
        new Recipe(111L, 1L, 79L, 1L, 1);
        new Recipe(112L, 1L, 112L, 2L, 1);
        new Recipe(112L, 1L, 79L, 1L, 1);

        // Rune finished
        new Recipe(113L, 1L, 113L, 2L, 1);
        new Recipe(113L, 1L, 16L, 1L, 1);
        new Recipe(114L, 1L, 114L, 2L, 1);
        new Recipe(114L, 1L, 16L, 1L, 1);
        new Recipe(115L, 1L, 115L, 2L, 1);
        new Recipe(115L, 1L, 16L, 1L, 1);
        new Recipe(116L, 1L, 116L, 2L, 1);
        new Recipe(116L, 1L, 16L, 1L, 1);
        new Recipe(117L, 1L, 117L, 2L, 1);
        new Recipe(117L, 1L, 16L, 1L, 1);
        new Recipe(118L, 1L, 118L, 2L, 1);
        new Recipe(118L, 1L, 16L, 1L, 1);
        new Recipe(119L, 1L, 119L, 2L, 1);
        new Recipe(119L, 1L, 16L, 1L, 1);
        new Recipe(120L, 1L, 120L, 2L, 1);
        new Recipe(120L, 1L, 16L, 1L, 1);
        new Recipe(121L, 1L, 121L, 2L, 1);
        new Recipe(121L, 1L, 16L, 1L, 1);
        new Recipe(122L, 1L, 122L, 2L, 1);
        new Recipe(122L, 1L, 16L, 1L, 1);
        new Recipe(123L, 1L, 123L, 2L, 1);
        new Recipe(123L, 1L, 16L, 1L, 1);
        new Recipe(124L, 1L, 124L, 2L, 1);
        new Recipe(124L, 1L, 16L, 1L, 1);
        new Recipe(125L, 1L, 125L, 2L, 1);
        new Recipe(125L, 1L, 16L, 1L, 1);
        new Recipe(126L, 1L, 126L, 2L, 1);
        new Recipe(126L, 1L, 16L, 1L, 1);
        new Recipe(127L, 1L, 127L, 2L, 1);
        new Recipe(127L, 1L, 16L, 1L, 1);
        new Recipe(128L, 1L, 128L, 2L, 1);
        new Recipe(128L, 1L, 16L, 1L, 1);

        new Setting(1L, "Sounds", false);
        new Setting(2L, "Music", false);
        new Setting(3L, "RestockNotifications", false);
        new Setting(4L, "NotificationSounds", false);
        new Setting(5L, "VisitorNotifications", false);

        new Trader(1, 4, "Poor Ore", "Full of low quality ore.", 1, 0, 0, 60);
        new Trader(2, 4, "Less Poor Ore", "The ore here is not so poor.", 5, 0, 0, 40);
        new Trader(1, 4, "Rare Ore", "This trader is too rare to be found.", 5, 0, 0, 5);
        new Trader(1, 4, "Average Ore", "The ore in store is not too poor.", 10, 0, 0, 15);
        new Trader(2, 4, "Silver Miner", "Cor, ore!", 10, 0, 0, 5);

        new Trader_Stock(1L, 1L, 1, 0, 5, 50);
        new Trader_Stock(1L, 2L, 1, 1, 5, 50);
        new Trader_Stock(2L, 1L, 1, 0, 10, 100);
        new Trader_Stock(2L, 2L, 1, 10, 10, 100);
        new Trader_Stock(3L, 3L, 1, 0, 10, 100);
        new Trader_Stock(4L, 2L, 1, 0, 10, 100);
        new Trader_Stock(5L, 9L, 1, 0, 1, 10);
        new Trader_Stock(5L, 8L, 1, 1, 1, 10);

        new Slot(1, 0, 0);
        new Slot(1, 5, 0);
        new Slot(1, 10, 0);
        new Slot(2, 0, 0);
        new Slot(2, 5, 0);
        new Slot(2, 10, 0);
        new Slot(3, 0, 0);
        new Slot(3, 5, 0);
        new Slot(3, 10, 0);
        new Slot(4, 0, 0);
        new Slot(4, 5, 0);
        new Slot(4, 10, 0);
        new Slot(5, 1, 0);
        new Slot(5, 5, 0);
        new Slot(5, 5, 0);
        new Slot(5, 10, 0);
        new Slot(6, 10, 0);
        new Slot(6, 10, 0);
        new Slot(6, 10, 0);
        new Slot(6, 10, 0);
        new Slot(6, 10, 0);

        new State(1L, "Normal", "", 0L, 15);
        new State(2L, "Unfinished", "(unf) ", 0L, 15);
        new State(3L, "Red Enchant", "(red) ", 72L, 2);
        new State(4L, "Blue Enchant", "(blue) ", 73L, 2);
        new State(5L, "Green Enchant", "(green) ", 74L, 1);
        new State(6L, "White Enchant", "(white) ", 75L, 1);
        new State(7L, "Black Enchant", "(black) ", 76L, 1);

        new Tier(1L, "Bronze", 30);
        new Tier(2L, "Iron", 25);
        new Tier(3L, "Steel", 15);
        new Tier(4L, "Mithril", 10);
        new Tier(5L, "Adamant", 7);
        new Tier(6L, "Rune", 4);
        new Tier(7L, "Dragon", 2);
        new Tier(8L, "Silver", 10);
        new Tier(9L, "Gold", 10);
        new Tier(10L, "None", 35);

        new Type(1L, "Ore", 1, 30);
        new Type(2L, "Bar", 1, 30);
        new Type(3L, "Dagger", 2, 25);
        new Type(4L, "Sword", 2, 25);
        new Type(5L, "Longsword", 2, 25);
        new Type(6L, "Bow", 2, 20);
        new Type(7L, "Half Shield", 3, 20);
        new Type(8L, "Full Shield", 3, 20);
        new Type(9L, "Chainmail", 3, 20);
        new Type(10L, "Platebody", 3, 15);
        new Type(11L, "Half Helmet", 3, 15);
        new Type(12L, "Full Helmet", 3, 15);
        new Type(13L, "Boots", 3, 15);
        new Type(14L, "Gloves", 3, 10);
        new Type(15L, "Pickaxe", 4, 10);
        new Type(16L, "Hatchet", 4, 10);
        new Type(17L, "Fishing Rod", 4, 10);
        new Type(18L, "Hammer", 4, 10);
        new Type(19L, "Secondary", 1, 30);
        new Type(20L, "Gem", 1, 5);
        new Type(21L, "Food", 2, 30);
        new Type(100L, "Internal", 0, 0);

        new Upgrade("Visitor Spawn Time", "mins", 1000, 25, 10, 25);
        new Upgrade("Shop Restock Time", "hours", 250, 24, 2, 24);
        new Upgrade("Maximum Visitors", "visitors", 1000, 2, 10, 2);
        new Upgrade("Maximum Traders", "traders", 250, 3, 10, 3);
        new Upgrade("Gold Bonus", "%", 250, 0, 20, 0);
        new Upgrade("XP Bonus", "%", 250, 0, 20, 0);

        new Visitor_Stats(1L, 99, 1L, 1L, 1, 1452022352000L, 1454841674000L);
        new Visitor_Stats(2L, 99, 2L, 2L, 2, 1452022352000L, 1454841674000L);
        new Visitor_Stats(3L, 99, 3L, 3L, 3, 1452022352000L, 1454841674000L);
        new Visitor_Stats(4L, 99, 4L, 4L, 4, 1452022352000L, 1454841674000L);
        new Visitor_Stats(5L, 99, 5L, 1L, 5, 1452022352000L, 1454841674000L);
        new Visitor_Stats(6L, 99, 52L, 1L, 1, 1452022352000L, 1454841674000L);
        new Visitor_Stats(7L, 99, 52L, 1L, 1, 1452022352000L, 1454841674000L);
        new Visitor_Stats(8L, 99, 52L, 1L, 1, 1452022352000L, 1454841674000L);
        new Visitor_Stats(9L, 99, 52L, 1L, 1, 1452022352000L, 1454841674000L);
        new Visitor_Stats(10L, 99, 52L, 1L, 1, 1452022352000L, 1454841674000L);
        new Visitor_Stats(11L, 99, 52L, 1L, 1, 1452022352000L, 1454841674000L);
        new Visitor_Stats(12L, 99, 52L, 1L, 1, 1452022352000L, 1454841674000L);
        new Visitor_Stats(13L, 99, 52L, 1L, 1, 1452022352000L, 1454841674000L);

        new Visitor_Type(1L, "Senor Spicy Hot", "I like unfinished things, they burn better!", 1L, 14L, 2L, 1.1, 1.1, 3.0, false, false, false, 3);
        new Visitor_Type(2L, "Mister Hatchet", "If only I was a woodcutter...", 3L, 16L, 1L, 1.2, 1.2, 1.2, false, false, false, 6);
        new Visitor_Type(3L, "Lord of the Junk", "It's not rubbish, it's treasure!", 1L, 3L, 2L, 1.05, 1.05, 1.05, false, false, false, 10);
        new Visitor_Type(4L, "Monsieur Fancypants", "Only the best for me, old chap.", 7L, 10L, 4L, 1.6, 1.55, 1.5, false, false, false, 1);
        new Visitor_Type(5L, "Grumbling Rock", "Me hungry. Tummy rumbling.", 10L, 1L, 1L, 1.05, 1.4, 1.05, false, false, false, 5);
        new Visitor_Type(6L, "Large Grumbling Rock", "Me very hungry. Tummy rumbling loud.", 10L, 1L, 1L, 1.6, 1.8, 1.05, false, false, false, 1);
        new Visitor_Type(7L, "R. De Couleur", "They say I'm the short-tempered one, but I just like the colour.", 7L, 20L, 3L, 1.25, 1.05, 1.66, false, false, false, 2);
        new Visitor_Type(8L, "G. De Couleur", "They say I'm the jealous one, but I just like the colour.", 5L, 20L, 4L, 1.25, 1.05, 1.66, false, false, false, 2);
        new Visitor_Type(9L, "B. De Couleur", "They say I'm the unhappy one, but I just like the colour.", 6L, 20L, 5L, 1.25, 1.05, 1.66, false, false, false, 2);
        new Visitor_Type(10L, "W. De Couleur", "They say I'm the scared one, but I just like the colour.", 8L, 20L, 6L, 1.25, 1.05, 1.66, false, false, false, 1);
        new Visitor_Type(11L, "B. De Couleur", "They say I'm the scary one, but I just like the colour.", 10L, 20L, 7L, 1.25, 1.05, 1.66, false, false, false, 1);
        new Visitor_Type(12L, "Gelatinous Egg", "So what if I'm not quite in shape?", 10L, 19L, 2L, 1.05, 1.15, 1.1, false, false, false, 8);
        new Visitor_Type(13L, "The Great Cheese Menace", "You there! Got any of the yellow stuff?", 10L, 21L, 1L, 1.05, 1.55, 1.05, false, false, false, 6);
    }

}

