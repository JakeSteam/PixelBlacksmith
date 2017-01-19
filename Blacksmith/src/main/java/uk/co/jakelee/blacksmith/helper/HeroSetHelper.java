package uk.co.jakelee.blacksmith.helper;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmith.components.Hero_Set;
import uk.co.jakelee.blacksmith.model.Hero;

public class HeroSetHelper {
    public static List<Hero_Set> getCurrentSets(Hero hero) {
        List<Hero_Set> sets = new ArrayList<>();

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

        if (onlyCoreItemsEquipped(hero)) {
            sets.add(new Hero_Set("Bare Essentials", "having only the essential slots filled (Helmet, Armour, Weapon, Shield).", 25));
        }

        return sets;
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
                && hero.getBootsState() == hero.getFoodState()
                && hero.getFoodState() == hero.getGlovesState()
                && hero.getGlovesState() == hero.getHelmetState()
                && hero.getHelmetState() == hero.getRingState()
                && hero.getRingState() == hero.getShieldState()
                && hero.getShieldState() == hero.getWeaponState();
    }

    public static double getMultiplier(Hero hero) {
        List<Hero_Set> sets = getCurrentSets(hero);
        double multiplier = 1;
        for (Hero_Set set : sets) {
            multiplier = 1 + (multiplier * (((double)set.getBonus()) / 100));
        }
        return multiplier;
    }
}
