package uk.co.jakelee.blacksmith.helper;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmith.model.Achievement;
import uk.co.jakelee.blacksmith.model.Category;
import uk.co.jakelee.blacksmith.model.Character;
import uk.co.jakelee.blacksmith.model.Criteria;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Location;
import uk.co.jakelee.blacksmith.model.Message;
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
import uk.co.jakelee.blacksmith.model.Visitor;
import uk.co.jakelee.blacksmith.model.Visitor_Demand;
import uk.co.jakelee.blacksmith.model.Visitor_Stats;
import uk.co.jakelee.blacksmith.model.Visitor_Type;

public class DatabaseHelper {
    public final static int DB_EMPTY = 0;
    public final static int DB_V1_0_0 = 1;
    public final static int DB_V1_0_1 = 2;

    public static void initialSQL() {
        createAchievement();
        createCategory();
        createCharacter();
        createCriteria();
        createInventory();
        createItem();
        createLocation();
        createMessage();
        createPlayerInfo();
        createRecipe();
        createSetting();
        createSlot();
        createState();
        createTier();
        createTrader();
        createType();
        createUpgrade();
        createVisitor();
        createVisitorDemand();
        createVisitorStats();
        createVisitorType();
    }

    public static void patch100to101() {
        Log.d("BlacksmithDB", "Updating database version from V1.0.0 to V1.0.1");
    }

    private static void createAchievement() {
        List<Achievement> achievements = new ArrayList<>();
        
        achievements.add(new Achievement("Open For Business 1", "Completed a visitor", 1, 9, "CgkI6tnE2Y4OEAIQAg"));
        achievements.add(new Achievement("Open For Business 2", "Completed 10 visitors", 10, 9, "CgkI6tnE2Y4OEAIQAw"));
        achievements.add(new Achievement("Open For Business 3", "Completed 100 visitors", 100, 9, "CgkI6tnE2Y4OEAIQBA"));
        achievements.add(new Achievement("Open For Business 4", "Completed 1000 visitors", 1000, 9, "CgkI6tnE2Y4OEAIQBQ"));
        achievements.add(new Achievement("The Forge Is Hot 1", "Smelted a bar", 1, 3, "CgkI6tnE2Y4OEAIQBg"));
        achievements.add(new Achievement("The Forge Is Hot 2", "Smelted 10 bars", 10, 3, "CgkI6tnE2Y4OEAIQBw"));
        achievements.add(new Achievement("The Forge Is Hot 3", "Smelted 100 bars", 100, 3, "CgkI6tnE2Y4OEAIQCg"));
        achievements.add(new Achievement("The Forge Is Hot 4", "Smelted 1000 bars", 1000, 3, "CgkI6tnE2Y4OEAIQCw"));
        achievements.add(new Achievement("Like Hot Cakes 1", "Sell an item to visitors", 1, 5, "CgkI6tnE2Y4OEAIQDA"));
        achievements.add(new Achievement("Like Hot Cakes 2", "Sell 10 items to visitors", 10, 5, "CgkI6tnE2Y4OEAIQDQ"));
        achievements.add(new Achievement("Like Hot Cakes 3", "Sell 100 items to visitors", 100, 5, "CgkI6tnE2Y4OEAIQDg"));
        achievements.add(new Achievement("Like Hot Cakes 4", "Sell 1000 items to visitors", 1000, 5, "CgkI6tnE2Y4OEAIQDw"));
        achievements.add(new Achievement("Supply And Demand 1", "Buy an item from the marketplace", 1, 8, "CgkI6tnE2Y4OEAIQEA"));
        achievements.add(new Achievement("Supply And Demand 2", "Buy 10 items from the marketplace", 10, 8, "CgkI6tnE2Y4OEAIQEQ"));
        achievements.add(new Achievement("Supply And Demand 3", "Buy 100 items from the marketplace", 100, 8, "CgkI6tnE2Y4OEAIQEg"));
        achievements.add(new Achievement("Supply And Demand 4", "Buy 1000 items from the marketplace", 1000, 8, "CgkI6tnE2Y4OEAIQEw"));
        achievements.add(new Achievement("Mr Moneybags 1", "Earn 200 coins", 200, 12, "CgkI6tnE2Y4OEAIQFA"));
        achievements.add(new Achievement("Mr Moneybags 2", "Earn 1000 coins", 1000, 12, "CgkI6tnE2Y4OEAIQFQ"));
        achievements.add(new Achievement("Mr Moneybags 3", "Earn 3000 coins", 3000, 12, "CgkI6tnE2Y4OEAIQFg"));
        achievements.add(new Achievement("Mr Moneybags 4", "Earn 10000 coins", 10000, 12, "CgkI6tnE2Y4OEAIQFw"));
        achievements.add(new Achievement("Mastery Of Bronze", "Reach level 5", 5, 14, "CgkI6tnE2Y4OEAIQGA"));
        achievements.add(new Achievement("Mastery Of Iron", "Reach level 10", 10, 14, "CgkI6tnE2Y4OEAIQGQ"));
        achievements.add(new Achievement("Mastery Of Steel", "Reach level 20", 20, 14, "CgkI6tnE2Y4OEAIQGg"));
        achievements.add(new Achievement("Mastery Of Mithril", "Reach level 30", 30, 14, "CgkI6tnE2Y4OEAIQGw"));
        achievements.add(new Achievement("Mastery Of Adamant", "Reach level 40", 40, 14, "CgkI6tnE2Y4OEAIQHA"));
        achievements.add(new Achievement("Mastery Of Jewellery", "Reach level 45", 45, 14, "CgkI6tnE2Y4OEAIQHQ"));
        achievements.add(new Achievement("Mastery Of Rune", "Reach level 50", 50, 14, "CgkI6tnE2Y4OEAIQHg"));
        achievements.add(new Achievement("Mastery Of Dragon", "Reach level 60", 60, 14, "CgkI6tnE2Y4OEAIQHw"));
        achievements.add(new Achievement("The Fun Never Stops", "Prestige", 1, 17, "CgkI6tnE2Y4OEAIQIA"));

        Achievement.saveInTx(achievements);
    }

    private static void createCategory() {
        List<Category> categories = new ArrayList<>();
        
        categories.add(new Category("Unknown", "Item type category could not be found."));
        categories.add(new Category("Crafting", "An item that can be used for crafting."));
        categories.add(new Category("Weapon", "An item that can be used as a weapon."));
        categories.add(new Category("Armour", "An item that can be used as armour."));
        categories.add(new Category("Skill", "An item that can be used to train a skill."));
        categories.add(new Category("Rare", "An item that is extremely rare, and hard to obtain."));

        Category.saveInTx(categories);
    }

    private static void createCharacter() {
        List<Character> characters = new ArrayList<>();
        
        characters.add(new Character(1L, "Sean Keeper", "Greetings! See anything you like?"));
        characters.add(new Character(2L, "The Iron Knight", "Look, it's knight with a k, okay?"));
        characters.add(new Character(3L, "The Golden Warrior", "Can never have enough golden goldy gold. EVER."));
        characters.add(new Character(4L, "Cloth King", "Want clothing? You're gonna need some cloth!"));
        characters.add(new Character(5L, "Woodchuck", "How much wood can I cut? Enough!"));
        characters.add(new Character(6L, "Oog", "Oog oog!!!"));
        characters.add(new Character(7L, "GemBot 5000", "Bzzt!"));
        characters.add(new Character(8L, "Farmer's Little Friend", "You'd better not have any weedkiller on you."));
        characters.add(new Character(9L, "Death Beetle", "I am an exclusive trader."));

        Character.saveInTx(characters);
    }

    private static void createCriteria() {
        List<Criteria> criterias = new ArrayList<>();
        
        criterias.add(new Criteria(1L, "State"));
        criterias.add(new Criteria(2L, "Tier"));
        criterias.add(new Criteria(3L, "Type"));
        
        Criteria.saveInTx(criterias);
    }

    public static void createInventory() {
        List<Inventory> inventories = new ArrayList<>();

        // 200 gold
        inventories.add(new Inventory(52L, Constants.STATE_NORMAL, 200));

        // 15 copper & tin.
        inventories.add(new Inventory(1L, Constants.STATE_NORMAL, 15));
        inventories.add(new Inventory(2L, Constants.STATE_NORMAL, 15));

        // 2 sapphires
        inventories.add(new Inventory(73L, Constants.STATE_NORMAL, 2));

        // 5 apples & cheese
        inventories.add(new Inventory(77L, Constants.STATE_NORMAL, 5));
        inventories.add(new Inventory(78L, Constants.STATE_NORMAL, 5));

        // 0 bronze bars + daggers, so that they are visible
        inventories.add(new Inventory(11L, Constants.STATE_NORMAL, 1));
        inventories.add(new Inventory(20L, Constants.STATE_NORMAL, 2));

        Inventory.saveInTx(inventories);

        /*new Inventory(1L, 1, 101);
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
        new Inventory(129L, 1, 100);
        new Inventory(130L, 1, 100);
        new Inventory(131L, 1, 100);
        new Inventory(148L, 1, 100);*/
    }

    private static void createItem() {
        List<Item> items = new ArrayList<>();
        
        items.add(new Item(1L, "Copper ore", "A piece of copper ore.", 1, 11, 2, 0));
        items.add(new Item(2L, "Tin ore", "A piece of tin ore.", 1, 11, 2, 0));
        items.add(new Item(3L, "Coal", "A piece of coal.", 1, 11, 5, 0));
        items.add(new Item(4L, "Iron ore", "A piece of iron ore.", 1, 11, 7, 0));
        items.add(new Item(5L, "Mithril ore", "A piece of mithril ore.", 1, 11, 12, 0));
        items.add(new Item(6L, "Adamantite ore", "A piece of adamantite ore.", 1, 11, 25, 0));
        items.add(new Item(7L, "Runite ore", "A piece of runite ore.", 1, 11, 50, 0));
        items.add(new Item(8L, "Gold nugget", "A golden nugget.", 1, 11, 30, 0));
        items.add(new Item(9L, "Silver nugget", "A silver nugget.", 1, 11, 20, 0));
        items.add(new Item(10L, "Dragonite ore", "A piece of dragonite ore.", 1, 11, 100, 0));
        items.add(new Item(11L, "Bronze bar", "A fresh bar of bronze.", 2, 11, 7, 1));
        items.add(new Item(12L, "Iron bar", "A fresh bar of iron.", 2, 11, 15, 5));
        items.add(new Item(13L, "Steel bar", "A fresh bar of steel.", 2, 11, 20, 10));
        items.add(new Item(14L, "Mithril bar", "A fresh bar of mithril.", 2, 11, 24, 20));
        items.add(new Item(15L, "Adamant bar", "A fresh bar of adamant.", 2, 11, 50, 30));
        items.add(new Item(16L, "Rune bar", "A fresh bar of rune.", 2, 11, 100, 40));
        items.add(new Item(17L, "Silver bar", "A fresh bar of silver.", 2, 11, 22, 35));
        items.add(new Item(18L, "Gold bar", "A fresh bar of gold.", 2, 11, 35, 45));
        items.add(new Item(19L, "Dragon bar", "A fresh bar of dragon.", 2, 11, 175, 50));
        items.add(new Item(20L, "Bronze dagger", "A blunt bronze dagger.", 3, 1, 8, 1));
        items.add(new Item(21L, "Bronze sword", "A fairly blunt bronze sword.", 4, 1, 15, 1));
        items.add(new Item(22L, "Bronze longsword", "A longer bronze sword.", 5, 1, 15, 1));
        items.add(new Item(23L, "Bronze bow", "A bow with bronze elements.", 6, 1, 15, 1));
        items.add(new Item(24L, "Bronze half shield", "A fairly weak bronze shield.", 7, 1, 15, 2));
        items.add(new Item(25L, "Bronze full shield", "A slightly stronger bronze shield.", 8, 1, 15, 2));
        items.add(new Item(26L, "Bronze chainmail", "A light set of bronze body armour.", 9, 1, 15, 2));
        items.add(new Item(27L, "Bronze platebody", "A heavy set of bronze body armour.", 10, 1, 29, 3));
        items.add(new Item(28L, "Bronze half helmet", "A no-frills approach to protection.", 11, 1, 15, 3));
        items.add(new Item(29L, "Bronze full helmet", "A frills approach to protection.", 12, 1, 22, 4));
        items.add(new Item(30L, "Bronze boots", "A set of boots for those that like the muddy look.", 13, 1, 15, 4));
        items.add(new Item(31L, "Bronze gloves", "A set of gloves for those that like the muddy look.", 14, 1, 15, 4));
        items.add(new Item(32L, "Bronze pickaxe", "A weak pickaxe for mining rocks.", 15, 1, 15, 4));
        items.add(new Item(33L, "Bronze hatchet", "A weak axe for cutting down trees.", 16, 1, 15, 4));
        items.add(new Item(34L, "Bronze fishing rod", "A weak rod for catching fish.", 17, 1, 15, 4));
        items.add(new Item(35L, "Bronze hammer", "A weak hammer who misses his anvil.", 18, 1, 8, 4));
        items.add(new Item(36L, "Iron dagger", "A blunt iron dagger.", 3, 2, 17, 5));
        items.add(new Item(37L, "Iron sword", "A bluntish iron sword.", 4, 2, 32, 5));
        items.add(new Item(38L, "Iron longsword", "A longer iron sword.", 5, 2, 32, 5));
        items.add(new Item(39L, "Iron bow", "A bow with iron elements.", 6, 2, 32, 5));
        items.add(new Item(40L, "Iron half shield", "An iron half shield.", 7, 2, 47, 6));
        items.add(new Item(41L, "Iron full shield", "An improved iron shield.", 8, 2, 47, 6));
        items.add(new Item(42L, "Iron chainmail", "A light set of iron body armour.", 9, 2, 47, 6));
        items.add(new Item(43L, "Iron platebody", "A heavy set of iron body armour.", 10, 2, 77, 7));
        items.add(new Item(44L, "Iron half helmet", "A tiny bit of frills approach to protection.", 11, 2, 47, 7));
        items.add(new Item(45L, "Iron full helmet", "A very frilly approach to protection.", 12, 2, 62, 8));
        items.add(new Item(46L, "Iron boots", "A set of boots for those that like the grey look.", 13, 2, 32, 8));
        items.add(new Item(47L, "Iron gloves", "A set of gloves for those that like the grey look.", 14, 2, 32, 8));
        items.add(new Item(48L, "Iron pickaxe", "A standard pickaxe for mining rocks.", 15, 2, 32, 9));
        items.add(new Item(49L, "Iron hatchet", "A standard axe for cutting down trees.", 16, 2, 32, 9));
        items.add(new Item(50L, "Iron fishing rod", "A standard rod for catching fish.", 17, 2, 32, 9));
        items.add(new Item(51L, "Iron hammer", "A standard hammer who misses his anvil.", 18, 2, 17, 9));
        items.add(new Item(52L, "Coins", "Coins! Glorious coins!", 100, 11, 1, 1));
        items.add(new Item(53L, "Steel dagger", "An average steel dagger.", 3, 3, 23, 10));
        items.add(new Item(54L, "Steel sword", "A slightly blunt steel sword.", 4, 3, 43, 10));
        items.add(new Item(55L, "Steel longsword", "An even longer steel sword.", 5, 3, 43, 10));
        items.add(new Item(56L, "Steel bow", "A bow with steel elements.", 6, 3, 43, 10));
        items.add(new Item(57L, "Steel half shield", "Steel shield, but only half.", 7, 3, 63, 12));
        items.add(new Item(58L, "Steel full shield", "Steel shield, the fullest.", 8, 3, 63, 12));
        items.add(new Item(59L, "Steel chainmail", "A light set of steel body armour.", 9, 3, 63, 12));
        items.add(new Item(60L, "Steel platebody", "A heavy set of steel body armour.", 10, 3, 103, 14));
        items.add(new Item(61L, "Steel half helmet", "A little bit frilly approach to protection.", 11, 3, 63, 14));
        items.add(new Item(62L, "Steel full helmet", "A rather frilly approach to protection.", 12, 3, 83, 16));
        items.add(new Item(63L, "Steel boots", "A set of boots for those that like the grey look, now with added shine.", 13, 3, 43, 16));
        items.add(new Item(64L, "Steel gloves", "A set of gloves for those that like the grey look, now with added shine.", 14, 3, 43, 16));
        items.add(new Item(65L, "Steel pickaxe", "A sharper pickaxe for mining rocks.", 15, 3, 43, 18));
        items.add(new Item(66L, "Steel hatchet", "A sharper axe for cutting down trees.", 16, 3, 43, 18));
        items.add(new Item(67L, "Steel fishing rod", "A sharper rod for catching fish.", 17, 3, 43, 18));
        items.add(new Item(68L, "Steel hammer", "A heavier hammer that tends to hit the anvil. Just.", 18, 3, 23, 18));
        items.add(new Item(69L, "Spidersilk", "A stand of spidersilk.", 19, 11, 1, 1));
        items.add(new Item(70L, "Silk", "A fine strip of silk.", 19, 11, 1, 1));
        items.add(new Item(71L, "Logs", "Some basic logs.", 19, 11, 1, 1));
        items.add(new Item(72L, "Ruby", "A red gem.", 20, 11, 150, 12));
        items.add(new Item(73L, "Sapphire", "A blue gem.", 20, 11, 150, 12));
        items.add(new Item(74L, "Emerald", "A green gem.", 20, 11, 150, 12));
        items.add(new Item(75L, "Diamond", "A white gem.", 20, 11, 250, 15));
        items.add(new Item(76L, "Onyx", "A black gem.", 20, 11, 250, 15));
        items.add(new Item(77L, "Apple", "A nice, shiny apple.", 21, 11, 5, 1));
        items.add(new Item(78L, "Cheese", "Smells a bit cheesy.", 21, 11, 5, 1));
        items.add(new Item(79L, "Bread", "A bit stale, but edible.", 21, 11, 5, 1));
        items.add(new Item(80L, "Raw Meat", "Still dripping. Yuck.", 21, 11, 5, 1));
        items.add(new Item(81L, "Mithril dagger", "A pretty good blue-tinted dagger.", 3, 4, 29, 20));
        items.add(new Item(82L, "Mithril sword", "A slightly sharp sword, with a blue hue.", 4, 4, 53, 20));
        items.add(new Item(83L, "Mithril longsword", "item = 'long' + previousItem;", 5, 4, 53, 20));
        items.add(new Item(84L, "Mithril bow", "Use to fire arrows out of the blue.", 6, 4, 53, 20));
        items.add(new Item(85L, "Mithril half shield", "A bl shi.", 7, 4, 77, 22));
        items.add(new Item(86L, "Mithril full shield", "A blue shield.", 8, 4, 77, 22));
        items.add(new Item(87L, "Mithril chainmail", "A set of mithril chainmail, seemingly for the smaller gentleman.", 9, 4, 77, 22));
        items.add(new Item(88L, "Mithril platebody", "A more complete approach to sword-stopping.", 10, 4, 125, 24));
        items.add(new Item(89L, "Mithril half helmet", "Blue on blue for you.", 11, 4, 77, 24));
        items.add(new Item(90L, "Mithril full helmet", "Blue on more blue, still for you.", 12, 4, 101, 26));
        items.add(new Item(91L, "Mithril boots", "Note: Despite appearances, you are not walking on water.", 13, 4, 53, 26));
        items.add(new Item(92L, "Mithril gloves", "Note: Despite appearances, you are not washing your hands.", 14, 4, 53, 26));
        items.add(new Item(93L, "Mithril pickaxe", "This pick is no myth!", 15, 4, 53, 28));
        items.add(new Item(94L, "Mithril hatchet", "It's a shame magical blue trees don't exist.", 16, 4, 53, 28));
        items.add(new Item(95L, "Mithril fishing rod", "The fish seem oddly attracted to the blue metal.", 17, 4, 53, 28));
        items.add(new Item(96L, "Mithril hammer", "A heavier hammer that tends to hit the anvil. Just.", 18, 4, 29, 28));
        items.add(new Item(97L, "Adamant dagger", "A scarily sharp green dagger.", 3, 5, 57, 30));
        items.add(new Item(98L, "Adamant sword", "A rather sharp sword, with a green tint.", 4, 5, 107, 30));
        items.add(new Item(99L, "Adamant longsword", "I'm adamant that this sword is longer.", 5, 5, 107, 30));
        items.add(new Item(100L, "Adamant bow", "A fine bow, with an intriguing green hue.", 6, 5, 107, 30));
        items.add(new Item(101L, "Adamant half shield", "Don't let anyone know this shield looks like a pea.", 7, 5, 157, 32));
        items.add(new Item(102L, "Adamant full shield", "A rather impressive green shield.", 8, 5, 157, 32));
        items.add(new Item(103L, "Adamant chainmail", "A fine set of chainmail, even considering the colour.", 9, 5, 157, 32));
        items.add(new Item(104L, "Adamant platebody", "The best platebody for those that can't kill dragons.", 10, 5, 257, 34));
        items.add(new Item(105L, "Adamant half helmet", "Green on green, lookin' mean.", 11, 5, 157, 34));
        items.add(new Item(106L, "Adamant full helmet", "Gold on green, still lookin' mean.", 12, 5, 207, 36));
        items.add(new Item(107L, "Adamant boots", "Ugh, how do these even get so mould- ... oh!", 13, 5, 107, 36));
        items.add(new Item(108L, "Adamant gloves", "Maybe you should take up gardening.", 14, 5, 107, 36));
        items.add(new Item(109L, "Adamant pickaxe", "Mean, green, mining machine.", 15, 5, 107, 38));
        items.add(new Item(110L, "Adamant hatchet", "Powerful, but has a habit of blending into the target.", 16, 5, 107, 38));
        items.add(new Item(111L, "Adamant fishing rod", "The green rod seems to lure the fishies in...", 17, 5, 107, 38));
        items.add(new Item(112L, "Adamant hammer", "A very solid hammer, used for crafting powerful equipment.", 18, 5, 57, 38));
        items.add(new Item(113L, "Rune dagger", "A nifty little blade with a very sharp point.", 3, 6, 110, 40));
        items.add(new Item(114L, "Rune sword", "The rune rune rune, rune is the (s)word.", 4, 6, 210, 40));
        items.add(new Item(115L, "Rune longsword", "A very long, and very sharp, sword.", 5, 6, 210, 40));
        items.add(new Item(116L, "Rune bow", "The shortest of shortbows, the longest of longbows.", 6, 6, 210, 40));
        items.add(new Item(117L, "Rune half shield", "Looks rather like a crystal, but isn't.", 7, 6, 310, 42));
        items.add(new Item(118L, "Rune full shield", "A very nice shield, used by professional adventurers.", 8, 6, 310, 42));
        items.add(new Item(119L, "Rune chainmail", "Well, it's not a platebody, but it's still pretty good.", 9, 6, 310, 42));
        items.add(new Item(120L, "Rune platebody", "Made famous by DR4G0NK1LL3R, truly a brave adventurer.", 10, 6, 510, 44));
        items.add(new Item(121L, "Rune half helmet", "A very protective helmet, without all the trimmings.", 11, 6, 140, 44));
        items.add(new Item(122L, "Rune full helmet", "An especially protective helmet, with all the trimmings.", 12, 6, 410, 46));
        items.add(new Item(123L, "Rune boots", "Walk the streets in style with these super-comfy boots.", 13, 6, 210, 46));
        items.add(new Item(124L, "Rune gloves", "Brand new Rune gloves, now with extra protection!", 14, 6, 210, 46));
        items.add(new Item(125L, "Rune pickaxe", "A pickaxe any miner would be proud to use.", 15, 6, 210, 48));
        items.add(new Item(126L, "Rune hatchet", "An extremely sharp axe, for the mightiest of trees.", 16, 6, 210, 48));
        items.add(new Item(127L, "Rune fishing rod", "The fish won't rune away from this rod!", 17, 6, 210, 48));
        items.add(new Item(128L, "Rune hammer", "The mightiest of hammers! Or at least a very mighty one.", 18, 6, 110, 48));
        items.add(new Item(129L, "Powdered Sapphire", "This sapphire is in tiny shards.", 22, 11, 20, 0));
        items.add(new Item(130L, "Powdered Emerald", "This emerald is in tiny shards.", 22, 11, 30, 0));
        items.add(new Item(131L, "Powdered Diamond", "This diamond is in tiny shards.", 22, 11, 40, 0));
        items.add(new Item(132L, "Dragon dagger", "The deadliest dragon dagger!", 3, 7, 200, 50));
        items.add(new Item(133L, "Dragon sword", "Sharp as a dragon's wit.", 4, 7, 375, 50));
        items.add(new Item(134L, "Dragon longsword", "A very long, and very sharp, sword.", 5, 7, 375, 50));
        items.add(new Item(135L, "Dragon bow", "A striking red bow, with a hint of flames.", 6, 7, 375, 50));
        items.add(new Item(136L, "Dragon half shield", "Half shield or not, this will protect from a dragon's breath.", 7, 7, 550, 52));
        items.add(new Item(137L, "Dragon full shield", "Rumoured to be forged from dragon remains.", 8, 7, 550, 52));
        items.add(new Item(138L, "Dragon chainmail", "Unusually, small insects seem to herd around the item.", 9, 7, 550, 52));
        items.add(new Item(139L, "Dragon platebody", "The ultimate in heavy duty protection.", 10, 7, 900, 54));
        items.add(new Item(140L, "Dragon half helmet", "There'll be no headshots whilst this is worn.", 11, 7, 140, 54));
        items.add(new Item(141L, "Dragon full helmet", "The helmet almost seems to repel arrows.", 12, 7, 725, 56));
        items.add(new Item(142L, "Dragon boots", "I don't think a dragon's foot would fit in these...", 13, 7, 375, 56));
        items.add(new Item(143L, "Dragon gloves", "I'm certain a dragon's claw wouldn't fit in here...", 14, 7, 375, 56));
        items.add(new Item(144L, "Dragon pickaxe", "For mining the toughest of ores.", 15, 7, 375, 58));
        items.add(new Item(145L, "Dragon hatchet", "For felling the tallest great oaks", 16, 7, 375, 58));
        items.add(new Item(146L, "Dragon fishing spear", "Let's take the fight to the fishes!", 17, 7, 375, 58));
        items.add(new Item(147L, "Dragon hammer", "Want to avoid getting hammered? Use this!", 18, 7, 200, 58));
        items.add(new Item(148L, "Draconic visage", "The orb seems to pulsate with power.", 23, 20, 300, 50));
        items.add(new Item(149L, "Silver ring", "A plain ring of silver.", 24, 8, 25, 35));
        items.add(new Item(150L, "Silver sapphire ring", "A ring of silver, with a glittering blue gem.", 24, 8, 185, 40));
        items.add(new Item(151L, "Silver emerald ring", "An emerald-embossed silver ring.", 24, 8, 185, 40));
        items.add(new Item(152L, "Silver ruby ring", "A silver ring with a fine ruby in.", 24, 8, 185, 40));
        items.add(new Item(153L, "Silver diamond ring", "A fine silver ring with a precious white gem.", 24, 8, 285, 45));
        items.add(new Item(154L, "Silver onyx ring", "A mildly ominous silver and black ring.", 24, 8, 285, 45));
        items.add(new Item(155L, "Gold ring", "A plain ring of gold.", 24, 9, 40, 35));
        items.add(new Item(156L, "Gold sapphire ring", "A golden ring, with a stunningly large sapphire.", 24, 9, 200, 40));
        items.add(new Item(157L, "Gold emerald ring", "A ring of fine gold, with a glittering emerald.", 24, 9, 200, 40));
        items.add(new Item(158L, "Gold ruby ring", "An expensive looking gold and red ring.", 24, 9, 200, 40));
        items.add(new Item(159L, "Gold diamond ring", "An intimidatingly opulent ring.", 24, 9, 300, 45));
        items.add(new Item(160L, "Gold onyx ring", "The ultimate in finger decorations.", 24, 9, 300, 45));
        items.add(new Item(161L, "Legendary dagger", "The dagger whispers of murders past.", 3, 10, 1000, 50));
        items.add(new Item(162L, "Legendary sword", "An absurdly menacing blade.", 4, 10, 2000, 50));
        items.add(new Item(163L, "Legendary longsword", "For long distance damage.", 5, 10, 2000, 50));
        items.add(new Item(164L, "Legendary bow", "Fired arrows seem to hone in on the target.", 6, 10, 2000, 50));
        items.add(new Item(165L, "Legendary half shield", "No, two halves don't make a whole. Sorry.", 7, 10, 3000, 52));
        items.add(new Item(166L, "Legendary full shield", "Maximum protection, maximum style.", 8, 10, 3000, 52));
        items.add(new Item(167L, "Legendary chainmail", "All the protection, with all the mobility.", 9, 10, 3000, 52));
        items.add(new Item(168L, "Legendary platebody", "Distinctly better for not-dying than dragon armour.", 10, 10, 5000, 54));
        items.add(new Item(169L, "Legendary half helmet", "Fit for a king!", 11, 10, 1000, 54));
        items.add(new Item(170L, "Legendary full helmet", "Possibly the comfiest helmet ever made.", 12, 10, 4000, 56));
        items.add(new Item(171L, "Legendary boots", "These boots were made for stomping. On enemies.", 13, 10, 2000, 56));
        items.add(new Item(172L, "Legendary gloves", "These gloves seem to fuse to legendary weapons automatically.", 14, 10, 2000, 56));
        items.add(new Item(173L, "Legendary pickaxe", "Fragments of ore seem to be drawn to the powerful blade.", 15, 10, 3000, 58));
        items.add(new Item(174L, "Legendary hatchet", "Trees shake with fear when the axe draws closer.", 16, 10, 3000, 58));
        items.add(new Item(175L, "Legendary fishing spear", "Arguably, this could be used as a weapon...", 17, 10, 3000, 58));
        items.add(new Item(176L, "Legendary hammer", "Used for the forging of the mightiest equipment.", 18, 10, 2000, 58));
        
        Item.saveInTx(items);
    }

    private static void createLocation() {
        List<Location> locations = new ArrayList<>();
        
        locations.add(new Location(1L, "Anvil"));
        locations.add(new Location(2L, "Furnace"));
        locations.add(new Location(3L, "Selling"));
        locations.add(new Location(4L, "Market"));
        locations.add(new Location(5L, "Table"));
        locations.add(new Location(6L, "Enchanting"));

        Location.saveInTx(locations);
    }

    private static void createMessage() {
        Message introMessage = new Message(System.currentTimeMillis(), "This is the first message! After 99 other messages, you'll never see it again.");
        introMessage.save();
    }

    private static void createPlayerInfo() {
        List<Player_Info> player_infos = new ArrayList<>();
        
        player_infos.add(new Player_Info("XP", 45));
        player_infos.add(new Player_Info("", 0));
        player_infos.add(new Player_Info("ItemsSmelted", 0, 0));
        player_infos.add(new Player_Info("ItemsCrafted", 0));
        player_infos.add(new Player_Info("ItemsTraded", 0, 0));
        player_infos.add(new Player_Info("ItemsEnchanted", 0));
        player_infos.add(new Player_Info("ItemsSold", 0, 0));
        player_infos.add(new Player_Info("ItemsBought", 0, 0));
        player_infos.add(new Player_Info("VisitorsCompleted", 0, 0));
        player_infos.add(new Player_Info("DateRestocked", System.currentTimeMillis()));
        player_infos.add(new Player_Info("DateVisitorSpawned", System.currentTimeMillis()));
        player_infos.add(new Player_Info("CoinsEarned", 0, 0));
        player_infos.add(new Player_Info("DateStarted", System.currentTimeMillis()));
        player_infos.add(new Player_Info("SavedLevel", 1, 0));
        player_infos.add(new Player_Info("UpgradesBought", 0));
        player_infos.add(new Player_Info("Premium", 0));
        player_infos.add(new Player_Info("Prestige", 0, 0));
        player_infos.add(new Player_Info("DateLastPrestiged", 0L));

        Player_Info.saveInTx(player_infos);
    }

    private static void createRecipe() {
        List<Recipe> recipes = new ArrayList<>();

        // Powdered gems
        recipes.add(new Recipe(129L, 1L, 73L, 1L, 1));
        recipes.add(new Recipe(130L, 1L, 74L, 1L, 1));
        recipes.add(new Recipe(131L, 1L, 75L, 1L, 1));

        // Silver Rings
        recipes.add(new Recipe(149L, 1L, 17L, 1L, 1));
        recipes.add(new Recipe(150L, 1L, 17L, 1L, 1));
        recipes.add(new Recipe(150L, 1L, 73L, 1L, 1));
        recipes.add(new Recipe(151L, 1L, 17L, 1L, 1));
        recipes.add(new Recipe(151L, 1L, 74L, 1L, 1));
        recipes.add(new Recipe(152L, 1L, 17L, 1L, 1));
        recipes.add(new Recipe(152L, 1L, 72L, 1L, 1));
        recipes.add(new Recipe(153L, 1L, 17L, 1L, 1));
        recipes.add(new Recipe(153L, 1L, 75L, 1L, 1));
        recipes.add(new Recipe(154L, 1L, 17L, 1L, 1));
        recipes.add(new Recipe(154L, 1L, 76L, 1L, 1));

        // Gold Rings
        recipes.add(new Recipe(155L, 1L, 18L, 1L, 1));
        recipes.add(new Recipe(156L, 1L, 18L, 1L, 1));
        recipes.add(new Recipe(156L, 1L, 73L, 1L, 1));
        recipes.add(new Recipe(157L, 1L, 18L, 1L, 1));
        recipes.add(new Recipe(157L, 1L, 74L, 1L, 1));
        recipes.add(new Recipe(158L, 1L, 18L, 1L, 1));
        recipes.add(new Recipe(158L, 1L, 72L, 1L, 1));
        recipes.add(new Recipe(159L, 1L, 18L, 1L, 1));
        recipes.add(new Recipe(159L, 1L, 75L, 1L, 1));
        recipes.add(new Recipe(160L, 1L, 18L, 1L, 1));
        recipes.add(new Recipe(160L, 1L, 76L, 1L, 1));

        // Bars
        recipes.add(new Recipe(11L, 1L, 1L, 1L, 1));
        recipes.add(new Recipe(11L, 1L, 2L, 1L, 1));
        recipes.add(new Recipe(12L, 1L, 4L, 1L, 2));
        recipes.add(new Recipe(13L, 1L, 4L, 1L, 2));
        recipes.add(new Recipe(13L, 1L, 3L, 1L, 1));
        recipes.add(new Recipe(14L, 1L, 5L, 1L, 1));
        recipes.add(new Recipe(14L, 1L, 3L, 1L, 2));
        recipes.add(new Recipe(15L, 1L, 6L, 1L, 1));
        recipes.add(new Recipe(15L, 1L, 3L, 1L, 4));
        recipes.add(new Recipe(16L, 1L, 7L, 1L, 1));
        recipes.add(new Recipe(16L, 1L, 3L, 1L, 8));
        recipes.add(new Recipe(17L, 1L, 9L, 1L, 1));
        recipes.add(new Recipe(18L, 1L, 8L, 1L, 1));
        recipes.add(new Recipe(19L, 1L, 10L, 1L, 1));
        recipes.add(new Recipe(19L, 1L, 3L, 1L, 12));

        // Bronze unfinished
        recipes.add(new Recipe(20L, 2L, 11L, 1L, 1));
        recipes.add(new Recipe(21L, 2L, 11L, 1L, 2));
        recipes.add(new Recipe(22L, 2L, 11L, 1L, 2));
        recipes.add(new Recipe(23L, 2L, 11L, 1L, 2));
        recipes.add(new Recipe(24L, 2L, 11L, 1L, 3));
        recipes.add(new Recipe(25L, 2L, 11L, 1L, 3));
        recipes.add(new Recipe(26L, 2L, 11L, 1L, 3));
        recipes.add(new Recipe(27L, 2L, 11L, 1L, 5));
        recipes.add(new Recipe(28L, 2L, 11L, 1L, 3));
        recipes.add(new Recipe(29L, 2L, 11L, 1L, 4));
        recipes.add(new Recipe(30L, 2L, 11L, 1L, 2));
        recipes.add(new Recipe(31L, 2L, 11L, 1L, 2));
        recipes.add(new Recipe(32L, 2L, 11L, 1L, 2));
        recipes.add(new Recipe(33L, 2L, 11L, 1L, 2));
        recipes.add(new Recipe(34L, 2L, 11L, 1L, 2));
        recipes.add(new Recipe(35L, 2L, 11L, 1L, 1));

        // Iron unfinished
        recipes.add(new Recipe(36L, 2L, 12L, 1L, 1));
        recipes.add(new Recipe(37L, 2L, 12L, 1L, 2));
        recipes.add(new Recipe(38L, 2L, 12L, 1L, 2));
        recipes.add(new Recipe(39L, 2L, 12L, 1L, 2));
        recipes.add(new Recipe(40L, 2L, 12L, 1L, 3));
        recipes.add(new Recipe(41L, 2L, 12L, 1L, 3));
        recipes.add(new Recipe(42L, 2L, 12L, 1L, 3));
        recipes.add(new Recipe(43L, 2L, 12L, 1L, 5));
        recipes.add(new Recipe(44L, 2L, 12L, 1L, 3));
        recipes.add(new Recipe(45L, 2L, 12L, 1L, 4));
        recipes.add(new Recipe(46L, 2L, 12L, 1L, 2));
        recipes.add(new Recipe(47L, 2L, 12L, 1L, 2));
        recipes.add(new Recipe(48L, 2L, 12L, 1L, 2));
        recipes.add(new Recipe(49L, 2L, 12L, 1L, 2));
        recipes.add(new Recipe(50L, 2L, 12L, 1L, 2));
        recipes.add(new Recipe(51L, 2L, 12L, 1L, 1));

        // Steel unfinished
        recipes.add(new Recipe(53L, 2L, 13L, 1L, 1));
        recipes.add(new Recipe(54L, 2L, 13L, 1L, 2));
        recipes.add(new Recipe(55L, 2L, 13L, 1L, 2));
        recipes.add(new Recipe(56L, 2L, 13L, 1L, 2));
        recipes.add(new Recipe(57L, 2L, 13L, 1L, 3));
        recipes.add(new Recipe(58L, 2L, 13L, 1L, 3));
        recipes.add(new Recipe(59L, 2L, 13L, 1L, 3));
        recipes.add(new Recipe(60L, 2L, 13L, 1L, 5));
        recipes.add(new Recipe(61L, 2L, 13L, 1L, 3));
        recipes.add(new Recipe(62L, 2L, 13L, 1L, 4));
        recipes.add(new Recipe(63L, 2L, 13L, 1L, 2));
        recipes.add(new Recipe(64L, 2L, 13L, 1L, 2));
        recipes.add(new Recipe(65L, 2L, 13L, 1L, 2));
        recipes.add(new Recipe(66L, 2L, 13L, 1L, 2));
        recipes.add(new Recipe(67L, 2L, 13L, 1L, 2));
        recipes.add(new Recipe(68L, 2L, 13L, 1L, 1));

        // Mithril unfinished
        recipes.add(new Recipe(81L, 2L, 14L, 1L, 1));
        recipes.add(new Recipe(82L, 2L, 14L, 1L, 2));
        recipes.add(new Recipe(83L, 2L, 14L, 1L, 2));
        recipes.add(new Recipe(84L, 2L, 14L, 1L, 2));
        recipes.add(new Recipe(85L, 2L, 14L, 1L, 3));
        recipes.add(new Recipe(86L, 2L, 14L, 1L, 3));
        recipes.add(new Recipe(87L, 2L, 14L, 1L, 3));
        recipes.add(new Recipe(88L, 2L, 14L, 1L, 5));
        recipes.add(new Recipe(89L, 2L, 14L, 1L, 3));
        recipes.add(new Recipe(90L, 2L, 14L, 1L, 4));
        recipes.add(new Recipe(91L, 2L, 14L, 1L, 2));
        recipes.add(new Recipe(92L, 2L, 14L, 1L, 2));
        recipes.add(new Recipe(93L, 2L, 14L, 1L, 2));
        recipes.add(new Recipe(94L, 2L, 14L, 1L, 2));
        recipes.add(new Recipe(95L, 2L, 14L, 1L, 2));
        recipes.add(new Recipe(96L, 2L, 14L, 1L, 1));

        // Adamant unfinished
        recipes.add(new Recipe(97L, 2L, 15L, 1L, 1));
        recipes.add(new Recipe(98L, 2L, 15L, 1L, 2));
        recipes.add(new Recipe(99L, 2L, 15L, 1L, 2));
        recipes.add(new Recipe(100L, 2L, 15L, 1L, 2));
        recipes.add(new Recipe(101L, 2L, 15L, 1L, 3));
        recipes.add(new Recipe(102L, 2L, 15L, 1L, 3));
        recipes.add(new Recipe(103L, 2L, 15L, 1L, 3));
        recipes.add(new Recipe(104L, 2L, 15L, 1L, 5));
        recipes.add(new Recipe(105L, 2L, 15L, 1L, 3));
        recipes.add(new Recipe(106L, 2L, 15L, 1L, 4));
        recipes.add(new Recipe(107L, 2L, 15L, 1L, 2));
        recipes.add(new Recipe(108L, 2L, 15L, 1L, 2));
        recipes.add(new Recipe(109L, 2L, 15L, 1L, 2));
        recipes.add(new Recipe(110L, 2L, 15L, 1L, 2));
        recipes.add(new Recipe(111L, 2L, 15L, 1L, 2));
        recipes.add(new Recipe(112L, 2L, 15L, 1L, 1));

        // Rune unfinished
        recipes.add(new Recipe(113L, 2L, 16L, 1L, 1));
        recipes.add(new Recipe(114L, 2L, 16L, 1L, 2));
        recipes.add(new Recipe(115L, 2L, 16L, 1L, 2));
        recipes.add(new Recipe(116L, 2L, 16L, 1L, 2));
        recipes.add(new Recipe(117L, 2L, 16L, 1L, 3));
        recipes.add(new Recipe(118L, 2L, 16L, 1L, 3));
        recipes.add(new Recipe(119L, 2L, 16L, 1L, 3));
        recipes.add(new Recipe(120L, 2L, 16L, 1L, 5));
        recipes.add(new Recipe(121L, 2L, 16L, 1L, 3));
        recipes.add(new Recipe(122L, 2L, 16L, 1L, 4));
        recipes.add(new Recipe(123L, 2L, 16L, 1L, 2));
        recipes.add(new Recipe(124L, 2L, 16L, 1L, 2));
        recipes.add(new Recipe(125L, 2L, 16L, 1L, 2));
        recipes.add(new Recipe(126L, 2L, 16L, 1L, 2));
        recipes.add(new Recipe(127L, 2L, 16L, 1L, 2));
        recipes.add(new Recipe(128L, 2L, 16L, 1L, 1));

        // Dragon unfinished
        recipes.add(new Recipe(132L, 2L, 19L, 1L, 1));
        recipes.add(new Recipe(133L, 2L, 19L, 1L, 2));
        recipes.add(new Recipe(134L, 2L, 19L, 1L, 2));
        recipes.add(new Recipe(135L, 2L, 19L, 1L, 2));
        recipes.add(new Recipe(136L, 2L, 19L, 1L, 3));
        recipes.add(new Recipe(137L, 2L, 19L, 1L, 3));
        recipes.add(new Recipe(138L, 2L, 19L, 1L, 3));
        recipes.add(new Recipe(139L, 2L, 19L, 1L, 5));
        recipes.add(new Recipe(140L, 2L, 19L, 1L, 3));
        recipes.add(new Recipe(141L, 2L, 19L, 1L, 4));
        recipes.add(new Recipe(142L, 2L, 19L, 1L, 2));
        recipes.add(new Recipe(143L, 2L, 19L, 1L, 2));
        recipes.add(new Recipe(144L, 2L, 19L, 1L, 2));
        recipes.add(new Recipe(145L, 2L, 19L, 1L, 2));
        recipes.add(new Recipe(146L, 2L, 19L, 1L, 2));
        recipes.add(new Recipe(147L, 2L, 19L, 1L, 1));

        // Bronze finished
        recipes.add(new Recipe(20L, 1L, 20L, 2L, 1));
        recipes.add(new Recipe(20L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(21L, 1L, 21L, 2L, 1));
        recipes.add(new Recipe(21L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(22L, 1L, 22L, 2L, 1));
        recipes.add(new Recipe(22L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(23L, 1L, 23L, 2L, 1));
        recipes.add(new Recipe(23L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(24L, 1L, 24L, 2L, 1));
        recipes.add(new Recipe(24L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(25L, 1L, 25L, 2L, 1));
        recipes.add(new Recipe(25L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(26L, 1L, 26L, 2L, 1));
        recipes.add(new Recipe(26L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(27L, 1L, 27L, 2L, 1));
        recipes.add(new Recipe(27L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(28L, 1L, 28L, 2L, 1));
        recipes.add(new Recipe(28L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(29L, 1L, 29L, 2L, 1));
        recipes.add(new Recipe(29L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(30L, 1L, 30L, 2L, 1));
        recipes.add(new Recipe(30L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(31L, 1L, 31L, 2L, 1));
        recipes.add(new Recipe(31L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(32L, 1L, 32L, 2L, 1));
        recipes.add(new Recipe(32L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(33L, 1L, 33L, 2L, 1));
        recipes.add(new Recipe(33L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(34L, 1L, 34L, 2L, 1));
        recipes.add(new Recipe(34L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(35L, 1L, 35L, 2L, 1));
        recipes.add(new Recipe(35L, 1L, 69L, 1L, 1));

        // Iron finished
        recipes.add(new Recipe(36L, 1L, 36L, 2L, 1));
        recipes.add(new Recipe(36L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(37L, 1L, 37L, 2L, 1));
        recipes.add(new Recipe(37L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(38L, 1L, 38L, 2L, 1));
        recipes.add(new Recipe(38L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(39L, 1L, 39L, 2L, 1));
        recipes.add(new Recipe(39L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(40L, 1L, 40L, 2L, 1));
        recipes.add(new Recipe(40L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(41L, 1L, 41L, 2L, 1));
        recipes.add(new Recipe(41L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(42L, 1L, 42L, 2L, 1));
        recipes.add(new Recipe(42L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(43L, 1L, 43L, 2L, 1));
        recipes.add(new Recipe(43L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(44L, 1L, 44L, 2L, 1));
        recipes.add(new Recipe(44L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(45L, 1L, 45L, 2L, 1));
        recipes.add(new Recipe(45L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(46L, 1L, 46L, 2L, 1));
        recipes.add(new Recipe(46L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(47L, 1L, 47L, 2L, 1));
        recipes.add(new Recipe(47L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(48L, 1L, 48L, 2L, 1));
        recipes.add(new Recipe(48L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(49L, 1L, 49L, 2L, 1));
        recipes.add(new Recipe(49L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(50L, 1L, 50L, 2L, 1));
        recipes.add(new Recipe(50L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(51L, 1L, 51L, 2L, 1));
        recipes.add(new Recipe(51L, 1L, 70L, 1L, 1));

        // Steel finished
        recipes.add(new Recipe(53L, 1L, 53L, 2L, 1));
        recipes.add(new Recipe(53L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(54L, 1L, 54L, 2L, 1));
        recipes.add(new Recipe(54L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(55L, 1L, 55L, 2L, 1));
        recipes.add(new Recipe(55L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(56L, 1L, 56L, 2L, 1));
        recipes.add(new Recipe(56L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(57L, 1L, 57L, 2L, 1));
        recipes.add(new Recipe(57L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(58L, 1L, 58L, 2L, 1));
        recipes.add(new Recipe(58L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(59L, 1L, 59L, 2L, 1));
        recipes.add(new Recipe(59L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(60L, 1L, 60L, 2L, 1));
        recipes.add(new Recipe(60L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(61L, 1L, 61L, 2L, 1));
        recipes.add(new Recipe(61L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(62L, 1L, 62L, 2L, 1));
        recipes.add(new Recipe(62L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(63L, 1L, 63L, 2L, 1));
        recipes.add(new Recipe(63L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(64L, 1L, 64L, 2L, 1));
        recipes.add(new Recipe(64L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(65L, 1L, 65L, 2L, 1));
        recipes.add(new Recipe(65L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(66L, 1L, 66L, 2L, 1));
        recipes.add(new Recipe(66L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(67L, 1L, 67L, 2L, 1));
        recipes.add(new Recipe(67L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(68L, 1L, 68L, 2L, 1));
        recipes.add(new Recipe(68L, 1L, 71L, 1L, 1));

        // Mithril finished
        recipes.add(new Recipe(81L, 1L, 81L, 2L, 1));
        recipes.add(new Recipe(81L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(82L, 1L, 82L, 2L, 1));
        recipes.add(new Recipe(82L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(83L, 1L, 83L, 2L, 1));
        recipes.add(new Recipe(83L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(84L, 1L, 84L, 2L, 1));
        recipes.add(new Recipe(84L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(85L, 1L, 85L, 2L, 1));
        recipes.add(new Recipe(85L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(86L, 1L, 86L, 2L, 1));
        recipes.add(new Recipe(86L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(87L, 1L, 87L, 2L, 1));
        recipes.add(new Recipe(87L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(88L, 1L, 88L, 2L, 1));
        recipes.add(new Recipe(88L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(89L, 1L, 89L, 2L, 1));
        recipes.add(new Recipe(89L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(90L, 1L, 90L, 2L, 1));
        recipes.add(new Recipe(90L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(91L, 1L, 91L, 2L, 1));
        recipes.add(new Recipe(91L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(92L, 1L, 92L, 2L, 1));
        recipes.add(new Recipe(92L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(93L, 1L, 93L, 2L, 1));
        recipes.add(new Recipe(93L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(94L, 1L, 94L, 2L, 1));
        recipes.add(new Recipe(94L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(95L, 1L, 95L, 2L, 1));
        recipes.add(new Recipe(95L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(96L, 1L, 96L, 2L, 1));
        recipes.add(new Recipe(96L, 1L, 129L, 1L, 1));

        // Adamant finished
        recipes.add(new Recipe(97L, 1L, 97L, 2L, 1));
        recipes.add(new Recipe(97L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(98L, 1L, 98L, 2L, 1));
        recipes.add(new Recipe(98L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(99L, 1L, 99L, 2L, 1));
        recipes.add(new Recipe(99L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(100L, 1L, 100L, 2L, 1));
        recipes.add(new Recipe(100L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(101L, 1L, 101L, 2L, 1));
        recipes.add(new Recipe(101L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(102L, 1L, 102L, 2L, 1));
        recipes.add(new Recipe(102L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(103L, 1L, 103L, 2L, 1));
        recipes.add(new Recipe(103L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(104L, 1L, 104L, 2L, 1));
        recipes.add(new Recipe(104L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(105L, 1L, 105L, 2L, 1));
        recipes.add(new Recipe(105L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(106L, 1L, 106L, 2L, 1));
        recipes.add(new Recipe(106L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(107L, 1L, 107L, 2L, 1));
        recipes.add(new Recipe(107L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(108L, 1L, 108L, 2L, 1));
        recipes.add(new Recipe(108L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(109L, 1L, 109L, 2L, 1));
        recipes.add(new Recipe(109L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(110L, 1L, 110L, 2L, 1));
        recipes.add(new Recipe(110L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(111L, 1L, 111L, 2L, 1));
        recipes.add(new Recipe(111L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(112L, 1L, 112L, 2L, 1));
        recipes.add(new Recipe(112L, 1L, 130L, 1L, 1));

        // Rune finished
        recipes.add(new Recipe(113L, 1L, 113L, 2L, 1));
        recipes.add(new Recipe(113L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(113L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(114L, 1L, 114L, 2L, 1));
        recipes.add(new Recipe(114L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(114L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(115L, 1L, 115L, 2L, 1));
        recipes.add(new Recipe(115L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(115L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(116L, 1L, 116L, 2L, 1));
        recipes.add(new Recipe(116L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(116L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(117L, 1L, 117L, 2L, 1));
        recipes.add(new Recipe(117L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(117L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(118L, 1L, 118L, 2L, 1));
        recipes.add(new Recipe(118L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(118L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(119L, 1L, 119L, 2L, 1));
        recipes.add(new Recipe(119L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(119L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(120L, 1L, 120L, 2L, 1));
        recipes.add(new Recipe(120L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(120L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(121L, 1L, 121L, 2L, 1));
        recipes.add(new Recipe(121L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(121L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(122L, 1L, 122L, 2L, 1));
        recipes.add(new Recipe(122L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(122L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(123L, 1L, 123L, 2L, 1));
        recipes.add(new Recipe(123L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(123L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(124L, 1L, 124L, 2L, 1));
        recipes.add(new Recipe(124L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(124L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(125L, 1L, 125L, 2L, 1));
        recipes.add(new Recipe(125L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(125L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(126L, 1L, 126L, 2L, 1));
        recipes.add(new Recipe(126L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(126L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(127L, 1L, 127L, 2L, 1));
        recipes.add(new Recipe(127L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(127L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(128L, 1L, 128L, 2L, 1));
        recipes.add(new Recipe(128L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(128L, 1L, 131L, 1L, 1));

        // Dragon finished
        recipes.add(new Recipe(132L, 1L, 132L, 2L, 1));
        recipes.add(new Recipe(132L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(133L, 1L, 133L, 2L, 1));
        recipes.add(new Recipe(133L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(134L, 1L, 134L, 2L, 1));
        recipes.add(new Recipe(134L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(135L, 1L, 135L, 2L, 1));
        recipes.add(new Recipe(135L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(136L, 1L, 136L, 2L, 1));
        recipes.add(new Recipe(136L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(137L, 1L, 137L, 2L, 1));
        recipes.add(new Recipe(137L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(138L, 1L, 138L, 2L, 1));
        recipes.add(new Recipe(138L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(139L, 1L, 139L, 2L, 1));
        recipes.add(new Recipe(139L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(140L, 1L, 140L, 2L, 1));
        recipes.add(new Recipe(140L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(141L, 1L, 141L, 2L, 1));
        recipes.add(new Recipe(141L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(142L, 1L, 142L, 2L, 1));
        recipes.add(new Recipe(142L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(143L, 1L, 143L, 2L, 1));
        recipes.add(new Recipe(143L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(144L, 1L, 144L, 2L, 1));
        recipes.add(new Recipe(144L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(145L, 1L, 145L, 2L, 1));
        recipes.add(new Recipe(145L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(146L, 1L, 146L, 2L, 1));
        recipes.add(new Recipe(146L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(147L, 1L, 147L, 2L, 1));
        recipes.add(new Recipe(147L, 1L, 148L, 1L, 1));

        // Legendary finished
        recipes.add(new Recipe(161L, 1L, 161L, 2L, 1));
        recipes.add(new Recipe(162L, 1L, 162L, 2L, 2));
        recipes.add(new Recipe(163L, 1L, 163L, 2L, 2));
        recipes.add(new Recipe(164L, 1L, 164L, 2L, 2));
        recipes.add(new Recipe(165L, 1L, 165L, 2L, 3));
        recipes.add(new Recipe(166L, 1L, 166L, 2L, 3));
        recipes.add(new Recipe(167L, 1L, 167L, 2L, 3));
        recipes.add(new Recipe(168L, 1L, 168L, 2L, 5));
        recipes.add(new Recipe(169L, 1L, 169L, 2L, 1));
        recipes.add(new Recipe(170L, 1L, 170L, 2L, 4));
        recipes.add(new Recipe(171L, 1L, 171L, 2L, 2));
        recipes.add(new Recipe(172L, 1L, 172L, 2L, 2));
        recipes.add(new Recipe(173L, 1L, 173L, 2L, 3));
        recipes.add(new Recipe(174L, 1L, 174L, 2L, 3));
        recipes.add(new Recipe(175L, 1L, 175L, 2L, 3));
        recipes.add(new Recipe(176L, 1L, 176L, 2L, 2));

        Recipe.saveInTx(recipes);
    }

    private static void createSetting() {
        List<Setting> settings = new ArrayList<>();
        
        settings.add(new Setting(1L, "Sounds", false));
        settings.add(new Setting(2L, "Music", false));
        settings.add(new Setting(3L, "RestockNotifications", false));
        settings.add(new Setting(4L, "NotificationSounds", false));
        settings.add(new Setting(5L, "VisitorNotifications", false));
        settings.add(new Setting(6L, "TrySignIn", true));
        
        Setting.saveInTx(settings);
    }

    private static void createSlot() {
        List<Slot> slots = new ArrayList<>();
        
        slots.add(new Slot(1, 1, false));
        slots.add(new Slot(1, 3, false));
        slots.add(new Slot(1, 7, false));
        slots.add(new Slot(1, 16, false));
        slots.add(new Slot(1, 25, false));
        slots.add(new Slot(1, 33, false));
        slots.add(new Slot(1, 42, false));
        slots.add(new Slot(1, 1, true));
        slots.add(new Slot(2, 1, false));
        slots.add(new Slot(2, 4, false));
        slots.add(new Slot(2, 8, false));
        slots.add(new Slot(2, 17, false));
        slots.add(new Slot(2, 24, false));
        slots.add(new Slot(2, 30, false));
        slots.add(new Slot(2, 44, false));
        slots.add(new Slot(2, 1, true));
        slots.add(new Slot(3, 1, false));
        slots.add(new Slot(3, 5, false));
        slots.add(new Slot(3, 13, false));
        slots.add(new Slot(3, 20, false));
        slots.add(new Slot(3, 35, false));
        slots.add(new Slot(3, 1, true));
        slots.add(new Slot(4, 1, false));
        slots.add(new Slot(4, 6, false));
        slots.add(new Slot(4, 10, false));
        slots.add(new Slot(4, 23, false));
        slots.add(new Slot(4, 40, false));
        slots.add(new Slot(4, 1, true));
        slots.add(new Slot(5, 1, false));
        slots.add(new Slot(5, 5, false));
        slots.add(new Slot(5, 10, false));
        slots.add(new Slot(5, 15, false));
        slots.add(new Slot(5, 27, false));
        slots.add(new Slot(5, 39, false));
        slots.add(new Slot(5, 50, false));
        slots.add(new Slot(5, 1, true));
        slots.add(new Slot(6, 1, false));
        slots.add(new Slot(6, 20, false));
        slots.add(new Slot(6, 30, false));
        slots.add(new Slot(6, 40, false));
        slots.add(new Slot(6, 50, false));
        slots.add(new Slot(6, 60, false));
        slots.add(new Slot(6, 70, false));
        slots.add(new Slot(6, 1, true));
        
        Slot.saveInTx(slots);
    }

    private static void createState() {
        List<State> states = new ArrayList<>();

        states.add(new State(1L, "Normal", "", 0L, 0, 15));
        states.add(new State(2L, "Unfinished", "(unf) ", 0L, 0, 15));
        states.add(new State(3L, "Red Enchant", "(red) ", 72L, 10, 2));
        states.add(new State(4L, "Blue Enchant", "(blue) ", 73L, 15, 2));
        states.add(new State(5L, "Green Enchant", "(green) ", 74L, 20, 1));
        states.add(new State(6L, "White Enchant", "(white) ", 75L, 25, 1));
        states.add(new State(7L, "Black Enchant", "(black) ", 76L, 30, 1));

        State.saveInTx(states);
    }

    private static void createTier() {
        List<Tier> tiers = new ArrayList<>();
        
        tiers.add(new Tier(1L, "Bronze", 1, 30));
        tiers.add(new Tier(2L, "Iron", 5, 25));
        tiers.add(new Tier(3L, "Steel", 10, 15));
        tiers.add(new Tier(4L, "Mithril", 20, 10));
        tiers.add(new Tier(5L, "Adamant", 30, 7));
        tiers.add(new Tier(6L, "Rune", 40, 4));
        tiers.add(new Tier(7L, "Dragon", 50, 2));
        tiers.add(new Tier(8L, "Silver", 35, 10));
        tiers.add(new Tier(9L, "Gold", 45, 10));
        tiers.add(new Tier(10L, "Premium", 1, 0));
        tiers.add(new Tier(11L, "None", 1, 35));
        
        Tier.saveInTx(tiers);
    }

    private static void createTrader() {
        List<Trader> traders = new ArrayList<>();
        List<Trader_Stock> trader_stocks = new ArrayList<>();
        
        traders.add(new Trader(1L, 4, "The Scraps", "I was gonna chuck this stuff out.. you interested?", 0, 0, 0, 60));
        trader_stocks.add(new Trader_Stock(1L, 1L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(1L, 1L, 1, 40, 35));
        trader_stocks.add(new Trader_Stock(1L, 2L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(1L, 2L, 1, 40, 35));
        trader_stocks.add(new Trader_Stock(1L, 11L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(1L, 11L, 1, 20, 20));

        traders.add(new Trader(1L, 4, "The Off Cuts", "This stuff isn't the best, but it'll do.", 5, 0, 0, 40));
        trader_stocks.add(new Trader_Stock(2L, 3L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(2L, 3L, 1, 20, 15));
        trader_stocks.add(new Trader_Stock(2L, 3L, 1, 100, 75));

        traders.add(new Trader(1L, 4, "The Backbone", "You're gonna like what I've got. I think.", 10, 0, 0, 15));
        trader_stocks.add(new Trader_Stock(3L, 5L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(3L, 5L, 1, 100, 120));
        trader_stocks.add(new Trader_Stock(3L, 6L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(3L, 6L, 1, 100, 75));
        trader_stocks.add(new Trader_Stock(3L, 14L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(3L, 14L, 1, 30, 18));
        trader_stocks.add(new Trader_Stock(3L, 14L, 1, 120, 40));
        trader_stocks.add(new Trader_Stock(3L, 15L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(3L, 15L, 1, 30, 6));
        trader_stocks.add(new Trader_Stock(3L, 15L, 1, 100, 20));

        traders.add(new Trader(1L, 4, "The Prime Cuts", "My very best stock, for the connoisseur.", 15, 0, 0, 5));
        trader_stocks.add(new Trader_Stock(4L, 7L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(4L, 7L, 1, 30, 15));
        trader_stocks.add(new Trader_Stock(4L, 16L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(4L, 16L, 1, 100, 10));

        traders.add(new Trader(2L, 4, "The Iron Throne", "I rule my iron with an iron fist!", 0, 0, 0, 5));
        trader_stocks.add(new Trader_Stock(5L, 4L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(5L, 4L, 1, 10, 25));
        trader_stocks.add(new Trader_Stock(5L, 4L, 1, 130, 90));
        trader_stocks.add(new Trader_Stock(5L, 12L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(5L, 12L, 1, 15, 10));

        traders.add(new Trader(2L, 4, "The Iron Fist", "I rule my iron from an iron throne!", 25, 0, 0, 1));
        trader_stocks.add(new Trader_Stock(6L, 4L, 1, 0, 25));
        trader_stocks.add(new Trader_Stock(6L, 4L, 1, 10, 90));
        trader_stocks.add(new Trader_Stock(6L, 4L, 1, 130, 200));
        trader_stocks.add(new Trader_Stock(6L, 12L, 1, 0, 15));
        trader_stocks.add(new Trader_Stock(6L, 12L, 1, 15, 100));

        traders.add(new Trader(3L, 4, "The Nuggets", "Check out these lil shiners.", 5, 0, 0, 1));
        trader_stocks.add(new Trader_Stock(7L, 9L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(7L, 9L, 1, 25, 15));
        trader_stocks.add(new Trader_Stock(7L, 17L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(7L, 17L, 1, 25, 10));

        traders.add(new Trader(3L, 4, "The Nougat", "Panned these all myself, honest!", 15, 0, 0, 1));
        trader_stocks.add(new Trader_Stock(8L, 9L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(8L, 9L, 1, 25, 15));
        trader_stocks.add(new Trader_Stock(8L, 8L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(8L, 8L, 1, 50, 30));

        traders.add(new Trader(3L, 4, "The Golden Boulders", "Check out these lil shiners.", 35, 0, 0, 1));
        trader_stocks.add(new Trader_Stock(9L, 8L, 1, 0, 30));
        trader_stocks.add(new Trader_Stock(9L, 8L, 1, 100, 150));
        trader_stocks.add(new Trader_Stock(9L, 18L, 1, 0, 15));
        trader_stocks.add(new Trader_Stock(9L, 18L, 1, 50, 100));

        traders.add(new Trader(4L, 4, "A Straight Cut", "Definitely not off the back of a cart.", 0, 0, 0, 10));
        trader_stocks.add(new Trader_Stock(10L, 69L, 1, 0, 15));
        trader_stocks.add(new Trader_Stock(10L, 69L, 1, 35, 50));

        traders.add(new Trader(4L, 4, "A Finer Cut", "Reams upon reams of high quality cloth.", 15, 0, 0, 2));
        trader_stocks.add(new Trader_Stock(11L, 70L, 1, 0, 15));
        trader_stocks.add(new Trader_Stock(11L, 70L, 1, 35, 50));

        traders.add(new Trader(5L, 4, "Logged In", "LOGS! LOOOGS! LOTSA LOOOOOOGS!!!", 0, 0, 0, 30));
        trader_stocks.add(new Trader_Stock(12L, 71L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(12L, 71L, 1, 50, 25));
        trader_stocks.add(new Trader_Stock(12L, 71L, 1, 200, 40));

        traders.add(new Trader(6L, 4, "Oog", "OOG. OOG. OOG.", 15, 0, 0, 3));
        trader_stocks.add(new Trader_Stock(13L, 71L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(13L, 3L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(13L, 1L, 1, 0, 2));

        traders.add(new Trader(7L, 4, "GemBooth 1000", "Bzzt! Buy gems!", 10, 0, 0, 3));
        trader_stocks.add(new Trader_Stock(14L, 72L, 1, 0, 1));
        trader_stocks.add(new Trader_Stock(14L, 72L, 1, 10, 3));
        trader_stocks.add(new Trader_Stock(14L, 73L, 1, 0, 1));
        trader_stocks.add(new Trader_Stock(14L, 73L, 1, 10, 3));
        trader_stocks.add(new Trader_Stock(14L, 74L, 1, 0, 1));
        trader_stocks.add(new Trader_Stock(14L, 74L, 1, 10, 3));

        traders.add(new Trader(7L, 4, "GemEmporium 5000", "Bzzt! Buy BETTER gems!", 25, 0, 0, 2));
        trader_stocks.add(new Trader_Stock(15L, 75L, 1, 0, 1));
        trader_stocks.add(new Trader_Stock(15L, 75L, 1, 10, 2));
        trader_stocks.add(new Trader_Stock(15L, 76L, 1, 0, 1));
        trader_stocks.add(new Trader_Stock(15L, 75L, 1, 10, 2));

        traders.add(new Trader(7L, 4, "GemCrusher 9000", "Bzzt! Buy BROKEN gems!", 25, 0, 0, 4));
        trader_stocks.add(new Trader_Stock(16L, 129L, 1, 0, 1));
        trader_stocks.add(new Trader_Stock(16L, 129L, 1, 10, 2));
        trader_stocks.add(new Trader_Stock(16L, 130L, 1, 0, 1));
        trader_stocks.add(new Trader_Stock(16L, 130L, 1, 10, 2));
        trader_stocks.add(new Trader_Stock(16L, 131L, 1, 0, 1));
        trader_stocks.add(new Trader_Stock(16L, 131L, 1, 20, 5));

        traders.add(new Trader(8L, 4, "Farmyard Funland", "All the fun of the farm!", 5, 0, 0, 25));
        trader_stocks.add(new Trader_Stock(17L, 77L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(17L, 77L, 1, 20, 20));
        trader_stocks.add(new Trader_Stock(17L, 80L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(17L, 80L, 1, 20, 20));

        traders.add(new Trader(8L, 4, "Baker's Dozen", "What, a plant can't run a business?", 5, 0, 0, 25));
        trader_stocks.add(new Trader_Stock(18L, 78L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(18L, 78L, 1, 20, 20));
        trader_stocks.add(new Trader_Stock(18L, 79L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(18L, 79L, 1, 20, 20));

        traders.add(new Trader(9L, 4, "The Exclusive Emporium", "You won't find any of this anywhere else!", 30, 0, 0, 1));
        trader_stocks.add(new Trader_Stock(19L, 148L, 1, 0, 1));

        Trader.saveInTx(traders);
        Trader_Stock.saveInTx(trader_stocks);
    }

    private static void createType() {
        List<Type> types = new ArrayList<>();
        
        types.add(new Type(1L, "Ore", 1, 30));
        types.add(new Type(2L, "Bar", 1, 30));
        types.add(new Type(3L, "Dagger", 2, 25));
        types.add(new Type(4L, "Sword", 2, 25));
        types.add(new Type(5L, "Longsword", 2, 25));
        types.add(new Type(6L, "Bow", 2, 20));
        types.add(new Type(7L, "Half Shield", 3, 20));
        types.add(new Type(8L, "Full Shield", 3, 20));
        types.add(new Type(9L, "Chainmail", 3, 20));
        types.add(new Type(10L, "Platebody", 3, 15));
        types.add(new Type(11L, "Half Helmet", 3, 15));
        types.add(new Type(12L, "Full Helmet", 3, 15));
        types.add(new Type(13L, "Boot", 3, 15));
        types.add(new Type(14L, "Glove", 3, 10));
        types.add(new Type(15L, "Pickaxe", 4, 10));
        types.add(new Type(16L, "Hatchet", 4, 10));
        types.add(new Type(17L, "Fishing Rod", 4, 10));
        types.add(new Type(18L, "Hammer", 4, 10));
        types.add(new Type(19L, "Secondary", 1, 30));
        types.add(new Type(20L, "Gem", 1, 5));
        types.add(new Type(21L, "Food", 2, 30));
        types.add(new Type(22L, "Powder", 1, 1));
        types.add(new Type(23L, "Rare", 6, 0));
        types.add(new Type(24L, "Ring", 4, 1));
        types.add(new Type(100L, "Internal", 0, 0));
        
        Type.saveInTx(types);
    }

    public static void createUpgrade() {
        List<Upgrade> upgrades = new ArrayList<>();

        upgrades.add(new Upgrade("Visitor Spawn Time", "mins", 1000, 1, 25, 10, 25));
        upgrades.add(new Upgrade("Shop Restock Time", "hours", 250, 1, 24, 2, 24));
        upgrades.add(new Upgrade("Maximum Visitors", "visitors", 1000, 1, 2, 10, 2));
        upgrades.add(new Upgrade("Maximum Traders", "traders", 250, 1, 3, 10, 3));
        upgrades.add(new Upgrade("Gold Bonus", "%", 250, 5, 0, 50, 0));
        upgrades.add(new Upgrade("XP Bonus", "%", 250, 5, 0, 50, 0));
        upgrades.add(new Upgrade("Legendary Chance", "%", 1250, 5, 5, 100, 5));

        Upgrade.saveInTx(upgrades);
    }

    public static void createVisitor() {
        Visitor visitor = new Visitor(System.currentTimeMillis(), 33L);
        visitor.save();
    }

    public static void createVisitorDemand() {
        List<Visitor_Demand> visitor_demands = new ArrayList<>();
        
        // Req: 2 ore, 1 bar, 1 unfinished
        visitor_demands.add(new Visitor_Demand(1L, 3L, 1L, 0, 2, true));
        visitor_demands.add(new Visitor_Demand(1L, 3L, 2L, 0, 1, true));
        visitor_demands.add(new Visitor_Demand(1L, 1L, 2L, 0, 1, true));

        // Opt: 1 cheese
        visitor_demands.add(new Visitor_Demand(1L, 3L, 21L, 0, 1, false));
        
        Visitor_Demand.saveInTx(visitor_demands);
    }

    public static void createVisitorStats() {
        List<Visitor_Stats> visitorStatses = new ArrayList<>();
        
        visitorStatses.add(new Visitor_Stats(1L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(2L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(3L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(4L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(5L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(6L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(7L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(8L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(9L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(10L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(11L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(12L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(13L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(14L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(15L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(16L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(17L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(18L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(19L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(20L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(21L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(22L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(23L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(24L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(25L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(26L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(27L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(28L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(29L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(30L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(31L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(32L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(33L, 1, 52L, 1L, 0, System.currentTimeMillis(), 0L));
        visitorStatses.add(new Visitor_Stats(34L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(35L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(36L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(37L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(38L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(39L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(40L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(41L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(42L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(43L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(44L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(45L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(46L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(47L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(48L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(49L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(50L, 0, 52L, 1L, 0, 0L, 0L));

        Visitor_Stats.saveInTx(visitorStatses);
    }

    public static void createVisitorType() {
        List<Visitor_Type> visitor_types = new ArrayList<>();
        
        visitor_types.add(new Visitor_Type(1L, "Senor Spicy Hot", "I like unfinished things, they burn better!", 1L, 14L, 2L, 1.1, 1.1, 3.0, false, false, false, 3));
        visitor_types.add(new Visitor_Type(2L, "Mister Hatchet", "If only I was a woodcutter...", 3L, 16L, 1L, 1.2, 1.2, 1.2, false, false, false, 6));
        visitor_types.add(new Visitor_Type(3L, "Lord of the Junk", "It's not rubbish, it's treasure!", 1L, 3L, 2L, 1.05, 1.05, 1.05, false, false, false, 10));
        visitor_types.add(new Visitor_Type(4L, "Monsieur Fancypants", "Only the best for me, old chap.", 10L, 10L, 4L, 1.6, 1.55, 1.5, false, false, false, 1));
        visitor_types.add(new Visitor_Type(5L, "Grumbling Rock", "Me hungry. Tummy rumbling.", 11L, 1L, 1L, 1.05, 1.4, 1.05, false, false, false, 5));
        visitor_types.add(new Visitor_Type(6L, "Large Grumbling Rock", "Me very hungry. Tummy rumbling loud.", 11L, 1L, 1L, 1.6, 1.8, 1.05, false, false, false, 1));
        visitor_types.add(new Visitor_Type(7L, "R. De Couleur", "They say I'm the short-tempered one, but I just like the colour.", 7L, 20L, 3L, 1.25, 1.05, 1.66, false, false, false, 2));
        visitor_types.add(new Visitor_Type(8L, "G. De Couleur", "They say I'm the jealous one, but I just like the colour.", 5L, 20L, 4L, 1.25, 1.05, 1.66, false, false, false, 2));
        visitor_types.add(new Visitor_Type(9L, "B. De Couleur", "They say I'm the unhappy one, but I just like the colour.", 6L, 20L, 5L, 1.25, 1.05, 1.66, false, false, false, 2));
        visitor_types.add(new Visitor_Type(10L, "W. De Couleur", "They say I'm the scared one, but I just like the colour.", 8L, 20L, 6L, 1.25, 1.05, 1.66, false, false, false, 1));
        visitor_types.add(new Visitor_Type(11L, "B. De Couleur", "They say I'm the scary one, but I just like the colour.", 20L, 20L, 7L, 1.25, 1.05, 1.66, false, false, false, 1));
        visitor_types.add(new Visitor_Type(12L, "Gelatinous Egg", "So what if I'm not quite in shape?", 11L, 19L, 2L, 1.05, 1.15, 1.1, false, false, false, 8));
        visitor_types.add(new Visitor_Type(13L, "The Great Cheese Menace", "You there! Got any of the yellow stuff?", 11L, 21L, 1L, 1.05, 1.55, 1.05, false, false, false, 6));
        visitor_types.add(new Visitor_Type(14L, "The Leech King", "Slurp, slurp.", 7L, 4L, 7L, 1.25, 1.25, 1.25, false, false, false, 3));
        visitor_types.add(new Visitor_Type(15L, "Man of Magicka", "Do you like my outfit?", 11L, 14L, 7L, 1.05, 1.05, 1.05, false, false, false, 7));
        visitor_types.add(new Visitor_Type(16L, "Rare Chest", "Give me your rare items, I'll look after them!", 5L, 21L, 1L, 1.10, 1.02, 1.04, false, false, false, 1));
        visitor_types.add(new Visitor_Type(17L, "Chest", "Give me your items, I'll look after them!", 4L, 21L, 2L, 1.10, 1.01, 1.04, false, false, false, 4));
        visitor_types.add(new Visitor_Type(18L, "Eye of Ender", "Don't worry, it's a coloured contact lens.", 6L, 12L, 6L, 1.30, 1.30, 1.30, false, false, false, 1));
        visitor_types.add(new Visitor_Type(19L, "BRAINS", "BRAINS", 1L, 12L, 5L, 1.10, 1.15, 1.15, false, false, false, 6));
        visitor_types.add(new Visitor_Type(20L, "Asterisk The Ghoul", "Seen Obelix anywhere?", 1L, 16L, 1L, 1.05, 1.10, 1.05, false, false, false, 3));
        visitor_types.add(new Visitor_Type(21L, "Eye of Starter", "I don't suppose you've got any monocles?", 3L, 11L, 6L, 1.20, 1.20, 1.20, false, false, false, 4));
        visitor_types.add(new Visitor_Type(22L, "Marcell", "I've got shoes on.", 11L, 19L, 1L, 1.01, 1.01, 1.01, false, false, false, 5));
        visitor_types.add(new Visitor_Type(23L, "Crabby", "Snippy, snippy!", 11L, 3L, 1L, 1.02, 1.02, 1.02, false, false, false, 3));
        visitor_types.add(new Visitor_Type(24L, "Southerner", "From down south. No, REALLY south. REAAALLLY south.", 7L, 6L, 3L, 1.15, 1.15, 1.10, false, false, false, 2));
        visitor_types.add(new Visitor_Type(25L, "Magical Mushroom", "No nibbling.", 11L, 21L, 1L, 1.01, 1.15, 1.05, false, false, false, 5));
        visitor_types.add(new Visitor_Type(26L, "Overwasp", "You know I can sting repeatedly, right?", 9L, 4L, 1L, 1.25, 1.15, 1.05, false, false, false, 4));
        visitor_types.add(new Visitor_Type(27L, "Obelix The Ghoul", "Seen Asterisk anywhere?", 1L, 16L, 1L, 1.05, 1.10, 1.05, false, false, false, 3));
        visitor_types.add(new Visitor_Type(28L, "Whippersnappette", "Don't tell Steve, but his secret mine isn't so secret.", 5L, 15L, 1L, 1.05, 1.15, 1.05, false, false, false, 5));
        visitor_types.add(new Visitor_Type(29L, "Hatty", "I wish my hair wasn't so exposed to the elements.", 11L, 12L, 1L, 1.08, 1.15, 1.05, false, false, false, 5));
        visitor_types.add(new Visitor_Type(30L, "Archer", "It's all about the bullseye.", 2L, 6L, 5L, 1.10, 1.25, 1.15, false, false, false, 2));
        visitor_types.add(new Visitor_Type(31L, "Steve", "Gotta dig deeper and deeper and deeper and...", 6L, 15L, 1L, 1.05, 1.15, 1.05, false, false, false, 6));
        visitor_types.add(new Visitor_Type(32L, "Whippersnapper", "Don't tell Steve, but his secret fish pond isn't so secret.", 11L, 17L, 4L, 1.05, 1.20, 1.02, false, false, false, 6));
        visitor_types.add(new Visitor_Type(33L, "Mr T Utorial", "Need a hand?", 1L, 3L, 4L, 1.05, 1.05, 1.05, true, false, false, 1));
        visitor_types.add(new Visitor_Type(34L, "Sammy the Snake", "Ssscared of sssnakesss?", 3L, 6L, 1L, 1.10, 1.15, 1.05, false, false, false, 9));
        visitor_types.add(new Visitor_Type(35L, "Power Orb", "I am all powerful. Okay, apart from equipment. Shh.", 6L, 1L, 4L, 1.03, 1.12, 1.09, false, false, false, 7));
        visitor_types.add(new Visitor_Type(36L, "Frankie Fire", "Is it hot in here?", 10L, 23L, 3L, 1.08, 1.15, 1.20, false, false, false, 7));
        visitor_types.add(new Visitor_Type(37L, "Emerald Giant", "I.. can.. barely.. move.", 5L, 4L, 5L, 1.16, 1.08, 1.16, false, false, false, 6));
        visitor_types.add(new Visitor_Type(38L, "PURPLEBOT9000", "BZZ.. GIVE SILVER. PLZ.", 8L, 24L, 1L, 1.19, 1.11, 1.01, false, false, false, 5));
        visitor_types.add(new Visitor_Type(39L, "Whirling Dervish", "I'm a little bit dizzy. Actually, very dizzy.", 6L, 2L, 4L, 1.10, 1.02, 1.12, false, false, false, 5));
        visitor_types.add(new Visitor_Type(40L, "Stumps", "Sure is cold up there.", 11L, 13L, 1L, 1.04, 1.12, 1.03, false, false, false, 9));
        visitor_types.add(new Visitor_Type(41L, "Octomum", "I am a completely normal human. Completely normal.", 4L, 14L, 4L, 1.18, 1.13, 1.08, false, false, false, 3));
        visitor_types.add(new Visitor_Type(42L, "Battletoad", "Make it quick, I've got somewhere to be.", 5L, 17L, 5L, 1.08, 1.20, 1.07, false, false, false, 1));
        visitor_types.add(new Visitor_Type(43L, "Casper", "I hope I'm not scaring you. I'm so sorry if I am. Sorry.", 8L, 22L, 6L, 1.08, 1.11, 1.09, false, false, false, 4));
        visitor_types.add(new Visitor_Type(44L, "Mummioso", "I am more than a mere mummy, I am Mummioso!", 11L, 10L, 6L, 1.02, 1.12, 1.04, false, false, false, 4));
        visitor_types.add(new Visitor_Type(45L, "The Black Knight", "It's not even a scratch!", 2L, 5L, 7L, 1.10, 1.14, 1.15, false, false, false, 4));
        visitor_types.add(new Visitor_Type(46L, "Mrs BRAINS", "Hello! Oh, also BRAINS.", 1L, 12L, 5L, 1.11, 1.16, 1.16, false, false, false, 1));
        visitor_types.add(new Visitor_Type(47L, "Sir Stumps", "My bark is as good as my bite.", 11L, 13L, 1L, 1.05, 1.13, 1.04, false, false, false, 1));
        visitor_types.add(new Visitor_Type(48L, "The Great Bread Menace", "You there! Got any of the yeasty stuff?", 11L, 21L, 1L, 1.05, 1.55, 1.05, false, false, false, 1));
        visitor_types.add(new Visitor_Type(49L, "Bloody Mary", "Bloody Mary, Bloody Mary, Bloody Mary!", 8L, 22L, 6L, 1.08, 1.11, 1.09, false, false, false, 1));
        visitor_types.add(new Visitor_Type(50L, "COLDBOT5000", "It's freezerin' time!", 6L, 18L, 4L, 1.13, 1.15, 1.09, false, false, false, 6));

        Visitor_Type.saveInTx(visitor_types);
    }
}

