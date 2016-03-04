package uk.co.jakelee.blacksmith.model;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmith.helper.Constants;

public class Trader extends SugarRecord {
    int shopkeeper;
    int location;
    String name;
    String description;
    int level;
    int weighting;
    long arrivalTime;

    public Trader() {
    }

    public Trader(int shopkeeper, int location, String name, String description, int level, int weighting, long arrivalTime) {
        this.shopkeeper = shopkeeper;
        this.location = location;
        this.name = name;
        this.description = description;
        this.level = level;
        this.weighting = weighting;
        this.arrivalTime = arrivalTime;
        this.save();
    }

    public int getShopkeeper() {
        return shopkeeper;
    }

    public void setShopkeeper(int shopkeeper) {
        this.shopkeeper = shopkeeper;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getWeighting() {
        return weighting;
    }

    public void setWeighting(int weighting) {
        this.weighting = weighting;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public static List<Trader> checkTraderStatus(long location) {
        List<Trader> traders = Select.from(Trader.class).where(
                Condition.prop("location").eq(location),
                Condition.prop("arrival_time").gt(0)).list();
        List<Trader> newTraders = new ArrayList<Trader>();

        // Remove any completed traders
        for (Trader trader : traders) {
            if (trader.isOutOfStock()) {
                trader.delete();
                traders.remove(trader);
            }
        }

        // Add any new traders
        if (traders.size() < Constants.MAXIMUM_TRADERS) {
            newTraders.add(Trader.makeTraderAppear());
        }

        return newTraders;
    }

    public static Trader makeTraderAppear() {
        Trader traderToArrive = selectTraderType();
        traderToArrive.setArrivalTime(System.currentTimeMillis());
        traderToArrive.save();
        return traderToArrive;
    }

    public static Trader selectTraderType () {
        Trader selectedTraderType = new Trader();
        int playerLevel = Player_Info.getPlayerLevel();
        List<Trader> traderTypes = Select.from(Trader.class).where(
                Condition.prop("location").eq(Constants.LOCATION_MARKET),
                Condition.prop("level").lt(playerLevel + 1),
                Condition.prop("arrival_time").eq(0)).list();

        double totalWeighting = 0.0;
        for (Trader traderType : traderTypes) {
            totalWeighting += traderType.getWeighting();
        }

        double randomNumber = Math.random() * totalWeighting;
        double probabilityIterator = 0.0;
        for (Trader traderType :  traderTypes) {
            probabilityIterator += traderType.getWeighting();
            if (probabilityIterator >= randomNumber) {
                selectedTraderType = traderType;
                break;
            }
        }
        return selectedTraderType;
    }

    public boolean isOutOfStock() {
        List<Trader_Stock> stocks = Select.from(Trader_Stock.class).where(
                Condition.prop("trader_type").eq(this.getId())).list();
        boolean hasStock = false;
        for (Trader_Stock stock : stocks) {
            if (stock.getStock() > 0) {
                hasStock = true;
            }
        }

        return !hasStock;
    }
}
