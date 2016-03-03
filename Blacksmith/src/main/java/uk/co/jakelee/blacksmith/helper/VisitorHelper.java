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
import uk.co.jakelee.blacksmith.model.Trader;
import uk.co.jakelee.blacksmith.model.Trader_Type;
import uk.co.jakelee.blacksmith.model.Type;
import uk.co.jakelee.blacksmith.model.Visitor;
import uk.co.jakelee.blacksmith.model.Visitor_Demand;
import uk.co.jakelee.blacksmith.model.Visitor_Stats;
import uk.co.jakelee.blacksmith.model.Visitor_Type;

public class VisitorHelper {
    public static List<Pair<Long, Long>> existingCriteria = new ArrayList<>();

    public static boolean tryCreateVisitor() {
        if (Visitor.count(Visitor.class) < Constants.MAXIMUM_VISITORS) {
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

    public static int getNumberNewVisitors(long currentTime) {
        long lastSpawn = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("DateVisitorSpawned")).first().getLongValue();
        int currentVisitors = Visitor.listAll(Visitor.class).size();
        int maxVisitors = Constants.MAXIMUM_VISITORS;
        int maximumNewVisitors = maxVisitors - currentVisitors;

        long timeDifference = currentTime - lastSpawn;
        long possibleNewVisitors = timeDifference / Constants.MILLISECONDS_BETWEEN_VISITOR_SPAWNS;

        // Make sure we don't return more than possible
        if (possibleNewVisitors >= maximumNewVisitors) {
            return maximumNewVisitors;
        } else {
            return (int) possibleNewVisitors;
        }
    }

    public static void createNewVisitor() {
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

    public static void generateDemand(int i, Long visitorID) {
        Long criteriaType = Long.valueOf(getRandomNumber(Constants.MINIMUM_CRITERIA, Constants.MAXIMUM_CRITERIA));
        Criteria criteria = Criteria.findById(Criteria.class, criteriaType);

        Long criteriaValue = 1L;
        if (criteria.getName().equals("State")) {
            criteriaValue = selectDemandState().getId();
        } else if (criteria.getName().equals("Tier")) {
            criteriaValue = selectDemandTier().getId();
        } else if (criteria.getName().equals("Type")) {
            criteriaValue = selectDemandType().getId();
        }

        int quantity = getRandomNumber(Constants.MINIMUM_QUANTITY, Constants.MAXIMUM_QUANTITY);

        boolean required = getRandomBoolean(Constants.DEMAND_REQUIRED_PERCENTAGE);
        if (i == 1) {
            required = true;
        }

        // Check if the current criteria already exists. If it does, try again.
        Pair currentCriteria = new Pair(criteriaType, criteriaValue);
        if (existingCriteria.contains(currentCriteria)) {
            generateDemand(i, visitorID);
        } else {
            Visitor_Demand demand = new Visitor_Demand(visitorID, criteriaType, criteriaValue, 0, quantity, required);
            demand.save();
            existingCriteria.add(currentCriteria);
        }
    }

    public static int getRandomNumber(int minimum, int maximum) {
        Random random = new Random();
        int number = random.nextInt((maximum - minimum) + 1) + minimum;
        return number;
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

    public static Visitor_Type selectVisitorType () {
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

    public static State selectDemandState () {
        State selectedState = new State();

        // Work out the total probability for the visitors.
        List<State> states = State.listAll(State.class);
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

    public static Type selectDemandType () {
        Type selectedType = new Type();

        // Work out the total probability for the visitors.
        List<Type> types = Type.listAll(Type.class);
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

    public static Tier selectDemandTier () {
        Tier selectedTier = new Tier();

        // Work out the total probability for the visitors.
        List<Tier> tiers = Tier.listAll(Tier.class);
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
        int roundedPercent = (int) percent;

        if (percent >= 0) {
            return "+" + String.valueOf(roundedPercent) + "%";
        } else {
            return "-" + String.valueOf(roundedPercent) + "%";
        }
    }

    public static int getVisitorAddCost() {
        int playerLevel = Player_Info.getPlayerLevel();
        return playerLevel * 100;
    }

    public static int getVisitorDismissCost(long visitorID) {
        int unfulfilledDemands = Visitor_Demand.find(Visitor_Demand.class, "quantity_provided >= quantity AND visitor_id = " + visitorID).size();

        int playerLevel = Player_Info.getPlayerLevel();
        return playerLevel * 10 * unfulfilledDemands;
    }

    public static void createNewTrader() {
        long arrivalTime = System.currentTimeMillis();
        long departureTime = DateHelper.minutesToMilliseconds(getRandomNumber(Constants.MINIMUM_VISITOR_MINUTES, Constants.MAXIMUM_VISITOR_MINUTES));
        Trader_Type traderType = selectTraderType();

        Trader newTrader = new Trader(arrivalTime, departureTime, traderType.getId());
        newTrader.save();
    }

    public static Trader_Type selectTraderType () {
        Trader_Type selectedTraderType = new Trader_Type();

        List<Trader_Type> traderTypes = Trader_Type.findWithQuery(Trader_Type.class,
                "SELECT * FROM TraderType WHERE id NOT IN (SELECT visitor_type FROM Trader)");

        double totalWeighting = 0.0;
        for (Trader_Type traderType : traderTypes) {
            totalWeighting += traderType.getWeighting();
        }

        double randomNumber = Math.random() * totalWeighting;
        double probabilityIterator = 0.0;
        for (Trader_Type traderType :  traderTypes) {
            probabilityIterator += traderType.getWeighting();
            if (probabilityIterator >= randomNumber) {
                selectedTraderType = traderType;
                break;
            }
        }
        return selectedTraderType;
    }
}
