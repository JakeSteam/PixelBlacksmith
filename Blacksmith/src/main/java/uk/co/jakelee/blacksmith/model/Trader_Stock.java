package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.helper.Constants;

public class Trader_Stock extends SugarRecord {
    Long traderType;
    Long itemID;
    int state;
    int requiredPurchases;
    int stock;
    int defaultStock;

    public Trader_Stock() {

    }

    public Trader_Stock(Long traderType, Long itemID, int state, int requiredPurchases, int stock, int defaultStock) {
        this.traderType = traderType;
        this.itemID = itemID;
        this.state = state;
        this.requiredPurchases = requiredPurchases;
        this.stock = stock;
        this.defaultStock = defaultStock;
        this.save();
    }

    public Long getItemID() {
        return itemID;
    }

    public void setItemID(Long itemID) {
        this.itemID = itemID;
    }

    public Long getTraderType() {
        return traderType;
    }

    public void setTraderType(Long traderType) {
        this.traderType = traderType;
    }

    public int getRequiredPurchases() {
        return requiredPurchases;
    }

    public void setRequiredPurchases(int requiredPurchases) {
        this.requiredPurchases = requiredPurchases;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
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

    public static boolean shouldRestock() {
        Player_Info dateRefreshed = Select.from(Player_Info.class).where(
                Condition.prop("name").eq("DateRestocked")).first();

        return (dateRefreshed.getLongValue() + Constants.MILLISECONDS_BETWEEN_RESTOCKS) < System.currentTimeMillis();
    }

    public static void restockTraders() {
        new Thread(new Runnable() {
            public void run() {
                List<Trader_Stock> traderStocks = Trader_Stock.listAll(Trader_Stock.class);
                for (Trader_Stock traderStock : traderStocks) {
                    traderStock.setStock(traderStock.getDefaultStock());
                    traderStock.save();
                }

                List<Trader> traders = Trader.listAll(Trader.class);
                for (Trader trader : traders) {
                    trader.setStatus(Constants.TRADER_NOT_PRESENT);
                    trader.save();
                }

                Player_Info dateRefreshed = Select.from(Player_Info.class).where(
                        Condition.prop("name").eq("DateRestocked")).first();
                dateRefreshed.setLongValue(System.currentTimeMillis());
                dateRefreshed.save();
            }
        }).start();
    }
}
