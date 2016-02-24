package uk.co.jakelee.blacksmith.helper;

public class Constants {
    public static final int TRUE = 1;

    // Information about game mechanics
    public static final Double LEVEL_MODIFIER = 0.25;
    public static final Double DEFAULT_BONUS = 1.00;
    public static final int MILLISECONDS_BETWEEN_UI_REFRESHES = 1000; // 1 sec
    public static final int MILLISECONDS_BETWEEN_RESTOCK_CHECKS = 60000; // 60 sec
    public static final long MILLISECONDS_BETWEEN_RESTOCKS = 43200000; // 12 hours
    public static final int MILLISECONDS_BETWEEN_VISITOR_CHECKS = 900000; // 15 min
    public static final int MAXIMUM_VISITORS = 10;
    public static final int MAXIMUM_VISITORS_PER_ROW = 5;
    public static final int CRAFT_TIME_MULTIPLIER = 3000;
    public static final int MAX_CRAFTS = 10;
    public static final int MINIMUM_REWARDS = 1;
    public static final int MAXIMUM_REWARDS = 3;

    public static final int NUMBER_OF_TROPHY_COLUMNS = 4;
    public static final int VISITS_TROPHY = 100;
    public static final int VISITS_ALMOST = 66;
    public static final int VISITS_STARTED = 33;
    public static final int VISITS_UNSTARTED = 0;
    public static final int TROPHY_ITEM_REWARDS = 10;

    // Error codes
    public static final int SUCCESS = 1;
    public static final int ERROR_PLAYER_LEVEL = 2;
    public static final int ERROR_UNDISCOVERED = 3;
    public static final int ERROR_NOT_ENOUGH_INGREDIENTS = 4;
    public static final int ERROR_NO_SPARE_SLOTS = 5;
    public static final int ERROR_NO_ITEMS = 6;
    public static final int ERROR_NO_GEMS = 7;
    public static final int ERROR_NOT_ENOUGH_ITEMS = 8;
    public static final int ERROR_NOT_ENOUGH_COINS = 9;
    public static final int ERROR_SHOP_RUN_OUT = 10;

    // Information about lookup tables
    public static final Long ITEM_COINS = 52L;

    public static final Long LOCATION_ANVIL = 1L;
    public static final Long LOCATION_FURNACE = 2L;
    public static final Long LOCATION_SELLING = 3L;
    public static final Long LOCATION_MINE = 4L;
    public static final Long LOCATION_TABLE = 5L;
    public static final Long LOCATION_ENCHANTING = 6L;

    public static final Long SETTING_SOUNDS = 1L;
    public static final Long SETTING_MUSIC = 2L;
    public static final Long SETTING_RESTOCK_NOTIFICATIONS = 3L;
    public static final Long SETTING_NOTIFICATION_SOUNDS = 4L;
    public static final Long SETTING_VISITOR_NOTIFICATIONS = 5L;

    public static final int STATE_NORMAL = 1;
    public static final int STATE_UNFINISHED = 2;
    public static final int STATE_ENCHANTED_MIN = 3;
    public static final int STATE_ENCHANTED_MAX = 7;

    public static final int TIER_MIN = 1;
    public static final int TIER_MAX = 6;

    public static final int TYPE_ANVIL_MIN = 3;
    public static final int TYPE_ANVIL_MAX = 18;

    public static final int TYPE_ORE = 1;
    public static final int TYPE_BAR = 2;
    public static final int TYPE_SECONDARY = 19;
    public static final int TYPE_GEM = 20;
    public static final int TYPE_FOOD = 21;
    public static final int[] VISITOR_REWARD_TYPES = {TYPE_ORE, TYPE_BAR, TYPE_SECONDARY, TYPE_GEM, TYPE_FOOD};


    // State modifiers
    public static final double STATE_UNFINISHED_MODIFIER = 0.5;
    public static final int STATE_ENCHANTED_ADDER = 100;

    // Demands
    public final static int MINIMUM_DEMANDS = 2;
    public final static int MAXIMUM_DEMANDS = 7;
    public final static int MINIMUM_QUANTITY = 1;
    public final static int MAXIMUM_QUANTITY = 10;
    public final static int MINIMUM_CRITERIA = 1;
    public final static int MAXIMUM_CRITERIA = 3;
    public final static int DEMAND_REQUIRED_PERCENTAGE = 70;

}
