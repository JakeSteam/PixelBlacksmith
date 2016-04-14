package uk.co.jakelee.blacksmith.model;

import android.content.Context;
import android.widget.Toast;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.Iterator;
import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.ToastHelper;

public class Trader extends SugarRecord {
    private long shopkeeper;
    private int location;
    private String name;
    private String description;
    private int level;
    private int status;
    private int purchases;
    private int weighting;

    public Trader() {
    }

    public Trader(long shopkeeper, int location, String name, String description, int level, int status, int purchases, int weighting) {
        this.shopkeeper = shopkeeper;
        this.location = location;
        this.name = name;
        this.description = description;
        this.level = level;
        this.status = status;
        this.purchases = purchases;
        this.weighting = weighting;
        this.save();
    }

    public static void checkTraderStatus(Context context, long location) {
        List<Trader> traders = Select.from(Trader.class).where(
                Condition.prop("location").eq(location),
                Condition.prop("status").eq(Constants.TRADER_PRESENT)).list();
        Iterator<Trader> tradersIter = traders.iterator();
        int numberOfTraders = traders.size();

        // Remove any completed traders
        while (tradersIter.hasNext()) {
            Trader trader = tradersIter.next();
            if (trader.isOutOfStock()) {
                trader.setStatus(Constants.TRADER_OUT_OF_STOCK);
                trader.save();
                tradersIter.remove();
                numberOfTraders--;
            }
        }

        // Add any necessary new traders
        while (numberOfTraders < Upgrade.getValue("Maximum Traders")) {
            Trader.makeTraderAppear(context);
            numberOfTraders++;
        }
    }

    private static void makeTraderAppear(Context context) {
        Trader traderToArrive = selectTraderType();
        if (traderToArrive.getName() != null) {
            traderToArrive.setStatus(Constants.TRADER_PRESENT);
            traderToArrive.save();
            ToastHelper.showToast(context, Toast.LENGTH_SHORT, String.format(context.getString(R.string.traderArrived), traderToArrive.getName()));
        }
    }

    private static Trader selectTraderType() {
        Trader selectedTraderType = new Trader();
        int playerLevel = Player_Info.getPlayerLevel();
        List<Trader> traderTypes = Select.from(Trader.class).where(
                Condition.prop("location").eq(Constants.LOCATION_MARKET),
                Condition.prop("level").lt(playerLevel + 1),
                Condition.prop("status").eq(Constants.TRADER_NOT_PRESENT)).list();

        double totalWeighting = 0.0;
        for (Trader traderType : traderTypes) {
            totalWeighting += traderType.getWeighting();
        }

        double randomNumber = Math.random() * totalWeighting;
        double probabilityIterator = 0.0;
        for (Trader traderType : traderTypes) {
            probabilityIterator += traderType.getWeighting();
            if (probabilityIterator >= randomNumber) {
                selectedTraderType = traderType;
                break;
            }
        }
        return selectedTraderType;
    }

    public long getShopkeeper() {
        return shopkeeper;
    }

    public void setShopkeeper(long shopkeeper) {
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPurchases() {
        return purchases;
    }

    public void setPurchases(int purchases) {
        this.purchases = purchases;
    }

    private int getWeighting() {
        return weighting;
    }

    public void setWeighting(int weighting) {
        this.weighting = weighting;
    }

    private boolean isOutOfStock() {
        List<Trader_Stock> stocks = Select.from(Trader_Stock.class).where(
                Condition.prop("trader_type").eq(this.getId()),
                Condition.prop("required_purchases").lt(this.getPurchases() + 1)).list();

        boolean hasStock = false;
        for (Trader_Stock stock : stocks) {
            if (stock.getStock() > 0) {
                hasStock = true;
            }
        }

        return !hasStock;
    }
}
