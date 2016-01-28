package uk.co.jakelee.blacksmith.helper;

import java.util.List;
import java.util.Random;

import uk.co.jakelee.blacksmith.model.Criteria;
import uk.co.jakelee.blacksmith.model.Visitor;
import uk.co.jakelee.blacksmith.model.Visitor_Demand;
import uk.co.jakelee.blacksmith.model.Visitor_Stats;
import uk.co.jakelee.blacksmith.model.Visitor_Type;

public class VisitorHelper {

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

        int minimumCriteria = 1;
        int maximumCriteria = 1;
        if (criteria.getName().equals("State")) {
            minimumCriteria = Constants.STATE_MIN;
            maximumCriteria = Constants.STATE_MAX;
        } else if (criteria.getName().equals("Tier")) {
            minimumCriteria = Constants.TIER_MIN;
            maximumCriteria = Constants.TIER_MAX;
        } else if (criteria.getName().equals("Type")) {
            minimumCriteria = Constants.TYPE_MIN;;
            maximumCriteria = Constants.TYPE_MAX;
        }
        Long criteriaValue = Long.valueOf(getRandomNumber(minimumCriteria, maximumCriteria));

        int quantity = getRandomNumber(Constants.MINIMUM_QUANTITY, Constants.MAXIMUM_QUANTITY);

        boolean required = getRandomBoolean(Constants.DEMAND_REQUIRED_PERCENTAGE);
        if (i == 1) {
            required = true;
        }

        Visitor_Demand demand = new Visitor_Demand(visitorID, criteriaType, criteriaValue, 0, quantity, required);
        demand.save();
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
}
