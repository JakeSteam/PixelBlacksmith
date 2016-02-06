package uk.co.jakelee.blacksmith.helper;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.co.jakelee.blacksmith.model.Criteria;
import uk.co.jakelee.blacksmith.model.State;
import uk.co.jakelee.blacksmith.model.Tier;
import uk.co.jakelee.blacksmith.model.Type;
import uk.co.jakelee.blacksmith.model.Visitor;
import uk.co.jakelee.blacksmith.model.Visitor_Demand;
import uk.co.jakelee.blacksmith.model.Visitor_Stats;
import uk.co.jakelee.blacksmith.model.Visitor_Type;

public class VisitorHelper {
    public static List<Pair<Long, Long>> existingCriteria = new ArrayList<>();

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

    public static boolean getRandomBoolean(int truePercentage) {
        return getRandomNumber(0, 100) > truePercentage;
    }

    public static Visitor_Type selectVisitorType () {
        Visitor_Type visitor = new Visitor_Type();

        // Work out the total probability for the visitors.
        List<Visitor_Type> types = Visitor_Type.listAll(Visitor_Type.class);
        double totalWeighting = 0.0;
        for (Visitor_Type type : types) {
            totalWeighting += type.getWeighting();
        }

        // Generate a number between 0 and total probability.
        double randomNumber = Math.random() * totalWeighting;

        // Use the random number to select a visitor type.
        double probabilityIterator = 0.0;
        for (Visitor_Type type : types) {
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
}
