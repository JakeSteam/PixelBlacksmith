package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.controls.TextViewPixel;
import uk.co.jakelee.blacksmith.model.Character;
import uk.co.jakelee.blacksmith.model.Hero;
import uk.co.jakelee.blacksmith.model.Hero_Adventure;
import uk.co.jakelee.blacksmith.model.Hero_Resource;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Message;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Setting;
import uk.co.jakelee.blacksmith.model.Super_Upgrade;
import uk.co.jakelee.blacksmith.model.Upgrade;
import uk.co.jakelee.blacksmith.model.Visitor_Stats;
import uk.co.jakelee.blacksmith.model.Visitor_Type;
import uk.co.jakelee.blacksmith.model.Worker;
import uk.co.jakelee.blacksmith.model.Worker_Resource;

public class WorkerHelper {
    public final static String INTENT_ID = "uk.co.jakelee.blacksmith.workerID";
    public final static String INTENT_TYPE = "uk.co.jakelee.blacksmith.equipmentType";
    public final static String INTENT_HERO = "uk.co.jakelee.blacksmith.hero";

    public static List<Worker_Resource> getResourcesByTool(int toolID) {
        return Select.from(Worker_Resource.class).where(
                Condition.prop("tool_id").eq(toolID))
                .orderBy("resource_quantity DESC").list();
    }

    public static List<Hero_Resource> getResourcesByAdventure(int adventureID) {
        return Select.from(Hero_Resource.class).where(
                Condition.prop("adventure_id").eq(adventureID))
                .orderBy("resource_quantity DESC").list();
    }

    public static void populateResources(DisplayHelper dh, LinearLayout container, long toolID) {
        List<Worker_Resource> resources = getResourcesByTool((int) toolID);
        for (Worker_Resource resource : resources) {
            container.addView(dh.createImageView("item", String.valueOf(resource.getResourceID()), 22, 22));
        }
    }

    public static void populateResources(DisplayHelper dh, LinearLayout container, int adventureId) {
        List<Hero_Resource> resources = getResourcesByAdventure(adventureId);
        for (Hero_Resource resource : resources) {
            container.addView(dh.createImageView("item", String.valueOf(resource.getResourceID()), 22, 22));
        }
    }

    public static boolean isReady(Worker worker) {
        return worker.getTimeStarted() == 0;
    }

    public static boolean isReady(Hero hero) {
        return hero.getTimeStarted() == 0 && hero.getCurrentAdventure() > 0;
    }

    public static String getTimeRemainingString(long timeStarted) {
        return DateHelper.getHoursMinsRemaining(getTimeRemaining(timeStarted) + (DateHelper.MILLISECONDS_IN_SECOND * 60)); // Rounded up.
    }

    public static long getTimeRemaining(long timeStarted) {
        int minutesForCompletion = Upgrade.getValue("Worker Time");

        if (Super_Upgrade.isEnabled(Constants.SU_HALF_WORKER_TIME)) {
            minutesForCompletion = minutesForCompletion / 2;
        }

        long timeForCompletion = DateHelper.minutesToMilliseconds(minutesForCompletion);
        return (timeStarted + timeForCompletion) - System.currentTimeMillis();
    }

    public static int getBuyCost(Worker worker) {
        return worker.getLevelUnlocked() * Constants.WORKER_COST_MULTIPLIER;
    }

    public static int getBuyCost(Hero hero) {
        return hero.getLevelUnlocked() * Constants.HERO_COST_MULTIPLIER;
    }

    public static String getButtonText(Context context, Worker worker) {
        if (isReady(worker)) {
            return context.getString(R.string.worker_start_gathering);
        } else {
            return context.getString(R.string.worker_returns_in) + WorkerHelper.getTimeRemainingString(worker.getTimeStarted());
        }
    }

    public static String getButtonText(Context context, Hero hero) {
        if (isReady(hero)) {
            Hero_Adventure adventure = Hero_Adventure.getAdventure(hero.getCurrentAdventure());
            Visitor_Type vType = Visitor_Type.findById(Visitor_Type.class, hero.getVisitorId());
            return context.getString(R.string.worker_start_adventure) + " (" + getAdventureSuccessChance(getTotalStrength(hero, vType), adventure.getDifficulty()) + "%)";
        } else if (hero.getVisitorId() == 0) {
            return context.getString(R.string.worker_select_hero);
        } else if (hero.getCurrentAdventure() == 0) {
            return context.getString(R.string.worker_select_adventure);
        } else {
            return context.getString(R.string.worker_returns_in) + WorkerHelper.getTimeRemainingString(hero.getTimeStarted());
        }
    }

    public static boolean sendOutWorker(Worker worker) {
        if (!isReady(worker)) {
            return false;
        } else {
            worker.setTimeStarted(System.currentTimeMillis());
            worker.save();
            return true;
        }
    }

    public static boolean sendOutHero(Hero hero) {
        if (!isReady(hero)) {
            return false;
        } else {
            hero.setTimeStarted(System.currentTimeMillis());
            hero.save();
            return true;
        }
    }

    public static void checkForFinishedHeroes(Context context) {
        List<Hero> heroes = Select.from(Hero.class).where(
                Condition.prop("purchased").eq(1),
                Condition.prop("time_started").notEq(0)).list();
        int heroesFinished = 0;
        int heroesSuccessful = 0;
        List<String> heroNames = new ArrayList<>();
        String lastResult = "";

        boolean refillFood = Setting.getSafeBoolean(Constants.SETTING_AUTOFEED);

        for (Hero hero : heroes) {
            if (getTimeRemaining(hero.getTimeStarted()) <= 0) {
                Visitor_Type heroVisitor = Visitor_Type.findById(Visitor_Type.class, hero.getVisitorId());
                int adventureResult = getAdventureResult(hero);
                Hero_Adventure adventure = Hero_Adventure.getAdventure(hero.getCurrentAdventure());

                if (adventureResult == Constants.HERO_RESULT_SUCCESS || adventureResult == Constants.HERO_RESULT_SUPER_SUCCESS) {
                    Player_Info.addXp(adventure.getDifficulty());
                    adventure.setCompleted(true);
                    adventure.save();
                    lastResult = rewardResources(context, hero, adventureResult == Constants.HERO_RESULT_SUPER_SUCCESS);
                    heroesSuccessful++;
                } else {
                    List<EQUIP_SLOTS> slotsToEmpty = getSlotsToEmpty(hero, adventureResult);
                    String lastResultString = removeEquippedItems(hero, slotsToEmpty);
                    if (slotsToEmpty.size() > 0) {
                        lastResult = String.format(context.getString(R.string.heroFailText),
                                heroVisitor.getName(context),
                                adventure.getName(context),
                                lastResultString);
                    } else {
                        lastResult = String.format(context.getString(R.string.heroFailNoItemsText),
                                heroVisitor.getName(context),
                                adventure.getName(context));
                    }
                }
                heroNames.add(heroVisitor.getName(context));
                completeHero(hero, refillFood, heroVisitor, adventureResult == Constants.HERO_RESULT_SUCCESS || adventureResult == Constants.HERO_RESULT_SUPER_SUCCESS);
                heroesFinished++;
                Message.add(lastResult);
            }
        }

        if (heroesFinished > 1) {
            ToastHelper.showPositiveToast(((Activity) context).findViewById(R.id.worker), ToastHelper.LONG, String.format(context.getString(R.string.heroesReturned),
                    heroesFinished,
                    workerNamesToString(heroNames),
                    heroesSuccessful), true);
        } else if (heroesFinished > 0 && heroesSuccessful > 0) {
            ToastHelper.showPositiveToast(null, ToastHelper.LONG, lastResult, true);
        } else if (heroesFinished > 0 && heroesSuccessful == 0) {
            ToastHelper.showErrorToast(null, ToastHelper.LONG, lastResult, true);
        }

        GooglePlayHelper.UpdateEvent(Constants.EVENT_HERO_TRIPS, heroesSuccessful);
    }

    public static List<EQUIP_SLOTS> getSlotsWithItems(Hero hero) {
        List<EQUIP_SLOTS> slotsWithItems = new ArrayList<>();
        if (hero.getHelmetItem() > 0) {
            slotsWithItems.add(EQUIP_SLOTS.Helmet);
        }
        if (hero.getArmourItem() > 0) {
            slotsWithItems.add(EQUIP_SLOTS.Armour);
        }
        if (hero.getWeaponItem() > 0) {
            slotsWithItems.add(EQUIP_SLOTS.Weapon);
        }
        if (hero.getShieldItem() > 0) {
            slotsWithItems.add(EQUIP_SLOTS.Shield);
        }
        if (hero.getGlovesItem() > 0) {
            slotsWithItems.add(EQUIP_SLOTS.Gloves);
        }
        if (hero.getBootsItem() > 0) {
            slotsWithItems.add(EQUIP_SLOTS.Boots);
        }
        if (hero.getRingItem() > 0) {
            slotsWithItems.add(EQUIP_SLOTS.Ring);
        }
        return slotsWithItems;
    }

    public static List<EQUIP_SLOTS> getSlotsToEmpty(Hero hero, int itemsToRemove) {
        List<EQUIP_SLOTS> slotsWithItems = getSlotsWithItems(hero);
        if (slotsWithItems.size() == 0 || slotsWithItems.size() <= itemsToRemove) {
            return slotsWithItems;
        }

        Collections.shuffle(slotsWithItems);

        List<EQUIP_SLOTS> slotsToEmpty = new ArrayList<>();
        for (int i = 0; i < itemsToRemove; i++) {
            slotsToEmpty.add(slotsWithItems.get(i));
        }
        return slotsToEmpty;
    }

    public static String removeEquippedItems(Hero hero, List<EQUIP_SLOTS> slotsToEmpty) {
        String slotsString = "";
        for (EQUIP_SLOTS slot : slotsToEmpty) {
            slotsString += slot.name() + ", ";
            switch (slot) {
                case Helmet:
                    hero.setHelmetItem(0);
                    hero.setHelmetState(0);
                    break;
                case Armour:
                    hero.setArmourItem(0);
                    hero.setArmourState(0);
                    break;
                case Weapon:
                    hero.setWeaponItem(0);
                    hero.setWeaponState(0);
                    break;
                case Shield:
                    hero.setShieldItem(0);
                    hero.setShieldState(0);
                    break;
                case Gloves:
                    hero.setGlovesItem(0);
                    hero.setGlovesState(0);
                    break;
                case Boots:
                    hero.setBootsItem(0);
                    hero.setBootsState(0);
                    break;
                case Ring:
                    hero.setRingItem(0);
                    hero.setRingState(0);
                    break;
            }
        }
        hero.save();

        if (slotsString.endsWith(", ")) {
            slotsString = slotsString.substring(0, slotsString.length() - 2);
        }

        return slotsString;
    }

    public static int getAdventureResult(Hero hero) {
        Hero_Adventure adventure = Select.from(Hero_Adventure.class).where(Condition.prop("adventure_id").eq(hero.getCurrentAdventure())).first();
        Visitor_Type vType = Visitor_Type.findById(Visitor_Type.class, hero.getVisitorId());
        int heroStrength = WorkerHelper.getTotalStrength(hero, vType);
        int difficulty = adventure.getDifficulty();
        int chanceOfSuccess = getAdventureSuccessChance(heroStrength, difficulty);
        boolean isSuccessful = VisitorHelper.getRandomBoolean(100 - chanceOfSuccess);
        if (chanceOfSuccess >= 100) {
            return Constants.HERO_RESULT_SUPER_SUCCESS;
        } else if (isSuccessful) {
            return Constants.HERO_RESULT_SUCCESS;
        } else {
            return VisitorHelper.getRandomNumber(Constants.HERO_RESULT_MIN, Constants.HERO_RESULT_MAX);
        }
    }

    public static int getAdventureSuccessChance(int heroStrength, int difficulty) {
        int strPercentOfDiff = (heroStrength / difficulty) * 100;
        if (heroStrength < difficulty) {
            int successChance = strPercentOfDiff - 50;
            return successChance > 0 ? successChance : 0;
        } else {
            int successChance = strPercentOfDiff / 2;
            return successChance < 100 ? successChance : 100;
        }
    }

    public static void completeHero(Hero hero, boolean refillFood, Visitor_Type heroVisitor, boolean successful) {
        // If autorefill is on and hero has food, remove 1 from inventory and leave the current food used.
        if (hero.getFoodItem() > 0) {
            Inventory currentFoodStock = Inventory.getInventory((long) hero.getFoodItem(), Constants.STATE_NORMAL);
            if (currentFoodStock.getQuantity() > 0 && refillFood) {
                currentFoodStock.setQuantity(currentFoodStock.getQuantity() - 1);
                currentFoodStock.save();
            } else {
                hero.setFoodItem(0);
            }
        }

        hero.setTimeStarted(0);
        hero.save();

        if (successful) {
            heroVisitor.setAdventuresCompleted(heroVisitor.getAdventuresCompleted() + 1);
        }
        heroVisitor.setAdventuresAttempted(heroVisitor.getAdventuresAttempted() + 1);
        heroVisitor.save();
    }

    public static void checkForFinishedWorkers(Context context) {
        List<Worker> workers = Select.from(Worker.class).where(
                Condition.prop("purchased").eq(1),
                Condition.prop("time_started").notEq(0)).list();
        int workersFinished = 0;
        List<String> workerNames = new ArrayList<>();
        String rewardText;
        boolean refillFood = Setting.getSafeBoolean(Constants.SETTING_AUTOFEED);

        for (Worker worker : workers) {
            if (getTimeRemaining(worker.getTimeStarted()) <= 0) {
                rewardText = rewardResources(context, worker);
                workerNames.add(Character.findById(Character.class, worker.getCharacterID()).getName(context));
                workersFinished++;

                // If autorefill is on and worker has food, remove 1 from inventory and leave the current food used.
                if (worker.getFoodUsed() > 0) {
                    Inventory currentFoodStock = Inventory.getInventory(worker.getFoodUsed(), Constants.STATE_NORMAL);
                    if (currentFoodStock.getQuantity() > 0 && refillFood) {
                        currentFoodStock.setQuantity(currentFoodStock.getQuantity() - 1);
                        currentFoodStock.save();
                    } else {
                        worker.setFoodUsed(0);
                    }
                }

                worker.setTimeStarted(0);
                worker.setTimesCompleted(worker.getTimesCompleted() + 1);
                worker.save();

                ToastHelper.showPositiveToast(null, ToastHelper.LONG, rewardText, true);
            }
        }

        if (workersFinished > 1) {
            ToastHelper.showPositiveToast(null, ToastHelper.LONG, String.format(context.getString(R.string.workersReturned),
                    workersFinished,
                    workerNamesToString(workerNames)), true);
        }

        GooglePlayHelper.UpdateEvent(Constants.EVENT_HELPER_TRIPS, workersFinished);
    }

    private static String workerNamesToString(List<String> names) {
        String nameString = "";
        for (String name : names) {
            nameString += "\"" + name + "\", ";
        }

        return nameString.replaceAll(", $", "");
    }

    public static String rewardResources(Context context, Worker worker) {
        Character workerCharacter = Character.findById(Character.class, worker.getCharacterID());
        List<Worker_Resource> resources = getResourcesByTool((int) worker.getToolUsed());
        return String.format(context.getString(R.string.workerReturned),
                workerCharacter.getName(context),
                getRewardResourcesText(context, worker, resources, true));
    }

    private static String rewardResources(Context context, Hero hero, boolean superSuccess) {
        Visitor_Type vType = Visitor_Type.findById(Visitor_Type.class, hero.getVisitorId());
        List<Hero_Resource> resources = getResourcesByAdventure(hero.getCurrentAdventure());
        if (superSuccess) {
            resources = superSuccessResources(resources);
        }
        return String.format(context.getString(R.string.workerReturned),
                vType.getName(context),
                getRewardResourcesText(context, hero, resources, true, superSuccess));
    }

    private static List<Hero_Resource> superSuccessResources(List<Hero_Resource> resources) {
        double modifier = (VisitorHelper.getRandomNumber(Constants.SUPER_SUCCESS_MINIMUM, Constants.SUPER_SUCCESS_MAXIMUM) / 100);
        for (Hero_Resource resource : resources) {
            double modifiedQuantity = ((double) resource.getResourceQuantity()) * modifier;
            resource.setResourceQuantity((int) Math.ceil(modifiedQuantity));
        }
        return resources;
    }

    public static String getRewardResourcesText(Context context, Hero hero, List<Hero_Resource> resources, boolean addItems, boolean isSuperSuccess) {
        LinkedHashMap<String, Integer> data = new LinkedHashMap<>();
        Item foodItem = Item.findById(Item.class, hero.getFoodItem());

        for (Hero_Resource resource : resources) {
            int numberResources = (Super_Upgrade.isEnabled(Constants.SU_WORKER_RESOURCES) ? 2 : 1) * resource.getResourceQuantity();
            if (addItems) {
                Inventory resourceInventory = Inventory.getInventory((long) resource.getResourceID(), resource.getResourceState());
                resourceInventory.setQuantity(resourceInventory.getQuantity() + numberResources);
                resourceInventory.save();
            }

            Item item = Item.findById(Item.class, resource.getResourceID());
            Integer temp;
            if (data.containsKey(item.getName(context))) {
                temp = data.get(item.getName(context)) + numberResources;
                data.put(item.getName(context), temp);
            } else {
                data.put(item.getName(context), numberResources);
            }
        }

        String bonusText = "";
        if (addItems &&
                (Super_Upgrade.isEnabled(Constants.SU_PAGE_CHANCE) ||
                        (foodItem != null && VisitorHelper.getRandomBoolean(100 - foodItem.getValue())))) {
            // If rewarding resources, and have luckily got a page
            List<Item> pages = Select.from(Item.class).where(Condition.prop("type").eq(Constants.TYPE_PAGE)).list();
            Item rewardedPage = VisitorHelper.pickRandomItemFromList(pages);
            Inventory.addItem(rewardedPage.getId(), Constants.STATE_NORMAL, 1);

            bonusText = ", " + context.getString(R.string.fragment_and_a_rare) + " " + rewardedPage.getName(context);
        } else if (!addItems && foodItem != null) {
            bonusText = ", " + context.getString(R.string.fragment_and_a_page);
        }

        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();

            result.append(String.format("%dx %s, ", value, key));
        }

        try {
            return result.substring(0, result.length() - 2) + bonusText;
        } catch (IndexOutOfBoundsException e) {
            return result + bonusText;
        }
    }

    public static String getRewardResourcesText(Context context, Worker worker, List<Worker_Resource> resources, boolean addItems) {
        LinkedHashMap<String, Integer> data = new LinkedHashMap<>();
        Item foodItem = Item.findById(Item.class, worker.getFoodUsed());
        boolean applyFoodBonus = worker.getFoodUsed() > 0 && (worker.getTimeStarted() > 0 || addItems);

        boolean favouriteFoodUsed = false;
        if (worker.getFoodUsed() == worker.getFavouriteFood()) {
            favouriteFoodUsed = true;
            worker.setFavouriteFoodDiscovered(true);
            worker.save();
        }

        for (Worker_Resource resource : resources) {
            if (applyFoodBonus) {
                resource.applyFoodBonus(foodItem, favouriteFoodUsed);
            }
            int numberResources = (Super_Upgrade.isEnabled(Constants.SU_WORKER_RESOURCES) ? 2 : 1) * resource.getResourceQuantity();

            if (addItems) {
                Inventory resourceInventory = Inventory.getInventory((long) resource.getResourceID(), resource.getResourceState());
                resourceInventory.setQuantity(resourceInventory.getQuantity() + numberResources);
                resourceInventory.save();
            }

            Item item = Item.findById(Item.class, resource.getResourceID());
            Integer temp;
            if (data.containsKey(item.getName(context))) {
                temp = data.get(item.getName(context)) + numberResources;
                data.put(item.getName(context), temp);
            } else {
                data.put(item.getName(context), numberResources);
            }
        }

        String bonusText = "";
        if (addItems &&
                (Super_Upgrade.isEnabled(Constants.SU_PAGE_CHANCE) ||
                        (foodItem != null && VisitorHelper.getRandomBoolean(100 - foodItem.getValue())))) {
            // If rewarding resources, and have luckily got a page
            List<Item> pages = Select.from(Item.class).where(Condition.prop("type").eq(Constants.TYPE_PAGE)).list();
            Item rewardedPage = VisitorHelper.pickRandomItemFromList(pages);
            Inventory.addItem(rewardedPage.getId(), Constants.STATE_NORMAL, 1);

            bonusText = ", " + context.getString(R.string.fragment_and_a_rare) + rewardedPage.getName(context);
        } else if (!addItems && foodItem != null) {
            // If checking resources
            if (foodItem.getId() == worker.getFavouriteFood() && worker.isFavouriteFoodDiscovered()) {
                bonusText = ", " + context.getString(R.string.fragment_and_a_page_very);
            } else {
                bonusText = ", " + context.getString(R.string.fragment_and_a_page);
            }
        }

        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();

            result.append(String.format("%dx %s, ", value, key));
        }

        if (result.length() > 3) {
            return result.substring(0, result.length() - 2) + bonusText;
        }
        return "";
    }

    public static String getTimesCompletedString(Context context, Worker worker) {
        Character character = Character.findById(Character.class, worker.getCharacterID());
        Item foodUsed = Item.findById(Item.class, worker.getFoodUsed());
        return String.format(context.getString(R.string.workerTimesCompleted),
                character.getName(context),
                worker.getTimesCompleted(),
                foodUsed != null ? foodUsed.getName(context) : "N/A",
                foodUsed != null ? (foodUsed.getId() == worker.getFavouriteFood() && worker.isFavouriteFoodDiscovered() ? 2 : 1) * foodUsed.getValue() : 0);
    }

    public static String getTimesCompletedString(Context context, Hero hero) {
        Visitor_Type visitorType = Visitor_Type.findById(Visitor_Type.class, hero.getVisitorId());
        Visitor_Stats visitorStats = Visitor_Stats.findById(Visitor_Stats.class, hero.getVisitorId());
        return String.format(context.getString(R.string.heroTimesCompleted),
                visitorType.getName(context),
                visitorStats.getAdventuresCompleted(),
                WorkerHelper.getTotalStrength(hero, visitorType));
    }

    public static String getTimeLeftString(Context context, Worker worker) {
        Character character = Character.findById(Character.class, worker.getCharacterID());
        String timeRemaining = DateHelper.getHoursMinsSecsRemaining(WorkerHelper.getTimeRemaining(worker.getTimeStarted()));
        return String.format(context.getString(R.string.workerReturnTime),
                character.getName(context),
                timeRemaining);
    }

    public static String getTimeLeftString(Context context, Hero hero) {
        Visitor_Type vType = Visitor_Type.findById(Visitor_Type.class, hero.getVisitorId());
        String timeRemaining = DateHelper.getHoursMinsSecsRemaining(WorkerHelper.getTimeRemaining(hero.getTimeStarted()));
        return String.format(context.getString(R.string.workerReturnTime),
                vType.getName(context),
                timeRemaining);
    }

    public static List<Inventory> getTools(Context context, String selection) {
        String whereClause = "1 > 2";
        if (selection.equals(context.getString(R.string.tool_pickaxe))) {
            whereClause = "type = 15";
        } else if (selection.equals(context.getString(R.string.tool_hammer))) {
            whereClause = "type = 18";
        } else if (selection.equals(context.getString(R.string.tool_fishing_rod))) {
            whereClause = "type = 17";
        } else if (selection.equals(context.getString(R.string.tool_hatchet))) {
            whereClause = "type = 16";
        } else if (selection.equals(context.getString(R.string.tool_gloves))) {
            whereClause = "type = 14";
        } else if (selection.equals(context.getString(R.string.tool_gem))) {
            whereClause = "type = 20";
        } else if (selection.equals(context.getString(R.string.tool_silver_ring))) {
            whereClause = "type = 24 AND tier = 8";
        } else if (selection.equals(context.getString(R.string.tool_gold_ring))) {
            whereClause = "type = 24 AND tier = 9";
        } else if (selection.equals(context.getString(R.string.tool_visage))) {
            whereClause = "id = 148";
        }
        List<Item> items = Item.find(Item.class, whereClause);

        return Inventory.find(Inventory.class, getStringFromMatchingItems(items));
    }

    private static String getStringFromMatchingItems(List<Item> items) {
        StringBuilder itemString = new StringBuilder();
        for (Item item : items) {
            itemString.append(item.getId().toString());
            itemString.append(",");
        }

        if (itemString.length() > 2) {
            return "item IN (" + itemString.substring(0, itemString.length() - 1) + ") AND state = 1 AND quantity > 0 ORDER BY item ASC";
        } else {
            return "item = 9999";
        }
    }

    public static int getBasePrice(int itemId, int state) {
        Item item = Item.findById(Item.class, itemId);
        int baseValue = 4 + (item.getTier() * 4);

        if (item.getTier() == Constants.TIER_NONE) {
            return item.getValue();
        } else if (state == Constants.STATE_NORMAL) {
            return baseValue;
        } else if (state == Constants.STATE_UNFINISHED) {
            return baseValue / 2;
        } else {
            return baseValue + (3 * state);
        }
    }

    public static void setStrengthText(Visitor_Type vType, TextViewPixel view, int item, int state) {
        setStrengthText(vType, view, item, state, false);
    }

    public static void setStrengthText(Visitor_Type vType, TextViewPixel view, int item, int state, boolean useDarkColours) {
        int baseStrength = WorkerHelper.getBasePrice(item, state);
        int bonusStrength = WorkerHelper.getAdjustedStrength(vType, item, state);

        if (useDarkColours) {
            view.setTextColor(baseStrength == bonusStrength ? Color.BLACK : Color.parseColor("#267c18"));
        } else {
            view.setTextColor(baseStrength == bonusStrength ? Color.WHITE : Color.GREEN);
        }

        view.setText(Integer.toString(bonusStrength));
    }

    public static int getAdjustedStrength(Visitor_Type vType, int item, int state) {
        if (item == 0 || state == 0 || vType == null) {
            return 0;
        }

        int baseStrength = WorkerHelper.getBasePrice(item, state);
        double bonus = vType.getBonus(item, state);
        return (int) Math.ceil(baseStrength * bonus);
    }

    public static int getTotalStrength(Hero hero, Visitor_Type vType) {
        int totalStrength = 0;
        totalStrength += getAdjustedStrength(vType, hero.getFoodItem(), hero.getFoodState());
        totalStrength += getAdjustedStrength(vType, hero.getHelmetItem(), hero.getHelmetState());
        totalStrength += getAdjustedStrength(vType, hero.getArmourItem(), hero.getArmourState());
        totalStrength += getAdjustedStrength(vType, hero.getWeaponItem(), hero.getWeaponState());
        totalStrength += getAdjustedStrength(vType, hero.getShieldItem(), hero.getShieldState());
        totalStrength += getAdjustedStrength(vType, hero.getGlovesItem(), hero.getGlovesState());
        totalStrength += getAdjustedStrength(vType, hero.getBootsItem(), hero.getBootsState());
        totalStrength += getAdjustedStrength(vType, hero.getRingItem(), hero.getRingState());

        totalStrength = (int) Math.ceil(((double) totalStrength) * HeroSetHelper.getMultiplier(hero));

        return totalStrength;
    }

    public static boolean canBeSelectedAsHero(Visitor_Type vType, Visitor_Stats vStats) {
        return vType != null && vStats != null &&
                vStats.getVisits() >= Constants.HERO_MIN_VISITS
                && vStats.getBestItemValue() >= Constants.HERO_MIN_TRADE
                && vType.getPreferencesDiscovered() >= Constants.HERO_MIN_PREFS;
    }

    private enum EQUIP_SLOTS {Helmet, Armour, Weapon, Shield, Gloves, Boots, Ring}
}
