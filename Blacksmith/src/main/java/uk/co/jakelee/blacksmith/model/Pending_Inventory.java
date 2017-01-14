package uk.co.jakelee.blacksmith.model;

import android.text.format.DateUtils;
import android.util.Pair;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.main.AnvilActivity;
import uk.co.jakelee.blacksmith.main.FurnaceActivity;
import uk.co.jakelee.blacksmith.main.InventoryActivity;
import uk.co.jakelee.blacksmith.main.TableActivity;

public class Pending_Inventory extends SugarRecord {
    private Long item;
    private long state;
    private long timeCreated;
    private int quantity;
    private int craftTime;
    private Long locationID;

    public Pending_Inventory() {
    }

    public Pending_Inventory(Long item, long state, long timeCreated, int quantity, int craftTime, Long locationID) {
        this.item = item;
        this.state = state;
        this.timeCreated = timeCreated;
        this.quantity = quantity;
        this.craftTime = craftTime;
        this.locationID = locationID;
    }

    public static List<Pending_Inventory> getPendingItems(Long locationID, boolean includeFuture) {
        long maxTime = System.currentTimeMillis() + (includeFuture ? DateUtils.YEAR_IN_MILLIS : 0);

        return Select.from(Pending_Inventory.class).where(
                Condition.prop("location_id").eq(locationID),
                Condition.prop("time_created").lt(maxTime))
                .orderBy("time_created ASC").list();
    }

    public static void addScheduledItems(final FurnaceActivity activity, final long location, final List<Pair<Long, Integer>> items) {
        new Thread(new Runnable() {
            public void run() {
                processScheduledItems(items, location);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.calculatingComplete();
                    }
                });
            }
        }).start();
    }

    public static void addScheduledItems(final AnvilActivity activity, final long location, final List<Pair<Long, Integer>> items) {
        new Thread(new Runnable() {
            public void run() {
                processScheduledItems(items, location);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.calculatingComplete();
                    }
                });
            }
        }).start();
    }

    public static void addScheduledItems(final TableActivity activity, final long location, final List<Pair<Long, Integer>> items) {
        new Thread(new Runnable() {
            public void run() {
                processScheduledItems(items, location);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.calculatingComplete();
                    }
                });
            }
        }).start();
    }

    public static void addScheduledItems(final InventoryActivity activity, final List<Integer> values) {
        new Thread(new Runnable() {
            public void run() {
                processSellingItems(values);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.calculatingComplete();
                    }
                });
            }
        }).start();
    }

    private static void processSellingItems(final List<Integer> values) {
        for (Integer value : values) {
            Pending_Inventory.addScheduledItem(Constants.ITEM_COINS, Constants.STATE_NORMAL, value, Constants.LOCATION_SELLING);
        }
    }


    private static void processScheduledItems(final List<Pair<Long, Integer>> items, final long location) {
        if (true) {
            Pending_Inventory.addScheduledItem(items.get(0).first, items.get(0).second, items.size(), location);
        } else {
            for (Pair item : items) {
                Pending_Inventory.addScheduledItem((long) item.first, (int) item.second, 1, location);
            }
        }
    }

    public static void addScheduledItems(final long location, final List<Pair<Long, Integer>> items) {
        new Thread(new Runnable() {
            public void run() {
                for (Pair item : items) {
                    Pending_Inventory.addScheduledItem((long) item.first, (int) item.second, 1, location);
                }
            }
        }).start();
    }

    public static void addItem(Long itemID, long state, int quantity, Long location) {
        long time = System.currentTimeMillis();
        addPendingInventory(itemID, state, quantity, location, time);
    }

    public static void addScheduledItem(Long itemID, long state, int quantity, Long location) {
        long timeSlotAvailable = getTimeSlotAvailable(location);
        addPendingInventory(itemID, state, quantity, location, timeSlotAvailable);
    }

    private static void addPendingInventory(Long itemID, long state, int quantity, Long location, long startTime) {
        Item item = Item.findById(Item.class, itemID);
        int craftTimeMultiplier = Upgrade.getValue("Craft Time");
        int craftTime = item.getModifiedValue(state) * quantity * craftTimeMultiplier;

        Pending_Inventory newItem = new Pending_Inventory(itemID, state, startTime, quantity, craftTime, location);
        newItem.save();
    }

    private static long getTimeSlotAvailable(Long location) {
        List<Pending_Inventory> pendingItems = getPendingItems(location, true);
        int numSlots = Slot.getUnlockedSlots(location);

        // Add all of the times a slot will become available to a list
        List<Long> finishTimes = new ArrayList<>();
        for (Pending_Inventory pending_inventory : pendingItems) {
            long finishTime = pending_inventory.getTimeCreated() + pending_inventory.getCraftTime();
            finishTimes.add(finishTime);
        }

        // Sort these times so the latest time is first
        Collections.sort(finishTimes, Collections.<Long>reverseOrder());

        if (finishTimes.size() >= numSlots) {
            // If we're all full up, get the first time a slot will become available
            return finishTimes.get(numSlots-1);
        } else {
            // Otherwise, it can go in now
            return System.currentTimeMillis();
        }
    }

    public static String getPendingItemsText(long locationID) {
        List<Pending_Inventory> pendingItems = Select.from(Pending_Inventory.class).where(
                Condition.prop("location_id").eq(locationID)).list();
        HashMap<String, Integer> data = new HashMap<>();

        for (Pending_Inventory pendingItem : pendingItems) {
            Item item = Item.findById(Item.class, pendingItem.getItem());
            Integer temp;
            if(data.containsKey(item.getName())) {
                temp = data.get(item.getName())+pendingItem.getQuantity();
                data.put(item.getName(),temp);
            }
            else {
                data.put(item.getName(),pendingItem.getQuantity());
            }
        }

        if (data.size() == 0) {
            return "No pending items.";
        } else {
            StringBuilder result = new StringBuilder();
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();

                result.append(String.format("%dx %s, ", value, key));
            }
            return String.format("Pending items: %s.", result.substring(0, result.length() - 2));
        }
    }

    public Long getItem() {
        return item;
    }

    public void setItem(Long item) {
        this.item = item;
    }

    public long getState() {
        return state;
    }

    public void setState(long state) {
        this.state = state;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCraftTime() {
        return craftTime;
    }

    public void setCraftTime(int craftTime) {
        this.craftTime = craftTime;
    }

    public Long getLocationID() {
        return locationID;
    }

    public void setLocationID(Long locationID) {
        this.locationID = locationID;
    }
}
