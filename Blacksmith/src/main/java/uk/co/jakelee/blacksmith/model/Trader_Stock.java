package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.helper.DateHelper;

public class Trader_Stock extends SugarRecord {
    private Long traderType;
    private Long itemID;
    private int state;
    private int requiredPurchases;
    private int stock;
    private int defaultStock;

    public Trader_Stock() {

    }

    public Trader_Stock(Long traderType, Long itemID, int state, int requiredPurchases, int stock) {
        this.traderType = traderType;
        this.itemID = itemID;
        this.state = state;
        this.requiredPurchases = requiredPurchases;
        this.stock = stock;
        this.defaultStock = stock;
    }

    public static boolean shouldRestock() {
        return getMillisecondsUntilRestock() <= 0;
    }

    public static String getRestockTimeLeft() {
        long milliseconds = getMillisecondsUntilRestock() + (DateHelper.MILLISECONDS_IN_SECOND * 60); // Round up to nearest minute.
        return DateHelper.getHoursMinsRemaining(milliseconds > 0 ? milliseconds : 0);
    }

    public static long getMillisecondsUntilRestock() {
        long unixRestocked = Select.from(Player_Info.class).where(Condition.prop("name").eq("DateRestocked")).first().getLongValue();
        long unixNextRestock = unixRestocked + DateHelper.hoursToMilliseconds(Upgrade.getValue("Market Restock Time"));
        long unixRestockDifference = unixNextRestock - System.currentTimeMillis();

        return unixRestockDifference;
    }

    public static void restockTraders() {
            Trader_Stock.executeQuery("UPDATE traderstock SET stock = default_stock");
            Trader.executeQuery("UPDATE trader SET status = 0");

            Player_Info dateRefreshed = Select.from(Player_Info.class).where(
                    Condition.prop("name").eq("DateRestocked")).first();
            dateRefreshed.setLongValue(System.currentTimeMillis());
            dateRefreshed.save();
    }

    public static int getUnlockedCount() {
        int stocksUnlocked = 0;
        List<Trader> traders = Trader.listAll(Trader.class);
        for (Trader trader : traders) {
            stocksUnlocked += (int) Select.from(Trader_Stock.class).where(
                    Condition.prop("trader_type").eq(trader.getId()),
                    Condition.prop("required_purchases").lt(trader.getPurchases() + 1)).count();
        }

        return stocksUnlocked;
    }

    public Long getTraderType() {
        return traderType;
    }

    public void setTraderType(Long traderType) {
        this.traderType = traderType;
    }

    public Long getItemID() {
        return itemID;
    }

    public void setItemID(Long itemID) {
        this.itemID = itemID;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getRequiredPurchases() {
        return requiredPurchases;
    }

    public void setRequiredPurchases(int requiredPurchases) {
        this.requiredPurchases = requiredPurchases;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getDefaultStock() {
        return defaultStock;
    }

    public void setDefaultStock(int defaultStock) {
        this.defaultStock = defaultStock;
    }
}
