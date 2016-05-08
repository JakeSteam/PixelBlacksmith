package uk.co.jakelee.blacksmith.helper;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.model.Character;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Upgrade;
import uk.co.jakelee.blacksmith.model.Worker;
import uk.co.jakelee.blacksmith.model.Worker_Resource;

public class WorkerHelper {
    public final static String INTENT_ID = "uk.co.jakelee.blacksmith.workerID";

    public static List<Worker_Resource> getResourcesByTool(long toolID) {
        return getResourcesByTool((int) toolID);
    }
    public static List<Worker_Resource> getResourcesByTool(int toolID) {
        return Select.from(Worker_Resource.class).where(
                Condition.prop("tool_id").eq(toolID)).list();
    }

    public static boolean isValidTool(long toolID) {
        return isValidTool((int) toolID);
    }

    public static boolean isValidTool(int toolID) {
        return Select.from(Worker_Resource.class).where(
                Condition.prop("tool_id").eq(toolID)).count() > 0;
    }

    public static void populateResources(DisplayHelper dh, LinearLayout container, long toolID) {
        List<Worker_Resource> resources = getResourcesByTool(toolID);
        for (Worker_Resource resource : resources) {
            container.addView(dh.createImageView("item", String.valueOf(resource.getResourceID()), 22, 22));
        }
    }

    public static boolean isReady(Worker worker) {
        return worker.getTimeStarted() == 0;
    }

    public static String getTimeRemainingString(Worker worker) {
        return DateHelper.getHoursMinsRemaining(getTimeRemaining(worker));
    }

    public static long getTimeRemaining(Worker worker) {
        long timeStarted = worker.getTimeStarted();
        int minutesForCompletion = Upgrade.getValue("Worker Time");
        long timeForCompletion = DateHelper.minutesToMilliseconds(minutesForCompletion);
        return (timeStarted + timeForCompletion) - System.currentTimeMillis();

    }

    public static int getBuyCost(Worker worker) {
        return worker.getLevelUnlocked() * Constants.WORKER_COST_MULTIPLIER;
    }

    public static String getButtonText(Worker worker) {
        if (isReady(worker)) {
            return "Start gathering";
        } else {
            return "Returns in " + WorkerHelper.getTimeRemainingString(worker);
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

    public static void checkForFinishedWorkers(Context context) {
        List<Worker> workers = Select.from(Worker.class).where(
                Condition.prop("purchased").eq(1),
                Condition.prop("time_started").notEq(0)).list();

        for (Worker worker : workers) {
            if (getTimeRemaining(worker) <= 0) {
                worker.setTimeStarted(0);
                worker.setTimesCompleted(worker.getTimesCompleted() + 1);
                worker.save();

                rewardResources(context, worker);
            }
        }
    }

    public static void rewardResources(Context context, Worker worker) {
        List<Worker_Resource> resources = getResourcesByTool(worker.getToolUsed());
        ToastHelper.showPositiveToast(context, Toast.LENGTH_LONG, String.format(context.getString(R.string.workerReturned),
                getRewardResourcesText(resources)), true);
    }

    public static String getRewardResourcesText(List<Worker_Resource> resources) {
        HashMap<String, Integer> data = new HashMap<>();

        for (Worker_Resource resource : resources) {
            Inventory resourceInventory = Inventory.getInventory((long) resource.getResourceID(), resource.getResourceState());
            resourceInventory.setQuantity(resourceInventory.getQuantity() + resource.getResourceQuantity());
            resourceInventory.save();

            Item item = Item.findById(Item.class, resource.getResourceID());
            Integer temp;
            if(data.containsKey(item.getName())) {
                temp = data.get(item.getName()) + resource.getResourceQuantity();
                data.put(item.getName(), temp);
            }
            else {
                data.put(item.getName(), resource.getResourceQuantity());
            }
        }

        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();

            result.append(String.format("%dx %s, ", value, key));
        }
        return result.substring(0, result.length() - 2);
    }

    public static String getTimesCompletedString(Context context, Worker worker) {
        Character character = Character.findById(Character.class, worker.getCharacterID());
        return String.format(context.getString(R.string.workerTimesCompleted),
                character.getName(),
                worker.getTimesCompleted());
    }

    public static String getTimeLeftString(Context context, Worker worker) {
        Character character = Character.findById(Character.class, worker.getCharacterID());
        String timeRemaining = DateHelper.getHoursMinsSecsRemaining(WorkerHelper.getTimeRemaining(worker));
        return String.format(context.getString(R.string.workerReturnTime),
                character.getName(),
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
        return "item IN (" + itemString.substring(0, itemString.length() - 1) + ") AND state = 1";
    }
}