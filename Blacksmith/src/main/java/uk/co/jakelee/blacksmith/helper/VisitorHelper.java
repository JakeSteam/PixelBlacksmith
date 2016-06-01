package uk.co.jakelee.blacksmith.helper;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.model.Criteria;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.State;
import uk.co.jakelee.blacksmith.model.Tier;
import uk.co.jakelee.blacksmith.model.Type;
import uk.co.jakelee.blacksmith.model.Upgrade;
import uk.co.jakelee.blacksmith.model.Visitor;
import uk.co.jakelee.blacksmith.model.Visitor_Demand;
import uk.co.jakelee.blacksmith.model.Visitor_Stats;
import uk.co.jakelee.blacksmith.model.Visitor_Type;

public class VisitorHelper {
    private static List<Pair<Long, Long>> existingCriteria = new ArrayList<>();

    public static boolean tryCreateVisitor() {
        if (Visitor.count(Visitor.class) < Upgrade.getValue("Maximum Visitors")) {
            createNewVisitor();

            Player_Info lastVisitorSpawn = Select.from(Player_Info.class).where(
                    Condition.prop("name").eq("DateVisitorSpawned")).first();
            lastVisitorSpawn.setLongValue(System.currentTimeMillis());
            lastVisitorSpawn.save();
            return true;
        } else {
            return false;
        }
    }

    public static int tryCreateRequiredVisitors() {
        int numberOfPossibleVisitors = VisitorHelper.getNumberNewVisitors(System.currentTimeMillis());
        int numberOfActualVisitors = 0;
        for (int i = 0; i < numberOfPossibleVisitors; i++) {
            if (VisitorHelper.tryCreateVisitor()) {
                numberOfActualVisitors++;
            }
        }

        return numberOfActualVisitors;
    }

    private static int getNumberNewVisitors(long currentTime) {
        long lastSpawn = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("DateVisitorSpawned")).first().getLongValue();
        int currentVisitors = Visitor.listAll(Visitor.class).size();
        int maxVisitors = Upgrade.getValue("Maximum Visitors");
        int maximumNewVisitors = maxVisitors - currentVisitors;

        long timeDifference = currentTime - lastSpawn;
        long possibleNewVisitors = timeDifference / DateHelper.minutesToMilliseconds(Upgrade.getValue("Visitor Spawn Time"));

        // Make sure we don't return more than possible
        if (possibleNewVisitors >= maximumNewVisitors) {
            return maximumNewVisitors;
        } else {
            return (int) possibleNewVisitors;
        }
    }

    private static void createNewVisitor() {
        existingCriteria = new ArrayList<>();
        Long currentMillis = System.currentTimeMillis();

        // Select the visitor type to be used
        Visitor_Type visitorType = selectVisitorType();

        // Update the selected visitor type's statistics
        Visitor_Stats visitorStats = Visitor_Stats.findById(Visitor_Stats.class, visitorType.getVisitorID());
        try {
            if (visitorStats.getFirstSeen() == 0) {
                visitorStats.setFirstSeen(currentMillis);
            }
            visitorStats.setVisits(visitorStats.getVisits() + 1);
            visitorStats.save();

            // Make a visitor of the selected type
            Visitor visitor = new Visitor(currentMillis, visitorType.getVisitorID());
            visitor.save();

            // Work out how many demands need to be generated
            int numDemands = getRandomNumber(Constants.MINIMUM_DEMANDS, Constants.MAXIMUM_DEMANDS);

            // Generate the demands
            for (int i = 1; i <= numDemands; i++) {
                generateDemand(i, visitor.getId());
            }
        } catch (Exception e) {
            Log.d("Blacksmith", "Failure loading new visitor.");
        }
    }

    private static void generateDemand(int i, Long visitorID) {
        Long criteriaType = (long) getRandomNumber(Constants.MINIMUM_CRITERIA, Constants.MAXIMUM_CRITERIA);
        Criteria criteria = Criteria.findById(Criteria.class, criteriaType);

        Long criteriaValue = 1L;
        switch (criteria.getName()) {
            case "State":
                criteriaValue = selectDemandState().getId();
                break;
            case "Tier":
                criteriaValue = selectDemandTier().getId();
                break;
            case "Type":
                criteriaValue = selectDemandType().getId();
                break;
        }

        int maxQuantity = (criteria.getName().equals("State") ? Constants.MAXIMUM_QUANTITY_STATE : Constants.MAXIMUM_QUANTITY);
        int quantity = getRandomNumber(Constants.MINIMUM_QUANTITY, maxQuantity);
        boolean required = (i == 1 || getRandomBoolean(Constants.DEMAND_REQUIRED_PERCENTAGE)); // 70% chance of demands optional

        // Check if the current criteria already exists. If it does, try again.
        Pair<Long, Long> currentCriteria = new Pair<>(criteriaType, criteriaValue);
        if (existingCriteria.contains(currentCriteria)) {
            generateDemand(i, visitorID);
        } else {
            Visitor_Demand visitor_demand = new Visitor_Demand(visitorID, criteriaType, criteriaValue, 0, quantity, required);
            visitor_demand.save();
            existingCriteria.add(currentCriteria);
        }
    }

    public static int getRandomNumber(int minimum, int maximum) {
        Random random = new Random();
        return random.nextInt((maximum - minimum) + 1) + minimum;
    }

    public static void removeVisitor(Visitor visitor) {
        Visitor_Demand.deleteAll(Visitor_Demand.class, "visitor_id = " + visitor.getId());
        Visitor.delete(visitor);
    }

    public static int pickRandomNumberFromArray(int[] possibleNumbers) {
        int randomIndex = new Random().nextInt(possibleNumbers.length);
        return possibleNumbers[randomIndex];
    }

    public static Item pickRandomItemFromList(List<Item> list) {
        int randomIndex = new Random().nextInt(list.size());
        return list.get(randomIndex);
    }

    public static boolean getRandomBoolean(int falsePercentage) {
        return getRandomNumber(0, 100) > falsePercentage;
    }

    private static Visitor_Type selectVisitorType() {
        Visitor_Type visitor = new Visitor_Type();

        List<Visitor_Type> visitorTypes = Visitor_Type.findWithQuery(Visitor_Type.class,
                "SELECT * FROM VisitorType WHERE visitor_id NOT IN (SELECT type FROM Visitor)");

        // Work out the total weighting.
        double totalWeighting = 0.0;
        for (Visitor_Type type : visitorTypes) {
            totalWeighting += type.getWeighting();
        }

        // Generate a number between 0 and total probability.
        double randomNumber = Math.random() * totalWeighting;

        // Use the random number to select a visitor type.
        double probabilityIterator = 0.0;
        for (Visitor_Type type : visitorTypes) {
            probabilityIterator += type.getWeighting();
            if (probabilityIterator >= randomNumber) {
                visitor = type;
                break;
            }
        }
        return visitor;
    }

    private static State selectDemandState() {
        State selectedState = new State();

        // Work out the total probability for the visitors.
        List<State> states = Select.from(State.class).where(
                Condition.prop("minimum_level").lt(Player_Info.getPlayerLevel() + 1)).list();
        double totalWeighting = 0.0;
        for (State state : states) {
            totalWeighting += state.getWeighting();
        }

        // Generate a number between 0 and total probability.
        double randomNumber = Math.random() * totalWeighting;

        // Use the random number to select a visitor type.
        double probabilityIterator = 0.0;
        for (State state : states) {
            probabilityIterator += state.getWeighting();
            if (probabilityIterator >= randomNumber) {
                selectedState = state;
                break;
            }
        }
        return selectedState;
    }

    private static Type selectDemandType() {
        Type selectedType = new Type();

        // Work out the total probability for the visitors.
        List<Type> types = Select.from(Type.class).where(
                Condition.prop("minimum_level").lt(Player_Info.getPlayerLevel() + 2)).list();
        double totalWeighting = 0.0;
        for (Type type : types) {
            totalWeighting += type.getWeighting();
        }

        // Generate a number between 0 and total probability.
        double randomNumber = Math.random() * totalWeighting;

        // Use the random number to select a visitor type.
        double probabilityIterator = 0.0;
        for (Type type : types) {
            probabilityIterator += type.getWeighting();
            if (probabilityIterator >= randomNumber) {
                selectedType = type;
                break;
            }
        }
        return selectedType;
    }

    private static Tier selectDemandTier() {
        Tier selectedTier = new Tier();

        // Work out the total probability for the visitors.
        List<Tier> tiers = Select.from(Tier.class).where(
                Condition.prop("minimum_level").lt(Player_Info.getPlayerLevel() + 2)).list(); // + 2 so next level unlocks are shown
        double totalWeighting = 0.0;
        for (Tier tier : tiers) {
            totalWeighting += tier.getWeighting();
        }

        // Generate a number between 0 and total probability.
        double randomNumber = Math.random() * totalWeighting;

        // Use the random number to select a visitor type.
        double probabilityIterator = 0.0;
        for (Tier tier : tiers) {
            probabilityIterator += tier.getWeighting();
            if (probabilityIterator >= randomNumber) {
                selectedTier = tier;
                break;
            }
        }
        return selectedTier;
    }

    public static String multiplierToPercent(double multiplier) {
        double difference = multiplier - 1.00;
        double percent = difference * 100;
        int roundedPercent = (int) Math.ceil(percent);

        if (roundedPercent >= 0) {
            return "+" + String.valueOf(roundedPercent) + "%";
        } else {
            return "-" + String.valueOf(roundedPercent) + "%";
        }
    }

    public static double percentToMultiplier(int percent) {
        double percentMultiplier = percent + 100;
        return percentMultiplier / 100;
    }

    public static int getVisitorAddCost() {
        int playerLevel = Player_Info.getPlayerLevel();
        return playerLevel * 100;
    }

    public static int getVisitorDismissCost(long visitorID) {
        Visitor visitor = Visitor.findById(Visitor.class, visitorID);
        int unfulfilledDemands = Visitor_Demand.find(Visitor_Demand.class, "quantity_provided < quantity AND visitor_id = " + visitorID).size();

        int playerLevel = Player_Info.getPlayerLevel();
        int maxCost = playerLevel * 10 * unfulfilledDemands;

        int minutesSinceArrival = DateHelper.getMinutesInMilliseconds(System.currentTimeMillis() - visitor.getArrivalTime());
        return getAdjustedDismissCost(maxCost, minutesSinceArrival);
    }

    public static int getAdjustedDismissCost(int maxCost, int minutes) {
        int MAX_DISCOUNT = 90;
        // E.g. 1000 coins.
        // 6 mins = 1% = 990
        // 18 mins = 3% = 970
        // 60 mins = 10% = 900
        // 300 mins = 50% = 500
        // 600 mins = 90% = 100 (100% capped)
        int reductionPercent = (minutes > MAX_DISCOUNT * 6 ? MAX_DISCOUNT : minutes / 6);
        int discount = (maxCost * reductionPercent) / 100;
        return maxCost - discount;
    }

    public static void displayPreference(Context context, View view, int string, String preferred) {
        String multiplier = (String) view.getTag(R.id.multiplier);

        ToastHelper.showToast(context, Toast.LENGTH_SHORT, String.format(context.getString(string),
                multiplier,
                preferred), false);
    }

    public static long getTimeUntilSpawn() {
        long unixSpawned = Select.from(Player_Info.class).where(Condition.prop("name").eq("DateVisitorSpawned")).first().getLongValue();
        long unixNextSpawn = unixSpawned + DateHelper.minutesToMilliseconds(Upgrade.getValue("Visitor Spawn Time"));
        long timeLeft = unixNextSpawn - System.currentTimeMillis();
        return (timeLeft > 0 ? timeLeft : 0);
    }

    public static String getRewardString(Context context, boolean rewardLegendary, boolean isFullyComplete) {
        List<String> strings = new ArrayList<>();
        if (rewardLegendary && isFullyComplete) {
            strings.add(context.getString(R.string.visitorLeavesCompletePremium1));
            strings.add(context.getString(R.string.visitorLeavesCompletePremium2));
            strings.add(context.getString(R.string.visitorLeavesCompletePremium3));
        } else if (rewardLegendary && !isFullyComplete) {
            strings.add(context.getString(R.string.visitorLeavesLegendary1));
            strings.add(context.getString(R.string.visitorLeavesLegendary2));
            strings.add(context.getString(R.string.visitorLeavesLegendary3));
        }else if (!rewardLegendary && isFullyComplete) {
            strings.add(context.getString(R.string.visitorLeavesComplete1));
            strings.add(context.getString(R.string.visitorLeavesComplete2));
            strings.add(context.getString(R.string.visitorLeavesComplete3));
        }else if (!rewardLegendary && !isFullyComplete) {
            strings.add(context.getString(R.string.visitorLeaves1));
            strings.add(context.getString(R.string.visitorLeaves2));
            strings.add(context.getString(R.string.visitorLeaves3));
        }
        int position = VisitorHelper.getRandomNumber(0, strings.size() - 1);
        return strings.get(position);
    }

    public static void rewardXp(boolean isFullyComplete) {
        Player_Info.addXp(Player_Info.getPlayerLevel() * (isFullyComplete ? Constants.QUEST_XP_MODIFIER_MEDIUM : Constants.QUEST_XP_MODIFIER_EASY));
    }

    public static void createVisitorReward(Context context, boolean isFullyComplete) {
        int minimumRewards = Upgrade.getValue("Minimum Visitor Rewards");
        int maximumRewards = Upgrade.getValue("Maximum Visitor Rewards");
        if (minimumRewards == 0 || maximumRewards == 0) {
            minimumRewards = 1;
            maximumRewards = 5;
        }

        int numRewards = (isFullyComplete ? 2 : 1) * VisitorHelper.getRandomNumber(minimumRewards, maximumRewards);
        boolean rewardLegendary = Player_Info.isPremium() && VisitorHelper.getRandomBoolean(100 - Upgrade.getValue("Legendary Chance"));
        int typeID = VisitorHelper.pickRandomNumberFromArray(Constants.VISITOR_REWARD_TYPES);

        // Get normal reward
        List<Item> matchingItems = Select.from(Item.class).where(Condition.prop("type").eq(typeID)).list();
        Item selectedItem = VisitorHelper.pickRandomItemFromList(matchingItems);
        Inventory.addItem(selectedItem.getId(), Constants.STATE_NORMAL, numRewards, false);
        String rewardString = VisitorHelper.getRewardString(context, rewardLegendary, isFullyComplete);

        // Get legendary reward
        if (rewardLegendary) {
            List<Item> premiumItems = Select.from(Item.class).where(Condition.prop("tier").eq(Constants.TIER_PREMIUM)).list();
            Item premiumItem = VisitorHelper.pickRandomItemFromList(premiumItems);
            Inventory.addItem(premiumItem.getId(), Constants.STATE_UNFINISHED, 1, false);
            ToastHelper.showToast(context, Toast.LENGTH_LONG, String.format(rewardString,
                    numRewards,
                    selectedItem.getName(),
                    premiumItem.getFullName(Constants.STATE_UNFINISHED)), true);
        } else {
            ToastHelper.showToast(context, Toast.LENGTH_LONG, String.format(rewardString,
                    numRewards,
                    selectedItem.getFullName(Constants.STATE_NORMAL)), true);
        }
    }

    public static List<Pair<Item, Integer>> createVisitorTrophyReward(Visitor visitor) {
        List<Pair<Item, Integer>> rewards = new ArrayList<>();

        Pair<Item, Integer> itemReward = createVisitorItemReward(visitor);
        Pair<Item, Integer> pageReward = createVisitorPageReward();
        rewards.add(itemReward);
        rewards.add(pageReward);

        Inventory.addItem(itemReward.first.getId(), itemReward.second, Constants.TROPHY_ITEM_REWARDS);
        Inventory.addItem(pageReward.first.getId(), pageReward.second, Constants.TROPHY_PAGE_REWARDS);

        return rewards;
    }

    private static Pair<Item, Integer> createVisitorItemReward(Visitor visitor) {
        Visitor_Type visitorType = Select.from(Visitor_Type.class).where(
                Condition.prop("visitor_id").eq(visitor.getType())).first();

        Long preferredState = visitorType.getStatePreferred();
        Long preferredTier = visitorType.getTierPreferred();
        Long preferredType = visitorType.getTypePreferred();

        Item preferredItem = Select.from(Item.class).where(
                Condition.prop("tier").eq(preferredTier),
                Condition.prop("type").eq(preferredType)).orderBy("value DESC").first();

        if (preferredItem == null) {
            preferredItem = Select.from(Item.class).where(
                    Condition.prop("type").eq(preferredType)).orderBy("value DESC").first();
        }

        return new Pair<>(preferredItem, (int) (long) preferredState);
    }

    private static Pair<Item, Integer> createVisitorPageReward() {
        List<Item> pages = Select.from(Item.class).where(Condition.prop("type").eq(Constants.TYPE_PAGE)).list();
        Item rewardedPage = VisitorHelper.pickRandomItemFromList(pages);

        return new Pair<>(rewardedPage, Constants.STATE_NORMAL);
    }

    public static String getDiscoveredPreferencesText(Context context, Visitor_Type vType) {
        String type = context.getString(R.string.unknownText);
        String tier = context.getString(R.string.unknownText);
        String state = context.getString(R.string.unknownText);

        if (vType.isTypeDiscovered()) {
            type = Type.findById(Type.class, vType.getTypePreferred()).getName();
        }
        if (vType.isTierDiscovered()) {
            tier = Tier.findById(Tier.class, vType.getTierPreferred()).getName();
        }
        if (vType.isStateDiscovered()) {
            state = State.findById(State.class, vType.getStatePreferred()).getName();
        }

        return String.format(context.getString(R.string.trophyPreferences), type, tier, state);
    }

}
