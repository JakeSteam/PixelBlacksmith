package uk.co.jakelee.blacksmith.helper;

import android.content.Context;
import android.widget.LinearLayout;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.model.Character;
import uk.co.jakelee.blacksmith.model.Hero;
import uk.co.jakelee.blacksmith.model.Hero_Resource;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Setting;
import uk.co.jakelee.blacksmith.model.Upgrade;
import uk.co.jakelee.blacksmith.model.Visitor_Stats;
import uk.co.jakelee.blacksmith.model.Visitor_Type;
import uk.co.jakelee.blacksmith.model.Worker;
import uk.co.jakelee.blacksmith.model.Worker_Resource;

public class WorkerHelper {
    public final static String INTENT_ID = "uk.co.jakelee.blacksmith.workerID";

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
        return hero.getTimeStarted() == 0;
    }

    public static String getTimeRemainingString(Worker worker) {
        return DateHelper.getHoursMinsRemaining(getTimeRemaining(worker) + (DateHelper.MILLISECONDS_IN_SECOND * 60)); // Rounded up.
    }

    public static String getTimeRemainingString(Hero hero) {
        return DateHelper.getHoursMinsRemaining(getTimeRemaining(hero) + (DateHelper.MILLISECONDS_IN_SECOND * 60)); // Rounded up.
    }

    public static long getTimeRemaining(Worker worker) {
        long timeStarted = worker.getTimeStarted();
        int minutesForCompletion = Upgrade.getValue("Worker Time");
        long timeForCompletion = DateHelper.minutesToMilliseconds(minutesForCompletion);
        return (timeStarted + timeForCompletion) - System.currentTimeMillis();
    }

    public static long getTimeRemaining(Hero hero) {
        long timeStarted = hero.getTimeStarted();
        long timeForCompletion = DateHelper.minutesToMilliseconds(Constants.HERO_MINUTES_TAKEN);
        return (timeStarted + timeForCompletion) - System.currentTimeMillis();
    }

    public static int getBuyCost(Worker worker) {
        return worker.getLevelUnlocked() * Constants.WORKER_COST_MULTIPLIER;
    }

    public static int getBuyCost(Hero hero) {
        return hero.getLevelUnlocked() * Constants.HERO_COST_MULTIPLIER;
    }

    public static String getButtonText(Worker worker) {
        if (isReady(worker)) {
            return "Start gathering";
        } else {
            return "Returns in " + WorkerHelper.getTimeRemainingString(worker);
        }
    }

    public static String getButtonText(Hero hero) {
        if (isReady(hero)) {
            return "Start gathering";
        } else {
            return "Returns in " + WorkerHelper.getTimeRemainingString(hero);
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
        List<String> heroNames = new ArrayList<>();
        String rewardText = "";

        boolean refillFood = Setting.getSafeBoolean(Constants.SETTING_AUTOFEED);

        for (Hero hero : heroes) {
            if (getTimeRemaining(hero) <= 0) {
                rewardText = rewardResources(context, hero);
                Visitor_Type heroVisitor = Visitor_Type.findById(Visitor_Type.class, hero.getVisitorId());
                heroNames.add(heroVisitor.getName());
                heroesFinished++;

                // If autorefill is on and hero has food, remove 1 from inventory and leave the current food used.
                if (refillFood && hero.getFoodItem() > 0) {
                    Inventory currentFoodStock = Inventory.getInventory((long) hero.getFoodItem(), Constants.STATE_NORMAL);
                    if (currentFoodStock.getQuantity() > 0) {
                        currentFoodStock.setQuantity(currentFoodStock.getQuantity() - 1);
                        currentFoodStock.save();
                    } else {
                        hero.setFoodItem(0);
                    }
                }

                hero.setTimeStarted(0);
                hero.save();

                heroVisitor.setAdventuresCompleted(heroVisitor.getAdventuresCompleted() + 1);
                heroVisitor.save();

                ToastHelper.showPositiveToast(null, ToastHelper.LONG, rewardText, true);
            }
        }

        if (heroesFinished > 1) {
            ToastHelper.showPositiveToast(null, ToastHelper.LONG, String.format(context.getString(R.string.heroesReturned),
                    heroesFinished,
                    workerNamesToString(heroNames)), true);
        }
    }

    public static void checkForFinishedWorkers(Context context) {
        List<Worker> workers = Select.from(Worker.class).where(
                Condition.prop("purchased").eq(1),
                Condition.prop("time_started").notEq(0)).list();
        int workersFinished = 0;
        List<String> workerNames = new ArrayList<>();
        String rewardText = "";

        boolean refillFood = Setting.getSafeBoolean(Constants.SETTING_AUTOFEED);

        for (Worker worker : workers) {
            if (getTimeRemaining(worker) <= 0) {
                rewardText = rewardResources(context, worker);
                workerNames.add(Character.findById(Character.class, worker.getCharacterID()).getName());
                workersFinished++;

                // If autorefill is on and worker has food, remove 1 from inventory and leave the current food used.
                if (refillFood && worker.getFoodUsed() > 0) {
                    Inventory currentFoodStock = Inventory.getInventory(worker.getFoodUsed(), Constants.STATE_NORMAL);
                    if (currentFoodStock.getQuantity() > 0) {
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
                workerCharacter.getName(),
                getRewardResourcesText(worker, resources, true));
    }

    public static String rewardResources(Context context, Hero hero) {
        Visitor_Type vType = Visitor_Type.findById(Visitor_Type.class, hero.getVisitorId());
        List<Hero_Resource> resources = getResourcesByAdventure(hero.getCurrentAdventure());
        return String.format(context.getString(R.string.workerReturned),
                vType.getName(),
                getRewardResourcesText(hero, resources, true));
    }

    public static String getRewardResourcesText(Hero hero, List<Hero_Resource> resources, boolean addItems) {
        LinkedHashMap<String, Integer> data = new LinkedHashMap<>();
        Item foodItem = Item.findById(Item.class, hero.getFoodItem());

        for (Hero_Resource resource : resources) {
            if (addItems) {
                Inventory resourceInventory = Inventory.getInventory((long) resource.getResourceID(), resource.getResourceState());
                resourceInventory.setQuantity(resourceInventory.getQuantity() + resource.getResourceQuantity());
                resourceInventory.save();
            }

            Item item = Item.findById(Item.class, resource.getResourceID());
            Integer temp;
            if(data.containsKey(item.getName())) {
                temp = data.get(item.getName()) + resource.getResourceQuantity();
                data.put(item.getName(), temp);
            } else {
                data.put(item.getName(), resource.getResourceQuantity());
            }
        }

        String bonusText = "";
        if (addItems && foodItem != null && VisitorHelper.getRandomBoolean(100 - foodItem.getValue())) {
            // If rewarding resources, and have luckily got a page
            List<Item> pages = Select.from(Item.class).where(Condition.prop("type").eq(Constants.TYPE_PAGE)).list();
            Item rewardedPage = VisitorHelper.pickRandomItemFromList(pages);
            Inventory.addItem(rewardedPage.getId(), Constants.STATE_NORMAL, 1);

            bonusText = String.format(", and a rare %s", rewardedPage.getName());
        } else if (!addItems && foodItem != null) {
            bonusText = ", and possibly a rare page";
        }

        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();

            result.append(String.format("%dx %s, ", value, key));
        }
        return result.substring(0, result.length() - 2) + bonusText;
    }

    public static String getRewardResourcesText(Worker worker, List<Worker_Resource> resources, boolean addItems) {
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

            if (addItems) {
                Inventory resourceInventory = Inventory.getInventory((long) resource.getResourceID(), resource.getResourceState());
                resourceInventory.setQuantity(resourceInventory.getQuantity() + resource.getResourceQuantity());
                resourceInventory.save();
            }

            Item item = Item.findById(Item.class, resource.getResourceID());
            Integer temp;
            if(data.containsKey(item.getName())) {
                temp = data.get(item.getName()) + resource.getResourceQuantity();
                data.put(item.getName(), temp);
            } else {
                data.put(item.getName(), resource.getResourceQuantity());
            }
        }

        String bonusText = "";
        if (addItems && foodItem != null && VisitorHelper.getRandomBoolean(100 - foodItem.getValue())) {
            // If rewarding resources, and have luckily got a page
            List<Item> pages = Select.from(Item.class).where(Condition.prop("type").eq(Constants.TYPE_PAGE)).list();
            Item rewardedPage = VisitorHelper.pickRandomItemFromList(pages);
            Inventory.addItem(rewardedPage.getId(), Constants.STATE_NORMAL, 1);

            bonusText = String.format(", and a rare %s", rewardedPage.getName());
        } else if (!addItems && foodItem != null) {
            // If checking resources
            if (foodItem.getId() == worker.getFavouriteFood() && worker.isFavouriteFoodDiscovered()) {
                bonusText = ", and very possibly a rare page";
            } else {
                bonusText = ", and possibly a rare page";
            }
        }

        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();

            result.append(String.format("%dx %s, ", value, key));
        }
        return result.substring(0, result.length() - 2) + bonusText;
    }

    public static String getTimesCompletedString(Context context, Worker worker) {
        Character character = Character.findById(Character.class, worker.getCharacterID());
        Item foodUsed = Item.findById(Item.class, worker.getFoodUsed());
        return String.format(context.getString(R.string.workerTimesCompleted),
                character.getName(),
                worker.getTimesCompleted(),
                foodUsed != null ? foodUsed.getName() : "nothing",
                foodUsed != null ? (foodUsed.getId() == worker.getFavouriteFood() && worker.isFavouriteFoodDiscovered() ? 2 : 1) * foodUsed.getValue() : 0);
    }

    public static String getTimesCompletedString(Context context, Hero hero) {
        Visitor_Type visitorType = Visitor_Type.findById(Visitor_Type.class, hero.getVisitorId());
        Visitor_Stats visitorStats = Visitor_Stats.findById(Visitor_Stats.class, hero.getVisitorId());
        return String.format(context.getString(R.string.heroTimesCompleted),
                visitorType.getName(),
                visitorStats.getAdventuresCompleted(),
                hero.getTotalItemBonusPercent());
    }

    public static String getTimeLeftString(Context context, Worker worker) {
        Character character = Character.findById(Character.class, worker.getCharacterID());
        String timeRemaining = DateHelper.getHoursMinsSecsRemaining(WorkerHelper.getTimeRemaining(worker));
        return String.format(context.getString(R.string.workerReturnTime),
                character.getName(),
                timeRemaining);
    }

    public static String getTimeLeftString(Context context, Hero hero) {
        Visitor_Type vType = Visitor_Type.findById(Visitor_Type.class, hero.getVisitorId());
        String timeRemaining = DateHelper.getHoursMinsSecsRemaining(WorkerHelper.getTimeRemaining(hero));
        return String.format(context.getString(R.string.workerReturnTime),
                vType.getName(),
                timeRemaining);
    }

    public static List<Inventory> getTools(String selection) {
        String whereClause = "1 > 2";
        if (selection.equals("Pickaxe (Ore)")) {
            whereClause = "type = 15";
        } else if (selection.equals("Hammer (Bar)")) {
            whereClause = "type = 18";
        } else if (selection.equals("Fishing Rod (Food)")) {
            whereClause = "type = 17";
        } else if (selection.equals("Hatchet (Wood)")) {
            whereClause = "type = 16";
        } else if (selection.equals("Gloves (Silk)")) {
            whereClause = "type = 14";
        } else if (selection.equals("Gem (Powder)")) {
            whereClause = "type = 20";
        } else if (selection.equals("Silver Ring (Silver + Gems)")) {
            whereClause = "type = 24 AND tier = 8";
        } else if (selection.equals("Gold Ring (Gold + Gems)")) {
            whereClause = "type = 24 AND tier = 9";
        } else if (selection.equals("Visage (Coins)")) {
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
        return "item IN (" + itemString.substring(0, itemString.length() - 1) + ") AND state = 1 AND quantity > 0 ORDER BY item ASC";
    }

    public static int getBasePrice(int itemId, int state) {
        Item item = Item.findById(Item.class, itemId);
        int baseValue = 4 + (item.getTier() * 4);

        if (state == Constants.STATE_NORMAL) {
            return baseValue;
        } else if (state == Constants.STATE_UNFINISHED) {
            return baseValue / 2;
        } else {
            return baseValue + (3 * state);
        }
    }

    public static int getTotalStrength(Hero hero) {
        int totalStrength = 0;
        totalStrength += Item.findById(Item.class, hero.getFoodItem()).getValue();
        totalStrength += getBasePrice(hero.getHelmetItem(), hero.getHelmetState());
        totalStrength += getBasePrice(hero.getArmourItem(), hero.getArmourState());
        totalStrength += getBasePrice(hero.getWeaponItem(), hero.getWeaponState());
        totalStrength += getBasePrice(hero.getShieldItem(), hero.getShieldState());
        totalStrength += getBasePrice(hero.getGlovesItem(), hero.getGlovesState());
        totalStrength += getBasePrice(hero.getBootsItem(), hero.getBootsState());
        totalStrength += getBasePrice(hero.getRingItem(), hero.getRingState());

        return totalStrength;
    }
}
