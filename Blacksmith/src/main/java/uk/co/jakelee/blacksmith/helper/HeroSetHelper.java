package uk.co.jakelee.blacksmith.helper;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmith.components.Hero_Set;
import uk.co.jakelee.blacksmith.model.Hero;
import uk.co.jakelee.blacksmith.model.Item;

public class HeroSetHelper {

    public static List<Hero_Set> getCurrentSets(Hero hero) {
        List<Hero_Set> sets = new ArrayList<>();

        Item foodItem = Item.findById(Item.class, hero.getFoodItem());
        Item ringItem = Item.findById(Item.class, hero.getRingItem());
        Item armourItem = Item.findById(Item.class, hero.getArmourItem());
        Item bootsItem = Item.findById(Item.class, hero.getBootsItem());
        Item glovesItem = Item.findById(Item.class, hero.getGlovesItem());
        Item helmetItem = Item.findById(Item.class, hero.getHelmetItem());
        Item shieldItem = Item.findById(Item.class, hero.getShieldItem());
        Item weaponItem = Item.findById(Item.class, hero.getWeaponItem());

        if (itemsInMetalSlots(hero) && itemsAllSameTier(armourItem, bootsItem, glovesItem, helmetItem, shieldItem, weaponItem)) {
            sets.add(new Hero_Set("Consistency King", "having the same tier in all metal slots.", 50));
        }

        if (itemsInMetalSlots(hero) && itemsAllSelectedTier(Constants.TIER_BRONZE, armourItem, bootsItem, glovesItem, helmetItem, shieldItem, weaponItem)) {
            sets.add(new Hero_Set("Brown And Out", "having all bronze items equipped.", 10));
        }

        if (itemsInMetalSlots(hero) && itemsAllSelectedTier(Constants.TIER_ADAMANT, armourItem, bootsItem, glovesItem, helmetItem, shieldItem, weaponItem)) {
            sets.add(new Hero_Set("Flower Power", "having all green (adamant) items equipped.", 10));
        }

        if (itemsInMetalSlots(hero) && itemsAllSelectedTier(Constants.TIER_DRAGON, armourItem, bootsItem, glovesItem, helmetItem, shieldItem, weaponItem)) {
            sets.add(new Hero_Set("Fire Breather", "having all dragon items equipped.", 18));
        }

        if (itemsInMetalSlots(hero) && itemsAllSelectedTier(Constants.TIER_PREMIUM, armourItem, bootsItem, glovesItem, helmetItem, shieldItem, weaponItem)) {
            sets.add(new Hero_Set("Lord of Legends", "having all legendary items equipped.", 30));
        }

        if (numberOfItemsEquipped(hero, 1)) {
            sets.add(new Hero_Set("One Itemer", "having only one item equipped.", 40));
        }

        if (itemsInAllSlots(hero)) {
            sets.add(new Hero_Set("Fully Loaded", "having an item in every slot.", 20));
        }

        if (onlyFoodEquipped(hero)) {
            sets.add(new Hero_Set("Hungry", "only having a food item equipped.", 10));
        }

        if (itemsInNoSlots(hero)) {
            sets.add(new Hero_Set("Broke", "having no items equipped.", 0));
        }

        if (itemsAllSameState(hero) && itemsInAllSlots(hero)) {
            sets.add(new Hero_Set("State Of Mind", "having all equipped items of the same state.", 20));
        }

        if (itemsAllSelectedState(Constants.STATE_RED, hero) && itemsInAllSlots(hero)) {
            sets.add(new Hero_Set("Firey Demon", "having all red enchanted items equipped.", 25));
        }

        if (itemsAllSelectedState(Constants.STATE_UNFINISHED, hero) && itemsInAllSlots(hero)) {
            sets.add(new Hero_Set("Falling Apart", "having all unfinished items equipped.", 25));
        }

        if (onlyCoreItemsEquipped(hero)) {
            sets.add(new Hero_Set("Bare Essentials", "having only the essential slots filled (Helmet, Armour, Weapon, Shield).", 25));
        }

        if (onlyAdditionalItemsEquipped(hero)) {
            sets.add(new Hero_Set("Bare Inessentials", "having only the inessential slots filled (Gloves, Boots, Ring, Food).", 25));
        }

        if (onlyCornerItems(hero)) {
            sets.add(new Hero_Set("Corner The Market", "having only the corner slots filled in.", 10));
        }

        if (onlyMiddleItems(hero)) {
            sets.add(new Hero_Set("The Plus Side", "having a \"plus\" (+) of equipped items.", 22));
        }

        if (itemsInMetalSlots(hero) && itemsAllSelectedTier(Constants.TIER_IRON, armourItem, bootsItem, glovesItem, helmetItem, shieldItem, weaponItem)) {
            sets.add(new Hero_Set("Oh The Irony", "having all iron items equipped.", 30));
        }

        if (itemsInMetalSlots(hero) && itemsAllSelectedTier(Constants.TIER_STEEL, armourItem, bootsItem, glovesItem, helmetItem, shieldItem, weaponItem)) {
            sets.add(new Hero_Set("Nerves Of Steel", "having all steel items equipped.", 27));
        }

        if (hero.getArmourItem() == 119) {
            sets.add(new Hero_Set("Dragon Slayee", "have a rune chainmail equipped.", 1));
        }

        if (hero.getArmourItem() == 120) {
            sets.add(new Hero_Set("Dragon Slayer", "have a rune platebody equipped.", 11));
        }

        if (itemsAllSelectedState(Constants.STATE_YELLOW, hero) && itemsInAllSlots(hero) && itemsAllSelectedTier(Constants.TIER_DRAGON, armourItem, bootsItem, glovesItem, helmetItem, shieldItem, weaponItem)) {
            sets.add(new Hero_Set("Godlike", "have yellow enchanted legendary items in all slots.", 70));
        }

        if (itemsAllSelectedState(Constants.STATE_YELLOW, hero) && itemsInAllSlots(hero) && itemsAllSelectedTier(Constants.TIER_PREMIUM, armourItem, bootsItem, glovesItem, helmetItem, shieldItem, weaponItem)) {
            sets.add(new Hero_Set("Godly", "have yellow-enchanted legendary items in all slots.", 100));
        }

        return sets;
    }

    private static boolean onlyMiddleItems(Hero hero) {
        return hero.getArmourItem() == 0
                && hero.getBootsItem() > 0
                && hero.getFoodItem() == 0
                && hero.getGlovesItem() == 0
                && hero.getHelmetItem() > 0
                && hero.getRingItem() == 0
                && hero.getShieldItem() > 0
                && hero.getWeaponItem() > 0;
    }

    private static boolean onlyCornerItems(Hero hero) {
        return hero.getArmourItem() > 0
                && hero.getBootsItem() == 0
                && hero.getFoodItem() > 0
                && hero.getGlovesItem() > 0
                && hero.getHelmetItem() == 0
                && hero.getRingItem() > 0
                && hero.getShieldItem() == 0
                && hero.getWeaponItem() == 0;
    }

    private static boolean numberOfItemsEquipped(Hero hero, int desiredNumber) {
        int actualNumber = 0;
        if (hero.getFoodItem() > 0) {
            actualNumber++;
        }
        if (hero.getRingItem() > 0) {
            actualNumber++;
        }
        if (hero.getArmourItem() > 0) {
            actualNumber++;
        }
        if (hero.getBootsItem() > 0) {
            actualNumber++;
        }
        if (hero.getGlovesItem() > 0) {
            actualNumber++;
        }
        if (hero.getHelmetItem() > 0) {
            actualNumber++;
        }
        if (hero.getShieldItem() > 0) {
            actualNumber++;
        }
        if (hero.getWeaponItem() > 0) {
            actualNumber++;
        }

        return actualNumber == desiredNumber;
    }

    private static boolean itemsInMetalSlots(Hero hero) {
        return hero.getArmourItem() > 0
                && hero.getBootsItem() > 0
                && hero.getGlovesItem() > 0
                && hero.getHelmetItem() > 0
                && hero.getShieldItem() > 0
                && hero.getWeaponItem() > 0;
    }

    private static boolean itemsAllSelectedTier(int tier, Item armourItem, Item bootsItem, Item glovesItem, Item helmetItem, Item shieldItem, Item weaponItem) {
        return armourItem.getTier() == tier
                && itemsAllSameTier(armourItem, bootsItem, glovesItem, helmetItem, shieldItem, weaponItem);
    }

    private static boolean itemsAllSelectedState(int state, Hero hero) {
        return hero.getArmourState() == state && itemsAllSameState(hero);
    }

    private static boolean itemsAllSameTier(Item armourItem, Item bootsItem, Item glovesItem, Item helmetItem, Item shieldItem, Item weaponItem) {
        return armourItem.getTier() == bootsItem.getTier()
                && bootsItem.getTier() == glovesItem.getTier()
                && glovesItem.getTier() == helmetItem.getTier()
                && helmetItem.getTier() == shieldItem.getTier()
                && shieldItem.getTier() == weaponItem.getTier();
    }

    private static boolean onlyCoreItemsEquipped(Hero hero) {
        return hero.getArmourItem() > 0
                && hero.getBootsItem() == 0
                && hero.getFoodItem() == 0
                && hero.getGlovesItem() == 0
                && hero.getHelmetItem() > 0
                && hero.getRingItem() == 0
                && hero.getShieldItem() > 0
                && hero.getWeaponItem() > 0;
    }

    private static boolean onlyAdditionalItemsEquipped(Hero hero) {
        return hero.getArmourItem() == 0
                && hero.getBootsItem() > 0
                && hero.getFoodItem() > 0
                && hero.getGlovesItem() > 0
                && hero.getHelmetItem() == 0
                && hero.getRingItem() > 0
                && hero.getShieldItem() == 0
                && hero.getWeaponItem() == 0;
    }

    private static boolean itemsInAllSlots(Hero hero) {
        return hero.getArmourItem() > 0
                && hero.getBootsItem() > 0
                && hero.getFoodItem() > 0
                && hero.getGlovesItem() > 0
                && hero.getHelmetItem() > 0
                && hero.getRingItem() > 0
                && hero.getShieldItem() > 0
                && hero.getWeaponItem() > 0;
    }

    private static boolean onlyFoodEquipped(Hero hero) {
        return hero.getArmourItem() == 0
                && hero.getBootsItem() == 0
                && hero.getFoodItem() > 0
                && hero.getGlovesItem() == 0
                && hero.getHelmetItem() == 0
                && hero.getRingItem() == 0
                && hero.getShieldItem() == 0
                && hero.getWeaponItem() == 0;
    }

    private static boolean itemsInNoSlots(Hero hero) {
        return hero.getArmourItem() == 0
                && hero.getBootsItem() == 0
                && hero.getFoodItem() == 0
                && hero.getGlovesItem() == 0
                && hero.getHelmetItem() == 0
                && hero.getRingItem() == 0
                && hero.getShieldItem() == 0
                && hero.getWeaponItem() == 0;
    }

    private static boolean itemsAllSameState(Hero hero) {
        return hero.getArmourState() == hero.getBootsState()
                && hero.getBootsState() == hero.getGlovesState()
                && hero.getGlovesState() == hero.getHelmetState()
                && hero.getHelmetState() == hero.getShieldState()
                && hero.getShieldState() == hero.getWeaponState();
    }

    public static double getMultiplier(Hero hero) {
        List<Hero_Set> sets = getCurrentSets(hero);
        int bonus = 0;
        for (Hero_Set set : sets) {
            bonus += set.getBonus();
        }
        return 1 + ((double) bonus / 100);
    }
}
