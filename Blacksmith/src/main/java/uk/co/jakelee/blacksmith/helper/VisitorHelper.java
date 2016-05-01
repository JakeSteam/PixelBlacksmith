package uk.co.jakelee.blacksmith.helper;

import android.util.Pair;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.co.jakelee.blacksmith.model.Criteria;
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
            new Thread(new Runnable() {
                public void run() {
                    createNewVisitor();

                    Player_Info lastVisitorSpawn = Select.from(Player_Info.class).where(
                            Condition.prop("name").eq("DateVisitorSpawned")).first();
                    lastVisitorSpawn.setLongValue(System.currentTimeMillis());
                    lastVisitorSpawn.save();
                }
            }).start();
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

        int quantity = getRandomNumber(Constants.MINIMUM_QUANTITY, Constants.MAXIMUM_QUANTITY);
        boolean required = (i == 1 || getRandomBoolean(Constants.DEMAND_REQUIRED_PERCENTAGE));

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

    public static boolean getRandomBoolean(int truePercentage) {
        return getRandomNumber(0, 100) > truePercentage;
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
        int unfulfilledDemands = Visitor_Demand.find(Visitor_Demand.class, "quantity_provided <= quantity AND visitor_id = " + visitorID).size();

        int playerLevel = Player_Info.getPlayerLevel();
        return playerLevel * 10 * unfulfilledDemands;
    }
}
