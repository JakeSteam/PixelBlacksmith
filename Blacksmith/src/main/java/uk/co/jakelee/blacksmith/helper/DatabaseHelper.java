package uk.co.jakelee.blacksmith.helper;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.main.MainActivity;
import uk.co.jakelee.blacksmith.main.SplashScreenActivity;
import uk.co.jakelee.blacksmith.model.Achievement;
import uk.co.jakelee.blacksmith.model.Assistant;
import uk.co.jakelee.blacksmith.model.Category;
import uk.co.jakelee.blacksmith.model.Character;
import uk.co.jakelee.blacksmith.model.Contribution_Goal;
import uk.co.jakelee.blacksmith.model.Criteria;
import uk.co.jakelee.blacksmith.model.Hero;
import uk.co.jakelee.blacksmith.model.Hero_Adventure;
import uk.co.jakelee.blacksmith.model.Hero_Category;
import uk.co.jakelee.blacksmith.model.Hero_Resource;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Location;
import uk.co.jakelee.blacksmith.model.Message;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Recipe;
import uk.co.jakelee.blacksmith.model.Setting;
import uk.co.jakelee.blacksmith.model.Slot;
import uk.co.jakelee.blacksmith.model.State;
import uk.co.jakelee.blacksmith.model.Super_Upgrade;
import uk.co.jakelee.blacksmith.model.Tier;
import uk.co.jakelee.blacksmith.model.Trader;
import uk.co.jakelee.blacksmith.model.Trader_Stock;
import uk.co.jakelee.blacksmith.model.Type;
import uk.co.jakelee.blacksmith.model.Upgrade;
import uk.co.jakelee.blacksmith.model.Visitor;
import uk.co.jakelee.blacksmith.model.Visitor_Demand;
import uk.co.jakelee.blacksmith.model.Visitor_Stats;
import uk.co.jakelee.blacksmith.model.Visitor_Type;
import uk.co.jakelee.blacksmith.model.Worker;
import uk.co.jakelee.blacksmith.model.Worker_Resource;

import static android.content.Context.MODE_PRIVATE;

public class DatabaseHelper extends AsyncTask<String, String, String> {
    private final static int DB_EMPTY = 0;
    private final static int DB_V1_0_0 = 1;
    private final static int DB_V1_0_1 = 2;
    private final static int DB_V1_2_0 = 3;
    private final static int DB_V1_2_1 = 4;
    private final static int DB_V1_3_0 = 5;
    private final static int DB_V1_4_0 = 6;
    private final static int DB_V1_5_0 = 7;
    private final static int DB_V1_5_4 = 9;
    private final static int DB_V1_6_0 = 10;
    private final static int DB_V1_6_1 = 11;
    private final static int DB_V1_7_0 = 12;
    private final static int DB_V1_7_2 = 13;
    private final static int DB_V1_7_4 = 14;
    private final static int DB_V1_7_7 = 15;
    private final static int DB_V2_0_0 = 16;
    private final static int DB_V2_0_1 = 17;
    private final static int DB_V2_0_3 = 18;
    private final static int DB_V2_1_0 = 19;
    private final static int DB_V2_1_3 = 20;
    private final static int DB_V2_2_0 = 21;
    private final static int DB_V2_3_0 = 22;
    private final static int DB_V2_3_4 = 23;
    private final static int DB_V2_3_5 = 24;
    private final static int DB_V2_3_6 = 25;

    public final static int DB_LATEST = DB_V2_3_6;

    private SplashScreenActivity callingActivity;
    private ProgressBar progressBar;
    private TextView progressText;

    public DatabaseHelper() {
    }

    public DatabaseHelper(SplashScreenActivity activity, boolean runningFromMain) {
        this.callingActivity = activity;
        if (runningFromMain) {
            this.progressText = (TextView) activity.findViewById(R.id.progressText);
            this.progressBar = (ProgressBar) activity.findViewById(R.id.progressBar);
        }
    }

    private void setProgress(String currentTask, int percentage) {
        if (progressText != null && progressBar != null) {
            publishProgress(currentTask);
            progressBar.setProgress(percentage);
        }
    }

    @Override
    protected String doInBackground(String... params) {
        SharedPreferences prefs;
        if (callingActivity != null) {
            prefs = callingActivity.getSharedPreferences("uk.co.jakelee.blacksmith", MODE_PRIVATE);
        } else {
            prefs = MainActivity.prefs;
        }

        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) <= DatabaseHelper.DB_EMPTY) {
            setProgress("Achievements", 0);
            createAchievement();
            setProgress("Category", 2);
            createCategory();
            setProgress("Character", 5);
            createCharacter();
            setProgress("Criteria", 7);
            createCriteria();
            setProgress("Inventory", 10);
            createInventory();
            setProgress("Items", 12);
            createItem();
            setProgress("Locations", 15);
            createLocation();
            setProgress("Messages", 17);
            createMessage();
            setProgress("Statistics", 20);
            createPlayerInfo();
            setProgress("Recipes", 22);
            createRecipe();
            setProgress("Settings", 25);
            createSetting();
            setProgress("Slots", 27);
            createSlot();
            setProgress("States", 30);
            createState();
            setProgress("Tiers", 32);
            createTier();
            setProgress("Traders", 35);
            createTrader();
            setProgress("Types", 37);
            createType();
            setProgress("Upgrades", 40);
            createUpgrade();
            setProgress("Visitors", 42);
            createVisitor();
            setProgress("Visitor Demands", 45);
            createVisitorDemand();
            setProgress("Visitor Stats", 47);
            createVisitorStats();
            setProgress("Visitor Types", 50);
            createVisitorType();

            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V1_0_0).apply();
            TutorialHelper.currentlyInTutorial = true;
        }

        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) <= DatabaseHelper.DB_V1_0_0) {
            setProgress("1.0.1 Patch", 52);
            patch100to101();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V1_0_1).apply();
        }

        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) <= DatabaseHelper.DB_V1_0_1) {
            setProgress("1.2.0 Patch", 55);
            patch101to120();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V1_2_0).apply();
        }

        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) <= DatabaseHelper.DB_V1_2_0) {
            setProgress("1.2.1 Patch", 57);
            patch120to121();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V1_2_1).apply();
        }

        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) <= DatabaseHelper.DB_V1_2_1) {
            setProgress("1.3.0 Patch", 60);
            patch121to130();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V1_3_0).apply();
        }

        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) <= DatabaseHelper.DB_V1_3_0) {
            setProgress("1.4.0 Patch", 62);
            patch130to140();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V1_4_0).apply();
        }

        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) <= DatabaseHelper.DB_V1_4_0) {
            setProgress("1.5.0 Patch", 65);
            patch140to150();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V1_5_0).apply();
        }

        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) <= DatabaseHelper.DB_V1_5_0) {
            setProgress("1.5.4 Patch", 67);
            patch150to154();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V1_5_4).apply();
        }

        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) <= DatabaseHelper.DB_V1_5_4) {
            setProgress("1.6.0 Patch", 70);
            patch154to160();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V1_6_0).apply();
        }

        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) <= DatabaseHelper.DB_V1_6_0) {
            setProgress("1.6.1 Patch", 72);
            patch160to161();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V1_6_1).apply();
        }

        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) <= DatabaseHelper.DB_V1_6_1) {
            setProgress("1.7.0 Patch", 75);
            patch161to170();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V1_7_0).apply();
        }

        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) <= DatabaseHelper.DB_V1_7_0) {
            setProgress("1.7.2 Patch", 77);
            patch170to172();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V1_7_2).apply();
        }

        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) <= DatabaseHelper.DB_V1_7_2) {
            setProgress("1.7.4 Patch", 80);
            patch172to174();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V1_7_4).apply();
        }

        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) <= DatabaseHelper.DB_V1_7_4) {
            setProgress("1.7.7 Patch", 82);
            patch174to177();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V1_7_7).apply();
        }

        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) <= DatabaseHelper.DB_V1_7_7) {
            setProgress("2.0.0 Patch", 85);
            patch177to200();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V2_0_0).apply();
        }

        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) <= DatabaseHelper.DB_V2_0_0) {
            setProgress("2.0.1 Patch", 88);
            patch200to201();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V2_0_1).apply();
        }

        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) <= DatabaseHelper.DB_V2_0_1) {
            setProgress("2.0.3 Patch", 91);
            patch201to203();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V2_0_3).apply();
        }

        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) <= DatabaseHelper.DB_V2_0_3) {
            setProgress("2.1.0 Patch", 94);
            patch203to210();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V2_1_0).apply();
        }

        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) <= DatabaseHelper.DB_V2_1_0) {
            setProgress("2.1.3 Patch", 95);
            patch212to213();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V2_1_3).apply();
        }

        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) < DatabaseHelper.DB_V2_2_0) {
            setProgress("2.2.0 Patch", 96);
            patch214to220();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V2_2_0).apply();
        }

        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) < DatabaseHelper.DB_V2_3_0) {
            setProgress("2.3.0 Christmas Patch", 97);
            patch220to230();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V2_3_0).apply();
        }

        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) < DatabaseHelper.DB_V2_3_4) {
            setProgress("2.3.4", 98);
            patch233to234();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V2_3_4).apply();
        }

        if (prefs.getInt("databaseVersion", DatabaseHelper.DB_EMPTY) < DatabaseHelper.DB_V2_3_5) {
            setProgress("2.3.5", 99);
            patch234to235();
            prefs.edit().putInt("databaseVersion", DatabaseHelper.DB_V2_3_5).apply();
        }

        setProgress("Complete", 100);
        return "";
    }

    @Override
    protected void onPostExecute(String result) {
        if (callingActivity != null) {
            callingActivity.startGame();
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        progressText.setText(values[0].equals("Complete") ? "Starting Up\n" : ("Installing\n" + values[0]));
    }

    private void patch100to101() {
        // Add upgradeable restock cost
        Upgrade restockAllCost = new Upgrade("Restock All Cost", "coins", 7, 50, 650, 50, 650);
        restockAllCost.save();
    }

    private void patch101to120() {
        // Updated mithril sword description
        Item mithrilLongsword = Item.findById(Item.class, 83L);
        if (mithrilLongsword != null) {
            mithrilLongsword.setDescription("This is a sword? No, longer!");
            mithrilLongsword.save();
        }

        // Set enchanting to be L20+
        Slot firstEnchantingSlot = Select.from(Slot.class).where(
                Condition.prop("location").eq(Constants.LOCATION_ENCHANTING),
                Condition.prop("level").eq(1)).first();
        if (firstEnchantingSlot != null) {
            firstEnchantingSlot.setLevel(25);
            firstEnchantingSlot.save();
        }

        List<Item> powders = Select.from(Item.class).where(Condition.prop("type").eq(Constants.TYPE_POWDERS)).list();
        for (Item powder : powders) {
            powder.setLevel(10);
        }
        Item.saveInTx(powders);

    }

    private void patch120to121() {
        Slot firstEnchantingSlot = Select.from(Slot.class).where(
                Condition.prop("location").eq(Constants.LOCATION_ENCHANTING),
                Condition.prop("level").eq(25)).first();
        if (firstEnchantingSlot != null) {
            firstEnchantingSlot.setLevel(10);
            firstEnchantingSlot.save();
        }
    }

    private void patch121to130() {
        Upgrade upgrade = new Upgrade("Worker Time", "mins", 50, 30, 180, 30, 180);
        upgrade.save();

        Setting setting = new Setting(7L, "WorkerNotifications", true);
        setting.save();

        createWorkers();
        createWorkerResources();

        List<Slot> slots = new ArrayList<>();
        slots.add(new Slot(1, 70, false));
        slots.add(new Slot(2, 70, false));
        slots.add(new Slot(3, 70, false));
        slots.add(new Slot(3, 45, false));
        slots.add(new Slot(5, 70, false));
        slots.add(new Slot(6, 25, false));
        slots.add(new Slot(1, 9999, false));
        slots.add(new Slot(2, 9999, false));
        slots.add(new Slot(3, 9999, false));
        slots.add(new Slot(4, 9999, false));
        slots.add(new Slot(5, 9999, false));
        slots.add(new Slot(6, 9999, false));
        Slot.saveInTx(slots);

        Trader trader = Select.from(Trader.class).where(
                Condition.prop("name").eq("The Exclusive Emporium")).first();
        trader.setWeighting(15);
        trader.save();

        Trader_Stock traderStock = Select.from(Trader_Stock.class).where(
                Condition.prop("trader_type").eq(20L)).first();
        traderStock.setDefaultStock(5);
        traderStock.setStock(5);
        traderStock.save();

        Item.executeQuery("UPDATE item SET value = value - 1 WHERE tier = 1 OR id = 11");
    }

    private void patch130to140() {
        Trader_Stock.executeQuery("UPDATE traderstock SET item_id = item_id + 16 WHERE trader_type = 33");
        Item.executeQuery("UPDATE item SET value = 550 WHERE id = 140");
        Visitor_Type.executeQuery("UPDATE visitortype SET tier_preferred = 11 WHERE visitor_id = 11");

        List<Upgrade> upgrades = new ArrayList<>();
        upgrades.add(new Upgrade("Minimum Visitor Rewards", "", 350, 1, 1, 5, 1));
        upgrades.add(new Upgrade("Maximum Visitor Rewards", "", 350, 1, 5, 15, 5));
        Upgrade.saveInTx(upgrades);

        if (!Player_Info.isPremium()) {
            Slot.executeQuery("UPDATE slot SET premium = 1 WHERE level = 9999");
        }
    }

    private void patch140to150() {
        if (Upgrade.getValue("Minimum Visitor Rewards") == 0) {
            patch130to140();
        }

        Achievement collectionAchiev = new Achievement("The Collector", "Completed The Collection", 1, 19, "CgkI6tnE2Y4OEAIQJA");
        collectionAchiev.save();

        Character foodCharacter = new Character(21L, "Farmer's Evil Friend", "What do you think of my new paint job?");
        foodCharacter.save();

        Item.executeQuery("UPDATE Item SET description = 'A sturdier rod for catching fish.' WHERE id = 67"); // fishing rod
        Item.executeQuery("UPDATE Item SET value = 410 WHERE id = 121"); // rune helm
        Item.executeQuery("UPDATE Item SET type = 27, value = 10 WHERE id = 78"); //cheese
        Item.executeQuery("UPDATE Item SET type = 27, value = 20 WHERE id = 79"); //bread
        List<Item> items = new ArrayList<>();
        items.add(new Item(177L, "Red Page", "The page seems to glow with power.", 25, 11, 0, 1));
        items.add(new Item(178L, "Red Page (dirty)", "The page seems to glow with dulled power.", 25, 11, 0, 1));
        items.add(new Item(179L, "Yellow Page", "The page seems to vibrate.", 25, 11, 0, 1));
        items.add(new Item(180L, "Yellow Page (dirty)", "The page seems to vibrate slightly.", 25, 11, 0, 1));
        items.add(new Item(181L, "Green Page", "The page emits strong, leafy smell.", 25, 11, 0, 1));
        items.add(new Item(182L, "Green Page (dirty)", "The page emits a faint flowery smell.", 25, 11, 0, 1));
        items.add(new Item(183L, "Blue Page", "The page is rather soggy.", 25, 11, 0, 1));
        items.add(new Item(184L, "Blue Page (dirty)", "The page is slightly damp", 25, 11, 0, 1));
        items.add(new Item(185L, "Pink Page", "The page's appearance somehow cheers you up.", 25, 11, 0, 1));
        items.add(new Item(186L, "Pink Page (dirty)", "The page makes you feel a little bit happier.", 25, 11, 0, 1));
        items.add(new Item(187L, "Brown Page", "The page is a little muddy.", 25, 11, 0, 1));
        items.add(new Item(188L, "Brown Page (dirty)", "The page is covered in rather messy mud.", 25, 11, 0, 1));
        items.add(new Item(189L, "Black Page", "The page whispers stories of battles long ended to you.", 25, 11, 0, 1));
        items.add(new Item(190L, "Black Page (dirty)", "The page's appearance makes you more irritable.", 25, 11, 0, 1));
        items.add(new Item(191L, "White Page", "The page's leaves have petals sprouting from them.", 25, 11, 0, 1));
        items.add(new Item(192L, "White Page (dirty)", "The page has bugs crawling all over.", 25, 11, 0, 1));

        items.add(new Item(193L, "Book of Strength", "A powerful book, with information on combat techniques.", 26, 11, 15000, 1));
        items.add(new Item(194L, "Book of Agility", "An agile book, containing information on dodging techniques.", 26, 11, 15000, 1));
        items.add(new Item(195L, "Book of Nature", "A natural book, containing information on gardening.", 26, 11, 15000, 1));
        items.add(new Item(196L, "Book of Water", "A wet book, containing information on sea creatures.", 26, 11, 15000, 1));
        items.add(new Item(197L, "Book of Peace", "A relaxing book, helping to negotiate between others.", 26, 11, 15000, 1));
        items.add(new Item(198L, "Book of Earth", "A brown book, containing information about Earth.", 26, 11, 15000, 1));
        items.add(new Item(199L, "Book of War", "A deadly book, containing information on poisons.", 26, 11, 15000, 1));
        items.add(new Item(200L, "Book of Life", "A lively book, containing information on the world's species.", 26, 11, 15000, 1));
        items.add(new Item(201L, "The Collection", "A collection of all books,the ultimate prize.", 26, 11, 55000, 100));

        items.add(new Item(202L, "Wheat", "The grain in game falls mainly in... here.", 21, 11, 5, 1));
        items.add(new Item(203L, "Egg", "Egg-sactly what you need.", 21, 11, 5, 1));
        items.add(new Item(204L, "Milk", "Freshly squeezed.", 21, 11, 5, 1));
        items.add(new Item(205L, "Blueberry", "Like a blackberry, but bluer.", 21, 11, 5, 1));
        items.add(new Item(206L, "Banana", "Not split. Yet.", 21, 11, 5, 1));
        items.add(new Item(207L, "Orange", "Orange you glad you bought this?", 21, 11, 5, 1));
        items.add(new Item(208L, "Cherry", "Cherish this cherry. Or use it.", 21, 11, 5, 1));
        items.add(new Item(209L, "Candy", "Don't eat too many!", 21, 11, 5, 1));
        items.add(new Item(210L, "Chocolate", "Don't eat too much!", 21, 11, 5, 1));
        items.add(new Item(211L, "Raw Fish", "A very fishy dishy. Delishy!.", 21, 11, 5, 1));
        items.add(new Item(212L, "Fruit Salad", "Fresh, not from a can.", 27, 11, 30, 1));
        items.add(new Item(213L, "Cooked Fish", "A less fishy dishy, still delishy!.", 27, 11, 10, 1));
        items.add(new Item(214L, "Cooked Meat", "Not dripping any more. Phew!", 27, 11, 10, 1));
        items.add(new Item(215L, "Ham + Cheese S'wich", "A sandwich with all the insides inside(s).", 27, 11, 45, 1));
        items.add(new Item(216L, "Ham Sandwich", "Ham, not spam!", 27, 11, 35, 1));
        items.add(new Item(217L, "Cheese Sandwich", "Not grilled, unfortunately.", 27, 11, 35, 1));
        items.add(new Item(218L, "Pie", "This pie isn't", 27, 11, 30, 1));
        items.add(new Item(219L, "Cookie", "Look at the way it crumbles.", 27, 11, 30, 1));
        Item.saveInTx(items);

        List<Player_Info> player_infos = new ArrayList<>();
        player_infos.add(new Player_Info("LastDonated", "never"));
        player_infos.add(new Player_Info("TimesDonated", 0));
        player_infos.add(new Player_Info("LastBonusClaimed", System.currentTimeMillis()));
        player_infos.add(new Player_Info("BonusesClaimed", 0));
        player_infos.add(new Player_Info("CollectionsCreated", 0, 0));
        player_infos.add(new Player_Info("QuestsCompleted", 0));
        Player_Info.saveInTx(player_infos);

        List<Recipe> recipes = new ArrayList<>();
        // Red book
        recipes.add(new Recipe(193L, 1L, 177L, 1L, 5));
        recipes.add(new Recipe(193L, 1L, 178L, 1L, 5));
        recipes.add(new Recipe(193L, 1L, 132L, 1L, 2));
        recipes.add(new Recipe(193L, 1L, 133L, 1L, 2));
        recipes.add(new Recipe(193L, 1L, 134L, 1L, 2));
        recipes.add(new Recipe(193L, 1L, 145L, 1L, 2));
        recipes.add(new Recipe(193L, 1L, 146L, 1L, 2));
        recipes.add(new Recipe(193L, 1L, 147L, 1L, 2));
        recipes.add(new Recipe(193L, 1L, 10L, 1L, 250));
        // Yellow book
        recipes.add(new Recipe(194L, 1L, 179L, 1L, 5));
        recipes.add(new Recipe(194L, 1L, 180L, 1L, 5));
        recipes.add(new Recipe(194L, 1L, 156L, 1L, 1));
        recipes.add(new Recipe(194L, 1L, 157L, 1L, 1));
        recipes.add(new Recipe(194L, 1L, 158L, 1L, 1));
        recipes.add(new Recipe(194L, 1L, 159L, 1L, 1));
        recipes.add(new Recipe(194L, 1L, 160L, 1L, 1));
        recipes.add(new Recipe(194L, 1L, 206L, 1L, 2));
        recipes.add(new Recipe(194L, 1L, 8L, 1L, 300));
        recipes.add(new Recipe(194L, 1L, 52L, 1L, 5000));
        // Green book
        recipes.add(new Recipe(195L, 1L, 181L, 1L, 5));
        recipes.add(new Recipe(195L, 1L, 182L, 1L, 5));
        recipes.add(new Recipe(195L, 1L, 6L, 1L, 400));
        recipes.add(new Recipe(195L, 1L, 100L, 5L, 20));
        recipes.add(new Recipe(195L, 1L, 130L, 1L, 100));
        // Blue book
        recipes.add(new Recipe(196L, 1L, 183L, 1L, 5));
        recipes.add(new Recipe(196L, 1L, 184L, 1L, 5));
        recipes.add(new Recipe(196L, 1L, 129L, 1L, 120));
        recipes.add(new Recipe(196L, 1L, 119L, 1L, 3));
        recipes.add(new Recipe(196L, 1L, 120L, 1L, 3));
        recipes.add(new Recipe(196L, 1L, 121L, 4L, 1));
        recipes.add(new Recipe(196L, 1L, 122L, 4L, 1));
        recipes.add(new Recipe(196L, 1L, 211, 1L, 5));
        recipes.add(new Recipe(196L, 1L, 73L, 1L, 5));
        // Pink book
        recipes.add(new Recipe(197L, 1L, 185L, 1L, 5));
        recipes.add(new Recipe(197L, 1L, 186L, 1L, 5));
        recipes.add(new Recipe(197L, 1L, 148L, 1L, 8));
        recipes.add(new Recipe(197L, 1L, 80L, 1L, 100));
        // Brown book
        recipes.add(new Recipe(198L, 1L, 187L, 1L, 5));
        recipes.add(new Recipe(198L, 1L, 188L, 1L, 5));
        recipes.add(new Recipe(198L, 1L, 79L, 1L, 100));
        recipes.add(new Recipe(198L, 1L, 71L, 1L, 100));
        recipes.add(new Recipe(198L, 1L, 32L, 1L, 5));
        recipes.add(new Recipe(198L, 1L, 32L, 2L, 5));
        recipes.add(new Recipe(198L, 1L, 32L, 3L, 5));
        recipes.add(new Recipe(198L, 1L, 32L, 4L, 5));
        recipes.add(new Recipe(198L, 1L, 32L, 5L, 5));
        recipes.add(new Recipe(198L, 1L, 32L, 6L, 5));
        recipes.add(new Recipe(198L, 1L, 32L, 7L, 5));
        // Black book
        recipes.add(new Recipe(199L, 1L, 189L, 1L, 5));
        recipes.add(new Recipe(199L, 1L, 190L, 1L, 5));
        recipes.add(new Recipe(199L, 1L, 3L, 1L, 200));
        recipes.add(new Recipe(199L, 1L, 31L, 7L, 1));
        recipes.add(new Recipe(199L, 1L, 47L, 7L, 1));
        recipes.add(new Recipe(199L, 1L, 64L, 7L, 1));
        recipes.add(new Recipe(199L, 1L, 92L, 7L, 1));
        recipes.add(new Recipe(199L, 1L, 108L, 7L, 1));
        recipes.add(new Recipe(199L, 1L, 124L, 7L, 1));
        recipes.add(new Recipe(199L, 1L, 143L, 7L, 1));
        // White book
        recipes.add(new Recipe(200L, 1L, 191L, 1L, 5));
        recipes.add(new Recipe(200L, 1L, 192L, 1L, 5));
        recipes.add(new Recipe(200L, 1L, 13L, 1L, 150));
        recipes.add(new Recipe(200L, 1L, 17L, 1L, 150));
        recipes.add(new Recipe(200L, 1L, 150L, 1L, 1));
        recipes.add(new Recipe(200L, 1L, 151L, 1L, 1));
        recipes.add(new Recipe(200L, 1L, 152L, 1L, 1));
        recipes.add(new Recipe(200L, 1L, 153L, 1L, 1));
        recipes.add(new Recipe(200L, 1L, 154L, 1L, 1));
        // Collection
        recipes.add(new Recipe(201L, 1L, 193L, 1L, 1));
        recipes.add(new Recipe(201L, 1L, 194L, 1L, 1));
        recipes.add(new Recipe(201L, 1L, 195L, 1L, 1));
        recipes.add(new Recipe(201L, 1L, 196L, 1L, 1));
        recipes.add(new Recipe(201L, 1L, 197L, 1L, 1));
        recipes.add(new Recipe(201L, 1L, 198L, 1L, 1));
        recipes.add(new Recipe(201L, 1L, 199L, 1L, 1));
        recipes.add(new Recipe(201L, 1L, 200L, 1L, 1));
        recipes.add(new Recipe(201L, 1L, 52L, 1L, 10000));

        // Bread
        recipes.add(new Recipe(79L, 1L, 202L, 1L, 1));
        recipes.add(new Recipe(79L, 1L, 203L, 1L, 1));
        recipes.add(new Recipe(79L, 1L, 204L, 1L, 1));
        // Cheese
        recipes.add(new Recipe(78L, 1L, 204L, 1L, 1));
        // Fruit Salad
        recipes.add(new Recipe(212L, 1L, 77L, 1L, 1));
        recipes.add(new Recipe(212L, 1L, 205L, 1L, 1));
        recipes.add(new Recipe(212L, 1L, 206L, 1L, 1));
        recipes.add(new Recipe(212L, 1L, 207L, 1L, 1));
        recipes.add(new Recipe(212L, 1L, 208L, 1L, 1));
        // Cooked Fish
        recipes.add(new Recipe(213L, 1L, 211L, 1L, 1));
        // Cooked Meat
        recipes.add(new Recipe(214L, 1L, 80L, 1L, 1));
        // Ham + Cheese S'wich
        recipes.add(new Recipe(215L, 1L, 79L, 1L, 1));
        recipes.add(new Recipe(215L, 1L, 78L, 1L, 1));
        recipes.add(new Recipe(215L, 1L, 214L, 1L, 1));
        // Ham S'wich
        recipes.add(new Recipe(216L, 1L, 79L, 1L, 1));
        recipes.add(new Recipe(216L, 1L, 214L, 1L, 1));
        // Cheese S'wich
        recipes.add(new Recipe(217L, 1L, 79L, 1L, 1));
        recipes.add(new Recipe(217L, 1L, 78L, 1L, 1));
        // Pie
        recipes.add(new Recipe(218L, 1L, 202L, 1L, 1));
        recipes.add(new Recipe(218L, 1L, 203L, 1L, 1));
        recipes.add(new Recipe(218L, 1L, 204L, 1L, 1));
        recipes.add(new Recipe(218L, 1L, 205L, 1L, 1));
        // Cookie
        recipes.add(new Recipe(219L, 1L, 202L, 1L, 1));
        recipes.add(new Recipe(219L, 1L, 203L, 1L, 1));
        recipes.add(new Recipe(219L, 1L, 204L, 1L, 1));
        recipes.add(new Recipe(219L, 1L, 210L, 1L, 1));
        Recipe.saveInTx(recipes);

        List<Setting> settings = new ArrayList<>();
        settings.add(new Setting(8L, "HideAllAdverts", false));
        settings.add(new Setting(9L, "BonusNotifications", true));
        Setting.saveInTx(settings);

        Trader_Stock.executeQuery("UPDATE TraderStock SET stock = 3 * stock, default_stock = 3 * default_stock WHERE trader_type = 15"); // Increase coal amounts
        Trader_Stock.executeQuery("UPDATE TraderStock SET stock = 3 * stock, default_stock = 3 * default_stock WHERE trader_type = 16");
        Trader_Stock.executeQuery("UPDATE TraderStock SET stock = 5 * stock, default_stock = 5 * default_stock WHERE trader_type = 17");
        List<Trader> traders = new ArrayList<>();
        List<Trader_Stock> trader_stocks = new ArrayList<>();
        // Add extra coal to existing shops
        trader_stocks.add(new Trader_Stock(46L, 3L, 1, 350, 100));
        trader_stocks.add(new Trader_Stock(25L, 3L, 1, 100, 200));
        trader_stocks.add(new Trader_Stock(23L, 3L, 1, 40, 60));
        trader_stocks.add(new Trader_Stock(21L, 3L, 1, 20, 30));
        trader_stocks.add(new Trader_Stock(2L, 3L, 1, 250, 80));

        // Add fish to existing shop
        trader_stocks.add(new Trader_Stock(41L, 211L, 1, 0, 20));

        traders.add(new Trader(21L, 4, "The Pre-Bakery", "All the ingredients, none of the bread!", 5, 0, 0, 10));
        trader_stocks.add(new Trader_Stock(54L, 202L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(54L, 203L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(54L, 204L, 1, 0, 10));

        traders.add(new Trader(21L, 4, "Fruit Stand", "All of your five a day.", 10, 0, 0, 15));
        trader_stocks.add(new Trader_Stock(55L, 77L, 1, 0, 6));
        trader_stocks.add(new Trader_Stock(55L, 205L, 1, 0, 6));
        trader_stocks.add(new Trader_Stock(55L, 206L, 1, 0, 6));
        trader_stocks.add(new Trader_Stock(55L, 207L, 1, 0, 6));
        trader_stocks.add(new Trader_Stock(55L, 208L, 1, 0, 6));

        traders.add(new Trader(21L, 4, "The Sweet Spot", "Careful, they'll rot your teeth.", 15, 0, 0, 20));
        trader_stocks.add(new Trader_Stock(56L, 209L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(56L, 210L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(56L, 209L, 1, 25, 20));
        trader_stocks.add(new Trader_Stock(56L, 210L, 1, 25, 20));
        Trader.saveInTx(traders);
        Trader_Stock.saveInTx(trader_stocks);

        List<Type> types = new ArrayList<>();
        types.add(new Type(25L, "Page", 1, 30, 0));
        types.add(new Type(26L, "Book", 1, 30, 0));
        types.add(new Type(27L, "Processed Food", 1, 1, 15));
        Type.saveInTx(types);

        Visitor_Type.executeQuery("UPDATE VisitorType SET type_preferred = 5 WHERE visitor_id = 8");
        Visitor_Type.executeQuery("UPDATE VisitorType SET type_preferred = 4 WHERE visitor_id = 9");

        Worker.executeQuery("UPDATE worker SET food_used = 0, favourite_food_discovered = 0, favourite_food = worker_id + 211"); // add new field defaults
        Worker_Resource.deleteAll(Worker_Resource.class, "tool_id IN (34, 50, 67, 95, 111, 127, 146, 175)"); // recreate fishing rods
        List<Worker_Resource> workerResources = new ArrayList<>();
        workerResources.add(new Worker_Resource(34, 77, 1, 5)); // Bronze fishing rod
        workerResources.add(new Worker_Resource(34, 80, 1, 5)); // Bronze fishing rod
        workerResources.add(new Worker_Resource(50, 202, 1, 3)); // Iron fishing rod
        workerResources.add(new Worker_Resource(50, 203, 1, 3)); // Iron fishing rod
        workerResources.add(new Worker_Resource(50, 204, 1, 3)); // Iron fishing rod
        workerResources.add(new Worker_Resource(67, 202, 1, 6)); // Steel fishing rod
        workerResources.add(new Worker_Resource(67, 203, 1, 6)); // Steel fishing rod
        workerResources.add(new Worker_Resource(67, 204, 1, 6)); // Steel fishing rod
        workerResources.add(new Worker_Resource(95, 205, 1, 5)); // Mithril fishing rod
        workerResources.add(new Worker_Resource(95, 206, 1, 5)); // Mithril fishing rod
        workerResources.add(new Worker_Resource(95, 207, 1, 5)); // Mithril fishing rod
        workerResources.add(new Worker_Resource(95, 208, 1, 5)); // Mithril fishing rod
        workerResources.add(new Worker_Resource(111, 209, 1, 10)); // Adamant fishing rod
        workerResources.add(new Worker_Resource(111, 210, 1, 10)); // Adamant fishing rod
        workerResources.add(new Worker_Resource(127, 77, 1, 10)); // Rune fishing rod
        workerResources.add(new Worker_Resource(127, 203, 1, 10)); // Rune fishing rod
        workerResources.add(new Worker_Resource(127, 80, 1, 10)); // Rune fishing rod
        workerResources.add(new Worker_Resource(127, 211, 1, 10)); // Rune fishing rod
        workerResources.add(new Worker_Resource(146, 205, 1, 15)); // Dragon fishing rod
        workerResources.add(new Worker_Resource(146, 206, 1, 15)); // Dragon fishing rod
        workerResources.add(new Worker_Resource(146, 207, 1, 15)); // Dragon fishing rod
        workerResources.add(new Worker_Resource(146, 208, 1, 15)); // Dragon fishing rod
        workerResources.add(new Worker_Resource(175, 202, 1, 30)); // Legendary fishing rod
        workerResources.add(new Worker_Resource(175, 203, 1, 30)); // Legendary fishing rod
        workerResources.add(new Worker_Resource(175, 204, 1, 30)); // Legendary fishing rod
        Worker_Resource.saveInTx(workerResources);
    }

    private void patch150to154() {
        Type.executeQuery("UPDATE type SET name = 'Cooked Food' WHERE id IN (27, 28)");
        Type.executeQuery("UPDATE type SET name = 'Raw Food' WHERE id = 21");
    }

    private void patch154to160() {
        // Fix collection + prestige achievements
        Achievement.executeQuery("UPDATE achievement SET player_info_id = (SELECT id FROM playerinfo WHERE name = 'CollectionsCreated') WHERE name = 'The Collector'");
        Achievement.executeQuery("UPDATE achievement SET player_info_id = (SELECT id FROM playerinfo WHERE name = 'Prestige') WHERE name = 'The Fun Never Stops'");
        Player_Info.executeQuery("UPDATE playerinfo SET last_sent_value = 0 WHERE name IN ('CollectionsCreated','Prestige')");

        // Change gold bar to level 35
        Item.executeQuery("UPDATE item SET level = 35 WHERE id = 18");

        // Store current version, so updates can be checked
        List<Player_Info> infos = new ArrayList<>();
        infos.add(new Player_Info("SavedVersion", 0));
        infos.add(new Player_Info("HighestLevel", Player_Info.getPlayerLevel()));
        Player_Info.saveInTx(infos);

        // Change pie to include 2 apples, not 1 blueberry. Change legendary half helmet + hammer to use 3 parts each.
        Recipe.executeQuery("UPDATE recipe SET ingredient = 77 WHERE item = 218 and ingredient = 205");
        Recipe.executeQuery("UPDATE recipe SET quantity = 3 WHERE item IN (169, 176)");

        // Setting to control whether food should auto re fill
        List<Setting> settings = new ArrayList<>();
        settings.add(new Setting(10L, "Autofeed", false));
        settings.add(new Setting(11L, "EnableClickChange", true));
        Setting.saveInTx(settings);

        // Rename premium tier
        Tier.executeQuery("UPDATE tier SET name = 'Legendary' WHERE name = 'Premium'");

        // Fix trader desc
        Trader.executeQuery("UPDATE trader SET description = 'Lots and and lots and lots of ore!' WHERE name = 'Lots More Ore'");

        // Change upgrade name
        Upgrade.executeQuery("UPDATE upgrade SET name = 'Coins Bonus' WHERE name = 'Gold Bonus'");

        // Change mice to prefer cooked food
        Visitor_Type.executeQuery("UPDATE visitortype SET type_preferred = (SELECT id FROM type WHERE name = 'Cooked Food') WHERE visitor_id IN (13,48)");

        // Change type of worker 7. Swap favourite item for worker 1 + 3, and worker 4 + 7 (currently worker_id + 211).
        Worker.executeQuery("UPDATE worker SET character_id = 11 WHERE worker_id = 7");
        Worker.executeQuery("UPDATE worker SET favourite_food = 214 WHERE worker_id = 1");
        Worker.executeQuery("UPDATE worker SET favourite_food = 212 WHERE worker_id = 3");
        Worker.executeQuery("UPDATE worker SET favourite_food = 218 WHERE worker_id = 4");
        Worker.executeQuery("UPDATE worker SET favourite_food = 215 WHERE worker_id = 7");

        // Halve hammer resources, and change ruby / onyx resources.
        Worker_Resource.executeQuery("UPDATE workerresource SET resource_quantity = resource_quantity * 0.75 WHERE tool_id IN (35, 51, 68, 96, 112, 128, 147, 176)");
        Worker_Resource.executeQuery("DELETE FROM workerresource WHERE tool_id IN (72, 76)");
        List<Worker_Resource> workerResources = new ArrayList<>();
        workerResources.add(new Worker_Resource(72, 129, 1, 2)); // Ruby
        workerResources.add(new Worker_Resource(72, 130, 1, 2)); // Ruby
        workerResources.add(new Worker_Resource(76, 129, 1, 3)); // Onyx
        workerResources.add(new Worker_Resource(76, 130, 1, 2)); // Onyx
        workerResources.add(new Worker_Resource(76, 131, 1, 2)); // Onyx
        Worker_Resource.saveInTx(workerResources);
    }

    private void patch160to161() {
        Worker_Resource.executeQuery("DELETE FROM workerresource WHERE tool_id IN (72, 76)");
        List<Worker_Resource> workerResources = new ArrayList<>();
        workerResources.add(new Worker_Resource(72, 129, 1, 2)); // Ruby
        workerResources.add(new Worker_Resource(72, 130, 1, 2)); // Ruby
        workerResources.add(new Worker_Resource(76, 129, 1, 3)); // Onyx
        workerResources.add(new Worker_Resource(76, 130, 1, 2)); // Onyx
        workerResources.add(new Worker_Resource(76, 131, 1, 2)); // Onyx
        Worker_Resource.saveInTx(workerResources);
    }

    private void patch161to170() {
        // Update prices of legendary hammer + half helmet to reflect part change
        Item.executeQuery("UPDATE item SET value = 3000 WHERE name IN (\"Legendary half helmet\",\"Legendary hammer\")");

        // Update prices of bronze items
        Item.executeQuery("UPDATE item SET value = 5 WHERE name = \"Bronze bar\"");
        Item.executeQuery("UPDATE item SET value = 12 WHERE name IN (\"Bronze sword\",\"Bronze longsword\",\"Bronze boots\",\"Bronze pickaxe\",\"Bronze fishing rod\")");
        Item.executeQuery("UPDATE item SET value = 17 WHERE name IN (\"Bronze half shield\",\"Bronze full shield\",\"Bronze half helmet\",\"Bronze chainmail\")");
        Item.executeQuery("UPDATE item SET value = 22 WHERE name = \"Bronze full helmet\"");
        Item.executeQuery("UPDATE item SET value = 27 WHERE name = \"Bronze platebody\"");

        // Update prices of all other tiers
        Item.executeQuery("UPDATE item SET value = value + 8 WHERE name LIKE \"Mithril%\" AND name <> \"Mithril bar\" AND name <> \"Mithril ore\"");
        Item.executeQuery("UPDATE item SET value = value + 7 WHERE name LIKE \"Adamant%\" AND name <> \"Adamant bar\" AND name <> \"Adamant ore\"");
        Item.executeQuery("UPDATE item SET value = value + 15 WHERE name LIKE \"Rune%\" AND name <> \"Rune bar\" AND name <> \"Rune ore\"");
        Item.executeQuery("UPDATE item SET value = value + 15 WHERE name LIKE \"Dragon%\" AND name <> \"Dragon bar\" AND name <> \"Dragon ore\"");

        // Update prices of powdered items + visage
        Item.executeQuery("UPDATE item SET value = 10 WHERE name IN (\"Powdered sapphire\",\"Powdered emerald\")");
        Item.executeQuery("UPDATE item SET value = 20 WHERE name = \"Powdered diamond\"");
        Item.executeQuery("UPDATE item SET value = 30 WHERE name = \"Draconic visage\"");

        // Delete all sapphire from rune recipes.
        Recipe.deleteAll(Recipe.class, "ingredient = 129 AND item IN (SELECT id FROM item WHERE name LIKE \"Rune%\")");

        Worker_Resource.executeQuery("UPDATE workerresource SET resource_quantity = 10 WHERE tool_id IN (73, 74, 75)");
        Worker_Resource.executeQuery("UPDATE workerresource SET resource_quantity = 5 WHERE resource_id IN (72, 76)");

        List<Setting> settings = new ArrayList<>();
        settings.add(new Setting(12L, "OnlyAvailableItems", false));
        settings.add(new Setting(13L, "OpenMessageLog", true));
        settings.add(new Setting(14L, "Fullscreen", true));
        settings.add(new Setting(15L, "Autorefresh", false));
        settings.add(new Setting(16L, "CheckFullscreen", true));
        settings.add(new Setting(17L, "UpdateSlots", true));
        settings.add(new Setting(18L, "LongToasts", false));
        settings.add(new Setting(19L, "HandleMax", false));
        Setting.saveInTx(settings);

        // Updating minimum levels for traders
        Trader.executeQuery("UPDATE trader SET level = 20 WHERE name = \"The Backbone\" OR name = \"GemCrusher 9000\"");
        Trader.executeQuery("UPDATE trader SET level = 35 WHERE name = \"The Nougat\" OR name = \"The Golden Boulders\"");
        Trader.executeQuery("UPDATE trader SET level = 40 WHERE name = \"The Prime Cuts\"");
        Trader.executeQuery("UPDATE trader SET level = 45 WHERE name = \"The Exclusive Emporium\"");

        // Increasing stock for gem / powder traders
        Trader_Stock.executeQuery("UPDATE traderstock SET default_stock = default_stock * 5 WHERE trader_type = 48");

        // Actually fixing green and blue visitors...
        Visitor_Type.executeQuery("UPDATE VisitorType SET state_preferred = 5, type_preferred = 20 WHERE visitor_id = 8");
        Visitor_Type.executeQuery("UPDATE VisitorType SET state_preferred = 4, type_preferred = 20 WHERE visitor_id = 9");

        createContributionGoals();
        createHero();
        createSuperUpgrade();
    }

    private void patch170to172() {
        List<Hero_Resource> heroResources = new ArrayList<>();
        heroResources.add(new Hero_Resource(251, 91, Constants.STATE_NORMAL, 10));
        heroResources.add(new Hero_Resource(252, 107, Constants.STATE_NORMAL, 7));
        heroResources.add(new Hero_Resource(253, 123, Constants.STATE_NORMAL, 4));
        heroResources.add(new Hero_Resource(254, 142, Constants.STATE_NORMAL, 1));
        Hero_Resource.saveInTx(heroResources);

        Item.executeQuery("UPDATE item SET value = 25 WHERE name = \"Adamantite ore\"");
        Item.executeQuery("UPDATE item SET value = 100 WHERE name = \"Dragonite ore\"");
        Item.executeQuery("UPDATE item SET value = 12 WHERE name IN (\"Bronze bow\", \"Bronze gloves\", \"Bronze hatchet\")");
        Item.executeQuery("UPDATE item SET value = 10 WHERE name IN (\"Powdered Sapphire\", \"Powdered Emerald\")");
        Item.executeQuery("UPDATE item SET value = 20 WHERE name = \"Powdered Diamond\"");
        Item.executeQuery("UPDATE item SET value = 325 WHERE name = \"Rune half helmet\"");

        Item.executeQuery("UPDATE type SET name = \"Page\" WHERE id = " + Constants.TYPE_PAGE);
        Item.executeQuery("UPDATE type SET name = \"Book\" WHERE id = " + Constants.TYPE_BOOK);
        Super_Upgrade.executeQuery("UPDATE superupgrade SET name = \"2x Worker Resources\" WHERE super_upgrade_id = " + Constants.SU_WORKER_RESOURCES);
    }

    private void patch172to174() {
        Hero_Adventure.executeQuery("UPDATE heroadventure SET adventure_id = adventure_id + 1 WHERE name IN (\"Hunt Tarantula Spiders\",\"Hunt Black Widow Spiders\")");
        Hero_Resource.executeQuery("UPDATE heroresource SET adventure_id = 234 WHERE resource_id = 70 AND resource_quantity = 13");

        Worker_Resource.executeQuery("UPDATE workerresource SET resource_quantity = 1 WHERE tool_id BETWEEN 149 AND 160 AND resource_id BETWEEN 72 AND 76");
        Worker_Resource.executeQuery("UPDATE workerresource SET resource_quantity = 10 WHERE tool_id IN (73, 74, 75)");
        Worker_Resource.executeQuery("UPDATE workerresource SET resource_quantity = 5 WHERE tool_id IN (72, 76)");
    }

    private void patch174to177() {
        Hero_Resource.executeQuery("UPDATE heroresource SET adventure_id = 417 WHERE adventure_id = 416 AND resource_id IN (192, 149, 60)");
    }

    private void patch177to200() {
        if (Setting.findById(Setting.class, Constants.SETTING_FINISHED_NOTIFICATIONS) == null) {
            List<Setting> settings = new ArrayList<>();
            settings.add(new Setting(Constants.SETTING_FINISHED_NOTIFICATIONS, "FinishedNotifications", true));
            settings.add(new Setting(Constants.SETTING_BULK_STACK, "BulkCrafting", false));
            settings.add(new Setting(Constants.SETTING_ORIENTATION, "Orientation", Constants.ORIENTATION_AUTO));
            Setting.saveInTx(settings);

            List<Item> items = new ArrayList<>();
            items.add(new Item(220L, "Amethyst", "A purple gem", 20, 11, 350, 20));
            items.add(new Item(221L, "Citrine", "A yellow gem", 20, 11, 350, 20));
            Item.saveInTx(items);

            List<State> states = new ArrayList<>();
            states.add(new State(8L, "Purple Enchant", "(purp) ", 220L, 40, 10));
            states.add(new State(9L, "Yellow Enchant", "(yellow) ", 221L, 40, 10));
            State.saveInTx(states);

            List<Trader_Stock> traderStocks = new ArrayList<>();
            traderStocks.add(new Trader_Stock(16L, 220L, 1, 0, 5));
            traderStocks.add(new Trader_Stock(16L, 221L, 1, 0, 5));
            traderStocks.add(new Trader_Stock(48L, 220L, 1, 0, 3));
            traderStocks.add(new Trader_Stock(48L, 221L, 1, 0, 3));
            Trader_Stock.saveInTx(traderStocks);

            Super_Upgrade oldDoubleCraft = Super_Upgrade.find(Constants.SU_DOUBLE_FURNACE_CRAFTS);
            oldDoubleCraft.setName("2x Furnace Items");
            oldDoubleCraft.save();

            List<Super_Upgrade> superUpgrades = new ArrayList<>();
            superUpgrades.add(new Super_Upgrade(Constants.SU_DOUBLE_ANVIL_CRAFTS, "2x Anvil Items", 1, false));
            superUpgrades.add(new Super_Upgrade(Constants.SU_DOUBLE_TABLE_CRAFTS, "2x Table Items", 1, false));
            superUpgrades.add(new Super_Upgrade(Constants.SU_DOUBLE_ENCHANT_CRAFTS, "2x Enchant Table Items", 1, false));
            Super_Upgrade.saveInTx(superUpgrades);
        }

        Hero_Category.executeQuery("UPDATE herocategory SET name = (name || " +
                "' (' || (SELECT MIN(difficulty) FROM heroadventure WHERE subcategory = category_id) || " +
                "'-' || (SELECT MAX(difficulty) FROM heroadventure WHERE subcategory = category_id) || ')')" +
                " WHERE parent > 0");
        Hero_Category.executeQuery("UPDATE herocategory SET name = \"Gathering (10-150)\" WHERE category_id = 1");
        Hero_Category.executeQuery("UPDATE herocategory SET name = \"Animal Hunting (100-250)\" WHERE category_id = 2");
        Hero_Category.executeQuery("UPDATE herocategory SET name = \"Monster Hunting (190-600)\" WHERE category_id = 3");
        Hero_Category.executeQuery("UPDATE herocategory SET name = \"Elite Challenges (700-1500)\" WHERE category_id = 4");
        Hero_Category.executeQuery("UPDATE herocategory SET name = \"Guard Duty (100-300)\" WHERE category_id = 5");
        Hero_Category.executeQuery("UPDATE herocategory SET name = \"Exploring (500-800)\" WHERE category_id = 6");
        Hero_Category.executeQuery("UPDATE herocategory SET name = \"Escort (350-690)\" WHERE category_id = 7");

        List<Visitor_Stats> visitorStats = new ArrayList<>();
        visitorStats.add(new Visitor_Stats(51L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStats.add(new Visitor_Stats(52L, 0, 52L, 1L, 0, 0L, 0L));
        Visitor_Stats.saveInTx(visitorStats);

        List<Visitor_Type> visitorTypes = new ArrayList<>();
        visitorTypes.add(new Visitor_Type(51L, "Mr Tentacles", "Want a burger?", 4L, 27L, 9L, 1.05, 1.35, 1.12, false, false, false, 15));
        visitorTypes.add(new Visitor_Type(52L, "Bresh Bosh", "I's got all these coins but nuffin good. Whatchu got?", 6L, 14L, 3L, 1.39, 1.14, 1.20, false, false, false, 9));
        Visitor_Type.saveInTx(visitorTypes);
    }

    private void patch200to201() {
        new Player_Info("CoinsPurchased", 0).save();
    }


    private void patch201to203() {
        List<Worker_Resource> workerResources = new ArrayList<>();
        workerResources.add(new Worker_Resource(220, 130, 1, 8));
        workerResources.add(new Worker_Resource(221, 131, 1, 8));
        Worker_Resource.saveInTx(workerResources);
    }

    private void patch203to210() {
        List<Item> items = new ArrayList<>();
        items.add(new Item(222L, "Silver amethyst ring", "A rather pretty silver and purple ring.", 24, 8, 385, 50));
        items.add(new Item(223L, "Silver citrine ring", "An intriguing silver and yellow ring.", 24, 8, 385, 50));
        items.add(new Item(224L, "Gold amethyst ring", "A rather pretty gold and purple ring.", 24, 9, 385, 50));
        items.add(new Item(225L, "Gold citrine ring", "An intriguing gold and silver ring.", 24, 9, 385, 50));
        items.add(new Item(226L, "Rare Candy", "Unfortunately won't level you up.", 21, 11, 25, 30));
        items.add(new Item(227L, "Bowl Of Water", "Thirst quenching, and rather wet.", 21, 11, 10, 1));
        items.add(new Item(228L, "Soup", "Hearty and filling!", 27, 11, 25, 10));
        items.add(new Item(229L, "Cereal", "The most important meal of the day, apparently.", 27, 11, 20, 10));
        items.add(new Item(230L, "White Chocolate", "Mmmm, so sweet!", 21, 11, 7, 5));
        items.add(new Item(231L, "Golden Egg", "So pretty! So golden! So eggy!", 27, 11, 20000, 1));
        Item.saveInTx(items);

        List<Recipe> recipes = new ArrayList<>();
        recipes.add(new Recipe(222L, 1L, 17L, 1L, 1));
        recipes.add(new Recipe(222L, 1L, 220L, 1L, 1));
        recipes.add(new Recipe(223L, 1L, 17L, 1L, 1));
        recipes.add(new Recipe(223L, 1L, 221L, 1L, 1));
        recipes.add(new Recipe(224L, 1L, 18L, 1L, 1));
        recipes.add(new Recipe(224L, 1L, 220L, 1L, 1));
        recipes.add(new Recipe(225L, 1L, 18L, 1L, 1));
        recipes.add(new Recipe(225L, 1L, 221L, 1L, 1));

        recipes.add(new Recipe(228L, 1L, 227L, 1L, 1));
        recipes.add(new Recipe(228L, 1L, 214L, 1L, 1));
        recipes.add(new Recipe(229L, 1L, 227L, 1L, 1));
        recipes.add(new Recipe(229L, 1L, 202L, 1L, 1));

        recipes.add(new Recipe(231L, 1L, 203L, 1L, 100));
        recipes.add(new Recipe(231L, 1L, 203L, 3L, 10));
        recipes.add(new Recipe(231L, 1L, 203L, 4L, 10));
        recipes.add(new Recipe(231L, 1L, 203L, 5L, 10));
        recipes.add(new Recipe(231L, 1L, 203L, 6L, 10));
        recipes.add(new Recipe(231L, 1L, 203L, 7L, 10));
        recipes.add(new Recipe(231L, 1L, 203L, 8L, 10));
        recipes.add(new Recipe(231L, 1L, 203L, 9L, 10));
        Recipe.saveInTx(recipes);

        List<Worker_Resource> workerResources = new ArrayList<>();
        workerResources.add(new Worker_Resource(222, 9, 1, 6)); // Silver Ore
        workerResources.add(new Worker_Resource(222, 17, 1, 6)); // Silver Bar
        workerResources.add(new Worker_Resource(222, 220, 1, 2)); // Amethyst
        workerResources.add(new Worker_Resource(223, 9, 1, 6)); // Silver Ore
        workerResources.add(new Worker_Resource(223, 17, 1, 6)); // Silver Bar
        workerResources.add(new Worker_Resource(223, 221, 1, 2)); // Citrine
        workerResources.add(new Worker_Resource(224, 8, 1, 6)); // Gold Ore
        workerResources.add(new Worker_Resource(224, 18, 1, 6)); // Gold Bar
        workerResources.add(new Worker_Resource(224, 220, 1, 2)); // Amethyst
        workerResources.add(new Worker_Resource(225, 8, 1, 6)); // Gold Ore
        workerResources.add(new Worker_Resource(225, 18, 1, 6)); // Gold Bar
        workerResources.add(new Worker_Resource(225, 221, 1, 2)); // Citrine
        Worker_Resource.saveInTx(workerResources);

        List<Super_Upgrade> superUpgrades = new ArrayList<>();
        superUpgrades.add(new Super_Upgrade(Constants.SU_BUY_ALL_MARKET, "Buy All From Market", 2, false));
        superUpgrades.add(new Super_Upgrade(Constants.SU_HALVE_TIMES, "Halve All Times", 4, false));
        Super_Upgrade.saveInTx(superUpgrades);

        List<Setting> settings = new ArrayList<>();
        settings.add(new Setting(Constants.SETTING_LANGUAGE, "Language", LanguageHelper.getDefaultLanguage()));
        settings.add(new Setting(Constants.SETTING_ASSISTANT_NOTIFICATIONS, "AssistantNotifications", true));
        Setting.saveInTx(settings);

        List<Player_Info> statistics = new ArrayList<>();
        statistics.add(new Player_Info("ActiveAssistant", 0));
        statistics.add(new Player_Info("LastAssistantClaim", 0L));
        statistics.add(new Player_Info("TotalAssistantClaims", 0));
        Player_Info.saveInTx(statistics);

        new Visitor_Type(53L, "Easter Bunny", "Here to spread Easter joy!", 11L, 27L, 1L, 1.24, 1.24, 1.24, false, false, false, 40).save();
        new Visitor_Stats(53L, 0, 52L, 1L, 0, 0L, 0L).save();

        createAssistants();
    }

    private void patch212to213() {
        Visitor_Type.executeQuery("UPDATE visitortype SET weighting = 2 WHERE visitor_id = 53");
        Assistant.executeQuery("UPDATE assistant SET reward_quantity = (reward_quantity / 3), reward_frequency = (reward_frequency * 2)");
    }

    private void patch214to220() {
        Trader.executeQuery("DELETE FROM trader WHERE shopkeeper = 0");

        List<Visitor_Stats> visitorStats = new ArrayList<>();
        visitorStats.add(new Visitor_Stats(54L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStats.add(new Visitor_Stats(55L, 0, 52L, 1L, 0, 0L, 0L));
        Visitor_Stats.saveInTx(visitorStats);

        List<Visitor_Type> visitorTypes = new ArrayList<>();
        visitorTypes.add(new Visitor_Type(54L, "Blinky", "I love my brother!", 7L, 18L, 3L, 1.15, 1.07, 1.16, false, false, false, 6));
        visitorTypes.add(new Visitor_Type(55L, "Sparky", "I hate my brother!", 7L, 18L, 4L, 1.15, 1.07, 1.16, false, false, false, 6));
        Visitor_Type.saveInTx(visitorTypes);
    }

    private void patch220to230() {
        new Visitor_Type(56L, "Father Christmas", "Ho ho ho, everyone!", 11L, 1L, 3L, 1.24, 1.24, 1.24, false, false, false, 40).save();
        new Visitor_Stats(56L, 0, 52L, 1L, 0, 0L, 0L).save();

        List<Item> items = new ArrayList<>();
        items.add(new Item(232L, "Present", "Only for good boys and girls!", 23, 11, 1, 1));
        items.add(new Item(233L, "Goody Sack", "Full of so many presents!", 27, 11, 21012, 1));
        Item.saveInTx(items);

        List<Recipe> recipes = new ArrayList<>();
        recipes.add(new Recipe(233L, 1L, 232L, 1L, 100));
        recipes.add(new Recipe(233L, 1L, 232L, 3L, 10));
        recipes.add(new Recipe(233L, 1L, 232L, 4L, 10));
        recipes.add(new Recipe(233L, 1L, 232L, 5L, 10));
        recipes.add(new Recipe(233L, 1L, 232L, 6L, 10));
        recipes.add(new Recipe(233L, 1L, 232L, 7L, 10));
        recipes.add(new Recipe(233L, 1L, 232L, 8L, 10));
        recipes.add(new Recipe(233L, 1L, 232L, 9L, 10));
        Recipe.saveInTx(recipes);

        new Setting(Constants.SETTING_SEASONAL_EFFECTS, "SeasonalEffects", true).save();
    }

    private void patch233to234() {
        Recipe recipe = Select.from(Recipe.class).where(
                Condition.prop("item").eq(233),
                Condition.prop("ingredient").eq(232),
                Condition.prop("quantity").eq(100)
        ).first();
        if (recipe != null) {
            recipe.delete();
        }
    }

    private void patch234to235() {
        Visitor_Type santaVisitor = Select.from(Visitor_Type.class)
                .where(Condition.prop("visitor_id").eq(56)).first();

        if (santaVisitor != null) {
            santaVisitor.setWeighting(2);
            santaVisitor.save();
        }
    }

    private void patch235to236() {
        Visitor_Type santaVisitor = Select.from(Visitor_Type.class).where(Condition.prop("visitor_id").eq(56)).first();
        if (santaVisitor != null) {
            santaVisitor.setWeighting(2);
            santaVisitor.save();
        }

        Item present = Select.from(Item.class).where(Condition.prop("id").eq(233)).first();
        if (present != null) {
            present.setValue(300);
            present.save();
        }
    }

    private void createAssistants() {
        List<Assistant> assistants = new ArrayList<>();
        assistants.add(new Assistant(1, 5, 1000, 0.1, 19, 0, 70, 1, 100, 600000, 0.01));
        assistants.add(new Assistant(2, 12, 5000, 0.07, 19, 0, 71, 1, 220, 600000, 0.02));
        assistants.add(new Assistant(3, 20, 12000, 0.05, 19, 0, 52, 1, 3000, 900000, 0.03));
        assistants.add(new Assistant(4, 27, 20000, 0.02, 19, 0, 75, 1, 30, 2100000, 0.04));
        assistants.add(new Assistant(5, 33, 35000, 0.01, 19, 0, 148, 1, 50, 3600000, 0.06));
        assistants.add(new Assistant(6, 45, 60000, 0.008, 19, 0, 52, 1, 30000, 3600000, 0.07));
        Assistant.saveInTx(assistants);
    }

    private void createContributionGoals() {
        List<Contribution_Goal> goals = new ArrayList<>();
        goals.add(new Contribution_Goal(1, "Thank You", 1, "Get an extra little thank you.\n", "Thanks! Your contributions are what make further development on Pixel Blacksmith possible!\n"));
        goals.add(new Contribution_Goal(2, "Dev Queue", 3, "Get access to the Trello board used to plan / prioritise new features, changes, and bug fixes.", "<a href=\"https://trello.com/b/Zw01amFA/\">Trello board.</a> Under each Trello category, the higher an item is, the higher priority it is, and the sooner it'll be worked on. Items are also tagged with the release (e.g. 1.7.0) it will be in."));
        goals.add(new Contribution_Goal(3, "Beta & Credits", 6, "Get beta access to all releases at least 24hr in advance, and get your name / username in the credits.", "Hey, send an email to <a href=\"mailto:i.am.now.a.beta.player@jakelee.co.uk\">the beta address</a> and I'll get you added to the beta testing / credits!"));
        goals.add(new Contribution_Goal(4, "Create Quest", 10, "Create a quest and quest reward for a hero to be added to the game.", "Woo! Try and make your idea fit into an existing category, and email it to <a href=\"mailto:i.make.the.quests@jakelee.co.uk\">the quest address</a>, and I'll put it in the game."));
        goals.add(new Contribution_Goal(5, "Create Item", 18, "Create an item (e.g. weapon, armour, ingredient) to be added to the game.", "Hey! Email your item name, description, ingredients, and purpose to <a href=\"mailto:hi.i.am.an.item.creator@jakelee.co.uk@jakelee.co.uk\">the item address</a>. We might have to make some changes to balance the item though!"));
        goals.add(new Contribution_Goal(6, "Create Visitor", 24, "Create a visitor, along with preferences.", "You're a big fan of contributing, huh? Email a visitor name, description, and preferences to <a href=\"mailto:i.make.people.happen@jakelee.co.uk\">the visitor address</a>, and we'll discuss the visitor's appearance etc."));
        goals.add(new Contribution_Goal(7, "Create Feature", 38, "Suggest an entirely new feature (within reason!) for the game.", "Wow, that's impressive..! Send me an email to <a href=\"mailto:i.basically.make.the.game@jakelee.co.uk\">the feature address</a>, and we'll work together to make the feature reality."));
        Contribution_Goal.saveInTx(goals);
    }

    private void createSuperUpgrade() {
        List<Super_Upgrade> upgrades = new ArrayList<>();
        upgrades.add(new Super_Upgrade(Constants.SU_CONTRIBUTIONS, "100x Contribution Reward", 0, false));
        upgrades.add(new Super_Upgrade(Constants.SU_MARKET_RESTOCK, "Free Market Restock", 0, false));
        upgrades.add(new Super_Upgrade(Constants.SU_DOUBLE_FURNACE_CRAFTS, "2x Crafted Items", 1, false));
        upgrades.add(new Super_Upgrade(Constants.SU_WORKER_RESOURCES, "5x Worker Resources", 1, false));
        upgrades.add(new Super_Upgrade(Constants.SU_PAGE_CHANCE, "Guaranteed Pages", 2, false));
        upgrades.add(new Super_Upgrade(Constants.SU_HALF_BONUS_CHEST, "-50% Bonus Chest Time", 2, false));
        upgrades.add(new Super_Upgrade(Constants.SU_SINGLE_DEMAND, "1 Demand Per Visitor", 3, false));
        upgrades.add(new Super_Upgrade(Constants.SU_HALF_MARKET_COST, "-50% Market Buy Cost", 4, false));
        upgrades.add(new Super_Upgrade(Constants.SU_HALF_WORKER_TIME, "-50% Worker Time", 5, false));
        upgrades.add(new Super_Upgrade(Constants.SU_DOUBLE_TRADE_PRICE, "2x Trade Price", 5, false));
        upgrades.add(new Super_Upgrade(Constants.SU_TRADER_STOCK, "2x Trader Items Purchased", 5, false));
        upgrades.add(new Super_Upgrade(Constants.SU_BONUS_XP, "2x All XP", 6, false));
        upgrades.add(new Super_Upgrade(Constants.SU_BONUS_GOLD, "2x Coin Earnings", 6, false));
        upgrades.add(new Super_Upgrade(Constants.SU_QUEST_MED, "All Quests Medium+", 0, false));
        upgrades.add(new Super_Upgrade(Constants.SU_QUEST_HARD, "All Quests Hard+", 2, false));
        upgrades.add(new Super_Upgrade(Constants.SU_QUEST_ELITE, "All Quests Elite", 3, false));
        Super_Upgrade.saveInTx(upgrades);
    }

    private void createHero() {
        List<Hero> heroes = new ArrayList<>();
        heroes.add(new Hero(1, 5));
        heroes.add(new Hero(2, 25));
        heroes.add(new Hero(3, 35));
        heroes.add(new Hero(4, 45));
        heroes.add(new Hero(5, 55));
        heroes.add(new Hero(6, 65));
        Hero.saveInTx(heroes);

        List<Hero_Category> heroCategories = new ArrayList<>();
        List<Hero_Adventure> heroAdventures = new ArrayList<>();
        List<Hero_Resource> heroResources = new ArrayList<>();

        // Gathering: 1 - 150 difficulty.
        heroCategories.add(new Hero_Category(1, "Gathering", 0));
        heroCategories.add(new Hero_Category(11, "Gather Plants", 1));
        heroAdventures.add(new Hero_Adventure(111, 11, "Gather Red Weed", 10));
        heroResources.add(new Hero_Resource(111, 20, Constants.STATE_NORMAL, 5));
        heroAdventures.add(new Hero_Adventure(112, 11, "Gather Nirnroot Herbs", 20));
        heroResources.add(new Hero_Resource(112, 23, Constants.STATE_NORMAL, 5));
        heroAdventures.add(new Hero_Adventure(113, 11, "Gather Kyrt Herbs", 30));
        heroResources.add(new Hero_Resource(113, 27, Constants.STATE_NORMAL, 5));

        heroCategories.add(new Hero_Category(12, "Gather Insects", 1));
        heroAdventures.add(new Hero_Adventure(121, 12, "Gather Ants", 25));
        heroResources.add(new Hero_Resource(121, 37, Constants.STATE_NORMAL, 5));
        heroAdventures.add(new Hero_Adventure(122, 12, "Gather Bees", 35));
        heroResources.add(new Hero_Resource(122, 40, Constants.STATE_NORMAL, 5));
        heroAdventures.add(new Hero_Adventure(123, 12, "Gather Stick Insects", 45));
        heroResources.add(new Hero_Resource(123, 42, Constants.STATE_NORMAL, 5));
        heroAdventures.add(new Hero_Adventure(124, 12, "Gather Hercules Beetles", 45));
        heroResources.add(new Hero_Resource(124, 50, Constants.STATE_NORMAL, 5));
        heroAdventures.add(new Hero_Adventure(125, 12, "Gather Praying Mantis", 95));
        heroResources.add(new Hero_Resource(125, 43, Constants.STATE_NORMAL, 5));

        heroCategories.add(new Hero_Category(13, "Gather Bones", 1));
        heroAdventures.add(new Hero_Adventure(131, 13, "Gather Bird Bones", 25));
        heroResources.add(new Hero_Resource(131, 55, Constants.STATE_NORMAL, 5));
        heroAdventures.add(new Hero_Adventure(132, 13, "Gather Rat Bones", 35));
        heroResources.add(new Hero_Resource(132, 57, Constants.STATE_NORMAL, 5));
        heroAdventures.add(new Hero_Adventure(133, 13, "Gather Fish Bones", 45));
        heroResources.add(new Hero_Resource(133, 58, Constants.STATE_NORMAL, 5));
        heroAdventures.add(new Hero_Adventure(134, 13, "Gather Monkey Bones", 55));
        heroResources.add(new Hero_Resource(134, 61, Constants.STATE_NORMAL, 5));
        heroAdventures.add(new Hero_Adventure(135, 13, "Gather Shark Bones", 65));
        heroResources.add(new Hero_Resource(135, 62, Constants.STATE_NORMAL, 5));

        heroCategories.add(new Hero_Category(14, "Gather Ore", 1));
        heroAdventures.add(new Hero_Adventure(141, 14, "Gather Coal", 15));
        heroResources.add(new Hero_Resource(141, 3, Constants.STATE_NORMAL, 10));
        heroAdventures.add(new Hero_Adventure(142, 14, "Gather Iron Ore", 25));
        heroResources.add(new Hero_Resource(142, 4, Constants.STATE_NORMAL, 10));
        heroAdventures.add(new Hero_Adventure(143, 14, "Gather Silver Nuggets", 35));
        heroResources.add(new Hero_Resource(143, 9, Constants.STATE_NORMAL, 10));
        heroAdventures.add(new Hero_Adventure(144, 14, "Gather Gold Nuggets", 45));
        heroResources.add(new Hero_Resource(144, 8, Constants.STATE_NORMAL, 10));
        heroAdventures.add(new Hero_Adventure(145, 14, "Gather Mithril Ore", 65));
        heroResources.add(new Hero_Resource(145, 5, Constants.STATE_NORMAL, 10));
        heroAdventures.add(new Hero_Adventure(146, 14, "Gather Adamantite Ore", 85));
        heroResources.add(new Hero_Resource(146, 6, Constants.STATE_NORMAL, 10));
        heroAdventures.add(new Hero_Adventure(147, 14, "Gather Runite Ore", 105));
        heroResources.add(new Hero_Resource(147, 7, Constants.STATE_NORMAL, 10));
        heroAdventures.add(new Hero_Adventure(148, 14, "Gather Dragonite Ore", 125));
        heroResources.add(new Hero_Resource(148, 10, Constants.STATE_NORMAL, 10));

        heroCategories.add(new Hero_Category(15, "Gather Corpses", 1));
        heroAdventures.add(new Hero_Adventure(151, 15, "Gather Mice Corpses", 50));
        heroResources.add(new Hero_Resource(151, 78, Constants.STATE_NORMAL, 15));
        heroResources.add(new Hero_Resource(151, 217, Constants.STATE_NORMAL, 1));
        heroAdventures.add(new Hero_Adventure(152, 15, "Gather Wolf Corpses", 75));
        heroResources.add(new Hero_Resource(152, 80, Constants.STATE_NORMAL, 5));
        heroResources.add(new Hero_Resource(152, 70, Constants.STATE_NORMAL, 3));
        heroAdventures.add(new Hero_Adventure(153, 15, "Gather Human Corpses", 100));
        heroResources.add(new Hero_Resource(153, 80, Constants.STATE_NORMAL, 10));
        heroAdventures.add(new Hero_Adventure(154, 15, "Gather Giant Corpses", 125));
        heroResources.add(new Hero_Resource(154, 80, Constants.STATE_NORMAL, 15));
        heroAdventures.add(new Hero_Adventure(155, 15, "Gather Monster Corpses", 150));
        heroResources.add(new Hero_Resource(155, 80, Constants.STATE_NORMAL, 10));
        heroResources.add(new Hero_Resource(155, 130, Constants.STATE_NORMAL, 3));

        heroCategories.add(new Hero_Category(16, "Gather Buried Treasure", 1));
        heroAdventures.add(new Hero_Adventure(161, 16, "Gather Buried Coins", 130));
        heroResources.add(new Hero_Resource(161, 52, Constants.STATE_NORMAL, 275));
        heroAdventures.add(new Hero_Adventure(162, 16, "Gather Buried Gems", 140));
        heroResources.add(new Hero_Resource(162, 129, Constants.STATE_NORMAL, 2));
        heroResources.add(new Hero_Resource(162, 130, Constants.STATE_NORMAL, 2));
        heroResources.add(new Hero_Resource(162, 131, Constants.STATE_NORMAL, 2));
        heroAdventures.add(new Hero_Adventure(163, 16, "Gather Buried Chests", 150));
        heroResources.add(new Hero_Resource(163, 129, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(163, 130, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(163, 52, Constants.STATE_NORMAL, 225));

        // Animal Hunting: 100 - 250 difficulty.
        heroCategories.add(new Hero_Category(2, "Animal Hunting", 0));
        heroCategories.add(new Hero_Category(21, "Fly Hunting", 2));
        heroAdventures.add(new Hero_Adventure(211, 21, "Hunt Fruit Flies", 100));
        heroResources.add(new Hero_Resource(211, 77, Constants.STATE_NORMAL, 2));
        heroResources.add(new Hero_Resource(211, 206, Constants.STATE_NORMAL, 2));
        heroResources.add(new Hero_Resource(211, 207, Constants.STATE_NORMAL, 2));
        heroAdventures.add(new Hero_Adventure(212, 21, "Hunt Blue Flies", 120));
        heroResources.add(new Hero_Resource(212, 16, Constants.STATE_NORMAL, 3));
        heroAdventures.add(new Hero_Adventure(213, 21, "Hunt Mutant Flies", 150));
        heroResources.add(new Hero_Resource(213, 80, Constants.STATE_NORMAL, 8));
        heroResources.add(new Hero_Resource(213, 214, Constants.STATE_NORMAL, 8));
        heroAdventures.add(new Hero_Adventure(214, 21, "Hunt Vampire Flies", 180));
        heroResources.add(new Hero_Resource(214, 80, Constants.STATE_NORMAL, 8));
        heroResources.add(new Hero_Resource(214, 80, Constants.STATE_NORMAL, 8));
        heroAdventures.add(new Hero_Adventure(215, 21, "Hunt Scavenger Flies", 220));
        heroResources.add(new Hero_Resource(215, 155, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(215, 113, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(215, 73, Constants.STATE_NORMAL, 1));

        heroCategories.add(new Hero_Category(22, "Slug Hunting", 2));
        heroAdventures.add(new Hero_Adventure(221, 22, "Hunt Slow Slugs", 130));
        heroResources.add(new Hero_Resource(221, 204, Constants.STATE_NORMAL, 3));
        heroAdventures.add(new Hero_Adventure(222, 22, "Hunt Sticky Slugs", 150));
        heroResources.add(new Hero_Resource(222, 204, Constants.STATE_NORMAL, 3));
        heroResources.add(new Hero_Resource(222, 79, Constants.STATE_NORMAL, 3));
        heroAdventures.add(new Hero_Adventure(223, 22, "Hunt Fancy Slugs", 170));
        heroResources.add(new Hero_Resource(223, 204, Constants.STATE_NORMAL, 3));
        heroResources.add(new Hero_Resource(223, 158, Constants.STATE_NORMAL, 1));
        heroAdventures.add(new Hero_Adventure(224, 22, "Hunt Deadly Slugs", 190));
        heroResources.add(new Hero_Resource(224, 204, Constants.STATE_NORMAL, 3));
        heroResources.add(new Hero_Resource(224, 114, 4, 2));

        heroCategories.add(new Hero_Category(23, "Spider Hunting", 2));
        heroAdventures.add(new Hero_Adventure(231, 23, "Hunt Baby Spiders", 110));
        heroResources.add(new Hero_Resource(231, 69, Constants.STATE_NORMAL, 13));
        heroAdventures.add(new Hero_Adventure(232, 23, "Hunt Daddy Long Legs Spiders", 140));
        heroResources.add(new Hero_Resource(232, 69, Constants.STATE_NORMAL, 4));
        heroResources.add(new Hero_Resource(232, 70, Constants.STATE_NORMAL, 9));
        heroAdventures.add(new Hero_Adventure(232, 23, "Hunt Tarantula Spiders", 190));
        heroResources.add(new Hero_Resource(233, 69, Constants.STATE_NORMAL, 9));
        heroResources.add(new Hero_Resource(233, 70, Constants.STATE_NORMAL, 4));
        heroAdventures.add(new Hero_Adventure(233, 23, "Hunt Black Widow Spiders", 250));
        heroResources.add(new Hero_Resource(231, 70, Constants.STATE_NORMAL, 13));

        heroCategories.add(new Hero_Category(24, "Frog Hunting", 2));
        heroAdventures.add(new Hero_Adventure(241, 24, "Hunt Baby Frogs", 110));
        heroResources.add(new Hero_Resource(241, 24, Constants.STATE_NORMAL, 3));
        heroResources.add(new Hero_Resource(241, 25, Constants.STATE_NORMAL, 3));
        heroAdventures.add(new Hero_Adventure(242, 24, "Hunt Jumping Frogs", 140));
        heroResources.add(new Hero_Resource(242, 57, Constants.STATE_NORMAL, 3));
        heroResources.add(new Hero_Resource(242, 58, Constants.STATE_NORMAL, 3));
        heroAdventures.add(new Hero_Adventure(243, 24, "Hunt Skermit The Frogs", 170));
        heroResources.add(new Hero_Resource(243, 85, Constants.STATE_NORMAL, 3));
        heroResources.add(new Hero_Resource(243, 86, Constants.STATE_NORMAL, 3));
        heroAdventures.add(new Hero_Adventure(244, 24, "Hunt Mr. Frogs", 210));
        heroResources.add(new Hero_Resource(244, 101, Constants.STATE_NORMAL, 3));
        heroResources.add(new Hero_Resource(244, 102, Constants.STATE_NORMAL, 3));
        heroAdventures.add(new Hero_Adventure(245, 24, "Hunt Hypnofrogs", 250));
        heroResources.add(new Hero_Resource(245, 117, Constants.STATE_NORMAL, 3));
        heroResources.add(new Hero_Resource(245, 118, Constants.STATE_NORMAL, 3));

        heroCategories.add(new Hero_Category(25, "Rabbit Hunting", 2));
        heroAdventures.add(new Hero_Adventure(251, 25, "Hunt White Rabbits", 100));
        heroAdventures.add(new Hero_Adventure(252, 25, "Hunt Dwarf Rabbits", 130));
        heroAdventures.add(new Hero_Adventure(253, 25, "Hunt Wooly Rabbits", 170));
        heroAdventures.add(new Hero_Adventure(254, 25, "Hunt Wabbits", 220));

        heroCategories.add(new Hero_Category(26, "Hellcat Hunting", 2));
        heroAdventures.add(new Hero_Adventure(261, 26, "Hunt Blue Hellcats", 210));
        heroResources.add(new Hero_Resource(261, 73, Constants.STATE_NORMAL, 3));
        heroAdventures.add(new Hero_Adventure(262, 26, "Hunt Green Hellcats", 220));
        heroResources.add(new Hero_Resource(262, 74, Constants.STATE_NORMAL, 3));
        heroAdventures.add(new Hero_Adventure(263, 26, "Hunt Red Hellcats", 230));
        heroResources.add(new Hero_Resource(263, 72, Constants.STATE_NORMAL, 3));
        heroAdventures.add(new Hero_Adventure(264, 26, "Hunt White Hellcats", 240));
        heroResources.add(new Hero_Resource(264, 75, Constants.STATE_NORMAL, 3));
        heroAdventures.add(new Hero_Adventure(265, 26, "Hunt Black Hellcats", 250));
        heroResources.add(new Hero_Resource(265, 76, Constants.STATE_NORMAL, 3));

        heroCategories.add(new Hero_Category(27, "Wolf Hunting", 2));
        heroAdventures.add(new Hero_Adventure(271, 27, "Hunt White Wolves", 160));
        heroResources.add(new Hero_Resource(271, 80, Constants.STATE_NORMAL, 10));
        heroResources.add(new Hero_Resource(271, 17, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(271, 56, 6, 3));
        heroAdventures.add(new Hero_Adventure(272, 27, "Hunt Wargs", 180));
        heroResources.add(new Hero_Resource(272, 80, Constants.STATE_NORMAL, 10));
        heroResources.add(new Hero_Resource(272, 17, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(272, 58, 6, 3));
        heroAdventures.add(new Hero_Adventure(273, 27, "Hunt Big Bad Wolves", 200));
        heroResources.add(new Hero_Resource(273, 80, Constants.STATE_NORMAL, 10));
        heroResources.add(new Hero_Resource(273, 17, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(273, 63, 6, 3));
        heroAdventures.add(new Hero_Adventure(274, 27, "Hunt Direwolves", 220));
        heroResources.add(new Hero_Resource(274, 80, Constants.STATE_NORMAL, 10));
        heroResources.add(new Hero_Resource(274, 17, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(274, 68, 6, 3));
        heroAdventures.add(new Hero_Adventure(275, 27, "Hunt Werewolves", 240));
        heroResources.add(new Hero_Resource(275, 80, Constants.STATE_NORMAL, 10));
        heroResources.add(new Hero_Resource(275, 17, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(275, 60, 6, 3));

        heroCategories.add(new Hero_Category(28, "Bear Hunting", 2));
        heroAdventures.add(new Hero_Adventure(281, 28, "Hunt Bobo The Bears", 100));
        heroResources.add(new Hero_Resource(281, 80, Constants.STATE_NORMAL, 4));
        heroResources.add(new Hero_Resource(281, 211, Constants.STATE_NORMAL, 4));
        heroAdventures.add(new Hero_Adventure(282, 28, "Hunt Panda Bears", 140));
        heroResources.add(new Hero_Resource(282, 80, Constants.STATE_NORMAL, 6));
        heroResources.add(new Hero_Resource(282, 211, Constants.STATE_NORMAL, 6));
        heroAdventures.add(new Hero_Adventure(283, 28, "Hunt Pom Bears", 180));
        heroResources.add(new Hero_Resource(283, 80, Constants.STATE_NORMAL, 9));
        heroResources.add(new Hero_Resource(283, 211, Constants.STATE_NORMAL, 9));
        heroAdventures.add(new Hero_Adventure(284, 28, "Hunt Black Bears", 220));
        heroResources.add(new Hero_Resource(284, 80, Constants.STATE_NORMAL, 14));
        heroResources.add(new Hero_Resource(284, 211, Constants.STATE_NORMAL, 14));
        heroAdventures.add(new Hero_Adventure(285, 28, "Hunt Grizzly Bears", 250));
        heroResources.add(new Hero_Resource(285, 80, Constants.STATE_NORMAL, 21));
        heroResources.add(new Hero_Resource(285, 211, Constants.STATE_NORMAL, 21));

        heroCategories.add(new Hero_Category(29, "Tiger Hunting", 2));
        heroAdventures.add(new Hero_Adventure(291, 29, "Hunt Tiggers", 100));
        heroResources.add(new Hero_Resource(291, 1, Constants.STATE_NORMAL, 7));
        heroResources.add(new Hero_Resource(291, 3, Constants.STATE_NORMAL, 7));
        heroAdventures.add(new Hero_Adventure(292, 29, "Hunt Young Tigers", 110));
        heroResources.add(new Hero_Resource(292, 1, Constants.STATE_NORMAL, 13));
        heroResources.add(new Hero_Resource(292, 3, Constants.STATE_NORMAL, 13));
        heroAdventures.add(new Hero_Adventure(293, 29, "Hunt Gladiatorial Tigers", 170));
        heroResources.add(new Hero_Resource(293, 1, Constants.STATE_NORMAL, 22));
        heroResources.add(new Hero_Resource(293, 3, Constants.STATE_NORMAL, 22));
        heroAdventures.add(new Hero_Adventure(294, 29, "Hunt Phony Tigers", 180));
        heroResources.add(new Hero_Resource(294, 1, Constants.STATE_NORMAL, 30));
        heroResources.add(new Hero_Resource(294, 3, Constants.STATE_NORMAL, 30));
        heroAdventures.add(new Hero_Adventure(295, 29, "Hunt Shere Khan", 240));
        heroResources.add(new Hero_Resource(295, 1, Constants.STATE_NORMAL, 41));
        heroResources.add(new Hero_Resource(295, 3, Constants.STATE_NORMAL, 41));

        // Monster Hunting: 200 - 600 difficulty.
        heroCategories.add(new Hero_Category(3, "Monster Hunting", 0));
        heroCategories.add(new Hero_Category(31, "Ghost Hunting", 3));
        heroAdventures.add(new Hero_Adventure(311, 31, "Hunt Poltergeists", 220));
        heroResources.add(new Hero_Resource(311, 192, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(311, 54, 6, 1));
        heroAdventures.add(new Hero_Adventure(312, 31, "Hunt Victorian Ghosts", 280));
        heroResources.add(new Hero_Resource(312, 192, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(312, 63, 6, 1));
        heroAdventures.add(new Hero_Adventure(313, 31, "Hunt Bloody Barons", 340));
        heroResources.add(new Hero_Resource(313, 192, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(313, 67, 6, 1));
        heroAdventures.add(new Hero_Adventure(314, 31, "Hunt Black Knights", 400));
        heroResources.add(new Hero_Resource(314, 192, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(314, 68, 6, 1));

        heroCategories.add(new Hero_Category(32, "Vampire Hunting", 3));
        heroAdventures.add(new Hero_Adventure(321, 32, "Hunt Vampire Bats", 310));
        heroResources.add(new Hero_Resource(321, 178, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(321, 40, 3, 1));
        heroAdventures.add(new Hero_Adventure(322, 32, "Hunt Vampire Counts", 350));
        heroResources.add(new Hero_Resource(322, 178, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(322, 44, 3, 1));
        heroAdventures.add(new Hero_Adventure(323, 32, "Hunt Draugrs", 390));
        heroResources.add(new Hero_Resource(323, 178, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(323, 49, 3, 1));

        heroCategories.add(new Hero_Category(33, "Sea Monster Hunting", 3));
        heroAdventures.add(new Hero_Adventure(331, 33, "Hunt Loch Ness Monsters", 500));
        heroResources.add(new Hero_Resource(331, 184, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(331, 115, 4, 1));
        heroAdventures.add(new Hero_Adventure(332, 33, "Hunt Leviathans", 600));
        heroResources.add(new Hero_Resource(332, 184, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(332, 118, 4, 1));
        heroAdventures.add(new Hero_Adventure(333, 33, "Hunt Elder Gods", 600));
        heroResources.add(new Hero_Resource(333, 184, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(333, 126, 4, 1));

        heroCategories.add(new Hero_Category(34, "Alien Hunting", 3));
        heroAdventures.add(new Hero_Adventure(341, 34, "Hunt Soft Ones", 190));
        heroResources.add(new Hero_Resource(341, 190, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(341, 82, 7, 1));
        heroAdventures.add(new Hero_Adventure(342, 34, "Hunt Reapers", 290));
        heroResources.add(new Hero_Resource(342, 190, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(342, 93, 7, 1));
        heroAdventures.add(new Hero_Adventure(343, 34, "Hunt Daleks", 390));
        heroResources.add(new Hero_Resource(343, 190, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(343, 96, 7, 1));
        heroAdventures.add(new Hero_Adventure(344, 34, "Hunt Xenomorphs", 490));
        heroResources.add(new Hero_Resource(344, 190, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(344, 81, 7, 1));
        heroAdventures.add(new Hero_Adventure(345, 34, "Hunt Moties", 590));
        heroResources.add(new Hero_Resource(345, 190, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(345, 99, 7, 1));

        heroCategories.add(new Hero_Category(35, "Zombie Hunting", 3));
        heroAdventures.add(new Hero_Adventure(351, 35, "Hunt Infected", 400));
        heroResources.add(new Hero_Resource(351, 182, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(351, 100, 5, 1));
        heroAdventures.add(new Hero_Adventure(352, 35, "Hunt Crawlers", 440));
        heroResources.add(new Hero_Resource(352, 182, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(352, 107, 5, 1));
        heroAdventures.add(new Hero_Adventure(353, 35, "Hunt Runners", 480));
        heroResources.add(new Hero_Resource(353, 182, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(353, 110, 5, 1));
        heroAdventures.add(new Hero_Adventure(354, 35, "Hunt Spitters", 520));
        heroResources.add(new Hero_Resource(354, 182, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(354, 98, 5, 1));
        heroAdventures.add(new Hero_Adventure(355, 35, "Hunt Boomers", 600));
        heroResources.add(new Hero_Resource(355, 182, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(355, 103, 5, 1));

        // Elite Challenges: 700 - 1500 difficulty.
        heroCategories.add(new Hero_Category(4, "Elite Challenges", 0));
        heroCategories.add(new Hero_Category(41, "Bosses", 4));
        heroAdventures.add(new Hero_Adventure(411, 41, "Kill Big Smoke", 700));
        heroResources.add(new Hero_Resource(411, 100, 5, 3));
        heroResources.add(new Hero_Resource(411, 116, 5, 3));
        heroResources.add(new Hero_Resource(411, 135, 5, 3));
        heroAdventures.add(new Hero_Adventure(412, 41, "Kill Big Daddy", 740));
        heroResources.add(new Hero_Resource(412, 38, 7, 3));
        heroResources.add(new Hero_Resource(412, 45, 7, 3));
        heroResources.add(new Hero_Resource(412, 51, 7, 3));
        heroAdventures.add(new Hero_Adventure(413, 41, "Kill Olmec", 780));
        heroResources.add(new Hero_Resource(413, 18, Constants.STATE_NORMAL, 25));
        heroResources.add(new Hero_Resource(413, 72, Constants.STATE_NORMAL, 5));
        heroResources.add(new Hero_Resource(413, 141, Constants.STATE_NORMAL, 10));
        heroAdventures.add(new Hero_Adventure(414, 41, "Kill Handsome Jack", 820));
        heroResources.add(new Hero_Resource(414, 52, Constants.STATE_NORMAL, 5000));
        heroResources.add(new Hero_Resource(414, 8, Constants.STATE_NORMAL, 35));
        heroResources.add(new Hero_Resource(414, 18, Constants.STATE_NORMAL, 35));
        heroAdventures.add(new Hero_Adventure(415, 41, "Kill The Warrior", 860));
        heroResources.add(new Hero_Resource(415, 80, Constants.STATE_NORMAL, 35));
        heroResources.add(new Hero_Resource(415, 132, Constants.STATE_NORMAL, 15));
        heroResources.add(new Hero_Resource(415, 113, Constants.STATE_NORMAL, 15));
        heroAdventures.add(new Hero_Adventure(416, 41, "Kill Nihilanth", 900));
        heroResources.add(new Hero_Resource(416, 148, Constants.STATE_NORMAL, 3));
        heroResources.add(new Hero_Resource(416, 214, Constants.STATE_NORMAL, 30));
        heroAdventures.add(new Hero_Adventure(417, 41, "Kill GLaDOS", 940));
        heroResources.add(new Hero_Resource(416, 192, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(416, 149, Constants.STATE_NORMAL, 10));
        heroResources.add(new Hero_Resource(416, 60, 6, 7));
        heroAdventures.add(new Hero_Adventure(418, 41, "Kill Netherlord", 980));
        heroResources.add(new Hero_Resource(418, 72, Constants.STATE_NORMAL, 10));
        heroResources.add(new Hero_Resource(418, 52, Constants.STATE_NORMAL, 6500));
        heroResources.add(new Hero_Resource(418, 133, 3, 5));
        heroAdventures.add(new Hero_Adventure(419, 41, "Kill Doviculus", 1020));
        heroResources.add(new Hero_Resource(419, 51, Constants.STATE_NORMAL, 100));
        heroResources.add(new Hero_Resource(419, 38, Constants.STATE_NORMAL, 100));

        heroCategories.add(new Hero_Category(42, "Hordes", 4));
        heroAdventures.add(new Hero_Adventure(421, 42, "Survive Blacksmith Horde", 750));
        heroResources.add(new Hero_Resource(421, 112, 5, 4));
        heroResources.add(new Hero_Resource(421, 15, Constants.STATE_NORMAL, 100));
        heroResources.add(new Hero_Resource(421, 6, Constants.STATE_NORMAL, 100));
        heroAdventures.add(new Hero_Adventure(422, 42, "Survive Spider Horde", 850));
        heroResources.add(new Hero_Resource(422, 69, Constants.STATE_NORMAL, 100));
        heroResources.add(new Hero_Resource(422, 70, Constants.STATE_NORMAL, 100));
        heroAdventures.add(new Hero_Adventure(423, 42, "Survive Golem Horde", 950));
        heroResources.add(new Hero_Resource(423, 9, Constants.STATE_NORMAL, 100));
        heroResources.add(new Hero_Resource(423, 4, Constants.STATE_NORMAL, 100));
        heroResources.add(new Hero_Resource(423, 3, Constants.STATE_NORMAL, 100));
        heroAdventures.add(new Hero_Adventure(424, 42, "Survive Angry Mob", 1050));
        heroResources.add(new Hero_Resource(424, 66, Constants.STATE_NORMAL, 22));
        heroResources.add(new Hero_Resource(424, 126, Constants.STATE_NORMAL, 22));
        heroResources.add(new Hero_Resource(424, 135, Constants.STATE_NORMAL, 11));
        heroAdventures.add(new Hero_Adventure(425, 42, "Survive Warrior Horde", 1100));
        heroResources.add(new Hero_Resource(425, 140, Constants.STATE_NORMAL, 22));
        heroResources.add(new Hero_Resource(425, 119, Constants.STATE_NORMAL, 11));
        heroResources.add(new Hero_Resource(425, 142, Constants.STATE_NORMAL, 22));

        heroCategories.add(new Hero_Category(43, "Unbeatable", 4));
        heroAdventures.add(new Hero_Adventure(431, 43, "Achieve Nirvana", 1000));
        heroResources.add(new Hero_Resource(431, 52, Constants.STATE_NORMAL, 20000));
        heroResources.add(new Hero_Resource(431, 186, Constants.STATE_NORMAL, 1));
        heroAdventures.add(new Hero_Adventure(432, 43, "Calculate Prime Number Formula", 1100));
        heroResources.add(new Hero_Resource(432, 52, Constants.STATE_NORMAL, 20000));
        heroResources.add(new Hero_Resource(432, 190, Constants.STATE_NORMAL, 1));
        heroAdventures.add(new Hero_Adventure(433, 43, "Live Forever", 1200));
        heroResources.add(new Hero_Resource(433, 52, Constants.STATE_NORMAL, 20000));
        heroResources.add(new Hero_Resource(433, 204, Constants.STATE_NORMAL, 60));
        heroAdventures.add(new Hero_Adventure(434, 43, "Raise The Dead", 1300));
        heroResources.add(new Hero_Resource(434, 52, Constants.STATE_NORMAL, 20000));
        heroResources.add(new Hero_Resource(434, 160, Constants.STATE_NORMAL, 10));
        heroResources.add(new Hero_Resource(434, 154, Constants.STATE_NORMAL, 10));
        heroAdventures.add(new Hero_Adventure(435, 43, "Turn Back Time", 1400));
        heroResources.add(new Hero_Resource(435, 52, Constants.STATE_NORMAL, 20000));
        heroResources.add(new Hero_Resource(435, 129, Constants.STATE_NORMAL, 45));
        heroResources.add(new Hero_Resource(435, 130, Constants.STATE_NORMAL, 45));
        heroAdventures.add(new Hero_Adventure(436, 43, "Complete All Adventures", 1500));
        heroResources.add(new Hero_Resource(436, 52, Constants.STATE_NORMAL, 20000));
        heroResources.add(new Hero_Resource(436, 139, Constants.STATE_NORMAL, 20));
        heroResources.add(new Hero_Resource(436, 147, 3, 4));

        // Guard Duty: 100 - 300 difficulty.
        heroCategories.add(new Hero_Category(5, "Guard Duty", 0));
        heroCategories.add(new Hero_Category(51, "Guard Shops", 5));
        heroAdventures.add(new Hero_Adventure(511, 51, "Guard Butchers", 100));
        heroResources.add(new Hero_Resource(511, 80, Constants.STATE_NORMAL, 5));
        heroResources.add(new Hero_Resource(511, 214, Constants.STATE_NORMAL, 5));
        heroAdventures.add(new Hero_Adventure(512, 51, "Guard Bakers", 110));
        heroResources.add(new Hero_Resource(512, 79, Constants.STATE_NORMAL, 3));
        heroResources.add(new Hero_Resource(512, 218, Constants.STATE_NORMAL, 3));
        heroResources.add(new Hero_Resource(512, 219, Constants.STATE_NORMAL, 3));
        heroAdventures.add(new Hero_Adventure(513, 51, "Guard Sandwich Shop", 120));
        heroResources.add(new Hero_Resource(513, 216, Constants.STATE_NORMAL, 2));
        heroResources.add(new Hero_Resource(513, 217, Constants.STATE_NORMAL, 2));
        heroResources.add(new Hero_Resource(513, 215, Constants.STATE_NORMAL, 2));
        heroAdventures.add(new Hero_Adventure(514, 51, "Guard Fishmongers", 130));
        heroResources.add(new Hero_Resource(514, 211, Constants.STATE_NORMAL, 9));
        heroAdventures.add(new Hero_Adventure(515, 51, "Guard Pie Shop", 140));
        heroResources.add(new Hero_Resource(515, 218, Constants.STATE_NORMAL, 6));
        heroAdventures.add(new Hero_Adventure(516, 51, "Guard Fruit Stand", 150));
        heroResources.add(new Hero_Resource(516, 205, Constants.STATE_NORMAL, 2));
        heroResources.add(new Hero_Resource(516, 208, Constants.STATE_NORMAL, 2));
        heroResources.add(new Hero_Resource(516, 218, Constants.STATE_NORMAL, 2));
        heroAdventures.add(new Hero_Adventure(517, 51, "Guard Farmer's Stand", 160));
        heroResources.add(new Hero_Resource(517, 202, Constants.STATE_NORMAL, 3));
        heroResources.add(new Hero_Resource(517, 203, Constants.STATE_NORMAL, 3));
        heroResources.add(new Hero_Resource(517, 204, Constants.STATE_NORMAL, 3));
        heroAdventures.add(new Hero_Adventure(518, 51, "Guard Sweet Shop", 170));
        heroResources.add(new Hero_Resource(518, 209, Constants.STATE_NORMAL, 3));
        heroResources.add(new Hero_Resource(518, 210, Constants.STATE_NORMAL, 3));
        heroResources.add(new Hero_Resource(518, 219, Constants.STATE_NORMAL, 3));

        heroCategories.add(new Hero_Category(52, "Guard Markets", 5));
        heroAdventures.add(new Hero_Adventure(521, 52, "Guard Local Market", 100));
        heroResources.add(new Hero_Resource(521, 79, Constants.STATE_NORMAL, 5));
        heroResources.add(new Hero_Resource(521, 52, Constants.STATE_NORMAL, 250));
        heroAdventures.add(new Hero_Adventure(522, 52, "Guard Farmer's Market", 200));
        heroResources.add(new Hero_Resource(522, 79, Constants.STATE_NORMAL, 10));
        heroResources.add(new Hero_Resource(522, 52, Constants.STATE_NORMAL, 750));
        heroAdventures.add(new Hero_Adventure(523, 52, "Guard County Market", 300));
        heroResources.add(new Hero_Resource(523, 79, Constants.STATE_NORMAL, 20));
        heroResources.add(new Hero_Resource(523, 52, Constants.STATE_NORMAL, 1500));

        heroCategories.add(new Hero_Category(53, "Guard Factories", 5));
        heroAdventures.add(new Hero_Adventure(531, 53, "Guard Food Processing Factory", 150));
        heroResources.add(new Hero_Resource(531, 78, Constants.STATE_NORMAL, 3));
        heroResources.add(new Hero_Resource(531, 214, Constants.STATE_NORMAL, 3));
        heroResources.add(new Hero_Resource(531, 215, Constants.STATE_NORMAL, 2));
        heroAdventures.add(new Hero_Adventure(532, 53, "Guard Smelting Factory", 200));
        heroResources.add(new Hero_Resource(532, 3, Constants.STATE_NORMAL, 15));
        heroResources.add(new Hero_Resource(532, 5, Constants.STATE_NORMAL, 4));
        heroResources.add(new Hero_Resource(532, 14, Constants.STATE_NORMAL, 6));
        heroAdventures.add(new Hero_Adventure(533, 53, "Guard Clothing Factory", 250));
        heroResources.add(new Hero_Resource(533, 69, Constants.STATE_NORMAL, 15));
        heroResources.add(new Hero_Resource(533, 70, Constants.STATE_NORMAL, 15));
        heroAdventures.add(new Hero_Adventure(534, 53, "Guard Gem Factory", 300));
        heroResources.add(new Hero_Resource(534, 72, Constants.STATE_NORMAL, 2));
        heroResources.add(new Hero_Resource(534, 130, Constants.STATE_NORMAL, 4));
        heroResources.add(new Hero_Resource(534, 131, Constants.STATE_NORMAL, 4));

        heroCategories.add(new Hero_Category(54, "Guard Banks", 5));
        heroAdventures.add(new Hero_Adventure(541, 54, "Guard Local Bank", 190));
        heroResources.add(new Hero_Resource(541, 52, Constants.STATE_NORMAL, 200));
        heroAdventures.add(new Hero_Adventure(542, 54, "Guard Town Bank", 210));
        heroResources.add(new Hero_Resource(542, 52, Constants.STATE_NORMAL, 220));
        heroAdventures.add(new Hero_Adventure(543, 54, "Guard City Bank", 220));
        heroResources.add(new Hero_Resource(543, 52, Constants.STATE_NORMAL, 260));
        heroAdventures.add(new Hero_Adventure(544, 54, "Guard State Bank", 230));
        heroResources.add(new Hero_Resource(544, 52, Constants.STATE_NORMAL, 310));
        heroAdventures.add(new Hero_Adventure(545, 54, "Guard Federal Bank", 240));
        heroResources.add(new Hero_Resource(545, 52, Constants.STATE_NORMAL, 400));

        heroCategories.add(new Hero_Category(55, "Guard Royalty", 5));
        heroAdventures.add(new Hero_Adventure(551, 55, "Guard Princess", 270));
        heroResources.add(new Hero_Resource(551, 9, Constants.STATE_NORMAL, 8));
        heroResources.add(new Hero_Resource(551, 17, Constants.STATE_NORMAL, 8));
        heroAdventures.add(new Hero_Adventure(552, 55, "Guard Prince", 260));
        heroResources.add(new Hero_Resource(552, 8, Constants.STATE_NORMAL, 8));
        heroResources.add(new Hero_Resource(552, 18, Constants.STATE_NORMAL, 8));
        heroAdventures.add(new Hero_Adventure(553, 55, "Guard Queen", 280));
        heroResources.add(new Hero_Resource(553, 9, Constants.STATE_NORMAL, 16));
        heroResources.add(new Hero_Resource(553, 17, Constants.STATE_NORMAL, 16));
        heroAdventures.add(new Hero_Adventure(554, 55, "Guard King", 290));
        heroResources.add(new Hero_Resource(554, 8, Constants.STATE_NORMAL, 16));
        heroResources.add(new Hero_Resource(554, 18, Constants.STATE_NORMAL, 16));

        // Exploring: 500 - 800 difficulty.
        heroCategories.add(new Hero_Category(6, "Exploring", 0));
        heroCategories.add(new Hero_Category(61, "Explore Forest", 6));
        heroAdventures.add(new Hero_Adventure(611, 61, "Explore Shrubs", 500));
        heroResources.add(new Hero_Resource(611, 73, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(611, 202, Constants.STATE_NORMAL, 10));
        heroAdventures.add(new Hero_Adventure(612, 61, "Explore Bushes", 520));
        heroResources.add(new Hero_Resource(612, 74, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(612, 203, Constants.STATE_NORMAL, 10));
        heroAdventures.add(new Hero_Adventure(613, 61, "Explore Trees", 540));
        heroResources.add(new Hero_Resource(613, 76, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(613, 77, Constants.STATE_NORMAL, 10));
        heroAdventures.add(new Hero_Adventure(614, 61, "Explore Treetops", 600));
        heroResources.add(new Hero_Resource(614, 75, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(614, 130, Constants.STATE_NORMAL, 5));

        heroCategories.add(new Hero_Category(62, "Explore Desert", 6));
        heroAdventures.add(new Hero_Adventure(621, 62, "Explore Sandwich", 500));
        heroResources.add(new Hero_Resource(621, 217, Constants.STATE_NORMAL, 8));
        heroAdventures.add(new Hero_Adventure(622, 62, "Explore Sandcastle", 650));
        heroResources.add(new Hero_Resource(622, 216, Constants.STATE_NORMAL, 8));
        heroAdventures.add(new Hero_Adventure(623, 62, "Explore Sand Dune", 700));
        heroResources.add(new Hero_Resource(623, 215, Constants.STATE_NORMAL, 8));

        heroCategories.add(new Hero_Category(63, "Explore City", 6));
        heroAdventures.add(new Hero_Adventure(631, 63, "Explore Central City", 600));
        heroResources.add(new Hero_Resource(631, 188, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(631, 13, Constants.STATE_NORMAL, 10));
        heroResources.add(new Hero_Resource(631, 12, Constants.STATE_NORMAL, 10));
        heroAdventures.add(new Hero_Adventure(632, 63, "Explore West City", 640));
        heroResources.add(new Hero_Resource(632, 188, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(632, 14, Constants.STATE_NORMAL, 20));
        heroAdventures.add(new Hero_Adventure(633, 63, "Explore South City", 680));
        heroResources.add(new Hero_Resource(633, 188, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(633, 15, Constants.STATE_NORMAL, 20));
        heroAdventures.add(new Hero_Adventure(634, 63, "Explore East City", 720));
        heroResources.add(new Hero_Resource(634, 188, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(634, 16, Constants.STATE_NORMAL, 20));
        heroAdventures.add(new Hero_Adventure(635, 63, "Explore North City", 760));
        heroResources.add(new Hero_Resource(635, 188, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(635, 19, Constants.STATE_NORMAL, 20));

        heroCategories.add(new Hero_Category(64, "Explore Underwater", 6));
        heroAdventures.add(new Hero_Adventure(641, 64, "Explore Coral Reef", 600));
        heroResources.add(new Hero_Resource(641, 211, Constants.STATE_NORMAL, 25));
        heroAdventures.add(new Hero_Adventure(642, 64, "Explore Mariana Trench", 800));
        heroResources.add(new Hero_Resource(642, 211, Constants.STATE_NORMAL, 45));

        heroCategories.add(new Hero_Category(65, "Explore Caves", 6));
        heroAdventures.add(new Hero_Adventure(651, 65, "Explore Small Cave", 750));
        heroResources.add(new Hero_Resource(651, 105, Constants.STATE_NORMAL, 2));
        heroResources.add(new Hero_Resource(651, 128, Constants.STATE_NORMAL, 2));
        heroResources.add(new Hero_Resource(651, 144, Constants.STATE_NORMAL, 2));
        heroAdventures.add(new Hero_Adventure(652, 65, "Explore Medium Cave", 790));
        heroResources.add(new Hero_Resource(652, 110, Constants.STATE_NORMAL, 4));
        heroResources.add(new Hero_Resource(652, 127, Constants.STATE_NORMAL, 4));
        heroResources.add(new Hero_Resource(652, 143, Constants.STATE_NORMAL, 4));
        heroAdventures.add(new Hero_Adventure(653, 65, "Explore Large Cave", 800));
        heroResources.add(new Hero_Resource(653, 106, 3, 2));
        heroResources.add(new Hero_Resource(653, 122, 4, 2));
        heroResources.add(new Hero_Resource(653, 132, 5, 2));

        // Escort: 300 - 700 difficulty.
        heroCategories.add(new Hero_Category(7, "Escort", 0));
        heroCategories.add(new Hero_Category(71, "Escort Villagers", 7));
        heroAdventures.add(new Hero_Adventure(711, 71, "Escort Butcher", 350));
        heroResources.add(new Hero_Resource(711, 80, Constants.STATE_NORMAL, 15));
        heroResources.add(new Hero_Resource(711, 52, Constants.STATE_NORMAL, 50));
        heroAdventures.add(new Hero_Adventure(712, 71, "Escort Baker", 370));
        heroResources.add(new Hero_Resource(712, 79, Constants.STATE_NORMAL, 15));
        heroResources.add(new Hero_Resource(712, 202, Constants.STATE_NORMAL, 5));
        heroResources.add(new Hero_Resource(712, 52, Constants.STATE_NORMAL, 75));
        heroAdventures.add(new Hero_Adventure(713, 71, "Escort Fisherman", 390));
        heroResources.add(new Hero_Resource(713, 211, Constants.STATE_NORMAL, 15));
        heroResources.add(new Hero_Resource(713, 52, Constants.STATE_NORMAL, 100));
        heroAdventures.add(new Hero_Adventure(714, 71, "Escort Silk Trader", 410));
        heroResources.add(new Hero_Resource(714, 69, Constants.STATE_NORMAL, 10));
        heroResources.add(new Hero_Resource(714, 70, Constants.STATE_NORMAL, 10));
        heroResources.add(new Hero_Resource(714, 52, Constants.STATE_NORMAL, 125));
        heroAdventures.add(new Hero_Adventure(715, 71, "Escort Gem Trader", 430));
        heroResources.add(new Hero_Resource(715, 76, Constants.STATE_NORMAL, 3));
        heroResources.add(new Hero_Resource(715, 131, Constants.STATE_NORMAL, 10));
        heroResources.add(new Hero_Resource(715, 52, Constants.STATE_NORMAL, 150));
        heroAdventures.add(new Hero_Adventure(716, 71, "Escort Weapons Trader", 450));
        heroResources.add(new Hero_Resource(716, 115, Constants.STATE_NORMAL, 7));
        heroResources.add(new Hero_Resource(716, 116, Constants.STATE_NORMAL, 7));
        heroResources.add(new Hero_Resource(716, 52, Constants.STATE_NORMAL, 175));
        heroAdventures.add(new Hero_Adventure(717, 71, "Escort Armour Trader", 470));
        heroResources.add(new Hero_Resource(717, 118, Constants.STATE_NORMAL, 7));
        heroResources.add(new Hero_Resource(717, 120, Constants.STATE_NORMAL, 7));
        heroResources.add(new Hero_Resource(717, 52, Constants.STATE_NORMAL, 200));

        heroCategories.add(new Hero_Category(72, "Escort Visitors", 7));
        heroAdventures.add(new Hero_Adventure(721, 72, "Escort Day Visitor", 500));
        heroResources.add(new Hero_Resource(721, 180, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(721, 155, Constants.STATE_NORMAL, 4));
        heroAdventures.add(new Hero_Adventure(722, 72, "Escort Night Visitor", 520));
        heroResources.add(new Hero_Resource(722, 180, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(722, 156, Constants.STATE_NORMAL, 4));
        heroAdventures.add(new Hero_Adventure(723, 72, "Escort Winter Visitor", 540));
        heroResources.add(new Hero_Resource(723, 180, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(723, 157, Constants.STATE_NORMAL, 4));
        heroAdventures.add(new Hero_Adventure(724, 72, "Escort Summer Visitor", 560));
        heroResources.add(new Hero_Resource(724, 180, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(724, 158, Constants.STATE_NORMAL, 4));
        heroAdventures.add(new Hero_Adventure(725, 72, "Escort Autumn Visitor", 580));
        heroResources.add(new Hero_Resource(725, 180, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(725, 159, Constants.STATE_NORMAL, 4));
        heroAdventures.add(new Hero_Adventure(726, 72, "Escort Spring Visitor", 600));
        heroResources.add(new Hero_Resource(726, 180, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(726, 160, Constants.STATE_NORMAL, 4));
        heroAdventures.add(new Hero_Adventure(727, 72, "Escort Lost Courier", 620));
        heroResources.add(new Hero_Resource(727, 180, Constants.STATE_NORMAL, 1));
        heroResources.add(new Hero_Resource(727, 216, Constants.STATE_NORMAL, 2));
        heroResources.add(new Hero_Resource(727, 217, Constants.STATE_NORMAL, 2));

        heroCategories.add(new Hero_Category(73, "Escort Soldiers", 7));
        heroAdventures.add(new Hero_Adventure(731, 73, "Escort Underground Soldier", 630));
        heroResources.add(new Hero_Resource(731, 52, Constants.STATE_NORMAL, 1000));
        heroResources.add(new Hero_Resource(731, 148, Constants.STATE_NORMAL, 1));
        heroAdventures.add(new Hero_Adventure(732, 73, "Escort Shiny Soldier", 660));
        heroResources.add(new Hero_Resource(732, 52, Constants.STATE_NORMAL, 1200));
        heroResources.add(new Hero_Resource(732, 18, Constants.STATE_NORMAL, 3));
        heroResources.add(new Hero_Resource(732, 148, Constants.STATE_NORMAL, 1));
        heroAdventures.add(new Hero_Adventure(733, 73, "Escort Elegant Soldier", 690));
        heroResources.add(new Hero_Resource(733, 52, Constants.STATE_NORMAL, 1400));
        heroResources.add(new Hero_Resource(733, 70, Constants.STATE_NORMAL, 5));
        heroResources.add(new Hero_Resource(733, 148, Constants.STATE_NORMAL, 1));

        Hero_Category.saveInTx(heroCategories);
        Hero_Adventure.saveInTx(heroAdventures);
        Hero_Resource.saveInTx(heroResources);
    }

    private void createAchievement() {
        List<Achievement> achievements = new ArrayList<>();

        achievements.add(new Achievement("Open For Business 1", "Completed a visitor", 1, 9, "CgkI6tnE2Y4OEAIQAg"));
        achievements.add(new Achievement("Open For Business 2", "Completed 10 visitors", 10, 9, "CgkI6tnE2Y4OEAIQAw"));
        achievements.add(new Achievement("Open For Business 3", "Completed 100 visitors", 100, 9, "CgkI6tnE2Y4OEAIQBA"));
        achievements.add(new Achievement("Open For Business 4", "Completed 1000 visitors", 1000, 9, "CgkI6tnE2Y4OEAIQBQ"));
        achievements.add(new Achievement("The Forge Is Hot 1", "Smelted a bar", 1, 3, "CgkI6tnE2Y4OEAIQBg"));
        achievements.add(new Achievement("The Forge Is Hot 2", "Smelted 10 bars", 10, 3, "CgkI6tnE2Y4OEAIQBw"));
        achievements.add(new Achievement("The Forge Is Hot 3", "Smelted 100 bars", 100, 3, "CgkI6tnE2Y4OEAIQCg"));
        achievements.add(new Achievement("The Forge Is Hot 4", "Smelted 1000 bars", 1000, 3, "CgkI6tnE2Y4OEAIQCw"));
        achievements.add(new Achievement("Like Hot Cakes 1", "Sell an item to visitors", 1, 5, "CgkI6tnE2Y4OEAIQDA"));
        achievements.add(new Achievement("Like Hot Cakes 2", "Sell 10 items to visitors", 10, 5, "CgkI6tnE2Y4OEAIQDQ"));
        achievements.add(new Achievement("Like Hot Cakes 3", "Sell 100 items to visitors", 100, 5, "CgkI6tnE2Y4OEAIQDg"));
        achievements.add(new Achievement("Like Hot Cakes 4", "Sell 1000 items to visitors", 1000, 5, "CgkI6tnE2Y4OEAIQDw"));
        achievements.add(new Achievement("Supply And Demand 1", "Buy an item from the marketplace", 1, 8, "CgkI6tnE2Y4OEAIQEA"));
        achievements.add(new Achievement("Supply And Demand 2", "Buy 10 items from the marketplace", 10, 8, "CgkI6tnE2Y4OEAIQEQ"));
        achievements.add(new Achievement("Supply And Demand 3", "Buy 100 items from the marketplace", 100, 8, "CgkI6tnE2Y4OEAIQEg"));
        achievements.add(new Achievement("Supply And Demand 4", "Buy 1000 items from the marketplace", 1000, 8, "CgkI6tnE2Y4OEAIQEw"));
        achievements.add(new Achievement("Mr Moneybags 1", "Earn 200 coins", 200, 12, "CgkI6tnE2Y4OEAIQFA"));
        achievements.add(new Achievement("Mr Moneybags 2", "Earn 1000 coins", 1000, 12, "CgkI6tnE2Y4OEAIQFQ"));
        achievements.add(new Achievement("Mr Moneybags 3", "Earn 3000 coins", 3000, 12, "CgkI6tnE2Y4OEAIQFg"));
        achievements.add(new Achievement("Mr Moneybags 4", "Earn 10000 coins", 10000, 12, "CgkI6tnE2Y4OEAIQFw"));
        achievements.add(new Achievement("Mastery Of Bronze", "Reach level 5", 5, 14, "CgkI6tnE2Y4OEAIQGA"));
        achievements.add(new Achievement("Mastery Of Iron", "Reach level 10", 10, 14, "CgkI6tnE2Y4OEAIQGQ"));
        achievements.add(new Achievement("Mastery Of Steel", "Reach level 20", 20, 14, "CgkI6tnE2Y4OEAIQGg"));
        achievements.add(new Achievement("Mastery Of Mithril", "Reach level 30", 30, 14, "CgkI6tnE2Y4OEAIQGw"));
        achievements.add(new Achievement("Mastery Of Adamant", "Reach level 40", 40, 14, "CgkI6tnE2Y4OEAIQHA"));
        achievements.add(new Achievement("Mastery Of Jewellery", "Reach level 45", 45, 14, "CgkI6tnE2Y4OEAIQHQ"));
        achievements.add(new Achievement("Mastery Of Rune", "Reach level 50", 50, 14, "CgkI6tnE2Y4OEAIQHg"));
        achievements.add(new Achievement("Mastery Of Dragon", "Reach level 60", 60, 14, "CgkI6tnE2Y4OEAIQHw"));
        achievements.add(new Achievement("The Fun Never Stops", "Prestige", 1, 17, "CgkI6tnE2Y4OEAIQIA"));

        Achievement.saveInTx(achievements);
    }

    private void createCategory() {
        List<Category> categories = new ArrayList<>();

        categories.add(new Category("Unknown", "Item type category could not be found."));
        categories.add(new Category("Crafting", "An item that can be used for crafting."));
        categories.add(new Category("Weapon", "An item that can be used as a weapon."));
        categories.add(new Category("Armour", "An item that can be used as armour."));
        categories.add(new Category("Skill", "An item that can be used to train a skill."));
        categories.add(new Category("Rare", "An item that is extremely rare, and hard to obtain."));

        Category.saveInTx(categories);
    }

    private void createCharacter() {
        List<Character> characters = new ArrayList<>();

        characters.add(new Character(1L, "Sean Keeper", "Greetings! See anything you like?"));
        characters.add(new Character(2L, "The Iron Knight", "Look, it's knight with a k, okay?"));
        characters.add(new Character(3L, "The Golden Warrior", "Can never have enough golden goldy gold. EVER."));
        characters.add(new Character(4L, "Cloth King", "Want clothing? You're gonna need some cloth!"));
        characters.add(new Character(5L, "Woodchuck", "How much wood can I cut? Enough!"));
        characters.add(new Character(6L, "Oog", "Oog oog!!!"));
        characters.add(new Character(7L, "GemBot 5000", "Bzzt!"));
        characters.add(new Character(8L, "Farmer's Little Friend", "You'd better not have any weedkiller on you."));
        characters.add(new Character(9L, "Death Beetle", "I am an exclusive trader."));
        characters.add(new Character(10L, "Miss Odds & Ends", "Hello dear! I hope you like my shop."));
        characters.add(new Character(11L, "Deathly Ghoul", "Boo! Only joking, I'm not that scary."));
        characters.add(new Character(12L, "Bleached Crab", "Maybe I've spent a little too long in the sun."));
        characters.add(new Character(13L, "Goblin Warrior", "Orite matey, got any gold?"));
        characters.add(new Character(14L, "Floating Tradesman", "It's rather hard carrying stock around y'know."));
        characters.add(new Character(15L, "Goblin Businessman", "Hello there! Come, friend, trade away!"));
        characters.add(new Character(16L, "Blobette", "Bloop bloop."));
        characters.add(new Character(17L, "Ricky Stardust", "Floating in the stars forever."));
        characters.add(new Character(18L, "Grumblestiltskin", "Fine, I'll trade if I have to. Hmph."));
        characters.add(new Character(19L, "Oculus Lift", "Flippity flappity, my friend."));
        characters.add(new Character(20L, "Blob King", "Also bloop. Even more so."));

        Character.saveInTx(characters);
    }

    private void createCriteria() {
        List<Criteria> criterias = new ArrayList<>();

        criterias.add(new Criteria(1L, "State"));
        criterias.add(new Criteria(2L, "Tier"));
        criterias.add(new Criteria(3L, "Type"));

        Criteria.saveInTx(criterias);
    }

    void createInventory() {
        List<Inventory> inventories = new ArrayList<>();

        // 200 gold
        inventories.add(new Inventory(52L, Constants.STATE_NORMAL, 200));

        // 150 copper, tin, iron.
        inventories.add(new Inventory(1L, Constants.STATE_NORMAL, 150));
        inventories.add(new Inventory(2L, Constants.STATE_NORMAL, 150));
        inventories.add(new Inventory(4L, Constants.STATE_NORMAL, 150));

        // Make bronze bar + dagger visible.
        inventories.add(new Inventory(11L, Constants.STATE_NORMAL, 0));
        inventories.add(new Inventory(20L, Constants.STATE_UNFINISHED, 0));
        inventories.add(new Inventory(20L, Constants.STATE_NORMAL, 0));

        // 30 silk, spidersilk
        inventories.add(new Inventory(69L, Constants.STATE_NORMAL, 30));
        inventories.add(new Inventory(70L, Constants.STATE_NORMAL, 30));

        // 2 sapphires
        inventories.add(new Inventory(73L, Constants.STATE_NORMAL, 2));

        // 5 apples & cheese
        inventories.add(new Inventory(77L, Constants.STATE_NORMAL, 5));
        inventories.add(new Inventory(78L, Constants.STATE_NORMAL, 5));

        Inventory.saveInTx(inventories);
    }

    private void createItem() {
        List<Item> items = new ArrayList<>();

        items.add(new Item(1L, "Copper ore", "A piece of copper ore.", 1, 11, 2, 0));
        items.add(new Item(2L, "Tin ore", "A piece of tin ore.", 1, 11, 2, 0));
        items.add(new Item(3L, "Coal", "A piece of coal.", 1, 11, 5, 0));
        items.add(new Item(4L, "Iron ore", "A piece of iron ore.", 1, 11, 7, 0));
        items.add(new Item(5L, "Mithril ore", "A piece of mithril ore.", 1, 11, 12, 0));
        items.add(new Item(6L, "Adamantite ore", "A piece of adamantite ore.", 1, 11, 25, 0));
        items.add(new Item(7L, "Runite ore", "A piece of runite ore.", 1, 11, 50, 0));
        items.add(new Item(8L, "Gold nugget", "A golden nugget.", 1, 11, 30, 0));
        items.add(new Item(9L, "Silver nugget", "A silver nugget.", 1, 11, 20, 0));
        items.add(new Item(10L, "Dragonite ore", "A piece of dragonite ore.", 1, 11, 100, 0));
        items.add(new Item(11L, "Bronze bar", "A fresh bar of bronze.", 2, 11, 7, 1));
        items.add(new Item(12L, "Iron bar", "A fresh bar of iron.", 2, 11, 15, 5));
        items.add(new Item(13L, "Steel bar", "A fresh bar of steel.", 2, 11, 20, 10));
        items.add(new Item(14L, "Mithril bar", "A fresh bar of mithril.", 2, 11, 24, 20));
        items.add(new Item(15L, "Adamant bar", "A fresh bar of adamant.", 2, 11, 50, 30));
        items.add(new Item(16L, "Rune bar", "A fresh bar of rune.", 2, 11, 100, 40));
        items.add(new Item(17L, "Silver bar", "A fresh bar of silver.", 2, 11, 22, 35));
        items.add(new Item(18L, "Gold bar", "A fresh bar of gold.", 2, 11, 35, 45));
        items.add(new Item(19L, "Dragon bar", "A fresh bar of dragon.", 2, 11, 175, 50));
        items.add(new Item(20L, "Bronze dagger", "A blunt bronze dagger.", 3, 1, 8, 1));
        items.add(new Item(21L, "Bronze sword", "A fairly blunt bronze sword.", 4, 1, 15, 1));
        items.add(new Item(22L, "Bronze longsword", "A longer bronze sword.", 5, 1, 15, 1));
        items.add(new Item(23L, "Bronze bow", "A bow with bronze elements.", 6, 1, 15, 1));
        items.add(new Item(24L, "Bronze half shield", "A fairly weak bronze shield.", 7, 1, 15, 2));
        items.add(new Item(25L, "Bronze full shield", "A slightly stronger bronze shield.", 8, 1, 15, 2));
        items.add(new Item(26L, "Bronze chainmail", "A light set of bronze body armour.", 9, 1, 15, 2));
        items.add(new Item(27L, "Bronze platebody", "A heavy set of bronze body armour.", 10, 1, 29, 3));
        items.add(new Item(28L, "Bronze half helmet", "A no-frills approach to protection.", 11, 1, 15, 3));
        items.add(new Item(29L, "Bronze full helmet", "A frills approach to protection.", 12, 1, 22, 4));
        items.add(new Item(30L, "Bronze boots", "A set of boots for those that like the muddy look.", 13, 1, 15, 4));
        items.add(new Item(31L, "Bronze gloves", "A set of gloves for those that like the muddy look.", 14, 1, 15, 4));
        items.add(new Item(32L, "Bronze pickaxe", "A weak pickaxe for mining rocks.", 15, 1, 15, 4));
        items.add(new Item(33L, "Bronze hatchet", "A weak axe for cutting down trees.", 16, 1, 15, 4));
        items.add(new Item(34L, "Bronze fishing rod", "A weak rod for catching fish.", 17, 1, 15, 4));
        items.add(new Item(35L, "Bronze hammer", "A weak hammer who misses his anvil.", 18, 1, 8, 4));
        items.add(new Item(36L, "Iron dagger", "A blunt iron dagger.", 3, 2, 17, 5));
        items.add(new Item(37L, "Iron sword", "A bluntish iron sword.", 4, 2, 32, 5));
        items.add(new Item(38L, "Iron longsword", "A longer iron sword.", 5, 2, 32, 5));
        items.add(new Item(39L, "Iron bow", "A bow with iron elements.", 6, 2, 32, 5));
        items.add(new Item(40L, "Iron half shield", "An iron half shield.", 7, 2, 47, 6));
        items.add(new Item(41L, "Iron full shield", "An improved iron shield.", 8, 2, 47, 6));
        items.add(new Item(42L, "Iron chainmail", "A light set of iron body armour.", 9, 2, 47, 6));
        items.add(new Item(43L, "Iron platebody", "A heavy set of iron body armour.", 10, 2, 77, 7));
        items.add(new Item(44L, "Iron half helmet", "A tiny bit of frills approach to protection.", 11, 2, 47, 7));
        items.add(new Item(45L, "Iron full helmet", "A very frilly approach to protection.", 12, 2, 62, 8));
        items.add(new Item(46L, "Iron boots", "A set of boots for those that like the grey look.", 13, 2, 32, 8));
        items.add(new Item(47L, "Iron gloves", "A set of gloves for those that like the grey look.", 14, 2, 32, 8));
        items.add(new Item(48L, "Iron pickaxe", "A standard pickaxe for mining rocks.", 15, 2, 32, 9));
        items.add(new Item(49L, "Iron hatchet", "A standard axe for cutting down trees.", 16, 2, 32, 9));
        items.add(new Item(50L, "Iron fishing rod", "A standard rod for catching fish.", 17, 2, 32, 9));
        items.add(new Item(51L, "Iron hammer", "A standard hammer who misses his anvil.", 18, 2, 17, 9));
        items.add(new Item(52L, "Coins", "Coins! Glorious coins!", 100, 11, 1, 1));
        items.add(new Item(53L, "Steel dagger", "An average steel dagger.", 3, 3, 23, 10));
        items.add(new Item(54L, "Steel sword", "A slightly blunt steel sword.", 4, 3, 43, 10));
        items.add(new Item(55L, "Steel longsword", "An even longer steel sword.", 5, 3, 43, 10));
        items.add(new Item(56L, "Steel bow", "A bow with steel elements.", 6, 3, 43, 10));
        items.add(new Item(57L, "Steel half shield", "Steel shield, but only half.", 7, 3, 63, 12));
        items.add(new Item(58L, "Steel full shield", "Steel shield, the fullest.", 8, 3, 63, 12));
        items.add(new Item(59L, "Steel chainmail", "A light set of steel body armour.", 9, 3, 63, 12));
        items.add(new Item(60L, "Steel platebody", "A heavy set of steel body armour.", 10, 3, 103, 14));
        items.add(new Item(61L, "Steel half helmet", "A little bit frilly approach to protection.", 11, 3, 63, 14));
        items.add(new Item(62L, "Steel full helmet", "A rather frilly approach to protection.", 12, 3, 83, 16));
        items.add(new Item(63L, "Steel boots", "A set of boots for those that like the grey look, now with added shine.", 13, 3, 43, 16));
        items.add(new Item(64L, "Steel gloves", "A set of gloves for those that like the grey look, now with added shine.", 14, 3, 43, 16));
        items.add(new Item(65L, "Steel pickaxe", "A sharper pickaxe for mining rocks.", 15, 3, 43, 18));
        items.add(new Item(66L, "Steel hatchet", "A sharper axe for cutting down trees.", 16, 3, 43, 18));
        items.add(new Item(67L, "Steel fishing rod", "A sharper rod for catching fish.", 17, 3, 43, 18));
        items.add(new Item(68L, "Steel hammer", "A heavier hammer that tends to hit the anvil. Just.", 18, 3, 23, 18));
        items.add(new Item(69L, "Spidersilk", "A stand of spidersilk.", 19, 11, 1, 1));
        items.add(new Item(70L, "Silk", "A fine strip of silk.", 19, 11, 1, 1));
        items.add(new Item(71L, "Logs", "Some basic logs.", 19, 11, 1, 1));
        items.add(new Item(72L, "Ruby", "A red gem.", 20, 11, 150, 12));
        items.add(new Item(73L, "Sapphire", "A blue gem.", 20, 11, 150, 12));
        items.add(new Item(74L, "Emerald", "A green gem.", 20, 11, 150, 12));
        items.add(new Item(75L, "Diamond", "A white gem.", 20, 11, 250, 15));
        items.add(new Item(76L, "Onyx", "A black gem.", 20, 11, 250, 15));
        items.add(new Item(77L, "Apple", "A nice, shiny apple.", 21, 11, 5, 1));
        items.add(new Item(78L, "Cheese", "Smells a bit cheesy.", 21, 11, 5, 1));
        items.add(new Item(79L, "Bread", "A bit stale, but edible.", 21, 11, 5, 1));
        items.add(new Item(80L, "Raw Meat", "Still dripping. Yuck.", 21, 11, 5, 1));
        items.add(new Item(81L, "Mithril dagger", "A pretty good blue-tinted dagger.", 3, 4, 29, 20));
        items.add(new Item(82L, "Mithril sword", "A slightly sharp sword, with a blue hue.", 4, 4, 53, 20));
        items.add(new Item(83L, "Mithril longsword", "item = 'long' + previousItem;", 5, 4, 53, 20));
        items.add(new Item(84L, "Mithril bow", "Use to fire arrows out of the blue.", 6, 4, 53, 20));
        items.add(new Item(85L, "Mithril half shield", "A bl shi.", 7, 4, 77, 22));
        items.add(new Item(86L, "Mithril full shield", "A blue shield.", 8, 4, 77, 22));
        items.add(new Item(87L, "Mithril chainmail", "A set of mithril chainmail, seemingly for the smaller gentleman.", 9, 4, 77, 22));
        items.add(new Item(88L, "Mithril platebody", "A more complete approach to sword-stopping.", 10, 4, 125, 24));
        items.add(new Item(89L, "Mithril half helmet", "Blue on blue for you.", 11, 4, 77, 24));
        items.add(new Item(90L, "Mithril full helmet", "Blue on more blue, still for you.", 12, 4, 101, 26));
        items.add(new Item(91L, "Mithril boots", "Note: Despite appearances, you are not walking on water.", 13, 4, 53, 26));
        items.add(new Item(92L, "Mithril gloves", "Note: Despite appearances, you are not washing your hands.", 14, 4, 53, 26));
        items.add(new Item(93L, "Mithril pickaxe", "This pick is no myth!", 15, 4, 53, 28));
        items.add(new Item(94L, "Mithril hatchet", "It's a shame magical blue trees don't exist.", 16, 4, 53, 28));
        items.add(new Item(95L, "Mithril fishing rod", "The fish seem oddly attracted to the blue metal.", 17, 4, 53, 28));
        items.add(new Item(96L, "Mithril hammer", "A heavier hammer that tends to hit the anvil. Just.", 18, 4, 29, 28));
        items.add(new Item(97L, "Adamant dagger", "A scarily sharp green dagger.", 3, 5, 57, 30));
        items.add(new Item(98L, "Adamant sword", "A rather sharp sword, with a green tint.", 4, 5, 107, 30));
        items.add(new Item(99L, "Adamant longsword", "I'm adamant that this sword is longer.", 5, 5, 107, 30));
        items.add(new Item(100L, "Adamant bow", "A fine bow, with an intriguing green hue.", 6, 5, 107, 30));
        items.add(new Item(101L, "Adamant half shield", "Don't let anyone know this shield looks like a pea.", 7, 5, 157, 32));
        items.add(new Item(102L, "Adamant full shield", "A rather impressive green shield.", 8, 5, 157, 32));
        items.add(new Item(103L, "Adamant chainmail", "A fine set of chainmail, even considering the colour.", 9, 5, 157, 32));
        items.add(new Item(104L, "Adamant platebody", "The best platebody for those that can't kill dragons.", 10, 5, 257, 34));
        items.add(new Item(105L, "Adamant half helmet", "Green on green, lookin' mean.", 11, 5, 157, 34));
        items.add(new Item(106L, "Adamant full helmet", "Gold on green, still lookin' mean.", 12, 5, 207, 36));
        items.add(new Item(107L, "Adamant boots", "Ugh, how do these even get so mould- ... oh!", 13, 5, 107, 36));
        items.add(new Item(108L, "Adamant gloves", "Maybe you should take up gardening.", 14, 5, 107, 36));
        items.add(new Item(109L, "Adamant pickaxe", "Mean, green, mining machine.", 15, 5, 107, 38));
        items.add(new Item(110L, "Adamant hatchet", "Powerful, but has a habit of blending into the target.", 16, 5, 107, 38));
        items.add(new Item(111L, "Adamant fishing rod", "The green rod seems to lure the fishies in...", 17, 5, 107, 38));
        items.add(new Item(112L, "Adamant hammer", "A very solid hammer, used for crafting powerful equipment.", 18, 5, 57, 38));
        items.add(new Item(113L, "Rune dagger", "A nifty little blade with a very sharp point.", 3, 6, 110, 40));
        items.add(new Item(114L, "Rune sword", "The rune rune rune, rune is the (s)word.", 4, 6, 210, 40));
        items.add(new Item(115L, "Rune longsword", "A very long, and very sharp, sword.", 5, 6, 210, 40));
        items.add(new Item(116L, "Rune bow", "The shortest of shortbows, the longest of longbows.", 6, 6, 210, 40));
        items.add(new Item(117L, "Rune half shield", "Looks rather like a crystal, but isn't.", 7, 6, 310, 42));
        items.add(new Item(118L, "Rune full shield", "A very nice shield, used by professional adventurers.", 8, 6, 310, 42));
        items.add(new Item(119L, "Rune chainmail", "Well, it's not a platebody, but it's still pretty good.", 9, 6, 310, 42));
        items.add(new Item(120L, "Rune platebody", "Made famous by DR4G0NK1LL3R, truly a brave adventurer.", 10, 6, 510, 44));
        items.add(new Item(121L, "Rune half helmet", "A very protective helmet, without all the trimmings.", 11, 6, 140, 44));
        items.add(new Item(122L, "Rune full helmet", "An especially protective helmet, with all the trimmings.", 12, 6, 410, 46));
        items.add(new Item(123L, "Rune boots", "Walk the streets in style with these super-comfy boots.", 13, 6, 210, 46));
        items.add(new Item(124L, "Rune gloves", "Brand new Rune gloves, now with extra protection!", 14, 6, 210, 46));
        items.add(new Item(125L, "Rune pickaxe", "A pickaxe any miner would be proud to use.", 15, 6, 210, 48));
        items.add(new Item(126L, "Rune hatchet", "An extremely sharp axe, for the mightiest of trees.", 16, 6, 210, 48));
        items.add(new Item(127L, "Rune fishing rod", "The fish won't rune away from this rod!", 17, 6, 210, 48));
        items.add(new Item(128L, "Rune hammer", "The mightiest of hammers! Or at least a very mighty one.", 18, 6, 110, 48));
        items.add(new Item(129L, "Powdered Sapphire", "This sapphire is in tiny shards.", 22, 11, 20, 0));
        items.add(new Item(130L, "Powdered Emerald", "This emerald is in tiny shards.", 22, 11, 30, 0));
        items.add(new Item(131L, "Powdered Diamond", "This diamond is in tiny shards.", 22, 11, 40, 0));
        items.add(new Item(132L, "Dragon dagger", "The deadliest dragon dagger!", 3, 7, 200, 50));
        items.add(new Item(133L, "Dragon sword", "Sharp as a dragon's wit.", 4, 7, 375, 50));
        items.add(new Item(134L, "Dragon longsword", "A very long, and very sharp, sword.", 5, 7, 375, 50));
        items.add(new Item(135L, "Dragon bow", "A striking red bow, with a hint of flames.", 6, 7, 375, 50));
        items.add(new Item(136L, "Dragon half shield", "Half shield or not, this will protect from a dragon's breath.", 7, 7, 550, 52));
        items.add(new Item(137L, "Dragon full shield", "Rumoured to be forged from dragon remains.", 8, 7, 550, 52));
        items.add(new Item(138L, "Dragon chainmail", "Unusually, small insects seem to herd around the item.", 9, 7, 550, 52));
        items.add(new Item(139L, "Dragon platebody", "The ultimate in heavy duty protection.", 10, 7, 900, 54));
        items.add(new Item(140L, "Dragon half helmet", "There'll be no headshots whilst this is worn.", 11, 7, 140, 54));
        items.add(new Item(141L, "Dragon full helmet", "The helmet almost seems to repel arrows.", 12, 7, 725, 56));
        items.add(new Item(142L, "Dragon boots", "I don't think a dragon's foot would fit in these...", 13, 7, 375, 56));
        items.add(new Item(143L, "Dragon gloves", "I'm certain a dragon's claw wouldn't fit in here...", 14, 7, 375, 56));
        items.add(new Item(144L, "Dragon pickaxe", "For mining the toughest of ores.", 15, 7, 375, 58));
        items.add(new Item(145L, "Dragon hatchet", "For felling the tallest great oaks", 16, 7, 375, 58));
        items.add(new Item(146L, "Dragon fishing spear", "Let's take the fight to the fishes!", 17, 7, 375, 58));
        items.add(new Item(147L, "Dragon hammer", "Want to avoid getting hammered? Use this!", 18, 7, 200, 58));
        items.add(new Item(148L, "Draconic visage", "The orb seems to pulsate with power.", 23, 20, 300, 50));
        items.add(new Item(149L, "Silver ring", "A plain ring of silver.", 24, 8, 25, 35));
        items.add(new Item(150L, "Silver sapphire ring", "A ring of silver, with a glittering blue gem.", 24, 8, 185, 40));
        items.add(new Item(151L, "Silver emerald ring", "An emerald-embossed silver ring.", 24, 8, 185, 40));
        items.add(new Item(152L, "Silver ruby ring", "A silver ring with a fine ruby in.", 24, 8, 185, 40));
        items.add(new Item(153L, "Silver diamond ring", "A fine silver ring with a precious white gem.", 24, 8, 285, 45));
        items.add(new Item(154L, "Silver onyx ring", "A mildly ominous silver and black ring.", 24, 8, 285, 45));
        items.add(new Item(155L, "Gold ring", "A plain ring of gold.", 24, 9, 40, 35));
        items.add(new Item(156L, "Gold sapphire ring", "A golden ring, with a stunningly large sapphire.", 24, 9, 200, 40));
        items.add(new Item(157L, "Gold emerald ring", "A ring of fine gold, with a glittering emerald.", 24, 9, 200, 40));
        items.add(new Item(158L, "Gold ruby ring", "An expensive looking gold and red ring.", 24, 9, 200, 40));
        items.add(new Item(159L, "Gold diamond ring", "An intimidatingly opulent ring.", 24, 9, 300, 45));
        items.add(new Item(160L, "Gold onyx ring", "The ultimate in finger decorations.", 24, 9, 300, 45));
        items.add(new Item(161L, "Legendary dagger", "The dagger whispers of murders past.", 3, 10, 1000, 50));
        items.add(new Item(162L, "Legendary sword", "An absurdly menacing blade.", 4, 10, 2000, 50));
        items.add(new Item(163L, "Legendary longsword", "For long distance damage.", 5, 10, 2000, 50));
        items.add(new Item(164L, "Legendary bow", "Fired arrows seem to hone in on the target.", 6, 10, 2000, 50));
        items.add(new Item(165L, "Legendary half shield", "No, two halves don't make a whole. Sorry.", 7, 10, 3000, 52));
        items.add(new Item(166L, "Legendary full shield", "Maximum protection, maximum style.", 8, 10, 3000, 52));
        items.add(new Item(167L, "Legendary chainmail", "All the protection, with all the mobility.", 9, 10, 3000, 52));
        items.add(new Item(168L, "Legendary platebody", "Distinctly better for not-dying than dragon armour.", 10, 10, 5000, 54));
        items.add(new Item(169L, "Legendary half helmet", "Fit for a king!", 11, 10, 1000, 54));
        items.add(new Item(170L, "Legendary full helmet", "Possibly the comfiest helmet ever made.", 12, 10, 4000, 56));
        items.add(new Item(171L, "Legendary boots", "These boots were made for stomping. On enemies.", 13, 10, 2000, 56));
        items.add(new Item(172L, "Legendary gloves", "These gloves seem to fuse to legendary weapons automatically.", 14, 10, 2000, 56));
        items.add(new Item(173L, "Legendary pickaxe", "Fragments of ore seem to be drawn to the powerful blade.", 15, 10, 3000, 58));
        items.add(new Item(174L, "Legendary hatchet", "Trees shake with fear when the axe draws closer.", 16, 10, 3000, 58));
        items.add(new Item(175L, "Legendary fishing spear", "Arguably, this could be used as a weapon...", 17, 10, 3000, 58));
        items.add(new Item(176L, "Legendary hammer", "Used for the forging of the mightiest equipment.", 18, 10, 2000, 58));

        Item.saveInTx(items);
    }

    private void createLocation() {
        List<Location> locations = new ArrayList<>();

        locations.add(new Location(1L, "Anvil"));
        locations.add(new Location(2L, "Furnace"));
        locations.add(new Location(3L, "Selling"));
        locations.add(new Location(4L, "Market"));
        locations.add(new Location(5L, "Table"));
        locations.add(new Location(6L, "Enchanting"));

        Location.saveInTx(locations);
    }

    private void createMessage() {
        Message introMessage = new Message(System.currentTimeMillis(), "This is the first message! After 99 other messages, you'll never see it again.");
        introMessage.save();
    }

    private void createPlayerInfo() {
        List<Player_Info> player_infos = new ArrayList<>();

        player_infos.add(new Player_Info("XP", Constants.STARTING_XP));
        player_infos.add(new Player_Info("", 0));
        player_infos.add(new Player_Info("ItemsSmelted", 0, 0));
        player_infos.add(new Player_Info("ItemsCrafted", 0));
        player_infos.add(new Player_Info("ItemsTraded", 0, 0));
        player_infos.add(new Player_Info("ItemsEnchanted", 0));
        player_infos.add(new Player_Info("ItemsSold", 0, 0));
        player_infos.add(new Player_Info("ItemsBought", 0, 0));
        player_infos.add(new Player_Info("VisitorsCompleted", 0, 0));
        player_infos.add(new Player_Info("DateRestocked", System.currentTimeMillis()));
        player_infos.add(new Player_Info("DateVisitorSpawned", System.currentTimeMillis()));
        player_infos.add(new Player_Info("CoinsEarned", 0, 0));
        player_infos.add(new Player_Info("DateStarted", System.currentTimeMillis()));
        player_infos.add(new Player_Info("SavedLevel", 1, 0));
        player_infos.add(new Player_Info("UpgradesBought", 0));
        player_infos.add(new Player_Info("Premium", 0));
        player_infos.add(new Player_Info("Prestige", 0, 0));
        player_infos.add(new Player_Info("DateLastPrestiged", 0L));

        Player_Info.saveInTx(player_infos);
    }

    private void createRecipe() {
        List<Recipe> recipes = new ArrayList<>();

        // Powdered gems
        recipes.add(new Recipe(129L, 1L, 73L, 1L, 1));
        recipes.add(new Recipe(130L, 1L, 74L, 1L, 1));
        recipes.add(new Recipe(131L, 1L, 75L, 1L, 1));

        // Silver Rings
        recipes.add(new Recipe(149L, 1L, 17L, 1L, 1));
        recipes.add(new Recipe(150L, 1L, 17L, 1L, 1));
        recipes.add(new Recipe(150L, 1L, 73L, 1L, 1));
        recipes.add(new Recipe(151L, 1L, 17L, 1L, 1));
        recipes.add(new Recipe(151L, 1L, 74L, 1L, 1));
        recipes.add(new Recipe(152L, 1L, 17L, 1L, 1));
        recipes.add(new Recipe(152L, 1L, 72L, 1L, 1));
        recipes.add(new Recipe(153L, 1L, 17L, 1L, 1));
        recipes.add(new Recipe(153L, 1L, 75L, 1L, 1));
        recipes.add(new Recipe(154L, 1L, 17L, 1L, 1));
        recipes.add(new Recipe(154L, 1L, 76L, 1L, 1));

        // Gold Rings
        recipes.add(new Recipe(155L, 1L, 18L, 1L, 1));
        recipes.add(new Recipe(156L, 1L, 18L, 1L, 1));
        recipes.add(new Recipe(156L, 1L, 73L, 1L, 1));
        recipes.add(new Recipe(157L, 1L, 18L, 1L, 1));
        recipes.add(new Recipe(157L, 1L, 74L, 1L, 1));
        recipes.add(new Recipe(158L, 1L, 18L, 1L, 1));
        recipes.add(new Recipe(158L, 1L, 72L, 1L, 1));
        recipes.add(new Recipe(159L, 1L, 18L, 1L, 1));
        recipes.add(new Recipe(159L, 1L, 75L, 1L, 1));
        recipes.add(new Recipe(160L, 1L, 18L, 1L, 1));
        recipes.add(new Recipe(160L, 1L, 76L, 1L, 1));

        // Bars
        recipes.add(new Recipe(11L, 1L, 1L, 1L, 1));
        recipes.add(new Recipe(11L, 1L, 2L, 1L, 1));
        recipes.add(new Recipe(12L, 1L, 4L, 1L, 2));
        recipes.add(new Recipe(13L, 1L, 4L, 1L, 2));
        recipes.add(new Recipe(13L, 1L, 3L, 1L, 1));
        recipes.add(new Recipe(14L, 1L, 5L, 1L, 1));
        recipes.add(new Recipe(14L, 1L, 3L, 1L, 2));
        recipes.add(new Recipe(15L, 1L, 6L, 1L, 1));
        recipes.add(new Recipe(15L, 1L, 3L, 1L, 4));
        recipes.add(new Recipe(16L, 1L, 7L, 1L, 1));
        recipes.add(new Recipe(16L, 1L, 3L, 1L, 8));
        recipes.add(new Recipe(17L, 1L, 9L, 1L, 1));
        recipes.add(new Recipe(18L, 1L, 8L, 1L, 1));
        recipes.add(new Recipe(19L, 1L, 10L, 1L, 1));
        recipes.add(new Recipe(19L, 1L, 3L, 1L, 12));

        // Bronze unfinished
        recipes.add(new Recipe(20L, 2L, 11L, 1L, 1));
        recipes.add(new Recipe(21L, 2L, 11L, 1L, 2));
        recipes.add(new Recipe(22L, 2L, 11L, 1L, 2));
        recipes.add(new Recipe(23L, 2L, 11L, 1L, 2));
        recipes.add(new Recipe(24L, 2L, 11L, 1L, 3));
        recipes.add(new Recipe(25L, 2L, 11L, 1L, 3));
        recipes.add(new Recipe(26L, 2L, 11L, 1L, 3));
        recipes.add(new Recipe(27L, 2L, 11L, 1L, 5));
        recipes.add(new Recipe(28L, 2L, 11L, 1L, 3));
        recipes.add(new Recipe(29L, 2L, 11L, 1L, 4));
        recipes.add(new Recipe(30L, 2L, 11L, 1L, 2));
        recipes.add(new Recipe(31L, 2L, 11L, 1L, 2));
        recipes.add(new Recipe(32L, 2L, 11L, 1L, 2));
        recipes.add(new Recipe(33L, 2L, 11L, 1L, 2));
        recipes.add(new Recipe(34L, 2L, 11L, 1L, 2));
        recipes.add(new Recipe(35L, 2L, 11L, 1L, 1));

        // Iron unfinished
        recipes.add(new Recipe(36L, 2L, 12L, 1L, 1));
        recipes.add(new Recipe(37L, 2L, 12L, 1L, 2));
        recipes.add(new Recipe(38L, 2L, 12L, 1L, 2));
        recipes.add(new Recipe(39L, 2L, 12L, 1L, 2));
        recipes.add(new Recipe(40L, 2L, 12L, 1L, 3));
        recipes.add(new Recipe(41L, 2L, 12L, 1L, 3));
        recipes.add(new Recipe(42L, 2L, 12L, 1L, 3));
        recipes.add(new Recipe(43L, 2L, 12L, 1L, 5));
        recipes.add(new Recipe(44L, 2L, 12L, 1L, 3));
        recipes.add(new Recipe(45L, 2L, 12L, 1L, 4));
        recipes.add(new Recipe(46L, 2L, 12L, 1L, 2));
        recipes.add(new Recipe(47L, 2L, 12L, 1L, 2));
        recipes.add(new Recipe(48L, 2L, 12L, 1L, 2));
        recipes.add(new Recipe(49L, 2L, 12L, 1L, 2));
        recipes.add(new Recipe(50L, 2L, 12L, 1L, 2));
        recipes.add(new Recipe(51L, 2L, 12L, 1L, 1));

        // Steel unfinished
        recipes.add(new Recipe(53L, 2L, 13L, 1L, 1));
        recipes.add(new Recipe(54L, 2L, 13L, 1L, 2));
        recipes.add(new Recipe(55L, 2L, 13L, 1L, 2));
        recipes.add(new Recipe(56L, 2L, 13L, 1L, 2));
        recipes.add(new Recipe(57L, 2L, 13L, 1L, 3));
        recipes.add(new Recipe(58L, 2L, 13L, 1L, 3));
        recipes.add(new Recipe(59L, 2L, 13L, 1L, 3));
        recipes.add(new Recipe(60L, 2L, 13L, 1L, 5));
        recipes.add(new Recipe(61L, 2L, 13L, 1L, 3));
        recipes.add(new Recipe(62L, 2L, 13L, 1L, 4));
        recipes.add(new Recipe(63L, 2L, 13L, 1L, 2));
        recipes.add(new Recipe(64L, 2L, 13L, 1L, 2));
        recipes.add(new Recipe(65L, 2L, 13L, 1L, 2));
        recipes.add(new Recipe(66L, 2L, 13L, 1L, 2));
        recipes.add(new Recipe(67L, 2L, 13L, 1L, 2));
        recipes.add(new Recipe(68L, 2L, 13L, 1L, 1));

        // Mithril unfinished
        recipes.add(new Recipe(81L, 2L, 14L, 1L, 1));
        recipes.add(new Recipe(82L, 2L, 14L, 1L, 2));
        recipes.add(new Recipe(83L, 2L, 14L, 1L, 2));
        recipes.add(new Recipe(84L, 2L, 14L, 1L, 2));
        recipes.add(new Recipe(85L, 2L, 14L, 1L, 3));
        recipes.add(new Recipe(86L, 2L, 14L, 1L, 3));
        recipes.add(new Recipe(87L, 2L, 14L, 1L, 3));
        recipes.add(new Recipe(88L, 2L, 14L, 1L, 5));
        recipes.add(new Recipe(89L, 2L, 14L, 1L, 3));
        recipes.add(new Recipe(90L, 2L, 14L, 1L, 4));
        recipes.add(new Recipe(91L, 2L, 14L, 1L, 2));
        recipes.add(new Recipe(92L, 2L, 14L, 1L, 2));
        recipes.add(new Recipe(93L, 2L, 14L, 1L, 2));
        recipes.add(new Recipe(94L, 2L, 14L, 1L, 2));
        recipes.add(new Recipe(95L, 2L, 14L, 1L, 2));
        recipes.add(new Recipe(96L, 2L, 14L, 1L, 1));

        // Adamant unfinished
        recipes.add(new Recipe(97L, 2L, 15L, 1L, 1));
        recipes.add(new Recipe(98L, 2L, 15L, 1L, 2));
        recipes.add(new Recipe(99L, 2L, 15L, 1L, 2));
        recipes.add(new Recipe(100L, 2L, 15L, 1L, 2));
        recipes.add(new Recipe(101L, 2L, 15L, 1L, 3));
        recipes.add(new Recipe(102L, 2L, 15L, 1L, 3));
        recipes.add(new Recipe(103L, 2L, 15L, 1L, 3));
        recipes.add(new Recipe(104L, 2L, 15L, 1L, 5));
        recipes.add(new Recipe(105L, 2L, 15L, 1L, 3));
        recipes.add(new Recipe(106L, 2L, 15L, 1L, 4));
        recipes.add(new Recipe(107L, 2L, 15L, 1L, 2));
        recipes.add(new Recipe(108L, 2L, 15L, 1L, 2));
        recipes.add(new Recipe(109L, 2L, 15L, 1L, 2));
        recipes.add(new Recipe(110L, 2L, 15L, 1L, 2));
        recipes.add(new Recipe(111L, 2L, 15L, 1L, 2));
        recipes.add(new Recipe(112L, 2L, 15L, 1L, 1));

        // Rune unfinished
        recipes.add(new Recipe(113L, 2L, 16L, 1L, 1));
        recipes.add(new Recipe(114L, 2L, 16L, 1L, 2));
        recipes.add(new Recipe(115L, 2L, 16L, 1L, 2));
        recipes.add(new Recipe(116L, 2L, 16L, 1L, 2));
        recipes.add(new Recipe(117L, 2L, 16L, 1L, 3));
        recipes.add(new Recipe(118L, 2L, 16L, 1L, 3));
        recipes.add(new Recipe(119L, 2L, 16L, 1L, 3));
        recipes.add(new Recipe(120L, 2L, 16L, 1L, 5));
        recipes.add(new Recipe(121L, 2L, 16L, 1L, 3));
        recipes.add(new Recipe(122L, 2L, 16L, 1L, 4));
        recipes.add(new Recipe(123L, 2L, 16L, 1L, 2));
        recipes.add(new Recipe(124L, 2L, 16L, 1L, 2));
        recipes.add(new Recipe(125L, 2L, 16L, 1L, 2));
        recipes.add(new Recipe(126L, 2L, 16L, 1L, 2));
        recipes.add(new Recipe(127L, 2L, 16L, 1L, 2));
        recipes.add(new Recipe(128L, 2L, 16L, 1L, 1));

        // Dragon unfinished
        recipes.add(new Recipe(132L, 2L, 19L, 1L, 1));
        recipes.add(new Recipe(133L, 2L, 19L, 1L, 2));
        recipes.add(new Recipe(134L, 2L, 19L, 1L, 2));
        recipes.add(new Recipe(135L, 2L, 19L, 1L, 2));
        recipes.add(new Recipe(136L, 2L, 19L, 1L, 3));
        recipes.add(new Recipe(137L, 2L, 19L, 1L, 3));
        recipes.add(new Recipe(138L, 2L, 19L, 1L, 3));
        recipes.add(new Recipe(139L, 2L, 19L, 1L, 5));
        recipes.add(new Recipe(140L, 2L, 19L, 1L, 3));
        recipes.add(new Recipe(141L, 2L, 19L, 1L, 4));
        recipes.add(new Recipe(142L, 2L, 19L, 1L, 2));
        recipes.add(new Recipe(143L, 2L, 19L, 1L, 2));
        recipes.add(new Recipe(144L, 2L, 19L, 1L, 2));
        recipes.add(new Recipe(145L, 2L, 19L, 1L, 2));
        recipes.add(new Recipe(146L, 2L, 19L, 1L, 2));
        recipes.add(new Recipe(147L, 2L, 19L, 1L, 1));

        // Bronze finished
        recipes.add(new Recipe(20L, 1L, 20L, 2L, 1));
        recipes.add(new Recipe(20L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(21L, 1L, 21L, 2L, 1));
        recipes.add(new Recipe(21L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(22L, 1L, 22L, 2L, 1));
        recipes.add(new Recipe(22L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(23L, 1L, 23L, 2L, 1));
        recipes.add(new Recipe(23L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(24L, 1L, 24L, 2L, 1));
        recipes.add(new Recipe(24L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(25L, 1L, 25L, 2L, 1));
        recipes.add(new Recipe(25L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(26L, 1L, 26L, 2L, 1));
        recipes.add(new Recipe(26L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(27L, 1L, 27L, 2L, 1));
        recipes.add(new Recipe(27L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(28L, 1L, 28L, 2L, 1));
        recipes.add(new Recipe(28L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(29L, 1L, 29L, 2L, 1));
        recipes.add(new Recipe(29L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(30L, 1L, 30L, 2L, 1));
        recipes.add(new Recipe(30L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(31L, 1L, 31L, 2L, 1));
        recipes.add(new Recipe(31L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(32L, 1L, 32L, 2L, 1));
        recipes.add(new Recipe(32L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(33L, 1L, 33L, 2L, 1));
        recipes.add(new Recipe(33L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(34L, 1L, 34L, 2L, 1));
        recipes.add(new Recipe(34L, 1L, 69L, 1L, 1));
        recipes.add(new Recipe(35L, 1L, 35L, 2L, 1));
        recipes.add(new Recipe(35L, 1L, 69L, 1L, 1));

        // Iron finished
        recipes.add(new Recipe(36L, 1L, 36L, 2L, 1));
        recipes.add(new Recipe(36L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(37L, 1L, 37L, 2L, 1));
        recipes.add(new Recipe(37L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(38L, 1L, 38L, 2L, 1));
        recipes.add(new Recipe(38L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(39L, 1L, 39L, 2L, 1));
        recipes.add(new Recipe(39L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(40L, 1L, 40L, 2L, 1));
        recipes.add(new Recipe(40L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(41L, 1L, 41L, 2L, 1));
        recipes.add(new Recipe(41L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(42L, 1L, 42L, 2L, 1));
        recipes.add(new Recipe(42L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(43L, 1L, 43L, 2L, 1));
        recipes.add(new Recipe(43L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(44L, 1L, 44L, 2L, 1));
        recipes.add(new Recipe(44L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(45L, 1L, 45L, 2L, 1));
        recipes.add(new Recipe(45L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(46L, 1L, 46L, 2L, 1));
        recipes.add(new Recipe(46L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(47L, 1L, 47L, 2L, 1));
        recipes.add(new Recipe(47L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(48L, 1L, 48L, 2L, 1));
        recipes.add(new Recipe(48L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(49L, 1L, 49L, 2L, 1));
        recipes.add(new Recipe(49L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(50L, 1L, 50L, 2L, 1));
        recipes.add(new Recipe(50L, 1L, 70L, 1L, 1));
        recipes.add(new Recipe(51L, 1L, 51L, 2L, 1));
        recipes.add(new Recipe(51L, 1L, 70L, 1L, 1));

        // Steel finished
        recipes.add(new Recipe(53L, 1L, 53L, 2L, 1));
        recipes.add(new Recipe(53L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(54L, 1L, 54L, 2L, 1));
        recipes.add(new Recipe(54L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(55L, 1L, 55L, 2L, 1));
        recipes.add(new Recipe(55L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(56L, 1L, 56L, 2L, 1));
        recipes.add(new Recipe(56L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(57L, 1L, 57L, 2L, 1));
        recipes.add(new Recipe(57L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(58L, 1L, 58L, 2L, 1));
        recipes.add(new Recipe(58L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(59L, 1L, 59L, 2L, 1));
        recipes.add(new Recipe(59L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(60L, 1L, 60L, 2L, 1));
        recipes.add(new Recipe(60L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(61L, 1L, 61L, 2L, 1));
        recipes.add(new Recipe(61L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(62L, 1L, 62L, 2L, 1));
        recipes.add(new Recipe(62L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(63L, 1L, 63L, 2L, 1));
        recipes.add(new Recipe(63L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(64L, 1L, 64L, 2L, 1));
        recipes.add(new Recipe(64L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(65L, 1L, 65L, 2L, 1));
        recipes.add(new Recipe(65L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(66L, 1L, 66L, 2L, 1));
        recipes.add(new Recipe(66L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(67L, 1L, 67L, 2L, 1));
        recipes.add(new Recipe(67L, 1L, 71L, 1L, 1));
        recipes.add(new Recipe(68L, 1L, 68L, 2L, 1));
        recipes.add(new Recipe(68L, 1L, 71L, 1L, 1));

        // Mithril finished
        recipes.add(new Recipe(81L, 1L, 81L, 2L, 1));
        recipes.add(new Recipe(81L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(82L, 1L, 82L, 2L, 1));
        recipes.add(new Recipe(82L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(83L, 1L, 83L, 2L, 1));
        recipes.add(new Recipe(83L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(84L, 1L, 84L, 2L, 1));
        recipes.add(new Recipe(84L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(85L, 1L, 85L, 2L, 1));
        recipes.add(new Recipe(85L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(86L, 1L, 86L, 2L, 1));
        recipes.add(new Recipe(86L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(87L, 1L, 87L, 2L, 1));
        recipes.add(new Recipe(87L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(88L, 1L, 88L, 2L, 1));
        recipes.add(new Recipe(88L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(89L, 1L, 89L, 2L, 1));
        recipes.add(new Recipe(89L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(90L, 1L, 90L, 2L, 1));
        recipes.add(new Recipe(90L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(91L, 1L, 91L, 2L, 1));
        recipes.add(new Recipe(91L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(92L, 1L, 92L, 2L, 1));
        recipes.add(new Recipe(92L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(93L, 1L, 93L, 2L, 1));
        recipes.add(new Recipe(93L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(94L, 1L, 94L, 2L, 1));
        recipes.add(new Recipe(94L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(95L, 1L, 95L, 2L, 1));
        recipes.add(new Recipe(95L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(96L, 1L, 96L, 2L, 1));
        recipes.add(new Recipe(96L, 1L, 129L, 1L, 1));

        // Adamant finished
        recipes.add(new Recipe(97L, 1L, 97L, 2L, 1));
        recipes.add(new Recipe(97L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(98L, 1L, 98L, 2L, 1));
        recipes.add(new Recipe(98L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(99L, 1L, 99L, 2L, 1));
        recipes.add(new Recipe(99L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(100L, 1L, 100L, 2L, 1));
        recipes.add(new Recipe(100L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(101L, 1L, 101L, 2L, 1));
        recipes.add(new Recipe(101L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(102L, 1L, 102L, 2L, 1));
        recipes.add(new Recipe(102L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(103L, 1L, 103L, 2L, 1));
        recipes.add(new Recipe(103L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(104L, 1L, 104L, 2L, 1));
        recipes.add(new Recipe(104L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(105L, 1L, 105L, 2L, 1));
        recipes.add(new Recipe(105L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(106L, 1L, 106L, 2L, 1));
        recipes.add(new Recipe(106L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(107L, 1L, 107L, 2L, 1));
        recipes.add(new Recipe(107L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(108L, 1L, 108L, 2L, 1));
        recipes.add(new Recipe(108L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(109L, 1L, 109L, 2L, 1));
        recipes.add(new Recipe(109L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(110L, 1L, 110L, 2L, 1));
        recipes.add(new Recipe(110L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(111L, 1L, 111L, 2L, 1));
        recipes.add(new Recipe(111L, 1L, 130L, 1L, 1));
        recipes.add(new Recipe(112L, 1L, 112L, 2L, 1));
        recipes.add(new Recipe(112L, 1L, 130L, 1L, 1));

        // Rune finished
        recipes.add(new Recipe(113L, 1L, 113L, 2L, 1));
        recipes.add(new Recipe(113L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(113L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(114L, 1L, 114L, 2L, 1));
        recipes.add(new Recipe(114L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(114L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(115L, 1L, 115L, 2L, 1));
        recipes.add(new Recipe(115L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(115L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(116L, 1L, 116L, 2L, 1));
        recipes.add(new Recipe(116L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(116L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(117L, 1L, 117L, 2L, 1));
        recipes.add(new Recipe(117L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(117L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(118L, 1L, 118L, 2L, 1));
        recipes.add(new Recipe(118L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(118L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(119L, 1L, 119L, 2L, 1));
        recipes.add(new Recipe(119L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(119L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(120L, 1L, 120L, 2L, 1));
        recipes.add(new Recipe(120L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(120L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(121L, 1L, 121L, 2L, 1));
        recipes.add(new Recipe(121L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(121L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(122L, 1L, 122L, 2L, 1));
        recipes.add(new Recipe(122L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(122L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(123L, 1L, 123L, 2L, 1));
        recipes.add(new Recipe(123L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(123L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(124L, 1L, 124L, 2L, 1));
        recipes.add(new Recipe(124L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(124L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(125L, 1L, 125L, 2L, 1));
        recipes.add(new Recipe(125L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(125L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(126L, 1L, 126L, 2L, 1));
        recipes.add(new Recipe(126L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(126L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(127L, 1L, 127L, 2L, 1));
        recipes.add(new Recipe(127L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(127L, 1L, 131L, 1L, 1));
        recipes.add(new Recipe(128L, 1L, 128L, 2L, 1));
        recipes.add(new Recipe(128L, 1L, 129L, 1L, 1));
        recipes.add(new Recipe(128L, 1L, 131L, 1L, 1));

        // Dragon finished
        recipes.add(new Recipe(132L, 1L, 132L, 2L, 1));
        recipes.add(new Recipe(132L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(133L, 1L, 133L, 2L, 1));
        recipes.add(new Recipe(133L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(134L, 1L, 134L, 2L, 1));
        recipes.add(new Recipe(134L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(135L, 1L, 135L, 2L, 1));
        recipes.add(new Recipe(135L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(136L, 1L, 136L, 2L, 1));
        recipes.add(new Recipe(136L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(137L, 1L, 137L, 2L, 1));
        recipes.add(new Recipe(137L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(138L, 1L, 138L, 2L, 1));
        recipes.add(new Recipe(138L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(139L, 1L, 139L, 2L, 1));
        recipes.add(new Recipe(139L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(140L, 1L, 140L, 2L, 1));
        recipes.add(new Recipe(140L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(141L, 1L, 141L, 2L, 1));
        recipes.add(new Recipe(141L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(142L, 1L, 142L, 2L, 1));
        recipes.add(new Recipe(142L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(143L, 1L, 143L, 2L, 1));
        recipes.add(new Recipe(143L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(144L, 1L, 144L, 2L, 1));
        recipes.add(new Recipe(144L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(145L, 1L, 145L, 2L, 1));
        recipes.add(new Recipe(145L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(146L, 1L, 146L, 2L, 1));
        recipes.add(new Recipe(146L, 1L, 148L, 1L, 1));
        recipes.add(new Recipe(147L, 1L, 147L, 2L, 1));
        recipes.add(new Recipe(147L, 1L, 148L, 1L, 1));

        // Legendary finished
        recipes.add(new Recipe(161L, 1L, 161L, 2L, 1));
        recipes.add(new Recipe(162L, 1L, 162L, 2L, 2));
        recipes.add(new Recipe(163L, 1L, 163L, 2L, 2));
        recipes.add(new Recipe(164L, 1L, 164L, 2L, 2));
        recipes.add(new Recipe(165L, 1L, 165L, 2L, 3));
        recipes.add(new Recipe(166L, 1L, 166L, 2L, 3));
        recipes.add(new Recipe(167L, 1L, 167L, 2L, 3));
        recipes.add(new Recipe(168L, 1L, 168L, 2L, 5));
        recipes.add(new Recipe(169L, 1L, 169L, 2L, 1));
        recipes.add(new Recipe(170L, 1L, 170L, 2L, 4));
        recipes.add(new Recipe(171L, 1L, 171L, 2L, 2));
        recipes.add(new Recipe(172L, 1L, 172L, 2L, 2));
        recipes.add(new Recipe(173L, 1L, 173L, 2L, 3));
        recipes.add(new Recipe(174L, 1L, 174L, 2L, 3));
        recipes.add(new Recipe(175L, 1L, 175L, 2L, 3));
        recipes.add(new Recipe(176L, 1L, 176L, 2L, 2));

        Recipe.saveInTx(recipes);
    }

    private void createSetting() {
        List<Setting> settings = new ArrayList<>();

        settings.add(new Setting(1L, "Sounds", true));
        settings.add(new Setting(2L, "Music", true));
        settings.add(new Setting(3L, "RestockNotifications", true));
        settings.add(new Setting(4L, "NotificationSounds", true));
        settings.add(new Setting(5L, "VisitorNotifications", true));
        settings.add(new Setting(6L, "TrySignIn", true));

        Setting.saveInTx(settings);
    }

    private void createSlot() {
        List<Slot> slots = new ArrayList<>();

        slots.add(new Slot(1, 1, false));
        slots.add(new Slot(1, 3, false));
        slots.add(new Slot(1, 7, false));
        slots.add(new Slot(1, 16, false));
        slots.add(new Slot(1, 25, false));
        slots.add(new Slot(1, 33, false));
        slots.add(new Slot(1, 42, false));
        slots.add(new Slot(1, 1, true));
        slots.add(new Slot(2, 1, false));
        slots.add(new Slot(2, 4, false));
        slots.add(new Slot(2, 8, false));
        slots.add(new Slot(2, 17, false));
        slots.add(new Slot(2, 24, false));
        slots.add(new Slot(2, 30, false));
        slots.add(new Slot(2, 44, false));
        slots.add(new Slot(2, 1, true));
        slots.add(new Slot(3, 1, false));
        slots.add(new Slot(3, 5, false));
        slots.add(new Slot(3, 13, false));
        slots.add(new Slot(3, 20, false));
        slots.add(new Slot(3, 35, false));
        slots.add(new Slot(3, 1, true));
        slots.add(new Slot(4, 1, false));
        slots.add(new Slot(4, 6, false));
        slots.add(new Slot(4, 10, false));
        slots.add(new Slot(4, 23, false));
        slots.add(new Slot(4, 40, false));
        slots.add(new Slot(4, 1, true));
        slots.add(new Slot(5, 1, false));
        slots.add(new Slot(5, 5, false));
        slots.add(new Slot(5, 10, false));
        slots.add(new Slot(5, 15, false));
        slots.add(new Slot(5, 27, false));
        slots.add(new Slot(5, 39, false));
        slots.add(new Slot(5, 50, false));
        slots.add(new Slot(5, 1, true));
        slots.add(new Slot(6, 1, false));
        slots.add(new Slot(6, 20, false));
        slots.add(new Slot(6, 30, false));
        slots.add(new Slot(6, 40, false));
        slots.add(new Slot(6, 50, false));
        slots.add(new Slot(6, 60, false));
        slots.add(new Slot(6, 70, false));
        slots.add(new Slot(6, 1, true));

        Slot.saveInTx(slots);
    }

    private void createState() {
        List<State> states = new ArrayList<>();

        states.add(new State(1L, "Normal", "", 0L, 0, 15));
        states.add(new State(2L, "Unfinished", "(unf) ", 0L, 0, 15));
        states.add(new State(3L, "Red Enchant", "(red) ", 72L, 10, 2));
        states.add(new State(4L, "Blue Enchant", "(blue) ", 73L, 15, 2));
        states.add(new State(5L, "Green Enchant", "(green) ", 74L, 20, 1));
        states.add(new State(6L, "White Enchant", "(white) ", 75L, 25, 1));
        states.add(new State(7L, "Black Enchant", "(black) ", 76L, 30, 1));

        State.saveInTx(states);
    }

    private void createTier() {
        List<Tier> tiers = new ArrayList<>();

        tiers.add(new Tier(1L, "Bronze", 1, 30));
        tiers.add(new Tier(2L, "Iron", 5, 25));
        tiers.add(new Tier(3L, "Steel", 10, 15));
        tiers.add(new Tier(4L, "Mithril", 20, 10));
        tiers.add(new Tier(5L, "Adamant", 30, 7));
        tiers.add(new Tier(6L, "Rune", 40, 4));
        tiers.add(new Tier(7L, "Dragon", 50, 2));
        tiers.add(new Tier(8L, "Silver", 35, 10));
        tiers.add(new Tier(9L, "Gold", 45, 10));
        tiers.add(new Tier(10L, "Premium", 1, 0));
        tiers.add(new Tier(11L, "None", 1, 35));

        Tier.saveInTx(tiers);
    }

    private void createTrader() {
        List<Trader> traders = new ArrayList<>();
        List<Trader_Stock> trader_stocks = new ArrayList<>();

        traders.add(new Trader(1L, 4, "The Scraps", "I was gonna chuck this stuff out.. you interested?", 0, 0, 0, 60));
        trader_stocks.add(new Trader_Stock(1L, 1L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(1L, 1L, 1, 50, 35));
        trader_stocks.add(new Trader_Stock(1L, 2L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(1L, 2L, 1, 50, 35));
        trader_stocks.add(new Trader_Stock(1L, 11L, 1, 0, 3));
        trader_stocks.add(new Trader_Stock(1L, 11L, 1, 30, 20));

        traders.add(new Trader(1L, 4, "Lots More Ore", "Lots and and lots and lots of ore!", 0, 0, 0, 100));
        trader_stocks.add(new Trader_Stock(2L, 1L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(2L, 1L, 1, 70, 30));
        trader_stocks.add(new Trader_Stock(2L, 2L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(2L, 2L, 1, 70, 30));
        trader_stocks.add(new Trader_Stock(2L, 3L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(2L, 3L, 1, 120, 20));
        trader_stocks.add(new Trader_Stock(2L, 4L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(2L, 4L, 1, 120, 20));

        traders.add(new Trader(1L, 4, "The Off Cuts", "This stuff isn't the best, but it'll do.", 5, 0, 0, 40));
        trader_stocks.add(new Trader_Stock(3L, 3L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(3L, 3L, 1, 20, 15));
        trader_stocks.add(new Trader_Stock(3L, 3L, 1, 100, 75));

        traders.add(new Trader(1L, 4, "The Backbone", "You're gonna like what I've got. I think.", 10, 0, 0, 15));
        trader_stocks.add(new Trader_Stock(4L, 5L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(4L, 5L, 1, 100, 120));
        trader_stocks.add(new Trader_Stock(4L, 6L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(4L, 6L, 1, 100, 75));
        trader_stocks.add(new Trader_Stock(4L, 14L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(4L, 14L, 1, 30, 18));
        trader_stocks.add(new Trader_Stock(4L, 14L, 1, 120, 40));
        trader_stocks.add(new Trader_Stock(4L, 15L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(4L, 15L, 1, 30, 6));
        trader_stocks.add(new Trader_Stock(4L, 15L, 1, 100, 20));

        traders.add(new Trader(1L, 4, "The Prime Cuts", "My very best stock, for the connoisseur.", 15, 0, 0, 5));
        trader_stocks.add(new Trader_Stock(5L, 7L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(5L, 7L, 1, 30, 15));
        trader_stocks.add(new Trader_Stock(5L, 16L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(5L, 16L, 1, 100, 10));

        traders.add(new Trader(2L, 4, "The Iron Throne", "I rule my iron with an iron fist!", 0, 0, 0, 5));
        trader_stocks.add(new Trader_Stock(6L, 4L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(6L, 4L, 1, 11, 25));
        trader_stocks.add(new Trader_Stock(6L, 4L, 1, 130, 90));
        trader_stocks.add(new Trader_Stock(6L, 12L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(6L, 12L, 1, 85, 10));

        traders.add(new Trader(2L, 4, "The Iron Fist", "I rule my iron from an iron throne!", 25, 0, 0, 1));
        trader_stocks.add(new Trader_Stock(7L, 4L, 1, 0, 25));
        trader_stocks.add(new Trader_Stock(7L, 4L, 1, 70, 90));
        trader_stocks.add(new Trader_Stock(7L, 4L, 1, 130, 200));
        trader_stocks.add(new Trader_Stock(7L, 12L, 1, 0, 15));
        trader_stocks.add(new Trader_Stock(7L, 12L, 1, 60, 100));

        traders.add(new Trader(3L, 4, "The Nuggets", "Check out these lil shiners.", 5, 0, 0, 1));
        trader_stocks.add(new Trader_Stock(8L, 9L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(8L, 9L, 1, 25, 15));
        trader_stocks.add(new Trader_Stock(8L, 17L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(8L, 17L, 1, 25, 10));

        traders.add(new Trader(3L, 4, "The Nougat", "Panned these all myself, honest!", 15, 0, 0, 1));
        trader_stocks.add(new Trader_Stock(9L, 9L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(9L, 9L, 1, 25, 15));
        trader_stocks.add(new Trader_Stock(9L, 8L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(9L, 8L, 1, 50, 30));

        traders.add(new Trader(3L, 4, "The Golden Boulders", "Check out these lil shiners.", 35, 0, 0, 1));
        trader_stocks.add(new Trader_Stock(10L, 8L, 1, 0, 30));
        trader_stocks.add(new Trader_Stock(10L, 8L, 1, 100, 150));
        trader_stocks.add(new Trader_Stock(10L, 18L, 1, 0, 15));
        trader_stocks.add(new Trader_Stock(10L, 18L, 1, 50, 100));

        traders.add(new Trader(4L, 4, "A Straight Cut", "Definitely not off the back of a cart.", 0, 0, 0, 10));
        trader_stocks.add(new Trader_Stock(11L, 69L, 1, 0, 15));
        trader_stocks.add(new Trader_Stock(11L, 69L, 1, 35, 50));

        traders.add(new Trader(4L, 4, "A Finer Cut", "Reams upon reams of high quality cloth.", 15, 0, 0, 2));
        trader_stocks.add(new Trader_Stock(12L, 70L, 1, 0, 15));
        trader_stocks.add(new Trader_Stock(12L, 70L, 1, 35, 50));

        traders.add(new Trader(5L, 4, "Logged In", "LOGS! LOOOGS! LOTSA LOOOOOOGS!!!", 0, 0, 0, 30));
        trader_stocks.add(new Trader_Stock(13L, 71L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(13L, 71L, 1, 50, 25));
        trader_stocks.add(new Trader_Stock(13L, 71L, 1, 200, 40));

        traders.add(new Trader(6L, 4, "Oog", "OOG. OOG. OOG.", 15, 0, 0, 3));
        trader_stocks.add(new Trader_Stock(14L, 71L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(14L, 3L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(14L, 1L, 1, 0, 2));

        traders.add(new Trader(7L, 4, "GemBooth 1000", "Bzzt! Buy gems!", 10, 0, 0, 3));
        trader_stocks.add(new Trader_Stock(15L, 72L, 1, 0, 1));
        trader_stocks.add(new Trader_Stock(15L, 72L, 1, 10, 3));
        trader_stocks.add(new Trader_Stock(15L, 73L, 1, 0, 1));
        trader_stocks.add(new Trader_Stock(15L, 73L, 1, 10, 3));
        trader_stocks.add(new Trader_Stock(15L, 74L, 1, 0, 1));
        trader_stocks.add(new Trader_Stock(15L, 74L, 1, 10, 3));

        traders.add(new Trader(7L, 4, "GemEmporium 5000", "Bzzt! Buy BETTER gems!", 25, 0, 0, 2));
        trader_stocks.add(new Trader_Stock(16L, 75L, 1, 0, 1));
        trader_stocks.add(new Trader_Stock(16L, 75L, 1, 10, 2));
        trader_stocks.add(new Trader_Stock(16L, 76L, 1, 0, 1));
        trader_stocks.add(new Trader_Stock(16L, 75L, 1, 10, 2));

        traders.add(new Trader(7L, 4, "GemCrusher 9000", "Bzzt! Buy BROKEN gems!", 25, 0, 0, 4));
        trader_stocks.add(new Trader_Stock(17L, 129L, 1, 0, 1));
        trader_stocks.add(new Trader_Stock(17L, 129L, 1, 10, 2));
        trader_stocks.add(new Trader_Stock(17L, 130L, 1, 0, 1));
        trader_stocks.add(new Trader_Stock(17L, 130L, 1, 10, 2));
        trader_stocks.add(new Trader_Stock(17L, 131L, 1, 0, 1));
        trader_stocks.add(new Trader_Stock(17L, 131L, 1, 20, 5));

        traders.add(new Trader(8L, 4, "Farmyard Funland", "All the fun of the farm!", 5, 0, 0, 25));
        trader_stocks.add(new Trader_Stock(18L, 77L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(18L, 77L, 1, 20, 20));
        trader_stocks.add(new Trader_Stock(18L, 80L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(18L, 80L, 1, 20, 20));

        traders.add(new Trader(8L, 4, "Baker's Dozen", "What, a plant can't run a business?", 5, 0, 0, 25));
        trader_stocks.add(new Trader_Stock(19L, 78L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(19L, 78L, 1, 20, 20));
        trader_stocks.add(new Trader_Stock(19L, 79L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(19L, 79L, 1, 20, 20));

        traders.add(new Trader(9L, 4, "The Exclusive Emporium", "You won't find any of this anywhere else!", 30, 0, 0, 1));
        trader_stocks.add(new Trader_Stock(20L, 148L, 1, 0, 1));

        traders.add(new Trader(10L, 4, "The Little Ore Shop", "A little ore in store, you it is for!", 0, 0, 0, 50));
        trader_stocks.add(new Trader_Stock(21L, 1L, 1, 0, 3));
        trader_stocks.add(new Trader_Stock(21L, 1L, 1, 10, 10));
        trader_stocks.add(new Trader_Stock(21L, 2L, 1, 0, 3));
        trader_stocks.add(new Trader_Stock(21L, 2L, 1, 10, 10));
        trader_stocks.add(new Trader_Stock(21L, 3L, 1, 0, 3));
        trader_stocks.add(new Trader_Stock(21L, 3L, 1, 10, 10));

        traders.add(new Trader(10L, 4, "The Little Bar Shop", "Some bars inside, no need to hide.", 0, 0, 0, 50));
        trader_stocks.add(new Trader_Stock(22L, 11L, 1, 0, 3));
        trader_stocks.add(new Trader_Stock(22L, 11L, 1, 10, 8));
        trader_stocks.add(new Trader_Stock(22L, 12L, 1, 0, 3));
        trader_stocks.add(new Trader_Stock(22L, 12L, 1, 10, 8));

        traders.add(new Trader(10L, 4, "The Bigger Ore Shop", "Ore? A lot, that's what I've got.", 10, 0, 0, 25));
        trader_stocks.add(new Trader_Stock(23L, 1L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(23L, 1L, 1, 20, 20));
        trader_stocks.add(new Trader_Stock(23L, 2L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(23L, 2L, 1, 20, 20));
        trader_stocks.add(new Trader_Stock(23L, 3L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(23L, 3L, 1, 20, 20));
        trader_stocks.add(new Trader_Stock(23L, 4L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(23L, 4L, 1, 20, 20));

        traders.add(new Trader(10L, 4, "The Bigger Bar Shop", "Lots more bars, don't go fars!", 10, 0, 0, 25));
        trader_stocks.add(new Trader_Stock(24L, 11L, 1, 0, 8));
        trader_stocks.add(new Trader_Stock(24L, 11L, 1, 16, 20));
        trader_stocks.add(new Trader_Stock(24L, 12L, 1, 0, 8));
        trader_stocks.add(new Trader_Stock(24L, 12L, 1, 16, 20));
        trader_stocks.add(new Trader_Stock(24L, 13L, 1, 0, 8));
        trader_stocks.add(new Trader_Stock(24L, 13L, 1, 16, 20));

        traders.add(new Trader(10L, 4, "The Biggest Ore Shop", "Ore to the max, racks and racks.", 20, 0, 0, 8));
        trader_stocks.add(new Trader_Stock(25L, 1L, 1, 0, 20));
        trader_stocks.add(new Trader_Stock(25L, 1L, 1, 40, 50));
        trader_stocks.add(new Trader_Stock(25L, 2L, 1, 0, 20));
        trader_stocks.add(new Trader_Stock(25L, 2L, 1, 40, 50));
        trader_stocks.add(new Trader_Stock(25L, 3L, 1, 0, 20));
        trader_stocks.add(new Trader_Stock(25L, 3L, 1, 40, 50));
        trader_stocks.add(new Trader_Stock(25L, 4L, 1, 0, 20));
        trader_stocks.add(new Trader_Stock(25L, 4L, 1, 40, 50));
        trader_stocks.add(new Trader_Stock(25L, 5L, 1, 0, 20));
        trader_stocks.add(new Trader_Stock(25L, 6L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(25L, 7L, 1, 0, 5));

        traders.add(new Trader(10L, 4, "The Biggest Bar Shop", "So many bars, enough to fill Mars.", 20, 0, 0, 8));
        trader_stocks.add(new Trader_Stock(26L, 11L, 1, 0, 20));
        trader_stocks.add(new Trader_Stock(26L, 11L, 1, 40, 30));
        trader_stocks.add(new Trader_Stock(26L, 12L, 1, 0, 20));
        trader_stocks.add(new Trader_Stock(26L, 12L, 1, 40, 30));
        trader_stocks.add(new Trader_Stock(26L, 13L, 1, 0, 20));
        trader_stocks.add(new Trader_Stock(26L, 13L, 1, 40, 30));
        trader_stocks.add(new Trader_Stock(26L, 14L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(26L, 15L, 1, 0, 8));
        trader_stocks.add(new Trader_Stock(26L, 16L, 1, 0, 6));
        trader_stocks.add(new Trader_Stock(26L, 17L, 1, 0, 4));
        trader_stocks.add(new Trader_Stock(26L, 18L, 1, 0, 4));
        trader_stocks.add(new Trader_Stock(26L, 19L, 1, 0, 1));

        traders.add(new Trader(11L, 4, "The Scary Silk Shop", "About as scary as regular silk.", 0, 0, 0, 80));
        trader_stocks.add(new Trader_Stock(27L, 69L, 1, 0, 15));
        trader_stocks.add(new Trader_Stock(27L, 69L, 1, 31, 35));
        trader_stocks.add(new Trader_Stock(27L, 70L, 1, 0, 15));
        trader_stocks.add(new Trader_Stock(27L, 70L, 1, 31, 35));

        traders.add(new Trader(11L, 4, "The Lethal Log Lair", "About as lethal as any old log.", 5, 0, 0, 35));
        trader_stocks.add(new Trader_Stock(28L, 71L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(28L, 71L, 1, 11, 25));
        trader_stocks.add(new Trader_Stock(28L, 71L, 1, 51, 70));

        traders.add(new Trader(11L, 4, "The Petrifying Powder Place", "Powder can be scary! Course it can!", 30, 0, 0, 12));
        trader_stocks.add(new Trader_Stock(29L, 129L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(29L, 129L, 1, 11, 25));
        trader_stocks.add(new Trader_Stock(29L, 130L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(29L, 130L, 1, 11, 25));
        trader_stocks.add(new Trader_Stock(29L, 131L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(29L, 131L, 1, 11, 25));

        traders.add(new Trader(12L, 4, "Surf and Steel", "Don't steal my steel.", 10, 0, 0, 50));
        trader_stocks.add(new Trader_Stock(30L, 57L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(30L, 58L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(30L, 63L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(30L, 64L, 1, 0, 2));

        traders.add(new Trader(12L, 4, "Manatees and Mithril", "I won't mith my mithril. I mean miss.", 20, 0, 0, 35));
        trader_stocks.add(new Trader_Stock(31L, 93L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(31L, 84L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(31L, 96L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(31L, 95L, 1, 0, 2));

        traders.add(new Trader(12L, 4, "Atolls and Adamant", "I'm adamant you'll like my adamant.", 30, 0, 0, 30));
        trader_stocks.add(new Trader_Stock(32L, 110L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(32L, 99L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(32L, 105L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(32L, 106L, 1, 0, 2));

        traders.add(new Trader(12L, 4, "Reefs and Rune", "Don't rune away!", 40, 0, 0, 30));
        trader_stocks.add(new Trader_Stock(33L, 110L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(33L, 99L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(33L, 105L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(33L, 106L, 1, 0, 2));

        traders.add(new Trader(12L, 4, "Dives and Dragon", "Forged from seadragon hide.", 50, 0, 0, 20));
        trader_stocks.add(new Trader_Stock(34L, 146L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(34L, 145L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(34L, 135L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(34L, 137L, 1, 0, 2));

        traders.add(new Trader(13L, 4, "Swords a'plenty!", "Get your swords here!", 0, 0, 0, 5));
        trader_stocks.add(new Trader_Stock(35L, 21L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(35L, 37L, 1, 5, 2));
        trader_stocks.add(new Trader_Stock(35L, 54L, 1, 12, 2));
        trader_stocks.add(new Trader_Stock(35L, 82L, 1, 30, 2));
        trader_stocks.add(new Trader_Stock(35L, 98L, 1, 55, 2));
        trader_stocks.add(new Trader_Stock(35L, 114L, 1, 110, 2));
        trader_stocks.add(new Trader_Stock(35L, 133L, 1, 300, 2));

        traders.add(new Trader(13L, 4, "Shields a'plenty!", "Get your shields here!", 0, 0, 0, 5));
        trader_stocks.add(new Trader_Stock(36L, 25L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(36L, 41L, 1, 5, 2));
        trader_stocks.add(new Trader_Stock(36L, 58L, 1, 12, 2));
        trader_stocks.add(new Trader_Stock(36L, 86L, 1, 30, 2));
        trader_stocks.add(new Trader_Stock(36L, 102L, 1, 55, 2));
        trader_stocks.add(new Trader_Stock(36L, 118L, 1, 110, 2));
        trader_stocks.add(new Trader_Stock(36L, 137L, 1, 300, 2));

        traders.add(new Trader(13L, 4, "Armour a'plenty!", "Get your armour here!", 0, 0, 0, 5));
        trader_stocks.add(new Trader_Stock(37L, 27L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(37L, 43L, 1, 5, 2));
        trader_stocks.add(new Trader_Stock(37L, 60L, 1, 12, 2));
        trader_stocks.add(new Trader_Stock(37L, 88L, 1, 30, 2));
        trader_stocks.add(new Trader_Stock(37L, 104L, 1, 55, 2));
        trader_stocks.add(new Trader_Stock(37L, 120L, 1, 110, 2));
        trader_stocks.add(new Trader_Stock(37L, 139L, 1, 300, 2));

        traders.add(new Trader(14L, 4, "The Floating Fruitmonger", "It's all technically fruit!", 5, 0, 0, 20));
        trader_stocks.add(new Trader_Stock(38L, 77L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(38L, 77L, 1, 12, 10));
        trader_stocks.add(new Trader_Stock(38L, 77L, 1, 40, 25));
        trader_stocks.add(new Trader_Stock(38L, 77L, 1, 100, 40));

        traders.add(new Trader(14L, 4, "The Fromage Fermenter", "Finely fermented, frankly.", 5, 0, 0, 20));
        trader_stocks.add(new Trader_Stock(39L, 78L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(39L, 78L, 1, 12, 10));
        trader_stocks.add(new Trader_Stock(39L, 78L, 1, 40, 25));
        trader_stocks.add(new Trader_Stock(39L, 78L, 1, 100, 40));

        traders.add(new Trader(14L, 4, "Mill Hill", "Baker's dozens!", 5, 0, 0, 20));
        trader_stocks.add(new Trader_Stock(40L, 79L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(40L, 79L, 1, 12, 10));
        trader_stocks.add(new Trader_Stock(40L, 79L, 1, 40, 25));
        trader_stocks.add(new Trader_Stock(40L, 79L, 1, 100, 40));

        traders.add(new Trader(14L, 4, "Bloodsucker's Boxes", "Don't ask how I got this.", 5, 0, 0, 20));
        trader_stocks.add(new Trader_Stock(41L, 80L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(41L, 80L, 1, 12, 10));
        trader_stocks.add(new Trader_Stock(41L, 80L, 1, 40, 25));
        trader_stocks.add(new Trader_Stock(41L, 80L, 1, 100, 40));

        traders.add(new Trader(15L, 4, "Runic Remedies", "The finest rune resources in the land!", 40, 0, 0, 40));
        trader_stocks.add(new Trader_Stock(42L, 3L, 1, 0, 20));
        trader_stocks.add(new Trader_Stock(42L, 7L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(42L, 16L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(42L, 3L, 1, 120, 80));
        trader_stocks.add(new Trader_Stock(42L, 7L, 1, 120, 20));
        trader_stocks.add(new Trader_Stock(42L, 16L, 1, 120, 20));

        traders.add(new Trader(15L, 4, "Dragon Delights", "All 100% gen-u-ine dragon.", 50, 0, 0, 20));
        trader_stocks.add(new Trader_Stock(43L, 3L, 1, 0, 30));
        trader_stocks.add(new Trader_Stock(43L, 10L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(43L, 19L, 1, 0, 5));
        trader_stocks.add(new Trader_Stock(43L, 3L, 1, 150, 120));
        trader_stocks.add(new Trader_Stock(43L, 10L, 1, 150, 20));
        trader_stocks.add(new Trader_Stock(43L, 19L, 1, 150, 20));

        traders.add(new Trader(16L, 4, "Silver Waistbands", "For blobs only!", 10, 0, 0, 2));
        trader_stocks.add(new Trader_Stock(44L, 149L, 1, 0, 1));
        trader_stocks.add(new Trader_Stock(44L, 149L, 1, 5, 5));

        traders.add(new Trader(16L, 4, "Gold Belts", "For fancy blobs only!", 15, 0, 0, 2));
        trader_stocks.add(new Trader_Stock(45L, 155L, 1, 0, 1));
        trader_stocks.add(new Trader_Stock(45L, 155L, 1, 5, 5));

        traders.add(new Trader(17L, 4, "Space Ores", "Fresh off the asteroid.", 0, 0, 0, 75));
        trader_stocks.add(new Trader_Stock(46L, 1L, 1, 0, 20));
        trader_stocks.add(new Trader_Stock(46L, 2L, 1, 0, 20));
        trader_stocks.add(new Trader_Stock(46L, 3L, 1, 0, 20));
        trader_stocks.add(new Trader_Stock(46L, 4L, 1, 0, 20));
        trader_stocks.add(new Trader_Stock(46L, 5L, 1, 150, 20));
        trader_stocks.add(new Trader_Stock(46L, 6L, 1, 250, 20));
        trader_stocks.add(new Trader_Stock(46L, 7L, 1, 350, 20));
        trader_stocks.add(new Trader_Stock(46L, 8L, 1, 150, 20));
        trader_stocks.add(new Trader_Stock(46L, 9L, 1, 250, 20));
        trader_stocks.add(new Trader_Stock(46L, 10L, 1, 500, 20));

        traders.add(new Trader(17L, 4, "Space Bars", "From the slightly more advanced asteroids.", 0, 0, 0, 40));
        trader_stocks.add(new Trader_Stock(47L, 11L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(47L, 12L, 1, 0, 10));
        trader_stocks.add(new Trader_Stock(47L, 13L, 1, 40, 10));
        trader_stocks.add(new Trader_Stock(47L, 14L, 1, 90, 10));
        trader_stocks.add(new Trader_Stock(47L, 15L, 1, 150, 10));
        trader_stocks.add(new Trader_Stock(47L, 16L, 1, 250, 10));
        trader_stocks.add(new Trader_Stock(47L, 17L, 1, 60, 10));
        trader_stocks.add(new Trader_Stock(47L, 18L, 1, 80, 10));
        trader_stocks.add(new Trader_Stock(47L, 19L, 1, 450, 10));

        traders.add(new Trader(17L, 4, "Space Gems", "Formed when a space rock meets high G-forces.", 10, 0, 0, 2));
        trader_stocks.add(new Trader_Stock(48L, 72L, 1, 0, 1));
        trader_stocks.add(new Trader_Stock(48L, 73L, 1, 3, 1));
        trader_stocks.add(new Trader_Stock(48L, 74L, 1, 8, 1));
        trader_stocks.add(new Trader_Stock(48L, 75L, 1, 15, 1));
        trader_stocks.add(new Trader_Stock(48L, 76L, 1, 25, 1));

        traders.add(new Trader(18L, 4, "My Stuff", "Not for sale. Go away.", 0, 0, 0, 15));
        trader_stocks.add(new Trader_Stock(49L, 38L, 1, 0, 1));
        trader_stocks.add(new Trader_Stock(49L, 41L, 1, 1, 1));
        trader_stocks.add(new Trader_Stock(49L, 43L, 1, 2, 1));
        trader_stocks.add(new Trader_Stock(49L, 45L, 1, 3, 1));
        trader_stocks.add(new Trader_Stock(49L, 46L, 1, 4, 1));
        trader_stocks.add(new Trader_Stock(49L, 47L, 1, 5, 1));

        traders.add(new Trader(18L, 4, "Still My Stuff", "I'm hanging onto this. Shoo.", 30, 0, 0, 5));
        trader_stocks.add(new Trader_Stock(50L, 99L, 1, 0, 1));
        trader_stocks.add(new Trader_Stock(50L, 102L, 1, 1, 1));
        trader_stocks.add(new Trader_Stock(50L, 103L, 1, 2, 1));
        trader_stocks.add(new Trader_Stock(50L, 106L, 1, 3, 1));
        trader_stocks.add(new Trader_Stock(50L, 107L, 1, 4, 1));
        trader_stocks.add(new Trader_Stock(50L, 108L, 1, 5, 1));

        traders.add(new Trader(19L, 4, "Opticians", "Well, they help protect your eyes at least.", 15, 0, 0, 25));
        trader_stocks.add(new Trader_Stock(51L, 28L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(51L, 61L, 1, 9, 3));
        trader_stocks.add(new Trader_Stock(51L, 62L, 1, 9, 3));
        trader_stocks.add(new Trader_Stock(51L, 90L, 1, 20, 5));
        trader_stocks.add(new Trader_Stock(51L, 105L, 1, 35, 8));
        trader_stocks.add(new Trader_Stock(51L, 121L, 1, 60, 10));
        trader_stocks.add(new Trader_Stock(51L, 122L, 1, 60, 10));

        traders.add(new Trader(20L, 4, "The King's Armoury", "Buy the very best.", 50, 0, 0, 50));
        trader_stocks.add(new Trader_Stock(52L, 140L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(52L, 141L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(52L, 138L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(52L, 139L, 1, 0, 2));

        traders.add(new Trader(20L, 4, "The King's Weapon Rack", "Buy the very best.", 50, 0, 0, 50));
        trader_stocks.add(new Trader_Stock(53L, 133L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(53L, 135L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(53L, 132L, 1, 0, 2));
        trader_stocks.add(new Trader_Stock(53L, 145L, 1, 0, 2));

        Trader.saveInTx(traders);
        Trader_Stock.saveInTx(trader_stocks);
    }

    private void createType() {
        List<Type> types = new ArrayList<>();

        types.add(new Type(1L, "Ore", 1, 1, 30));
        types.add(new Type(2L, "Bar", 1, 1, 30));
        types.add(new Type(3L, "Dagger", 2, 1, 25));
        types.add(new Type(4L, "Sword", 2, 1, 25));
        types.add(new Type(5L, "Longsword", 2, 1, 25));
        types.add(new Type(6L, "Bow", 2, 1, 20));
        types.add(new Type(7L, "Half Shield", 3, 2, 20));
        types.add(new Type(8L, "Full Shield", 3, 2, 20));
        types.add(new Type(9L, "Chainmail", 3, 2, 20));
        types.add(new Type(10L, "Platebody", 3, 3, 15));
        types.add(new Type(11L, "Half Helmet", 3, 3, 15));
        types.add(new Type(12L, "Full Helmet", 3, 4, 15));
        types.add(new Type(13L, "Boot", 3, 4, 15));
        types.add(new Type(14L, "Glove", 3, 4, 10));
        types.add(new Type(15L, "Pickaxe", 4, 4, 10));
        types.add(new Type(16L, "Hatchet", 4, 4, 10));
        types.add(new Type(17L, "Fishing Rod", 4, 4, 10));
        types.add(new Type(18L, "Hammer", 4, 4, 10));
        types.add(new Type(19L, "Secondary", 1, 5, 30));
        types.add(new Type(20L, "Gem", 1, 10, 5));
        types.add(new Type(21L, "Food", 2, 1, 30));
        types.add(new Type(22L, "Powder", 1, 15, 1));
        types.add(new Type(23L, "Rare", 6, 1, 0));
        types.add(new Type(24L, "Ring", 4, 35, 1));
        types.add(new Type(100L, "Internal", 0, 0, 0));

        Type.saveInTx(types);
    }

    private void createUpgrade() {
        List<Upgrade> upgrades = new ArrayList<>();

        upgrades.add(new Upgrade("Visitor Spawn Time", "mins", 1000, 1, 25, 10, 25));
        upgrades.add(new Upgrade("Market Restock Time", "hours", 250, 1, 24, 2, 24));
        upgrades.add(new Upgrade("Maximum Visitors", "visitors", 1000, 1, 2, 10, 2));
        upgrades.add(new Upgrade("Maximum Traders", "traders", 250, 1, 3, 10, 3));
        upgrades.add(new Upgrade("Gold Bonus", "%", 750, 5, 0, 50, 0));
        upgrades.add(new Upgrade("XP Bonus", "%", 750, 5, 0, 50, 0));
        upgrades.add(new Upgrade("Craft Time", "ms per g", 35, 50, 600, 50, 600));
        upgrades.add(new Upgrade("Legendary Chance", "%", 1250, 5, 5, 100, 5));

        Upgrade.saveInTx(upgrades);
    }

    private void createVisitor() {
        Visitor visitor = new Visitor(System.currentTimeMillis(), 33L);
        visitor.save();
    }

    private void createVisitorDemand() {
        List<Visitor_Demand> visitor_demands = new ArrayList<>();

        // Req: 2 ore, 1 bar, 1 unfinished, 1 finished
        visitor_demands.add(new Visitor_Demand(1L, 3L, 1L, 0, 2, true));
        visitor_demands.add(new Visitor_Demand(1L, 1L, 2L, 0, 1, true));
        visitor_demands.add(new Visitor_Demand(1L, 1L, 1L, 0, 1, true));

        // Opt: 1 cheese
        visitor_demands.add(new Visitor_Demand(1L, 3L, 21L, 0, 1, false));

        Visitor_Demand.saveInTx(visitor_demands);
    }

    private void createVisitorStats() {
        List<Visitor_Stats> visitorStatses = new ArrayList<>();

        visitorStatses.add(new Visitor_Stats(1L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(2L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(3L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(4L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(5L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(6L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(7L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(8L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(9L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(10L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(11L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(12L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(13L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(14L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(15L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(16L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(17L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(18L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(19L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(20L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(21L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(22L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(23L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(24L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(25L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(26L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(27L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(28L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(29L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(30L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(31L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(32L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(33L, 1, 52L, 1L, 0, System.currentTimeMillis(), 0L));
        visitorStatses.add(new Visitor_Stats(34L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(35L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(36L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(37L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(38L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(39L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(40L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(41L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(42L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(43L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(44L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(45L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(46L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(47L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(48L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(49L, 0, 52L, 1L, 0, 0L, 0L));
        visitorStatses.add(new Visitor_Stats(50L, 0, 52L, 1L, 0, 0L, 0L));

        Visitor_Stats.saveInTx(visitorStatses);
    }

    private void createVisitorType() {
        List<Visitor_Type> visitor_types = new ArrayList<>();

        visitor_types.add(new Visitor_Type(1L, "Senor Spicy Hot", "I like unfinished things, they burn better!", 1L, 14L, 2L, 1.1, 1.1, 3.0, false, false, false, 3));
        visitor_types.add(new Visitor_Type(2L, "Mister Hatchet", "If only I was a woodcutter...", 3L, 16L, 1L, 1.2, 1.2, 1.2, false, false, false, 6));
        visitor_types.add(new Visitor_Type(3L, "Lord of the Junk", "It's not rubbish, it's treasure!", 1L, 3L, 2L, 1.05, 1.05, 1.05, false, false, false, 10));
        visitor_types.add(new Visitor_Type(4L, "Monsieur Fancypants", "Only the best for me, old chap.", 10L, 10L, 4L, 1.6, 1.55, 1.5, false, false, false, 1));
        visitor_types.add(new Visitor_Type(5L, "Grumbling Rock", "Me hungry. Tummy rumbling.", 11L, 1L, 1L, 1.05, 1.4, 1.05, false, false, false, 5));
        visitor_types.add(new Visitor_Type(6L, "Large Grumbling Rock", "Me very hungry. Tummy rumbling loud.", 11L, 1L, 1L, 1.6, 1.8, 1.05, false, false, false, 1));
        visitor_types.add(new Visitor_Type(7L, "R. De Couleur", "They say I'm the short-tempered one, but I just like the colour.", 7L, 20L, 3L, 1.25, 1.05, 1.66, false, false, false, 2));
        visitor_types.add(new Visitor_Type(8L, "G. De Couleur", "They say I'm the jealous one, but I just like the colour.", 5L, 20L, 4L, 1.25, 1.05, 1.66, false, false, false, 2));
        visitor_types.add(new Visitor_Type(9L, "B. De Couleur", "They say I'm the unhappy one, but I just like the colour.", 6L, 20L, 5L, 1.25, 1.05, 1.66, false, false, false, 2));
        visitor_types.add(new Visitor_Type(10L, "W. De Couleur", "They say I'm the scared one, but I just like the colour.", 8L, 20L, 6L, 1.25, 1.05, 1.66, false, false, false, 1));
        visitor_types.add(new Visitor_Type(11L, "B. De Couleur", "They say I'm the scary one, but I just like the colour.", 20L, 20L, 7L, 1.25, 1.05, 1.66, false, false, false, 1));
        visitor_types.add(new Visitor_Type(12L, "Gelatinous Egg", "So what if I'm not quite in shape?", 11L, 19L, 2L, 1.05, 1.15, 1.1, false, false, false, 8));
        visitor_types.add(new Visitor_Type(13L, "The Great Cheese Menace", "You there! Got any of the yellow stuff?", 11L, 21L, 1L, 1.05, 1.55, 1.05, false, false, false, 6));
        visitor_types.add(new Visitor_Type(14L, "The Leech King", "Slurp, slurp.", 7L, 4L, 7L, 1.25, 1.25, 1.25, false, false, false, 3));
        visitor_types.add(new Visitor_Type(15L, "Man of Magicka", "Do you like my outfit?", 11L, 14L, 7L, 1.05, 1.05, 1.05, false, false, false, 7));
        visitor_types.add(new Visitor_Type(16L, "Rare Chest", "Give me your rare items, I'll look after them!", 5L, 21L, 1L, 1.10, 1.02, 1.04, false, false, false, 1));
        visitor_types.add(new Visitor_Type(17L, "Chest", "Give me your items, I'll look after them!", 4L, 21L, 2L, 1.10, 1.01, 1.04, false, false, false, 4));
        visitor_types.add(new Visitor_Type(18L, "Eye of Ender", "Don't worry, it's a coloured contact lens.", 6L, 12L, 6L, 1.30, 1.30, 1.30, false, false, false, 1));
        visitor_types.add(new Visitor_Type(19L, "BRAINS", "BRAINS", 1L, 12L, 5L, 1.10, 1.15, 1.15, false, false, false, 6));
        visitor_types.add(new Visitor_Type(20L, "Asterisk The Ghoul", "Seen Obelix anywhere?", 1L, 16L, 1L, 1.05, 1.10, 1.05, false, false, false, 3));
        visitor_types.add(new Visitor_Type(21L, "Eye of Starter", "I don't suppose you've got any monocles?", 3L, 11L, 6L, 1.20, 1.20, 1.20, false, false, false, 4));
        visitor_types.add(new Visitor_Type(22L, "Marcell", "I've got shoes on.", 11L, 19L, 1L, 1.01, 1.01, 1.01, false, false, false, 5));
        visitor_types.add(new Visitor_Type(23L, "Crabby", "Snippy, snippy!", 11L, 3L, 1L, 1.02, 1.02, 1.02, false, false, false, 3));
        visitor_types.add(new Visitor_Type(24L, "Southerner", "From down south. No, REALLY south. REAAALLLY south.", 7L, 6L, 3L, 1.15, 1.15, 1.10, false, false, false, 2));
        visitor_types.add(new Visitor_Type(25L, "Magical Mushroom", "No nibbling.", 11L, 21L, 1L, 1.01, 1.15, 1.05, false, false, false, 5));
        visitor_types.add(new Visitor_Type(26L, "Overwasp", "You know I can sting repeatedly, right?", 9L, 4L, 1L, 1.25, 1.15, 1.05, false, false, false, 4));
        visitor_types.add(new Visitor_Type(27L, "Obelix The Ghoul", "Seen Asterisk anywhere?", 1L, 16L, 1L, 1.05, 1.10, 1.05, false, false, false, 3));
        visitor_types.add(new Visitor_Type(28L, "Whippersnappette", "Don't tell Steve, but his secret mine isn't so secret.", 5L, 15L, 1L, 1.05, 1.15, 1.05, false, false, false, 5));
        visitor_types.add(new Visitor_Type(29L, "Hatty", "I wish my hair wasn't so exposed to the elements.", 11L, 12L, 1L, 1.08, 1.15, 1.05, false, false, false, 5));
        visitor_types.add(new Visitor_Type(30L, "Archer", "It's all about the bullseye.", 2L, 6L, 5L, 1.10, 1.25, 1.15, false, false, false, 2));
        visitor_types.add(new Visitor_Type(31L, "Steve", "Gotta dig deeper and deeper and deeper and...", 6L, 15L, 1L, 1.05, 1.15, 1.05, false, false, false, 6));
        visitor_types.add(new Visitor_Type(32L, "Whippersnapper", "Don't tell Steve, but his secret fish pond isn't so secret.", 11L, 17L, 4L, 1.05, 1.20, 1.02, false, false, false, 6));
        visitor_types.add(new Visitor_Type(33L, "Mr T Utorial", "Need a hand?", 1L, 1L, 4L, 1.05, 1.05, 1.05, true, true, false, 1));
        visitor_types.add(new Visitor_Type(34L, "Sammy the Snake", "Ssscared of sssnakesss?", 3L, 6L, 1L, 1.10, 1.15, 1.05, false, false, false, 9));
        visitor_types.add(new Visitor_Type(35L, "Power Orb", "I am all powerful. Okay, apart from equipment. Shh.", 6L, 1L, 4L, 1.03, 1.12, 1.09, false, false, false, 7));
        visitor_types.add(new Visitor_Type(36L, "Frankie Fire", "Is it hot in here?", 10L, 23L, 3L, 1.08, 1.15, 1.20, false, false, false, 7));
        visitor_types.add(new Visitor_Type(37L, "Emerald Giant", "I.. can.. barely.. move.", 5L, 4L, 5L, 1.16, 1.08, 1.16, false, false, false, 6));
        visitor_types.add(new Visitor_Type(38L, "PURPLEBOT9000", "BZZ.. GIVE SILVER. PLZ.", 8L, 24L, 1L, 1.19, 1.11, 1.01, false, false, false, 5));
        visitor_types.add(new Visitor_Type(39L, "Whirling Dervish", "I'm a little bit dizzy. Actually, very dizzy.", 6L, 2L, 4L, 1.10, 1.02, 1.12, false, false, false, 5));
        visitor_types.add(new Visitor_Type(40L, "Stumps", "Sure is cold up there.", 11L, 13L, 1L, 1.04, 1.12, 1.03, false, false, false, 9));
        visitor_types.add(new Visitor_Type(41L, "Octomum", "I am a completely normal human. Completely normal.", 4L, 14L, 4L, 1.18, 1.13, 1.08, false, false, false, 3));
        visitor_types.add(new Visitor_Type(42L, "Battletoad", "Make it quick, I've got somewhere to be.", 5L, 17L, 5L, 1.08, 1.20, 1.07, false, false, false, 1));
        visitor_types.add(new Visitor_Type(43L, "Casper", "I hope I'm not scaring you. I'm so sorry if I am. Sorry.", 8L, 22L, 6L, 1.08, 1.11, 1.09, false, false, false, 4));
        visitor_types.add(new Visitor_Type(44L, "Mummioso", "I am more than a mere mummy, I am Mummioso!", 11L, 10L, 6L, 1.02, 1.12, 1.04, false, false, false, 4));
        visitor_types.add(new Visitor_Type(45L, "The Black Knight", "It's not even a scratch!", 2L, 5L, 7L, 1.10, 1.14, 1.15, false, false, false, 4));
        visitor_types.add(new Visitor_Type(46L, "Mrs BRAINS", "Hello! Oh, also BRAINS.", 1L, 12L, 5L, 1.11, 1.16, 1.16, false, false, false, 1));
        visitor_types.add(new Visitor_Type(47L, "Sir Stumps", "My bark is as good as my bite.", 11L, 13L, 1L, 1.05, 1.13, 1.04, false, false, false, 1));
        visitor_types.add(new Visitor_Type(48L, "The Great Bread Menace", "You there! Got any of the yeasty stuff?", 11L, 21L, 1L, 1.05, 1.55, 1.05, false, false, false, 1));
        visitor_types.add(new Visitor_Type(49L, "Bloody Mary", "Bloody Mary, Bloody Mary, Bloody Mary!", 8L, 22L, 6L, 1.08, 1.11, 1.09, false, false, false, 1));
        visitor_types.add(new Visitor_Type(50L, "COLDBOT5000", "It's freezerin' time!", 6L, 18L, 4L, 1.13, 1.15, 1.09, false, false, false, 6));

        Visitor_Type.saveInTx(visitor_types);
    }

    private void createWorkers() {
        List<Worker> workers = new ArrayList<>();
        workers.add(new Worker(1, 16, 1, 32L, 1, 0L, 0, false));
        workers.add(new Worker(2, 14, 10, 32L, 1, 0L, 0, false));
        workers.add(new Worker(3, 8, 20, 32L, 1, 0L, 0, false));
        workers.add(new Worker(4, 7, 30, 32L, 1, 0L, 0, false));
        workers.add(new Worker(5, 4, 40, 32L, 1, 0L, 0, false));
        workers.add(new Worker(6, 17, 50, 32L, 1, 0L, 0, false));
        workers.add(new Worker(7, 7, 60, 32L, 1, 0L, 0, false));
        Worker.saveInTx(workers);
    }

    private void createWorkerResources() {
        List<Worker_Resource> workerResources = new ArrayList<>();
        workerResources.add(new Worker_Resource(32, 1, 1, 10)); // Bronze pickaxe
        workerResources.add(new Worker_Resource(32, 2, 1, 10)); // Bronze pickaxe
        workerResources.add(new Worker_Resource(48, 4, 1, 25)); // Iron pickaxe
        workerResources.add(new Worker_Resource(48, 1, 1, 5)); // Iron pickaxe
        workerResources.add(new Worker_Resource(48, 2, 1, 5)); // Iron pickaxe
        workerResources.add(new Worker_Resource(65, 4, 1, 25)); // Steel pickaxe
        workerResources.add(new Worker_Resource(65, 3, 1, 5)); // Steel pickaxe
        workerResources.add(new Worker_Resource(93, 5, 1, 10)); // Mithril pickaxe
        workerResources.add(new Worker_Resource(93, 3, 1, 15)); // Mithril pickaxe
        workerResources.add(new Worker_Resource(109, 6, 1, 8)); // Adamant pickaxe
        workerResources.add(new Worker_Resource(109, 3, 1, 32)); // Adamant pickaxe
        workerResources.add(new Worker_Resource(125, 7, 1, 8)); // Rune pickaxe
        workerResources.add(new Worker_Resource(125, 3, 1, 64)); // Rune pickaxe
        workerResources.add(new Worker_Resource(125, 9, 1, 20)); // Rune pickaxe
        workerResources.add(new Worker_Resource(144, 10, 1, 8)); // Dragon pickaxe
        workerResources.add(new Worker_Resource(144, 3, 1, 80)); // Dragon pickaxe
        workerResources.add(new Worker_Resource(144, 8, 1, 20)); // Dragon pickaxe
        workerResources.add(new Worker_Resource(173, 10, 1, 15)); // Legendary pickaxe
        workerResources.add(new Worker_Resource(173, 3, 1, 100)); // Legendary pickaxe
        workerResources.add(new Worker_Resource(173, 7, 1, 12)); // Legendary pickaxe

        workerResources.add(new Worker_Resource(35, 11, 1, 10)); // Bronze hammer
        workerResources.add(new Worker_Resource(51, 12, 1, 13)); // Iron hammer
        workerResources.add(new Worker_Resource(51, 11, 1, 4)); // Iron hammer
        workerResources.add(new Worker_Resource(68, 13, 1, 8)); // Steel hammer
        workerResources.add(new Worker_Resource(96, 14, 1, 10)); // Mithril hammer
        workerResources.add(new Worker_Resource(112, 15, 1, 10)); // Adamant hammer
        workerResources.add(new Worker_Resource(128, 16, 1, 10)); // Rune hammer
        workerResources.add(new Worker_Resource(128, 17, 1, 5)); // Rune hammer
        workerResources.add(new Worker_Resource(147, 19, 1, 10)); // Dragon hammer
        workerResources.add(new Worker_Resource(147, 18, 1, 5)); // Dragon hammer
        workerResources.add(new Worker_Resource(176, 19, 1, 20)); // Legendary hammer
        workerResources.add(new Worker_Resource(176, 18, 1, 5)); // Legendary hammer
        workerResources.add(new Worker_Resource(176, 16, 1, 10)); // Legendary hammer

        workerResources.add(new Worker_Resource(34, 77, 1, 5)); // Bronze fishing rod
        workerResources.add(new Worker_Resource(50, 78, 1, 5)); // Iron fishing rod
        workerResources.add(new Worker_Resource(67, 79, 1, 5)); // Steel fishing rod
        workerResources.add(new Worker_Resource(95, 80, 1, 5)); // Mithril fishing rod
        workerResources.add(new Worker_Resource(111, 77, 1, 10)); // Adamant fishing rod
        workerResources.add(new Worker_Resource(111, 78, 1, 10)); // Adamant fishing rod
        workerResources.add(new Worker_Resource(127, 79, 1, 10)); // Rune fishing rod
        workerResources.add(new Worker_Resource(127, 80, 1, 10)); // Rune fishing rod
        workerResources.add(new Worker_Resource(146, 80, 1, 15)); // Dragon fishing rod
        workerResources.add(new Worker_Resource(146, 79, 1, 15)); // Dragon fishing rod
        workerResources.add(new Worker_Resource(146, 78, 1, 15)); // Dragon fishing rod
        workerResources.add(new Worker_Resource(175, 77, 1, 20)); // Legendary fishing rod
        workerResources.add(new Worker_Resource(175, 78, 1, 20)); // Legendary fishing rod
        workerResources.add(new Worker_Resource(175, 79, 1, 20)); // Legendary fishing rod

        workerResources.add(new Worker_Resource(33, 71, 1, 5)); // Bronze hatchet
        workerResources.add(new Worker_Resource(49, 71, 1, 10)); // Iron hatchet
        workerResources.add(new Worker_Resource(66, 71, 1, 15)); // Steel hatchet
        workerResources.add(new Worker_Resource(94, 71, 1, 20)); // Mithril hatchet
        workerResources.add(new Worker_Resource(110, 71, 1, 25)); // Adamant hatchet
        workerResources.add(new Worker_Resource(126, 71, 1, 30)); // Rune hatchet
        workerResources.add(new Worker_Resource(145, 71, 1, 35)); // Dragon hatchet
        workerResources.add(new Worker_Resource(174, 71, 1, 40)); // Legendary hatchet

        workerResources.add(new Worker_Resource(31, 69, 1, 5)); // Bronze gloves
        workerResources.add(new Worker_Resource(47, 70, 1, 5)); // Iron gloves
        workerResources.add(new Worker_Resource(64, 69, 1, 13)); // Steel gloves
        workerResources.add(new Worker_Resource(92, 70, 1, 13)); // Mithril gloves
        workerResources.add(new Worker_Resource(108, 69, 1, 25)); // Adamant gloves
        workerResources.add(new Worker_Resource(124, 70, 1, 25)); // Rune gloves
        workerResources.add(new Worker_Resource(143, 69, 1, 20)); // Dragon gloves
        workerResources.add(new Worker_Resource(143, 70, 1, 20)); // Dragon gloves
        workerResources.add(new Worker_Resource(172, 69, 1, 30)); // Legendary gloves
        workerResources.add(new Worker_Resource(172, 70, 1, 30)); // Legendary gloves

        workerResources.add(new Worker_Resource(72, 129, 1, 2)); // Ruby
        workerResources.add(new Worker_Resource(73, 129, 1, 5)); // Sapphire
        workerResources.add(new Worker_Resource(74, 130, 1, 5)); // Emerald
        workerResources.add(new Worker_Resource(75, 131, 1, 5)); // Diamond
        workerResources.add(new Worker_Resource(76, 129, 1, 5)); // Onyx

        workerResources.add(new Worker_Resource(149, 9, 1, 5)); // Silver Ring
        workerResources.add(new Worker_Resource(149, 17, 1, 5)); // Silver Ring
        workerResources.add(new Worker_Resource(150, 9, 1, 5)); // Silver Sapphire Ring
        workerResources.add(new Worker_Resource(150, 17, 1, 5)); // Silver Sapphire Ring
        workerResources.add(new Worker_Resource(150, 73, 1, 1)); // Silver Sapphire Ring
        workerResources.add(new Worker_Resource(151, 9, 1, 5)); // Silver Emerald Ring
        workerResources.add(new Worker_Resource(151, 17, 1, 5)); // Silver Emerald Ring
        workerResources.add(new Worker_Resource(151, 74, 1, 1)); // Silver Emerald Ring
        workerResources.add(new Worker_Resource(152, 9, 1, 5)); // Silver Ruby Ring
        workerResources.add(new Worker_Resource(152, 17, 1, 5)); // Silver Ruby Ring
        workerResources.add(new Worker_Resource(152, 72, 1, 1)); // Silver Ruby Ring
        workerResources.add(new Worker_Resource(153, 9, 1, 5)); // Silver Diamond Ring
        workerResources.add(new Worker_Resource(153, 17, 1, 5)); // Silver Diamond Ring
        workerResources.add(new Worker_Resource(153, 75, 1, 1)); // Silver Diamond Ring
        workerResources.add(new Worker_Resource(154, 9, 1, 5)); // Silver Onyx Ring
        workerResources.add(new Worker_Resource(154, 17, 1, 5)); // Silver Onyx Ring
        workerResources.add(new Worker_Resource(154, 76, 1, 1)); // Silver Onyx Ring

        workerResources.add(new Worker_Resource(155, 8, 1, 5)); // Gold Ring
        workerResources.add(new Worker_Resource(155, 18, 1, 5)); // Gold Ring
        workerResources.add(new Worker_Resource(156, 8, 1, 5)); // Gold Sapphire Ring
        workerResources.add(new Worker_Resource(156, 18, 1, 5)); // Gold Sapphire Ring
        workerResources.add(new Worker_Resource(156, 73, 1, 1)); // Gold Sapphire Ring
        workerResources.add(new Worker_Resource(157, 8, 1, 5)); // Gold Emerald Ring
        workerResources.add(new Worker_Resource(157, 18, 1, 5)); // Gold Emerald Ring
        workerResources.add(new Worker_Resource(157, 74, 1, 1)); // Gold Emerald Ring
        workerResources.add(new Worker_Resource(158, 8, 1, 5)); // Gold Ruby Ring
        workerResources.add(new Worker_Resource(158, 18, 1, 5)); // Gold Ruby Ring
        workerResources.add(new Worker_Resource(158, 72, 1, 1)); // Gold Ruby Ring
        workerResources.add(new Worker_Resource(159, 8, 1, 5)); // Gold Diamond Ring
        workerResources.add(new Worker_Resource(159, 18, 1, 5)); // Gold Diamond Ring
        workerResources.add(new Worker_Resource(159, 75, 1, 1)); // Gold Diamond Ring
        workerResources.add(new Worker_Resource(160, 8, 1, 5)); // Gold Onyx Ring
        workerResources.add(new Worker_Resource(160, 18, 1, 5)); // Gold Onyx Ring
        workerResources.add(new Worker_Resource(160, 76, 1, 1)); // Gold Onyx Ring

        workerResources.add(new Worker_Resource(148, 52, 1, 300)); // Visage
        Worker_Resource.saveInTx(workerResources);
    }
}

