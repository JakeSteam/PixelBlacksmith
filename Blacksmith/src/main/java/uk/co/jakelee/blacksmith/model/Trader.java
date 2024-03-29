package uk.co.jakelee.blacksmith.model;

import android.content.Context;
import android.widget.TableLayout;

import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.helper.Constants;
import uk.co.jakelee.blacksmith.helper.TextHelper;
import uk.co.jakelee.blacksmith.helper.ToastHelper;
import uk.co.jakelee.blacksmith.helper.TutorialHelper;

public class Trader extends SugarRecord {
    private long shopkeeper;
    private int location;
    private String name;
    private String description;
    private int level;
    private int status;
    private int purchases;
    private int weighting;
    private boolean fixed;

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
        this.fixed = false;
    }

    public static void checkTraderStatus(Context context, TableLayout marketLayout, long location) {
        List<Trader> traders = Select.from(Trader.class).where(
                Condition.prop("location").eq(location),
                Condition.prop("status").eq(Constants.TRADER_PRESENT)).list();

        int numberOfTraders = 0;
        for (Trader trader : traders) {
            boolean restocking = false;
            if (trader.isOutOfStock()) {
                if (trader.isFixed()) {
                    trader.restock(0);
                } else {
                    trader.setStatus(Constants.TRADER_OUT_OF_STOCK);
                    restocking = true;
                }
                trader.save();
            }

            if (!trader.isFixed() && !restocking) {
                numberOfTraders++;
            }
        }

        while (numberOfTraders < Upgrade.getValue("Maximum Traders")) {
            Trader.makeTraderAppear(context, marketLayout);
            numberOfTraders++;
        }
    }

    private static void makeTraderAppear(Context context, TableLayout marketLayout) {
        Trader traderToArrive = selectTraderType();
        if (!traderToArrive.getName(context).startsWith("trader_name_")) {
            traderToArrive.setStatus(Constants.TRADER_PRESENT);
            traderToArrive.save();
            if (!TutorialHelper.currentlyInTutorial) {
                ToastHelper.showToast(marketLayout, ToastHelper.SHORT, String.format(context.getString(R.string.traderArrived), traderToArrive.getName(context)), true);
            }
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

    public static int restockAll(int restockCost) {
        if (Inventory.getCoins() < restockCost) {
            return Constants.ERROR_NOT_ENOUGH_COINS;
        } else {
            // Remove coins
            Inventory coinStock = Inventory.getInventory(Constants.ITEM_COINS, Constants.STATE_NORMAL);
            coinStock.setQuantity(coinStock.getQuantity() - restockCost);
            coinStock.save();

            Trader.executeQuery("UPDATE trader SET status = " + Constants.TRADER_NOT_PRESENT);
            Trader_Stock.executeQuery("UPDATE traderstock SET stock = default_stock");

            return Constants.SUCCESS;
        }
    }

    public static int getFixedCount() {
        return (int) Select.from(Trader.class).where(
                Condition.prop("location").eq(Constants.LOCATION_MARKET),
                Condition.prop("fixed").eq(1)).count();
    }

    public static int getRestockAllCost() {
        return Upgrade.getValue("Restock All Cost");
    }

    public static int outOfStockTraders() {
        List<Trader> traders = Trader.listAll(Trader.class);
        int outOfStock = 0;
        for (Trader trader : traders) {
            if (trader.isOutOfStock()) {
                outOfStock++;
            }
        }
        return outOfStock;
    }

    public int restock(int restockCost) {
        if (Inventory.getCoins() < restockCost) {
            return Constants.ERROR_NOT_ENOUGH_COINS;
        } else {
            // Remove coins
            Inventory coinStock = Inventory.getInventory(Constants.ITEM_COINS, Constants.STATE_NORMAL);
            coinStock.setQuantity(coinStock.getQuantity() - restockCost);
            coinStock.save();

            // Restock
            List<Trader_Stock> trader_stocks = Select.from(Trader_Stock.class).where(
                    Condition.prop("trader_type").eq(getId())).list();
            for (Trader_Stock trader_stock : trader_stocks) {
                trader_stock.setStock(trader_stock.getDefaultStock());
                trader_stock.save();
            }

            return Constants.SUCCESS;
        }
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

    public String getName(Context context) {
        return TextHelper.getInstance(context).getText("trader_name_" + getId());
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription(Context context) {
        return TextHelper.getInstance(context).getText("trader_desc_" + getId());
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

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
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
