package uk.co.jakelee.blacksmith.helper;

import java.util.List;
import java.util.Random;

import uk.co.jakelee.blacksmith.model.Criteria;
import uk.co.jakelee.blacksmith.model.Visitor;
import uk.co.jakelee.blacksmith.model.Visitor_Demand;
import uk.co.jakelee.blacksmith.model.Visitor_Stats;
import uk.co.jakelee.blacksmith.model.Visitor_Type;

public class VisitorHelper {
    public final static int MINIMUM_DEMANDS = 2;
    public final static int MAXIMUM_DEMANDS = 7;
    public final static int MINIMUM_QUANTITY = 3;
    public final static int MAXIMUM_QUANTITY = 20;
    public final static int MINIMUM_CRITERIA = 1;
    public final static int MAXIMUM_CRITERIA = 3;

    public final static int MINIMUM_STATE = 1;
    public final static int MAXIMUM_STATE = 4;
    public final static int MINIMUM_TYPE = 1;
    public final static int MAXIMUM_TYPE = 20;
    public final static int MINIMUM_TIER = 1;
    public final static int MAXIMUM_TIER = 10;



    public final static int DEMAND_REQUIRED_PERCENTAGE = 70;

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
        int numDemands = getRandomNumber(MINIMUM_DEMANDS, MAXIMUM_DEMANDS);

        // Generate the demands
        for (int i = 1; i <= numDemands; i++) {
            generateDemand(i, visitor.getId());
        }
    }

    public static void generateDemand(int i, Long visitorID) {
        // TODO: Make these values be generated instead of hardcoded
        Long criteriaType = Long.valueOf(getRandomNumber(MINIMUM_CRITERIA, MAXIMUM_CRITERIA));
        Criteria criteria = Criteria.findById(Criteria.class, criteriaType);

        int minimumCriteria = 1;
        int maximumCriteria = 1;
        if (criteria.getName() == "State") {
            minimumCriteria = MINIMUM_STATE;
            maximumCriteria = MAXIMUM_STATE;
        } else if (criteria.getName() == "Tier") {
            minimumCriteria = MINIMUM_TIER;
            maximumCriteria = MAXIMUM_TIER;
        } else if (criteria.getName() == "Type") {
            minimumCriteria = MINIMUM_TYPE;
            maximumCriteria = MAXIMUM_TYPE;
        }
        Long criteriaValue = Long.valueOf(getRandomNumber(minimumCriteria, maximumCriteria));

        int quantity = getRandomNumber(MINIMUM_QUANTITY, MAXIMUM_QUANTITY);
        
        boolean required = getRequired(i);

        Visitor_Demand demand = new Visitor_Demand(visitorID, criteriaType, criteriaValue, 0, quantity, required);
        demand.save();
    }

    public static boolean getRequired(int demandNumber) {
        if (demandNumber == 1) {
            // First demand is guaranteed to be required
            return true;
        }
        return getRandomBoolean(DEMAND_REQUIRED_PERCENTAGE);
    }

    public static int getRandomNumber(int minimum, int maximum) {
        Random random = new Random();
        int numDemands = random.nextInt(maximum) + minimum;
        return numDemands;
    }

    public static boolean getRandomBoolean(int truePercentage) {
        if (getRandomNumber(0, 100) > truePercentage) {
            return true;
        } else {
            return false;
        }
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
